package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;

public class Items implements Serializable {
	private static final long	serialVersionUID	= -5066784066601085335L;
	private AccessInfo			accessInfo;
	private String				etag;
	private String				id;
	private String				kind;
	private SaleInfo			saleInfo;
	private SearchInfo			searchInfo;
	private String				selfLink;
	private VolumeInfo			volumeInfo;

	public AccessInfo getAccessInfo() {
		return this.accessInfo;
	}

	public void setAccessInfo(AccessInfo accessInfo) {
		this.accessInfo = accessInfo;
	}

	public String getEtag() {
		return this.etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return this.kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public SaleInfo getSaleInfo() {
		return this.saleInfo;
	}

	public void setSaleInfo(SaleInfo saleInfo) {
		this.saleInfo = saleInfo;
	}

	public SearchInfo getSearchInfo() {
		return this.searchInfo;
	}

	public void setSearchInfo(SearchInfo searchInfo) {
		this.searchInfo = searchInfo;
	}

	public String getSelfLink() {
		return this.selfLink;
	}

	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}

	public VolumeInfo getVolumeInfo() {
		return this.volumeInfo;
	}

	public void setVolumeInfo(VolumeInfo volumeInfo) {
		this.volumeInfo = volumeInfo;
	}
}
