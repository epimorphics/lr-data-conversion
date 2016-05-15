package com.epimorphics.lr.data.ppd.verification;

import java.util.HashSet;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDUtil;

public class VerifyAddressPropertyCountGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {

	private int column;
	private String property;
	private Set<String> addressHashes = new HashSet<String>();
	
	VerifyAddressPropertyCountGenerator(String outputDir, int column, String property, ErrorHandler errorHandler) {
		super(outputDir, "addresses-with-" + property + "-property-count", errorHandler);
		this.column = column;
		this.property = property;
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		if (PPDUtil.substituteDefault(line.get(column), null) != null) {
			addressHashes.add(line.getAddressHash());
		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?address) AS ?count) {\n");
		sb.append("      ?address a lrcommon:BS7666Address \n");
		sb.append("          ;    lrcommon:" + property + " ?val ;\n");
		sb.append("          .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		int count = addressHashes.size();
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND(\"incorrect count of addresses with " + property + " property: should be " + count + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
