package com.epimorphics.lr.data.ppd.verification;

import java.io.PrintWriter;

import com.epimorphics.lr.data.ErrorHandler;

public abstract class PPDVerificationQueryBase {
	
	private String outputDir;
	protected String queryName;
	ErrorHandler errorHandler;
	
	protected PPDVerificationQueryBase(String outputDir, String queryName, ErrorHandler errorHandler) {
		this.outputDir = outputDir;
		this.queryName = queryName;
		this.errorHandler = errorHandler;
	}
	
	protected void writeQuery(String query) {
		PrintWriter writer;
		String filePath = outputDir + "/" + queryName + ".rq";
		try {
			writer = new PrintWriter(filePath, "UTF-8");
		} catch (Exception e) {
			errorHandler.reportError("unable to open output file: " + filePath, e);
			throw new Error("fatal error");
		}
		writer.println("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		writer.println("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		writer.println("prefix owl: <http://www.w3.org/2002/07/owl#>");
		writer.println("prefix xsd: <http://www.w3.org/2001/XMLSchema#>");
		writer.println("prefix sr: <http://data.ordnancesurvey.co.uk/ontology/spatialrelations/>");
		writer.println("prefix lrhpi: <http://landregistry.data.gov.uk/def/hpi/>");
		writer.println("prefix lrppi: <http://landregistry.data.gov.uk/def/ppi/>");
		writer.println("prefix skos: <http://www.w3.org/2004/02/skos/core#>");
		writer.println("prefix lrcommon: <http://landregistry.data.gov.uk/def/common/>");
		writer.println("prefix text: <http://jena.apache.org/text#>");
		
		writer.println();
		writer.println("SELECT ?item ?message {");
		writer.println(query);
		writer.println("}");
		writer.close();
	}
}
