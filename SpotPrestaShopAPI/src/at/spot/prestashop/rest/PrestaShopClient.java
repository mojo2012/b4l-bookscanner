package at.spot.prestashop.rest;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import at.spot.log.Log;
import at.spot.prestashop.rest.PrestashopProduct.Product;
import at.spot.util.HttpUtil;
import at.spot.util.HttpUtil.RequestType;

public class PrestaShopClient {
	
	private URL baseUrl;
	private String apiKey;
	
	protected enum WebserviceRessourceType {
		Products("products/"),
		Categories("categories/"),
		StockAvailabilities("stock_availables/"),
		Stocks("stocks/"),
		Tags("tags/");
		
		private String url;
		
		WebserviceRessourceType(String url) {
			this.url = "api/" + url;
		}
		
		String getURL() {
			return url;
		}
	}
	
	/**
	 * Pass the url to the shop, eg. shop.example.com, and the API key.
	 * @param baseUrl
	 * @throws MalformedURLException 
	 */
	public PrestaShopClient(String baseUrl, String apiKey) throws MalformedURLException {
		this.baseUrl = new URL(baseUrl);
		this.apiKey = apiKey;
	}
	
	public void addImageToProduct(int productId, byte[] image) {
		
	}
	
	public void createProduct(Product product) throws Exception {
		PrestashopProduct p = new PrestashopProduct();
		p.setProduct(product);
		
		try {
			Marshaller m = JAXBContext.newInstance(PrestashopProduct.class).createMarshaller();
			
			StringWriter sw = new StringWriter();
			m.marshal(p, sw);
			
			URL updateUrl = getWebserviceUrl(WebserviceRessourceType.Products); 
			
			HttpUtil.request(RequestType.Post, updateUrl, null, apiKey, apiKey);
		} catch (JAXBException e) {
			Log.error("PrestaShopClient.createProduct", e, true);
			throw new Exception("Cannot create product.");
		}

	}
	
	public Product getProduct(int id) throws Exception {
		PrestashopProduct p = null;
		
		URL requestUrl;
		try {
			requestUrl = new URL(getWebserviceUrl(WebserviceRessourceType.Products), id + "");
			
			String xml = getRawXmlFromWebservice(requestUrl);
			
			Unmarshaller unmarshaller = JAXBContext.newInstance(PrestashopProduct.class).createUnmarshaller();
			p = (PrestashopProduct) unmarshaller.unmarshal(new StringReader(xml));
			
		} catch (Exception e) {
			Log.error("PrestaShopClient.getProduct", e, true);
			throw new Exception("Cannot retrieve product.");
		}
		
		return p != null ? p.getProduct() : null;
	}
	
	public List<Product> getAllProducts() {
		List<Product> prods = new ArrayList<>();
		
		
		return prods;
	}
	
	protected String getRawXmlFromWebservice(URL url) throws Exception {
		String ret = null;
		
		try {
			ret = HttpUtil.request(RequestType.Post, url, null, apiKey, apiKey);
		} catch (Exception e) {
			//Log.error("PrestaShopClient.getRawXmlFromWebservice", e, true);
			throw e;
		}
		
		return ret;
	}
	
	protected URL getWebserviceUrl(WebserviceRessourceType ressourceType) throws MalformedURLException {
		URL url = null;
		
		try {
			url = new URL(baseUrl, ressourceType.getURL());
		} catch (MalformedURLException e) {
			throw e;
		}
		
		return url;
	}
	
	public static void main(String[] args) {
		try {
			PrestaShopClient pc = new PrestaShopClient("http://shop.b4l-wien.at", "NL0VF0CJ48EE3QVNHO4W0ZWCZ6UY1VPN");
			
//			Product p = pc.getProduct(1162);
			
//			Log.info("main", p.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
