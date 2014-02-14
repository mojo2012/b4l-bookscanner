package at.spot.prestashop.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import at.spot.log.Log;
import at.spot.prestashop.service.json.EStatus;
import at.spot.prestashop.service.json.Product;
import at.spot.util.HttpUtil;
import at.spot.util.HttpUtil.RequestType;

import com.google.gson.Gson;

public class PrestaShopClient {
	
	private URL baseUrl;
	private String apiKey;
	
	protected enum WebserviceFunction {
		AddProduct("product_add.php");
		
		private String url;
		
		WebserviceFunction(String url) {
			this.url = "service/" + url;
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
	
	public void addProduct(Product product) throws Exception {
		try {
			String json = convertToJson(product);
			
			URL updateUrl = getWebserviceUrl(WebserviceFunction.AddProduct); 
			
			String ret = HttpUtil.request(RequestType.Post, updateUrl, null, json, null, null);
			
			System.out.println(ret);
		} catch (JAXBException e) {
			Log.error("PrestaShopClient.createProduct", e, true);
			throw new Exception("Cannot create product.");
		}
	}
//	
//	public Product getProduct(int id) throws Exception {
//		Product p = null;
//		
//		URL requestUrl;
//		try {
//			requestUrl = new URL(getWebserviceUrl(WebserviceRessourceType.Products), id + "");
//			
//			String xml = getRawXmlFromWebservice(requestUrl);
//			
//			Unmarshaller unmarshaller = JAXBContext.newInstance(Product.class).createUnmarshaller();
//			p = (Product) unmarshaller.unmarshal(new StringReader(xml));
//			
//		} catch (Exception e) {
//			Log.error("PrestaShopClient.getProduct", e, true);
//			throw new Exception("Cannot retrieve product.");
//		}
//		
//		return p != null ? p.getProduct() : null;
//	}
//	
//	public List<Product> getAllProducts() {
//		List<Product> prods = new ArrayList<>();
//		
//		
//		return prods;
//	}
	
//	protected String getRawXmlFromWebservice(URL url) throws Exception {
//		String ret = null;
//		
//		try {
//			ret = HttpUtil.request(RequestType.Post, url, null, apiKey, apiKey);
//		} catch (Exception e) {
//			//Log.error("PrestaShopClient.getRawXmlFromWebservice", e, true);
//			throw e;
//		}
//		
//		return ret;
//	}
//	
	protected URL getWebserviceUrl(WebserviceFunction ressourceType) throws MalformedURLException {
		URL url = null;
		
		try {
			url = new URL(baseUrl, ressourceType.getURL());
		} catch (MalformedURLException e) {
			throw e;
		}
		
		return url;
	}
	
	protected String convertToJson(Object o) {
		return new Gson().toJson(o);
	}
	
	public static void main(String[] args) {
		try {
			PrestaShopClient pc = new PrestaShopClient("http://shop.b4l-wien.at/service", "NL0VF0CJ48EE3QVNHO4W0ZWCZ6UY1VPN");
			
			Product p = new Product();
			p.setTitle("Test");
			p.setAmount(1);
			p.setShortDescription("short test");
			p.setLongDescription("long test");
			p.setPrice(1f);
			p.setTags(Arrays.asList("test_tag", "test_tag2"));
			p.setCategoryId(33);
			p.setOnline(false);
			p.setStatus(EStatus.Used);
			p.setSupplierId(32);
			p.setVatRate(10f);
			
			pc.addProduct(p);
			
		
//			Log.info("main", p.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
