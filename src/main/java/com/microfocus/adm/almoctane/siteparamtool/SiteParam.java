package com.microfocus.adm.almoctane.siteparamtool;

import java.util.Objects;

public class SiteParam {
	
	private String paramName;
	private String paramValue;
	private boolean isDirty;
	
	public SiteParam() {}
	
	public SiteParam(String paramName, String paramValue) {
		this.paramName = paramName;
		this.paramValue = paramValue;
	}
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean dirty) {
		isDirty = dirty;
	}

	@Override
	public String toString() {
		return "SiteParam [paramName=" + paramName + ", paramValue=" + paramValue + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SiteParam siteParam = (SiteParam) o;
		return Objects.equals(paramName, siteParam.paramName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(paramName);
	}
}