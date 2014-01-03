package ch.hearc.devmobile.travelnotebook.database;

import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public interface NotSingleLocation_I {

	public LatLng getEndLocationPosition(Geocoder geocoder);
	
	public void setEndLocation(String endLocation);
}
