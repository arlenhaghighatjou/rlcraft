package goblinbob.mobends.core.asset;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.env.EnvironmentModule;
import goblinbob.mobends.core.module.IModule;
import goblinbob.mobends.core.util.ConnectionHelper;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.http.conn.HttpHostConnectException;

public class AssetsModule {
   public static AssetsModule INSTANCE;
   private final String apiUrl = EnvironmentModule.getConfig().getApiUrl();
   private final File assetsDirectory;
   private final File localManifestFile;
   private AssetManifest localManifest;

   public AssetsModule(File configDirectory) {
      File modConfigDirectory = new File(configDirectory, "mobends");
      this.assetsDirectory = new File(modConfigDirectory, "assets");
      this.assetsDirectory.mkdirs();
      this.localManifestFile = new File(modConfigDirectory, "asset_manifest.json");
      this.updateAssets();
   }

   private void fetchLocalManifest() {
      this.localManifest = null;
      if (this.localManifestFile.isFile()) {
         Gson gson = ConnectionHelper.INSTANCE.getGson();

         try {
            this.localManifest = (AssetManifest)gson.fromJson(new BufferedReader(new FileReader(this.localManifestFile)), AssetManifest.class);
         } catch (FileNotFoundException | JsonParseException var3) {
            Core.LOG.warning("Failed to get local asset manifest.");
            var3.printStackTrace();
         }
      }

   }

   private AssetManifest fetchOnlineManifest() {
      Map<String, String> params = new HashMap();

      try {
         return (AssetManifest)ConnectionHelper.sendGetRequest(new URL(this.apiUrl + "/api/asset/manifest"), params, AssetManifest.class);
      } catch (HttpHostConnectException var3) {
         ;
      } catch (JsonParseException var4) {
         Core.LOG.warning("Failed to parse online asset manifest.");
         var4.printStackTrace();
      } catch (URISyntaxException | IOException var5) {
         Core.LOG.warning("Failed to get online asset manifest.");
         var5.printStackTrace();
      }

      return null;
   }

   private void storeManifestLocally(AssetManifest manifest) {
      try {
         FileWriter writer = new FileWriter(this.localManifestFile);
         Throwable var3 = null;

         try {
            Gson gson = ConnectionHelper.INSTANCE.getGson();
            gson.toJson(manifest, writer);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (writer != null) {
               if (var3 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  writer.close();
               }
            }

         }
      } catch (IOException | JsonParseException var15) {
         Core.LOG.warning("Failed to save local asset manifest.");
         var15.printStackTrace();
      }

      this.localManifest = manifest;
   }

   private void downloadAsset(AssetManifest manifest, AssetDefinition asset) throws MalformedAssetException {
      try {
         URL url = new URL(manifest.getBaseUrl() + asset.getPath().getAssetPath());
         URLConnection connection = url.openConnection();
         DataInputStream dis = new DataInputStream(connection.getInputStream());
         byte[] fileData = new byte[connection.getContentLength()];

         for(int q = 0; q < fileData.length; ++q) {
            fileData[q] = dis.readByte();
         }

         dis.close();
         File localAssetPath = this.getAssetFile(asset.getPath());
         localAssetPath.getParentFile().mkdirs();
         FileOutputStream fos = new FileOutputStream(localAssetPath);
         fos.write(fileData);
         fos.close();
      } catch (IOException var9) {
         throw new MalformedAssetException(String.format("Couldn't download asset: %s", asset.getPath()), var9);
      }
   }

   public void updateAssets() {
      this.fetchLocalManifest();
      AssetManifest onlineManifest = this.fetchOnlineManifest();
      if (onlineManifest != null) {
         Core.LOG.info("New assets detected");
         Iterable<AssetDefinition> assetsToUpdate = AssetManifest.getAssetsToUpdate(this.localManifest, onlineManifest);

         try {
            for(AssetDefinition asset : assetsToUpdate) {
               this.downloadAsset(onlineManifest, asset);
            }

            this.storeManifestLocally(onlineManifest);
         } catch (MalformedAssetException var5) {
            Core.LOG.warning(var5.getMessage());
         }

      }
   }

   public Collection<AssetDefinition> getAssets() {
      return (Collection<AssetDefinition>)(this.localManifest != null ? this.localManifest.getAssets() : Collections.emptyList());
   }

   public File getAssetFile(AssetLocation location) {
      return new File(this.assetsDirectory, location.getAssetPath());
   }

   public static class Factory implements IModule {
      public Factory() {
      }

      public void preInit(FMLPreInitializationEvent event) {
         AssetsModule.INSTANCE = new AssetsModule(event.getModConfigurationDirectory());
      }

      public void onRefresh() {
         AssetsModule.INSTANCE.updateAssets();
      }
   }
}
