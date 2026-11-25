package com.bloodnbonesgaming.loadingscreens.client;

import com.bloodnbonesgaming.lib.util.script.ScriptMethodDocumentation;
import com.bloodnbonesgaming.lib.util.script.ScriptUtil;
import com.bloodnbonesgaming.loadingscreens.client.gui.EnumGuiLocation;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementBase;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementText;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextRandom;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureAnimated;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureStretch;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureTile;
import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiWorkingOverride;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class LoadingScreenHandler {
   private static LoadingScreenHandler instance;
   private List<GuiElementBase> elements = new ArrayList();
   private boolean setup = false;

   public LoadingScreenHandler() {
   }

   public static LoadingScreenHandler getInstance() {
      return instance;
   }

   public void setup(Minecraft minecraft) {
   }

   public void renderScreen(Minecraft minecraft, int width, int height) {
      if (!this.setup) {
         this.setup(minecraft);
         this.setup = true;
      }

      for(GuiElementBase element : this.elements) {
         GlStateManager.enableAlpha();
         element.render(Minecraft.getMinecraft(), width, height);
         GlStateManager.disableAlpha();
      }

   }

   public void renderBufferLoadingScreen(Minecraft minecraft, int width, int height) {
      if (!(minecraft.currentScreen instanceof GuiWorkingOverride)) {
         GuiWorkingOverride gui = new GuiWorkingOverride();
         gui.title = "This is overriding the base loading screen";
         minecraft.displayGuiScreen(gui);
      }

   }

   public static void load() {
      instance = new LoadingScreenHandler();
      File file = new File("./config/loadingscreens/");
      if (!file.exists()) {
         file.mkdirs();
      } else {
         ScriptUtil.readScript(new File("./config/loadingscreens/LoadingScreens.txt"), instance, (Map)null);
      }

   }

   @ScriptMethodDocumentation(
      args = "String, String",
      usage = "anchor point, texture location",
      notes = "Adds a tiled texture. Documentation can be found in documentation/gui/guiElements/Tiled_Texture.txt"
   )
   public GuiElementTextureTile addTiledTexture(String location, String texture) throws Exception {
      EnumGuiLocation eLocation = EnumGuiLocation.valueOf(location.toUpperCase());
      if (eLocation != null) {
         GuiElementTextureTile element = new GuiElementTextureTile(eLocation, new ResourceLocation(texture));
         this.elements.add(element);
         return element;
      } else {
         throw new Exception(location + " is not a valid gui location!");
      }
   }

   @ScriptMethodDocumentation(
      args = "String, String",
      usage = "anchor point, texture location",
      notes = "Adds a stretched texture. Documentation can be found in documentation/gui/guiElements/Stretched_Texture.txt"
   )
   public GuiElementTextureStretch addStretchedTexture(String location, String texture) throws Exception {
      EnumGuiLocation eLocation = EnumGuiLocation.valueOf(location.toUpperCase());
      if (eLocation != null) {
         GuiElementTextureStretch element = new GuiElementTextureStretch(eLocation, new ResourceLocation(texture));
         this.elements.add(element);
         return element;
      } else {
         throw new Exception(location + " is not a valid gui location!");
      }
   }

   @ScriptMethodDocumentation(
      args = "String, String",
      usage = "anchor point, texture location",
      notes = "Adds an animated texture. Documentation can be found in documentation/gui/guiElements/Animated_Texture.txt"
   )
   public GuiElementTextureAnimated addAnimatedTexture(String location, String texture) throws Exception {
      EnumGuiLocation eLocation = EnumGuiLocation.valueOf(location.toUpperCase());
      if (eLocation != null) {
         try {
            GuiElementTextureAnimated element = new GuiElementTextureAnimated(eLocation, new ResourceLocation(texture));
            this.elements.add(element);
            return element;
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      } else {
         throw new Exception(location + " is not a valid gui location!");
      }
   }

   @ScriptMethodDocumentation(
      args = "String, String",
      usage = "anchor point, text",
      notes = "Adds a text string. Documentation can be found in documentation/gui/guiElements/Text.txt"
   )
   public GuiElementText addText(String location, String text) throws Exception {
      EnumGuiLocation eLocation = EnumGuiLocation.valueOf(location.toUpperCase());
      if (eLocation != null) {
         GuiElementText element = new GuiElementText(eLocation, text);
         this.elements.add(element);
         return element;
      } else {
         throw new Exception(location + " is not a valid gui location!");
      }
   }

   @ScriptMethodDocumentation(
      args = "String, String",
      usage = "anchor point, text",
      notes = "Adds a text string. Documentation can be found in documentation/gui/guiElements/Text.txt"
   )
   public GuiElementTextRandom addRandomText(String location) throws Exception {
      EnumGuiLocation eLocation = EnumGuiLocation.valueOf(location.toUpperCase());
      if (eLocation != null) {
         GuiElementTextRandom element = new GuiElementTextRandom(eLocation);
         this.elements.add(element);
         return element;
      } else {
         throw new Exception(location + " is not a valid gui location!");
      }
   }
}
