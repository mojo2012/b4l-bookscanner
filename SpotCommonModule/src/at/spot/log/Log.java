package at.spot.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static void error(String tag, Exception ex, boolean printStackTrace) {
		String msg = ex.getMessage();

		if (printStackTrace) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			ex.printStackTrace(pw);

			msg += "\n" + sw.toString();
		}

		error(tag, msg);
	}

	public static void error(String tag, String msg) {
		log("ERROR", tag, msg);
	};

	public static void debug(String tag, String msg) {
		log("DEBUG", tag, msg);
	};

	public static void info(String tag, String msg) {
		log("INFO", tag, msg);
	};

	public static void warn(String tag, String msg) {
		log("WARNING", tag, msg);
	};

	protected static void log(String type, String tag, String msg) {
		System.out.println(String.format("[%s] %s: %s / %s", type, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()), tag, msg));
	};
}
