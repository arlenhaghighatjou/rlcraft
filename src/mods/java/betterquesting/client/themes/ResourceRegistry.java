package betterquesting.client.themes;

import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.factories.colors.FactoryColorPulse;
import betterquesting.api2.client.gui.resources.factories.colors.FactoryColorSequence;
import betterquesting.api2.client.gui.resources.factories.colors.FactoryColorStatic;
import betterquesting.api2.client.gui.resources.factories.lines.FactoryLineTaxiCab;
import betterquesting.api2.client.gui.resources.factories.lines.FactorySimpleLine;
import betterquesting.api2.client.gui.resources.factories.textures.FactoryColorTexture;
import betterquesting.api2.client.gui.resources.factories.textures.FactoryEmptyTexture;
import betterquesting.api2.client.gui.resources.factories.textures.FactoryLayeredTexture;
import betterquesting.api2.client.gui.resources.factories.textures.FactoryPolyTextureC;
import betterquesting.api2.client.gui.resources.factories.textures.FactoryPolyTextureR;
import betterquesting.api2.client.gui.resources.factories.textures.FactorySimpleTexture;
import betterquesting.api2.client.gui.resources.factories.textures.FactorySlicedTexture;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.IResourceReg;
import betterquesting.api2.registry.FunctionRegistry;
import betterquesting.api2.registry.IFactoryData;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class ResourceRegistry implements IResourceReg {
   public static final ResourceRegistry INSTANCE = new ResourceRegistry();
   private final FunctionRegistry<IGuiTexture, JsonObject> TEX_REG = new FunctionRegistry<IGuiTexture, JsonObject>();
   private final FunctionRegistry<IGuiColor, JsonObject> COL_REG = new FunctionRegistry<IGuiColor, JsonObject>();
   private final FunctionRegistry<IGuiLine, JsonObject> LIN_REG = new FunctionRegistry<IGuiLine, JsonObject>();

   public ResourceRegistry() {
      this.lazyRegister(this.TEX_REG, FactorySimpleTexture.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactorySlicedTexture.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactoryLayeredTexture.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactoryColorTexture.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactoryEmptyTexture.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactoryPolyTextureR.INSTANCE, new JsonObject());
      this.lazyRegister(this.TEX_REG, FactoryPolyTextureC.INSTANCE, new JsonObject());
      this.lazyRegister(this.COL_REG, FactoryColorStatic.INSTANCE, new JsonObject());
      this.lazyRegister(this.COL_REG, FactoryColorSequence.INSTANCE, new JsonObject());
      this.lazyRegister(this.COL_REG, FactoryColorPulse.INSTANCE, new JsonObject());
      this.lazyRegister(this.LIN_REG, FactorySimpleLine.INSTANCE, new JsonObject());
      this.lazyRegister(this.LIN_REG, FactoryLineTaxiCab.INSTANCE, new JsonObject());
   }

   private <T, E> void lazyRegister(FunctionRegistry<T, E> reg, IFactoryData<T, E> factory, E template) {
      reg.register(factory.getRegistryName(), factory::loadFromData, template);
   }

   public FunctionRegistry<IGuiTexture, JsonObject> getTexReg() {
      return this.TEX_REG;
   }

   public FunctionRegistry<IGuiColor, JsonObject> getColorReg() {
      return this.COL_REG;
   }

   public FunctionRegistry<IGuiLine, JsonObject> getLineReg() {
      return this.LIN_REG;
   }
}
