package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
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
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import name.bauhan.sven.tools.addresshandler.viewmodel.AddressViewModel;
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
	AddressViewModel addressView = new AddressViewModel();
//	ObjectProperty<VCard> currentAddress = new SimpleObjectProperty<>();
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
	ListView<VCard> addrList;
	@FXML
	TextField orgText;
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
		addrList.setCellFactory((ListView<VCard> param) -> new VCardCell());
		// bindings
		if (HandlerPreferences.INSTANCE.getWorkingDir().isDirectory()) {
			fileChooser.setInitialDirectory(HandlerPreferences.INSTANCE.getWorkingDir());
		}
		HandlerPreferences.INSTANCE.workingDirProperty().bind(fileChooser.initialDirectoryProperty());
		prefixText.textProperty().bind(addressView.prefixProperty());
		givenText.textProperty().bind(addressView.givenNameProperty());
		familyText.textProperty().bind(addressView.familyNameProperty());
		orgText.textProperty().bind(addressView.orgNameProperty());
		homePhone.textProperty().bind(addressView.prefixProperty());
		cellPhone.textProperty().bind(addressView.mobilePhoneProperty());
		emailText.textProperty().bind(addressView.emailProperty());
		extAddrText.textProperty().bind(addressView.extAddressProperty());
		streetText.textProperty().bind(addressView.streetProperty());
		plzText.textProperty().bind(addressView.plzProperty());
		cityText.textProperty().bind(addressView.cityProperty());
		birthPick.valueProperty().bind(addressView.birthdayProperty());
	}

	private void showAddress(Number index_) {
		VCard current_addr = addresses.get(index_.intValue());
		addressView.set(current_addr);
	}

	@FXML
	private void handleOpenAction(ActionEvent event) {
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
			// change working directory
			fileChooser.setInitialDirectory(readFile.getParentFile());
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
//			addr_file.setAdresses(entries.get());
			addr_file.setAdresses(addresses);
			addr_file.writeFile();
			dataChanged("Saved file.", saveFile.getName());
			// change working directory
			fileChooser.setInitialDirectory(saveFile.getParentFile());
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
			addresses.forEach((vCard) -> list.add(vCard));
		}
	}
}
