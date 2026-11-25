package ivorius.reccomplex.gui;

import ivorius.reccomplex.RecurrentComplex;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTexturedButton extends GuiButton {
   private ResourceLocation texture;
   private int textureWidth;
   private int textureHeight;

   public GuiTexturedButton(int buttonId, int x, int y, ResourceLocation texture) {
      super(buttonId, x, y, "");
      this.setTexture(texture);
   }

   public GuiTexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture) {
      super(buttonId, x, y, widthIn, heightIn, "");
      this.setTexture(texture);
   }

   public GuiTexturedButton(int buttonId, int x, int y, String buttonText) {
      super(buttonId, x, y, buttonText);
   }

   public GuiTexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      super(buttonId, x, y, widthIn, heightIn, buttonText);
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void setTexture(ResourceLocation texture) {
      this.texture = texture;
      if (texture != null) {
         try {
            IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(texture);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            this.textureWidth = bufferedimage.getWidth();
            this.textureHeight = bufferedimage.getHeight();
         } catch (IOException var4) {
            RecurrentComplex.logger.error(var4);
            this.texture = null;
         }
      }

   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      super.drawButton(mc, mouseX, mouseY, partialTicks);
      if (this.visible && this.texture != null) {
         mc.getTextureManager().bindTexture(this.texture);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexture(this.x + (this.width - this.textureWidth) / 2, this.y + (this.height - this.textureHeight) / 2, 0, 0, this.textureWidth, this.textureHeight);
      }

   }

   public void drawTexture(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) / (float)this.textureWidth), (double)((float)(textureY + height) / (float)this.textureHeight)).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) / (float)this.textureWidth), (double)((float)(textureY + height) / (float)this.textureHeight)).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) / (float)this.textureWidth), (double)((float)(textureY + 0) / (float)this.textureHeight)).endVertex();
      vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) / (float)this.textureWidth), (double)((float)(textureY + 0) / (float)this.textureHeight)).endVertex();
      tessellator.draw();
   }
}
