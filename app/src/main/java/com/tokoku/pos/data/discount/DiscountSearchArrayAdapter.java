package com.tokoku.pos.data.discount;

import java.util.List;

import com.android.pos.dao.Discount;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;

import android.content.Context;

public class DiscountSearchArrayAdapter extends BaseSearchArrayAdapter<Discount> {
	
	public DiscountSearchArrayAdapter(Context context, List<Discount> discounts, Discount selectedDiscount, ItemActionListener<Discount> listener) {
		super(context, discounts, selectedDiscount, listener);
	}
	
	@Override
	public Long getItemId(Discount discount) {
		return discount.getId();
	}
	
	@Override
	public String getItemName(Discount discount) {
		return discount.getName();
	}
}