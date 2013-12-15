package name.bauhan.sven.tools.addresshandler;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;

/**
 *
 * @author Sven
 */
abstract public class AddressFile {
	
	private String file_name;
	private static final HashMap types = new HashMap();
	static {
		types.put(ExcelAddresses.file_pattern(), ExcelAddresses.class);
	}
	
	public static AddressFile create(String filename) {
		for (Iterator it = types.entrySet().iterator(); it.hasNext();) {
//			HashMap.Entry<Pattern,Class> entry = it.next();
		}
		return null;
	}

	protected AddressFile(String filename) {
		file_name = filename;
	}

//	abstract protected Pattern file_pattern();
	
	protected void readFile() {
		final Pattern csvExt = Pattern.compile(".*[.][cC][sS][vV]");
		final Pattern xlsExt = Pattern.compile(".*[.][xX][lL][sS]");
		final Pattern ldifExt = Pattern.compile(".*[.][lL][dD][iI][fF]");
		if (csvExt.matcher(file_name).matches()) {
			readCSV(file_name);
		} else if (xlsExt.matcher(file_name).matches()) {
			readExcel(file_name);
		} else if (ldifExt.matcher(file_name).matches()) {
			readLdif(file_name);
		} else {
			AddressHandler.logger.warn("Unrecognized file extension");
		}
	}

	protected static void readCSV(String filename) {
		Path path = Paths.get(filename);
		try {
			String type = Files.probeContentType(path);
			AddressHandler.logger.info("Detected type: " + type);
			CsvReader reader = new CsvReader(filename);
			reader.readHeaders();
			while (reader.readRecord()) {
				String uid = reader.get("uid");
			}
			AddressHandler.logger.info("Got CSV file with " + reader.getHeaderCount() + " header fields");
		} catch (FileNotFoundException ex) {
			AddressHandler.logger.warn("File not found: " + ex.getLocalizedMessage());
		} catch (IOException ex) {
			AddressHandler.logger.warn("Unable to get content type: " + ex.getLocalizedMessage());
		}
	}

	protected static void readExcel(String filename) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(filename));
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

	protected static void readLdif(String filename) {
		LdifReader reader = new LdifReader();
		try {
			List<LdifEntry> entries = reader.parseLdifFile(filename);
			for (LdifEntry entry : entries) {
				String name = entry.getDn().getName();
				AddressHandler.logger.info("Name: " + name);
			}
		} catch (LdapLdifException ex) {
			AddressHandler.logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		}
	}
	
}
