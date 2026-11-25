package goblinbob.mobends.core.supporters;

import goblinbob.mobends.core.asset.AssetLocation;
import java.util.Arrays;
import java.util.Collection;

public class AccessoryPart {
   private String key;
   private BindPoint bindPoint;
   private AssetLocation modelPath;
   private AssetLocation diffuseTexturePath;
   private AssetLocation inkedTexturePath;

   public AccessoryPart() {
   }

   public String getKey() {
      return this.key;
   }

   public BindPoint getBindPoint() {
      return this.bindPoint;
   }

   public AssetLocation getModelPath() {
      return this.modelPath;
   }

   public AssetLocation getDiffuseTexturePath() {
      return this.diffuseTexturePath;
   }

   public AssetLocation getInkedTexturePath() {
      return this.inkedTexturePath;
   }

   public Collection<AssetLocation> getAssetLocations() {
      return Arrays.asList(this.modelPath, this.diffuseTexturePath, this.inkedTexturePath);
   }
}
