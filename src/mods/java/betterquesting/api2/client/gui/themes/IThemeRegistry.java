package betterquesting.api2.client.gui.themes;

import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public interface IThemeRegistry {
   void registerTheme(IGuiTheme var1);

   IGuiTheme getCurrentTheme();

   IGuiTheme getTheme(ResourceLocation var1);

   void setTheme(ResourceLocation var1);

   void loadResourceThemes();

   IGuiTexture getTexture(ResourceLocation var1);

   IGuiColor getColor(ResourceLocation var1);

   IGuiLine getLine(ResourceLocation var1);

   <T> GuiScreen getGui(GuiKey<T> var1, T var2);

   void setDefaultTexture(ResourceLocation var1, IGuiTexture var2);

   void setDefaultColor(ResourceLocation var1, IGuiColor var2);

   void setDefaultLine(ResourceLocation var1, IGuiLine var2);

   <T> void setDefaultGui(GuiKey<T> var1, Function<T, GuiScreen> var2);

   List<IGuiTheme> getAllThemes();

   ResourceLocation[] getKnownTextures();

   ResourceLocation[] getKnownColors();

   ResourceLocation[] getKnownLines();

   GuiKey[] getKnownGuis();
}
