package name.bauhan.sven.tools.addresshandler;

import java.util.List;
import java.util.regex.Pattern;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;

/**
 * Handle LDIF address files.
 * @author Sven
 */
public class LDIFAdresses extends AddressFile {

	public LDIFAdresses(String filename) {
		super(filename);
	}

	static protected Pattern file_pattern() {
		final Pattern xlsExt = Pattern.compile(".*[.][lL][dD][iI][fF]");
		return xlsExt;
	}

	/**
	 * Reading an LDIF file.
	 */
	@Override
	protected void readFile() {
		LdifReader reader = new LdifReader();
		try {
			List<LdifEntry> entries = reader.parseLdifFile(file_name);
			for (LdifEntry entry : entries) {
				String name = entry.getDn().getName();
				AddressHandler.logger.info("Name: " + name);
			}
		} catch (LdapLdifException ex) {
			AddressHandler.logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void writeFile() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
