package betterquesting.api2.client.gui.resources.factories.textures;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.PolyTexture;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryPolyTextureC implements IFactoryData<IGuiTexture, JsonObject> {
   public static final FactoryPolyTextureC INSTANCE = new FactoryPolyTextureC();
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting", "poly_custom");

   public FactoryPolyTextureC() {
   }

   public IGuiTexture loadFromData(JsonObject data) {
      JsonArray jAry = JsonHelper.GetArray(data, "verts");
      double[] verts = new double[jAry.size()];

      for(int i = 0; i < jAry.size() && i < verts.length; ++i) {
         if (jAry.get(i).isJsonPrimitive()) {
            try {
               verts[i] = jAry.get(i).getAsDouble();
            } catch (Exception var12) {
               ;
            }
         }
      }

      boolean shadow = JsonHelper.GetBoolean(data, "shadow", true);
      JsonObject jCol = JsonHelper.GetObject(data, "color");

      IGuiColor color;
      try {
         color = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jCol, "colorType", "null")), jCol);
         if (color == null) {
            color = new GuiColorStatic(-1);
         }
      } catch (Exception var11) {
         color = new GuiColorStatic(-1);
      }

      int borderSize = JsonHelper.GetNumber(data, "borderSize", Integer.valueOf(0)).intValue();
      jCol = JsonHelper.GetObject(data, "borderColor");

      IGuiColor borColor;
      try {
         borColor = (IGuiColor)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getColorReg().createNew(new ResourceLocation(JsonHelper.GetString(jCol, "colorType", "null")), jCol);
         if (borColor == null) {
            borColor = new GuiColorStatic(-1);
         }
      } catch (Exception var10) {
         borColor = new GuiColorStatic(-1);
      }

      return (new PolyTexture(verts, shadow, color)).setBorder(borderSize, borColor);
   }

   public ResourceLocation getRegistryName() {
      return ID_NAME;
   }

   public IGuiTexture createNew() {
      return new PolyTexture(4, 45.0D, true, new GuiColorStatic(-1));
   }
}
