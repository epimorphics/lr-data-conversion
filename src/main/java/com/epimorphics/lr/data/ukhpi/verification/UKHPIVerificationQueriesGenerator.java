package com.epimorphics.lr.data.ukhpi.verification;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ProgressMonitor;
import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;

/**
 * Generate verification queries for PPD data in a triple store.
 * 
 * Creates a collection of query generators.  Each line of the input CSV
 * file is read and passed to each query generator.  Once the entire
 * CSV file has been read, each query generator outputs its query.
 * 
 * @author bwm
 *
 */
public class UKHPIVerificationQueriesGenerator {
	
	private Reader input;
	private ProgressMonitor progressMonitor;
	private ErrorHandler errorHandler;
	private String outputDir;
	
	public UKHPIVerificationQueriesGenerator(Reader input, String outputDir, ProgressMonitor progressMonitor, ErrorHandler errorHandler) {
		this.input = input;
		this.outputDir = outputDir;
		this.progressMonitor = progressMonitor;
		this.errorHandler = errorHandler;
	}
	
	public void generate() {
		CSVReader reader = new CSVReader(input);
		
		String [] line;
		List<UKHPIVerificationQueryGenerator> queryGenerators = specifyQueryGenerators();

		try {
			reader.readNext();  // skip header line
		    int lineCount = 1;

	        while ((line = reader.readNext()) != null) {
	        	UKHPICSVLine UKHPILine = new UKHPICSVLine(lineCount++, line, errorHandler);
	        	for ( UKHPIVerificationQueryGenerator generator : queryGenerators) {
	        		generator.addLine(UKHPILine);;
	        	}	        	
	            progressMonitor.progressByOne();
	        }
			reader.close();
        	for ( UKHPIVerificationQueryGenerator generator : queryGenerators) {
        		generator.generate();
        	}	        	
			progressMonitor.close();
		} catch (IOException e) {
			errorHandler.reportError("Error reading CSV file", e);
			throw new Error("fatal error", e);
		}
	}
	
	private List<UKHPIVerificationQueryGenerator> specifyQueryGenerators() {
		List<UKHPIVerificationQueryGenerator> queryGenerators = new ArrayList<UKHPIVerificationQueryGenerator>();
        
		queryGenerators.add(
        		new VerifyIndexRecordCountGenerator(outputDir, errorHandler)
        	);
        queryGenerators.add(
        		new VerifyTotalsGenerator(outputDir, errorHandler)
            );
		return queryGenerators;		
	}
}
