package com.epimorphics.lr.data.ukhpi.verification;

import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;

public interface UKHPIVerificationQueryGenerator {
	public void addLine(UKHPICSVLine line);
	public void generate();
}
