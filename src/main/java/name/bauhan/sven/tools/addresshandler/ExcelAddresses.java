package name.bauhan.sven.tools.addresshandler;

import java.util.regex.Pattern;

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
	
}
