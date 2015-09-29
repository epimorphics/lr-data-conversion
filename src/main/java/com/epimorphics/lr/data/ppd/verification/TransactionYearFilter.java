package com.epimorphics.lr.data.ppd.verification;

import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;

public class TransactionYearFilter implements PPDCSVLineFilter {
	
	private final String year;
	
	TransactionYearFilter(String year) {
		this.year = year;
	}

	@Override
	public boolean test(PPDCSVLine line) {
		return line.getTransactionDate().startsWith(year);
	}

}
