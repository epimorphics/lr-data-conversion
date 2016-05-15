package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ppd.PPDCSVLine;

public interface PPDVerificationQueryGenerator {
	public void addLine(PPDCSVLine line);
	public void generate();
}
