package goblinbob.mobends.core.pack;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import goblinbob.mobends.core.util.BendsPackHelper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class LocalBendsPack implements IBendsPack {
   private BendsPackMeta metadata;
   private ResourceLocation thumbnailLocation = ThumbnailProvider.DEFAULT_THUMBNAIL_LOCATION;

   public LocalBendsPack(String displayName, String author, String description) {
      this.metadata = new BendsPackMeta();
      this.metadata.key = BendsPackHelper.constructPackName(displayName);
      this.metadata.displayName = displayName;
      this.metadata.author = author;
      this.metadata.description = description;
   }

   public LocalBendsPack(BendsPackMeta metadata) {
      this.metadata = metadata;
   }

   public void rename(String newName) {
      if (!this.metadata.key.equals(newName)) {
         this.metadata.key = newName;
      }
   }

   public LocalBendsPack setThumbnailURL(String thumbnailURL) {
      this.thumbnailLocation = PackManager.INSTANCE.getThumbnailLocation(this.metadata.key, thumbnailURL);
      return this;
   }

   public String getKey() {
      return this.metadata.key;
   }

   public String getDisplayName() {
      return this.metadata.displayName;
   }

   public String getAuthor() {
      return this.metadata.author;
   }

   public String getDescription() {
      return this.metadata.description;
   }

   @Nullable
   public ResourceLocation getThumbnail() {
      return this.thumbnailLocation;
   }

   public boolean canPackBeEdited() {
      return true;
   }

   public static LocalBendsPack readFromFile(File file) throws IOException {
      JsonReader fileReader = new JsonReader(new FileReader(file));
      BendsPackMeta meta = (BendsPackMeta)(new Gson()).fromJson(fileReader, BendsPackMeta.class);
      return new LocalBendsPack(meta);
   }
}
