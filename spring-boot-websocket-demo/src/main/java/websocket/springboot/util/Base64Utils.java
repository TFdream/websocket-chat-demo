package websocket.springboot.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Ricky Fung
 */
public abstract class Base64Utils {
    private static Charset UTF_8 = StandardCharsets.UTF_8;

    public static String encode(String data) {
        return bytesToString(Base64.getEncoder().encode(data.getBytes(UTF_8)));
    }

    public static String decode(String data) {
        return bytesToString(Base64.getDecoder().decode(data.getBytes(UTF_8)));
    }

    public static String bytesToString(byte[] buf) {
        return new String(buf, UTF_8);
    }
}
