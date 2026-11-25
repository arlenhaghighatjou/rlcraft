package ivorius.reccomplex;

import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;

public class Repository {
   public static final String BASE_URL = "http://complex.ivorius.de";

   public Repository() {
   }

   public static void openWebLink(String url) {
      try {
         openWebLink(new URI(url));
      } catch (URISyntaxException var2) {
         RecurrentComplex.logger.error("Can't open url for {}", url, var2);
      }

   }

   public static void openWebLink(URI uri) {
      try {
         Class<?> oclass = Class.forName("java.awt.Desktop");
         Object object = oclass.getMethod("getDesktop").invoke((Object)null);
         oclass.getMethod("browse", URI.class).invoke(object, uri);
      } catch (Exception var3) {
         Throwable throwable = var3.getCause();
         RecurrentComplex.logger.error("Couldn't open link: {}", throwable == null ? "<UNKNOWN>" : throwable.getMessage());
      }

   }

   public static String browseURL() {
      return String.format("%s/submissions", "http://complex.ivorius.de");
   }

   public static String submitURL(@Nullable String id) {
      return String.format("%s/submissions/create%s", "http://complex.ivorius.de", id != null ? "?title=" + id : "");
   }
}
