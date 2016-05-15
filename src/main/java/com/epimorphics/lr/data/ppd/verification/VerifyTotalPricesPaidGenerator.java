package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;

public class VerifyTotalPricesPaidGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {

	private long total = 0;
	
	VerifyTotalPricesPaidGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "total-prices-paid", errorHandler);
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		total += Integer.parseInt(line.getPricePaid());
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (SUM(?pricePaid) AS ?sum) {\n");
		sb.append("      ?trans lrppi:pricePaid ?pricePaid \n");
		sb.append("         .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?sum != " + total + ")\n");
		sb.append("  BIND(?sum AS ?item)\n");
		sb.append("  BIND(\"incorrect total prices paid: should be " + total + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
