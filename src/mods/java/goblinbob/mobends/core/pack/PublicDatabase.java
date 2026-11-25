package goblinbob.mobends.core.pack;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import goblinbob.mobends.core.Core;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PublicDatabase {
   public PublicDatabase.PackEntry[] packs;

   public PublicDatabase() {
   }

   public static PublicDatabase downloadPublicDatabase(String databaseUrl) {
      try {
         URL publicDirectoryUrl = new URL(databaseUrl);
         JsonReader fileReader = new JsonReader(new InputStreamReader(publicDirectoryUrl.openStream()));
         Gson gson = new Gson();
         return (PublicDatabase)gson.fromJson(fileReader, PublicDatabase.class);
      } catch (JsonSyntaxException var4) {
         Core.LOG.warning("The downloaded database is not proper JSON.");
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      return null;
   }

   public class PackEntry {
      public String name;
      public String displayName;
      public String author;
      public String description;
      public String uploadedDate;
      public String updatedDate;
      public String downloadLink;
      public String thumbnail;

      public PackEntry() {
      }
   }
}
