package com.epimorphics.lr.data.ppd.verification;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.ProgressMonitor;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Generate verification queries for PPD data in a triple store.
 * 
 * @author bwm
 *
 */
public class PPDVerificationQueryGenerator {
	
	private Reader input;
	private ProgressMonitor progressMonitor;
	private ErrorHandler errorHandler;
	private String outputDir;
	
	public PPDVerificationQueryGenerator(Reader input, String outputDir, ProgressMonitor progressMonitor, ErrorHandler errorHandler) {
		this.input = input;
		this.outputDir = outputDir;
		this.progressMonitor = progressMonitor;
		this.errorHandler = errorHandler;
	}
	
	public void generate() {
		CSVReader reader = new CSVReader(input);
		String [] line;
		List<VerificationQueryGenerator> queryGenerators = new ArrayList<VerificationQueryGenerator>();
		queryGenerators.add(new VerifyTransactionCountGenerator(outputDir, errorHandler));
		queryGenerators.add(new VerifyTransactionRecordCountGenerator(outputDir, errorHandler));
		queryGenerators.add(new VerifyAddressCountGenerator(outputDir, errorHandler));
		try {
	        while ((line = reader.readNext()) != null) {
	        	PPDCSVLine ppdLine = new PPDCSVLine(line);
	        	for ( VerificationQueryGenerator generator : queryGenerators) {
	        		generator.addLine(ppdLine);;
	        	}	        	
	            progressMonitor.progressByOne();
	        }
			reader.close();
        	for ( VerificationQueryGenerator generator : queryGenerators) {
        		generator.generate();
        	}	        	
			progressMonitor.close();
		} catch (IOException e) {
			errorHandler.reportError("Error reading CSV file", e);
			throw new Error("fatal error", e);
		}
	}
}
