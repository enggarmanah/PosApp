package com.tokoku.pos.popup.search;

import java.util.List;
import java.util.Locale;

import com.tokoku.pos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocaleArrayAdapter extends ArrayAdapter<Locale> {

	private Context context;
	private List<Locale> locales;
	private ItemActionListener mCallback;

	public interface ItemActionListener {

		public void onLocaleSelected(Locale item);
	}

	class ViewHolder {
		TextView nameText;
		TextView telephoneText;
		TextView addressText;
	}

	public LocaleArrayAdapter(Context context, List<Locale> locales, ItemActionListener listener) {

		super(context, R.layout.popup_locale_list_item, locales);
		
		this.context = context;
		this.locales = locales;
		this.mCallback = listener;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final Locale locale = locales.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		
		TextView nameText = null;
		
		if (rowView == null) {

			rowView = inflater.inflate(R.layout.popup_locale_list_item, parent, false);
			
			nameText = (TextView) rowView.findViewById(R.id.nameText);

			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.nameText = nameText;

			rowView.setTag(viewHolder);

		} else {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			
			nameText = viewHolder.nameText;
		}
		
		nameText.setText(locale.getDisplayName());
		
		rowView.setOnClickListener(getItemOnClickListener(locale, nameText));

		return rowView;
	}
	
	private View.OnClickListener getItemOnClickListener(final Locale locale, final TextView itemNameView) {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				v.setSelected(true);

				mCallback.onLocaleSelected(locale);
			}
		};
	}
}