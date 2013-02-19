package us.kbase.networks.adaptor.mak;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.*;
import edu.uci.ics.jung.graph.Graph;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Nov 27, 2012
 * Time: 4:55:35 PM
 */
public class MAKTest {

    static Adaptor adaptor = new us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory("mak.config").buildAdaptor();

    //	final String genomeId = "kb|g.0";
    final String genomeId = "kb|g.20848";//g.21765";

    //	final String queryGeneId = "kb|g.0.peg.10";
    final Entity queryGene = new Entity("kb|g.20848.CDS.578", EntityType.GENE);

    //	final List<String> queryGeneIds = Arrays.asList("kb|g.0.peg.10",      "kb|g.0.peg.1032",     "kb|g.0.peg.1002",
// "kb|g.0.peg.880", "kb|g.0.peg.847",      "kb|g.0.peg.843",      "kb|g.0.peg.1247");
    final List<Entity> queryGenes = Arrays.asList(
            new Entity("kb|g.20848.CDS.578", EntityType.GENE),
            new Entity("kb|g.20848.CDS.47", EntityType.GENE),
            new Entity("kb|g.20848.CDS.769", EntityType.GENE),
            new Entity("kb|g.20848.CDS.669", EntityType.GENE),
            new Entity("kb|g.20848.CDS.406", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1347", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1304", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1130", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1303", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1995", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1083", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1127", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1030", EntityType.GENE),
            new Entity("kb|g.20848.CDS.1732", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2281", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2680", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2473", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2290", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2498", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2369", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2176", EntityType.GENE),
            new Entity("kb|g.20848.CDS.2914", EntityType.GENE),
            new Entity("kb|g.20848.CDS.3647", EntityType.GENE),
            new Entity("kb|g.20848.CDS.3564", EntityType.GENE),
            new Entity("kb|g.20848.CDS.3769", EntityType.GENE),
            new Entity("kb|g.20848.CDS.3862", EntityType.GENE)
    );

    final List<Entity> queryMixed = Arrays.asList(
               new Entity("kb|g.20848.CDS.998", EntityType.GENE),
               new Entity("kb|bicluster.47", EntityType.BICLUSTER),
               new Entity("kb|bicluster.170", EntityType.BICLUSTER),
               new Entity("kb|bicluster.444", EntityType.BICLUSTER),
               new Entity("kb|bicluster.718", EntityType.BICLUSTER),
               new Entity("kb|bicluster.821", EntityType.BICLUSTER),
               new Entity("kb|bicluster.980", EntityType.BICLUSTER),
               new Entity("kb|bicluster.1093", EntityType.BICLUSTER),
               new Entity("kb|bicluster.1351", EntityType.BICLUSTER),
               new Entity("kb|bicluster.1379", EntityType.BICLUSTER),
               new Entity("kb|bicluster.1652", EntityType.BICLUSTER),
               new Entity("kb|bicluster.1820", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3091", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3125", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3144", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3148", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3163", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3175", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3194", EntityType.BICLUSTER),
               new Entity("kb|bicluster.3235", EntityType.BICLUSTER)
       );

    @Test
    public void hasAdaptor() throws Exception {
        assertNotNull(adaptor);
    }

    @Test
    public void shouldReturnListOfDatasets() throws AdaptorException {
        assertNotNull("should return a list of Datasets", adaptor.getDatasets());
    }

    @Test
    public void shouldReturnDataSetForSOMR1() throws AdaptorException {
        Taxon taxid = new Taxon(genomeId);
        List<Dataset> datasets = adaptor.getDatasets(NetworkType.CO_EXPRESSION, DatasetSource.MAK_BICLUSTER, taxid);
        assertNotNull("should return a list of Datasets", datasets);
        assertTrue("list should contain at least one dataset", datasets.size() > 0);
        for (Dataset dataset : datasets) {
            assertTrue("dataset taxons should contain " + genomeId, dataset.getTaxons().contains(taxid));
        }
        NetworksUtil.printDatasets("", datasets);
    }

    @Test
    public void shouldReturnDataSetForSOMR1Gene() throws AdaptorException {
        List<Dataset> datasets = adaptor.getDatasets(queryGene);
        assertNotNull("should return a list of Datasets", datasets);
        assertTrue("list should contain at least one dataset", datasets.size() > 0);
        for (Dataset dataset : datasets) {
            assertTrue("dataset taxons should contain SOMR1", dataset.getTaxons().contains(new Taxon(genomeId)));
        }

    }

    @Test
    public void shouldReturnNetworkForSOMR1Gene() throws AdaptorException {
        int testnodes = 8;
        int testedges = 7;
        Taxon taxid = new Taxon(genomeId);
        List<Dataset> datasets = adaptor.getDatasets(NetworkType.CO_EXPRESSION, DatasetSource.MAK_BICLUSTER, taxid);
        assertNotNull("should return a list of Datasets", datasets);
        assertTrue("list should contain at least one dataset", datasets.size() > 0);
        Network network = adaptor.buildFirstNeighborNetwork(datasets.get(0), queryGene);
        assertNotNull("Should get a network back", network);
        Graph<Node, Edge> g = network.getGraph();
        assertNotNull("Network should have graph", g);
        assertEquals("Graph should have " + testnodes + " edges", testedges, g.getEdgeCount());
        assertEquals("Graph should have " + testedges + " nodes", testnodes, g.getVertexCount());
    }

    @Test
    public void shouldReturnNetworkForSOMR1Genes() throws AdaptorException {
        Taxon taxid = new Taxon(genomeId);
        List<Dataset> datasets = adaptor.getDatasets(NetworkType.CO_EXPRESSION, DatasetSource.MAK_BICLUSTER, taxid);
        assertNotNull("should return a list of Datasets for " + genomeId, datasets);
        assertTrue("list should contain at least one dataset for " + genomeId, datasets.size() > 0);

        System.out.println("shouldReturnNetworkForSOMR1Genes " + datasets.size() + "\t" + datasets.get(0));
        Dataset dataset = (Dataset) datasets.get(0);
        NetworkType networkType = (NetworkType) dataset.getNetworkType();
        System.out.println("shouldReturnNetworkForSOMR1Genes " + datasets.size() + "\tdataset " + dataset.getDescription()
                + "\t" + dataset.getId() + "\t" + dataset.getName());
        System.out.println("shouldReturnNetworkForSOMR1Genes networkType " +
                networkType.getDesccription() + "\t" + networkType.getId() + "\t" + networkType.getName());

        Network network = adaptor.buildInternalNetwork(datasets.get(0), queryGenes, Arrays.asList(EdgeType.GENE_GENE));//adaptor.buildInternalNetwork(datasets.get(0), queryGenes);
        assertNotNull("Should get a network back", network);
        Graph<Node, Edge> g = network.getGraph();
        assertNotNull("Network should have graph", g);
        assertEquals("Graph should have X edges", 182, g.getEdgeCount());
        
        // TODO:
        //assertEquals("Graph should have " + queryGenes.size() + " nodes", queryGenes.size(), g.getVertexCount());
        assertEquals("Graph should have " + queryGenes.size() + " nodes", 22, g.getVertexCount());
    }

    @Test
    public void shouldReturnNetworkForSOMR1GenesandBiclusters() throws AdaptorException {
        Taxon taxid = new Taxon(genomeId);
        List<Dataset> datasets = adaptor.getDatasets(NetworkType.CO_EXPRESSION, DatasetSource.MAK_BICLUSTER, taxid);
        assertNotNull("should return a list of Datasets for " + genomeId, datasets);
        assertTrue("list should contain at least one dataset for " + genomeId, datasets.size() > 0);

        System.out.println("shouldReturnNetworkForSOMR1GenesandBiclusters " + datasets.size() + "\t" + datasets.get(0));
        Dataset dataset = (Dataset) datasets.get(0);
        NetworkType networkType = (NetworkType) dataset.getNetworkType();
        System.out.println("shouldReturnNetworkForSOMR1GenesandBiclusters " + datasets.size() + "\tdataset " + dataset.getDescription()
                + "\t" + dataset.getId() + "\t" + dataset.getName());
        System.out.println("shouldReturnNetworkForSOMR1GenesandBiclusters networkType " +
                networkType.getDesccription() + "\t" + networkType.getId() + "\t" + networkType.getName());

        Network network = adaptor.buildInternalNetwork(datasets.get(0), queryMixed);//adaptor.buildInternalNetwork(datasets.get(0), queryGenes);
        assertNotNull("Should get a network back", network);
        Graph<Node, Edge> g = network.getGraph();
        assertNotNull("Network should have graph", g);
        
        //TODO
        //assertEquals("Graph should have X edges", 19, g.getEdgeCount());
        //assertEquals("Graph should have " + queryMixed.size() + " nodes", queryMixed.size(), g.getVertexCount());
    }
}
