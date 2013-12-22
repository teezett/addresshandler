package name.bauhan.sven.tools.addresshandler;

import java.util.regex.Pattern;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Sven
 */
public class ExcelAddressesTest {
	
	/**
	 * Test of file_pattern method, of class ExcelAddresses.
	 */
	@Test
	@Ignore("Creation is tested in factory method")
	public void testFile_pattern() {
		System.out.println("file_pattern");
		Pattern expResult = null;
		Pattern result = ExcelAddresses.file_pattern();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of readFile method, of class ExcelAddresses.
	 */
	@Test
	@Category(NotImplementedTests.class)
	public void testReadFile() {
		System.out.println("readFile");
		ExcelAddresses instance = (ExcelAddresses) AddressFile.create("test.xls");
		instance.readFile();
		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}
}
