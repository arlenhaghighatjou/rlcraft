package com.bloodnbonesgaming.loadingscreens.client;

import com.bloodnbonesgaming.lib.util.script.ScriptCall;
import com.bloodnbonesgaming.lib.util.script.ScriptTarget;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.bloodnbonesgaming.lib.util.script.ScriptUtil;
import com.bloodnbonesgaming.loadingscreens.client.gui.EnumGuiLocation;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementBase;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementText;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextRandom;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureAnimated;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureStretch;
import com.bloodnbonesgaming.loadingscreens.client.gui.GuiElementTextureTile;
import com.bloodnbonesgaming.loadingscreens.client.gui.override.GuiWorkingOverride;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class LoadingScreenHandler implements ScriptTarget {
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
      VFile2 file = new VFile2("config/loadingscreens/LoadingScreens.txt");
      if (file.exists()) {
         ScriptUtil.readScript(file, instance, (Map)null);
      }

   }

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

   public Object call(String method, Object[] args) throws Exception {
      if (args.length == 1 && method.equals("addRandomText") && ScriptCall.isString(args[0])) {
         return this.addRandomText((String)args[0]);
      }

      if (args.length == 2 && ScriptCall.isString(args[0]) && ScriptCall.isString(args[1])) {
         if (method.equals("addTiledTexture")) {
            return this.addTiledTexture((String)args[0], (String)args[1]);
         }

         if (method.equals("addStretchedTexture")) {
            return this.addStretchedTexture((String)args[0], (String)args[1]);
         }

         if (method.equals("addAnimatedTexture")) {
            return this.addAnimatedTexture((String)args[0], (String)args[1]);
         }

         if (method.equals("addText")) {
            return this.addText((String)args[0], (String)args[1]);
         }
      }

      return NO_METHOD;
   }
}
