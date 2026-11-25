package net.theawesomegem.fishingmadebetter.client;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class BakedModelRod implements IBakedModel {
   private IBakedModel modelMain;
   private IBakedModel modelMainCast;
   private IBakedModel[][] attachmentModels;
   private BakedModelRod.ItemOverrideListRod overridesList;

   public BakedModelRod(IBakedModel modelMain, IBakedModel modelMainCast, IBakedModel[][] attachmentModels) {
      this.modelMain = modelMain;
      this.modelMainCast = modelMainCast;
      this.attachmentModels = attachmentModels;
      this.overridesList = new BakedModelRod.ItemOverrideListRod(this);
   }

   public IBakedModel getCastVariant() {
      return this.modelMainCast;
   }

   public ItemOverrideList getOverrides() {
      return this.overridesList;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.modelMain.getParticleTexture();
   }

   public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
      return this.modelMain.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.modelMain.isAmbientOcclusion();
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public boolean isGui3d() {
      return this.modelMain.isGui3d();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.modelMain.getItemCameraTransforms();
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      Matrix4f matrix4f = (Matrix4f)this.modelMain.handlePerspective(cameraTransformType).getRight();
      return Pair.of(this, matrix4f);
   }

   private static class ItemOverrideListRod extends ItemOverrideList {
      private BakedModelRod modelRod;

      public ItemOverrideListRod(BakedModelRod modelRod) {
         super(Collections.emptyList());
         this.modelRod = modelRod;
      }

      public IBakedModel handleItemState(IBakedModel model, ItemStack stack, World world, EntityLivingBase entity) {
         if (stack != null && entity != null && entity instanceof EntityPlayer) {
            boolean flag = entity.getHeldItemMainhand() == stack;
            boolean flag1 = entity.getHeldItemOffhand() == stack;
            if (entity.getHeldItemMainhand().getItem() instanceof ItemFishingRod) {
               flag1 = false;
            }

            if ((flag || flag1) && entity instanceof EntityPlayer && ((EntityPlayer)entity).fishEntity != null) {
               return (new BakedModelRodFinal(this.modelRod.getCastVariant(), true, this.modelRod.attachmentModels)).setCurrentItemStack(stack);
            }
         }

         return (new BakedModelRodFinal(model, false, this.modelRod.attachmentModels)).setCurrentItemStack(stack);
      }
   }
}
