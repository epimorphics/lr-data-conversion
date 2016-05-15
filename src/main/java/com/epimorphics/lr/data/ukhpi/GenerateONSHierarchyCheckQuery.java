package com.epimorphics.lr.data.ukhpi;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.epimorphics.lr.data.ErrorHandler;

import au.com.bytecode.opencsv.CSVReader;
/**
 * Main program to generate a query to verify the triple store
 * region hierarchy is consistent with the ONS region hierarchy.
 * 
 * Reads from a CSV file and writes to stdout.
 * 
 * @author bwm
 *
 */
public class GenerateONSHierarchyCheckQuery {

	@Option(name="--input", usage="path to input file")
	private String inputPath = "data/input/ons-region-hierarchy-2016-05-05.csv";
	private static ErrorHandler errorHandler = new ErrorHandler();
	
	private Set<ParentChild> parentChildLinks = new HashSet<ParentChild>();
	private Set<String> regions = new HashSet<String>();
		
	public static void main(String[] args) {
		try {			
		    GenerateONSHierarchyCheckQuery instance = new GenerateONSHierarchyCheckQuery() ;
		    instance.parseArgs(args).execute();
		} catch( Throwable t ) {
            errorHandler.reportError("query generation failed");
        }		
	}
	
	private GenerateONSHierarchyCheckQuery parseArgs(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
			System.err.println();
			throw e;
		}
		return this;
	}
	
	private void execute() {
		Reader input = getInputReader(inputPath);
		readONSRegionHierarchy(input);
		writeQuery(System.out);
	}
	
	private void readONSRegionHierarchy(Reader input) {
		CSVReader reader = new CSVReader(input);
		String [] line;
		try {
			line = reader.readNext(); // skip column headers
	        while ((line = reader.readNext()) != null) {
	        	int childColumn = 2;
	        	while (childColumn < 8) {
	        	  int parentColumn = getParentColumn(line, childColumn);
	        	  if (parentColumn < 0) {
	        		  throw new Error("can't find parent column: " + line[0]);
	        	  }
	        	  addlink(line[childColumn], line[parentColumn], line[parentColumn + 1]);
	              childColumn = parentColumn;
	        	}
	        }
			reader.close();
		} catch (IOException e) {
			errorHandler.reportError("Error reading CSV file", e);
			throw new Error("fatal error", e);
		}
	}
	
	// find the next non-empy code column after the specified column
	private int getParentColumn(String[] line, int column) {
		if (line[column+2].length() > 1) { return column+2 ; }
		else if (line[column+4].length() > 1) { return column + 4; }
		else if (line[column+6].length() > 1) { return column + 6; }
		else return -1;
	}
	
	private void addlink(String child, String parent, String parentName) {
		child = child.trim();
		parent = parent.trim();
		if ( child != null && child.length() > 0 
		 &&  parent != null && parent.length() > 0 ) {
			parentChildLinks.add(new ParentChild(parentName, parent, child));			
			regions.add(parent);
			regions.add(child);
		} else {
			// debugging info
			System.err.println("'" + parent + "' '" + child + "'");
		}
		
	}
	
	private static final String prefixes =
			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +	        
            "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
			"prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
            "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "prefix sr: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>\n" +
            "prefix lrukhpi: <http://landregistry.data.gov.uk/def/ukhpi/>\n" +
            "prefix lrppi: <http://landregistry.data.gov.uk/def/ppi/>\n" +
            "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n" +
            "prefix lrcommon: <http://landregistry.data.gov.uk/def/common/>\n";
	
	private void writeQuery(PrintStream os) {
		os.println(prefixes);
		
		os.println("SELECT DISTINCT ?item ?message {");
		os.println();
		os.println("{");
		os.println("    VALUES(?onsParentName ?onsParent ?onsChild) {");
		
		for (ParentChild pc : parentChildLinks) {
			os.println("      (\"" + pc.getParentName() + "\" <http://statistics.data.gov.uk/id/statistical-geography/" + pc.getParent() + "> <http://statistics.data.gov.uk/id/statistical-geography/" + pc.getChild() + "> )");
		}
				
		os.println("    }");
		os.println();
		
		os.println("    ?lrParent owl:sameAs ?onsParent .");
		os.println("    ?lrChild  owl:sameAs ?onsChild  .");
		os.println("    FILTER NOT EXISTS { ?lrChild  sr:within ?lrParent }");
		os.println();
		os.println("    BIND(?lrChild AS ?item)");
		os.println("    BIND(CONCAT(\"should have parent \", STR(?onsParentName)) AS ?message)");
        os.println("} UNION {");
        os.println("    {");
        os.println("      SELECT (COUNT(DISTINCT ?lrRegion) AS ?lrRegionCount) {");
        os.println("        ?s lrukhpi:refRegion ?lrRegion");
        os.println("      }");
        os.println("    }");
        os.println("    FILTER( ?lrRegionCount != " + regions.size() + ")");
        os.println("    BIND(?lrRegionCount AS ?item)");
        os.println("    BIND(\"there should be data for " + regions.size() + " regions\" AS ?message)");
        os.println("  }");
        
        os.println();
		os.println("} ORDER BY STR(?item)");		
	}
	
	class ParentChild {
		private String parentName;
		private String parent;
		private String child;
		
		ParentChild(String parentName, String parent, String child) {
			this.parentName = parentName;
			this.parent = parent;
			this.child = child;
		}
		
		String getParentName() { return parentName; }
		String getParent()     { return parent; }
		String getChild()      { return child; }
		
		public boolean equals(Object other) {
			if (other == null) { return false; }
			if ( ! (other instanceof ParentChild)) { return false ; }
			return ((ParentChild) other).parent.equals(parent) 
			   &&  ((ParentChild) other).child.equals(child);			
		}
		
		public int hashCode() {
			return parent.hashCode() ^ child.hashCode();
		}
	}
	
	Reader getInputReader(String path) {
		try {
			return new BufferedReader(
					new InputStreamReader(new FileInputStream(path), "UTF-8")
		          );
		} catch (Exception e) {
			errorHandler.reportError("failed to open input file: " + path, e);
			throw new RuntimeException();
		}		
	}

}
