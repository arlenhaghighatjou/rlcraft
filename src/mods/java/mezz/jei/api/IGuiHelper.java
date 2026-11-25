package mezz.jei.api;

import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableBuilder;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITickTimer;
import net.minecraft.util.ResourceLocation;

public interface IGuiHelper {
   default IDrawableStatic createDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height) {
      return this.drawableBuilder(resourceLocation, u, v, width, height).build();
   }

   IDrawableBuilder drawableBuilder(ResourceLocation var1, int var2, int var3, int var4, int var5);

   IDrawableAnimated createAnimatedDrawable(IDrawableStatic var1, int var2, IDrawableAnimated.StartDirection var3, boolean var4);

   IDrawableStatic getSlotDrawable();

   IDrawableStatic createBlankDrawable(int var1, int var2);

   <V> IDrawable createDrawableIngredient(V var1);

   ICraftingGridHelper createCraftingGridHelper(int var1, int var2);

   ITickTimer createTickTimer(int var1, int var2, boolean var3);

   /** @deprecated */
   @Deprecated
   default IDrawableStatic createDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height, int textureWidth, int textureHeight) {
      return this.drawableBuilder(resourceLocation, u, v, width, height).setTextureSize(textureWidth, textureHeight).build();
   }

   /** @deprecated */
   @Deprecated
   default IDrawableStatic createDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
      return this.drawableBuilder(resourceLocation, u, v, width, height).addPadding(paddingTop, paddingBottom, paddingLeft, paddingRight).build();
   }
}
