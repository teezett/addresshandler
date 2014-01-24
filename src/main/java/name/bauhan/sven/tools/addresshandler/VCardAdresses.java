package name.bauhan.sven.tools.addresshandler;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle vCard address files.
 * @author Sven
 */
public class VCardAdresses extends AddressFile {

	/**
	 * Logger instance
	 */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	public VCardAdresses(String filename) {
		super(filename);
	}

	static protected Pattern file_pattern() {
		final Pattern vcardExt = Pattern.compile(".*[.](([vV][cC][aA][rR][dD])|([vV][cC][fF]))");
		return vcardExt;
	}

	/**
	 * Reading a vCard file.
	 */
	@Override
	protected void readFile() {
		try {
			List<VCard> addr = Ezvcard.parse(new File(file_name)).all();
			setAdresses(addr);
			logger.debug("Found vcard file with " + addr.size() + " entries");
		} catch (IOException ex) {
			logger.warn("IOException: " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void writeFile() {
		String content = Ezvcard.write(getAdresses()).go();
		File file = new File(file_name);
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException ex) {
			logger.error("Writing file: " + ex.getLocalizedMessage());
		}
	}
	
}
