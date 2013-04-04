package at.spot.b4lbookscanner.googlebooks;

import java.io.Serializable;
import java.util.List;

public class VolumeInfo implements Serializable {
	private static final long			serialVersionUID	= 1L;
	private List<String>				authors;
	private Number						averageRating;
	private String						canonicalVolumeLink;
	private List<String>				categories;
	private String						contentVersion;
	private String						description;
	private ImageLinks					imageLinks;
	private List<IndustryIdentifiers>	industryIdentifiers;
	private String						infoLink;
	private String						language;
	private Number						pageCount;
	private String						previewLink;
	private String						printType;
	private String						publishedDate;
	private String						publisher;
	private Number						ratingsCount;
	private String						title;

	public List<String> getAuthors() {
		return this.authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public Number getAverageRating() {
		return this.averageRating;
	}

	public void setAverageRating(Number averageRating) {
		this.averageRating = averageRating;
	}

	public String getCanonicalVolumeLink() {
		return this.canonicalVolumeLink;
	}

	public void setCanonicalVolumeLink(String canonicalVolumeLink) {
		this.canonicalVolumeLink = canonicalVolumeLink;
	}

	public List<String> getCategories() {
		return this.categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getContentVersion() {
		return this.contentVersion;
	}

	public void setContentVersion(String contentVersion) {
		this.contentVersion = contentVersion;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageLinks getImageLinks() {
		return this.imageLinks;
	}

	public void setImageLinks(ImageLinks imageLinks) {
		this.imageLinks = imageLinks;
	}

	public List<IndustryIdentifiers> getIndustryIdentifiers() {
		return this.industryIdentifiers;
	}

	public void setIndustryIdentifiers(List<IndustryIdentifiers> industryIdentifiers) {
		this.industryIdentifiers = industryIdentifiers;
	}

	public String getInfoLink() {
		return this.infoLink;
	}

	public void setInfoLink(String infoLink) {
		this.infoLink = infoLink;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Number getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(Number pageCount) {
		this.pageCount = pageCount;
	}

	public String getPreviewLink() {
		return this.previewLink;
	}

	public void setPreviewLink(String previewLink) {
		this.previewLink = previewLink;
	}

	public String getPrintType() {
		return this.printType;
	}

	public void setPrintType(String printType) {
		this.printType = printType;
	}

	public String getPublishedDate() {
		return this.publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getPublisher() {
		return this.publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Number getRatingsCount() {
		return this.ratingsCount;
	}

	public void setRatingsCount(Number ratingsCount) {
		this.ratingsCount = ratingsCount;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
