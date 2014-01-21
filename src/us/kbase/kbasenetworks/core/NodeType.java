package us.kbase.kbasenetworks.core;

public enum NodeType {
	GENE, PROTEIN, SUBSYSTEM, CLUSTER;
		
	// List of some predefined edge types
	public static final String EDGE_GENE_GENE = NodeType.toEdgeType(GENE, GENE);
	public static final String EDGE_GENE_CLUSTER = NodeType.toEdgeType(GENE, CLUSTER);
	public static final String EDGE_CLUSTER_CLUSTER = NodeType.toEdgeType(CLUSTER, CLUSTER);
	
	
	
	public static String toEdgeType(NodeType nt1, NodeType nt2){
		return nt1.name() + "_" + nt2.name();
	}	
	public static NodeType nodeType1(String edgeType)
	{
		return nodeType(edgeType, 0);
	}	
	public static NodeType nodeType2(String edgeType)
	{
		return nodeType(edgeType, 1);
	}		
	private static NodeType nodeType(String edgeType, int index)
	{
		return Enum.valueOf(NodeType.class, edgeType.split("_")[index]);
	}	
	
}
