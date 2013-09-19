package us.kbase.networks.adaptor.ppi.importer;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.strbio.util.*;
import org.strbio.net.PubMed;
import org.strbio.IO;
import org.strbio.io.*;
import us.kbase.networks.adaptor.ppi.local.PPI;

/**
   Import PPI data from a PSI-MI TAB 2.7 file, with KBase format
   modifications described below:

   1) Unique identifiers for Interactors A and B are KBase Feature ids
      [planned for future but not yet working: or other ids that are
      convertible to Feature IDs by a KBase service]

   2) When the complex expansion method (the 16th column in the file)
      is "spoke expansion" everything about Interactor B is ignored,
      and the data are assumed to refer to a multi-protein complex
      described by multiple lines listing data for Interactor A.

   3) Several pieces of optional metadata are encoded in the "Xref for
      Interactor A" field (the 23rd column in the file):  "dataset:"
      refers to InteractionDataset.description, "dataseturl:" refers to the
      InteractionDataset.url, and "url:" refers to Interaction.url.

   4) We have extended the PSI ontology for interaction detection methods
      (column 7) to include "kb:" methods, which refer to
      InteractionDetectionType.description.  If one if these methods
      is listed, the psi-mi: method is ignored.  If there is no "kb:"
      method, the text of the "psi-mi:" ontology is used instead.

   5) We have extended the PSI ontology for source database
      (column 13) to include "kb:" descriptions, which refer to
      InteractionDataset.data-source.  If one if these methods
      is listed, the psi-mi: method is ignored.  If there is no "kb:"
      method, the text of the "psi-mi:" ontology is used instead.

  @version 3.02, 9/18/13
  @author JMC
*/
public class ImportPSIMI {
    // cache sets of what's already stored in tables
    static HashSet <Integer> pubSet = new HashSet<Integer>();
    static HashSet <String> dsGenomeSet = new HashSet<String>();
    static HashSet <String> genomeSet = new HashSet<String>();
    static HashSet <String> featureSet = new HashSet<String>();
    
    // cache DB lookups:
    static HashMap <String,String> dsMap = new HashMap<String,String>();
    static HashMap <String,String> methodMap = new HashMap<String,String>();
    static HashMap <String,String> intMap = new HashMap<String,String>();
    static HashMap <String,String> proteinMap = new HashMap<String,String>();

    // cache max current assigned ids for each prefix type
    static HashMap <String,Integer> maxID = new HashMap<String,Integer>();

    final public static SimpleDateFormat medlineDateFormat1 =
	new SimpleDateFormat ("yyyy MMM d");
    final public static SimpleDateFormat medlineDateFormat2 =
	new SimpleDateFormat ("yyyy MMM");
    final public static SimpleDateFormat medlineDateFormat3 =
	new SimpleDateFormat ("yyyy");

    /**
       find max ids already in table
    */
    final public static int getMaxID(String prefix,
				     String tableName) throws Exception {
	PPI.connectRW();
	Connection con = PPI.getConnection();
	Statement stmt = PPI.createStatement(con);
	int prefixLength = prefix.length()+5; // for 'kb|' and dots
	ResultSet rs = stmt.executeQuery("select max(convert(substr(id,"+prefixLength+"),unsigned integer)) from "+tableName);
	int rv = 0;
	if (rs.next())
	    rv = rs.getInt(1);
	rs.close();
	stmt.close();
	con.close();
	return rv;
    }

    /**
       find max ids already in tables for all PPI datatypes
    */
    final public static void getMaxIDs() throws Exception {
	int id = getMaxID("ppi","Interaction");
	maxID.put("ppi",new Integer(id));
	id = getMaxID("ppid","InteractionDataset");
	maxID.put("ppid",new Integer(id));
	id = getMaxID("ppim","InteractionDetectionType");
	maxID.put("ppim",new Integer(id));
    }

    /**
       assign next ID for a given prefix, and increment the
       cached max ID
    */
    final public static String getNextID(String prefix) throws Exception {
	Integer id = maxID.get(prefix);
	id++;
	maxID.put(prefix,id);
	return("kb|"+prefix+"."+id);
    }

    /**
       read multi-line text data from a MEDLINE text format record.
    */
    final public static String readPubmedTag(BufferedReader infile) throws IOException
    {
	String buffer = infile.readLine();
	String rv = buffer.substring(6);
	do {
	    infile.mark(16384);
	    buffer=infile.readLine();
	    if ((buffer==null) ||
		(buffer.indexOf("      ")!=0)) {
		infile.reset();
	    }
	    else {
		rv += " "+buffer.substring(6);
	    }
	} while ((buffer!=null) &&
		 (buffer.indexOf("      ")==0));
	if (rv.length() == 0) rv = null;
	return rv;
    }

    /**
       Parse publication data in MEDLINE format returned by
       NCBI PubMed server.
    */
    final public static String[] parsePubmedData(BufferedReader infile)
	throws IOException {
	String[] rv = new String[2]; // title, publication date
	
	String buffer=infile.readLine();
	do {
	    if (buffer.indexOf("TI  - ")==0) {
		infile.reset();
		rv[0] = readPubmedTag(infile);
	    }
	    else if (buffer.indexOf("DP  - ")==0) {
		rv[1] = buffer.substring(6);
	    }
	    infile.mark(16384);
	    buffer=infile.readLine();
	    if (buffer==null) {
		infile.reset();
		return rv;
	    }
	} while (true);
    }

    /**
       retrieve PubMed data required to populate Publication record
       from NCBI server
    */
    final public static String[] getPubmedData(int pubmedID) throws Exception {
	String[] rv = new String[2];
	
	String data = PubMed.search(pubmedID,null);
	String[] relevantFields = parsePubmedData(new BufferedReader(new EOFStringReader(data)));

	if ((relevantFields[0]==null) ||
	    (relevantFields[1]==null))
	    throw new Exception("Couldn't get data for "+pubmedID);
	
	java.util.Date d = null;
	try {
	    d = medlineDateFormat1.parse(relevantFields[1]);
	}
	catch (Exception e) {
	    try {
		d = medlineDateFormat2.parse(relevantFields[1]);
	    }
	    catch (Exception e2) {
		d = medlineDateFormat3.parse(relevantFields[1]);
	    }
	}
	rv[0] = relevantFields[0];
	// assume bigint pubdate in Publication table is Unix timestamp
	rv[1] = Long.toString(d.getTime()/1000);
	return rv;
    }

    /**
       check whether we have a valid genome
    */
    final public static boolean isValidGenome(String genomeID) throws Exception {
	boolean rv = true;
	if (!genomeSet.contains(genomeID)) {
	    PPI.connectRW();
	    Connection con = PPI.getConnection();
	    PreparedStatement stmt = PPI.prepareStatement(con,
							  "select id from Genome where id=?");
	    stmt.setString(1,genomeID);
	    ResultSet rs = stmt.executeQuery();
	    if (!rs.next())
		rv = false;
	    rs.close();
	    stmt.close();
	    con.close();
	    genomeSet.add(genomeID);
	}
	return rv;
    }
    
    /**
       check whether we have a valid feature
    */
    final public static boolean isValidFeature(String featureID) throws Exception {
	boolean rv = true;
	if (!featureSet.contains(featureID)) {
	    PPI.connectRW();
	    Connection con = PPI.getConnection();
	    PreparedStatement stmt = PPI.prepareStatement(con,
							  "select id from Feature where id=?");
	    stmt.setString(1,featureID);
	    ResultSet rs = stmt.executeQuery();
	    if (!rs.next())
		rv = false;
	    rs.close();
	    stmt.close();
	    con.close();
	    featureSet.add(featureID);
	}
	return rv;
    }
    
    /**
       get protein for feature
    */
    final public static String getProtein(String featureID) throws Exception {
	boolean rv = true;

	String proteinID = proteinMap.get(featureID);
	if (proteinID != null)
	    return proteinID;

	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "select from_link from IsProteinFor where to_link=? limit 1");
	stmt.setString(1,featureID);
	ResultSet rs = stmt.executeQuery();
	if (rs.next())
	    proteinID = rs.getString(1);
	rs.close();
	stmt.close();
	con.close();
	proteinMap.put(featureID,proteinID);
	return proteinID;
    }
    
    /**
       Looks up a publication by its pubmed id, or creates one if it
       doesn't already exist in the Publication table.
    */
    final public static String lookupOrCreatePublication(int pubmedID) throws Exception {
	Integer intID = new Integer(pubmedID);
	String strID = intID.toString();
	
	if (pubSet.contains(intID))
	    return new String(strID);
	
	PPI.connectRW();
	Connection con = PPI.getConnection();
	Statement stmt = PPI.createStatement(con);
	ResultSet rs = stmt.executeQuery("select id from Publication where id=\""+pubmedID+"\"");
	if (rs.next()) {
	    pubSet.add(intID);
	    rs.close();
	    stmt.close();
	    con.close();
	    return strID;
	}
	rs.close();
	stmt.close();
	String[] pubmedData = getPubmedData(pubmedID);
	

	PreparedStatement stmt2 = PPI.prepareStatement(con,
						       "insert into Publication values (?, ?, ?, ?)");	
	stmt2.setString(1,strID);
	stmt2.setLong(2,StringUtil.atol(pubmedData[1]));	
	stmt2.setString(3,"http://www.ncbi.nlm.nih.gov/pubmed/"+strID);
	stmt2.setString(4,pubmedData[0]);
	stmt2.executeUpdate();
	stmt2.close();
	con.close();
	
	pubSet.add(intID);
	return strID;
    }

    /**
       Look up an interaction dataset by its description, or create
       one if it doesn't already exist.  Returns dataset id.
    */
    final public static String lookupOrCreateDataset(String description) throws Exception {
	String kbID = dsMap.get(description);
	if (kbID != null)
	    return kbID;
	
	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "select id from InteractionDataset where description=?");
	stmt.setString(1,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    String rv = rs.getString(1);
	    rs.close();
	    stmt.close();
	    con.close();
	    dsMap.put(description, rv);
	    return rv;
	}
	rs.close();
	stmt.close();
	stmt = PPI.prepareStatement(con,
				    "insert into InteractionDataset values (?, ?, ?, ?)");
	String rv = getNextID("ppid");
	stmt.setString(1,rv);
	stmt.setString(2,"");
	stmt.setString(3,description);
	stmt.setString(4,"");
	stmt.executeUpdate();
	stmt.close();
	con.close();
	dsMap.put(description, rv);
	return rv;
    }

    /**
       Look up an interaction detection type by its description, or return
       null if it doesn't exist.
    */
    final public static String lookupMethod(String description) throws Exception {
	if (description==null)
	    return null;

	String kbID = methodMap.get(description);
	if (kbID != null)
	    return kbID;
	
	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "select id from InteractionDetectionType where description=?");
	stmt.setString(1,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    String rv = rs.getString(1);
	    rs.close();
	    stmt.close();
	    con.close();
	    methodMap.put(description, rv);
	    return rv;
	}
	rs.close();
	stmt.close();
	con.close();
	return null;
    }
    
    /**
       Create an interaction detection type using its description, and
       return its id.
    */
    final public static String createMethod(String description) throws Exception {
	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "insert into InteractionDetectionType values (?, ?)");
	String rv = getNextID("ppim");
	stmt.setString(1,rv);
	stmt.setString(2,description);
	stmt.executeUpdate();
	stmt.close();
	con.close();
	methodMap.put(description, rv);
	return rv;
    }

    /**
       Look up an interaction by its description and dataset, or create
       one if it doesn't already exist
    */
    final public static String lookupOrCreateInteraction(String datasetID,
							 String description) throws Exception {
	String kbID = intMap.get(datasetID+"_"+description);
	if (kbID != null)
	    return kbID;
	
	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "select i.id from Interaction i, IsGroupingOf m where i.id=m.to_link and m.from_link=? and description=?");
	stmt.setString(1,datasetID);
	stmt.setString(2,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    String rv = rs.getString(1);
	    rs.close();
	    stmt.close();
	    con.close();
	    intMap.put(datasetID+"_"+description, rv);
	    return rv;
	}
	rs.close();
	stmt.close();
	stmt = PPI.prepareStatement(con,
				    "insert into Interaction values (?, ?, ?, ?, ?)");
	String rv = getNextID("ppi");
	stmt.setString(1,rv);
	stmt.setDouble(2,0.0);
	stmt.setInt(3,0);
	stmt.setString(4,description);
	stmt.setString(5,"");
	stmt.executeUpdate();
	stmt.close();
	con.close();
	intMap.put(datasetID+"_"+description, rv);
	return rv;
    }

    /**
       Get the genome id corresponding to a feature id, or
       null if error
    */
    final public static String getGenomeFor(String featureID) throws Exception {
	int pos = featureID.indexOf('.');
	int pos2 = featureID.indexOf('.',pos+1);

	if ((pos > 0) && (pos2 > pos))
	    return featureID.substring(0,pos2);

	// otherwise, do slow sql lookup
	PPI.connectRW();
	Connection con = PPI.getConnection();
	PreparedStatement stmt = PPI.prepareStatement(con,
						      "select from_link from IsOwnerOf where to_link=? limit 1");
	stmt.setString(1,featureID);
	ResultSet rs = stmt.executeQuery();
	String rv = null;
	if (rs.next())
	    rv = rs.getString(1);
	rs.close();
	stmt.close();
	con.close();
	return rv;
    }

    /**
       parses a field where we've modified the PSI-MI format to
       allow kb: extensions.  Returns the kb: description as first
       priority, the text part of the psi-mi annotation if present,
       or the whole string otherwise.  Returns null if the description
       is '-' or the original field was null.
    */
    final public static String parseKBField(String orig) {
	if (orig==null)
	    return null;

	String rv = new String(orig);
	
	// look for kb: field; otherwise use text of psi: field
	int pos = rv.indexOf("kb:");
	if (pos > -1) {
	    int pos2 = rv.indexOf("|",pos);
	    if (pos2 > -1)
		rv = rv.substring(pos+3,pos2);
	    else
		rv = rv.substring(pos+3);
	}
	else {
	    pos = rv.indexOf("(");
	    int pos2 = rv.indexOf(")",pos+1);
	    if ((pos > -1) && (pos2 > -1))
		rv = rv.substring(pos+1,pos2);
	}

	rv = rv.trim();
	if (rv.equals("-"))
	    return null;
	
	return rv;
    }

    /**
       parse crossreferences field into key/value pairs
    */
    final public static HashMap<String,String> parseXrefs(String xrefString) {
	HashMap<String,String> rv = new HashMap<String,String>();
	String[] xrefs = xrefString.split("\\|");
	for (String xref : xrefs) {
	    int pos = xref.indexOf(":");
	    if (pos > -1) {
		String key = xref.substring(0,pos).trim();
		String val = xref.substring(pos+1).trim();
		int l = val.length();
		if ((l > 2) &&
		    (val.charAt(0)=='"') &&
		    (val.charAt(l-1)=='"'))
		    val = val.substring(1,l-1);
		rv.put(key,val);
	    }
	}
	return rv;
    }
    
    final public static void main(String argv[]) {
	try {
	    PPI.connectRW();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    PreparedStatement stmt2;

	    // set up local PPI ID generator cache
	    getMaxIDs();

	    // keep track of what datasets we've seen in this file
	    HashSet<String> seenDatasets = new HashSet<String>();

	    // map datasets to url and source
	    HashMap<String,String> datasetURLMap = new HashMap<String,String>();
	    HashMap<String,String> datasetSourceMap = new HashMap<String,String>();

	    // map interactions to metadata
	    HashSet<String> directionalInteractions = new HashSet<String>();
	    HashMap<String,Double> interactionConfidence = new HashMap<String,Double>();
	    HashMap<String,String> interactionMethod = new HashMap<String,String>();
	    HashMap<String,String> interactionURL = new HashMap<String,String>();
	    HashMap<String,String> interactionPublication = new HashMap<String,String>();
	    HashMap<String,Integer> interactionRank = new HashMap<String,Integer>();

	    // dataset name
	    String defaultDatasetName = null;
	    if (argv.length > 1)
		defaultDatasetName = argv[1];

	    // read in file line by line
	    BufferedReader infile = IO.openReader(argv[0]);
	    String buffer;
	    while ((buffer=infile.readLine()) != null) {
		// System.out.println(buffer);
		// System.out.flush();
		
		StringTokenizer st = new StringTokenizer(buffer,"\t");

		// feature ids
		String featureID1 = st.nextToken();
		String featureID2 = st.nextToken();

		// check that they're valid
		if (!isValidFeature(featureID1))
		    throw new Exception ("KBase doesn't have feature "+featureID1);
		if (!isValidFeature(featureID2))
		    throw new Exception ("KBase doesn't have feature "+featureID2);

		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();

		// experiment/annotation method
		String method = parseKBField(st.nextToken());
		String methodID = null;
		if (method != null) {
		    methodID = lookupMethod(method);
		    if (methodID==null) {
			// throw new Exception("Must create method '"+method+"'");
			methodID = createMethod(method);
		    }
		}
		
		st.nextToken();

		// publication
		String publication = st.nextToken();
		String publicationID = null;
		int pos = publication.indexOf("pubmed:");
		if (pos > -1) {
		    int pubmedID = StringUtil.atoi(publication,pos+7);
		    publicationID = lookupOrCreatePublication(pubmedID);
		}
		
		st.nextToken();
		st.nextToken();

		// must be association data
		String interactionType = st.nextToken();
		if (!interactionType.equals("psi-mi:\"MI:0915\"(physical association)"))
		    throw new Exception("Format error: 12th field in '"+buffer+"' should be psi-mi:\"MI:0915\"(physical association)");

		// external source database
		String sourceDB = parseKBField(st.nextToken());

		// complex description
		String interaction = st.nextToken();

		// confidence
		String confidenceField = st.nextToken();
		double confidence = Double.NaN;
		pos = confidenceField.indexOf(":",pos);
		if ((pos > -1) &&
		    (Character.isDigit(confidenceField.charAt(pos+1))))
		    confidence = StringUtil.atod(confidenceField,pos+1);

		String complexExpansion = st.nextToken();
		boolean isSpoke = false;
		if (complexExpansion.equals("psi-mi:\"MI:1060\"(spoke expansion)"))
		    isSpoke = true;

		st.nextToken();
		st.nextToken();

		// experimental roles
		String expRole1 = st.nextToken();
		String expRole2 = st.nextToken();
		boolean isDirectional = false;
		boolean reverseIDs = false;
		if ((expRole1.equals("psi-mi:\"MI:0496\"(bait)")) &&
		    (expRole2.equals("psi-mi:\"MI:0498\"(prey)")))
		    isDirectional = true;
		else if ((expRole2.equals("psi-mi:\"MI:0496\"(bait)")) &&
			 (expRole1.equals("psi-mi:\"MI:0498\"(prey)"))) {
		    isDirectional = true;
		    reverseIDs = true;
		}

		st.nextToken();
		st.nextToken();

		// xrefs
		HashMap<String,String> xrefs1 = parseXrefs(st.nextToken());
		HashMap<String,String> xrefs2 = parseXrefs(st.nextToken());
		
		// look for special cases
		String datasetName = xrefs1.remove("dataset");
		if (datasetName == null)
		    datasetName = xrefs2.remove("dataset");
		else
		    xrefs2.remove("dataset");
		String datasetURL = xrefs1.remove("dataseturl");
		if (datasetURL == null)
		    datasetURL = xrefs2.remove("dataseturl");
		else
		    xrefs2.remove("dataseturl");
		String url = xrefs1.remove("url");
		if (url == null)
		    url = xrefs2.remove("url");
		else
		    xrefs2.remove("url");
		
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();

		// stoichiometry
		int stoich1 = StringUtil.atoi(st.nextToken());
		int stoich2 = StringUtil.atoi(st.nextToken());

		st.nextToken();
		st.nextToken();

		// insert data in this row
		if (datasetName==null)
		    datasetName = defaultDatasetName;
			
		if (datasetName==null)
		    throw new Exception("Need dataset name for row '"+buffer+"'");
		String datasetID = lookupOrCreateDataset(datasetName);

		// if this is the first time seeing this dataset
		// in this file, clear out any old data
		if (!seenDatasets.contains(datasetName)) {
		    seenDatasets.add(datasetName);
		    stmt2 = PPI.prepareStatement(con,
						 "delete from IsDatasetFor where from_link=?");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    
		    stmt2 = PPI.prepareStatement(con,
						 "delete dwm from DetectedWithMethod dwm join Interaction i on (i.id=dwm.to_link) join IsGroupingOf igo on (i.id=igo.to_link and igo.from_link=?)");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    stmt2 = PPI.prepareStatement(con,
						 "delete pi from PublishedInteraction pi join Interaction i on (i.id=pi.to_link) join IsGroupingOf igo on (i.id=igo.to_link and igo.from_link=?)");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    stmt2 = PPI.prepareStatement(con,
						 "delete ip from InteractionFeature ip join Interaction i on (i.id=ip.from_link) join IsGroupingOf igo on (i.id=igo.to_link and igo.from_link=?)");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    stmt2 = PPI.prepareStatement(con,
						 "delete i from Interaction i join IsGroupingOf igo on (i.id=igo.to_link and igo.from_link=?)");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    stmt2 = PPI.prepareStatement(con,
						 "delete from IsGroupingOf where from_link=?");
		    stmt2.setString(1,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}

		// update URL, source if different
		if ((datasetURL != null) &&
		    (!datasetURL.equals(datasetURLMap.get(datasetName)))) {
		    datasetURLMap.put(datasetName, datasetURL);
		    stmt2 = PPI.prepareStatement(con,
						 "update InteractionDataset set url=? where id=?");
		    stmt2.setString(1,datasetURL);
		    stmt2.setString(2,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}
		if ((sourceDB != null) &&
		    (!sourceDB.equals(datasetSourceMap.get(datasetName)))) {
		    datasetSourceMap.put(datasetName, sourceDB);
		    stmt2 = PPI.prepareStatement(con,
						 "update InteractionDataset set data_source=? where id=?");
		    stmt2.setString(1,sourceDB);
		    stmt2.setString(2,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}

		// set up interaction
		String interactionID = lookupOrCreateInteraction(datasetID,
								 interaction);
		String interactionKey = datasetID+"_"+interactionID;
		// update metadata if different
		if (isDirectional &&
		    (!directionalInteractions.contains(interactionKey))) {
		    directionalInteractions.add(interactionKey);
		    stmt2 = PPI.prepareStatement(con,
						 "update Interaction set directional=1 where id=?");
		    stmt2.setString(1,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}
		if (!Double.isNaN(confidence)) {
		    Double oldConf = interactionConfidence.get(interactionKey);
		    if ((oldConf == null) ||
			(oldConf.doubleValue() != confidence)) {
			interactionConfidence.put(interactionKey,
						  new Double(confidence));
			stmt2 = PPI.prepareStatement(con,
						     "update Interaction set confidence=? where id=?");
			stmt2.setDouble(1,confidence);
			stmt2.setString(2,interactionID);
			stmt2.executeUpdate();
			stmt2.close();
		    }
		}
		if (methodID != null) {
		    String oldMethod = interactionMethod.get(interactionKey);
		    if ((oldMethod == null) ||
			(!oldMethod.equals(methodID))) {
			interactionMethod.put(interactionKey,
					      methodID);
			stmt2 = PPI.prepareStatement(con,
						     "delete from DetectedWithMethod where to_link = ?");
			stmt2.setString(1,interactionID);
			stmt2.executeUpdate();
			stmt2.close();
			stmt2 = PPI.prepareStatement(con,
						     "insert into DetectedWithMethod values (?, ?)");
			stmt2.setString(1,methodID);
			stmt2.setString(2,interactionID);
			stmt2.executeUpdate();
			stmt2.close();
		    }
		}
		if ((url != null) &&
		    (!url.equals(interactionURL.get(interactionKey)))) {
		    interactionURL.put(interactionKey,url);
		    stmt2 = PPI.prepareStatement(con,
						 "update Interaction set url=? where id=?");
		    stmt2.setString(1,url);
		    stmt2.setString(2,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}
		if ((publicationID != null) &&
		    (!publicationID.equals(interactionPublication.get(interactionKey)))) {
		    interactionPublication.put(interactionKey,publicationID);
		    stmt2 = PPI.prepareStatement(con,
						 "delete from PublishedInteraction where to_link = ?");
		    stmt2.setString(1,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    stmt2 = PPI.prepareStatement(con,
						 "insert into PublishedInteraction values (?, ?)");
		    stmt2.setString(1,publicationID);
		    stmt2.setString(2,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}

		// add feature(s)
		if (reverseIDs && !isSpoke) {
		    String tmpS = featureID1;
		    featureID1 = featureID2;
		    featureID2 = tmpS;

		    int tmpI = stoich1;
		    stoich1 = stoich2;
		    stoich2 = tmpI;

		    HashMap<String,String> tmpH = xrefs1;
		    xrefs1 = xrefs2;
		    xrefs2 = tmpH;
		}

		// add feature that encodes 1st protein
		stmt2 = PPI.prepareStatement(con,
					     "insert into InteractionFeature values (?, ?, ?, ?, ?)");
		stmt2.setString(1,interactionID);
		stmt2.setString(2,featureID1);
		int rank = 1;
		Integer lastRank = interactionRank.get(interactionKey);
		if (lastRank != null)
		    rank = lastRank.intValue()+1;
		interactionRank.put(interactionKey, new Integer(rank));
		stmt2.setInt(3,rank);
		stmt2.setInt(4,stoich1);
		stmt2.setDouble(5,0.0); // no current way to encode strength
		stmt2.executeUpdate();
		
		// add 1st protein
		PreparedStatement stmt3 = PPI.prepareStatement(con,
							       "insert into InteractionProtein values (?, ?, ?, ?, ?)");
		stmt3.setString(1,interactionID);
		String proteinID1 = getProtein(featureID1);
		stmt3.setString(2,proteinID1);
		stmt3.setInt(3,rank);
		stmt3.setInt(4,stoich1);
		stmt3.setDouble(5,0.0); // no current way to encode strength
		stmt3.executeUpdate();

		if (!isSpoke) {
		    // add feature that encodes 2nd protein
		    stmt2.setString(2,featureID2);
		    rank++;
		    interactionRank.put(interactionKey, new Integer(rank));
		    stmt2.setInt(3,rank);
		    stmt2.setInt(4,stoich2);
		    stmt2.executeUpdate();

		    // add 2nd protein
		    String proteinID2 = getProtein(featureID2);
		    stmt3.setString(2,featureID2);
		    stmt3.setInt(3,rank);
		    stmt3.setInt(4,stoich2);
		    stmt3.executeUpdate();
		}
		stmt2.close();
		stmt3.close();

		// add genome link (must be same for both features)
		String genomeID = getGenomeFor(featureID1);
		if (!isValidGenome(genomeID))
		    throw new Exception ("KBase doesn't have genome "+genomeID);
		if (!dsGenomeSet.contains(datasetID+"_"+genomeID)) {
		    stmt2 = PPI.prepareStatement(con,
						 "insert into IsDatasetFor values (?, ?)");
		    stmt2.setString(1,datasetID);
		    stmt2.setString(2,genomeID);
		    stmt2.executeUpdate();
		    stmt2.close();
		    dsGenomeSet.add(datasetID+"_"+genomeID);
		}
	    }

	    stmt.close();
	    con.close();
	}
	catch (Exception e) {
	    System.out.println("Exception: "+e.getMessage());
	    e.printStackTrace();
	}
    }
}
