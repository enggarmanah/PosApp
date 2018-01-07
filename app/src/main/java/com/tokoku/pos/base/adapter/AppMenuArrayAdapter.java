package com.tokoku.pos.base.adapter;

import java.util.List;

import com.tokoku.pos.R;
import com.tokoku.pos.Config;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.CommonUtil;
import com.tokoku.pos.util.MerchantUtil;
import com.tokoku.pos.util.UserUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppMenuArrayAdapter extends ArrayAdapter<String> {

	private Context context;
	private List<String> menus;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public String getSelectedMenu();
	}

	class ViewHolder {
		
		ImageView menuImage;
		TextView menuText;
		TextView countText;
	}

	public AppMenuArrayAdapter(Context context, List<String> menus, ItemActionListener listener) {

		super(context, R.layout.app_menu_item, menus);
		
		this.context = context;
		this.menus = menus;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final String menu = menus.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		ImageView menuImage = null;
		TextView menuText = null;
		TextView countText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.app_menu_item, parent, false);
			
			menuImage = (ImageView) rowView.findViewById(R.id.menuImage);
			menuText = (TextView) rowView.findViewById(R.id.menuText);
			countText = (TextView) rowView.findViewById(R.id.countText);
			
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.menuImage = menuImage;
			viewHolder.menuText = menuText;
			viewHolder.countText = countText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			menuImage = viewHolder.menuImage;
			menuText = viewHolder.menuText;
			countText = viewHolder.countText;
		}
		
		boolean isSubMenu = false;
		
		if (context.getString(R.string.menu_user).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle_black));
		} else if (context.getString(R.string.menu_cashier).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_home_black));
		} else if (context.getString(R.string.menu_waitress).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_home_black));
		} else if (context.getString(R.string.menu_order).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_content_paste_black));
		} else if (context.getString(R.string.menu_report).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_assessment_black));
		} else if (context.getString(R.string.menu_favorite).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_outline_black));
		} else if (context.getString(R.string.menu_bills).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_local_atm_black));
		} else if (context.getString(R.string.menu_cashflow).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_forward_back_black));
		} else if (context.getString(R.string.menu_inventory).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_view_list_black));
		} else if (UserUtil.isRoot() && context.getString(R.string.menu_merchant).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_view_list_black));
		} else if (context.getString(R.string.menu_customer).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person_black));
		} else if (context.getString(R.string.menu_user_access).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_security_black));
		} else if (context.getString(R.string.menu_sync).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cached_black));
		} else if (context.getString(R.string.menu_data_management).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_turned_in_not_black));
		} else if (context.getString(R.string.menu_printer).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_print_black));
		} else if (context.getString(R.string.menu_reupload).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cached_black));
		} else if (context.getString(R.string.menu_change_password).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vpn_key_black));
		} else if (context.getString(R.string.menu_help).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_chat_bubble_outline_black));
		} else if (context.getString(R.string.menu_guide).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_info_outline_black));
		} else if (context.getString(R.string.menu_exit).equals(menu)) {
			menuImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_exit_to_app_black));
		} else {
			menuImage.setImageDrawable(null);
			isSubMenu = true;
		}
		
		if (isSubMenu) {
			menuText.setTextColor(context.getResources().getColor(R.color.text_medium));
		} else {
			menuText.setTextColor(context.getResources().getColor(R.color.text_dark));
		}
		
		if (context.getString(R.string.menu_user).equals(menu)) {
			
			if (UserUtil.getUser() != null) {
				menuText.setText(UserUtil.getUser().getName());
			}
		} else {
			menuText.setText(menu);
		}
		
		if (context.getString(R.string.menu_report).equals(menu)) {
			
			countText.setVisibility(View.VISIBLE);
			
			if (!Config.isMenuReportExpanded()) {
				
				Integer count = 0;
				
				if (UserUtil.isUserHasAccess(Constant.ACCESS_REPORT_INVENTORY)) {
					count += MerchantUtil.getBelowStockLimitProductCount();
				}
				
				if (UserUtil.isUserHasAccess(Constant.ACCESS_REPORT_BILLS)) {
					count += MerchantUtil.getPastDueBillsCount();
				}
				
				if (count > 99) {
					countText.setText("++");
				} else if (count > 0) {
					countText.setText(CommonUtil.formatNumber(count));
				} else {
					countText.setVisibility(View.GONE);
				}
			} else {
				countText.setVisibility(View.GONE);
			}
			
		} else if (context.getString(R.string.menu_report_inventory).equals(menu) ||
				context.getString(R.string.menu_report_bills).equals(menu)) {
			
			countText.setVisibility(View.VISIBLE);
			
			Integer count = 0;
			
			if (context.getString(R.string.menu_report_inventory).equals(menu)) {
				count = MerchantUtil.getBelowStockLimitProductCount();
			} else if (context.getString(R.string.menu_report_bills).equals(menu)) {
				count = MerchantUtil.getPastDueBillsCount();
			}
			
			if (count > 99) {
				countText.setText("++");
			} else if (count > 0) {
				countText.setText(CommonUtil.formatNumber(count));
			} else {
				countText.setVisibility(View.GONE);
			}
			
		} else {
			countText.setVisibility(View.GONE);
		}
		
		String selectedMenu = mCallback.getSelectedMenu();
		
		if (selectedMenu != null && selectedMenu.equals(menu)) {
			
			menuText.setTextColor(context.getResources().getColor(R.color.text_dark));
			//rowView.setBackgroundColor(context.getResources().getColor(R.color.menu_selected));
			rowView.setBackground(context.getResources().getDrawable(R.drawable.bg_menu_selected));
			
		} else {
			
			if (isSubMenu) {
				menuText.setTextColor(context.getResources().getColor(R.color.text_darkmed));
				//rowView.setBackgroundColor(context.getResources().getColor(R.color.menu_sub));
				rowView.setBackground(context.getResources().getDrawable(R.drawable.bg_menu_sub));
			} else {
				menuText.setTextColor(context.getResources().getColor(R.color.text_dark));
				//rowView.setBackgroundColor(context.getResources().getColor(R.color.menu_normal));
				rowView.setBackground(context.getResources().getDrawable(R.drawable.bg_menu));
			}
		}

		return rowView;
	}
}