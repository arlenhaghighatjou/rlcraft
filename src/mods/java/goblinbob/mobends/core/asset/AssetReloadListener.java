package goblinbob.mobends.core.asset;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class AssetReloadListener implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();

   public AssetReloadListener() {
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      AssetsModule.INSTANCE.updateAssets();
      AssetModels.INSTANCE.clearCache();
      Collection<AssetDefinition> assets = AssetsModule.INSTANCE.getAssets();
      ProgressBar bar = ProgressManager.push("Reloading Mo' Bends Assets", assets.size(), true);

      for(AssetDefinition asset : assets) {
         AssetLocation location = asset.getPath();
         bar.step(location.toString());
         AssetType assetType = location.getAssetType();
         if (assetType == AssetType.TEXTURE) {
            AssetTexture assetTexture = new AssetTexture(location);
            if (!Minecraft.getMinecraft().getTextureManager().loadTexture(location, assetTexture)) {
               LOGGER.error("Couldn't upload asset texture: {}", location.toString());
            }
         } else if (assetType == AssetType.MODEL) {
            try {
               AssetModels.INSTANCE.register(location);
            } catch (JsonSyntaxException | IOException var9) {
               LOGGER.error("Couldn't register asset model: {}", location.toString());
               var9.printStackTrace();
            }
         }
      }

      ProgressManager.pop(bar);
   }
}
