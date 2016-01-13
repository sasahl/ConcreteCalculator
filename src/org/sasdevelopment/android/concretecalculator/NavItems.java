package org.sasdevelopment.android.concretecalculator;
/*
 * This class is a helper class that is used to populate the ListView of the Navigation Drawer
 */
import android.graphics.drawable.Drawable;

public class NavItems {
	
	private Drawable icon;
	private String title;
	
	public NavItems(Drawable icon, String title) {
		this.icon = icon;
		this.title = title;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
