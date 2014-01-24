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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Sven
 */
public class ExcelAddresses extends AddressFile {

	enum Fields {

		PREFIX("Titel", 0),
		GIVEN_NAME("Vorname", 1),
		FAMILY_NAME("Nachname", 2),
		HOME_PHONE("Festnetz", 3),
		CELL_PHONE("Mobil", 4),
		EMAIL("Email", 5);
		private String title;
		private int index;
		/** Mapping from field title to enum value. */
		public static final Map<String, Fields> TITLE2FIELD = new HashMap<String, Fields>();

		static {
			for (Fields field : Fields.values()) {
				TITLE2FIELD.put(field.getTitle(), field);
			}
		}

		Fields(String name, int count) {
			title = name;
			index = count;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
	}
	/**
	 * Logger instance
	 */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	/**
	 * Name of sheet with addresses
	 */
	final private static String SHEET_NAME = "Addresses";
	final public static Pattern pattern = Pattern.compile(".*[.][xX][lL][sS]");
	private Map<Fields, Integer> field2column;
	
	ExcelAddresses(String filename) {
		super(filename);
	}

	static protected Pattern file_pattern() {
		final Pattern xlsExt = Pattern.compile(".*[.][xX][lL][sS]");
		return xlsExt;
	}

	/**
	 * Identify which fields are in the different columns.
	 * 
	 * @param sheet_  excel sheet
	 * @return  mapping, in which column which address field is stored
	 */
	private Map<Fields, Integer> identifyColumns(Sheet sheet_) {
		field2column = new EnumMap<Fields, Integer>(Fields.class);
		Cell[] headers = sheet_.getRow(0);
		for (int i = 0; i < headers.length; i++) {
			logger.debug("Searching title " + headers[i].getContents() + " in fields");
			Fields field = Fields.TITLE2FIELD.get(headers[i].getContents());
			if (field != null) {
				logger.debug("Found field " + field.getTitle() + " in column " + i);
				field2column.put(field, i);
			}
		}
		return field2column;
	}

	/**
	 * Read one row of cells and convert to VCard.
	 * 
	 * @param cells  row of cells
	 * @return  generated VCard
	 */
	private VCard readAddress(Cell[] cells) {
		VCard address = new VCard();
		Integer col;
		StructuredName name = new StructuredName();
		col = field2column.get(Fields.PREFIX);
		if ( col != null ) {
			name.addPrefix(cells[col].getContents());
		}
		col = field2column.get(Fields.GIVEN_NAME);
		if ( col != null ) {
			name.setGiven(cells[col].getContents());
		}
		col = field2column.get(Fields.FAMILY_NAME);
		if ( col != null ) {
			name.setFamily(cells[col].getContents());
		}
		address.setStructuredName(name);
		col = field2column.get(Fields.HOME_PHONE);
		if ( col != null ) {
			address.addTelephoneNumber(cells[col].getContents(), TelephoneType.HOME);
		}
		col = field2column.get(Fields.CELL_PHONE);
		if ( col != null ) {
			address.addTelephoneNumber(cells[col].getContents(), TelephoneType.CELL);
		}
		col = field2column.get(Fields.EMAIL);
		if ( col != null ) {
			String email = cells[col].getContents();
			address.addEmail(email);
		}
		return address;
	}
	
	@Override
	protected void readFile() {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(file_name));
//			Sheet sheet = workbook.getSheet(SHEET_NAME);
			Sheet sheet = workbook.getSheet(0);
			identifyColumns(sheet);
			logger.info("Found excel sheet with " + sheet.getRows() + " rows");
			addresses = new LinkedList<VCard>();
			for ( int i = 1; i < sheet.getRows(); i++ ) {
				Cell[] cells = sheet.getRow(i);
				VCard address = readAddress(cells);
				addresses.add(address);
			}
//			Cell a1 = sheet.getCell(0, 0);
//			String cell_a1 = a1.getContents();
//			AddressHandler.logger.info("Excel file, cell A1: " + cell_a1);
		} catch (IOException ex) {
			AddressHandler.logger.warn("Unable to open file: " + ex.getLocalizedMessage());
		} catch (BiffException ex) {
			AddressHandler.logger.warn("Biff exception: " + ex.getLocalizedMessage());
		}
	}

	private WritableSheet create_sheet_with_header(WritableWorkbook book) {
		WritableSheet sheet = book.createSheet(SHEET_NAME, 0);
		try {
			for (Fields field : Fields.values()) {
				Label label = new Label(field.getIndex(), 0, field.getTitle());
				sheet.addCell(label);
			}
		} catch (WriteException ex) {
		}
		return sheet;
	}

	private void write_addresses_to_sheet(WritableSheet sheet) {
		int row = 0;
		for (VCard vCard : getAdresses()) {
			try {
				row = row + 1;
				StructuredName name = vCard.getStructuredName();
				List<String> prefixes = name.getPrefixes();
				Label prefix = new Label(Fields.PREFIX.getIndex(), row, StringUtils.join(prefixes, " "));
				sheet.addCell(prefix);
				Label given_name = new Label(Fields.GIVEN_NAME.getIndex(), row, name.getGiven());
				sheet.addCell(given_name);
				Label family = new Label(Fields.FAMILY_NAME.getIndex(), row, name.getFamily());
				sheet.addCell(family);
				List<Telephone> tel_numbers = vCard.getTelephoneNumbers();
				for (Telephone tel : tel_numbers) {
					if (tel.getTypes().contains(TelephoneType.HOME)) {
						Label phone = new Label(Fields.HOME_PHONE.getIndex(), row, tel.getText());
						sheet.addCell(phone);
					} else if (tel.getTypes().contains(TelephoneType.CELL)) {
						Label phone = new Label(Fields.CELL_PHONE.getIndex(), row, tel.getText());
						sheet.addCell(phone);
					}
				}
				List<Email> email_addresses = vCard.getEmails();
				List<String> email_strings = new LinkedList<String>();
				for (Email email : email_addresses) {
					email_strings.add(email.getValue());
				}
				Label emails = new Label(Fields.EMAIL.getIndex(), row, StringUtils.join(email_strings, "\n"));
				sheet.addCell(emails);
			} catch (WriteException ex) {
				logger.warn("Writing addresse to cells: " + ex.getLocalizedMessage());
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
	}
}
