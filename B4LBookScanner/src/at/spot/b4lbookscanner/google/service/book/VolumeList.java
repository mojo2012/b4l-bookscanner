package at.spot.b4lbookscanner.google.service.book;

import java.io.Serializable;
import java.util.List;

public class VolumeList implements Serializable {
	private static final long	serialVersionUID	= 9115881977715850350L;
	private List<Items>			items;
	private String				kind;
	private Number				totalItems;

	public List<Items> getItems() {
		return this.items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public String getKind() {
		return this.kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Number getTotalItems() {
		return this.totalItems;
	}

	public void setTotalItems(Number totalItems) {
		this.totalItems = totalItems;
	}
}
