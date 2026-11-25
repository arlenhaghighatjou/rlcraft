package vazkii.quark.decoration.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.decoration.entity.EntityFlatItemFrame;

public class RenderGlassItemFrame extends RenderFlatItemFrame {
   public static final IRenderFactory<EntityItemFrame> FACTORY = RenderGlassItemFrame::new;
   private final TileEntityBanner banner = new TileEntityBanner();

   public RenderGlassItemFrame(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void renderModel(EntityFlatItemFrame entity, Minecraft mc) {
      BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      if (entity.getDisplayedItem().isEmpty()) {
         IBakedModel ibakedmodel = modelmanager.getModel((ModelResourceLocation)ModelHandler.resourceLocations.get("glass_item_frame_world"));
         blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   protected void renderItemStack(EntityItemFrame itemFrame, ItemStack stack) {
      if (stack.getItem() instanceof ItemBanner) {
         this.banner.setItemValues(stack, false);
         ResourceLocation res = BannerTextures.BANNER_DESIGNS.getResourceLocation(this.banner.getPatternResourceLocation(), this.banner.getPatternList(), this.banner.getColorList());
         if (res != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(res);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer buffer = tessellator.getBuffer();
            float f = 0.015625F;
            float u = 1.0F * f;
            float v = 1.0F * f;
            float w = 20.0F * f;
            float h = 40.0F * f;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.058F);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(0.0D, 1.0D, 0.0D).tex((double)(u + 0.0F), (double)(v + h)).endVertex();
            buffer.pos(1.0D, 1.0D, 0.0D).tex((double)(u + w), (double)(v + h)).endVertex();
            buffer.pos(1.0D, 0.0D, 0.0D).tex((double)(u + w), (double)(v + 0.0F)).endVertex();
            buffer.pos(0.0D, 0.0D, 0.0D).tex((double)(u + 0.0F), (double)(v + 0.0F)).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
         }
      } else {
         super.renderItemStack(itemFrame, stack);
      }

   }

   protected void transformItem(EntityItemFrame frame, ItemStack stack) {
      float s = 1.5F;
      if (stack.getItem() instanceof ItemShield) {
         s = 4.0F;
         GlStateManager.translate(-0.25F, 0.0F, 0.2F);
         GlStateManager.scale(s, s, s);
      } else {
         GlStateManager.translate(0.0F, 0.0F, 0.05F);
         GlStateManager.scale(s, s, s);
      }

      super.transformItem(frame, stack);
   }
}
