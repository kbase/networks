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
	
	public boolean hasDataset(String kbaseId) {
		List<MAKDataset> datasets = 
				session.createCriteria(MAKDataset.class)
				.add(Restrictions.eq("kbaseId", kbaseId))
				.list();
		
		return datasets.size() > 0;
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
	public List<MAKGene> getGenes(String datasetKBaseId, List<String> kbaseGeneIds){
		return 
			session.createCriteria(MAKGene.class)
			.add( Restrictions.in("kbaseId", kbaseGeneIds) )
			.createAlias("dataset", "ds")
			.add( Restrictions.eq("ds.kbaseId", datasetKBaseId) )
			.list();	
	}

	
	public MAKGene getGene(String datasetKBaseId, String geneKBaseId) {
		List<MAKGene> genes = 
			session.createCriteria(MAKGene.class)
				.add( Restrictions.eq("kbaseId", geneKBaseId) )
				.createAlias("dataset", "ds")
				.add( Restrictions.eq("ds.kbaseId", datasetKBaseId) )
				.list();
		
		return genes.size() > 0 ? genes.get(0) : null;
	}


	public List<MAKBicluster> getBiclusters(String datasetKBaseId, String geneKBaseId) {
		List<MAKGene> genes = 
			session.createCriteria(MAKGene.class)
				.add( Restrictions.eq("kbaseId", geneKBaseId) )
				.createAlias("dataset", "ds")
				.add( Restrictions.eq("ds.kbaseId", datasetKBaseId) )
				.list();
		
		List<MAKBicluster> clusters = new Vector<MAKBicluster>();
		for(MAKGene gene: genes)
		{
			clusters.add(gene.getBicluster());
		}
		
		return clusters;
	}

}
