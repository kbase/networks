package us.kbase.networks.core;

public class Taxon {
	private String genomeId;

	public Taxon(String genomeId) {
		super();
		this.genomeId = genomeId;
	}

	@Override
	public boolean equals(Object taxon)
	{
		if(taxon instanceof Taxon)
		{
			return ((Taxon) taxon).getGenomeId().equals(genomeId);
		}
		return false;
	}		
	
	public String getGenomeId() {
		return genomeId;
	}

}
