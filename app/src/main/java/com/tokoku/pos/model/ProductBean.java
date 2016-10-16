package com.tokoku.pos.model;

public class ProductBean extends BaseBean {

	private Long product_group_id;
	private String code;
	private String name;
	private String type;
	private String price_type;
	private Float price_1;
	private Float price_2;
	private Float price_3;
	private Float cost_price;
	private String pic_required;
	private String commision_type;
	private Float commision;
	private Float promo_price;
	private java.util.Date promo_start;
	private java.util.Date promo_end;
	private String quantity_type;
	private Float stock;
	private Float min_stock;
	private String status;

	public Long getProduct_group_id() {
		return product_group_id;
	}

	public void setProduct_group_id(Long product_group_id) {
		this.product_group_id = product_group_id;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getPrice_1() {
		return price_1;
	}

	public void setPrice_1(Float price_1) {
		this.price_1 = price_1;
	}

	public Float getPrice_2() {
		return price_2;
	}

	public void setPrice_2(Float price_2) {
		this.price_2 = price_2;
	}

	public Float getPrice_3() {
		return price_3;
	}

	public void setPrice_3(Float price_3) {
		this.price_3 = price_3;
	}

	public Float getCost_price() {
		return cost_price;
	}

	public void setCost_price(Float cost_price) {
		this.cost_price = cost_price;
	}

	public String getPic_required() {
		return pic_required;
	}

	public void setPic_required(String pic_required) {
		this.pic_required = pic_required;
	}

	public Float getCommision() {
		return commision;
	}

	public void setCommision(Float commision) {
		this.commision = commision;
	}

	public Float getPromo_price() {
		return promo_price;
	}

	public void setPromo_price(Float promo_price) {
		this.promo_price = promo_price;
	}

	public java.util.Date getPromo_start() {
		return promo_start;
	}

	public void setPromo_start(java.util.Date promo_start) {
		this.promo_start = promo_start;
	}

	public java.util.Date getPromo_end() {
		return promo_end;
	}

	public void setPromo_end(java.util.Date promo_end) {
		this.promo_end = promo_end;
	}
	
	public String getQuantity_type() {
		return quantity_type;
	}

	public void setQuantity_type(String quantity_type) {
		this.quantity_type = quantity_type;
	}

	public Float getStock() {
		return stock;
	}

	public void setStock(Float stock) {
		this.stock = stock;
	}

	public Float getMin_stock() {
		return min_stock;
	}

	public void setMin_stock(Float min_stock) {
		this.min_stock = min_stock;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrice_type() {
		return price_type;
	}

	public void setPrice_type(String price_type) {
		this.price_type = price_type;
	}

	public String getCommision_type() {
		return commision_type;
	}

	public void setCommision_type(String commision_type) {
		this.commision_type = commision_type;
	}
}
