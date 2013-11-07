package name.bauhan.sven.tools.addresshandler;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
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

/**
 * Hello world!
 *
 */
public class AddressHandler {

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
		return options;
	}

	protected static void start(CommandLine cmdline) {
		if (cmdline.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("addresshandler", defineOptions());
			System.exit(0);
		}
		if (cmdline.hasOption("f")) {
			logger.info("found option -f");
			String filename = cmdline.getOptionValue("f");
			Path path = Paths.get(filename);
			try {
				String type = Files.probeContentType(path);
				logger.info("Detected type: " + type);
				FileReader freader = new FileReader(filename);
				CSVReader reader = new CSVReader(freader);
				List<?> lines = reader.readAll();
				logger.info("Read " + lines.size() + " number of lines");
				String[] first = (String[]) lines.get(0);
				for (int i = 0; i < first.length; i++) {
					System.out.print("cell " + i + ": ");
					System.out.println(first[i]);
				}
			} catch (IOException ex) {
				logger.warn("Unable to get content type: " + ex.getLocalizedMessage());
			}
		}
	}

	public static void main(String[] args) {
		// create the parser
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine line = parser.parse(defineOptions(), args);
			start(line);
		} catch (ParseException ex) {
			logger.error("Parse Error: " + ex.getLocalizedMessage());
			System.exit(1);
		}
//		logger.info("AddressHandler");
//		System.out.println("Hello World!");
	}
}
