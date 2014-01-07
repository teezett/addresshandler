package name.bauhan.sven.tools.addresshandler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Sven
 */
public class AddressFileTest {
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	public AddressFileTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of create method, of class AddressFile.
	 */
	@Test
	public void testCreate() {
		String filename = "sample.xls";
		ExcelAddresses xlsAddr = (ExcelAddresses) AddressFile.create(filename);
		LDIFAdresses ldifAddr = (LDIFAdresses) AddressFile.create("sample.ldif");
		VCardAdresses vcfAddr = (VCardAdresses) AddressFile.create("sample.vcf");
		VCardAdresses vcardAddr = (VCardAdresses) AddressFile.create("sample.vcard");
		assertThat(xlsAddr, isA(ExcelAddresses.class));
		assertThat(ldifAddr, isA(LDIFAdresses.class));
		assertThat(vcfAddr, isA(VCardAdresses.class));
		assertThat(vcardAddr, isA(VCardAdresses.class));
	}

	/**
	 * Test exception handling of factory method.
	 */
	@Test
	public void test_that_exception_is_thrown_when_file_extension_is_unrecognized() {
		expected.expect(IllegalArgumentException.class);
		expected.expectMessage("unrecognized type");
		AddressFile addrFile = AddressFile.create("empty.txt");
	}
	
	/**
	 * Test of readFile method, of class AddressFile.
	 */
	@Test
	@Ignore("Testing derived methods")
	public void testReadFile() {
	}

}