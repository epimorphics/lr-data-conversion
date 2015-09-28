package com.epimorphics.lr.data.ppd.verification;

import java.util.HashSet;
import java.util.Set;

import com.epimorphics.lr.data.ppd.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDUtil;

public class VerifyAddressCountGenerator extends VerificationQueryBase implements VerificationQueryGenerator {

	private Set<String> addressHashes = new HashSet<String>();
	
	VerifyAddressCountGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "address-count", errorHandler);
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		addressHashes.add(PPDUtil.getAddressHash(line));
	}

	@Override
	public void generate() {
		int count = addressHashes.size();
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT (COUNT(DISTINCT ?address) AS ?count) {\n");
		sb.append("      ?address a lrcommon:BS7666Address .\n");
		sb.append("    }\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER(?count != " + count + ")\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND(\"incorrect address count: should be " + count + "\" AS ?message)\n");
		writeQuery(sb.toString());
	}
}
