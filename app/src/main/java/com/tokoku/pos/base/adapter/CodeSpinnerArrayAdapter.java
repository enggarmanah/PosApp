package com.tokoku.pos.base.adapter;

import android.content.Context;
import android.widget.Spinner;

import com.tokoku.pos.CodeBean;

public class CodeSpinnerArrayAdapter extends BaseSpinnerArrayAdapter<CodeBean>{
	
	Spinner spinner;
	
	public CodeSpinnerArrayAdapter(Spinner spinner, Context context, CodeBean[] options) {
		super(context, options);
		this.spinner = spinner;
	}
	
	public CodeSpinnerArrayAdapter(Spinner spinner, Context context, CodeBean[] options, int listNormalLayout, int listSelectedLayout, int itemSelected) {
		super(context, options, listNormalLayout, listSelectedLayout, itemSelected);
		this.spinner = spinner;
	}
	
	@Override
	protected String getSelectedValue() {
		
		if (spinner.getSelectedItem() != null) {
			return ((CodeBean) spinner.getSelectedItem()).getCode();
		} else {
			return null;
		}
	}
	
	@Override
	protected String getLabel1(CodeBean codeBean) {
		
		return codeBean.getLabel();
	}
	
	@Override
	protected String getValue(CodeBean codeBean) {
		
		return codeBean.getCode();
	}
}
