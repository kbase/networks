package us.kbase.networks.adaptor.jdbc;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import us.kbase.networks.adaptor.AbstractAdaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.IdGenerator;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;

import com.mchange.v2.c3p0.C3P0ProxyConnection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class GenericAdaptor extends AbstractAdaptor{

	private GenericAdaptorConfiguration adaptorConfig; 
	
	public GenericAdaptor(Configuration config) throws Exception{
		super(config);
	}
	
	@Override
	protected void init() throws AdaptorException{
		adaptorConfig = new GenericAdaptorConfiguration(config);
		super.init();
	}		
		
	@Override
	protected List<Dataset> loadDatasets() throws AdaptorException {
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		
		C3P0ProxyConnection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try{
			String methodName = "loadDatasets";
			String propertySuffix = methodName;
			
			QueryConfig queryConfig = adaptorConfig.getQueryConfig(propertySuffix);
			if(queryConfig == null) {
				throw new AdaptorException("Can not load datasets");
			};
			
			connection = adaptorConfig.getConnection();
			try{
				pst = queryConfig.buildPreparedStatement(connection);
				rs = pst.executeQuery();
							
				while(rs.next())
				{
					datasets.add(buildDataset(rs));
				}
				rs.close();
				pst.close();
			}
			finally{
				connection.close();
			}
		}
		catch(SQLException e)
		{
			throw new AdaptorException(e.getMessage(), e);
		}
		
		return datasets;
	}

	private Dataset buildDataset(ResultSet rs) throws SQLException {
		return new Dataset(
				rs.getString(Term.FIELD_NAME_DATASET_ID),
				rs.getString(Term.FIELD_NAME_DATASET_NAME),
				rs.getString(Term.FIELD_NAME_DATASET_DESCRIPTION),
				Enum.valueOf(NetworkType.class, rs.getString(Term.FIELD_NAME_DATASET_NETWORKTYPE)),
				Enum.valueOf(DatasetSource.class, rs.getString(Term.FIELD_NAME_DATASET_SOURCEREFERENCE)),
				buildTaxons(rs.getString(Term.FIELD_NAME_DATASET_TAXONS))
			);	
	}
	
	@Override
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		
		C3P0ProxyConnection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try{
			String methodName = "getDatasets";
			String propertySuffix = methodName + "." + entity.getType();
			
			QueryConfig queryConfig = adaptorConfig.getQueryConfig(propertySuffix);
			if(queryConfig == null) return datasets;

			connection = adaptorConfig.getConnection();
			try{
				pst = queryConfig.buildPreparedStatement(connection, toPropertyArguemnts(Term.toQueryIndexPrefix(entity.getType()), entity));
				if(pst == null)
				{
					throw new AdaptorException("Can not load datasets");
				}
				rs = pst.executeQuery();
				int datasetIdIndex = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_DatasetId, propertySuffix) ;
							
				while(rs.next())
				{
					Dataset dataset = getDataset(rs.getString(datasetIdIndex));
					if(dataset != null)
					{
						datasets.add(dataset);
					}
				}		
				rs.close();
				pst.close();
			}finally{
				connection.close();
			}
		}
		catch(SQLException e)
		{
			throw new AdaptorException(e.getMessage(), e);
		}
		
		return datasets;
	}
	

	@Override
	public Network buildNetwork(Dataset dataset) throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;

		List<EdgeType> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildNetwork(dataset, edgeTypes);
	}

	
	/*		
	String sjet = null;
	if((sjet = dataset.getProperty(JUNGEdgeType)) != null ) {
		jet = edu.uci.ics.jung.graph.util.EdgeType.valueOf(sjet);
	}
*/
	
	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> queryEdgeTypes)
			throws AdaptorException {
		
		String methodName = "buildNetwork";			
		MethodProperties methodProperties = new MethodProperties(methodName){
			@Override
			public String getPropertySuffix() {
				return methodName + "." + et1 + "_" + et2;			
			}			
		};
		
		return  
			buildNetwork(dataset, methodProperties, queryEdgeTypes,
				toPropertyArguemnts(dataset));
	}
	


	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, Entity entity)
			throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;
		
		List<EdgeType> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildFirstNeighborNetwork(dataset, entity, edgeTypes);
	}
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, final Entity entity,
			List<EdgeType> queryEdgeTypes) throws AdaptorException {

		String methodName = "buildFirstNeighborNetwork";
		MethodProperties methodProperties = new MethodProperties(methodName){
			@Override
			public String getPropertySuffix() {
				return methodName + "." + et1 + "_" + et2 + "." + entity.getType();			
			}			
		};
		
		return  
			buildNetwork(dataset, methodProperties, queryEdgeTypes,
				toPropertyArguemnts(dataset), 
				toPropertyArguemnts(Term.toQueryIndexPrefix(entity.getType()), entity));		
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities)
			throws AdaptorException {
		if( !hasDataset(dataset.getId())) return null;

		List<EdgeType> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildInternalNetwork(dataset, entities, edgeTypes);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<EdgeType> queryEdgeTypes) throws AdaptorException {
		
		String methodName = "buildInternalNetwork";
		MethodProperties methodProperties = new MethodProperties(methodName){
			@Override
			public String getPropertySuffix() {
				return methodName + "." + et1 + "_" + et2;			
			}			
		};
		Hashtable<EntityType, List<Entity>> type2EntitiesHash = classifyEntitiesByType(entities);
				
		QueryPropertyArguments[] qpArguments = new QueryPropertyArguments[type2EntitiesHash.keySet().size() + 1];
		int i = 0;
		qpArguments[i++] = toPropertyArguemnts(dataset);
		for(EntityType et: type2EntitiesHash.keySet())
		{
			qpArguments[i++] = toPropertyArguemnts(Term.toQueryIndexPrefix(et), type2EntitiesHash.get(et));
		}
		
		return  
			buildNetwork(dataset, methodProperties, queryEdgeTypes, qpArguments);
	}
	
	
	private Network buildNetwork(Dataset dataset, 
			MethodProperties methodProperties,
			List<EdgeType> queryEdgeTypes,
			QueryPropertyArguments ... propertiesArguments) throws AdaptorException
	{
		if( !hasDataset(dataset.getId())) return null;

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(
				IdGenerator.Network.nextId(),
				dataset.getId() + ": " + methodProperties.methodName, 
				graph);
		
		List<EdgeType> edgeTypes = getEdgeTypes(Term.SupportedEdgeTypes, queryEdgeTypes);
		if(edgeTypes.isEmpty()) return null;

		try {
			Hashtable<String,Node> nodeId2NodeHash = new Hashtable<String,Node>(); 
			for(EdgeType et : edgeTypes) {
				methodProperties.prepareEdgeType(et);
				populateGraph(graph, dataset, methodProperties, 
						nodeId2NodeHash, propertiesArguments);
			}
		} catch (Exception e ) {
			throw new AdaptorException(e.getMessage(), e);
		}
		return network;
		
	}
	

	private abstract class MethodProperties{
		String methodName;
		EdgeType edgeType;
		NodeType nt1;
		NodeType nt2;
		EntityType et1;
		EntityType et2;
		
		private MethodProperties(String methodName) throws AdaptorException
		{
			this.methodName = methodName;
		}
		
		public void prepareEdgeType(EdgeType edgeType) throws AdaptorException{
			this.edgeType = edgeType;
			assertEdgeTypeMapping();
			this.nt1 = edgeType.nodeType1();
			this.nt2 = edgeType.nodeType2();
			this.et1 = toEntityType(Term.Node1_Type_Mapping_Prefix, nt1);
			this.et2 = toEntityType(Term.Node2_Type_Mapping_Prefix, nt2);
		}
		
		public abstract String getPropertySuffix();
		
		private void assertEdgeTypeMapping() throws AdaptorException {
			
			if( !config.containsKey(Term.Node1_Type_Mapping_Prefix + edgeType.nodeType1().name())  || 
				!config.containsKey(Term.Node2_Type_Mapping_Prefix + edgeType.nodeType2().name()) 	)
				
				throw new AdaptorException("Couldn't find proper EdgeType to Node EntityType Mapping: " + 
						edgeType.toString());	
		}
	}

	private void populateGraph(Graph<Node, Edge> graph, Dataset dataset, MethodProperties methodProperties, 			
			Hashtable<String, Node> nodeId2NodeHash, 
			QueryPropertyArguments ... propertiesArguments) throws AdaptorException
	{
		C3P0ProxyConnection connection = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			String propertySuffix = methodProperties.getPropertySuffix();
			
			QueryConfig queryConfig = adaptorConfig.getQueryConfig(propertySuffix);
			if(queryConfig == null) return;
			
			connection = adaptorConfig.getConnection();
			try{
				pst = queryConfig.buildPreparedStatement(connection, propertiesArguments);
				if(pst == null) return;
			
				rs = pst.executeQuery();
			
				int entity1Idx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EntityId1, propertySuffix); 
				int entity2Idx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EntityId2, propertySuffix); 

				int nodeName1Idx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_NodeName1, propertySuffix); 
				int nodeName2Idx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_NodeName2, propertySuffix); 
			
				int edgeWeightIdx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_Weight, propertySuffix);
				int edgeNameIndex = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EdgeName, propertySuffix);
				int directedIndex = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EdgeDirected, propertySuffix);
			
				String[] node1FieldNames = getResultSetFieldNames(Term.Prefix_ResultsetName_NodeId1, propertySuffix);
				String[] node2FieldNames = getResultSetFieldNames(Term.Prefix_ResultsetName_NodeId2, propertySuffix);
				String[] edgeFieldNames  = getResultSetFieldNames(Term.Prefix_ResultsetName_Edge, propertySuffix);
			
				while(rs.next()) {
					Node node1 = buildNode(rs, graph, nodeId2NodeHash, entity1Idx, nodeName1Idx, methodProperties.et1, methodProperties.nt1, node1FieldNames);
					Node node2 = buildNode(rs, graph, nodeId2NodeHash, entity2Idx, nodeName2Idx, methodProperties.et2, methodProperties.nt2, node2FieldNames);
					Edge edge  = buildEdge(rs, dataset, edgeNameIndex, edgeWeightIdx, node1, node2, edgeFieldNames);
				
					if(directedIndex > -1)
					{
						final int directedValue = rs.getInt(directedIndex);
						switch(directedValue)
						{
							case  1: graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED); break;
							case -1: graph.addEdge(edge, node2, node1, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED); break;
							case  0: graph.addEdge(edge, node2, node1, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED); break;
						}
					}
					else{
						graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
					}				
				}
			
				rs.close();
				pst.close();
			} finally{
				connection.close();
			}
		}
		catch(Exception e)
		{
			throw new AdaptorException(e.getMessage(), e);
		}
	}
	
	private Edge buildEdge(ResultSet rs, Dataset dataset, int edgeNameIndex, int edgeWeightIdx, Node node1,
			Node node2, String[] edgeFieldNames) throws SQLException {
		
		String edgeName = node1.getId() + ":" + node2.getId();
		if(edgeNameIndex > -1)
		{
			edgeName = rs.getString(edgeNameIndex);
		}
		
		Edge edge = new Edge(IdGenerator.Edge.nextId(), edgeName, dataset);
		if(edgeWeightIdx > -1) {
			edge.setStrength(rs.getFloat(edgeWeightIdx));
		}
		
		// Set other edge properties
		for(String fieldName: edgeFieldNames)
		{
			edge.addProperty(fieldName, rs.getString(fieldName));
		}
		
		return edge;
	}

	private String[] getResultSetFieldNames(String prefixResultsetName, String propertySuffix) {
		String fieldNames = config.getString(prefixResultsetName + propertySuffix);
		if(fieldNames == null) return new String[]{};
				
		String[] fieldNameArray = fieldNames.split(":");
		for(int i = 0; i < fieldNameArray.length; i++)
		{
			fieldNameArray[i] = fieldNameArray[i].trim();  
		}
		
		return fieldNameArray;
	}

	private Hashtable<EntityType, List<Entity>> classifyEntitiesByType(List<Entity> entities) {
		Hashtable<EntityType, List<Entity>> type2EntitiesHash = new Hashtable<EntityType, List<Entity>>();
		for(Entity entity: entities)
		{
			List<Entity> cEntities = type2EntitiesHash.get(entity.getType());
			if(cEntities == null)
			{
				cEntities = new ArrayList<Entity>();
				type2EntitiesHash.put(entity.getType(), cEntities);
			}
			cEntities.add(entity);
		}
		
		return type2EntitiesHash;
	}

	private List<EdgeType> getEdgeTypes(String edgeTypesPrefix)
	{
		List<EdgeType> edgeTypes = new ArrayList<EdgeType>();
		
		String edgeTypesValue = config.getString(edgeTypesPrefix);
		if(edgeTypesValue != null && edgeTypesValue.trim().length() > 0)
		{
			for(String edgeTypeStr:  edgeTypesValue.split(":") )
			{
				edgeTypes.add(Enum.valueOf(EdgeType.class, edgeTypeStr));
			}
		}
		return edgeTypes;	
	}	
	
	private List<EdgeType> getEdgeTypes(String edgeTypesPrefix, List<EdgeType> queryEdgeTypes)
	{
		List<EdgeType> dsEdgeTypes = getEdgeTypes(edgeTypesPrefix);
		dsEdgeTypes.retainAll(queryEdgeTypes);		
		return dsEdgeTypes;
	}	
	

	private int getResultSetPropertyIndex(String prefixResultsetindex, String propertySuffix) {
		return Integer.parseInt(config.getString(prefixResultsetindex + propertySuffix, "-1"));
	}
	
	private Node buildNode(ResultSet rs, Graph<Node, Edge> graph, Map<String, Node> nodeId2NodeHash, 
			int entityIdx, int nodeNameIdx,
			EntityType et, NodeType nt, String[] nodeFieldNames) throws SQLException {
		
		String entityId = rs.getString(entityIdx);
		
		String nodeName = entityId;
		if(nodeNameIdx != -1)
		{
			nodeName = rs.getString(nodeNameIdx);
		}
		
		Node node = nodeId2NodeHash.get(entityId);
		if(node == null)
		{
			node = buildNode(IdGenerator.Node.nextId(), nodeName,  new Entity(entityId, et), nt); 
			nodeId2NodeHash.put(entityId, node);
			
			// Set additional node properties
			for(String fieldName: nodeFieldNames)
			{
				// For nodes, the last symbol in propertyName is either 1 or 2 => we need to remove it
				String propertyName = fieldName.substring(0, fieldName.length() - 1);
				node.addProperty(propertyName, rs.getString(fieldName));
			}			
			
			graph.addVertex(node);
		}
		return node;
	}	

	private List<Taxon> buildTaxons(String taxonsStr) {
		List<Taxon> taxons = new ArrayList<Taxon>();
		for(String taxon: taxonsStr.split(":"))
		{
			taxons.add(new Taxon(taxon));
		}
		return taxons;
	}
	
	private Node buildNode(String id, String name, Entity entity, NodeType nt) {
		switch(nt) {
		case GENE: return Node.buildGeneNode(id, name, entity);
		case PROTEIN: return Node.buildProteinNode(id, name, entity);
		case CLUSTER: return Node.buildClusterNode(id, name, entity);
		}
		return null;
	}
	
	private EntityType toEntityType(String nodeTypeMappingPrefix, NodeType nt) {
		return  Enum.valueOf(EntityType.class, config.getString(nodeTypeMappingPrefix + nt.name()));
	}	

	private QueryPropertyArguments toPropertyArguemnts(String propertyPrefix, Entity entity)
	{
		return new QueryPropertyArguments(propertyPrefix, new String[]{entity.getId()});
	}	
	
	private QueryPropertyArguments toPropertyArguemnts(String propertyPrefix, List<Entity> entities)
	{		
		return new QueryPropertyArguments(propertyPrefix,
				entities != null 
						?(String[]) Entity.toEntityIds(entities).toArray(new String[0])
						:new String[]{}
				);
	}
	private QueryPropertyArguments toPropertyArguemnts(Dataset dataset)
	{
		return new QueryPropertyArguments(Term.Prefix_QueryIndex_DatasetId, new String[]{dataset.getId()});
	}
}