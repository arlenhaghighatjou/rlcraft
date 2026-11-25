package betterquesting.api2.client.gui.resources.factories.colors;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.colors.GuiColorPulse;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryColorPulse implements IFactoryData<IGuiColor, JsonObject> {
   public static final FactoryColorPulse INSTANCE = new FactoryColorPulse();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "color_pulse");

   public FactoryColorPulse() {
   }

   public GuiColorPulse loadFromData(JsonObject data) {
      float period = JsonHelper.GetNumber(data, "period", Float.valueOf(1.0F)).floatValue();
      float phase = JsonHelper.GetNumber(data, "phase", Float.valueOf(1.0F)).floatValue();
      JsonObject jo1 = JsonHelper.GetObject(data, "color1");
      IGuiColor color1 = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jo1, "colorType", "null")), jo1);
      if (color1 == null) {
         color1 = new GuiColorStatic(-1);
      }

      JsonObject jo2 = JsonHelper.GetObject(data, "color2");
      IGuiColor color2 = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jo2, "colorType", "null")), jo2);
      if (color2 == null) {
         color2 = new GuiColorStatic(-1);
      }

      return new GuiColorPulse(color1, color2, (double)period, phase);
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public GuiColorPulse createNew() {
      return new GuiColorPulse(-1, -1, 1.0D, 1.0F);
   }
}
