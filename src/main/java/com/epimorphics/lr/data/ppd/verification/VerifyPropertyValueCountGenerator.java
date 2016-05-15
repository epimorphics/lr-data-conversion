package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;

public class VerifyPropertyValueCountGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {

	private int column;
	private String csvValue;
	private String property;
	private String propertyValue;
	private int count = 0;
	
	VerifyPropertyValueCountGenerator(String outputDir, String name, int column, String csvValue, String property, String propertyValue, ErrorHandler errorHandler) {
		super(outputDir, name, errorHandler);
		this.column = column;
		this.csvValue = csvValue;
		this.property = property ;
		this.propertyValue = propertyValue;
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		if (line.get(column).equals(csvValue)) {
			count++;
		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?s) AS ?count) {\n");
		sb.append("      ?s " + property + " " + propertyValue + " .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND(\"incorrect property value count for (" + property + ", " + propertyValue + ") : should be " + count + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
