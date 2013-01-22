package us.kbase.networks.adaptor.jdbc;

import us.kbase.networks.core.EntityType;

public class Term {
	public static final String FIELD_NAME_DATASET_ID = "DATASET_ID";
	public static final String FIELD_NAME_DATASET_NAME = "DATASET_NAME";
	public static final String FIELD_NAME_DATASET_DESCRIPTION = "DATASET_DESCRIPTION";
	public static final String FIELD_NAME_DATASET_NETWORKTYPE = "DATASET_NETWORKTYPE";
	public static final String FIELD_NAME_DATASET_SOURCEREFERENCE = "DATASET_SOURCEREFERENCE";
	public static final String FIELD_NAME_DATASET_TAXONS = "DATASET_TAXONS";

	
	
	public static final String SQL_Statement_Prefix = "SQL.";
	public static final String SQL_Like_Statement_Prefix = "SLIKE.";

	public static final String Node1_Type_Mapping_Prefix= "Edge2NodeType1.";
	public static final String Node2_Type_Mapping_Prefix = "Edge2NodeType2.";

	
	// Query parameter indexes
	public static final String Prefix_QueryIndex_GeneId = "QIndex.GeneId.";
	public static final String Prefix_QueryIndex_ProteinId = "QIndex.ProteinId."; 
	public static final String Prefix_QueryIndex_OperonId = "QIndex.OperonId."; 
	public static final String Prefix_QueryIndex_PPIComplexId = "QIndex.PPIComplexId."; 
	public static final String Prefix_QueryIndex_GenomeId = "QIndex.GenomeId."; 
	public static final String Prefix_QueryIndex_RegulonId = "QIndex.RegulonId.";
	public static final String Prefix_QueryIndex_RegulomeId = "QIndex.RegulomeId.";
	public static final String Prefix_QueryIndex_SubsystemId = "QIndex.SubsystemId.";
	public static final String Prefix_QueryIndex_ClusterId = "QIndex.ClusterId.";
	public static final String Prefix_QueryIndex_BiclusterId = "QIndex.BiclusterId.";	
	public static final String Prefix_QueryIndex_DatasetId = "QIndex.DatasetId.";
	
	public static final String[] Prefix_QueryIndexes = new String[]{
		Prefix_QueryIndex_GeneId,
		Prefix_QueryIndex_ProteinId,
		Prefix_QueryIndex_OperonId,
		Prefix_QueryIndex_PPIComplexId,
		Prefix_QueryIndex_GenomeId,
		Prefix_QueryIndex_RegulonId,
		Prefix_QueryIndex_RegulomeId,
		Prefix_QueryIndex_SubsystemId,
		Prefix_QueryIndex_ClusterId,
		Prefix_QueryIndex_BiclusterId,		
		Prefix_QueryIndex_DatasetId
	};
	
	public static String toQueryIndexPrefix(EntityType type) {
		if(type == EntityType.GENE) 			return Prefix_QueryIndex_GeneId;
		else if(type == EntityType.PROTEIN) 	return Prefix_QueryIndex_ProteinId;
		else if(type == EntityType.OPERON) 		return Prefix_QueryIndex_OperonId;
		else if(type == EntityType.PPI_COMPLEX)	return Prefix_QueryIndex_PPIComplexId;
		else if(type == EntityType.GENOME)		return Prefix_QueryIndex_GenomeId;
		else if(type == EntityType.REGULON)		return Prefix_QueryIndex_RegulonId;
		else if(type == EntityType.REGULOME)	return Prefix_QueryIndex_RegulomeId;
		else if(type == EntityType.SUBSYSTEM)	return Prefix_QueryIndex_SubsystemId;
		else if(type == EntityType.CLUSTER)		return Prefix_QueryIndex_ClusterId;
		else if(type == EntityType.BICLUSTER)	return Prefix_QueryIndex_BiclusterId;		
		
		return "";
	}	
	
	// Resultset column indexes
	public static final String Prefix_ResultsetIndex_EntityId1  	= "RSIndex.EntityId1.";
	public static final String Prefix_ResultsetIndex_EntityId2  	= "RSIndex.EntityId2.";
	public static final String Prefix_ResultsetIndex_NodeName1  	= "RSIndex.NodeName1.";
	public static final String Prefix_ResultsetIndex_NodeName2  	= "RSIndex.NodeName2.";		
	public static final String Prefix_ResultsetIndex_Weight  		= "RSIndex.Weight.";
	public static final String Prefix_ResultsetIndex_EdgeName 		= "RSIndex.EdgeName.";
	public static final String Prefix_ResultsetIndex_DatasetId 		= "RSIndex.DatasetId.";

	public static final String Prefix_ResultsetIndex_EdgeDirected 		= "RSIndex.EdgeDirected.";
	
	public static final String[] Prefix_ResultsetIndexes = new String[]{
		Prefix_ResultsetIndex_EntityId1, 
		Prefix_ResultsetIndex_EntityId2,
		Prefix_ResultsetIndex_NodeName1,
		Prefix_ResultsetIndex_NodeName2,
		Prefix_ResultsetIndex_Weight,
		Prefix_ResultsetIndex_EdgeName,
		Prefix_ResultsetIndex_DatasetId
	};
	
	public static final String Prefix_ResultsetName_NodeId1 	= "RSName.Node1Properties.";
	public static final String Prefix_ResultsetName_NodeId2 	= "RSName.Node2Properties.";
	public static final String Prefix_ResultsetName_Edge 		= "RSName.EdgeProperties.";			

	public static final String Safer_Internal_Network_Prefix = "SafeInt.";

	public static final String DefaultEdgeTypes = "Default.EdgeTypes";
	public static final String SupportedEdgeTypes = "Supported.EdgeTypes";
	public static final String JUNGEdgeType = "JUNG.EdgeType";
	

}
