package com.epimorphics.lr.data.ukhpi;

import com.epimorphics.lr.data.ErrorHandler;

public class UKHPICSVLine {
	
	private static final String[] shortName = {
	  "refPeriod",
	  "regionName",
	  "gssCode",
	  "averagePrice",
	  "housePriceIndex",
	  "housePriceIndexSA",
	  "percentageMonthlyChange",
	  "percentageAnnualChange",
	  "averagePriceSA",
	  "salesVolume"	  
	};
	
	public static final int COLUMN_AVERAGE_PRICE = 3;
	public static final int COLUMN_HOUSE_PRICE_INDEX = 4;
	public static final int COLUMN_SALES_VOLUME = 9;
	
	private String[] line;
	private int lineNumber;
	private ErrorHandler errorHandler;
	
	public UKHPICSVLine(int lineNumber, String[] line, ErrorHandler errorHandler) {
		this.line = line;
		this.lineNumber = lineNumber;
		this.errorHandler = errorHandler;
	}
	
	public String getColumnName(int columnNumber) {
		return shortName[columnNumber];
	}
	
	public String get(int column) {
		return line[column];
	}
	
	public Integer getInteger(int column) {
		String cell = line[column];
		if (cell == null || cell.length() == 0) {
			return null;
		} else {
			try {
			    return Integer.parseInt(cell);
			} catch (NumberFormatException e) {
				errorHandler.reportError("line " + lineNumber + ": invalid integer in column " + column + " : '" + cell + "'");
			    return null;
			}
		}		
	}
	
	public Double getDouble(int column) {
		String cell = line[column];
		if (cell == null || cell.length() == 0) {
			return null;
		} else {
			try {
			    return Double.valueOf(cell);
			} catch (NumberFormatException e) {
				errorHandler.reportError("line " + lineNumber + ": invalid double in column " + column + " : '" + cell + "'");
			    return null;
			}
		}
		
	}
	
	public Integer getSalesVolume() {
		return getInteger(COLUMN_SALES_VOLUME);
	}
}
