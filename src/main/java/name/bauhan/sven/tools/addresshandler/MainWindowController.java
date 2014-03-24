package name.bauhan.sven.tools.addresshandler;

import ezvcard.VCard;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Sven
 */
public class MainWindowController implements Initializable {

	final FileChooser fileChooser = new FileChooser();
	AddressFile addr_file;

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
	
	/**
	 * Initializes the controller class.
	 *
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		closeMenu.setDisable(true);
		saveMenu.setDisable(true);
		saveAsMenu.setDisable(true);
		leftStatus.setText("Initialized.");
		rightStatus.setText("<empty>");
	}

	@FXML
	private void handleOpenAction(ActionEvent event) {
		File readFile = fileChooser.showOpenDialog(null);
		if (readFile != null) {
			addr_file = AddressFile.create(readFile.getAbsolutePath());
			addr_file.readFile();
			closeMenu.setDisable(false);
			saveMenu.setDisable(false);
			saveAsMenu.setDisable(false);
			leftStatus.setText("Opened File.");
			rightStatus.setText(readFile.getName());
		}
	}

	@FXML
	private void handleCloseAction(ActionEvent event) {
		addr_file = null;
		System.gc();
		closeMenu.setDisable(true);
		saveMenu.setDisable(true);
		saveAsMenu.setDisable(true);
		leftStatus.setText("Closed file.");
		rightStatus.setText("<empty>");
	}

	@FXML
	private void handleSaveAction(ActionEvent event) {
		if (addr_file != null) {
			addr_file.writeFile();
			leftStatus.setText("Saved file.");
		}
	}

	@FXML
	private void handleSaveAsAction(ActionEvent event) {
		File saveFile = fileChooser.showSaveDialog(null);
		if (saveFile != null) {
			List<VCard> addresses = addr_file.getAdresses();
			addr_file = AddressFile.create(saveFile.getAbsolutePath());
			addr_file.setAdresses(addresses);
			addr_file.writeFile();
			leftStatus.setText("Saved file.");
			rightStatus.setText(saveFile.getName());
		}
	}

	@FXML
	private void handleCloseRequest(ActionEvent event_) {
		Platform.exit();
	}
}
