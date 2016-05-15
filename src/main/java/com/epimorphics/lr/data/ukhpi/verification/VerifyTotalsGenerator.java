package com.epimorphics.lr.data.ukhpi.verification;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;
import com.epimorphics.lr.data.ukhpi.UKHPITableDescription;
import com.epimorphics.lr.data.ukhpi.UKHPITableDescription.ColumnDescription;

public class VerifyTotalsGenerator extends UKHPIVerificationQueryBase implements UKHPIVerificationQueryGenerator {

//	private double totalHousePriceIndex = 0.0;
//	private long totalSalesVolume = 0;
//	private double totalAveragePrice = 0.0;
	
	private double[] totals = new double[UKHPITableDescription.columns.length];
	
	VerifyTotalsGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "column-totals", errorHandler);
	}
	
	@Override
	public void addLine(UKHPICSVLine line) {
		for (int i = 0 ; i < UKHPITableDescription.columns.length ; i++ ) {
			ColumnDescription cd = UKHPITableDescription.columns[i];
			if (cd.getMeasure()) {
				Double val = line.getDouble(i);
				if (val != null) {
					totals[i] += val;
				}
			}
		}
//		Integer vol = line.getSalesVolume();
//		if (vol != null) {
//			totalSalesVolume += vol;
//		}
//		
//		Double averagePrice = line.getDouble(UKHPICSVLine.COLUMN_AVERAGE_PRICE);
//		if (averagePrice != null) {
//			totalAveragePrice += averagePrice;
//		}
//		
//		Double housePriceIndex = line.getDouble(UKHPICSVLine.COLUMN_HOUSE_PRICE_INDEX);
//		if (housePriceIndex != null) {
//			totalHousePriceIndex += housePriceIndex;
//		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		for (int i = 0 ; i < UKHPITableDescription.columns.length ; i++ ) {
			ColumnDescription cd = UKHPITableDescription.columns[i];
			if (cd.getMeasure()) {
				generateOneColumn(sb, cd.getShortName(), String.format("%.0f", totals[i]));
				if (anotherMeasureableColumnExists(i)) {
					sb.append("  } UNION {\n");					
				}
			}
		}
		sb.append("  }\n");
		writeQuery(sb.toString());

//		generateOneColumn(sb, "averagePrice", Double.toString(totalAveragePrice));
//		sb.append("  } UNION {\n");
//		generateOneColumn(sb, "housePriceIndex", Double.toString(totalHousePriceIndex));
//		sb.append("  } UNION {\n");
//		generateOneColumn(sb, "salesVolume", Long.toString(totalSalesVolume));
//		sb.append("  }\n");
//		writeQuery(sb.toString());
	}
	
	private boolean anotherMeasureableColumnExists(int columnIndex) {
		for (int i = columnIndex + 1 ; i < UKHPITableDescription.columns.length ; i++ ) {
			if (UKHPITableDescription.columns[i].getMeasure()) {
				return true;
			}
		}
		return false;
	}
	
    public void generateOneColumn(StringBuffer sb, String shortName, String expectedValue) {
		sb.append("    {\n");
		sb.append("      SELECT (sum(?value) AS ?totalValue) {\n");
		sb.append("        ?indexRecord a ukhpi:MonthlyIndicesByRegion .\n");
		sb.append("        ?indexRecord ukhpi:" + shortName + " ?value .\n");
		sb.append("      }\n");
		sb.append("    }\n");
		sb.append("\n");
		sb.append("    FILTER(ABS(?totalValue - " + expectedValue + ") > 1)\n");
		sb.append("    BIND(?totalValue AS ?item)\n");
		sb.append("    BIND(\"incorrect total value for " + shortName + ": should be approximately " + expectedValue + "\" AS ?message)\n");
	}
}
