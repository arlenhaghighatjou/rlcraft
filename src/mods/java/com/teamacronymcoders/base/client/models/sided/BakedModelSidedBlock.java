package com.teamacronymcoders.base.client.models.sided;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.blocks.properties.PropertySideType;
import com.teamacronymcoders.base.blocks.properties.SideType;
import com.teamacronymcoders.base.client.models.ModelUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.util.vector.Vector3f;

public class BakedModelSidedBlock implements IBakedModel {
   final String name;
   public TextureAtlasSprite[][] textures;
   private static final ItemCameraTransforms defaultTransforms = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(75.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.25F, 0.0F), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(75.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.15625F, 0.0F), new Vector3f(0.375F, 0.375F, 0.375F)), new ItemTransformVec3f(new Vector3f(0.0F, 45.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0.0F, 225.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.4F, 0.4F, 0.4F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F)), new ItemTransformVec3f(new Vector3f(30.0F, 225.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.625F, 0.625F, 0.625F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.1875F, 0.0F), new Vector3f(0.25F, 0.25F, 0.25F)), new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.5F, 0.5F, 0.5F)));

   public BakedModelSidedBlock(String name, TextureAtlasSprite[][] textures) {
      this.name = name;
      this.textures = textures;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      TextureAtlasSprite[] tex = new TextureAtlasSprite[6];

      for(int i = 0; i < tex.length; ++i) {
         tex[i] = this.textures[i][0];
      }

      char[] keyArray = "000000".toCharArray();
      if (state instanceof IExtendedBlockState) {
         IExtendedBlockState extended = (IExtendedBlockState)state;

         for(int i = 0; i < PropertySideType.SIDE_TYPE.length; ++i) {
            if (extended.getUnlistedNames().contains(PropertySideType.SIDE_TYPE[i])) {
               SideType config = (SideType)extended.getValue(PropertySideType.SIDE_TYPE[i]);
               if (config != null) {
                  int c = config.ordinal();
                  tex[i] = this.textures[i][c];
                  keyArray[i] = Character.forDigit(c, 10);
               }
            }
         }
      }

      String key = this.name + String.copyValueOf(keyArray);
      if (!ModelLoaderSidedBlock.getInstance().getModelCache().containsKey(key)) {
         ModelLoaderSidedBlock.getInstance().getModelCache().put(key, bakeQuads(tex));
      }

      return (List)ModelLoaderSidedBlock.getInstance().getModelCache().get(key);
   }

   private static List<BakedQuad> bakeQuads(TextureAtlasSprite[] sprites) {
      List<BakedQuad> quads = Lists.newArrayListWithExpectedSize(6);
      float[] colour = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      Vector3f[] vertices = new Vector3f[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 0.0F, 0.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.DOWN, sprites[0], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colour, true));
      vertices = new Vector3f[]{new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 1.0F, 0.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.UP, sprites[1], new double[]{0.0D, 0.0D, 16.0D, 16.0D}, colour, false));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.NORTH, sprites[2], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colour, true));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(0.0F, 0.0F, 1.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.SOUTH, sprites[3], new double[]{16.0D, 16.0D, 0.0D, 0.0D}, colour, false));
      vertices = new Vector3f[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 1.0F), new Vector3f(0.0F, 0.0F, 1.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.WEST, sprites[4], new double[]{0.0D, 16.0D, 16.0D, 0.0D}, colour, true));
      vertices = new Vector3f[]{new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Vector3f(1.0F, 0.0F, 1.0F)};
      quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.EAST, sprites[5], new double[]{16.0D, 16.0D, 0.0D, 0.0D}, colour, false));
      return quads;
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return true;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   @Nonnull
   public TextureAtlasSprite getParticleTexture() {
      return this.textures[0][0];
   }

   @Nonnull
   public ItemCameraTransforms getItemCameraTransforms() {
      return defaultTransforms;
   }

   @Nonnull
   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }
}
