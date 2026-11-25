package betterquesting.api2.client.gui.resources.factories.colors;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.colors.GuiColorSequence;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class FactoryColorSequence implements IFactoryData<IGuiColor, JsonObject> {
   public static final FactoryColorSequence INSTANCE = new FactoryColorSequence();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "color_sequence");
   private static final IGuiColor NULL_COL = new GuiColorStatic(-1);

   public FactoryColorSequence() {
   }

   public GuiColorSequence loadFromData(JsonObject data) {
      List<IGuiColor> layers = new ArrayList();
      float interval = JsonHelper.GetNumber(data, "interval", Float.valueOf(1.0F)).floatValue();

      for(JsonElement je : JsonHelper.GetArray(data, "colors")) {
         if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();

            try {
               IGuiColor tFact = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jo, "colorType", "null")), jo);
               layers.add(tFact);
            } catch (Exception var9) {
               layers.add(NULL_COL);
            }
         }
      }

      return new GuiColorSequence(interval, (IGuiColor[])layers.toArray(new IGuiColor[0]));
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public GuiColorSequence createNew() {
      return new GuiColorSequence(1.0F, new IGuiColor[0]);
   }
}
