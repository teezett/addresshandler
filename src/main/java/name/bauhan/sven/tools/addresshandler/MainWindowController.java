package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Sven
 */
public class MainWindowController implements Initializable {

	/**
	 * Logger instance
	 */
	private static transient final org.slf4j.Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	final FileChooser fileChooser = new FileChooser();
	AddressFile addr_file;
	List<VCard> addresses;
//	ListProperty<VCard> entries = new SimpleListProperty<>();

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
	@FXML
	TextField extAddrText;
	@FXML
	TextField streetText;
	@FXML
	TextField plzText;
	@FXML
	TextField cityText;
	@FXML
	DatePicker birthPick;
	File currentPath;

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
		addrList.getSelectionModel().selectedIndexProperty().addListener(
						(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
							showAddress(newValue);
						});
	}

	private void showAddress(Number index_) {
//		VCard current_addr = entries.get(index_.intValue());
		VCard current_addr = addresses.get(index_.intValue());
		StructuredName name = current_addr.getStructuredName();
		List<String> prefixes = name.getPrefixes();
		String prefix = StringUtils.join(prefixes, " ");
		prefixText.setText(prefix);
		givenText.setText(name.getGiven());
		familyText.setText(name.getFamily());
		List<Telephone> tel_numbers = current_addr.getTelephoneNumbers();
		tel_numbers.stream().forEach((tel) -> {
			if (tel.getTypes().contains(TelephoneType.HOME)) {
				homePhone.setText(tel.getText());
			} else if (tel.getTypes().contains(TelephoneType.CELL)) {
				cellPhone.setText(tel.getText());
			}
		});
		emailText.clear();
		List<Email> email_addresses = current_addr.getEmails();
		email_addresses.stream().forEach((mail) -> {
			emailText.appendText(mail.getValue() + "\n");
		});
		List<Address> address_list = current_addr.getAddresses();
		if (!address_list.isEmpty()) {
			Address addr = address_list.get(0);
			extAddrText.setText(addr.getExtendedAddress());
			streetText.setText(addr.getStreetAddress());
			plzText.setText(addr.getPostalCode());
			cityText.setText(addr.getLocality());
		}
		birthPick.getEditor().clear();
		Birthday birth = current_addr.getBirthday();
		if (birth != null) {
			Instant instant = Instant.ofEpochMilli(birth.getDate().getTime());
			LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
			birthPick.setValue(res);
		}
	}

	@FXML
	private void handleOpenAction(ActionEvent event) {
		if (currentPath != null) {
			fileChooser.setInitialDirectory(currentPath);
		}
		File readFile = fileChooser.showOpenDialog(null);
		if (readFile != null) {
			addr_file = AddressFile.create(readFile.getAbsolutePath());
			// open progress dialog
			FXMLLoader loader = new FXMLLoader();
			URL dialog_address = this.getClass().getResource("/fxml/Progress.fxml");
			logger.debug("Dialog address: " + dialog_address);
			loader.setLocation(dialog_address);
			AnchorPane page;
			try {
				page = (AnchorPane) loader.load();
				// Create the dialog Stage.
			} catch (IOException ex) {
				logger.error("Unable to initialize Progress dialog!");
				return;
			}
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Loading File ...");
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			// show dialog
			dialogStage.show();
			// read the file
			LoadTask loadTask = new LoadTask(addr_file);
			logger.debug("new load Task " + loadTask);
			addr_file.setLoadTask(loadTask);
			loadTask.setOnSucceeded((WorkerStateEvent ev) -> {
				dialogStage.close();
				dataChanged("Opened file.", readFile.getName());
			});
			Thread thread = new Thread(loadTask);
			thread.start();
			// update statusbar
			currentPath = readFile.getParentFile();
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
		if (currentPath != null) {
			fileChooser.setInitialDirectory(currentPath);
		}
		File saveFile = fileChooser.showSaveDialog(null);
		if (saveFile != null) {
			addr_file = AddressFile.create(saveFile.getAbsolutePath());
//			addr_file.setAdresses(entries.get());
			addr_file.setAdresses(addresses);
			addr_file.writeFile();
			dataChanged("Saved file.", saveFile.getName());
			currentPath = saveFile.getParentFile();
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
//			entries.setAll(addr_file.getAdresses());
			addresses = addr_file.getAdresses();
			addresses.stream().map((vCard) -> {
				String name_str = "<n/a>";
				StructuredName name = vCard.getStructuredName();
				if (name != null) {
					List<String> prefixes = name.getPrefixes();
					String prefix = StringUtils.join(prefixes, " ");
					name_str = prefix + " " + name.getGiven() + " " + name.getFamily();
				}
				return name_str;
			}).forEach((name_str) -> {
				list.add(name_str);
			});
		}
	}
}
