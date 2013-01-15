package us.kbase.networks.core;

public enum EdgeType {
	GENE_GENE, GENE_CLUSTER, 
	PROTEIN_PROTEIN, PROTEIN_CLUSTER,
	CLUSTER_CLUSTER;
	
	public NodeType nodeType1()
	{
		return nodeType(0);
	}
	
	public NodeType nodeType2()
	{
		return nodeType(1);
	}
	
	private NodeType nodeType(int index)
	{
		return Enum.valueOf(NodeType.class, name().split("_")[index]);
	}
}
