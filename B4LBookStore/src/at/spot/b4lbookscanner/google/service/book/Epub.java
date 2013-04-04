package at.spot.b4lbookscanner.google.service.book;

import java.io.Serializable;

public class Epub implements Serializable {
	private static final long	serialVersionUID	= 3233525689750393928L;
	private boolean				isAvailable;

	public boolean getIsAvailable() {
		return this.isAvailable;
	}

	public void setIsAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
}
