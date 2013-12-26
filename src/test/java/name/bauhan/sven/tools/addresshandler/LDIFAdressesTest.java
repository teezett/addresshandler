package name.bauhan.sven.tools.addresshandler;

import java.net.URL;
import java.util.regex.Pattern;
import static name.bauhan.sven.tools.addresshandler.AllTestSuite.assertNoExceptionThrown;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven
 */
public class LDIFAdressesTest {
	
	private final static Logger logger = LoggerFactory.getLogger(ExcelAddressesTest.class);

	public LDIFAdressesTest() {
	}

	/**
	 * Test of file_pattern method, of class LDIFAdresses.
	 */
	@Test
	@Ignore("Already tested in factory method")
	public void testFile_pattern() {
		System.out.println("file_pattern");
		Pattern expResult = null;
		Pattern result = LDIFAdresses.file_pattern();
		assertEquals(expResult, result);
		fail("The test case is a prototype.");
	}

	/**
	 * Test of readFile method, of class LDIFAdresses.
	 */
	@Test
	public void testReadFile() {
		// Arrange
		URL url = this.getClass().getResource("/simple.ldif");
		logger.info("Reading file '" + url.getFile() + "' from path " + url.getPath());
		LDIFAdresses instance = (LDIFAdresses) AddressFile.create(url.getPath());
		// Act
		instance.readFile();
		// Assert
		assertNoExceptionThrown();
	}
}