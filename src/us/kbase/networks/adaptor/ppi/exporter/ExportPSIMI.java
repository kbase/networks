package us.kbase.networks.adaptor.ppi.exporter;

import java.sql.*;
import java.io.*;
import java.util.*;
import org.strbio.util.*;
import org.strbio.IO;
import us.kbase.networks.adaptor.ppi.local.PPI;

/**
   Dump PPI data to a PSI-MI TAB 2.7 file, with KBase format
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
      interaction_detection_type.description.  We only dump kb: ontology
      rather than using the psi-mi: ontology.

   5) We have extended the PSI ontology for source database
      (column 13) to include "kb:" descriptions, which refer to
      interaction_dataset.data_source.  We only dump kb: ontology
      rather than using the psi-mi: ontology.

  @version 2.0, 11/29/12
  @author JMC
*/
public class ExportPSIMI {
    /**
       encode all the data in interaction_data, as well as special
       variables, in xrefs field
    */
    final public static String encodeXrefs(int interactionProteinID,
					   String datasetName,
					   String datasetURL,
					   String url) throws Exception {
	String rv = "";
	if (datasetName != null)
	    rv += "dataset:"+datasetName+"|";
	if (datasetURL != null)
	    rv += "dataseturl:\""+datasetURL+"\"|";
	if (url != null)
	    rv += "url:\""+url+"\"|";

	PPI.connect();
	Connection con = PPI.getConnection();
	Statement stmt = PPI.createStatement(con);
	ResultSet rs = stmt.executeQuery("select description, data from interaction_data where interaction_protein_id="+interactionProteinID);
	while (rs.next()) {
	    String key = rs.getString(1);
	    String value = rs.getString(2);
	    rv += key+":\""+value+"\"|";
	}
	rs.close();
	stmt.close();
	con.close();
	if (rv.length() == 0)
	    return "-";
	else
	    return rv.substring(0,rv.length()-1);
    }
    
    final public static void main(String argv[]) {
	try {
	    PPI.connect();
	    Connection con = PPI.getConnection();
	    Statement stmt = PPI.createStatement(con);
	    Statement stmt2 = PPI.createStatement(con);
	    Statement stmt3 = PPI.createStatement(con);
	    ResultSet rs, rs2, rs3;

	    // dump out all datasets
	    rs3 = stmt3.executeQuery("select id, description, data_source, data_url from interaction_dataset");
	    while (rs3.next()) {
		int datasetID = rs3.getInt(1);
		String datasetName = rs3.getString(2);
		String datasetSource = rs3.getString(3);
		if (rs3.wasNull())
		    datasetSource = "-";
		else
		    datasetSource = "kb:"+datasetSource;
		String datasetURL = rs3.getString(4);

		rs2 = stmt2.executeQuery("select i.id, i.description, i.is_directional, i.confidence, m.description, i.data_url, p.link from interaction i left join interaction_detection_type m on i.detection_method_id=m.id left join tmp_publication p on p.id=i.citation_id where i.interaction_dataset_id="+datasetID+" order by id asc");
		while (rs2.next()) {
		    int interactionID = rs2.getInt(1);
		    String interaction = rs2.getString(2);
		    boolean isDirectional = (rs2.getInt(3)==1);
		    double confidence = rs2.getDouble(4);
		    String confidenceStr = "-";
		    if (!rs2.wasNull())
			confidenceStr = "author-score:"+confidence;
		    String method = rs2.getString(5);
		    if (method==null)
			method = "-";
		    else
			method = "kb:"+method;
		    String url = rs2.getString(6);
		    String publication = rs2.getString(7);
		    if (publication==null)
			publication = "-";

		    // need to know how many components, to
		    // decide how to encode
		    rs = stmt.executeQuery("select count(*) from interaction_protein where interaction_id="+interactionID);
		    rs.next();
		    int nComponents = rs.getInt(1);
		    rs.close();

		    // use spoke expansion of complex?
		    boolean isSpoke = true;
		    if (nComponents==2)
			isSpoke = false;
		    String expansionMethod = "-";
		    if (isSpoke)
			expansionMethod = "psi-mi:\"MI:1060\"(spoke expansion)";

		    // if doing spoke expansion use first component as hub:
		    String firstComponent = null;
		    int firstStoichiometry = 0;
		    String firstXrefs = null;

		    // get components in order
		    rs = stmt.executeQuery("select id, protein_id, stoichiometry from interaction_protein where interaction_id="+interactionID+" order by rank asc");
		    while (rs.next()) {
			int interactionProteinID = rs.getInt(1);
			String proteinID = rs.getString(2);
			int stoichiometry = rs.getInt(3);

			String xrefs = encodeXrefs(interactionProteinID,
						   datasetName,
						   datasetURL,
						   url);
			
			if ((firstComponent==null) && (isSpoke)) {
			    firstComponent = proteinID;
			    firstStoichiometry = stoichiometry;
			    firstXrefs = xrefs;
			}

			String role1 = "-";
			String role2 = "-";
			if ((isDirectional) && (!isSpoke)) {
			    role1 = "psi-mi:\"MI:0496\"(bait)";
			    role2 = "psi-mi:\"MI:0498\"(prey)";
			}
			String sto1 = "-";
			if (stoichiometry > 0)
			    sto1 = ""+stoichiometry;
			String sto2 = "-";
			String pid2 = null;
			int pos = proteinID.indexOf(".",5);
			String genome1 = proteinID.substring(0,pos);
			String genome2 = null;
			String xrefs2 = null;

			if (isSpoke) {
			    pid2 = firstComponent;
			    pos = pid2.indexOf(".",5);
			    genome2 = pid2.substring(0,pos);
			    if (firstStoichiometry > 0)
				sto2 = ""+firstStoichiometry;
			    xrefs2 = firstXrefs;
			}
			else {
			    rs.next();
			    int interactionProteinID2 = rs.getInt(1);
			    pid2 = rs.getString(2);
			    int stoichiometry2 = rs.getInt(3);

			    pos = pid2.indexOf(".",5);
			    genome2 = pid2.substring(0,pos);
			    if (stoichiometry2 > 0)
				sto2 = ""+stoichiometry2;
			    xrefs2 = encodeXrefs(interactionProteinID2,
						 datasetName,
						 datasetURL,
						 url);
			}

			System.out.println(proteinID+"\t"+pid2+"\t-\t-\t-\t-\t"+method+"\t-\t"+publication+"\t"+genome1+"\t"+genome2+"\tpsi-mi:\"MI:0915\"(physical association)\t"+datasetSource+"\t"+interaction+"\t"+confidenceStr+"\t"+expansionMethod+"\t-\t-\t"+role1+"\t"+role2+"\t-\t-\t"+xrefs+"\t"+xrefs2+"\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\t-\t"+sto1+"\t"+sto2+"\t-\t-");
		    }
		    rs.close();
		}
		rs2.close();
	    }
	    rs3.close();
	    stmt3.close();
	    stmt2.close();
	    stmt.close();
	    con.close();
	}
	catch (Exception e) {
	    System.out.println("Exception: "+e.getMessage());
	    e.printStackTrace();
	}
    }
}
