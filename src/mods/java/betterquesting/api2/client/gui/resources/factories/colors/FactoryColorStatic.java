package betterquesting.api2.client.gui.resources.factories.colors;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryColorStatic implements IFactoryData<IGuiColor, JsonObject> {
   public static final FactoryColorStatic INSTANCE = new FactoryColorStatic();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "color_static");

   public FactoryColorStatic() {
   }

   public GuiColorStatic loadFromData(JsonObject data) {
      int color;
      try {
         color = (int)Long.parseLong(JsonHelper.GetString(data, "color", "FFFFFFFF"), 16);
      } catch (NumberFormatException var4) {
         color = -1;
      }

      return new GuiColorStatic(color);
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public GuiColorStatic createNew() {
      return new GuiColorStatic(-1);
   }
}
