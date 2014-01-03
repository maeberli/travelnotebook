package ch.hearc.devmobile.travelnotebook;

import java.io.Serializable;

import android.view.View.OnClickListener;

public class MenuElement implements Serializable {

	/********************
	 * Private members
	 ********************/
	private static final long serialVersionUID = 1L;
	private OnClickListener onClickListener;
	private String label;

	/********************
	 * Constructor
	 ********************/
	public MenuElement(String label, OnClickListener action) {
		this.label = label;
		this.onClickListener = action;
	}

	/********************
	 * Public methods
	 ********************/
	public String getLabel() {
		return this.label;
	}

	public OnClickListener getActionListener() {
		return this.onClickListener;
	}

}
