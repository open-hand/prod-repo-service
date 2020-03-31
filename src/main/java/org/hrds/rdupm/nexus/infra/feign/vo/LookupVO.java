package org.hrds.rdupm.nexus.infra.feign.vo;

/**
 * 快码VO
 * @author weisen.yang@hand-china.com 2020/3/31
 */
public class LookupVO {
	private Long id;
	private Integer displayOrder;
	private String value;
	private String meaning;

	public Long getId() {
		return id;
	}

	public LookupVO setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public LookupVO setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
		return this;
	}

	public String getValue() {
		return value;
	}

	public LookupVO setValue(String value) {
		this.value = value;
		return this;
	}

	public String getMeaning() {
		return meaning;
	}

	public LookupVO setMeaning(String meaning) {
		this.meaning = meaning;
		return this;
	}
}
