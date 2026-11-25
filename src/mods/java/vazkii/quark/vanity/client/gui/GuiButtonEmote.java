package vazkii.quark.vanity.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.vanity.client.emotes.EmoteDescriptor;

public class GuiButtonEmote extends GuiButtonTranslucent {
   public final EmoteDescriptor desc;

   public GuiButtonEmote(int buttonId, int x, int y, EmoteDescriptor desc) {
      super(buttonId, x, y, 24, 24, "");
      this.desc = desc;
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partial) {
      super.drawButton(mc, mouseX, mouseY, partial);
      if (this.visible) {
         mc.getTextureManager().bindTexture(this.desc.texture);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         drawModalRectWithCustomSizedTexture(this.x + 4, this.y + 4, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         ResourceLocation tierTexture = this.desc.getTierTexture();
         if (tierTexture != null) {
            mc.getTextureManager().bindTexture(tierTexture);
            drawModalRectWithCustomSizedTexture(this.x + 4, this.y + 4, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }

         boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         if (hovered) {
            String name = this.desc.getLocalizedName();
            mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
            int w = mc.fontRenderer.getStringWidth(name);
            int left = this.x - w;
            int top = this.y - 8;
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0.0F, 0.0F, 100.0F);
            this.drawActualTexturedModalRect(left, top, 242, 9, 5, 17);

            for(int i = 0; i < w; ++i) {
               this.drawActualTexturedModalRect(left + i + 5, top, 248, 9, 1, 17);
            }

            this.drawActualTexturedModalRect(left + w + 5, top, 250, 9, 6, 17);
            mc.fontRenderer.drawString(name, left + 5, top + 3, 0);
            GlStateManager.popMatrix();
         }
      }

   }
}
