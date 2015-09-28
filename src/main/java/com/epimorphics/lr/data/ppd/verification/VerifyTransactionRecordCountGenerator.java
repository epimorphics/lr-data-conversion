package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;

public class VerifyTransactionRecordCountGenerator extends VerificationQueryBase implements VerificationQueryGenerator {

	private int count = 0;
	
	VerifyTransactionRecordCountGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "transaction-record-count", errorHandler);
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		count++;
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?trans) AS ?count) {\n");
		sb.append("      ?trans a lrppi:TransactionRecord .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND(\"incorrect transaction record count: should be " + count + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
