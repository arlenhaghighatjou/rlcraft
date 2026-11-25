package betterquesting.api2.client.gui.resources.factories.lines;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.lines.SimpleLine;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactorySimpleLine implements IFactoryData<IGuiLine, JsonObject> {
   public static final FactorySimpleLine INSTANCE = new FactorySimpleLine();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "line_simple");

   public FactorySimpleLine() {
   }

   public SimpleLine loadFromData(JsonObject data) {
      int stippleScale = JsonHelper.GetNumber(data, "stippleScale", Integer.valueOf(1)).intValue();

      short stippleMask;
      try {
         stippleMask = (short)Integer.parseInt(JsonHelper.GetString(data, "stippleMask", "1111111111111111"), 2);
      } catch (NumberFormatException var5) {
         stippleMask = -1;
      }

      return new SimpleLine(stippleScale, stippleMask);
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public SimpleLine createNew() {
      return new SimpleLine();
   }
}
