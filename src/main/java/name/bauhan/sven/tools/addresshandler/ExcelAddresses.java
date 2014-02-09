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
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jxl.CellType;
import jxl.DateCell;
import jxl.write.DateTime;
import org.apache.commons.lang3.StringUtils;

/**
 * Handle Excel address files.
 * 
 * @author Sven
 */
public class ExcelAddresses extends AddressFile {

	/**
	 * Logger instance
	 */
	private static transient final Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	/**
	 * Name of sheet with addresses
	 */
	final private static String SHEET_NAME = "Addresses";
	final public static Pattern pattern = Pattern.compile(".*[.][xX][lL][sS]");
	private Map<Fieldnames, Integer> field2column;

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
	 * @param sheet_ excel sheet
	 * @return mapping, in which column which address field is stored
	 */
	private Map<Fieldnames, Integer> identifyColumns(Sheet sheet_) {
		field2column = new EnumMap<Fieldnames, Integer>(Fieldnames.class);
		Cell[] headers = sheet_.getRow(0);
		for (int i = 0; i < headers.length; i++) {
			logger.debug("Searching title " + headers[i].getContents() + " in fields");
			Fieldnames field = Fieldnames.TITLE2FIELD.get(headers[i].getContents());
			if (field != null) {
				logger.debug("Found field " + field.getExcel() + " in column " + i);
				field2column.put(field, i);
			}
		}
		return field2column;
	}

	/**
	 * Read one row of cells and convert to VCard.
	 *
	 * @param cells row of cells
	 * @return generated VCard
	 */
	private VCard readAddress(Cell[] cells) {
		logger.debug("Row has " + String.valueOf(cells.length) + " entries");
		VCard vCard = new VCard();
		Integer col;
		StructuredName name = new StructuredName();
		col = field2column.get(Fieldnames.PREFIX);
		if ((col != null) && (col < cells.length)) {
			name.addPrefix(cells[col].getContents());
		}
		col = field2column.get(Fieldnames.GIVEN_NAME);
		if ((col != null) && (col < cells.length)) {
			name.setGiven(cells[col].getContents());
		}
		col = field2column.get(Fieldnames.FAMILY_NAME);
		if ((col != null) && (col < cells.length)) {
			name.setFamily(cells[col].getContents());
		}
		vCard.setStructuredName(name);
		col = field2column.get(Fieldnames.HOME_PHONE);
		if ((col != null) && (col < cells.length)) {
			vCard.addTelephoneNumber(cells[col].getContents(), TelephoneType.HOME);
		}
		col = field2column.get(Fieldnames.CELL_PHONE);
		if ((col != null) && (col < cells.length)) {
			vCard.addTelephoneNumber(cells[col].getContents(), TelephoneType.CELL);
		}
		col = field2column.get(Fieldnames.EMAIL);
		if ((col != null) && (col < cells.length)) {
			String email = cells[col].getContents();
			vCard.addEmail(email, EmailType.HOME);
		}
		Address address = new Address();
		address.addType(AddressType.HOME);
		col = field2column.get(Fieldnames.EXT_ADDR);
		if ((col != null) && (col < cells.length)) {
			String ext_addr = cells[col].getContents();
			address.setExtendedAddress(ext_addr);
		}
		col = field2column.get(Fieldnames.STREET);
		if ((col != null) && (col < cells.length)) {
			String street = cells[col].getContents();
			address.setStreetAddress(street);
		}
		col = field2column.get(Fieldnames.POSTAL_CODE);
		if ((col != null) && (col < cells.length)) {
			String postal_code = cells[col].getContents();
			address.setPostalCode(postal_code);
		}
		col = field2column.get(Fieldnames.CITY);
		if ((col != null) && (col < cells.length)) {
			String city = cells[col].getContents();
			address.setLocality(city);
		}
		vCard.addAddress(address);
		col = field2column.get(Fieldnames.BIRTHDAY);
		if ((col != null) && (col < cells.length) && (cells[col].getType() == CellType.DATE)) {
			DateCell dCell = (DateCell) cells[col];
			Birthday birth = new Birthday(dCell.getDate());
			vCard.setBirthday(birth);
		}
		return vCard;
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
			for (int i = 1; i < sheet.getRows(); i++) {
				logger.debug("Reading address number " + String.valueOf(i));
				Cell[] cells = sheet.getRow(i);
				VCard address = readAddress(cells);
				addresses.add(address);
			}
		} catch (IOException ex) {
			AddressHandler.logger.warn("Unable to open file: " + ex.getLocalizedMessage());
		} catch (BiffException ex) {
			AddressHandler.logger.warn("Biff exception: " + ex.getLocalizedMessage());
		}
	}

	private WritableSheet create_sheet_with_header(WritableWorkbook book) {
		WritableSheet sheet = book.createSheet(SHEET_NAME, 0);
		try {
			for (Fieldnames field : Fieldnames.values()) {
				if (field.getColumn() < 0) {
					// skip fields that are unused in Excel
					continue;
				}
				Label label = new Label(field.getColumn(), 0, field.getExcel());
				sheet.addCell(label);
			}
		} catch (WriteException ex) {
		}
		return sheet;
	}

	private void write_address_to_sheet(WritableSheet sheet_, int row_, VCard vCard_) throws WriteException {
		List<Address> address_list = vCard_.getAddresses();
		if (address_list.isEmpty()) {
			return;
		}
		Address addr = address_list.get(0);
		String ext_addr = addr.getExtendedAddress();
		if (ext_addr != null) {
			Label ext_cell = new Label(Fieldnames.EXT_ADDR.getColumn(), row_, ext_addr);
			sheet_.addCell(ext_cell);
		}
		String street = addr.getStreetAddress();
		if (street != null) {
			Label street_cell = new Label(Fieldnames.STREET.getColumn(), row_, street);
			sheet_.addCell(street_cell);
		}
		String postal = addr.getPostalCode();
		if (postal != null) {
			Label postal_cell = new Label(Fieldnames.POSTAL_CODE.getColumn(), row_, postal);
			sheet_.addCell(postal_cell);
		}
		String city = addr.getLocality();
		if (city != null) {
			Label city_cell = new Label(Fieldnames.CITY.getColumn(), row_, city);
			sheet_.addCell(city_cell);
		}
	}

	private void write_addresses_to_sheet(WritableSheet sheet) {
		int row = 0;
		for (VCard vCard : getAdresses()) {
			try {
				row = row + 1;
				StructuredName name = vCard.getStructuredName();
				List<String> prefixes = name.getPrefixes();
				Label prefix = new Label(Fieldnames.PREFIX.getColumn(), row, StringUtils.join(prefixes, " "));
				sheet.addCell(prefix);
				Label given_name = new Label(Fieldnames.GIVEN_NAME.getColumn(), row, name.getGiven());
				sheet.addCell(given_name);
				Label family = new Label(Fieldnames.FAMILY_NAME.getColumn(), row, name.getFamily());
				sheet.addCell(family);
				List<Telephone> tel_numbers = vCard.getTelephoneNumbers();
				for (Telephone tel : tel_numbers) {
					if (tel.getTypes().contains(TelephoneType.HOME)) {
						Label phone = new Label(Fieldnames.HOME_PHONE.getColumn(), row, tel.getText());
						sheet.addCell(phone);
					} else if (tel.getTypes().contains(TelephoneType.CELL)) {
						Label phone = new Label(Fieldnames.CELL_PHONE.getColumn(), row, tel.getText());
						sheet.addCell(phone);
					}
				}
				List<Email> email_addresses = vCard.getEmails();
				List<String> email_strings = new LinkedList<String>();
				for (Email email : email_addresses) {
					email_strings.add(email.getValue());
				}
				Label emails = new Label(Fieldnames.EMAIL.getColumn(), row, StringUtils.join(email_strings, "\n"));
				sheet.addCell(emails);
				write_address_to_sheet(sheet, row, vCard);
				Birthday birth = vCard.getBirthday();
				if (birth != null) {
					DateTime birth_cell = new DateTime(Fieldnames.BIRTHDAY.getColumn(), row, birth.getDate());
					sheet.addCell(birth_cell);
				}
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
