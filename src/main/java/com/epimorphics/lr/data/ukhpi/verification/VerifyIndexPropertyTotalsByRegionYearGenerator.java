package com.epimorphics.lr.data.ukhpi.verification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;

public class VerifyIndexPropertyTotalsByRegionYearGenerator extends UKHPIVerificationQueryBase implements UKHPIVerificationQueryGenerator {

	private RegionYearTotals totals = new RegionYearTotals();
	
	VerifyIndexPropertyTotalsByRegionYearGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "index-property-totals-by-region-year", errorHandler);
	}
	
	@Override
	public void addLine(UKHPICSVLine line) {
//	    if (!line.get(0).substring(6).equals("2015")) return;
//	    if (!line.get(1).equals("Southend-on-Sea")) return;
		Double propValue = line.getDouble(3);
		if (propValue != null) {
		  totals.add(line.get(UKHPICSVLine.COLUMN_AREA_CODE), line.get(UKHPICSVLine.COLUMN_DATE).substring(6), "averagePrice", line.getDouble(3));
		}
    }

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT DISTINCT ?indexProperty ?refRegion ?inScotland ?year ?expectedTotal (SUM(?value) AS ?total) {\n");
		sb.append("      VALUES ( ?onsRegion ?year ?indexProperty ?expectedTotal) {\n");
		Iterator<RegionYearTotal> iter = totals.getCountIterator();
		while (iter.hasNext()) {
	      RegionYearTotal total = iter.next();
	      sb.append("        ( <http://statistics.data.gov.uk/id/statistical-geography/" + total.getRegion() + "> \"" + total.getYear() + "\" <http://landregistry.data.gov.uk/def/ukhpi/" + total.getProperty() + "> " + total.getTotal() + " )\n" );
		}
		sb.append("      }\n");
		sb.append("      {\n");
		sb.append("        SELECT ?refRegion ?inScotland ?onsRegion {\n");
		sb.append("          ?refRegion rdfs:seeAlso ?onsRegion .\n");
        sb.append("          OPTIONAL {\n");
        sb.append("            FILTER EXISTS {\n");
        sb.append("              ?refRegion sr:within* <http://landregistry.data.gov.uk/id/region/scotland> .\n");
        sb.append("            }\n");
        sb.append("            BIND( true AS ?inScotland)\n");
        sb.append("          }\n");
        sb.append("        }\n");
        sb.append("      }\n");
		sb.append("      OPTIONAL {\n");
		sb.append("        ?indexRecord ukhpi:refRegion ?refRegion\n");
		sb.append("             ;       ukhpi:refPeriod ?refPeriod\n");
		sb.append("             ;       ?indexProperty  ?value\n");
		sb.append("             .\n");
		sb.append("        FILTER (  STRSTARTS ( STR( ?refPeriod ), STR( ?year ) ) )\n");
        sb.append("      }\n");
		sb.append("    } GROUP BY ?refRegion ?inScotland ?year ?expectedTotal ?indexProperty\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER( ! ( ?inScotland && STR ( ?year ) < \"2004\" ) )\n");
		sb.append("  FILTER( ABS(?total - ?expectedTotal) > 5 )\n");
		sb.append("  BIND(?total AS ?item)\n");
		sb.append("  BIND( CONCAT( \"property: \", STR(?indexProperty), \" expected: \", STR( ?expectedTotal ), \" region: \", STR( ?refRegion ), \" year: \", STR( ?year ), \" incorrect property total\" ) AS ?message)\n");
		writeQuery(sb.toString());
	}
	
	private static class RegionYearTotal {
		String region;
		String year;
		String property;
		Double total;
		
		RegionYearTotal(String region, String year, String property, Double total) {
			this.region = region;
			this.year = year;
			this.property = property;
			this.total = total;
		}
		
		String getRegion() { return region ; }
		String getYear() { return year ; }
		String getProperty() { return property ; }
		Double getTotal() { return total ; }
	}
	
	private static class RegionYearTotals {
		private Map<String, Map<String, Map<String,Double>>> regionMap = new HashMap<String, Map<String,Map<String,Double>>>();
		
		void add(String region, String year, String property, double value) {
			
			Map<String,Map<String,Double>> yearMap = regionMap.get(region);
			if ( yearMap == null) {
				yearMap = new HashMap<String,Map<String,Double>>();
				regionMap.put(region, yearMap );
			}
			
			Map<String, Double> propertyMap = yearMap.get(year);
			if (propertyMap == null) {
				propertyMap = new HashMap<String, Double>();
				yearMap.put(year, propertyMap);
			}			
			
			Double total = propertyMap.get(property);
			if ( total == null ) {
				propertyMap.put(property, value );
			} else {
				propertyMap.put(property, total + value);
			}	
		}
		
		Iterator<RegionYearTotal> getCountIterator() {
			return new RegionYearTotalIterator();			
		}
		
		class RegionYearTotalIterator implements Iterator<RegionYearTotal> {
			
			Iterator<String> regionIterator;
			Iterator<String> yearIterator;
			Iterator<String> propertyIterator;
			String region;
			String year;
			String property;
			
			RegionYearTotalIterator() {
				regionIterator = regionMap.keySet().iterator();
				region = regionIterator.next();
				yearIterator = regionMap.get(region).keySet().iterator();
				year = yearIterator.next();
				propertyIterator = regionMap.get(region).get(year).keySet().iterator();
				property = propertyIterator.next();
			}
			
			public boolean hasNext() { return property != null ; }
			
			public RegionYearTotal next() {
				if ( ! hasNext() ) { return null ; }
				RegionYearTotal result = new RegionYearTotal(region, year, property, regionMap.get(region).get(year).get(property));
				
				if (propertyIterator.hasNext()) {
					property = propertyIterator.next();
				} else {
				  if (yearIterator.hasNext()) {
					year = yearIterator.next();
					propertyIterator = regionMap.get(region).get(year).keySet().iterator();
					property = propertyIterator.next();
				  } else {
					if ( regionIterator.hasNext()) {
						region = regionIterator.next();
						yearIterator = regionMap.get(region).keySet().iterator();
						year = yearIterator.next();
						propertyIterator = regionMap.get(region).get(year).keySet().iterator();
						property = propertyIterator.next();
					} else {
						property = null; // finished
					}
				  }
				}
				return result;
			}
		}
	}
}
