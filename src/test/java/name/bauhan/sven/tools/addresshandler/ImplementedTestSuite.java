package name.bauhan.sven.tools.addresshandler;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all already implemented tests.
 * @author Sven
 */
@RunWith(Categories.class)
@Suite.SuiteClasses({name.bauhan.sven.tools.addresshandler.AllTestSuite.class})
@Categories.ExcludeCategory(NotImplementedTests.class)
public class ImplementedTestSuite {}
