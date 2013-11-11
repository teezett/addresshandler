package name.bauhan.sven.tools.addresshandler;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
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

	protected static void readCSV(String filename) {
		Path path = Paths.get(filename);
		try {
			String type = Files.probeContentType(path);
			logger.info("Detected type: " + type);
			CsvReader reader = new CsvReader(filename);
			reader.readHeaders();
			while (reader.readRecord()) {
				String uid = reader.get("uid");
//					logger.info("Found product number " + uid);
			}
			logger.info("Got CSV file with " + reader.getHeaderCount() + " header fields");
		} catch (FileNotFoundException ex) {
			logger.warn("File not found: " + ex.getLocalizedMessage());
		} catch (IOException ex) {
			logger.warn("Unable to get content type: " + ex.getLocalizedMessage());
		}
	}

	protected static void readExcel(String filename) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(filename));
			Sheet sheet = workbook.getSheet(0);
			Cell a1 = sheet.getCell(0, 0);
			String cell_a1 = a1.getContents();
			logger.info("Excel file, cell A1: " + cell_a1);
		} catch (IOException ex) {
			logger.warn("Unable to open file: " + ex.getLocalizedMessage());
		} catch (BiffException ex) {
			logger.warn("Biff exception: " + ex.getLocalizedMessage());
		}
	}
	
	protected static void readFile(String filename) {
		final Pattern csvExt = Pattern.compile(".*[.][cC][sS][vV]");
		final Pattern xlsExt = Pattern.compile(".*[.][xX][lL][sS]");
		if (csvExt.matcher(filename).matches()) {
			readCSV(filename);
		} else if( xlsExt.matcher(filename).matches()) {
			readExcel(filename);
		} else {
			logger.warn("Unrecognized file extension");
		}
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
			readFile(filename);
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
