package vazkii.quark.decoration.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

@SideOnly(Side.CLIENT)
public class RenderFlatItemFrame extends RenderItemFrame {
   private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
   private final Minecraft mc = Minecraft.getMinecraft();
   private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
   private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
   public static final IRenderFactory<EntityItemFrame> FACTORY = RenderFlatItemFrame::new;
   protected final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

   public RenderFlatItemFrame(RenderManager renderManagerIn) {
      super(renderManagerIn, Minecraft.getMinecraft().getRenderItem());
   }

   public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
      EntityFlatItemFrame entityFlat = (EntityFlatItemFrame)entity;
      GlStateManager.pushMatrix();
      BlockPos blockpos = entity.getHangingPosition();
      double d0 = (double)blockpos.getX() - entity.posX + x;
      double d1 = (double)blockpos.getY() - entity.posY + y;
      double d2 = (double)blockpos.getZ() - entity.posZ + z;
      GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
      if (entityFlat.realFacingDirection.getAxis() == Axis.Y) {
         GlStateManager.rotate(entityFlat.realFacingDirection == EnumFacing.DOWN ? -90.0F : 90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(entityFlat.realFacingDirection == EnumFacing.UP ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
      } else {
         GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
      }

      this.renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.enableOutlineMode(this.getTeamColor(entity));
      }

      this.renderModel(entityFlat, this.mc);
      if (this.renderOutlines) {
         GlStateManager.disableOutlineMode();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.4375F);
      boolean flipItem = entityFlat.realFacingDirection == EnumFacing.DOWN && !entityFlat.getDisplayedItem().isEmpty() && entityFlat.getDisplayedItem().getItem() instanceof ItemCompass;
      GlStateManager.rotate(flipItem ? -180.0F : 0.0F, 0.0F, 1.0F, 0.0F);
      this.renderItem(entity);
      GlStateManager.popMatrix();
      EnumFacing facing = entity.facingDirection;
      if (facing == null) {
         facing = EnumFacing.UP;
      }

      this.renderName(entity, x + (double)((float)facing.getFrontOffsetX() * 0.3F), y - (entityFlat.realFacingDirection == EnumFacing.DOWN ? 0.75D : 0.25D), z + (double)((float)facing.getFrontOffsetZ() * 0.3F));
   }

   protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
      BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      ItemStack displayStack = entity.getDisplayedItem();
      IBakedModel ibakedmodel;
      if (!displayStack.isEmpty() && displayStack.getItem() instanceof ItemMap && Items.FILLED_MAP.getMapData(displayStack, mc.world) != null) {
         ibakedmodel = modelmanager.getModel(this.mapModel);
      } else {
         ibakedmodel = modelmanager.getModel(this.itemFrameModel);
      }

      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
      return null;
   }

   private void renderItem(EntityItemFrame itemFrame) {
      ItemStack itemstack = itemFrame.getDisplayedItem();
      if (!itemstack.isEmpty()) {
         EntityItem entityitem = new EntityItem(itemFrame.getEntityWorld(), 0.0D, 0.0D, 0.0D, itemstack);
         Item item = entityitem.getItem().getItem();
         entityitem.getItem().setCount(1);
         entityitem.hoverStart = 0.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         int i = itemFrame.getRotation();
         if (item instanceof ItemMap) {
            i = i % 4 * 2;
         }

         GlStateManager.rotate((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         RenderItemInFrameEvent event = new RenderItemInFrameEvent(itemFrame, this);
         if (!MinecraftForge.EVENT_BUS.post(event)) {
            if (item instanceof ItemMap) {
               this.renderManager.renderEngine.bindTexture(MAP_BACKGROUND_TEXTURES);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               float f = 0.0078125F;
               GlStateManager.scale(f, f, f);
               GlStateManager.translate(-64.0F, -64.0F, 0.0F);
               MapData mapdata = Items.FILLED_MAP.getMapData(entityitem.getItem(), itemFrame.getEntityWorld());
               GlStateManager.translate(0.0F, 0.0F, -1.0F);
               if (mapdata != null) {
                  this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
               }
            } else {
               ItemStack stack = entityitem.getItem();
               this.renderItemStack(itemFrame, stack);
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }

   }

   protected void renderItemStack(EntityItemFrame itemFrame, ItemStack stack) {
      this.transformItem(itemFrame, stack);
      GlStateManager.pushAttrib();
      RenderHelper.enableStandardItemLighting();
      this.itemRenderer.renderItem(stack, TransformType.FIXED);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popAttrib();
   }

   protected void transformItem(EntityItemFrame frame, ItemStack stack) {
      if (stack.getItem() instanceof ItemBlock && ((EntityFlatItemFrame)frame).realFacingDirection.getAxis().equals(Axis.Y)) {
         GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.scale(0.5F, 0.5F, 0.5F);
   }

   protected void renderName(@Nonnull EntityItemFrame entity, double x, double y, double z) {
      if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
         double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
         float f = entity.isSneaking() ? 32.0F : 64.0F;
         if (d0 < (double)(f * f)) {
            String s = entity.getDisplayedItem().getDisplayName();
            this.renderLivingLabel(entity, s, x, y, z, 64);
         }
      }

   }
}
