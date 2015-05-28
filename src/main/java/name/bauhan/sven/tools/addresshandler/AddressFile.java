package name.bauhan.sven.tools.addresshandler;

//import com.csvreader.CsvReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ezvcard.VCard;

/**
 * Base class for different address data types.
 *
 * @author Sven
 */
abstract public class AddressFile {

	public enum Formats {

		VCARD,
		LDIF,
		EXCEL;
	}
	/** Logger instance */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	protected List<VCard> addresses = null;
	protected String file_name;
	private static final Map<Pattern, Class> types = new HashMap<Pattern, Class>();
	private LoadTask loadTask;

	public void setLoadTask(LoadTask loadTask) {
		this.loadTask = loadTask;
	}
	
	static {
		types.put(ExcelAddresses.file_pattern(), ExcelAddresses.class);
	}

	/**
	 * Factory method to create a derived object.
	 *
	 * @param filename detection of data type according to filename extension
	 * @return object of derived class
	 */
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
		} else if (LDIFAdresses.file_pattern().matcher(filename).matches()) {
			logger.info("Identified LDIF file");
			return new LDIFAdresses(filename);
		} else if (VCardAdresses.file_pattern().matcher(filename).matches()) {
			logger.info("Identified vCard file");
			return new VCardAdresses(filename);
		}
		throw new IllegalArgumentException("Unknown file extension: unrecognized type");
//		logger.warn("Unrecognized file type");
//		return null;
	}

	protected AddressFile(String filename) {
		file_name = filename;
	}

	abstract public void readFile();

	abstract public void writeFile();

//	protected static void readCSV(String filename) {
//		Path path = Paths.get(filename);
//		try {
//			String type = Files.probeContentType(path);
//			AddressHandler.logger.info("Detected type: " + type);
//			CsvReader reader = new CsvReader(filename);
//			reader.readHeaders();
//			while (reader.readRecord()) {
//				String uid = reader.get("uid");
//			}
//			AddressHandler.logger.info("Got CSV file with " + reader.getHeaderCount() + " header fields");
//		} catch (FileNotFoundException ex) {
//			AddressHandler.logger.warn("File not found: " + ex.getLocalizedMessage());
//		} catch (IOException ex) {
//			AddressHandler.logger.warn("Unable to get content type: " + ex.getLocalizedMessage());
//		}
//	}

	protected static void readLdif(String filename) {
//		LdifReader reader = new LdifReader();
//		try {
//			List<LdifEntry> entries = reader.parseLdifFile(filename);
//			for (LdifEntry entry : entries) {
//				String name = entry.getDn().getName();
//				AddressHandler.logger.info("Name: " + name);
//			}
//		} catch (LdapLdifException ex) {
//			AddressHandler.logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
//		}
	}

	/**
	 * @return the adresses
	 */
	public List<VCard> getAdresses() {
		return addresses;
	}

	/**
	 * @param adresses the adresses to set
	 */
	public void setAdresses(List<VCard> adresses) {
		this.addresses = adresses;
	}
}
