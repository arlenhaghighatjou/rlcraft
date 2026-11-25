package com.creativemd.creativecore.client.rendering.model;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CreativeModel implements IModel {
   public static CreativeModel instance = new CreativeModel();
   public static CreativeBakedModel bakedModel = new CreativeBakedModel();

   public CreativeModel() {
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableSet.of();
   }

   public Collection<ResourceLocation> getTextures() {
      return ImmutableSet.of();
   }

   public IModelState getDefaultState() {
      return TRSRTransformation.identity();
   }

   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, EaglerTextureAtlasSprite> bakedTextureGetter) {
      return bakedModel;
   }
}
