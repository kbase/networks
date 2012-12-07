package us.kbase.networks.adaptor.genericMySQL;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.NodeType;
import us.kbase.networks.core.Taxon;

public class GenericMySQLAdaptorTest {

	private static final String testConfig = "genericTest.config";
	private static final String testDataset = "genericTestDataset.json";
	private static Dataset dataset = null;
	private static ObjectMapper m = new ObjectMapper();
	private static JsonFactory jf = new JsonFactory();
	private Configuration config = null;
	private Adaptor adaptor = null;
	
	//@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Prepare test data set & configuration file
		Dataset dataset = new Dataset(
				"kb|netdataset.1", 
				"Dataset1", 
				"Description 1", 
				NetworkType.PROT_PROT_INTERACTION,
				DatasetSource.INTACT, 
				new Taxon("kb|g.3701"));
		dataset.addProperty("default.nodeType", NodeType.PROTEIN.toString());
		dataset.addProperty("default.edgeType", EdgeType.GENE_GENE.toString());
		dataset.addProperty("sql.findNeighbor.psIndex", "1");
		dataset.addProperty("sql.findNeighbor.rsIndex", "1");
		// permanent solution : not used now
		dataset.addProperty("sql.findNeighbor", "select B.protein from interaction_exchange A" 
				+ " join interaction_exchange B ON A.group_description = B.group_description" 
				+ " where A.protein <> B.protein and A.protein like ? ");
		dataset.addProperty("sql.findIntNetwork", "select B.protein from interaction_exchange A" 
				+ " join interaction_exchange B ON A.group_description = B.group_description" 
				+ " where A.protein <> B.protein and A.protein like ? ");
		dataset.addProperty("sql.findNeighbor.like", "yes");
		dataset.addProperty("sql.findIntNetwork.like", "yes");
		FileWriter fw = new FileWriter(testDataset);
		JsonGenerator jg = jf.createJsonGenerator(fw);
        jg.useDefaultPrettyPrinter();
        m.writeValue(jg, dataset);
        fw.close();

		Configuration config = new PropertiesConfiguration(); 
		config.addProperty("dataset.list", testDataset);
		config.addProperty("host", "140.221.92.108");
		config.addProperty("port", new Integer(3306));
		config.addProperty("db", "networks_pdev");
		config.addProperty("user", "queryEngine");
		config.addProperty("passwd", "4pm332PwhDH5Tend");
		
		//temporary solution
		config.addProperty("sql.findNeighbor", "select B.protein from interaction_exchange A" 
				+ " join interaction_exchange B ON A.group_description = B.group_description" 
				+ " where A.protein <> B.protein and A.protein like ? ");
		config.addProperty("sql.findIntNetwork", "select B.protein from interaction_exchange A" 
				+ " join interaction_exchange B ON A.group_description = B.group_description" 
				+ " where A.protein <> B.protein and A.protein like ? ");
		((FileConfiguration) config).save(testConfig);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//delete generated test config & json files
	}

	@Before
	public void setUp() throws Exception {
		config = new PropertiesConfiguration(testConfig);
		adaptor = new GenericMySQLAdaptor(config);
		dataset = adaptor.getDatasets().get(0);
//		FileReader fr = new FileReader(this.testDataset);
//		dataset = (Dataset) m.readValue(fr, Dataset.class);

//		config = new PropertiesConfiguration("plant-ppi.config");
//		adaptor = new GenericMySQLAdaptor(config);
//		FileReader fr = new FileReader("intact.json");
//		dataset = (Dataset) m.readValue(fr, Dataset.class);
	
	
	}

	@After
	public void tearDown() throws Exception {
		config = null;
	}

	@Test
	public void testGenericMySQLAdaptor() {
		adaptor = null;
		try {
			adaptor = new GenericMySQLAdaptor(config);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertNotNull("Successfully created adaptor", adaptor);
	}

	@Test
	public void testGetDatasets() {
		try {
			List<Dataset> dl = adaptor.getDatasets();
			assertEquals("Return one dataset?", dl.size(), 1);
			assertEquals("The same dataset?", dl.get(0), dataset); 
			assertEquals("Just id same?", dl.get(0).getId(), dataset.getId());
			NetworksUtil.printDatasets("", dl);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDatasetsNetworkType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetDatasetsDatasetSource() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetDatasetsTaxon() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetDatasetsNetworkTypeDatasetSourceTaxon() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testHasDataset() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testBuildNetworkDataset() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testBuildNetworkDatasetListOfEdgeType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetString() throws AdaptorException {

		// empty result test
		Network n1 = adaptor.buildFirstNeighborNetwork(dataset, "AT5G03240");
		assertTrue("Returned results", n1.getGraph().getVertexCount() == 1);

		// at least one result
		Network n2 = adaptor.buildFirstNeighborNetwork(dataset, "AT5G22340");
		assertTrue("Returned results", n2.getGraph().getVertexCount() > 1);	
	}

	@Test
	public void testBuildFirstNeighborNetworkDatasetStringListOfEdgeType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testBuildInternalNetworkDatasetListOfStringListOfEdgeType() {
		fail("Not yet implemented"); // TODO
	}

}
