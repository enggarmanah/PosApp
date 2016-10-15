package com.tokoku.pos.model;

import android.view.View;

public class FormFieldBean {
	
	View field;
	int label;
	
	public FormFieldBean(View field, int label) {
		
		this.field = field;
		this.label = label;
	}
	
	public View getField() {
		return field;
	}

	public void setField(View field) {
		this.field = field;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
}
