package net.ilexiconn.llibrary.client.gui.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.config.property.ForgeConfigProperty;
import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.ilexiconn.llibrary.client.gui.element.ListElement;
import net.ilexiconn.llibrary.client.gui.element.color.ColorScheme;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConfigGUI extends ElementGUI {
   public static final ColorScheme RETURN = ColorScheme.create(() -> {
      return LLibrary.CONFIG.getPrimaryColor();
   }, () -> {
      return LLibrary.CONFIG.getSecondaryColor();
   });
   public static final ColorScheme SIDEBAR = ColorScheme.create(() -> {
      return LLibrary.CONFIG.getPrimaryColor();
   }, () -> {
      return LLibrary.CONFIG.getTertiaryColor();
   });
   public static final ResourceLocation SETTINGS_ICON = new ResourceLocation("llibrary", "textures/gui/settings.png");
   protected GuiScreen parent;
   protected String parentName;
   protected List<ConfigCategory> categories;
   protected Map<ConfigProperty, Element<ConfigGUI>> propertyElements;
   protected ConfigCategory selectedCategory;
   private Mod mod;

   public ConfigGUI(GuiScreen parent, Object mod, Configuration config) {
      this(parent, mod, config, "Mod List");
   }

   public ConfigGUI(GuiScreen parent, Object mod, Configuration config, String parentName) {
      this.categories = new ArrayList();
      this.propertyElements = new HashMap();
      this.parent = parent;
      this.parentName = parentName;
      if (!mod.getClass().isAnnotationPresent(Mod.class)) {
         throw new RuntimeException("@Mod annotation not found in class " + mod + "!");
      } else {
         this.mod = (Mod)mod.getClass().getAnnotation(Mod.class);
         if (config != null) {
            List var10000 = this.categories;
            Stream var10001 = config.getCategoryNames().stream();
            config.getClass();
            var10000.addAll((Collection)var10001.map(config::getCategory).filter((category) -> {
               return category.size() > 0;
            }).map((category) -> {
               List<ConfigProperty> configProperties = new ArrayList();

               for(Property property : category.values()) {
                  ConfigProperty configProperty = ForgeConfigProperty.factory(property);
                  if (configProperty != null) {
                     configProperties.add(configProperty);
                  }
               }

               return new ConfigCategory(category.getQualifiedName(), configProperties);
            }).collect(Collectors.toList()));
         }

      }
   }

   public void initElements() {
      this.addElement((new ButtonElement(this, "<", 0.0F, 0.0F, 30, 20, (button) -> {
         this.mc.displayGuiScreen(this.parent);
         return true;
      })).withColorScheme(this.getReturnButtonColorScheme()));
      this.addElement(new LabelElement(this, this.parentName, 35.0F, 6.0F));
      this.addElement(new LabelElement(this, this.mod.name().toUpperCase() + " SETTINGS", 35.0F, 26.0F));
      ListElement<ConfigGUI> categoryList = (ListElement)(new ListElement(this, 0.0F, 40.0F, 120, this.height - 40, (List)this.categories.stream().map(ConfigCategory::getName).collect(Collectors.toList()), 20, (list) -> {
         this.selectedCategory = (ConfigCategory)this.categories.get(list.getSelectedIndex());
         this.propertyElements.values().forEach(this::removeElement);
         this.propertyElements.clear();
         return true;
      })).withPersistence(true).withColorScheme(this.getSidebarColorScheme());
      categoryList.setSelectedIndex(0);
      this.selectedCategory = (ConfigCategory)this.categories.get(0);
      this.addElement(categoryList);
      this.propertyElements.clear();
   }

   public void drawScreen(float mouseX, float mouseY, float partialTicks) {
      Gui.drawRect(0, 0, this.width, 40, this.getTopBackgroundColor());
      Gui.drawRect(120, 40, this.width, this.height, this.getContentBackgroundColor());
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      int color = this.getAccentColor();
      float a = (float)(color >> 24 & 255) / 255.0F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      GlStateManager.color(r, g, b, a);
      GlStateManager.scale(0.15F, 0.15F, 0.15F);
      this.mc.getTextureManager().bindTexture(SETTINGS_ICON);
      this.drawTexturedModalRect(40, 135, 0, 0, 128, 128);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      int x = 125;
      int y = 45;

      for(ConfigProperty property : this.selectedCategory.getProperties()) {
         this.fontRenderer.drawString(property.name, x, y, this.getTextColor());
         y += 10;
         if (property.description != null && property.description.length() > 0) {
            this.fontRenderer.drawString(property.description, x, y, this.getTextColor());
            y += 10;
         }

         Element<ConfigGUI> propertyElement = (Element)this.propertyElements.get(property);
         if (propertyElement == null) {
            propertyElement = property.provideElement(this, (float)x, (float)y);
            if (propertyElement != null) {
               this.decoratePropertyElement(propertyElement);
               this.propertyElements.put(property, propertyElement);
               this.addElement(propertyElement);
            }
         }

         if (propertyElement != null) {
            y += propertyElement.getHeight() + 4;
         }
      }

   }

   public void onGuiClosed() {
      MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(this.mod.modid(), (String)null, this.mc.world != null, false));
      super.onGuiClosed();
   }

   public ColorScheme getReturnButtonColorScheme() {
      return RETURN;
   }

   public ColorScheme getSidebarColorScheme() {
      return SIDEBAR;
   }

   public int getTopBackgroundColor() {
      return LLibrary.CONFIG.getPrimaryColor();
   }

   public int getContentBackgroundColor() {
      return LLibrary.CONFIG.getColorMode().equals("dark") ? -15132391 : -1;
   }

   public int getAccentColor() {
      return LLibrary.CONFIG.getAccentColor();
   }

   public int getTextColor() {
      return LLibrary.CONFIG.getTextColor();
   }

   public void decoratePropertyElement(Element<ConfigGUI> element) {
   }

   public GuiScreen getParent() {
      return this.parent;
   }
}
