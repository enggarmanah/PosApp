package com.tokoku.pos.base.listener;

public interface BaseItemListener<T> {
	
	public void displaySearch();
	
	public void addItem();
	
	public void updateItem(T item);
	
	public void deleteItem(T item);
	
	public void onAddCompleted();
	
	public void onUpdateCompleted();
	
	public void onItemUnselected();
	
	public void onDeleteCompleted();
	
	public void onSelectEmployee(boolean isMandatory);
	
	public void onSelectProduct(boolean isMandatory);
	
	public void onSelectProductGroup(boolean isMandatory);
	
	public void onSelectSupplier(boolean isMandatory);

	public void onSelectDelivery(boolean isMandatory);
	
	public void onSelectBill(boolean isMandatory);
	
	public void onSelectTransaction(boolean isMandatory);
	
	public void onSelectLocale(boolean isMandatory);
	
	public void disableEditMenu();
}
