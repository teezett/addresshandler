package name.bauhan.sven.tools.addresshandler;

import javafx.application.Application;
import static javafx.application.Application.launch;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ezvcard.VCard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Simple command line address file converter.
 *
 */
public class AddressHandler extends Application {

	protected static final Logger logger = LoggerFactory.getLogger(AddressHandler.class);
	
	/**
	 * Define the command line options.
	 *
	 * @return the available command line options
	 */
	protected static Options defineOptions() {
		Options options = new Options();
		Option help = new Option("h", "print this message");
		options.addOption(help);
		Option readFile = OptionBuilder.withArgName("file")
						.hasArg()
						.withDescription("File to be read")
						.create("f");
		options.addOption(readFile);
		Option outfile = OptionBuilder.withArgName("out")
						.hasArg()
						.withDescription("Output file")
						.create("o");
		options.addOption(outfile);
		return options;
	}

	protected static void start(CommandLine cmdline) {
		if (cmdline.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("addresshandler", defineOptions());
			System.exit(0);
		}
		AddressFile adrfile = null;
		if (cmdline.hasOption("f")) {
			logger.debug("found option -f");
			String filename = cmdline.getOptionValue("f");
			adrfile = AddressFile.create(filename);
			adrfile.readFile();
		}
		AddressFile outfile = null;
		if (cmdline.hasOption("o")) {
			String filename = cmdline.getOptionValue("o");
			outfile = AddressFile.create(filename);
		}
		if ( (adrfile != null) && (outfile != null) ) {
			List<VCard> addresses = adrfile.getAdresses();
			outfile.setAdresses(addresses);
			outfile.writeFile();
		}
	}

	public static void main(String[] args) {
		// create the parser
		launch(args);
//		CommandLineParser parser = new BasicParser();
//		try {
//			CommandLine line = parser.parse(defineOptions(), args);
//			start(line);
//		} catch (ParseException ex) {
//			logger.error("Parse Error: " + ex.getLocalizedMessage());
//			System.exit(1);
//		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
		Scene scene = new Scene(root);

		scene.getStylesheets().add("/styles/mainwindow.css");

		/* @todo: set version dynamically from project */
		primaryStage.setTitle("Address Handler 0.1");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
