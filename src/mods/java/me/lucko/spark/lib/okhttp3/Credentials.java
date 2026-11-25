package me.lucko.spark.lib.okhttp3;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import me.lucko.spark.lib.okio.ByteString;

public final class Credentials {
   private Credentials() {
   }

   public static String basic(String username, String password) {
      return basic(username, password, StandardCharsets.ISO_8859_1);
   }

   public static String basic(String username, String password, Charset charset) {
      String usernameAndPassword = username + ":" + password;
      String encoded = ByteString.encodeString(usernameAndPassword, charset).base64();
      return "Basic " + encoded;
   }
}
