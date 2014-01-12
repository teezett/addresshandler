package name.bauhan.sven.tools.addresshandler;

import java.net.URL;
import java.util.List;
import static name.bauhan.sven.tools.addresshandler.AllTestSuite.assertNoExceptionThrown;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ezvcard.VCard;

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
//	@Category(NotImplementedTests.class)
	public void testReadFile() {
		// Arrange
		URL url = this.getClass().getResource("/simple.vcf");
		logger.info("Reading file '" + url.getFile() + "' from path " + url.getPath());
		VCardAdresses instance = (VCardAdresses) AddressFile.create(url.getPath());
		// Act
		instance.readFile();
		List<VCard> adresses = instance.getAdresses();
		VCard vCard = adresses.get(0);
		// Assert
		assertNoExceptionThrown();
		assertThat("At least one address should be read", adresses, is(notNullValue()));
		assertThat("The one address should be read from file", adresses.size(), is(1));
		assertThat("The found addresses name is Forrest Gump", vCard.getFormattedName().getValue(), is("Forrest Gump"));
	}
}