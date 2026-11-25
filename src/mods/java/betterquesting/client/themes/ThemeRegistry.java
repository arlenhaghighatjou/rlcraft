package betterquesting.client.themes;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.lines.SimpleLine;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.SlicedTexture;
import betterquesting.api2.client.gui.themes.GuiKey;
import betterquesting.api2.client.gui.themes.IGuiTheme;
import betterquesting.api2.client.gui.themes.IThemeRegistry;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetGUIs;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.client.gui2.GuiHome;
import betterquesting.client.gui2.editors.GuiFileBrowser;
import betterquesting.client.gui2.editors.GuiTextEditor;
import betterquesting.client.gui2.editors.nbt.GuiEntitySelection;
import betterquesting.client.gui2.editors.nbt.GuiFluidSelection;
import betterquesting.client.gui2.editors.nbt.GuiItemSelection;
import betterquesting.client.gui2.editors.nbt.GuiNbtEditor;
import betterquesting.core.BetterQuesting;
import betterquesting.handlers.ConfigHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ThemeRegistry implements IThemeRegistry {
   public static final ThemeRegistry INSTANCE = new ThemeRegistry();
   private static final IGuiTexture NULL_TEXTURE = new SlicedTexture(PresetTexture.TX_NULL, new GuiRectangle(0, 0, 32, 32), new GuiPadding(8, 8, 8, 8));
   private static final IGuiLine NULL_LINE = new SimpleLine();
   private static final IGuiColor NULL_COLOR = new GuiColorStatic(-16777216);
   private final HashMap<ResourceLocation, IGuiTexture> defTextures = new HashMap();
   private final HashMap<ResourceLocation, IGuiLine> defLines = new HashMap();
   private final HashMap<ResourceLocation, IGuiColor> defColors = new HashMap();
   private final HashMap<GuiKey<?>, Function<?, GuiScreen>> defGuis = new HashMap();
   private final HashMap<ResourceLocation, IGuiTheme> themes = new HashMap();
   private final List<ResourceLocation> loadedThemes = new ArrayList();
   private final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private IGuiTheme activeTheme = null;
   private boolean setup = false;

   public ThemeRegistry() {
      PresetTexture.registerTextures(this);
      PresetIcon.registerIcons(this);
      PresetLine.registerLines(this);
      PresetColor.registerColors(this);
      this.setDefaultGui(PresetGUIs.HOME, (arg) -> {
         return new GuiHome(arg.parent);
      });
      this.setDefaultGui(PresetGUIs.EDIT_NBT, (arg) -> {
         if (arg.value instanceof NBTTagCompound) {
            return new GuiNbtEditor(arg.parent, (NBTTagCompound)arg.value, arg.callback);
         } else {
            return arg.value instanceof NBTTagList ? new GuiNbtEditor(arg.parent, (NBTTagList)arg.value, arg.callback) : null;
         }
      });
      this.setDefaultGui(PresetGUIs.EDIT_ITEM, (arg) -> {
         return new GuiItemSelection(arg.parent, (BigItemStack)arg.value, arg.callback);
      });
      this.setDefaultGui(PresetGUIs.EDIT_FLUID, (arg) -> {
         return new GuiFluidSelection(arg.parent, (FluidStack)arg.value, arg.callback);
      });
      this.setDefaultGui(PresetGUIs.EDIT_ENTITY, (arg) -> {
         return new GuiEntitySelection(arg.parent, (Entity)arg.value, arg.callback);
      });
      this.setDefaultGui(PresetGUIs.EDIT_TEXT, (arg) -> {
         return new GuiTextEditor(arg.parent, (String)arg.value, arg.callback);
      });
      this.setDefaultGui(PresetGUIs.FILE_EXPLORE, (arg) -> {
         return (new GuiFileBrowser(arg.parent, arg.callback, arg.root, arg.filter)).allowMultiSelect(arg.multiSelect);
      });
   }

   public void registerTheme(IGuiTheme theme) {
      if (theme != null && theme.getID() != null) {
         if (this.themes.containsKey(theme.getID())) {
            ;
         }

         this.themes.put(theme.getID(), theme);
         if (this.activeTheme == null) {
            this.setup = false;
         }

      } else {
         throw new NullPointerException("Cannot register null theme");
      }
   }

   public void setDefaultTexture(ResourceLocation key, IGuiTexture tex) {
      if (key != null && tex != null) {
         this.defTextures.put(key, tex);
      } else {
         throw new NullPointerException("Tried to register a default theme texture with one or more NULL arguments");
      }
   }

   public void setDefaultLine(ResourceLocation key, IGuiLine line) {
      if (key != null && line != null) {
         this.defLines.put(key, line);
      } else {
         throw new NullPointerException("Tried to register a default theme line with one or more NULL arguments");
      }
   }

   public void setDefaultColor(ResourceLocation key, IGuiColor color) {
      if (key != null && color != null) {
         this.defColors.put(key, color);
      } else {
         throw new NullPointerException("Tried to register default theme colour with one or more NULL arguments");
      }
   }

   public <T> void setDefaultGui(GuiKey<T> key, Function<T, GuiScreen> func) {
      if (key != null && func != null) {
         this.defGuis.put(key, func);
      } else {
         throw new NullPointerException("Tried to register a default gui with one or more NULL arguments");
      }
   }

   public void setTheme(ResourceLocation id) {
      this.setTheme((IGuiTheme)this.themes.get(id), id);
   }

   public IGuiTheme getTheme(ResourceLocation key) {
      return key == null ? null : (IGuiTheme)this.themes.get(key);
   }

   private void setTheme(IGuiTheme theme, ResourceLocation id) {
      this.activeTheme = theme;
      BQ_Settings.curTheme = id == null ? "" : id.toString();
      if (ConfigHandler.config != null) {
         ConfigHandler.config.get("general", "Theme", "").set(BQ_Settings.curTheme);
         ConfigHandler.config.save();
      } else {
         BetterQuesting.logger.log(Level.WARN, "Unable to save theme setting");
      }

   }

   public IGuiTheme getCurrentTheme() {
      if (!this.setup && this.activeTheme == null) {
         this.activeTheme = this.getTheme(new ResourceLocation(BQ_Settings.curTheme));
         this.setup = true;
      }

      return this.activeTheme;
   }

   public void loadResourceThemes() {
      List var10000 = this.loadedThemes;
      HashMap var10001 = this.themes;
      this.themes.getClass();
      var10000.forEach(var10001::remove);
      this.loadedThemes.clear();
      IResourceManager resManager = Minecraft.getMinecraft().getResourceManager();

      for(String domain : resManager.getResourceDomains()) {
         ResourceLocation res = new ResourceLocation(domain, "bq_themes.json");

         List<IResource> list;
         try {
            list = resManager.getAllResources(res);
         } catch (Exception var31) {
            continue;
         }

         for(IResource iresource : list) {
            try {
               InputStreamReader isr = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
               Throwable var9 = null;

               try {
                  JsonArray jAry = (JsonArray)this.GSON.fromJson(isr, JsonArray.class);
                  isr.close();

                  for(int i = 0; i < jAry.size(); ++i) {
                     JsonElement je = jAry.get(i);
                     if (!(je instanceof JsonObject)) {
                        BetterQuesting.logger.log(Level.WARN, "Invalid theme entry at index " + i + " in " + iresource.getResourceLocation());
                     } else {
                        JsonObject jThm = je.getAsJsonObject();
                        if (jThm.has("themeType")) {
                           BetterQuesting.logger.warn("Deprecated legacy theme entry " + i + " in " + iresource.getResourceLocation());
                           BetterQuesting.logger.warn("Please convert this to the new format");
                        } else {
                           ResourceLocation parentID = !jThm.has("themeParent") ? null : new ResourceLocation(JsonHelper.GetString(jThm, "themeParent", "minecraft:null"));
                           String themeName = JsonHelper.GetString(jThm, "themeName", "Unnamed Theme");
                           String idRaw = JsonHelper.GetString(jThm, "themeID", themeName);
                           idRaw = idRaw.toLowerCase().trim().replaceAll(" ", "_");
                           if (!idRaw.contains(":")) {
                              idRaw = domain + ":" + idRaw;
                           }

                           ResourceLocation themeId = new ResourceLocation(idRaw);

                           for(int n = 0; this.themes.containsKey(themeId); themeId = new ResourceLocation(domain, idRaw + n++)) {
                              ;
                           }

                           ResourceTheme resTheme;
                           try {
                              resTheme = new ResourceTheme(parentID, themeId, themeName);
                           } catch (Exception var32) {
                              BetterQuesting.logger.error("Failed to load theme entry " + i + " in " + iresource.getResourceLocation(), var32);
                              continue;
                           }

                           resTheme.loadFromJson(jThm);
                           this.themes.put(resTheme.getID(), resTheme);
                           this.loadedThemes.add(resTheme.getID());
                        }
                     }
                  }
               } catch (Throwable var33) {
                  var9 = var33;
                  throw var33;
               } finally {
                  if (isr != null) {
                     if (var9 != null) {
                        try {
                           isr.close();
                        } catch (Throwable var30) {
                           var9.addSuppressed(var30);
                        }
                     } else {
                        isr.close();
                     }
                  }

               }
            } catch (Exception var35) {
               BetterQuesting.logger.error("Error reading bq_themes.json from " + iresource.getResourceLocation(), var35);
            }
         }
      }

   }

   public IGuiTexture getTexture(ResourceLocation key) {
      if (key == null) {
         return NULL_TEXTURE;
      } else {
         IGuiTexture tex = null;
         if (this.getCurrentTheme() != null) {
            tex = this.activeTheme.getTexture(key);
         }

         if (tex == null) {
            tex = (IGuiTexture)this.defTextures.get(key);
         }

         return tex == null ? NULL_TEXTURE : tex;
      }
   }

   public IGuiLine getLine(ResourceLocation key) {
      if (key == null) {
         return NULL_LINE;
      } else {
         IGuiLine line = null;
         if (this.getCurrentTheme() != null) {
            line = this.activeTheme.getLine(key);
         }

         if (line == null) {
            line = (IGuiLine)this.defLines.get(key);
         }

         return line == null ? NULL_LINE : line;
      }
   }

   public IGuiColor getColor(ResourceLocation key) {
      if (key == null) {
         return NULL_COLOR;
      } else {
         IGuiColor color = null;
         if (this.getCurrentTheme() != null) {
            color = this.activeTheme.getColor(key);
         }

         if (color == null) {
            color = (IGuiColor)this.defColors.get(key);
         }

         return color == null ? NULL_COLOR : color;
      }
   }

   public <T> GuiScreen getGui(GuiKey<T> key, T args) {
      if (key == null) {
         return null;
      } else {
         Function<T, GuiScreen> func = null;
         if (this.getCurrentTheme() != null) {
            func = this.activeTheme.getGui(key);
         }

         if (func == null) {
            func = (Function)this.defGuis.get(key);
         }

         return func == null ? null : (GuiScreen)func.apply(args);
      }
   }

   public List<IGuiTheme> getAllThemes() {
      return new ArrayList(this.themes.values());
   }

   public ResourceLocation[] getKnownTextures() {
      return (ResourceLocation[])this.defTextures.keySet().toArray(new ResourceLocation[0]);
   }

   public ResourceLocation[] getKnownColors() {
      return (ResourceLocation[])this.defColors.keySet().toArray(new ResourceLocation[0]);
   }

   public ResourceLocation[] getKnownLines() {
      return (ResourceLocation[])this.defLines.keySet().toArray(new ResourceLocation[0]);
   }

   public GuiKey[] getKnownGuis() {
      return (GuiKey[])this.defGuis.keySet().toArray(new GuiKey[0]);
   }
}
