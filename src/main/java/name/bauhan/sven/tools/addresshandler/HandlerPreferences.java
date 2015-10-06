package name.bauhan.sven.tools.addresshandler;

import java.io.File;
import java.util.prefs.Preferences;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle program preferences.
 *
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public enum HandlerPreferences {
	INSTANCE;

	/**
	 * Logger instance
	 */
	private static transient final Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	/**
	 * Access to user preferences
	 */
	private final Preferences user_prefs
					= Preferences.userNodeForPackage(HandlerPreferences.class);

	/**
	 * Preference parameters
	 */
	public enum Parameter {
		WORKING_DIR;
	}
	private static final String DEFAULT_WORKING_DIR = System.getProperty("user.home");
	private final Property<File> workingDir = new SimpleObjectProperty<>();

	/**
	 * Default constructor.
	 */
	private HandlerPreferences() {
		initialize();
	}

	/**
	 * Initialize internal properties with values.
	 */
	private void initialize() {
		File workDir = new File(user_prefs.get(Parameter.WORKING_DIR.toString(), DEFAULT_WORKING_DIR));
		workingDir.setValue(workDir);
		workingDir.addListener((ObservableValue<? extends File> observable,
						File oldValue, File newValue) -> {
							user_prefs.put(Parameter.WORKING_DIR.toString(), newValue.toString());
						});
	}

	/**
	 * Access to working dir property.
	 *
	 * @return working dir property
	 */
	public Property<File> workingDirProperty() {
		return workingDir;
	}

	/**
	 * @return the workingDir
	 */
	public File getWorkingDir() {
		return workingDir.getValue();
	}

	/**
	 * @param workingDir the workingDir to set
	 */
	public void setWorkingDir(File workingDir) {
		this.workingDir.setValue(workingDir);
	}

}
