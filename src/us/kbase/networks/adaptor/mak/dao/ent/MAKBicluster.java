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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;


@Entity
@Table(name = "Bicluster")
public class MAKBicluster {

	@Id
	@GeneratedValue
	@Column(name = "biclusterId")
	private Integer id;
	private String name;
	private String kbaseId;
	
	@Column(columnDefinition="Text")
	private String conditionIds; 
	
	private String moveType;
	private Float preCriterion;
	private Float fullCrit;
	private Float exprMeanCrit;
	private Double exprRegCrit;
	private Float expMean;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "datasetId")			
	private MAKDataset dataset;
	
	@OneToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})	
	@JoinColumns({
		@JoinColumn(name = "biclusterId")
	})		
	private List<MAKGene> genes = new ArrayList<MAKGene>();	

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

	public void setGenes(List<MAKGene> genes) {
		this.genes = genes;
	}

	public List<MAKGene> getGenes() {
		return genes;
	}

	public String getConditionIds() {
		return conditionIds;
	}

	public void setConditionIds(String conditionIds) {
		this.conditionIds = conditionIds;
	}

	public String getMoveType() {
		return moveType;
	}

	public void setMoveType(String moveType) {
		this.moveType = moveType;
	}

	public Float getPreCriterion() {
		return preCriterion;
	}

	public void setPreCriterion(Float preCriterion) {
		this.preCriterion = preCriterion;
	}

	public Float getFullCrit() {
		return fullCrit;
	}

	public void setFullCrit(Float fullCrit) {
		this.fullCrit = fullCrit;
	}

	public Float getExprMeanCrit() {
		return exprMeanCrit;
	}

	public void setExprMeanCrit(Float exprMeanCrit) {
		this.exprMeanCrit = exprMeanCrit;
	}

	public Double getExprRegCrit() {
		return exprRegCrit;
	}

	public void setExprRegCrit(Double exprRegCrit) {
		this.exprRegCrit = exprRegCrit;
	}

	public Float getExpMean() {
		return expMean;
	}

	public void setExpMean(Float expMean) {
		this.expMean = expMean;
	}
	
}
