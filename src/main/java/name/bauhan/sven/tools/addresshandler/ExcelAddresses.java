package name.bauhan.sven.tools.addresshandler;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Sven
 */
public class ExcelAddresses extends AddressFile {

	private final static Logger logger = LoggerFactory.getLogger(ExcelAddresses.class);
	final public static Pattern pattern = Pattern.compile(".*[.][xX][lL][sS]");

	ExcelAddresses(String filename) {
		super(filename);
	}

	static protected Pattern file_pattern() {
		final Pattern xlsExt = Pattern.compile(".*[.][xX][lL][sS]");
		return xlsExt;
	}

	@Override
	protected void readFile() {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(file_name));
			Sheet sheet = workbook.getSheet(0);
			Cell a1 = sheet.getCell(0, 0);
			String cell_a1 = a1.getContents();
			AddressHandler.logger.info("Excel file, cell A1: " + cell_a1);
		} catch (IOException ex) {
			AddressHandler.logger.warn("Unable to open file: " + ex.getLocalizedMessage());
		} catch (BiffException ex) {
			AddressHandler.logger.warn("Biff exception: " + ex.getLocalizedMessage());
		}
	}

	private WritableSheet create_sheet_with_header(WritableWorkbook book) {
		WritableSheet sheet = book.createSheet("Addresses", 0);
		try {
			Label label = new Label(0, 0, "Prefix");
			sheet.addCell(label);
			label = new Label(1, 0, "Vorname");
			sheet.addCell(label);
			label = new Label(2, 0, "Nachname");
			sheet.addCell(label);
			label = new Label(3, 0, "Festnetz");
			sheet.addCell(label);
			label = new Label(4, 0, "Mobil");
			sheet.addCell(label);
			label = new Label(5, 0, "Email");
			sheet.addCell(label);
		} catch (WriteException ex) {
		}
		return sheet;
	}

	private void write_addresses_to_sheet(WritableSheet sheet) {
		int row = 0;
		for(VCard vCard: getAdresses()) {
			try {
				row = row + 1;
				StructuredName name = vCard.getStructuredName();
				List<String> prefixes = name.getPrefixes();
				Label prefix = new Label(0, row, StringUtils.join(prefixes, " "));
				sheet.addCell(prefix);
				Label given_name = new Label(1, row, name.getGiven());
				sheet.addCell(given_name);
				Label family = new Label(2, row, name.getFamily());
				sheet.addCell(family);
				List<Telephone> tel_numbers = vCard.getTelephoneNumbers();
				for (Telephone tel : tel_numbers) {
					if (tel.getTypes().contains(TelephoneType.HOME)) {
						Label phone = new Label(3, row, tel.getText());
						sheet.addCell(phone);
					} else if (tel.getTypes().contains(TelephoneType.CELL)) {
						Label phone = new Label(4, row, tel.getText());
						sheet.addCell(phone);
					}
				}
				List<Email> email_addresses = vCard.getEmails();
				List<String> email_strings = new LinkedList<String>();
				for (Email email : email_addresses) {
					email_strings.add(email.getValue());
				}
				Label emails = new Label(5, row, StringUtils.join(email_strings, "\n"));
				sheet.addCell(emails);
			} catch (WriteException ex) {
			}
		}
	}
	
	@Override
	public void writeFile() {
		try {
			WritableWorkbook book = Workbook.createWorkbook(new File(file_name));
			WritableSheet sheet = create_sheet_with_header(book);
			write_addresses_to_sheet(sheet);
			book.write();
			book.close();
		} catch (IOException ex) {
			logger.warn("Reading to excel: " + ex.getLocalizedMessage());
		} catch (WriteException ex) {
			logger.warn("Writing excel cell: " + ex.getLocalizedMessage());
		}
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
