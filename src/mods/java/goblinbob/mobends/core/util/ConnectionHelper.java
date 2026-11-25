package goblinbob.mobends.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import goblinbob.mobends.core.asset.AssetLocation;
import goblinbob.mobends.core.supporters.BindPoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ConnectionHelper {
   public static ConnectionHelper INSTANCE = new ConnectionHelper();
   private final CloseableHttpClient httpClient = HttpClients.createDefault();
   private Gson gson;

   private ConnectionHelper() {
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      builder.registerTypeAdapter(Color.class, new ColorAdapter());
      builder.registerTypeAdapter(BindPoint.class, new BindPoint.Adapter());
      builder.registerTypeAdapter(AssetLocation.class, new AssetLocation.Adapter());
      this.gson = builder.create();
   }

   public Gson getGson() {
      return this.gson;
   }

   public static <T> T sendGetRequest(URL url, Map<String, String> params, Class<T> responseClass) throws IOException, URISyntaxException {
      HttpGet request = new HttpGet();
      URIBuilder uriBuilder = new URIBuilder(url.toURI());

      for(Entry<String, String> entry : params.entrySet()) {
         uriBuilder.addParameter((String)entry.getKey(), (String)entry.getValue());
      }

      request.setURI(uriBuilder.build());
      CloseableHttpResponse response = INSTANCE.httpClient.execute(request);
      Throwable var21 = null;

      Object var8;
      try {
         HttpEntity entity = response.getEntity();
         if (entity == null) {
            return (T)null;
         }

         var8 = INSTANCE.gson.fromJson(EntityUtils.toString(entity), responseClass);
      } catch (Throwable var18) {
         var21 = var18;
         throw var18;
      } finally {
         if (response != null) {
            if (var21 != null) {
               try {
                  response.close();
               } catch (Throwable var17) {
                  var21.addSuppressed(var17);
               }
            } else {
               response.close();
            }
         }

      }

      return (T)var8;
   }

   public static <T> T sendPostRequest(URL url, JsonObject body, Class<T> responseClass) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      byte[] out = (new Gson()).toJson(body).getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      connection.setFixedLengthStreamingMode(length);
      connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      connection.connect();
      OutputStream os = connection.getOutputStream();
      Throwable response = null;

      try {
         os.write(out);
      } catch (Throwable var16) {
         response = var16;
         throw var16;
      } finally {
         if (os != null) {
            if (response != null) {
               try {
                  os.close();
               } catch (Throwable var15) {
                  response.addSuppressed(var15);
               }
            } else {
               os.close();
            }
         }

      }

      BufferedReader json = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      response = (T)INSTANCE.gson.fromJson(json, responseClass);
      return (T)response;
   }
}
