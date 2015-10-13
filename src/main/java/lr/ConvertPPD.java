package lr;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.epimorphics.lr.data.ppd.PPDCSVFileConverter;
import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.ProgressMonitor;
/**
 * Main program to convert a PPD CSV file to NQUADS
 * 
 * Reads from a CSV file and writes to stdout.
 * 
 * @author bwm
 *
 */
public class ConvertPPD {

	@Option(name="--input", usage="path to input file")
	private String inputPath = "data/test/input/appd-test.csv";
	@Option(name="--publishDate", usage="publication date")
	private String publishDate = null;
	private static ErrorHandler errorHandler = new ErrorHandler();
		
	public static void main(String[] args) {
		try {			
		    ConvertPPD instance = new ConvertPPD() ;
		    instance.parseArgs(args).execute();
		} catch( Throwable t ) {
            errorHandler.reportError("conversion failed");
        }		
	}
	
	private ConvertPPD parseArgs(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.err.println();
			throw e;
		}
		return this;
	}
	
	private void execute() {
		Reader input = getInputReader(inputPath);
		ProgressMonitor progressMonitor = new ProgressMonitor("processed", Logger.getLogger("progress"));
		PPDCSVFileConverter converter = new PPDCSVFileConverter(publishDate, input, System.out, progressMonitor, errorHandler);
		converter.convert();
	}
	
	Reader getInputReader(String path) {
		try {
			return new BufferedReader(
					new InputStreamReader(new FileInputStream(path), "UTF-8")
		          );
		} catch (Exception e) {
			errorHandler.reportError("failed to open input file: " + path, e);
			throw new RuntimeException();
		}		
	}

}
