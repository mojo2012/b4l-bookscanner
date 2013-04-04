package at.spot.b4lbookscanner.google.service.book;

import java.io.Serializable;

public class AccessInfo implements Serializable {
	private static final long	serialVersionUID	= -1503562619530177300L;
	private String				accessViewStatus;
	private String				country;
	private boolean				embeddable;
	private Epub				epub;
	private Pdf					pdf;
	private boolean				publicDomain;
	private String				textToSpeechPermission;
	private String				viewability;
	private String				webReaderLink;

	public String getAccessViewStatus() {
		return this.accessViewStatus;
	}

	public void setAccessViewStatus(String accessViewStatus) {
		this.accessViewStatus = accessViewStatus;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean getEmbeddable() {
		return this.embeddable;
	}

	public void setEmbeddable(boolean embeddable) {
		this.embeddable = embeddable;
	}

	public Epub getEpub() {
		return this.epub;
	}

	public void setEpub(Epub epub) {
		this.epub = epub;
	}

	public Pdf getPdf() {
		return this.pdf;
	}

	public void setPdf(Pdf pdf) {
		this.pdf = pdf;
	}

	public boolean getPublicDomain() {
		return this.publicDomain;
	}

	public void setPublicDomain(boolean publicDomain) {
		this.publicDomain = publicDomain;
	}

	public String getTextToSpeechPermission() {
		return this.textToSpeechPermission;
	}

	public void setTextToSpeechPermission(String textToSpeechPermission) {
		this.textToSpeechPermission = textToSpeechPermission;
	}

	public String getViewability() {
		return this.viewability;
	}

	public void setViewability(String viewability) {
		this.viewability = viewability;
	}

	public String getWebReaderLink() {
		return this.webReaderLink;
	}

	public void setWebReaderLink(String webReaderLink) {
		this.webReaderLink = webReaderLink;
	}
}
