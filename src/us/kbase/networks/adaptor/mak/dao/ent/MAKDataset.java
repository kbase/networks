package us.kbase.networks.adaptor.mak.dao.ent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "Dataset")
public class MAKDataset {
	@Id
	@GeneratedValue
	@Column(name = "datasetId")
	private Integer id;
	private String name;
	private String description;
	private String kbaseId;
	private String genomeKBaseId;
	
	
	@OneToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})	
	@JoinColumns({
		@JoinColumn(name = "datasetId")
	})		
	private List<MAKBicluster> biclusters = new ArrayList<MAKBicluster>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKbaseId() {
		return kbaseId;
	}

	public void setKbaseId(String kbaseId) {
		this.kbaseId = kbaseId;
	}

	public List<MAKBicluster> getBiclusters() {
		return biclusters;
	}

	public void setBiclusters(List<MAKBicluster> biclusters) {
		this.biclusters = biclusters;
	}

	public void setGenomeKBaseId(String genomeKBaseId) {
		this.genomeKBaseId = genomeKBaseId;
	}

	public String getGenomeKBaseId() {
		return genomeKBaseId;
	}	

}

