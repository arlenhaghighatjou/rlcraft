package goblinbob.mobends.core;

import com.google.gson.Gson;
import goblinbob.mobends.core.util.ErrorReporter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebAPI {
   public static WebAPI INSTANCE = new WebAPI();
   private static String apiUrl = "https://raw.githubusercontent.com/mobends/mobends-resources/master/static-api.json";
   private boolean initialized = false;
   private WebAPI.APIData data;

   public WebAPI() {
   }

   private void initialize() {
      if (!this.initialized) {
         try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.data = (WebAPI.APIData)(new Gson()).fromJson(json, WebAPI.APIData.class);
         } catch (IOException var4) {
            ErrorReporter.showErrorToPlayer("Couldn't fetch the WebAPI data. Some features may be disabled. Contact the developers if this is a prolonged issue.");
            var4.printStackTrace();
         }

         this.initialized = true;
      }
   }

   public String getOfficialAnimationEditorUrl() {
      this.initialize();
      return this.data == null ? null : this.data.officialAnimationEditorUrl;
   }

   private static class APIData {
      String officialAnimationEditorUrl = null;

      private APIData() {
      }
   }
}
