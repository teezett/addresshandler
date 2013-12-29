package name.bauhan.sven.tools.addresshandler;

import java.util.regex.Pattern;

/**
 * Handle vCard address files.
 * @author Sven
 */
public class VCardAdresses extends AddressFile {

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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
