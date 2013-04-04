package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;

public class ImageLinks implements Serializable {
	private static final long	serialVersionUID	= -4511112481353151042L;
	private String				smallThumbnail;
	private String				thumbnail;

	public String getSmallThumbnail() {
		return this.smallThumbnail;
	}

	public void setSmallThumbnail(String smallThumbnail) {
		this.smallThumbnail = smallThumbnail;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
