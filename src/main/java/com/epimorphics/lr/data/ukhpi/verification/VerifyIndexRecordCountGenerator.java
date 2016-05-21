package com.epimorphics.lr.data.ukhpi.verification;

import java.util.HashSet;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;

public class VerifyIndexRecordCountGenerator extends UKHPIVerificationQueryBase implements UKHPIVerificationQueryGenerator {

	private int count = 0;
	
	VerifyIndexRecordCountGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "index-record-count", errorHandler);
	}
	
	@Override
	public void addLine(UKHPICSVLine line) {
		count++;
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?indexRecord) AS ?count) {\n");
		sb.append("      ?indexRecord a ukhpi:MonthlyIndicesByRegion .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND(\"incorrect number of index records: should be " + count + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
