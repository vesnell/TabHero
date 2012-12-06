package pl.tabhero;


/** Holds planet data. */
public class mItems {
	private String name = "";
	private boolean checked = false;

	public mItems() {
	}

	public mItems(String name) {
		this.name = name;
	}

	public mItems(String name, boolean checked) {
		this.name = name;
		this.checked = checked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString() {
		return name;
	}

	public void toggleChecked() {
		checked = !checked;
	}

	
	/** Custom adapter for displaying an array of Planet objects. */
	
}
