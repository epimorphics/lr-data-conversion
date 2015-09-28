package lr;



import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
/**
 * Main program to run verification queries
 * 
 * Reads queries from a directory and reports those that return results.
 * 
 * @author bwm
 *
 */
public class RunVerificationQueries {

	@Option(name="--inputDir", usage="path to input file")
	private String inputDir = "sparql/ppd-verification";
	@Option(name="--endpoint", usage="query endpoint", required=true)
	private String endPoint = null;
	private static ErrorHandler errorHandler = new ErrorHandler();
		
	public static void main(String[] args) {
		try {			
		    RunVerificationQueries instance = new RunVerificationQueries() ;
		    instance.parseArgs(args).execute();
		} catch( Throwable t ) {
            errorHandler.reportError("run SPARQL queries failed");
        }		
	}
	
	private RunVerificationQueries parseArgs(String[] args) throws CmdLineException {
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
		logger.info("Starting verification queries");
		File[] queryFiles = getQueryFiles(inputDir);
		for (File queryFile : queryFiles) {
			runVerificationQuery(queryFile);
		}
		logger.info("running verification queries completed");
	}
	
	private File[] getQueryFiles(String inputPath) {
		File dir = new File(inputPath);
		return dir.listFiles();
	}
	
	private void runVerificationQuery(File queryFile) {
		String queryString;
		try {
			queryString = readFile(queryFile.getAbsolutePath());
		} catch (IOException e) {
			errorHandler.reportError("failed to read query " + queryFile, e);
			return;
		}		
		runVerificationQuery(queryFile, queryString);		
	}
	
	private void runVerificationQuery(File queryFile, String queryString) {
		logger.info("Executing query: " + queryFile);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(endPoint, queryString)) {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; ) {
		      QuerySolution soln = results.nextSolution() ;
		      String item = soln.getLiteral("item").getLexicalForm();
		      String message = soln.getLiteral("message").getLexicalForm();
		      errorHandler.reportError("verification failed: " + item + " : " + message);
		    }
		} catch (Exception e) {
			errorHandler.reportError("error executing query " + queryFile, e );
		}		
	}
	static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return new String(encoded, "UTF8");
	}
	
	private static final Logger logger = Logger.getLogger(RunVerificationQueries.class);
	

}
