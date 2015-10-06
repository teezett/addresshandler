package name.bauhan.sven.tools.addresshandler;

import java.io.File;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle program preferences.
 * 
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public enum HandlerPreferences {
	INSTANCE;
	
	/** Logger instance */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
	/** Access to user preferences */
	private final Preferences user_prefs
					= Preferences.userNodeForPackage(HandlerPreferences.class);
	/** Preference parameters */
	public enum Parameter {
		WORKING_DIR;
	}
	private File workingDir;
	private static final String DEFAULT_WORKING_DIR = System.getProperty("user.home");

	/**
	 * @return the workingDir
	 */
	public File getWorkingDir() {
		if (workingDir == null) {
			workingDir = new File(user_prefs.get(Parameter.WORKING_DIR.toString(), DEFAULT_WORKING_DIR));
		}
		return workingDir;
	}

	/**
	 * @param workingDir the workingDir to set
	 */
	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
		user_prefs.put(Parameter.WORKING_DIR.toString(), workingDir.toString());
	}
	
}
