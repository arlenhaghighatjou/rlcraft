package net.ilexiconn.llibrary.client.gui.element;

import java.util.function.Function;
import java.util.function.Supplier;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.color.ColorScheme;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ButtonElement<T extends IElementGUI> extends Element<T> {
   public static final ColorScheme CLOSE = ColorScheme.create(() -> {
      return LLibrary.CONFIG.getDarkAccentColor();
   }, () -> {
      return -2078905;
   });
   protected String text;
   protected Function<ButtonElement<T>, Boolean> function;

   public ButtonElement(T gui, String text, float posX, float posY, int width, int height, Function<ButtonElement<T>, Boolean> function) {
      super(gui, posX, posY, width, height);
      this.text = text;
      this.function = function;
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight(), this.isEnabled() && this.isSelected(mouseX, mouseY) ? this.getColorScheme().getSecondaryColor() : this.getColorScheme().getPrimaryColor());
      FontRenderer fontRenderer = this.gui.getFontRenderer();
      fontRenderer.drawString(this.text, this.getPosX() + (float)(this.getWidth() / 2) - (float)(fontRenderer.getStringWidth(this.text) / 2), this.getPosY() + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (this.isSelected(mouseX, mouseY)) {
         if (((Boolean)this.function.apply(this)).booleanValue()) {
            this.gui.playClickSound();
         }

         return true;
      } else {
         return false;
      }
   }
}
