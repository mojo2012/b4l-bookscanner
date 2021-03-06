package at.spot.prestashop.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import at.spot.gson.ByteArrayToBase64TypeAdapter;
import at.spot.log.Log;
import at.spot.prestashop.service.json.EStatus;
import at.spot.prestashop.service.json.Product;
import at.spot.util.HttpUtil;
import at.spot.util.HttpUtil.RequestType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
			
			String ret = HttpUtil.request(RequestType.Post, updateUrl, getAuthenticationHeaderFields(), null, json, null, null);
			
			System.out.println(ret);
		} catch (JAXBException e) {
			Log.error("PrestaShopClient.createProduct", e, true);
			throw new Exception("Cannot create product.");
		}
	}
	
	protected Map<String, String> getAuthenticationHeaderFields() {
		Map<String, String> header = new HashMap<>();
		
		header.put("Api-Key", apiKey);
		
		return header;
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
		//convert byte arrays to base64 encoded strings
		Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
//		gson = new Gson().toJson(o);
		return gson.toJson(o);
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
//			p.setSupplierId(32);
			p.setStockLocationId(36);
			p.setVatRate(10f);
			p.setEan13("978-3-86680-192-9");
			
			byte[] image = Files.readAllBytes(Paths.get("/Users/matthias/Desktop/913.jpg"));
			
			p.getImages().add(image);
			
			pc.addProduct(p);
			
		
//			Log.info("main", p.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
