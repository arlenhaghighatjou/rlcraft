package xzeroair.trinkets.vip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.TreeMap;

public class VIPHandler {
   private static final String VIPV2 = "https://raw.githubusercontent.com/XzeroAir/AuxFiles/master/VipsV2.json";
   private static Gson gson = (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
   public static TreeMap<String, VipUser> Vips;

   public VIPHandler() {
   }

   public static void popVIPList() {
      loadJsonFromUrl("https://raw.githubusercontent.com/XzeroAir/AuxFiles/master/VipsV2.json");
   }

   private static void loadJsonFromUrl(String url) {
      try {
         URL link = new URL(url);
         InputStream stream = link.openStream();
         InputStreamReader input = new InputStreamReader(stream);
         BufferedReader reader = new BufferedReader(input);

         try {
            Type mapType = (new TypeToken<TreeMap<String, VipUser>>() {
            }).getType();
            Vips = (TreeMap)gson.fromJson(reader, mapType);
         } catch (Exception var15) {
            var15.printStackTrace();
         } finally {
            try {
               reader.close();
               input.close();
               stream.close();
            } catch (IOException var14) {
               ;
            }

         }
      } catch (IOException var17) {
         var17.printStackTrace();
      }

   }
}
