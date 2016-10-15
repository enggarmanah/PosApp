package com.tokoku.pos.data.product;

import java.util.List;

import com.android.pos.dao.Product;
import com.tokoku.pos.base.adapter.BaseSearchArrayAdapter;
import com.tokoku.pos.util.CommonUtil;

import android.content.Context;

public class ProductSearchArrayAdapter extends BaseSearchArrayAdapter<Product> {
	
	public ProductSearchArrayAdapter(Context context, List<Product> products, Product selectedProduct, ItemActionListener<Product> listener) {
		super(context, products, selectedProduct, listener);
	}
	
	@Override
	public Long getItemId(Product product) {
		return product.getId();
	}
	
	@Override
	public String getItemName(Product product) {
		return product.getName();
	}
	
	@Override
	public String getItemInfo(Product product) {
		
		if (!CommonUtil.isEmpty(product.getCode())) {
			return product.getCode();
		} else {
			return CommonUtil.formatCurrency(product.getPrice1());
		}
	}
}