package com.tokoku.pos.base.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import com.tokoku.pos.R;
import com.tokoku.pos.base.listener.ReUploadListener;

import java.util.Calendar;

public class ReuploadFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	public ReuploadFragment() {
		super();
	}
	private ReUploadListener listener;

    public void setListener(ReUploadListener listener) {
        this.listener = listener;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
		Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        final DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Panel, this, year, month, day);
        
        dateDialog.setButton(
        	DialogInterface.BUTTON_POSITIVE, 
        	getString(R.string.select), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (which == DialogInterface.BUTTON_POSITIVE) {
            		
            		DatePicker dp = dateDialog.getDatePicker();
            		
            		int day = dp.getDayOfMonth();
            		int month = dp.getMonth();
            	    int year = dp.getYear();
            	   
	            	Calendar cal = Calendar.getInstance();
	               	cal.set(year, month, day);

                    listener.onReUploadDateSelected(cal.getTime());
            	}
            }
        });
        
        dateDialog.setButton(
        	DialogInterface.BUTTON_NEGATIVE, 
        	getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if (which == DialogInterface.BUTTON_NEGATIVE) {
            	   // do nothing
               }
            }
        });
        
        return dateDialog;
    }
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int day)  {}
}
