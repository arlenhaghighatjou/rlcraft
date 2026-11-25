package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.util.ResourceLocation;

public interface IGuiTexture {
   void drawTexture(int var1, int var2, int var3, int var4, float var5, float var6);

   void drawTexture(int var1, int var2, int var3, int var4, float var5, float var6, IGuiColor var7);

   ResourceLocation getTexture();

   IGuiRect getBounds();
}
