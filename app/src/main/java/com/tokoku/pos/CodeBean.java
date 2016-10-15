package com.tokoku.pos;

public class CodeBean {

	private String code;
	private String label;
	private String order;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public static String getNvlCode(CodeBean bean) {
		
		if (bean == null) {
			return null;
		} else {
			return bean.getCode();
		}
	}
}
