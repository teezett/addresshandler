package name.bauhan.sven.tools.addresshandler;

import java.net.URL;
import static name.bauhan.sven.tools.addresshandler.AllTestSuite.assertNoExceptionThrown;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven
 */
public class VCardAdressesTest {
	
	private final static Logger logger = LoggerFactory.getLogger(VCardAdressesTest.class);

	public VCardAdressesTest() {
	}

	/**
	 * Test of file_pattern method, of class VCardAdresses.
	 */
	@Test
	@Ignore("Already tested in factory method")
	public void testFile_pattern() {
	}

	/**
	 * Test of readFile method, of class VCardAdresses.
	 */
	@Test
	@Category(NotImplementedTests.class)
	public void testReadFile() {
		// Arrange
		URL url = this.getClass().getResource("/simple.vcf");
		logger.info("Reading file '" + url.getFile() + "' from path " + url.getPath());
		VCardAdresses instance = (VCardAdresses) AddressFile.create(url.getPath());
		// Act
		instance.readFile();
		// Assert
		assertNoExceptionThrown();
	}
}