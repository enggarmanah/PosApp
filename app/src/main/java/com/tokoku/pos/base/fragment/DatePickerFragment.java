package com.tokoku.pos.base.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import com.tokoku.pos.R;
import com.tokoku.pos.Constant;
import com.tokoku.pos.util.CommonUtil;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private DateFormat dateFormat = CommonUtil.getDateFormat();
	
	private TextView inputDate;
	private boolean isOK = false;
	
	public DatePickerFragment() {
		super();
	}
	
	public void setInputField(TextView inputDate) {
		
		this.inputDate = inputDate;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
		Calendar c = Calendar.getInstance();
		
        String dateStr = Constant.EMPTY_STRING;
        
        if (inputDate != null) {
        	dateStr = inputDate.getText().toString();
        }
        
        if (!CommonUtil.isEmpty(dateStr)) {
        	
        	try {
				c.setTime(dateFormat.parse(dateStr));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        
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
	               	
	               	String dateStr = dateFormat.format(cal.getTime());
	               	inputDate.setText(dateStr);
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
        
        dateDialog.setButton(
        	DialogInterface.BUTTON_NEUTRAL, 
        	getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if (which == DialogInterface.BUTTON_NEUTRAL) {
            	   inputDate.setText(Constant.EMPTY_STRING);
               }
            }
        });
        
        return dateDialog;
    }
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        
    	Calendar cal = Calendar.getInstance();
    	cal.set(year, month, day);
    	
    	if (isOK) {
    		String dateStr = dateFormat.format(cal.getTime());
    		inputDate.setText(dateStr);
    	}
    }

}
