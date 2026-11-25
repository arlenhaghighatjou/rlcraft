package goblinbob.mobends.core.asset;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.minecraft.util.ResourceLocation;

public class AssetLocation extends ResourceLocation {
   private static final String PREFIX = "assets/";
   private final AssetType assetType;
   private final String assetPath;

   public AssetLocation(String assetPath) {
      super("mobends", "assets/" + assetPath);
      this.assetPath = assetPath;
      if (assetPath.startsWith("models/")) {
         this.assetType = AssetType.MODEL;
      } else if (assetPath.startsWith("textures/")) {
         this.assetType = AssetType.TEXTURE;
      } else if (assetPath.endsWith(".json")) {
         this.assetType = AssetType.JSON;
      } else {
         this.assetType = AssetType.UNKNOWN;
      }

   }

   public AssetLocation(String assetPath, AssetType assetType) {
      super("mobends", "assets/" + assetPath);
      this.assetPath = assetPath;
      this.assetType = assetType;
   }

   public String getAssetPath() {
      return this.assetPath;
   }

   public AssetType getAssetType() {
      return this.assetType;
   }

   public static class Adapter extends TypeAdapter<AssetLocation> {
      public Adapter() {
      }

      public void write(JsonWriter out, AssetLocation value) throws IOException {
         out.value(value.assetPath);
      }

      public AssetLocation read(JsonReader in) throws IOException {
         return new AssetLocation(in.nextString());
      }
   }
}
