package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;

/**
 * FXML Controller class
 *
 * @author Sven
 */
public class MainWindowController implements Initializable {

	final FileChooser fileChooser = new FileChooser();
	AddressFile addr_file;
	List<VCard> addresses;

	@FXML
	MenuItem closeMenu;
	@FXML
	MenuItem saveMenu;
	@FXML
	MenuItem saveAsMenu;
	@FXML
	Label leftStatus;
	@FXML
	Label rightStatus;
	@FXML
	ListView addrList;
	@FXML
	TextField prefixText;
	@FXML
	TextField givenText;
	@FXML
	TextField familyText;
	@FXML
	TextField homePhone;
	@FXML
	TextField cellPhone;
	@FXML
	TextArea emailText;
	
	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		dataChanged("Initialized.", "<empty>");
		ObservableList extList = fileChooser.getExtensionFilters();
		extList.add(new FileChooser.ExtensionFilter("All address formats", "*xls", "*.vcf", "*.vcard", "*.ldif"));
		extList.add(new FileChooser.ExtensionFilter("All files", "*.*"));
		extList.add(new FileChooser.ExtensionFilter("Excel", "*.xls"));
		extList.add(new FileChooser.ExtensionFilter("VCard", "*.vcf", "*.vcard"));
		extList.add(new FileChooser.ExtensionFilter("LDIF", "*.ldif"));
		addrList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		addrList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				showAddress(newValue);
			}
		});
	}

	private void showAddress(Number index_) {
		VCard current_addr = addresses.get(index_.intValue());
		StructuredName name = current_addr.getStructuredName();
		List<String> prefixes = name.getPrefixes();
		String prefix = StringUtils.join(prefixes, " ");
		prefixText.setText(prefix);
		givenText.setText(name.getGiven());
		familyText.setText(name.getFamily());
		List<Telephone> tel_numbers = current_addr.getTelephoneNumbers();
		for (Telephone tel : tel_numbers) {
			if (tel.getTypes().contains(TelephoneType.HOME)) {
				homePhone.setText(tel.getText());
			} else if (tel.getTypes().contains(TelephoneType.CELL)) {
				cellPhone.setText(tel.getText());
			}
		}
		emailText.clear();
		List<Email> email_addresses = current_addr.getEmails();
		for (Email mail : email_addresses) {
			emailText.appendText(mail.getValue() + "\n");
		}
	}

	@FXML
	private void handleOpenAction(ActionEvent event) {
		File readFile = fileChooser.showOpenDialog(null);
		if (readFile != null) {
			addr_file = AddressFile.create(readFile.getAbsolutePath());
			addr_file.readFile();
			dataChanged("Opened file.", readFile.getName());
		}
	}

	@FXML
	private void handleCloseAction(ActionEvent event) {
		addr_file = null;
		System.gc();
		dataChanged("Closed file.", "<empty>");
	}

	@FXML
	private void handleSaveAction(ActionEvent event) {
		if (addr_file != null) {
			addr_file.writeFile();
			dataChanged("Saved file.", null);
		}
	}

	@FXML
	private void handleSaveAsAction(ActionEvent event) {
		File saveFile = fileChooser.showSaveDialog(null);
		if (saveFile != null) {
			addr_file = AddressFile.create(saveFile.getAbsolutePath());
			addr_file.setAdresses(addresses);
			addr_file.writeFile();
			dataChanged("Saved file.", saveFile.getName());
		}
	}

	@FXML
	private void handleCloseRequest(ActionEvent event_) {
		Platform.exit();
	}

	private void dataChanged(String action_, String file_) {
		boolean isFileOpened = (addr_file != null);

		// set menu disability
		closeMenu.setDisable(!isFileOpened);
		saveMenu.setDisable(!isFileOpened);
		saveAsMenu.setDisable(!isFileOpened);

		// Set status line
		leftStatus.setText(action_);
		if (file_ != null) {
			rightStatus.setText(file_);
		}

		// set list of addresses
		ObservableList list = addrList.getItems();
		list.clear();
		if (isFileOpened) {
			addresses = addr_file.getAdresses();
			for (VCard vCard : addresses) {
				String name_str = "<n/a>";
				StructuredName name = vCard.getStructuredName();
				if (name != null) {
					List<String> prefixes = name.getPrefixes();
					String prefix = StringUtils.join(prefixes, " ");
					name_str = prefix + " " + name.getGiven() + " " + name.getFamily();
				}
				list.add(name_str);
			}
		}
	}
}
