package betterquesting.client.themes;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.GuiKey;
import betterquesting.api2.client.gui.themes.IGuiTheme;
import betterquesting.core.BetterQuesting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ResourceTheme implements IGuiTheme {
   private final ResourceLocation ID;
   private final String dispName;
   private IGuiTheme parentTheme;
   private ResourceLocation parentID;
   private boolean cached = false;
   private final HashMap<ResourceLocation, IGuiTexture> TEX_MAP = new HashMap();
   private final HashMap<ResourceLocation, IGuiColor> COLOR_MAP = new HashMap();
   private final HashMap<ResourceLocation, IGuiLine> LINE_MAP = new HashMap();

   public ResourceTheme(ResourceLocation parentID, ResourceLocation id, String dispName) {
      this.ID = id;
      this.dispName = dispName;
      this.parentID = parentID;
   }

   public void loadFromJson(JsonObject jThm) {
      JsonObject jsonTextureRoot = JsonHelper.GetObject(jThm, "textures");

      for(Entry<String, JsonElement> entry : jsonTextureRoot.entrySet()) {
         if (((JsonElement)entry.getValue()).isJsonObject()) {
            JsonObject joTex = ((JsonElement)entry.getValue()).getAsJsonObject();
            ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joTex, "textureType", ""));
            IGuiTexture gTex = (IGuiTexture)ResourceRegistry.INSTANCE.getTexReg().createNew(typeID, joTex);
            if (gTex == null) {
               BetterQuesting.logger.error("Failed to load texture type " + typeID + " for theme " + this.ID);
            } else {
               this.setTexture(new ResourceLocation((String)entry.getKey()), gTex);
            }
         }
      }

      JsonObject jsonColourRoot = JsonHelper.GetObject(jThm, "colors");

      for(Entry<String, JsonElement> entry : jsonColourRoot.entrySet()) {
         if (entry.getValue() instanceof JsonObject) {
            JsonObject joCol = ((JsonElement)entry.getValue()).getAsJsonObject();
            ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joCol, "colorType", ""));
            IGuiColor gCol = (IGuiColor)ResourceRegistry.INSTANCE.getColorReg().createNew(typeID, joCol);
            if (gCol == null) {
               BetterQuesting.logger.error("Failed to load color type " + typeID + " for theme " + this.ID);
            } else {
               this.setColor(new ResourceLocation((String)entry.getKey()), gCol);
            }
         }
      }

      JsonObject jsonLinesRoot = JsonHelper.GetObject(jThm, "lines");

      for(Entry<String, JsonElement> entry : jsonLinesRoot.entrySet()) {
         if (entry.getValue() instanceof JsonObject) {
            JsonObject joLine = ((JsonElement)entry.getValue()).getAsJsonObject();
            ResourceLocation typeID = new ResourceLocation(JsonHelper.GetString(joLine, "lineType", ""));
            IGuiLine gLine = (IGuiLine)ResourceRegistry.INSTANCE.getLineReg().createNew(typeID, joLine);
            if (gLine == null) {
               BetterQuesting.logger.error("Failed to load line type " + typeID + " for theme " + this.ID);
            } else {
               this.setLine(new ResourceLocation((String)entry.getKey()), gLine);
            }
         }
      }

   }

   private IGuiTheme getParent() {
      if (this.cached) {
         return this.parentTheme;
      } else {
         IGuiTheme parent = ThemeRegistry.INSTANCE.getTheme(this.parentID);

         for(IGuiTheme checking = parent; checking != null && checking instanceof ResourceTheme; checking = ((ResourceTheme)checking).parentTheme) {
            if (((ResourceTheme)checking).parentTheme == this) {
               BetterQuesting.logger.error("Circular reference in resource theme " + this.ID);
               this.parentTheme = null;
               this.cached = true;
               return null;
            }
         }

         this.parentTheme = parent;
         this.cached = true;
         return this.parentTheme;
      }
   }

   public void setTexture(ResourceLocation key, IGuiTexture texture) {
      if (key != null) {
         this.TEX_MAP.put(key, texture);
      }
   }

   public void setColor(ResourceLocation key, IGuiColor color) {
      if (key != null) {
         this.COLOR_MAP.put(key, color);
      }
   }

   public void setLine(ResourceLocation key, IGuiLine line) {
      if (key != null) {
         this.LINE_MAP.put(key, line);
      }
   }

   public String getName() {
      return this.dispName;
   }

   public ResourceLocation getID() {
      return this.ID;
   }

   public IGuiTexture getTexture(ResourceLocation key) {
      IGuiTexture value = (IGuiTexture)this.TEX_MAP.get(key);
      if (value != null) {
         return value;
      } else {
         return this.getParent() != null ? this.getParent().getTexture(key) : null;
      }
   }

   public IGuiLine getLine(ResourceLocation key) {
      IGuiLine value = (IGuiLine)this.LINE_MAP.get(key);
      if (value != null) {
         return value;
      } else {
         return this.getParent() != null ? this.getParent().getLine(key) : null;
      }
   }

   public IGuiColor getColor(ResourceLocation key) {
      IGuiColor value = (IGuiColor)this.COLOR_MAP.get(key);
      if (value != null) {
         return value;
      } else {
         return this.getParent() != null ? this.getParent().getColor(key) : null;
      }
   }

   @Nullable
   public <T> Function<T, GuiScreen> getGui(GuiKey<T> key) {
      return null;
   }
}
