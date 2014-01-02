package ch.hearc.devmobile.travelnotebook;

import android.graphics.Color;

public class Utilities {

	public static int createTransparancyColor(int color, int transparency) {
		return Color.argb(transparency, Color.red(color), Color.green(color),
				Color.blue(color));
	}
}
