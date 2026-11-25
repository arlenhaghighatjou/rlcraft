package com.lycanitesmobs.client.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class EquipmentPartModel implements IBakedModel {
   public EquipmentPartModel() {
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return null;
   }

   public boolean isAmbientOcclusion() {
      return false;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return true;
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return null;
   }

   public ItemOverrideList getOverrides() {
      return new ItemOverrideList(new ArrayList());
   }
}
