package name.bauhan.sven.tools.addresshandler;

import java.net.URL;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static name.bauhan.sven.tools.addresshandler.AllTestSuite.assertNoExceptionThrown;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.FormattedName;
import ezvcard.property.StructuredName;
import java.util.HashMap;
import java.util.LinkedList;

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
	
	@Test
	@Category(NotImplementedTests.class)
	public void testWriteFile() {
		// Arrange
		AddressFile xlsFile = AddressFile.create("new.xls");
		List<VCard> addresses = new LinkedList<VCard>();
		VCard vCard = new VCard();
		vCard.addFormattedName(new FormattedName("Dr. Sheldon Cooper"));
		StructuredName name = new StructuredName();
		name.addPrefix("Dr.");
		name.setGiven("Sheldon");
		name.setFamily("Cooper");
		vCard.setStructuredName(name);
		vCard.addTelephoneNumber("+49-5151-12345", TelephoneType.HOME);
		vCard.addTelephoneNumber("+49-171-123456", TelephoneType.CELL);
		vCard.addEmail("sheldon@cooper.name", EmailType.HOME);
		vCard.addEmail("cooper@berkeley.edu", EmailType.WORK);
		addresses.add(vCard);
		// Act
		xlsFile.setAdresses(addresses);
		xlsFile.writeFile();
		// Assert
		assertNoExceptionThrown();
	}
}
