package com.epimorphics.lr.data.ppd.verification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ppd.PPDCSVLine;

/**
 * Generate a verification query to check that a sample of transactions are
 * indexed in the text index.
 * 
 * <p>
 * Hold on to upto 1000 lines of input data for each year month for which there is 
 * data.  For each one of these select 1 of these to check against the index.
 * <p>
 * @author bwm
 *
 */
public class VerifyTextIndexSampleGenerator extends PPDVerificationQueryBase implements PPDVerificationQueryGenerator {
	
	YearMonthToSampleMap map = new YearMonthToSampleMap();
	private String field;
	private int fieldIndex;
	
	VerifyTextIndexSampleGenerator(String outputDir, String field, int fieldIndex, ErrorHandler errorHandler) {
		super(outputDir, "text-index-sample-" + field, errorHandler);
		this.field = field;
		this.fieldIndex = fieldIndex;
	}
	
	@Override
	public void addLine(PPDCSVLine line) {
		if ("Y".equals(line.getNewBuild())) {;
		    map.add(line);
		}
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  VALUES ( ?transRec ?luceneQuery ?yearMonth ) {\n");
		for (String yearMonth : map.getKeySet()) {
			PPDCSVLine line = map.getSample(yearMonth);
			addTestOneMonth(sb, line, yearMonth);
		}
		sb.append("  }\n");

		sb.append("  ?transRec lrppi:propertyAddress ?addr .\n");
		// better to use FILTER NOT EXISTS here
		// but that is not working reliably
		sb.append("  OPTIONAL {\n");
		sb.append("    ?addr2 text:query ( ?luceneQuery 100000000 ) .\n");
		sb.append("    FILTER(?addr = ?addr2)\n");
		sb.append("  }\n");
		sb.append("  FILTER(!BOUND(?addr2))\n");
		sb.append("  BIND(?transRec AS ?item)\n");
		sb.append("  BIND(CONCAT(?luceneQuery, ' : text search does not return item') AS ?message)\n");
		writeQuery(sb.toString());
	}
	
	private void addTestOneMonth(StringBuffer sb, PPDCSVLine line, String yearMonth) {
		String postcode = line.getPostCode();
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "paon", line.getPaon()), yearMonth );
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "saon", line.getSaon()), yearMonth);
        if (fieldIndex == 0) {
		    addOneIndexCheck(sb, line.getGUID(), luceneQuery("postcode", postcode), yearMonth);
        } else {
		    addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, field, line.get(fieldIndex)), yearMonth);
        }
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "town", line.getTown()), yearMonth);
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "locality", line.getLocality()), yearMonth);
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "district", line.getDistrict()), yearMonth);
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery(postcode, "county", line.getCounty()), yearMonth);
//		addOneIndexCheck(sb, line.getGUID(), luceneQuery("postcode", line.getPostCode()), yearMonth );
	}
		
    private void addOneIndexCheck(StringBuffer sb, String guid, String luceneQuery, String yearMonth) {
      if (luceneQuery == null) {
    	  return;
      }
	  sb.append("    (\n");
	  sb.append("      <http://landregistry.data.gov.uk/data/ppi/transaction/" + guid + "/current>\n");
	  sb.append("      '" + luceneQuery + "'\n");
	  sb.append("      '" + yearMonth + "'\n");
	  sb.append("    )\n");
	}
	
	private String luceneQuery(String field, String value) {
		if (value == null || value.trim().length() == 0 ) {
			return null;
		}
		return (field + ":" + luceneTokenQuery(value));
	}

	private String luceneQuery(String postcode, String field, String value) {
		if (value == null || value.trim().length() == 0 ) {
			return null;
		}
		return ("( postcode:" + luceneTokenQuery(postcode) + " AND " + field + ":" + luceneTokenQuery(value) + " )");
	}
	
	private String luceneTokenQuery(String value) {
		String[] parts = value.replaceAll("'", "\\'").split("\\s+");
		if (parts.length == 2) {
		  return "( " + parts[0] + " AND " + parts[1] + " )";		
		} else {
			return "( " + parts[0] + " )";
		}
	}
	
	class YearMonthToSampleMap {

		private final Random random = new Random();
		private final Map<String, Pair> map = new HashMap<String, Pair>();
		
		void add(PPDCSVLine line) {
			// must have a postcode to be included in the map
			String postcode = line.getPostCode();
			if (postcode == null || postcode.trim().length() == 0) {
				return;  
			}
			String yearMonth = line.getTransactionDate().substring(0,7);
			Pair pair = map.get(yearMonth);
			if (pair == null) {
				map.put(yearMonth, new Pair(1,line));
				return;
			}
			
			pair.incrementCount();
			if (random.nextDouble() < 1.0D/pair.getCount()) {
				pair.setLine(line);
			}
		}
		
		Set<String> getKeySet() {
			return map.keySet();
		}			
		
		PPDCSVLine getSample(String yearMonth) {
			return map.get(yearMonth).getLine();
		}
		
		class Pair {
			int count;
			PPDCSVLine line;
			
			Pair(int count, PPDCSVLine line) {
				this.count = count;
				this.line = line;
			}
			
			void incrementCount() {
				count++;
			}
			
			void setLine(PPDCSVLine line) {
				this.line = line;
			}
			
			int getCount() { return count ; }
			PPDCSVLine getLine() { return line ; }			
		}
	}
}
