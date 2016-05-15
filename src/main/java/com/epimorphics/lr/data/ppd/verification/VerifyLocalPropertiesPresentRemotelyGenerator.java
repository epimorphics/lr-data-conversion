package com.epimorphics.lr.data.ppd.verification;

import java.util.HashSet;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;

public class VerifyLocalPropertiesPresentRemotelyGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {

	private PPDCSVLineFilter lineFilter;
	private String type;
	private String service;
	private String[] filters;
	private Set<String> ids = new HashSet<String>();
	
	VerifyLocalPropertiesPresentRemotelyGenerator(String outputDir, String name, PPDCSVLineFilter lineFilter, String type, String service, String[] filters, ErrorHandler errorHandler) {
		super(outputDir, name, errorHandler);
		this.lineFilter = lineFilter;
		this.type = type ;
		this.service = service;
		this.filters = filters;
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		if (lineFilter.test(line)) {
			ids.add(line.getGUID());
		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  VALUES ?transId {\n");
		for (String id : ids) {
			sb.append("    \"" + id + "\"^^lrppi:TransactionIdDatatype\n");
		}
		sb.append("  }\n");
		sb.append("\n");
		
		sb.append("  ?item lrppi:transactionId ?transId \n");
		sb.append("     ;  a " + type + "\n");
		sb.append("     ;  ?p ?o\n");
		sb.append("     .\n");
		for (String filter : filters) {
		  sb.append("  FILTER (" + filter + ")\n");
		}

		sb.append("  SERVICE <" + service + "> {\n");
		sb.append("    FILTER NOT EXISTS {\n");
		sb.append("      ?s lrppi:transactionId ?transId \n");
		sb.append("       ;  a " + type + "\n");
		sb.append("       ;  ?p ?o");
		sb.append("    }\n");
		sb.append("  }\n"); 
		
		sb.append("\n");
		
		sb.append("  BIND(CONCAT('missing remote property value for " + type + " ', STR(?transId), ' (', STR(?p), ', ', STR(?o), ')') AS ?message)\n");
		writeQuery(sb.toString());
	}
}
