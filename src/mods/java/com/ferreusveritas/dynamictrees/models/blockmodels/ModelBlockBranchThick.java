package com.ferreusveritas.dynamictrees.models.blockmodels;

import com.ferreusveritas.dynamictrees.event.TextureGenerationHandler;
import com.ferreusveritas.dynamictrees.models.bakedmodels.BakedModelBlockBranchThick;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBlockBranchThick extends ModelBlockBranchBasic {
   public ResourceLocation thickRingsTexture;

   public ModelBlockBranchThick(ModelBlock modelBlock) {
      super(modelBlock);
      this.thickRingsTexture = TextureGenerationHandler.addRingTextureLocation(this.ringsTexture);
   }

   public Collection<ResourceLocation> getTextures() {
      return ImmutableList.copyOf(new ResourceLocation[]{this.barkTexture, this.ringsTexture, this.thickRingsTexture});
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      try {
         return new BakedModelBlockBranchThick(this.barkTexture, this.ringsTexture, this.thickRingsTexture, bakedTextureGetter);
      } catch (Exception var5) {
         System.err.println("BranchModel.bake() failed due to exception:" + var5);
         return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
      }
   }
}
