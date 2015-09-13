package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
import javafx.scene.control.ListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cell for ListView of VCard objects.
 * 
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public class VCardCell extends ListCell<VCard>{

	/** Logger instance */
	private static transient final Logger logger =
					LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

}
