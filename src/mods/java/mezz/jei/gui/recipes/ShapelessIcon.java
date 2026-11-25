package mezz.jei.gui.recipes;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.client.config.HoverChecker;

public class ShapelessIcon {
   private static final int scale = 4;
   private final IDrawable icon = Internal.getHelpers().getGuiHelper().getShapelessIcon();
   private final HoverChecker hoverChecker;

   public ShapelessIcon() {
      int iconBottom = this.icon.getHeight() / 4;
      int iconLeft = 116 - this.icon.getWidth() / 4;
      int iconRight = iconLeft + this.icon.getWidth() / 4;
      this.hoverChecker = new HoverChecker(0, iconBottom, iconLeft, iconRight, 0);
   }

   public void draw(Minecraft minecraft, int recipeWidth) {
      int shapelessIconX = recipeWidth - this.icon.getWidth() / 4;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)shapelessIconX, 0.0F, 0.0F);
      GlStateManager.scale(0.25D, 0.25D, 1.0D);
      this.icon.draw(minecraft);
      GlStateManager.popMatrix();
   }

   @Nullable
   public List<String> getTooltipStrings(int mouseX, int mouseY) {
      return this.hoverChecker.checkHover(mouseX, mouseY) ? Collections.singletonList(Translator.translateToLocal("jei.tooltip.shapeless.recipe")) : null;
   }
}
