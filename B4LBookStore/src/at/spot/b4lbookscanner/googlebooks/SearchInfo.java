package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;

public class SearchInfo implements Serializable {
	private static final long	serialVersionUID	= -4213566408590130608L;
	private String				textSnippet;

	public String getTextSnippet() {
		return this.textSnippet;
	}

	public void setTextSnippet(String textSnippet) {
		this.textSnippet = textSnippet;
	}
}
