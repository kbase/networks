package us.kbase.networks.adaptor.mak.dao.hibernate;

import java.util.List;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import us.kbase.networks.adaptor.mak.dao.ent.MAKBicluster;
import us.kbase.networks.adaptor.mak.dao.ent.MAKDataset;
import us.kbase.networks.adaptor.mak.dao.ent.MAKGene;


public class DataProvider {
	Session session;	
	 
	public DataProvider()
	{
		session = HibernateUtil.getSession();
	}
	
	
	public Session getSession(){
		return session;
	}
	
	public void close()
	{
		session.close();
	}
	
	@SuppressWarnings("unchecked")
	public List<MAKDataset> getDatasets() {
		return session.createCriteria(MAKDataset.class).list();		
	}
	
	@SuppressWarnings("unchecked")
	public List<MAKDataset> getDatasetsByGenomeKBaseId(String genomeKBaseId) {
		return 
			session.createCriteria(MAKDataset.class)
			.add( Restrictions.eq("genomeKBaseId", genomeKBaseId) )
			.list();		
	}	
	
	@SuppressWarnings("unchecked")
	public List<MAKGene> getGenes(int datasetId, List<String> kbaseGeneIds){
		return 
			session.createCriteria(MAKGene.class)
			.add( Restrictions.in("kbaseId", kbaseGeneIds) )
			.createAlias("dataset", "ds")
			.add( Restrictions.eq("ds.id", datasetId) )
			.list();	
	}

	
	@SuppressWarnings("unchecked")
	public MAKGene getGene(int datasetId, String geneKBaseId) {
		List<MAKGene> genes = 
			session.createCriteria(MAKGene.class)
				.add( Restrictions.eq("kbaseId", geneKBaseId) )
				.createAlias("dataset", "ds")
				.add( Restrictions.eq("ds.id", datasetId) )
				.list();
		
		return genes.size() > 0 ? genes.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	public MAKBicluster getBicluster(String clusterKBaseId) {
		List<MAKBicluster> clusters = 
			session.createCriteria(MAKBicluster.class)
				.add( Restrictions.eq("kbaseId", clusterKBaseId) )
				.list();
		
		return clusters.size() > 0 ? clusters.get(0) : null;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MAKBicluster> getBiclusters(int datasetId, String geneKBaseId) {
		List<MAKGene> genes = 
			session.createCriteria(MAKGene.class)
				.add( Restrictions.eq("kbaseId", geneKBaseId) )
				.createAlias("dataset", "ds")
				.add( Restrictions.eq("ds.id", datasetId) )
				.list();
		
		List<MAKBicluster> clusters = new Vector<MAKBicluster>();
		for(MAKGene gene: genes)
		{
			clusters.add(gene.getBicluster());
		}
		
		return clusters;
	}

}
