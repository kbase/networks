package us.kbase.networks.core;

public final class Taxon {
	protected String genomeId;

	public Taxon(String genomeId) {
		super();
		this.genomeId = genomeId;
	}	
	
	public String getGenomeId() {
		return genomeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((genomeId == null) ? 0 : genomeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Taxon other = (Taxon) obj;
		if (genomeId == null) {
			if (other.genomeId != null)
				return false;
		} else if (!genomeId.equals(other.genomeId))
			return false;
		return true;
	}

}
