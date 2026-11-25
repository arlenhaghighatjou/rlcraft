package betterquesting.api2.client.gui.resources.factories.textures;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.SimpleTexture;
import betterquesting.api2.client.gui.resources.textures.SlideShowTexture;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class FactorySlideShowTexture implements IFactoryData<IGuiTexture, JsonObject> {
   public static final FactorySlideShowTexture INSTANCE = new FactorySlideShowTexture();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "texture_slides");
   private static final IGuiTexture NULL_TX = (new SimpleTexture(PresetTexture.TX_NULL, new GuiRectangle(0, 0, 16, 16))).maintainAspect(false);

   public FactorySlideShowTexture() {
   }

   public SlideShowTexture loadFromData(JsonObject data) {
      List<IGuiTexture> layers = new ArrayList();
      float interval = JsonHelper.GetNumber(data, "interval", Float.valueOf(1.0F)).floatValue();

      for(JsonElement je : JsonHelper.GetArray(data, "slides")) {
         if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();

            try {
               IGuiTexture tFact = (IGuiTexture)((IResourceReg)QuestingAPI.getAPI(ApiReference.RESOURCE_REG)).getTexReg().createNew(new ResourceLocation(JsonHelper.GetString(jo, "textureType", "null")), jo);
               layers.add(tFact);
            } catch (Exception var9) {
               layers.add(NULL_TX);
            }
         }
      }

      return new SlideShowTexture(interval, (IGuiTexture[])layers.toArray(new IGuiTexture[0]));
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public SlideShowTexture createNew() {
      return new SlideShowTexture(1.0F, new IGuiTexture[0]);
   }
}
