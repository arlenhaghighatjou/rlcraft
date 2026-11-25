package goblinbob.mobends.core.asset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel.Builder;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class AssetModels {
   public static final AssetModels INSTANCE = new AssetModels();
   private static final Logger LOGGER = LogManager.getLogger();
   private final FaceBakery faceBakery = new FaceBakery();
   private final Map<AssetLocation, SimpleBakedModel> bakedModelMap = new HashMap();

   public AssetModels() {
   }

   public SimpleBakedModel register(AssetLocation location) throws IOException {
      InputStream stream = new FileInputStream(AssetsModule.INSTANCE.getAssetFile(location));
      ModelBlock model = ModelBlock.deserialize(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
      SimpleBakedModel bakedModel = this.bakeModel(model, ModelRotation.X0_Y0, false);
      this.bakedModelMap.put(location, bakedModel);
      return bakedModel;
   }

   public void clearCache() {
      this.bakedModelMap.clear();
   }

   public SimpleBakedModel getModel(AssetLocation location) {
      if (!this.bakedModelMap.containsKey(location)) {
         try {
            SimpleBakedModel bakedModel = this.register(location);
            this.bakedModelMap.put(location, bakedModel);
            return bakedModel;
         } catch (IOException var3) {
            LOGGER.warn("Failed to bake asset model: {}", location.toString(), var3);
            this.bakedModelMap.put(location, (Object)null);
            return null;
         }
      } else {
         return (SimpleBakedModel)this.bakedModelMap.get(location);
      }
   }

   private AssetLocation resolveTextureName(String name) {
      return name.equals("missingno") ? null : new AssetLocation("textures/" + name);
   }

   protected BakedQuad makeBakedQuad(BlockPart p_177589_1_, BlockPartFace p_177589_2_, EaglerTextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_, ITransformation p_177589_5_, boolean p_177589_6_) {
      return this.faceBakery.makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade);
   }

   private SimpleBakedModel bakeModel(ModelBlock modelBlock, ITransformation modelRotationIn, boolean uvLocked) throws IOException {
      Builder modelBuilder = new Builder(modelBlock, modelBlock.createOverrides());
      boolean particleTextureSet = false;
      if (modelBlock.getElements().isEmpty()) {
         return null;
      } else {
         for(BlockPart blockpart : modelBlock.getElements()) {
            for(EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
               BlockPartFace blockpartface = (BlockPartFace)blockpart.mapFaces.get(enumfacing);
               AssetLocation textureLocation = this.resolveTextureName(modelBlock.resolveTextureName(blockpartface.texture));
               if (textureLocation != null) {
                  EaglerTextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(textureLocation);
                  PngSizeInfo sizeInfo = new PngSizeInfo(new FileInputStream(AssetsModule.INSTANCE.getAssetFile(textureLocation)));
                  sprite.loadSprite(sizeInfo, true);
                  sprite.initSprite(sizeInfo.pngWidth, sizeInfo.pngHeight, 0, 0, false);
                  if (!particleTextureSet) {
                     modelBuilder.setTexture(sprite);
                     particleTextureSet = true;
                  }

                  if (blockpartface.cullFace != null && TRSRTransformation.isInteger(modelRotationIn.getMatrix())) {
                     modelBuilder.addFaceQuad(modelRotationIn.rotate(blockpartface.cullFace), this.makeBakedQuad(blockpart, blockpartface, sprite, enumfacing, modelRotationIn, uvLocked));
                  } else {
                     modelBuilder.addGeneralQuad(this.makeBakedQuad(blockpart, blockpartface, sprite, enumfacing, modelRotationIn, uvLocked));
                  }
               }
            }
         }

         return (SimpleBakedModel)modelBuilder.makeBakedModel();
      }
   }
}
