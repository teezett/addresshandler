package name.bauhan.sven.tools.addresshandler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Ignore;

/**
 *
 * @author Sven
 */
public class AddressFileTest {
	
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
		ExcelAddresses result = (ExcelAddresses) AddressFile.create(filename);
//		AddressFile result = AddressFile.create(filename);
		assertThat(result, isA(ExcelAddresses.class));
	}

	/**
	 * Test of readFile method, of class AddressFile.
	 */
	@Test
	@Ignore("Testing derived methods")
	public void testReadFile() {
		System.out.println("readFile");
		AddressFile instance = null;
		instance.readFile();
		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}

	/**
	 * Test of readCSV method, of class AddressFile.
	 */
	@Test
	@Ignore("Test in derived class")
	public void testReadCSV() {
		System.out.println("readCSV");
		String filename = "";
		AddressFile.readCSV(filename);
		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}

	/**
	 * Test of readLdif method, of class AddressFile.
	 */
	@Test
	@Ignore("Test in derived class")
	public void testReadLdif() {
		System.out.println("readLdif");
		String filename = "";
		AddressFile.readLdif(filename);
		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}

	public class AddressFileImpl extends AddressFile {

		public AddressFileImpl() {
			super("");
		}

		public void readFile() {
		}
	}
}