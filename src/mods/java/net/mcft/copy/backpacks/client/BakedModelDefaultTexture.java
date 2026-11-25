package net.mcft.copy.backpacks.client;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedModelDefaultTexture implements IBakedModel {
   private final IBakedModel _baseModel;
   private final List<BakedQuad>[] _sideQuads;
   private final List<BakedQuad> _defaultQuads;

   public BakedModelDefaultTexture(IBakedModel base) {
      this._baseModel = base;
      this._sideQuads = (List[])Arrays.stream(EnumFacing.VALUES).map((side) -> {
         return filterQuads(this._baseModel.getQuads((IBlockState)null, side, 0L));
      }).toArray((length) -> {
         return new List[length];
      });
      this._defaultQuads = filterQuads(this._baseModel.getQuads((IBlockState)null, (EnumFacing)null, 0L));
   }

   private static List<BakedQuad> filterQuads(List<BakedQuad> source) {
      return (List)source.stream().filter((quad) -> {
         return !quad.getSprite().getIconName().contains("overlay");
      }).collect(Collectors.toList());
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return side != null ? this._sideQuads[side.getIndex()] : this._defaultQuads;
   }

   public boolean isAmbientOcclusion() {
      return this._baseModel.isAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this._baseModel.isGui3d();
   }

   public boolean isBuiltInRenderer() {
      return this._baseModel.isBuiltInRenderer();
   }

   public EaglerTextureAtlasSprite getParticleTexture() {
      return this._baseModel.getParticleTexture();
   }

   /** @deprecated */
   @Deprecated
   public ItemCameraTransforms getItemCameraTransforms() {
      return this._baseModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this._baseModel.getOverrides();
   }
}
