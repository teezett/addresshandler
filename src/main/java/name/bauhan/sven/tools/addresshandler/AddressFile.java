package name.bauhan.sven.tools.addresshandler;

import com.csvreader.CsvReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven
 */
abstract public class AddressFile {
	
	protected static final Logger logger = LoggerFactory.getLogger(AddressFile.class);

	protected String file_name;
	private static final Map<Pattern, Class> types = new HashMap<Pattern, Class>();
	static {
		types.put(ExcelAddresses.file_pattern(), ExcelAddresses.class);
	}
	
	public static AddressFile create(String filename) {
//		for (Map.Entry<Pattern,Class> entry : types.entrySet()) {
//			if (entry.getKey().matcher(filename).matches()) {
//				try {
//					Class<? extends AddressFile> clazz = entry.getValue();
//					return clazz.newInstance();
//				} catch (InstantiationException ex) {
//					logger.error(ex.getLocalizedMessage());
//					return null;
//				} catch (IllegalAccessException ex) {
//					logger.error(ex.getLocalizedMessage());
//					return null;
//				}
//			}
//		}
		if (ExcelAddresses.file_pattern().matcher(filename).matches()) {
			logger.info("Identified Excel file");
			return new ExcelAddresses(filename);
		}
		logger.warn("Unrecognized file type");
		return null;
	}

	protected AddressFile(String filename) {
		file_name = filename;
	}

//	abstract protected Pattern file_pattern();
	
	abstract protected void readFile();
//	protected void readFile() {
//		final Pattern csvExt = Pattern.compile(".*[.][cC][sS][vV]");
//		final Pattern xlsExt = Pattern.compile(".*[.][xX][lL][sS]");
//		final Pattern ldifExt = Pattern.compile(".*[.][lL][dD][iI][fF]");
//		if (csvExt.matcher(file_name).matches()) {
//			readCSV(file_name);
//		} else if (xlsExt.matcher(file_name).matches()) {
//			readExcel(file_name);
//		} else if (ldifExt.matcher(file_name).matches()) {
//			readLdif(file_name);
//		} else {
//			AddressHandler.logger.warn("Unrecognized file extension");
//		}
//	}

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

//	protected static void readExcel(String filename) {
//		try {
//			Workbook workbook = Workbook.getWorkbook(new File(filename));
//			Sheet sheet = workbook.getSheet(0);
//			Cell a1 = sheet.getCell(0, 0);
//			String cell_a1 = a1.getContents();
//			AddressHandler.logger.info("Excel file, cell A1: " + cell_a1);
//		} catch (IOException ex) {
//			AddressHandler.logger.warn("Unable to open file: " + ex.getLocalizedMessage());
//		} catch (BiffException ex) {
//			AddressHandler.logger.warn("Biff exception: " + ex.getLocalizedMessage());
//		}
//	}

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
