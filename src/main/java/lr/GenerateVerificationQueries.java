package lr;



import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.ProgressMonitor;
import com.epimorphics.lr.data.ppd.verification.PPDVerificationQueryGenerator;
/**
 * Main program to generate queries to verify the PPD data in a triple store
 * 
 * Reads from a CSV file and writes to stdout.
 * 
 * @author bwm
 *
 */
public class GenerateVerificationQueries {

	@Option(name="--input", usage="path to input file")
	private String inputPath = "data/test/input/appd-test.csv";
	@Option(name="--outputDir", usage="directory into which to write SPARQL queries")
	private String outputDirPath = "sparql/ppd-verification";
	private static ErrorHandler errorHandler = new ErrorHandler();
		
	public static void main(String[] args) {
		try {			
		    GenerateVerificationQueries instance = new GenerateVerificationQueries() ;
		    instance.parseArgs(args).execute();
		} catch( Throwable t ) {
            errorHandler.reportError("generate SPARQL queries failed");
        }		
	}
	
	private GenerateVerificationQueries parseArgs(String[] args) throws CmdLineException {
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
        createOutputDirectory(outputDirPath);
		ProgressMonitor progressMonitor = new ProgressMonitor("processed", Logger.getLogger("progress"));
		PPDVerificationQueryGenerator verificationQueryGenerator = new PPDVerificationQueryGenerator(input, outputDirPath, progressMonitor, errorHandler);
		verificationQueryGenerator.generate();
	}
	
	Reader getInputReader(String path) {
		try {
			return new FileReader(path);
		} catch (Exception e) {
			errorHandler.reportError("failed to open input file: " + path, e);
			throw new RuntimeException();
		}		
	}
	
	private void createOutputDirectory(String outputDirPath) {
		Path path = Paths.get(outputDirPath);
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			errorHandler.reportError("failed to create output directory: " + outputDirPath, e);
			// TODO Auto-generated catch block
			throw new Error("failed to create output directory");
		}
	}
}
