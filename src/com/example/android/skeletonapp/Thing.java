package com.example.android.skeletonapp;

import java.io.Serializable;
import java.util.Date;

public class Thing implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long idNum;
	private String idStr;
	
	private String childA;
	private String childB;
	
	private Boolean fieldA;
	private Integer fieldB;
	private Double fieldC;
	private String name;
	private Date date;
	
	
	
	public Long getIdNum() {
		return idNum;
	}
	public void setIdNum(Long idNum) {
		this.idNum = idNum;
	}
	public String getIdStr() {
		return idStr;
	}
	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}
	public String getChildA() {
		return childA;
	}
	public void setChildA(String childA) {
		this.childA = childA;
	}
	public String getChildB() {
		return childB;
	}
	public void setChildB(String childB) {
		this.childB = childB;
	}
	public Boolean getFieldA() {
		return fieldA;
	}
	public void setFieldA(Boolean fieldA) {
		this.fieldA = fieldA;
	}
	public Integer getFieldB() {
		return fieldB;
	}
	public void setFieldB(Integer fieldB) {
		this.fieldB = fieldB;
	}
	public Double getFieldC() {
		return fieldC;
	}
	public void setFieldC(Double fieldC) {
		this.fieldC = fieldC;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
