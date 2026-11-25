package goblinbob.mobends.core.asset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManifest {
   private String baseUrl = "";
   private List<AssetDefinition> assets = new ArrayList();

   public AssetManifest() {
   }

   public String getBaseUrl() {
      return this.baseUrl;
   }

   public Collection<AssetDefinition> getAssets() {
      return Collections.unmodifiableCollection(this.assets);
   }

   public static Iterable<AssetDefinition> getAssetsToUpdate(AssetManifest oldManifest, AssetManifest newManifest) {
      if (oldManifest == null) {
         return newManifest.getAssets();
      } else {
         List<AssetDefinition> assetsToUpdate = new ArrayList();
         Map<Integer, Integer> versionMap = new HashMap();

         for(AssetDefinition asset : oldManifest.getAssets()) {
            versionMap.put(Integer.valueOf(asset.getId()), Integer.valueOf(asset.getVersion()));
         }

         for(AssetDefinition asset : newManifest.getAssets()) {
            int assetId = asset.getId();
            if (!versionMap.containsKey(Integer.valueOf(assetId)) || ((Integer)versionMap.get(Integer.valueOf(assetId))).intValue() < asset.getVersion()) {
               assetsToUpdate.add(asset);
            }
         }

         return assetsToUpdate;
      }
   }
}
