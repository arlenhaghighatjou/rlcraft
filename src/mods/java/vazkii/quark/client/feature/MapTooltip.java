package vazkii.quark.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class MapTooltip extends Feature {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   public static boolean requireShift;

   public MapTooltip() {
   }

   public void setupConfig() {
      requireShift = this.loadPropBool("Needs Shift to be visible", "", false);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void makeTooltip(ItemTooltipEvent event) {
      if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemMap && requireShift && !GuiScreen.isShiftKeyDown()) {
         event.getToolTip().add(1, I18n.format("quarkmisc.mapShift", new Object[0]));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTooltip(PostText event) {
      if (!event.getStack().isEmpty() && event.getStack().getItem() instanceof ItemMap && (!requireShift || GuiScreen.isShiftKeyDown())) {
         Minecraft mc = Minecraft.getMinecraft();
         MapData mapdata = Items.FILLED_MAP.getMapData(event.getStack(), mc.world);
         if (mapdata == null) {
            return;
         }

         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         RenderHelper.disableStandardItemLighting();
         mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer buffer = tessellator.getBuffer();
         int pad = 7;
         float size = 135.0F;
         float scale = 0.5F;
         GlStateManager.translate((float)event.getX(), (float)event.getY() - size * scale - 5.0F, 0.0F);
         GlStateManager.scale(scale, scale, scale);
         buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
         buffer.pos((double)(-pad), (double)size, 0.0D).tex(0.0D, 1.0D).endVertex();
         buffer.pos((double)size, (double)size, 0.0D).tex(1.0D, 1.0D).endVertex();
         buffer.pos((double)size, (double)(-pad), 0.0D).tex(1.0D, 0.0D).endVertex();
         buffer.pos((double)(-pad), (double)(-pad), 0.0D).tex(0.0D, 0.0D).endVertex();
         tessellator.draw();
         mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
