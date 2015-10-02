package com.epimorphics.lr.data.ppd.verification;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;
import com.epimorphics.lr.data.ppd.ProgressMonitor;

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
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_PAON,      "paon",     errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_PAON,      "paon",     errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_STREET,    "street",   errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_LOCALITY,  "locality", errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_TOWN,      "town",     errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_DISTRICT,  "district", errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_COUNTY,    "county",   errorHandler));
		queryGenerators.add(new VerifyAddressPropertyCountGenerator(outputDir, PPDCSVLine.COLUMN_POSTCODE,  "postcode", errorHandler));
		queryGenerators.add(new VerifyTotalPricesPaidGenerator(outputDir, errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "standard-transaction-count", PPDCSVLine.COLUMN_TRANSACTION_CATEGORY, "A", "lrppi:transactionCategory", "lrppi:standardPricePaidTransaction", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "additional-transaction-count", PPDCSVLine.COLUMN_TRANSACTION_CATEGORY, "B", "lrppi:transactionCategory", "lrppi:additionalPricePaidTransaction", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "leasehold-count", PPDCSVLine.COLUMN_ESTATE_TYPE, "L", "lrppi:estateType", "lrcommon:leasehold", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "freehold-count", PPDCSVLine.COLUMN_ESTATE_TYPE, "F", "lrppi:estateType", "lrcommon:freehold", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "new-build-count", PPDCSVLine.COLUMN_NEWBUILD, "Y", "lrppi:newBuild", "true", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "not-new-build-count", PPDCSVLine.COLUMN_NEWBUILD, "N", "lrppi:newBuild", "false", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "flat-maisonette-count", PPDCSVLine.COLUMN_PROPERTY_TYPE, "F", "lrppi:propertyType", "lrcommon:flat-maisonette", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "terraced-count", PPDCSVLine.COLUMN_PROPERTY_TYPE, "T", "lrppi:propertyType", "lrcommon:terraced", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "semi-detached-count", PPDCSVLine.COLUMN_PROPERTY_TYPE, "S", "lrppi:propertyType", "lrcommon:semi-detached", errorHandler));
		queryGenerators.add(new VerifyPropertyValueCountGenerator(outputDir, "detached-count", PPDCSVLine.COLUMN_PROPERTY_TYPE, "D", "lrppi:propertyType", "lrcommon:detached", errorHandler));
	    PPDCSVLineFilter filter1995 = new TransactionYearFilter("1995");
	    queryGenerators.add(new VerifyFilteredPropertyValueCountGenerator(outputDir, "1995-count", filter1995, "lrppi:transactionDate", "STRSTARTS(STR(?propertyValue), \"1995\")", errorHandler));
		PPDCSVLineFilter filter2005 = new TransactionYearFilter("2005");
		queryGenerators.add(new VerifyFilteredPropertyValueCountGenerator(outputDir, "2005-count", filter2005, "lrppi:transactionDate", "STRSTARTS(STR(?propertyValue), \"2005\")", errorHandler));
	    PPDCSVLineFilter filter2015 = new TransactionYearFilter("2015");
		queryGenerators.add(new VerifyFilteredPropertyValueCountGenerator(outputDir, "2015-count", filter2015, "lrppi:transactionDate", "STRSTARTS(STR(?propertyValue), \"2015\")", errorHandler));
        PPDCSVLineFilter random = new RandomFilter(50000);        
        String[] filters = {
        		"?p != lrppi:publishDate"
        };
        queryGenerators.add(
        	new VerifyRemotePropertiesPresentLocallyGenerator(
        		outputDir, 
        		"remote-transaction-record-properties",
        		random,
        		"lrppi:TransactionRecord",
        		"http://landregistry.data.gov.uk/landregistry/query",
        		filters,
        		errorHandler)
        );
        String[] filters2 = {
        		"?p != lrppi:hasTransactionRecord"
        };
        queryGenerators.add(
        	new VerifyRemotePropertiesPresentLocallyGenerator(
        		outputDir, 
        		"remote-transaction-properties",
        		random,
        		"lrppi:Transaction",
        		"http://landregistry.data.gov.uk/landregistry/query",
        		filters2,
        		errorHandler)
        );
        String[] filters3 = {
        		"?p != lrppi:publishDate",
        		"?p != lrppi:transactionCategory"
        };
        queryGenerators.add(
        	new VerifyLocalPropertiesPresentRemotelyGenerator(
        		outputDir, 
        		"local-transaction-record-properties",
        		random,
        		"lrppi:TransactionRecord",
        		"http://landregistry.data.gov.uk/landregistry/query",
        		filters3,
        		errorHandler)
        );
        String[] filters4 = {
        		"?p != lrppi:hasTransactionRecord"
        };
        queryGenerators.add(
        	new VerifyLocalPropertiesPresentRemotelyGenerator(
        		outputDir, 
        		"local-transaction-properties",
        		random,
        		"lrppi:Transaction",
        		"http://landregistry.data.gov.uk/landregistry/query",
        		filters4,
        		errorHandler)
        );
        queryGenerators.add(
        	new VerifyLocalAddressPropertiesPresentRemotelyGenerator(
        		outputDir, 
        		"local-address-properties",
        		random,
        		"http://landregistry.data.gov.uk/landregistry/query",
        		errorHandler)
        );
        queryGenerators.add(
        	new VerifyRemoteAddressPropertiesPresentLocallyGenerator(
        		outputDir, 
        		"remote-address-properties",
        		random,
        		"http://landregistry.data.gov.uk/landregistry/query",
        		errorHandler)
        );

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
