package betterquesting.api2.client.gui.resources.factories.lines;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.lines.LineTaxiCab;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryLineTaxiCab implements IFactoryData<IGuiLine, JsonObject> {
   public static final FactoryLineTaxiCab INSTANCE = new FactoryLineTaxiCab();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "line_manhattan");

   public FactoryLineTaxiCab() {
   }

   public LineTaxiCab loadFromData(JsonObject data) {
      float bias = JsonHelper.GetNumber(data, "bias", Float.valueOf(0.5F)).floatValue();
      boolean isVertical = JsonHelper.GetBoolean(data, "isVertical", false);
      int stippleScale = JsonHelper.GetNumber(data, "stippleScale", Integer.valueOf(1)).intValue();

      short stippleMask;
      try {
         stippleMask = (short)Integer.parseInt(JsonHelper.GetString(data, "stippleMask", "1111111111111111"), 2);
      } catch (NumberFormatException var7) {
         stippleMask = -1;
      }

      return new LineTaxiCab(bias, isVertical, stippleScale, stippleMask);
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public LineTaxiCab createNew() {
      return new LineTaxiCab();
   }
}
