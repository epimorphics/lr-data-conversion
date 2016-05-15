package com.epimorphics.lr.data.ppd.verification;

import java.util.HashSet;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;

public class VerifyRemoteAddressPropertiesPresentLocallyGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {

	private PPDCSVLineFilter lineFilter;
	private String service;
	private Set<String> ids = new HashSet<String>();
	
	VerifyRemoteAddressPropertiesPresentLocallyGenerator(String outputDir, String name, PPDCSVLineFilter lineFilter, String service, ErrorHandler errorHandler) {
		super(outputDir, name, errorHandler);
		this.lineFilter = lineFilter;
		this.service = service;
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
		sb.append("  SERVICE <" + service + "> {\n");
		sb.append("    {\n");
		sb.append("      SELECT ?item ?p ?o {\n");
		sb.append("        VALUES ?item {\n");
		for (String id : ids) {
			sb.append("          <http://landregistry.data.gov.uk/data/ppi/address/" + id + ">\n");
		}
		sb.append("        }\n");
		sb.append("\n");
		sb.append("        ?item ?p ?o .\n");
		sb.append("      }\n");
		sb.append("    }\n");
		sb.append("  }\n"); 
		sb.append("\n");
		
		sb.append("  FILTER NOT EXISTS {\n");
		sb.append("    ?item ?p ?o .\n");
		sb.append("  }\n");
		sb.append("\n");
		
		sb.append("  BIND(CONCAT('missing local address property value for ', STR(?item), ' (', STR(?p), ', ', STR(?o), ')') AS ?message)\n");
		writeQuery(sb.toString());
	}
}
