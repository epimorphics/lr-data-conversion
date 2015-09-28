package com.epimorphics.lr.data.ppd;

public class PPDCSVLine {
	
	private static final int COLUMN_GUID = 0;
	private static final int COLUMN_PRICE_PAID = 1;
	private static final int COLUMN_TRANSACTION_DATE = 2;
	private static final int COLUMN_POSTCODE = 3;
	private static final int COLUMN_PROPERTY_TYPE = 4;
	private static final int COLUMN_NEWBUILD = 5;
	private static final int COLUMN_ESTATE_TYPE = 6;
    private static final int COLUMN_PAON = 7;
	private static final int COLUMN_SAON = 8;
	private static final int COLUMN_STREET = 9;
	private static final int COLUMN_LOCALITY = 10;
	private static final int COLUMN_TOWN = 11;
	private static final int COLUMN_DISTRICT = 12;
	private static final int COLUMN_COUNTY = 13;
	private static final int COLUMN_TRANSACTION_CATEGORY = 14;
	
	String[] line;
	
	public PPDCSVLine(String[] line) {
		this.line = line;
	}
	
	public String getGUID() {
		String guid = line[COLUMN_GUID].substring(1);
		guid = guid.substring(0, guid.length()-1);
		return guid;		
	}
	
	public String getPricePaid() {
		return line[COLUMN_PRICE_PAID];
	}
	
	public String getTransactionDate() {
		return line[COLUMN_TRANSACTION_DATE];
	}
	
	public String getPostCode() {
		return line[COLUMN_POSTCODE];
	}
	
	public String getPropertyType() {
		return line[COLUMN_PROPERTY_TYPE];
	}
	
	public String getNewBuild() {
		return line[COLUMN_NEWBUILD];
	}

	public String getEstateType() {
		return line[COLUMN_ESTATE_TYPE];
	}
	
	public String getPaon() {
		return line[COLUMN_PAON];
	}
	
	public String getSaon() {
		return line[COLUMN_SAON];
	}
	
	public String getStreet() {
		return line[COLUMN_STREET];
	}
	
	public String getLocality() {
		return line[COLUMN_LOCALITY];
	}
	
	public String getTown() {
		return line[COLUMN_TOWN];
	}
	
	public String getDistrict() {
		return line[COLUMN_DISTRICT];
	}
	
	public String getCounty() {
		return line[COLUMN_COUNTY];
	}
	
	public String getTransactionCategory() {
		return line[COLUMN_TRANSACTION_CATEGORY];
	}
}
