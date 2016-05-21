package com.epimorphics.lr.data.ukhpi.verification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.data.ukhpi.UKHPICSVLine;

public class VerifyIndexRecordCountByRegionYearGenerator extends UKHPIVerificationQueryBase implements UKHPIVerificationQueryGenerator {

	private RegionYearCounts counts = new RegionYearCounts();
	
	VerifyIndexRecordCountByRegionYearGenerator(String outputDir, ErrorHandler errorHandler) {
		super(outputDir, "index-record-count-by-region-year", errorHandler);
	}
	
	@Override
	public void addLine(UKHPICSVLine line) {
		counts.increment(line.get(UKHPICSVLine.COLUMN_AREA_CODE), line.get(UKHPICSVLine.COLUMN_DATE).substring(6));
	}

	@Override
	public void generate() {
		StringBuffer sb = new StringBuffer();
		sb.append("  {\n");
		sb.append("    SELECT DISTINCT ?refRegion ?inScotland ?year ?expectedCount (COUNT(DISTINCT ?indexRecord) AS ?count) {\n");
		sb.append("      VALUES ( ?onsRegion ?year ?expectedCount) {\n");
		Iterator<RegionYearCount> iter = counts.getCountIterator();
		while (iter.hasNext()) {
	      RegionYearCount count = iter.next();
	      sb.append("        ( <http://statistics.data.gov.uk/id/statistical-geography/" + count.getRegion() + "> \"" + count.getYear() + "\" " + count.getCount() + " )\n" );
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
		sb.append("             .\n");
		sb.append("        FILTER (  STRSTARTS ( STR( ?refPeriod ), STR( ?year ) ) )\n");
        sb.append("      }\n");
		sb.append("    } GROUP BY ?refRegion ?inScotland ?year ?expectedCount\n");
		sb.append("  }\n");
		sb.append("\n");
		sb.append("  FILTER( ! ( ?inScotland && STR ( ?year ) < \"2004\" ) )\n");
		sb.append("  FILTER(?count != ?expectedCount)\n");
		sb.append("  BIND(?count AS ?item)\n");
		sb.append("  BIND( CONCAT( \"expected: \", STR( ?expectedCount ), \" region: \", STR( ?refRegion ), \" year: \", STR( ?year ), \" incorrect number of index records\" ) AS ?message)\n");
		writeQuery(sb.toString());
	}
	
	private static class RegionYearCount {
		String region;
		String year;
		Integer count;
		
		RegionYearCount(String region, String year, Integer count) {
			this.region = region;
			this.year = year;
			this.count = count;
		}
		
		String getRegion() { return region ; }
		String getYear() { return year ; }
		Integer getCount() { return count ; }
	}
	
	private static class RegionYearCounts {
		private Map<String, Map<String, Integer>> map = new HashMap<String, Map<String,Integer>>();
		
		void increment(String region, String year) {
			Map<String, Integer> regionMap = map.get(region);
			if (regionMap == null) {
				regionMap = new HashMap<String,Integer>();
				map.put(region, regionMap);
			}
			
			Integer count = regionMap.get(year);
			if ( count == null ) {
				regionMap.put(year, 1);
			} else {
				regionMap.put(year, ++count);
			}			
		}
		
		Iterator<RegionYearCount> getCountIterator() {
			return new RegionYearCountIterator();			
		}
		
		class RegionYearCountIterator implements Iterator<RegionYearCount> {
			
			Iterator<String> regionIterator;
			Iterator<String> yearIterator;
			String region;
			String year;
			
			RegionYearCountIterator() {
				regionIterator = map.keySet().iterator();
				region = regionIterator.next();
				yearIterator = map.get(region).keySet().iterator();
				year = yearIterator.next();
			}
			
			public boolean hasNext() { return year != null ; }
			
			public RegionYearCount next() {
				if ( ! hasNext() ) { return null ; }
				RegionYearCount result = new RegionYearCount(region, year, map.get(region).get(year));
				
				if (yearIterator.hasNext()) {
					year = yearIterator.next();
				} else {
					if ( regionIterator.hasNext()) {
						region = regionIterator.next();
						yearIterator = map.get(region).keySet().iterator();
						year = yearIterator.next();
					} else {
						year = null; // finished
					}
				}
				return result;
			}
		}
	}
}
