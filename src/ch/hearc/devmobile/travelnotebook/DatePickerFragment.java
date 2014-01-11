package ch.hearc.devmobile.travelnotebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	/********************
	 * Private members
	 ********************/

	@SuppressWarnings("unused")
	private static final String LOGTAG = TravelItemFormActivity.class.getSimpleName();
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private View viewCaller;
	
	DateListener listener;
	
	public DatePickerFragment( View v ) {
		this.viewCaller = v;
	}
	
	public interface DateListener{
	    public void returnDate(String date, View v);
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        listener = (DateListener) getActivity(); 

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

	@Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, day);

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String formattedDate = sdf.format(c.getTime());
		if (listener != null) 
		{
		  listener.returnDate(formattedDate, viewCaller); 
		}
    }
}
