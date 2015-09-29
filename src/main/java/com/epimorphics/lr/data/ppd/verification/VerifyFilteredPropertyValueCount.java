package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;

public class VerifyFilteredPropertyValueCount extends VerificationQueryBase implements VerificationQueryGenerator {

	private PPDCSVLineFilter lineFilter;
	private String property;
	private String filter;
	private int count = 0;
	
	VerifyFilteredPropertyValueCount(String outputDir, String name, PPDCSVLineFilter lineFilter, String property, String filter, ErrorHandler errorHandler) {
		super(outputDir, name, errorHandler);
		this.lineFilter = lineFilter;
		this.property = property ;
		this.filter = filter;
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		if (lineFilter.test(line)) {
			count++;
		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?s) AS ?count) {\n");
		sb.append("      ?s " + property + " ?propertyValue  .\n");
		sb.append("      FILTER ( " + filter + ")\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND('incorrect property value count for (" + property + ", " + filter + ") : should be " + count + "' AS ?message)\n");
		writeQuery(sb.toString());
	}
}
