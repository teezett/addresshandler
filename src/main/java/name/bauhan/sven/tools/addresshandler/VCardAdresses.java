package name.bauhan.sven.tools.addresshandler;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle vCard address files.
 * @author Sven
 */
public class VCardAdresses extends AddressFile {

	private final Logger logger;

	public VCardAdresses(String filename) {
		super(filename);
		this.logger = LoggerFactory.getLogger(this.getClass());
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
		VCard vcard;
		try {
			vcard = Ezvcard.parse(new File(file_name)).first();
			logger.info("Found vcard entry with name " + vcard.getFormattedName().getValue());
		} catch (IOException ex) {
			logger.warn("IOException: " + ex.getLocalizedMessage());
		}
	}
	
}
