package betterquesting.api2.client.gui.resources.factories.textures;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.ColorTexture;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryColorTexture implements IFactoryData<IGuiTexture, JsonObject> {
   public static final FactoryColorTexture INSTANCE = new FactoryColorTexture();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "texture_color");

   public FactoryColorTexture() {
   }

   public ColorTexture loadFromData(JsonObject data) {
      int[] bounds = new int[]{0, 0, 0, 0};
      JsonArray jAry = JsonHelper.GetArray(data, "padding");

      for(int i = 0; i < jAry.size() && i < bounds.length; ++i) {
         if (jAry.get(i).isJsonPrimitive()) {
            try {
               bounds[i] = jAry.get(i).getAsInt();
            } catch (Exception var8) {
               ;
            }
         }
      }

      JsonObject jCol = JsonHelper.GetObject(data, "color");

      IGuiColor color;
      try {
         color = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jCol, "colorType", "null")), jCol);
         if (color == null) {
            color = new GuiColorStatic(-1);
         }
      } catch (Exception var7) {
         color = new GuiColorStatic(-1);
      }

      return new ColorTexture(color, new GuiPadding(bounds[0], bounds[1], bounds[2], bounds[3]));
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public ColorTexture createNew() {
      return new ColorTexture(new GuiColorStatic(-1), new GuiPadding(0, 0, 0, 0));
   }
}
