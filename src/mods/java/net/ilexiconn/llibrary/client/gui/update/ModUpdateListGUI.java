package net.ilexiconn.llibrary.client.gui.update;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.ilexiconn.llibrary.server.update.UpdateContainer;
import net.ilexiconn.llibrary.server.update.UpdateHandler;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.vector.Vector2f;

@SideOnly(Side.CLIENT)
public class ModUpdateListGUI extends GuiScrollingList {
   private ModUpdateGUI parent;
   private Map<Integer, ResourceLocation> cachedLogo;
   private Map<Integer, Vector2f> cachedLogoDimensions;

   public ModUpdateListGUI(ModUpdateGUI parent, int width) {
      super(parent.mc, width, parent.height, 32, parent.height - 55, 10, 35, parent.width, parent.height);
      this.parent = parent;
      this.cachedLogo = new HashMap();
      this.cachedLogoDimensions = new HashMap();
   }

   protected int getSize() {
      return UpdateHandler.INSTANCE.getOutdatedModList().size();
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.parent.selectModIndex(index);
   }

   protected boolean isSelected(int index) {
      return this.parent.modIndexSelected(index);
   }

   protected void drawBackground() {
      this.parent.drawDefaultBackground();
   }

   protected int getContentHeight() {
      return this.getSize() * 35 + 1;
   }

   protected void drawSlot(int idx, int right, int top, int height, Tessellator tess) {
      UpdateContainer updateContainer = (UpdateContainer)UpdateHandler.INSTANCE.getOutdatedModList().get(idx);
      String name = StringUtils.stripControlCodes(updateContainer.getModContainer().getName());
      String version = StringUtils.stripControlCodes(updateContainer.getLatestVersion().getVersionString());
      FontRenderer font = ClientProxy.MINECRAFT.fontRenderer;
      if (!this.cachedLogo.containsKey(Integer.valueOf(idx))) {
         BufferedImage icon = updateContainer.getIcon();
         if (icon != null) {
            this.cachedLogo.put(Integer.valueOf(idx), ClientProxy.MINECRAFT.getTextureManager().getDynamicTextureLocation("mod_icon", new DynamicTexture(icon)));
            this.cachedLogoDimensions.put(Integer.valueOf(idx), new Vector2f((float)icon.getWidth(), (float)icon.getHeight()));
         }
      }

      boolean hasIcon = this.cachedLogo.containsKey(Integer.valueOf(idx));
      font.drawString(font.trimStringToWidth(name, this.listWidth - 10), hasIcon ? this.left + 36 : this.left + 6, top + 7, 16777215);
      font.drawString(font.trimStringToWidth(version, this.listWidth - 10), hasIcon ? this.left + 36 : this.left + 6, top + 17, 13421772);
      if (hasIcon) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         ClientProxy.MINECRAFT.renderEngine.bindTexture((ResourceLocation)this.cachedLogo.get(Integer.valueOf(idx)));
         float scaleX = ((Vector2f)this.cachedLogoDimensions.get(Integer.valueOf(idx))).getX() / 32.0F;
         float scaleY = ((Vector2f)this.cachedLogoDimensions.get(Integer.valueOf(idx))).getY() / 32.0F;
         float scale = 1.0F;
         if (scaleX > 1.0F || scaleY > 1.0F) {
            scale = 1.0F / Math.max(scaleX, scaleY);
         }

         float iconWidth = ((Vector2f)this.cachedLogoDimensions.get(Integer.valueOf(idx))).getX() * scale;
         float iconHeight = ((Vector2f)this.cachedLogoDimensions.get(Integer.valueOf(idx))).getY() * scale;
         int offset = 12;
         WorldRenderer renderer = tess.getBuffer();
         renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         renderer.pos((double)offset, (double)((float)top + iconHeight), 0.0D).tex(0.0D, 1.0D).endVertex();
         renderer.pos((double)((float)offset + iconWidth), (double)((float)top + iconHeight), 0.0D).tex(1.0D, 1.0D).endVertex();
         renderer.pos((double)((float)offset + iconWidth), (double)top, 0.0D).tex(1.0D, 0.0D).endVertex();
         renderer.pos((double)offset, (double)top, 0.0D).tex(0.0D, 0.0D).endVertex();
         tess.draw();
      }

   }

   public int getWidth() {
      return this.listWidth;
   }

   public int getRight() {
      return this.right;
   }
}
