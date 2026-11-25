package betterquesting.api2.client.gui.resources.factories.textures;

import betterquesting.api2.client.gui.resources.textures.EmptyTexture;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public class FactoryEmptyTexture implements IFactoryData<IGuiTexture, JsonObject> {
   public static final FactoryEmptyTexture INSTANCE = new FactoryEmptyTexture();
   private static final ResourceLocation RES_ID = new ResourceLocation("betterquesting", "texture_none");

   public FactoryEmptyTexture() {
   }

   public IGuiTexture loadFromData(JsonObject data) {
      return this.createNew();
   }

   public ResourceLocation getRegistryName() {
      return RES_ID;
   }

   public IGuiTexture createNew() {
      return new EmptyTexture();
   }
}
