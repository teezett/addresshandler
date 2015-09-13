package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
import ezvcard.property.StructuredName;
import java.util.List;
import javafx.scene.control.ListCell;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cell for ListView of VCard objects.
 *
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public class VCardCell extends ListCell<VCard> {

	/**
	 * Logger instance
	 */
	private static transient final Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Override
	protected void updateItem(VCard vCard, boolean empty) {
		super.updateItem(vCard, empty);
		if (vCard != null) {
			String name_str = "<n/a>";
			StructuredName name = vCard.getStructuredName();
			if (name != null) {
				List<String> prefixes = name.getPrefixes();
				String prefix = StringUtils.join(prefixes, " ");
				name_str = prefix + " " + name.getGiven() + " " + name.getFamily();
			}
			setText(name_str);
		}
	}

}
