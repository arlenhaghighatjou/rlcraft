package mezz.jei.gui.elements;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.gui.TooltipRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.HoverChecker;

public abstract class GuiIconToggleButton {
   private final IDrawable offIcon;
   private final IDrawable onIcon;
   private final GuiIconButton button;
   private final HoverChecker hoverChecker;

   public GuiIconToggleButton(IDrawable offIcon, IDrawable onIcon) {
      this.offIcon = offIcon;
      this.onIcon = onIcon;
      this.button = new GuiIconButton(2, new DrawableBlank(0, 0), (mc, x, y) -> {
         return true;
      });
      this.hoverChecker = new HoverChecker(this.button, 0);
   }

   public void updateBounds(Rectangle area) {
      this.button.width = area.width;
      this.button.height = area.height;
      this.button.x = area.x;
      this.button.y = area.y;
   }

   public void draw(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      this.button.drawButton(minecraft, mouseX, mouseY, partialTicks);
      IDrawable icon = this.isIconToggledOn() ? this.onIcon : this.offIcon;
      icon.draw(minecraft, this.button.x + 2, this.button.y + 2);
   }

   public final boolean isMouseOver(int mouseX, int mouseY) {
      return this.hoverChecker.checkHover(mouseX, mouseY);
   }

   public final boolean handleMouseClick(int mouseX, int mouseY) {
      Minecraft minecraft = Minecraft.getMinecraft();
      return this.button.mousePressed(minecraft, mouseX, mouseY) && this.onMouseClicked(mouseX, mouseY);
   }

   public final void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.isMouseOver(mouseX, mouseY)) {
         List<String> tooltip = new ArrayList();
         this.getTooltips(tooltip);
         TooltipRenderer.drawHoveringText(minecraft, tooltip, mouseX, mouseY, 150);
      }

   }

   protected abstract void getTooltips(List<String> var1);

   protected abstract boolean isIconToggledOn();

   protected abstract boolean onMouseClicked(int var1, int var2);
}
