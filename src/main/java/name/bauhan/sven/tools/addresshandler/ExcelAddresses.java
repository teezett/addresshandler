package name.bauhan.sven.tools.addresshandler;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author Sven
 */
public class ExcelAddresses extends AddressFile {
	
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
	
}
