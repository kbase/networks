package us.kbase.networks.importer;

import java.sql.*;
import java.io.*;
import java.util.*;
import org.strbio.util.*;
import org.strbio.IO;
import us.kbase.networks.local.PPI;

/**
   Import PPI data from a PSI-MI TAB 2.7 file, with KBase format
   modifications described below:

   1) Unique identifiers for Interactors A and B are KBase Feature ids.

   2) When the complex expansion method (the 16th column in the file)
      is "spoke expansion" everything about Interactor B is ignored,
      and the data are assumed to refer to a multi-protein complex
      described by multiple lines listing data for Interactor A.

   3) Several pieces of optional metadata are encoded in the "Xref for
      Interactor A" field (the 23rd column in the file):  "dataset:"
      refers to interaction_dataset.description, "dataseturl:" refers to the
      interaction_dataset.data_url, and "url:" refers to interaction.data_url.

   4) We have extended the PSI ontology for interaction detection methods
      (column 7) to include "kb:" methods, which refer to
      interaction_detection_type.description.  If one if these methods
      is listed, the psi-mi: method is ignored.  If there is no "kb:"
      method, the text of the "psi-mi:" ontology is used instead.

   5) We have extended the PSI ontology for source database
      (column 13) to include "kb:" descriptions, which refer to
      interaction_dataset.data_source.  If one if these methods
      is listed, the psi-mi: method is ignored.  If there is no "kb:"
      method, the text of the "psi-mi:" ontology is used instead.

  @version 1.0, 10/3/12
  @author JMC
*/
public class ImportPSIMI {
    /**
       Looks up a publication by its pubmed id, or creates one if it
       doesn't already exist in the (standin) publication table.

       This function should be replaced by something that interacts
       with the real publications in the CS.
    */
    final public static String lookupOrCreatePublication(int pubmedID) throws Exception {
	Statement stmt = PPI.createStatement();
	ResultSet rs = stmt.executeQuery("select id from tmp_publication where link=\"pubmed:"+pubmedID+"\"");
	if (rs.next()) {
	    String rv = rs.getString(1);
	    rs.close();
	    stmt.close();
	    return rv;
	}
	rs.close();
	stmt.executeUpdate("insert into tmp_publication values (\"jmc|pub."+pubmedID+"\", null, \"pubmed:"+pubmedID+"\")");
	stmt.close();
	return ("jmc|pub."+pubmedID);
    }

    /**
       Look up an interaction dataset by its description, or create
       one if it doesn't already exist
    */
    final public static int lookupOrCreateDataset(String description) throws Exception {
	PreparedStatement stmt = PPI.prepareStatement("select id from interaction_dataset where description=?");
	stmt.setString(1,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    int rv = rs.getInt(1);
	    rs.close();
	    stmt.close();
	    return rv;
	}
	rs.close();
	stmt.close();
	stmt = PPI.prepareStatement("insert into interaction_dataset values (null, ?, null, null)",
				    Statement.RETURN_GENERATED_KEYS);
	stmt.setString(1,description);
	stmt.executeUpdate();
	rs = stmt.getGeneratedKeys();
	rs.next();
	int rv = rs.getInt(1);
	rs.close();
	stmt.close();
	return rv;
    }

    /**
       Look up an interaction detection type by its description, or return
       0 if it doesn't exist.
    */
    final public static int lookupMethod(String description) throws Exception {
	if (description==null)
	    return 0;
	
	PreparedStatement stmt = PPI.prepareStatement("select id from interaction_detection_type where description=?");
	stmt.setString(1,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    int rv = rs.getInt(1);
	    rs.close();
	    stmt.close();
	    return rv;
	}
	rs.close();
	stmt.close();
	return 0;
    }
    
    /**
       Create an interaction detection type using its description, and
       return its id.
    */
    final public static int createMethod(String description) throws Exception {
	PreparedStatement stmt = PPI.prepareStatement("insert into interaction_detection_type values (null, ?)",
						      Statement.RETURN_GENERATED_KEYS);
	stmt.setString(1,description);
	stmt.executeUpdate();
	ResultSet rs = stmt.getGeneratedKeys();
	rs.next();
	int rv = rs.getInt(1);
	rs.close();
	stmt.close();
	return rv;
    }

    /**
       Look up an interaction dataset by its description, or create
       one if it doesn't already exist
    */
    final public static int lookupOrCreateInteraction(int datasetID,
						      String description) throws Exception {
	PreparedStatement stmt = PPI.prepareStatement("select id from interaction where interaction_dataset_id=? and description=?");
	stmt.setInt(1,datasetID);
	stmt.setString(2,description);
	ResultSet rs = stmt.executeQuery();
	if (rs.next()) {
	    int rv = rs.getInt(1);
	    rs.close();
	    stmt.close();
	    return rv;
	}
	rs.close();
	stmt.close();
	stmt = PPI.prepareStatement("insert into interaction values (null, ?, ?, false, null, null, null, null)",
				    Statement.RETURN_GENERATED_KEYS);
	stmt.setInt(1,datasetID);
	stmt.setString(2,description);
	stmt.executeUpdate();
	rs = stmt.getGeneratedKeys();
	rs.next();
	int rv = rs.getInt(1);
	rs.close();
	stmt.close();
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
    
    final public static void main(String argv[]) {
	try {
	    PPI.connectRW();
	    Statement stmt = PPI.createStatement();
	    PreparedStatement stmt2;

	    // keep track of what datasets we've seen in this file
	    HashSet<String> seenDatasets = new HashSet<String>();

	    // map datasets to url and source
	    HashMap<String,String> datasetURLMap = new HashMap<String,String>();
	    HashMap<String,String> datasetSourceMap = new HashMap<String,String>();

	    // map interactions to metadata
	    HashSet<String> directionalInteractions = new HashSet<String>();
	    HashMap<String,Double> interactionConfidence = new HashMap<String,Double>();
	    HashMap<String,Integer> interactionMethod = new HashMap<String,Integer>();
	    HashMap<String,String> interactionURL = new HashMap<String,String>();
	    HashMap<String,String> interactionPublication = new HashMap<String,String>();
	    HashMap<String,Integer> interactionRank = new HashMap<String,Integer>();

	    // read in file line by line
	    BufferedReader infile = IO.openReader(argv[0]);
	    String buffer = infile.readLine();
	    while (buffer != null) {
		StringTokenizer st = new StringTokenizer(buffer,"\t");

		// feature ids
		String featureID1 = st.nextToken();
		String featureID2 = st.nextToken();

		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();

		// experiment/annotation method
		String method = parseKBField(st.nextToken());
		int methodID = lookupMethod(method);
		if (methodID==0) {
		    // throw new Exception("Must create method '"+method+"'");
		    methodID = createMethod(method);
		}
		st.nextToken();

		// publication
		String publication = st.nextToken();
		String publicationID = null;
		int pos = publication.indexOf("pubmed:");
		if (pos > 0) {
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
		boolean isDirected = false;
		boolean reverseIDs = false;
		if ((expRole1.equals("psi-mi:\"MI:0496\"(bait)")) &&
		    (expRole2.equals("psi-mi:\"MI:0498\"(prey)")))
		    isDirected = true;
		else if ((expRole2.equals("psi-mi:\"MI:0496\"(bait)")) &&
			 (expRole1.equals("psi-mi:\"MI:0498\"(prey)"))) {
		    isDirected = true;
		    reverseIDs = true;
		}

		st.nextToken();
		st.nextToken();

		// xrefs
		String[] xrefs = st.nextToken().split("\\|");
		String datasetName = null;
		String datasetURL = null;
		String url = null;
		for (String xref : xrefs) {
		    pos = xref.indexOf(":");
		    if (pos > -1) {
			String key = xref.substring(0,pos).trim();
			String val = xref.substring(pos+1).trim();
			if (key.equals("url"))
			    url = val;
			else if (key.equals("dataset"))
			    datasetName = val;
			else if (key.equals("dataseturl"))
			    datasetURL = val;
		    }
		}

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
		st.nextToken();

		// stoichiometry
		int stoich1 = StringUtil.atoi(st.nextToken());
		int stoich2 = StringUtil.atoi(st.nextToken());

		st.nextToken();
		st.nextToken();

		// insert data in this row
		if (datasetName==null)
		    throw new Exception("Need dataset name for row '"+buffer+"'");
		int datasetID = lookupOrCreateDataset(datasetName);

		// if this is the first time seeing this dataset
		// in this file, clear out old data
		if (!seenDatasets.contains(datasetName)) {
		    seenDatasets.add(datasetName);
		    stmt.executeUpdate("delete from interaction where interaction_dataset_id="+datasetID);
		}

		// update URL, source if different
		if ((datasetURL != null) &&
		    (!datasetURL.equals(datasetURLMap.get(datasetName)))) {
		    datasetURLMap.put(datasetName, datasetURL);
		    stmt2 = PPI.prepareStatement("update interaction_dataset set data_url=? where id=?");
		    stmt2.setString(1,datasetURL);
		    stmt2.setInt(2,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}
		if ((sourceDB != null) &&
		    (!sourceDB.equals(datasetSourceMap.get(datasetName)))) {
		    datasetSourceMap.put(datasetName, sourceDB);
		    stmt2 = PPI.prepareStatement("update interaction_dataset set data_source=? where id=?");
		    stmt2.setString(1,sourceDB);
		    stmt2.setInt(2,datasetID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}

		// set up interaction
		int interactionID = lookupOrCreateInteraction(datasetID,
							      interaction);
		String interactionKey = datasetID+"_"+interactionID;
		// update metadata if different
		if (isDirected &&
		    (!directionalInteractions.contains(interactionKey))) {
		    directionalInteractions.add(interactionKey);
		    stmt.executeUpdate("update interaction set is_directional=true where id="+interactionID);
		}
		if (!Double.isNaN(confidence)) {
		    Double oldConf = interactionConfidence.get(interactionKey);
		    if ((oldConf == null) ||
			(oldConf.doubleValue() != confidence)) {
			interactionConfidence.put(interactionKey,
						  new Double(confidence));
			stmt.executeUpdate("update interaction set confidence="+confidence+" where id="+interactionID);
		    }
		}
		if (methodID > 0) {
		    Integer oldMethod = interactionMethod.get(interactionKey);
		    if ((oldMethod == null) ||
			(oldMethod.intValue() != methodID)) {
			interactionMethod.put(interactionKey,
					      new Integer(methodID));
			stmt.executeUpdate("update interaction set detection_method_id="+methodID+" where id="+interactionID);
		    }
		}
		if ((url != null) &&
		    (!url.equals(interactionURL.get(interactionKey)))) {
		    interactionURL.put(interactionKey,url);
		    stmt2 = PPI.prepareStatement("update interaction set data_url=? where id=?");
		    stmt2.setString(1,url);
		    stmt2.setInt(2,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}
		if ((publicationID != null) &&
		    (!publicationID.equals(interactionPublication.get(interactionKey)))) {
		    interactionPublication.put(interactionKey,publicationID);
		    stmt2 = PPI.prepareStatement("update interaction set citation_id=? where id=?");
		    stmt2.setString(1,publicationID);
		    stmt2.setInt(2,interactionID);
		    stmt2.executeUpdate();
		    stmt2.close();
		}

		// add feature(s)
		if (reverseIDs && !isSpoke) {
		    String fid = featureID1;
		    featureID1 = featureID2;
		    featureID2 = fid;

		    int tmpI = stoich1;
		    stoich1 = stoich2;
		    stoich2 = tmpI;
		}

		// add 1st feature
		stmt2 = PPI.prepareStatement("insert into interaction_feature values (null, ?, ?, ?, null, ?)");
		stmt2.setInt(1,interactionID);
		stmt2.setString(2,featureID1);
		if (stoich1 > 0)
		    stmt2.setInt(3,stoich1);
		else
		    stmt2.setNull(3,Types.INTEGER);
		int rank = 1;
		Integer lastRank = interactionRank.get(interactionKey);
		if (lastRank != null)
		    rank = lastRank.intValue()+1;
		interactionRank.put(interactionKey, new Integer(rank));
		stmt2.setInt(4,rank);
		stmt2.executeUpdate();

		if (!isSpoke) {
		    // add 2nd feature
		    stmt2.setString(2,featureID2);
		    if (stoich2 > 0)
		    stmt2.setInt(3,stoich2);
		    else
			stmt2.setNull(3,Types.INTEGER);
		    rank++;
		    interactionRank.put(interactionKey, new Integer(rank));
		    stmt2.setInt(4,rank);
		    stmt2.executeUpdate();
		}
	    }
	}
	catch (Exception e) {
	    System.out.println("Exception: "+e.getMessage());
	    e.printStackTrace();
	}
    }
}
