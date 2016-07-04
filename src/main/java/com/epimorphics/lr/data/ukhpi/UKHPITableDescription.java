package com.epimorphics.lr.data.ukhpi;

public class UKHPITableDescription {
	
	public  static final ColumnDescription[] columns = {
			new ColumnDescription(  0, "date",                                        false),
			new ColumnDescription(  1, "regionName",                                  false),
            new ColumnDescription(  2, "areaCode",                                    false),
            new ColumnDescription(  3, "averagePrice",                                true),
            new ColumnDescription(  4, "housePriceIndex",                             true),
            new ColumnDescription(  5, "housePriceIndexSA",                           true),
            new ColumnDescription(  6, "percentageChange",                     true),
            new ColumnDescription(  7, "percentageAnnualChange",                      true),
            new ColumnDescription(  8, "averagePriceSA",                              true),
            new ColumnDescription(  9, "salesVolume",                                 true),
            new ColumnDescription( 10, "averagePriceDetached",                        true),
            new ColumnDescription( 11, "housePriceIndexDetached",                     true),
            new ColumnDescription( 12, "percentageChangeDetached",             true),
            new ColumnDescription( 13, "percentageAnnualChangeDetached",              true),
            new ColumnDescription( 14, "averagePriceSemiDetached",                    true),
            new ColumnDescription( 15, "housePriceIndexSemiDetached",                 true),
            new ColumnDescription( 16, "percentageChangeSemiDetached",         true),
            new ColumnDescription( 17, "percentageAnnualChangeSemiDetached",          true),
            new ColumnDescription( 18, "averagePriceTerraced",                        true),
            new ColumnDescription( 19, "housePriceIndexTerraced",                     true),
            new ColumnDescription( 20, "percentageChangeTerraced",             true),
            new ColumnDescription( 21, "percentageAnnualChangeTerraced",              true),
            new ColumnDescription( 22, "averagePriceFlatMaisonette",                  true),
            new ColumnDescription( 23, "housePriceIndexFlatMaisonette",               true),
            new ColumnDescription( 24, "percentageChangeFlatMaisonette",       true),
            new ColumnDescription( 25, "percentageAnnualChangeFlatMaisonette",        true),
            new ColumnDescription( 26, "averagePriceCash",                            true),
            new ColumnDescription( 27, "housePriceIndexCash",                         true),
            new ColumnDescription( 28, "percentageChangeCash",                 true),
            new ColumnDescription( 29, "percentageAnnualChangeCash",                  true),
            new ColumnDescription( 30, "averagePriceMortgage",                        true),
            new ColumnDescription( 31, "housePriceIndexMortgage",                     true),
            new ColumnDescription( 32, "percentageChangeMortgage",             true),
            new ColumnDescription( 33, "percentageAnnualChangeMortgage",              true),
            new ColumnDescription( 34, "averagePriceFirstTimeBuyer",                  true),
            new ColumnDescription( 35, "housePriceIndexFirstTimeBuyer",               true),
            new ColumnDescription( 36, "percentageChangeFirstTimeBuyer",       true),
            new ColumnDescription( 37, "percentageAnnualChangeFirstTimeBuyer",        true),
            new ColumnDescription( 38, "averagePriceFormerOwnerOccupier",             true),
            new ColumnDescription( 39, "housePriceIndexFormerOwnerOccupier",          true),
            new ColumnDescription( 40, "percentageChangeFormerOwnerOccupier",  true),
            new ColumnDescription( 41, "percentageAnnualChangeFormerOwnerOccupier",   true),
            new ColumnDescription( 42, "averagePriceNewBuild",                        true),
            new ColumnDescription( 43, "housePriceIndexNewBuild",                     true),
            new ColumnDescription( 44, "percentageChangeNewBuild",             true),
            new ColumnDescription( 45, "percentageAnnualChangeNewBuild",              true),
            new ColumnDescription( 46, "averagePriceExistingProperty",                true),
            new ColumnDescription( 47, "housePriceIndexExistingProperty",             true),
            new ColumnDescription( 48, "percentageChangeExistingProperty",     true),
            new ColumnDescription( 49, "percentageAnnualChangeExistingProperty",      true)
	};
	
	public static class ColumnDescription {
		private int columnNumber;
		private String shortName;
		private boolean measure;
		ColumnDescription(int columnNumber, String shortName, boolean measure) {
			this.columnNumber = columnNumber;
			this.shortName = shortName;
			this.measure = measure;			
		}
		
		public int getColumnNumber() { return columnNumber ; }
		public String getShortName()    { return shortName ; }
	    public boolean getMeasure()      { return measure ; }
	}

}
