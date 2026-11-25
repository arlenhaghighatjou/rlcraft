package me.ichun.mods.ichunutil.client.model.item;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import org.apache.commons.lang3.tuple.Pair;

public class ModelBaseWrapper implements IBakedModel {
   private static final List<BakedQuad> DUMMY_LIST = Collections.emptyList();
   @Nonnull
   private final IModelBase modelBase;
   private final Pair<IBakedModel, Matrix4f> selfPair;
   @Nonnull
   private VertexFormat defaultVertexFormat;
   private boolean isItemDualHanded;
   private boolean disableRender;
   private TransformType currentPerspective;
   private ItemStack lastStack;
   private EntityLivingBase lastEntity;

   public ModelBaseWrapper(@Nonnull IModelBase renderer) {
      this.defaultVertexFormat = DefaultVertexFormats.ITEM;
      this.isItemDualHanded = false;
      this.disableRender = false;
      this.modelBase = renderer;
      this.selfPair = Pair.of(this, (Object)null);
   }

   public ModelBaseWrapper(@Nonnull IModelBase renderer, @Nonnull VertexFormat defVertexFormat) {
      this(renderer);
      this.defaultVertexFormat = defVertexFormat;
   }

   public ModelBaseWrapper setItemDualHanded() {
      this.isItemDualHanded = true;
      return this;
   }

   public void setDisableRender(boolean disable) {
      this.disableRender = disable;
   }

   public void handleBlockState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      this.modelBase.handleBlockState(state, side, rand);
   }

   public void bindTexture(@Nonnull ResourceLocation rs) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(rs);
   }

   protected void rebindTexture() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
   }

   public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
      if (side == null && (Tessellator.getInstance().getBuffer().isDrawing || ForgeModContainer.allowEmissiveItems)) {
         this.handleBlockState(state, side, rand);
         if (!this.disableRender) {
            Tessellator tessellator = Tessellator.getInstance();
            boolean isDrawing = tessellator.getBuffer().isDrawing;
            if (isDrawing) {
               tessellator.draw();
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.bindTexture(this.modelBase.getTexture());
            this.modelBase.renderModel(iChunUtil.eventHandlerClient.renderTick);
            this.modelBase.postRender();
            this.rebindTexture();
            GlStateManager.popMatrix();
            this.currentPerspective = null;
            this.lastStack = null;
            this.lastEntity = null;
            if (isDrawing) {
               BufferBuilder bufferbuilder = tessellator.getBuffer();
               bufferbuilder.begin(7, this.defaultVertexFormat);
            }
         }

         return DUMMY_LIST;
      } else {
         return DUMMY_LIST;
      }
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

   public TextureAtlasSprite getParticleTexture() {
      return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.modelBase.getCameraTransforms();
   }

   public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
      this.currentPerspective = cameraTransformType;
      Pair<? extends IBakedModel, Matrix4f> pair = this.modelBase.handlePerspective(cameraTransformType, this.selfPair);
      if (this.modelBase.useVanillaCameraTransform()) {
         boolean isLeft = isLeftHand(cameraTransformType);
         if (this.isItemDualHanded && isFirstPerson(this.currentPerspective) && this.lastEntity instanceof EntityPlayer && ItemRenderingHelper.dualHandedAnimationRight > 0) {
            float prog = (float)Math.sin((double)MathHelper.clamp((isLeft ? EntityHelper.interpolateValues((float)ItemRenderingHelper.prevDualHandedAnimationLeft, (float)ItemRenderingHelper.dualHandedAnimationLeft, iChunUtil.eventHandlerClient.renderTick) : EntityHelper.interpolateValues((float)ItemRenderingHelper.prevDualHandedAnimationRight, (float)ItemRenderingHelper.dualHandedAnimationRight, iChunUtil.eventHandlerClient.renderTick)) / (float)ItemRenderingHelper.dualHandedAnimationTime, 0.0F, 1.0F) * 3.141592653589793D / 4.0D);
            GlStateManager.rotate(30.0F * prog, -1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.1F * prog, 0.3F * prog);
            GlStateManager.rotate((isLeft ? -35.0F : 35.0F) * prog, 0.0F, 1.0F, 0.0F);
         }

         ItemCameraTransforms.applyTransformSide(this.getItemCameraTransforms().getTransform(cameraTransformType), isLeft);
      }

      return pair;
   }

   public ItemOverrideList getOverrides() {
      return ModelBaseWrapper.ItemOverrideListHandler.INSTANCE.setModelBaseWrapper(this);
   }

   public static boolean isFirstPerson(TransformType type) {
      return type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.FIRST_PERSON_RIGHT_HAND;
   }

   public static boolean isThirdPerson(TransformType type) {
      return type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND;
   }

   public static boolean isEntityRender(TransformType type) {
      return isFirstPerson(type) || isThirdPerson(type);
   }

   public static boolean isLeftHand(TransformType type) {
      return type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND;
   }

   public static boolean isRightHand(TransformType type) {
      return type == TransformType.FIRST_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND;
   }

   public static boolean isItemRender(TransformType type) {
      return type == null || type == TransformType.GUI || type == TransformType.GROUND || type == TransformType.NONE;
   }

   private static final class ItemOverrideListHandler extends ItemOverrideList {
      private static final ModelBaseWrapper.ItemOverrideListHandler INSTANCE = new ModelBaseWrapper.ItemOverrideListHandler();
      private ModelBaseWrapper modelBaseWrapper;

      private ItemOverrideListHandler() {
         super(ImmutableList.of());
      }

      private ModelBaseWrapper.ItemOverrideListHandler setModelBaseWrapper(ModelBaseWrapper model) {
         this.modelBaseWrapper = model;
         return this;
      }

      public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
         this.modelBaseWrapper.lastStack = stack;
         this.modelBaseWrapper.lastEntity = entity;
         this.modelBaseWrapper.modelBase.handleItemState(stack, world, entity);
         return originalModel;
      }
   }
}
