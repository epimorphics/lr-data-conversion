package com.epimorphics.lr.data.ppd;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.epimorphics.lr.data.ErrorHandler;
import com.epimorphics.lr.ontology.COMMON;
import com.epimorphics.lr.ontology.PPI;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Convert a single line of PPD CSV data and write as NQUADS
 * 
 * @author bwm
 *
 */
public class PPDCSVLineConverter {

	private static final String PPD_GRAPH_URI = "http://landregistry.data.gov.uk/PPD";
	private static final String PPI_BASE="http://landregistry.data.gov.uk/data/ppi/" ;
	private static final String TRANSACTION_BASE = PPI_BASE + "transaction/";
	private static final String ADDRESS_BASE = PPI_BASE + "address/";
	
	private static final Map<String,Resource> ESTATE_TYPE = new HashMap<String,Resource>();
	private static final Map<String,Resource> PROPERTY_TYPE = new HashMap<String,Resource>();
	private static final Map<String,Resource> TRANSACTION_CATEGORY = new HashMap<String,Resource>();
	static {
		
		ESTATE_TYPE.put("F", COMMON.freehold);
		ESTATE_TYPE.put("L", COMMON.leasehold);
		
		PROPERTY_TYPE.put("D", COMMON.detached);
		PROPERTY_TYPE.put("F", COMMON.flat_maisonette);
		PROPERTY_TYPE.put("O", COMMON.otherPropertyType);
		PROPERTY_TYPE.put("S", COMMON.semi_detached);
		PROPERTY_TYPE.put("T", COMMON.terraced);
		
		
		TRANSACTION_CATEGORY.put("A", PPI.standardPricePaidTransaction);
		TRANSACTION_CATEGORY.put("B", PPI.additionalPricePaidTransaction);
	}
	
	private String publishDate;
	private PPDCSVLine line;
	private ErrorHandler errorHandler;
	private PrintStream output;
	
	private String guid;
	private String transactionURI;
	private String transactionRecordURI;
	private String addressURI;
	
	public PPDCSVLineConverter(String publishDate, Set<String> transactionIDs, String[] line, PrintStream output, ErrorHandler errorHandler) {
		this.publishDate = publishDate;
		this.line = new PPDCSVLine(line);
		this.output = output;
		this.errorHandler = errorHandler;
		guid = this.line.getGUID();
		if (transactionIDs.contains(guid)) {
			errorHandler.reportError("Input CSV contains multiple records for guid: " + guid);
			throw new Error("Input CSV contains multiple records for guid");
		} else {
			transactionIDs.add(guid);
		}
		transactionURI = TRANSACTION_BASE + guid ;
		transactionRecordURI = transactionURI + "/current" ;
		addressURI = ADDRESS_BASE + PPDUtil.getAddressHash(this.line);
	}
	
	public void convertLine() {
		writeTransactionResource();
		writeTransactionRecordResource();
	}
	
	protected void writeTransactionResource() {
		writeQuad(transactionURI, RDF.type, uri(PPI.Transaction.getURI()));
		writeQuad(transactionURI, PPI.transactionId, literal(guid, PPI.TransactionIdDatatype));
		writeQuad(transactionURI, PPI.hasTransactionRecord, uri(transactionRecordURI));
	}
	
	protected void writeTransactionRecordResource() {
		writeQuad(transactionRecordURI, RDF.type, uri(PPI.TransactionRecord.getURI()));
		writeQuad(transactionRecordURI, PPI.recordStatus, uri(PPI.add));
		writeQuad(transactionRecordURI, PPI.transactionId, literal(guid, PPI.TransactionIdDatatype));
		writeQuad(transactionRecordURI, PPI.hasTransaction, uri(transactionURI));
		writeQuad(transactionRecordURI, PPI.transactionDate, literal(transactionDate(), XSD.date));
		writeQuad(transactionRecordURI, PPI.pricePaid, literal(line.getPricePaid(), XSD.integer));
		writeQuad(transactionRecordURI, PPI.propertyType, uri(propertyType()));
		writeQuad(transactionRecordURI, PPI.newBuild, literal(newBuild(), XSD.xboolean) );
		writeQuad(transactionRecordURI, PPI.estateType, uri(estateType() )) ;
		writeQuad(transactionRecordURI, PPI.transactionCategory, uri(transactionCategory()));
		writeQuad(transactionRecordURI, PPI.propertyAddress, uri(addressURI));
		if (publishDate != null) {
			writeQuad(transactionRecordURI, PPI.publishDate, literal(publishDate, XSD.date));
		}
		writeAddress();
	}
	
	protected void writeAddress() {
		writeQuad(addressURI, RDF.type, uri(COMMON.BS7666Address.getURI()));
		writeQuad(addressURI, COMMON.paon, literal(PPDUtil.substituteDefault(line.getPaon(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.saon, literal(PPDUtil.substituteDefault(line.getSaon(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.street, literal(PPDUtil.substituteDefault(line.getStreet(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.locality, literal(PPDUtil.substituteDefault(line.getLocality(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.town, literal(PPDUtil.substituteDefault(line.getTown(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.district, literal(PPDUtil.substituteDefault(line.getDistrict(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.county, literal(PPDUtil.substituteDefault(line.getCounty(), null), XSD.xstring));
		writeQuad(addressURI, COMMON.postcode, literal(PPDUtil.substituteDefault(line.getPostCode(), null), XSD.xstring));
	}
		
	protected String transactionDate() {
		return line.getTransactionDate().substring(0,10);		
	}
	
	protected String newBuild() {
		String val = line.getNewBuild();
		if (val.equals("N")) {
			return "false";
		} else if (val.equals("Y")) {
			return "true";
		} else {
			errorHandler.warn("illegal value in new build column '" + val + "\"");
			return null;
		}
		
	}
	
	protected Resource propertyType() {
		Resource result = PROPERTY_TYPE.get(line.getPropertyType());
		if (result == null) {
			errorHandler.warn("unknown property type: '" + line.getPropertyType() + "'");
		}
		return result;
	}

	protected Resource estateType() {
		Resource result = ESTATE_TYPE.get(line.getEstateType());
		if (result == null) {
			errorHandler.warn("unknown estate type: '" + line.getEstateType() + "'");
		}
		return result;
	}
	
	protected Resource transactionCategory() {
		Resource result = TRANSACTION_CATEGORY.get(line.getTransactionCategory());
		if (result == null) {
			errorHandler.warn("unknown transaction category: '" + line.getTransactionCategory() + "'");
		}
		return result;
	}
	
	
	
	
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	protected void writeQuad(String subjectURI, Property property, String object) {
		if (object != null) {
		    output.println(uri(subjectURI) +
	                   " " + uri(property.getURI()) +
				       " " + object +
				       " <" + PPD_GRAPH_URI + "> ." );
		}
	}
	
	protected String uri(String uri) {
		return "<" + uri + ">";
	}
	
	protected String uri(Resource resource) {
		if (resource == null) {
			return null;
		} else {
			return "<" + resource.getURI() + ">";
		}
	}
	
	protected String literal(String s) {
		if ( s == null) {
			return null;
		} else {
		  return "\"" + escapeDoubleQuotes(s) + "\"";
		}
	}
	
	protected String literal(String s, Resource datatype) {
		if ( s == null ) {
			return null;
		} else {
		  return "\"" + escapeDoubleQuotes(s) + "\"^^<" + datatype.getURI() + ">";
		}
	}
	
	protected String escapeDoubleQuotes(String s) {
		return s.replaceAll("[\"]", "\\\\\"");
	}
}
