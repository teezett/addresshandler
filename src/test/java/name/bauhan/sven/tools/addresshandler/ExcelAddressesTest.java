package name.bauhan.sven.tools.addresshandler;

import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 *
 * @author Sven
 */
public class ExcelAddressesTest extends TestCase {
	
	public ExcelAddressesTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test of file_pattern method, of class ExcelAddresses.
	 */
//	public void testFile_pattern() {
//		System.out.println("file_pattern");
//		Pattern expResult = null;
//		Pattern result = ExcelAddresses.file_pattern();
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	/**
	 * Test of readFile method, of class ExcelAddresses.
	 */
	public void testReadFile() {
		System.out.println("readFile");
		ExcelAddresses instance = (ExcelAddresses) AddressFile.create("test.xls");
		instance.readFile();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}
