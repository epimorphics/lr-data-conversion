package com.epimorphics.lr.data.ppd;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Convert a PPD CSV file to NQUADS.
 * 
 * @author bwm
 *
 */
public class PPDCSVFileConverter {
	
	private String publishDate;
	private Reader input;
	private ProgressMonitor progressMonitor;
	private ErrorHandler errorHandler;
	private PrintStream output;
	
	public PPDCSVFileConverter(String publishDate, Reader input, PrintStream output, ProgressMonitor progressMonitor, ErrorHandler errorHandler) {
		this.publishDate = publishDate;
		this.input = input;
		this.output = output;
		this.progressMonitor = progressMonitor;
		this.errorHandler = errorHandler;
	}
	
	public void convert() {
		CSVReader reader = new CSVReader(input);
		String [] line;
		Set<String> transactionIDs = new HashSet<String>();
		try {
	        while ((line = reader.readNext()) != null) {
	        	PPDCSVLineConverter lineConverter = new PPDCSVLineConverter(publishDate, transactionIDs, line, output, errorHandler);
	        	lineConverter.convertLine();
	        	progressMonitor.progressByOne();
	        }
			reader.close();
			progressMonitor.close();
		} catch (IOException e) {
			errorHandler.reportError("Error reading CSV file", e);
			throw new Error("fatal error", e);
		}
	}
}
