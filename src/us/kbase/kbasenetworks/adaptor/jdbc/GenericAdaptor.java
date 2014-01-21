package us.kbase.kbasenetworks.adaptor.jdbc;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.DatasetSource;
import us.kbase.kbasenetworks.core.Edge;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.EntityType;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.NetworkType;
import us.kbase.kbasenetworks.core.Node;
import us.kbase.kbasenetworks.core.NodeType;
import us.kbase.kbasenetworks.core.Taxon;
import us.kbase.kbasenetworks.adaptor.AbstractAdaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.IdGenerator;

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
			}
			finally{
				try{ rs.close(); } catch(Exception e){}
				try{ pst.close(); } catch(Exception e){}
				try{ connection.close(); } catch(Exception e){}
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
			}finally{
				try{ rs.close(); } catch(Exception e){}
				try{ pst.close(); } catch(Exception e){}
				try{ connection.close(); } catch(Exception e){}
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

		List<String> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildNetwork(dataset, edgeTypes);
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<String> queryEdgeTypes)
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
		
		List<String> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildFirstNeighborNetwork(dataset, entity, edgeTypes);
	}
	
	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, final Entity entity,
			List<String> queryEdgeTypes) throws AdaptorException {

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

		List<String> edgeTypes = getEdgeTypes(Term.DefaultEdgeTypes);
		if(edgeTypes.size() == 0) return null;
		
		return buildInternalNetwork(dataset, entities, edgeTypes);
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<Entity> entities,
			List<String> queryEdgeTypes) throws AdaptorException {
		
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
			List<String> queryEdgeTypes,
			QueryPropertyArguments ... propertiesArguments) throws AdaptorException
	{
		if( !hasDataset(dataset.getId())) return null;

		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(
				IdGenerator.Network.nextId(),
				dataset.getId() + ": " + methodProperties.methodName, 
				graph);

		
		List<String> edgeTypes = getEdgeTypes(Term.SupportedEdgeTypes, queryEdgeTypes);
		if(edgeTypes.isEmpty()) return network;

		try {
			Hashtable<String,Node> nodeId2NodeHash = new Hashtable<String,Node>(); 
			for(String et : edgeTypes) {
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
		String edgeType;
		NodeType nt1;
		NodeType nt2;
		EntityType et1;
		EntityType et2;
		
		private MethodProperties(String methodName) throws AdaptorException
		{
			this.methodName = methodName;
		}
		
		public void prepareEdgeType(String edgeType) throws AdaptorException{
			this.edgeType = edgeType;
			assertEdgeTypeMapping();
			this.nt1 = NodeType.nodeType1(edgeType);
			this.nt2 = NodeType.nodeType2(edgeType);
			this.et1 = toEntityType(Term.Node1_Type_Mapping_Prefix, nt1);
			this.et2 = toEntityType(Term.Node2_Type_Mapping_Prefix, nt2);
		}
		
		public abstract String getPropertySuffix();
		
		private void assertEdgeTypeMapping() throws AdaptorException {
			
			if( !config.containsKey(Term.Node1_Type_Mapping_Prefix + NodeType.nodeType1(edgeType))  || 
				!config.containsKey(Term.Node2_Type_Mapping_Prefix + NodeType.nodeType2(edgeType)) 	)
				
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
			
				int edgeConfidenceIdx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_Confidence, propertySuffix);
				int edgeWeightIdx = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_Weight, propertySuffix);
				int edgeNameIndex = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EdgeName, propertySuffix);
				int directedIndex = getResultSetPropertyIndex(Term.Prefix_ResultsetIndex_EdgeDirected, propertySuffix);
			
				String[] node1FieldNames = getResultSetFieldNames(Term.Prefix_ResultsetName_NodeId1, propertySuffix);
				String[] node2FieldNames = getResultSetFieldNames(Term.Prefix_ResultsetName_NodeId2, propertySuffix);
				String[] edgeFieldNames  = getResultSetFieldNames(Term.Prefix_ResultsetName_Edge, propertySuffix);
			
				while(rs.next()) {
					Node node1 = buildNode(rs, graph, nodeId2NodeHash, entity1Idx, nodeName1Idx, methodProperties.et1, methodProperties.nt1, node1FieldNames);
					Node node2 = buildNode(rs, graph, nodeId2NodeHash, entity2Idx, nodeName2Idx, methodProperties.et2, methodProperties.nt2, node2FieldNames);
					
					Edge edge  = buildEdge(rs, dataset, edgeNameIndex, edgeWeightIdx, edgeConfidenceIdx, node1, node2, edgeFieldNames);
				
					if(directedIndex > -1)
					{
						final int directedValue = rs.getInt(directedIndex);
						if(directedValue > 0)
						{
							graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						} else if(directedValue < 0) 
						{
							graph.addEdge(edge, node2, node1, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
						}
						else{
							graph.addEdge(edge, node2, node1, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
						}
					}
					else{
						graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
					}				
				}
			
			} finally{
				try{ rs.close(); } catch(Exception e){}
				try{ pst.close(); } catch(Exception e){}
				try{ connection.close(); } catch(Exception e){}
			}
		}
		catch(Exception e)
		{
			throw new AdaptorException(e.getMessage(), e);
		}
	}
	
	private Edge buildEdge(ResultSet rs, Dataset dataset, int edgeNameIndex, int edgeWeightIdx, int edgeConfidenceIdx, Node node1,
			Node node2, String[] edgeFieldNames) throws SQLException {
		
		String edgeName = node1.getId() + ":" + node2.getId();
		if(edgeNameIndex > -1)
		{
			try{			
				edgeName = rs.getString(edgeNameIndex);
			}catch(Exception e){}
		}
		
		Edge edge = new Edge(IdGenerator.Edge.nextId(), edgeName, dataset);
		if(edgeWeightIdx > -1) {
			try{
				edge.setStrength(rs.getFloat(edgeWeightIdx));
			}catch(Exception e){}
		}
		if(edgeConfidenceIdx > -1)
		{
			try{
				edge.setConfidence(rs.getFloat(edgeConfidenceIdx));
			}catch(Exception e){}
		}
		
		// Set other edge properties
		for(String fieldName: edgeFieldNames)
		{
			try{
				edge.addProperty(fieldName, rs.getString(fieldName));
			}catch(Exception e){}
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

	private List<String> getEdgeTypes(String edgeTypesPrefix)
	{
		List<String> edgeTypes = new ArrayList<String>();
		
		String edgeTypesValue = config.getString(edgeTypesPrefix);
		if(edgeTypesValue != null && edgeTypesValue.trim().length() > 0)
		{
			for(String edgeTypeStr:  edgeTypesValue.split(":") )
			{
				edgeTypes.add(edgeTypeStr);
			}
		}
		return edgeTypes;	
	}	
	
	private List<String> getEdgeTypes(String edgeTypesPrefix, List<String> queryEdgeTypes)
	{
		List<String> dsEdgeTypes = getEdgeTypes(edgeTypesPrefix);
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
			try{
				nodeName = rs.getString(nodeNameIdx);
			}catch(Exception e){}
		}
		
		Node node = nodeId2NodeHash.get(entityId);
		if(node == null)
		{
			node = buildNode(IdGenerator.Node.nextId(), nodeName,  new Entity(entityId, et), nt); 
			nodeId2NodeHash.put(entityId, node);
			
			// Set additional node properties
			for(String fieldName: nodeFieldNames)
			{
				try{
					// For nodes, the last symbol in propertyName is either 1 or 2 => we need to remove it
					char lastSymbol = fieldName.charAt(fieldName.length() - 1);
					String propertyName = Character.isDigit(lastSymbol) 
						? fieldName.substring(0, fieldName.length() - 1)
						: fieldName;
					node.addProperty(propertyName, rs.getString(fieldName));
				}catch(Exception e){}
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
		case SUBSYSTEM: return Node.buildSubsystemNode(id, name, entity);
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
