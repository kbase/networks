package us.kbase.networks.adaptor.mak.dao.ent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "Gene")
public class MAKGene {

	@Id
	@GeneratedValue
	@Column(name = "geneId")
	private Integer id;
	private String name;
	private String locusTag;
	private Integer moId;
	private String kbaseId;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "datasetId")			
	private MAKDataset dataset;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "biclusterId")			
	private MAKBicluster bicluster;	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocusTag() {
		return locusTag;
	}

	public void setLocusTag(String locusTag) {
		this.locusTag = locusTag;
	}

	public String getKbaseId() {
		return kbaseId;
	}

	public void setKbaseId(String kbaseId) {
		this.kbaseId = kbaseId;
	}

	public MAKDataset getDataset() {
		return dataset;
	}

	public void setDataset(MAKDataset dataset) {
		this.dataset = dataset;
	}

	public MAKBicluster getBicluster() {
		return bicluster;
	}

	public void setBicluster(MAKBicluster bicluster) {
		this.bicluster = bicluster;
	}

	public void setMoId(Integer moId) {
		this.moId = moId;
	}

	public Integer getMoId() {
		return moId;
	}	

	
	
}
