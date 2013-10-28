package name.bauhan.sven.tools.addresshandler;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */
public class AddressHandler {

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
	}

	public static void main(String[] args) {
		// create the parser
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine line = parser.parse(defineOptions(), args);
			start(line);
		} catch (ParseException ex) {
		}

		System.out.println("Hello World!");
		CSVReader reader;
	}
}
