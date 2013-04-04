package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;

public class SaleInfo implements Serializable {
	private static final long	serialVersionUID	= 2497445655716881716L;
	private String				country;
	private boolean				isEbook;
	private String				saleability;

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean getIsEbook() {
		return this.isEbook;
	}

	public void setIsEbook(boolean isEbook) {
		this.isEbook = isEbook;
	}

	public String getSaleability() {
		return this.saleability;
	}

	public void setSaleability(String saleability) {
		this.saleability = saleability;
	}
}
