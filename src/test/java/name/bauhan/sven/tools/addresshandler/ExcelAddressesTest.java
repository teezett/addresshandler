package name.bauhan.sven.tools.addresshandler;

import java.net.URL;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static name.bauhan.sven.tools.addresshandler.AllTestSuite.assertNoExceptionThrown;

/**
 * Testing class ExcelAddresses.
 * @author Sven
 */
public class ExcelAddressesTest {

	private final static Logger logger = LoggerFactory.getLogger(ExcelAddressesTest.class);
	
	/**
	 * Test of file_pattern method, of class ExcelAddresses.
	 */
	@Test
	@Ignore("Creation is tested in factory method")
	public void testFile_pattern() {
	}

	/**
	 * Test of readFile method, of class ExcelAddresses.
	 */
	@Test
//	@Category(NotImplementedTests.class)
	public void testReadFile() {
		// Arrange
		URL url = this.getClass().getResource("/sample.xls");
		logger.info("Reading file '" + url.getFile() + "' from path " + url.getPath());
		ExcelAddresses instance = (ExcelAddresses) AddressFile.create(url.getPath());
		// Act
		instance.readFile();
		// Assert
		assertNoExceptionThrown();
	}
}
