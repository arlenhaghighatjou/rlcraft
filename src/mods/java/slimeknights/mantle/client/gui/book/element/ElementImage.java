package slimeknights.mantle.client.gui.book.element;

import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.element.ImageData;

@SideOnly(Side.CLIENT)
public class ElementImage extends SizedBookElement {
   public ImageData image;
   public int colorMultiplier;

   public ElementImage(ImageData image) {
      this(image, 16777215);
   }

   public ElementImage(ImageData image, int colorMultiplier) {
      this(image.x, image.y, image.width, image.height, image, colorMultiplier);
   }

   public ElementImage(int x, int y, int width, int height, ImageData image) {
      this(x, y, width, height, image, image.colorMultiplier);
   }

   public ElementImage(int x, int y, int width, int height, ImageData image, int colorMultiplier) {
      super(x, y, width, height);
      this.image = image;
      if (image.x != -1) {
         x = image.x;
      }

      if (image.y != -1) {
         y = image.y;
      }

      if (image.width != -1) {
         width = image.width;
      }

      if (image.height != -1) {
         height = image.height;
      }

      if (image.colorMultiplier != 16777215) {
         colorMultiplier = image.colorMultiplier;
      }

      this.x = x == -1 ? 0 : x;
      this.y = y == -1 ? 0 : y;
      this.width = width;
      this.height = height;
      this.colorMultiplier = colorMultiplier;
   }

   public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      float r = (float)(this.colorMultiplier >> 16 & 255) / 255.0F;
      float g = (float)(this.colorMultiplier >> 8 & 255) / 255.0F;
      float b = (float)(this.colorMultiplier & 255) / 255.0F;
      GlStateManager.color(r, g, b);
      if (this.image.item == null) {
         this.renderEngine.bindTexture(this.image.location);
         drawScaledCustomSizeModalRect(this.x, this.y, (float)this.image.u, (float)this.image.v, this.image.uw, this.image.vh, this.width, this.height, (float)this.image.texWidth, (float)this.image.texHeight);
      } else {
         GlStateManager.pushMatrix();
         GlStateManager.scale((float)this.width, (float)this.height, 1.0F);
         RenderHelper.enableGUIStandardItemLighting();
         this.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)this.image.item.getItems().get(0), this.x, this.y);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.popMatrix();
      }

   }
}
