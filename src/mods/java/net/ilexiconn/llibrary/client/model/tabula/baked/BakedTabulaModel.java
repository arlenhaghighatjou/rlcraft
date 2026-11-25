package net.ilexiconn.llibrary.client.model.tabula.baked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class BakedTabulaModel implements IBakedModel {
   private ImmutableList<BakedQuad> quads;
   private TextureAtlasSprite particle;
   private ImmutableMap<TransformType, TRSRTransformation> transforms;

   public BakedTabulaModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms) {
      this.quads = quads;
      this.particle = particle;
      this.transforms = transforms;
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return (List<BakedQuad>)(side == null ? this.quads : Collections.emptyList());
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particle;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return ItemCameraTransforms.DEFAULT;
   }

   public ItemOverrideList getOverrides() {
      return ItemOverrideList.NONE;
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
      return PerspectiveMapWrapper.handlePerspective(this, this.transforms, type);
   }
}
