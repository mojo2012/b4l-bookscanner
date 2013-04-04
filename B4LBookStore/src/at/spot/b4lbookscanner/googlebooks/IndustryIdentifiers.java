package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;

public class IndustryIdentifiers implements Serializable {
	private static final long	serialVersionUID	= 2066217962919065328L;
	private String				identifier;
	private String				type;

	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
