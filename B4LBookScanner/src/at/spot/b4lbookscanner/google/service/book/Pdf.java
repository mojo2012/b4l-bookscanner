package at.spot.b4lbookscanner.google.service.book;

import java.io.Serializable;

public class Pdf implements Serializable {
	private static final long	serialVersionUID	= -307117737034133350L;
	private boolean				isAvailable;

	public boolean getIsAvailable() {
		return this.isAvailable;
	}

	public void setIsAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
}
