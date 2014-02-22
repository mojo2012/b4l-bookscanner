package at.spot.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Util {
	public static byte[] convertImageToByteArray(BufferedImage originalImage) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] imageInByte = null;
		
		try {
			ImageIO.write(originalImage, "jpg", baos);

			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();

		} catch (Exception e) {
			throw e;
		} finally {
			invokeClose(baos);
		}

		return imageInByte;
	}

	public static void invokeClose(Object... objects) {
		for (Object o : objects) {
			try {
				if (o != null) {
					Class<?>[] params = {};
					Method m = o.getClass().getMethod("close", params);
					Object[] params2 = {};
					m.invoke(o, params2);
				}
			} catch (Exception ex) {
				System.out.println("SQLUtil.closeEm()");
				ex.printStackTrace();
			}
		}
	}
}
