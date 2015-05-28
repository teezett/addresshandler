package name.bauhan.sven.tools.addresshandler;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public class LoadTask extends Task<Integer> {

	public LoadTask(AddressFile addr_file) {
		addrFile = addr_file;
	}

	/** Logger instance */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private final AddressFile addrFile;
	private Integer lines;
	
	@Override
	protected Integer call() throws Exception {
		addrFile.readFile();
		return lines;
	}

	public void update(long done) {
		updateProgress(done, lines);
	}
}
