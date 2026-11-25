package rtg.client;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiPageButtonList.GuiButtonEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiLabelEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiPageButtonList.GuiSlideEntry;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import rtg.api.util.Logger;
import rtg.api.world.gen.RTGChunkGenSettings;

public class GuiCustomizeWorldScreenRTG extends GuiScreen implements FormatHelper, GuiResponder {
   private static final int ID_OFFSET = 127;
   private static final String LANG_KEY_PREFIX = "gui.createWorld.customize.";
   private static final String LANG_KEY_PREFIX_TITLE = "gui.createWorld.customize.title.";
   private static final String LANG_KEY_PREFIX_CATEGORY = "gui.createWorld.customize.category.";
   private static final String LANG_KEY_PREFIX_SETTING = "gui.createWorld.customize.setting.";
   private static final int BUTTON_DONE = 64;
   private static final int BUTTON_CLIPBOARD = 65;
   private static final int BUTTON_DEFAULTS = 66;
   private static final int BUTTON_PREVPAGE = 67;
   private static final int BUTTON_NEXTPAGE = 68;
   private static final int BUTTON_YES = 69;
   private static final int BUTTON_NO = 70;
   private static final RTGChunkGenSettings defaults = (new RTGChunkGenSettings.Factory()).build();
   private final GuiCreateWorld parent;
   private String title = "";
   private String subtitle = "";
   private String pageTitle = "";
   private String[] pageNames = new String[GuiCustomizeWorldScreenRTG.Page.values().length];
   private GuiPageButtonList list;
   private GuiButton done;
   private GuiButton clipboard;
   private GuiButton reset;
   private GuiButton prevPage;
   private GuiButton nextPage;
   private GuiButton confirm;
   private GuiButton cancel;
   private boolean settingsModified;
   private boolean confirmDismissed;
   private int confirmMode;

   public GuiCustomizeWorldScreenRTG(GuiScreen parentScreen, String generatorSettings) {
      this.parent = (GuiCreateWorld)parentScreen;
      if (!generatorSettings.isEmpty()) {
         GuiCustomizeWorldScreenRTG.Setting.parseSettings(generatorSettings);
         this.settingsModified = true;
      } else {
         GuiCustomizeWorldScreenRTG.Setting.resetToDefaults();
      }

   }

   public void initGui() {
      int page = 0;
      int scrolled = 0;
      if (this.list != null) {
         page = this.list.getPage();
         scrolled = this.list.getAmountScrolled();
      }

      this.title = I18n.format("gui.createWorld.customize.title.maintitle", new Object[0]);
      this.buttonList.clear();
      this.done = this.addButton(new GuiButton(64, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done", new Object[0])));
      this.clipboard = this.addButton(new GuiButton(65, this.width / 2 - 80, this.height - 27, 160, 20, I18n.format("gui.createWorld.customize.clipboard", new Object[0])));
      this.reset = this.addButton(new GuiButton(66, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults", new Object[0])));
      this.prevPage = this.addButton(new GuiButton(67, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev", new Object[0])));
      this.nextPage = this.addButton(new GuiButton(68, this.width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next", new Object[0])));
      this.confirm = new GuiButton(69, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes", new Object[0]));
      this.cancel = new GuiButton(70, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no", new Object[0]));
      this.clipboard.enabled = this.settingsModified;
      this.reset.enabled = this.settingsModified;
      this.confirm.visible = false;
      this.cancel.visible = false;
      this.buttonList.add(this.confirm);
      this.buttonList.add(this.cancel);
      if (this.confirmMode != 0) {
         this.confirm.visible = true;
         this.cancel.visible = true;
      }

      this.createPagedList();
      if (page != 0) {
         this.list.setPage(page);
         this.list.scrollBy(scrolled);
         this.updatePageControls();
      }

   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.list.handleMouseInput();
   }

   public void setEntryValue(int id, String value) {
      this.setSettingsModified(GuiCustomizeWorldScreenRTG.Setting.isModified());
   }

   public void setEntryValue(int id, boolean value) {
      GuiCustomizeWorldScreenRTG.Setting setting = GuiCustomizeWorldScreenRTG.Setting.getByID(id);
      if (setting != null) {
         setting.setCurValue(value);
         this.setSettingsModified(GuiCustomizeWorldScreenRTG.Setting.isModified());
      }

   }

   public void setEntryValue(int id, float value) {
      GuiCustomizeWorldScreenRTG.Setting setting = GuiCustomizeWorldScreenRTG.Setting.getByID(id);
      if (setting != null) {
         if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.INTEGER)) {
            setting.setCurValue((int)value);
         } else {
            setting.setCurValue(value);
         }

         this.setSettingsModified(GuiCustomizeWorldScreenRTG.Setting.isModified());
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         switch(button.id) {
         case 64:
            RTGChunkGenSettings.Factory factory = RTGChunkGenSettings.Factory.jsonToFactory(GuiCustomizeWorldScreenRTG.Setting.buildJson().toString());
            if (factory != null) {
               this.parent.chunkProviderSettingsJson = factory.toString();
            } else {
               Logger.error("Error parsing RTGChunkGenSettings settings from string: {}", GuiCustomizeWorldScreenRTG.Setting.buildJson().toString());
            }

            this.mc.displayGuiScreen(this.parent);
            break;
         case 65:
            this.copyToClipboard();
            break;
         case 66:
            if (this.settingsModified) {
               this.enterConfirmation(66);
            }
            break;
         case 67:
            this.list.previousPage();
            this.updatePageControls();
            break;
         case 68:
            this.list.nextPage();
            this.updatePageControls();
            break;
         case 69:
            this.exitConfirmation();
            break;
         case 70:
            this.confirmMode = 0;
            this.exitConfirmation();
         }
      }

   }

   private void createPagedList() {
      GuiListEntry[][] pages = new GuiListEntry[GuiCustomizeWorldScreenRTG.Page.values().length][];
      Lists.newArrayList(GuiCustomizeWorldScreenRTG.Page.values()).forEach((page) -> {
         int pg = page.ordinal();
         GuiCustomizeWorldScreenRTG.Id id = new GuiCustomizeWorldScreenRTG.Id();
         this.pageNames[pg] = page.getTitle();
         pages[pg] = new GuiListEntry[page.getPageSize()];
         page.CATEGORIES.forEach((cat) -> {
            pages[pg][id.next()] = new GuiLabelEntry(1127 + cat.ordinal(), cat.getTitle(), page.isPage1());
            id.next();
            List<GuiCustomizeWorldScreenRTG.Setting> sets = GuiCustomizeWorldScreenRTG.Setting.getSettingsForCategory(cat);
            sets.forEach((setting) -> {
               if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN)) {
                  pages[pg][id.next()] = new GuiButtonEntry(setting.getID(), setting.getLangKey(), setting.isPage1(), setting.curValue().getBool());
               }

               if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.INTEGER)) {
                  pages[pg][id.next()] = new GuiSlideEntry(setting.getID(), setting.getLangKey(), setting.isPage1(), (fid, fname, fval) -> {
                     return !setting.equals(GuiCustomizeWorldScreenRTG.Setting.waterSpoutChance) && !setting.equals(GuiCustomizeWorldScreenRTG.Setting.lavaSpoutChance) ? fname + ": " + String.format("%d", (int)fval) : fname + ((int)fval == 0 ? ": Disabled" : ": " + String.format("%d", (int)fval));
                  }, (float)setting.minValue().getInt(), (float)setting.maxValue().getInt(), (float)setting.curValue().getInt());
               }

               if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.FLOAT)) {
                  pages[pg][id.next()] = new GuiSlideEntry(setting.getID(), setting.getLangKey(), setting.isPage1(), (fid, fname, fval) -> {
                     return setting.equals(GuiCustomizeWorldScreenRTG.Setting.riverCutOffScale) ? fname + ": " + String.format("%3.3f", fval) : fname + ": " + String.format("%1.3f", fval);
                  }, setting.minValue().getFloat(), setting.maxValue().getFloat(), setting.curValue().getFloat());
               }

            });
            if (sets.size() % 2 != 0) {
               id.next();
            }

         });
      });
      this.list = new GuiPageButtonList(this.mc, this.width, this.height, 32, this.height - 32, 25, this, pages);
      this.updatePageControls();
   }

   private void updatePageControls() {
      this.prevPage.enabled = this.list.getPage() != 0;
      this.nextPage.enabled = this.list.getPage() != this.list.getPageCount() - 1;
      this.subtitle = I18n.format("book.pageIndicator", new Object[]{this.list.getPage() + 1, this.list.getPageCount()});
      this.pageTitle = this.pageNames[this.list.getPage()];
   }

   private void setSettingsModified(boolean settingsModified) {
      this.settingsModified = settingsModified;
      this.clipboard.enabled = settingsModified;
      this.reset.enabled = settingsModified;
   }

   private void setConfirmationControls(boolean confirm) {
      this.confirm.visible = confirm;
      this.cancel.visible = confirm;
      this.done.enabled = !confirm;
      this.prevPage.enabled = !confirm;
      this.nextPage.enabled = !confirm;
      this.reset.enabled = this.settingsModified && !confirm;
      this.list.setActive(!confirm);
   }

   private void enterConfirmation(int confirmMode) {
      this.confirmMode = confirmMode;
      this.setConfirmationControls(true);
   }

   private void exitConfirmation() throws IOException {
      switch(this.confirmMode) {
      case 64:
         this.actionPerformed((GuiListButton)this.list.getComponent(64));
         break;
      case 66:
         GuiCustomizeWorldScreenRTG.Setting.resetToDefaults();
         this.createPagedList();
         this.setSettingsModified(false);
      }

      this.confirmMode = 0;
      this.confirmDismissed = true;
      this.setConfirmationControls(false);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.confirmMode == 0 && !this.confirmDismissed) {
         this.list.mouseClicked(mouseX, mouseY, mouseButton);
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if (this.confirmDismissed) {
         this.confirmDismissed = false;
      } else if (this.confirmMode == 0) {
         this.list.mouseReleased(mouseX, mouseY, state);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 2, 16777215);
      this.drawCenteredString(this.fontRenderer, this.subtitle, this.width / 2, 12, 16777215);
      this.drawCenteredString(this.fontRenderer, this.pageTitle, this.width / 2, 22, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.confirmMode != 0) {
         drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
         this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
         this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
         this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
         this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder vertexbuffer = tessellator.getBuffer();
         this.mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         vertexbuffer.pos((double)(this.width / 2 - 90), 185.0D, 0.0D).tex(0.0D, 2.65625D).color(64, 64, 64, 64).endVertex();
         vertexbuffer.pos((double)(this.width / 2 + 90), 185.0D, 0.0D).tex(5.625D, 2.65625D).color(64, 64, 64, 64).endVertex();
         vertexbuffer.pos((double)(this.width / 2 + 90), 100.0D, 0.0D).tex(5.625D, 0.0D).color(64, 64, 64, 64).endVertex();
         vertexbuffer.pos((double)(this.width / 2 - 90), 100.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 64).endVertex();
         tessellator.draw();
         this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirmTitle", new Object[0]), this.width / 2, 105, 16777215);
         this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm1", new Object[0]), this.width / 2, 125, 16777215);
         this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.customize.custom.confirm2", new Object[0]), this.width / 2, 135, 16777215);
         this.confirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
         this.cancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
      }

   }

   private void copyToClipboard() {
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(GuiCustomizeWorldScreenRTG.Setting.buildJson().toString()), (ClipboardOwner)null);
   }

   public String getText(int id, String name, float value) {
      return name + ": " + value;
   }

   static enum Category {
      WORLD,
      SURFACE,
      UNDERGROUND,
      RIVERS,
      RTGLAKES,
      VANILLASURFACELAKES,
      VANILLAUNDERGROUNDLAKES,
      VANILLASPOUTS,
      VILLAGES,
      MINESHAFTS,
      DUNGEONS,
      TEMPLES,
      MONUMENTS,
      MANSIONS,
      STRONGHOLDS,
      DIRT,
      GRAVEL,
      GRANITE,
      DIORITE,
      ANDESITE,
      COAL,
      IRON,
      GOLD,
      REDSTONE,
      DIAMOND,
      LAPIS;

      private Category() {
      }

      public String getTitle() {
         return I18n.format("gui.createWorld.customize.category." + this.name(), new Object[0]);
      }
   }

   private static class Id {
      private int count;

      private Id() {
         this.count = 0;
      }

      public int next() {
         return this.count++;
      }
   }

   static enum Page {
      PAGE1(new GuiCustomizeWorldScreenRTG.Category[]{GuiCustomizeWorldScreenRTG.Category.WORLD, GuiCustomizeWorldScreenRTG.Category.SURFACE, GuiCustomizeWorldScreenRTG.Category.UNDERGROUND}),
      PAGE2(new GuiCustomizeWorldScreenRTG.Category[]{GuiCustomizeWorldScreenRTG.Category.RIVERS, GuiCustomizeWorldScreenRTG.Category.RTGLAKES, GuiCustomizeWorldScreenRTG.Category.VANILLASURFACELAKES, GuiCustomizeWorldScreenRTG.Category.VANILLAUNDERGROUNDLAKES, GuiCustomizeWorldScreenRTG.Category.VANILLASPOUTS}),
      PAGE3(new GuiCustomizeWorldScreenRTG.Category[]{GuiCustomizeWorldScreenRTG.Category.VILLAGES, GuiCustomizeWorldScreenRTG.Category.MINESHAFTS, GuiCustomizeWorldScreenRTG.Category.DUNGEONS, GuiCustomizeWorldScreenRTG.Category.TEMPLES, GuiCustomizeWorldScreenRTG.Category.MONUMENTS, GuiCustomizeWorldScreenRTG.Category.MANSIONS, GuiCustomizeWorldScreenRTG.Category.STRONGHOLDS}),
      PAGE4(new GuiCustomizeWorldScreenRTG.Category[]{GuiCustomizeWorldScreenRTG.Category.DIRT, GuiCustomizeWorldScreenRTG.Category.GRAVEL, GuiCustomizeWorldScreenRTG.Category.GRANITE, GuiCustomizeWorldScreenRTG.Category.DIORITE, GuiCustomizeWorldScreenRTG.Category.ANDESITE, GuiCustomizeWorldScreenRTG.Category.COAL, GuiCustomizeWorldScreenRTG.Category.IRON, GuiCustomizeWorldScreenRTG.Category.GOLD, GuiCustomizeWorldScreenRTG.Category.REDSTONE, GuiCustomizeWorldScreenRTG.Category.DIAMOND, GuiCustomizeWorldScreenRTG.Category.LAPIS});

      public final List<GuiCustomizeWorldScreenRTG.Category> CATEGORIES = new ArrayList();

      private Page(GuiCustomizeWorldScreenRTG.Category[] cats) {
         Collections.addAll(this.CATEGORIES, cats);
         if (this.ordinal() == 0) {
            for(GuiCustomizeWorldScreenRTG.Setting setting : GuiCustomizeWorldScreenRTG.Setting.values()) {
               if (this.CATEGORIES.contains(setting.getCategory())) {
                  setting.setPage1();
               }
            }
         }

      }

      public int getPageSize() {
         int total = this.CATEGORIES.size() * 2;

         for(GuiCustomizeWorldScreenRTG.Category category : this.CATEGORIES) {
            int tmp = GuiCustomizeWorldScreenRTG.Setting.getSettingsForCategory(category).size();
            total += tmp % 2 == 0 ? tmp : tmp + 1;
         }

         return total;
      }

      public String getTitle() {
         return I18n.format("gui.createWorld.customize.title." + this.name(), new Object[0]);
      }

      public boolean isPage1() {
         return this.equals(PAGE1);
      }
   }

   static enum Setting {
      seaLevel(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.seaLevel, Integer.valueOf(31), Integer.valueOf(95), GuiCustomizeWorldScreenRTG.Category.WORLD),
      useBoulders(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useBoulders, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.SURFACE),
      boulderMult(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.boulderMult, 0.2F, 5.0F, GuiCustomizeWorldScreenRTG.Category.SURFACE),
      useSnowLayers(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useSnowLayers, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.SURFACE),
      snowLayerTemp(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.snowLayerTemp, 0.05F, 0.15F, GuiCustomizeWorldScreenRTG.Category.SURFACE),
      sandDuneHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.sandDuneHeight, Integer.valueOf(1), Integer.valueOf(10), GuiCustomizeWorldScreenRTG.Category.SURFACE),
      bedrockLayers(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.bedrockLayers, Integer.valueOf(1), Integer.valueOf(10), GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      useCaves(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useCaves, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      caveChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.caveChance, Integer.valueOf(4), Integer.valueOf(200), GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      caveDensity(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.caveDensity, Integer.valueOf(1), Integer.valueOf(20), GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      useRavines(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useRavines, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      ravineChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.ravineChance, Integer.valueOf(4), Integer.valueOf(200), GuiCustomizeWorldScreenRTG.Category.UNDERGROUND),
      riverSizeMult(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.riverSizeMult, 0.5F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RIVERS),
      riverFrequency(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.riverFrequency, 0.0F, 8.0F, GuiCustomizeWorldScreenRTG.Category.RIVERS),
      riverBendMult(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.riverBendMult, 0.5F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RIVERS),
      riverCutOffAmpl(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.riverCutOffAmpl, 0.0F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RIVERS),
      riverCutOffScale(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.riverCutOffScale, 50.0F, 750.0F, GuiCustomizeWorldScreenRTG.Category.RIVERS),
      RTGlakeSizeMult(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.RTGlakeSizeMult, 0.0F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RTGLAKES),
      RTGlakeFreqMult(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.RTGlakeFreqMult, 0.0F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RTGLAKES),
      RTGlakeShoreBend(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.RTGlakeShoreBend, 0.0F, 2.0F, GuiCustomizeWorldScreenRTG.Category.RTGLAKES),
      useWaterLakes(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useWaterLakes, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.VANILLASURFACELAKES),
      waterLakeChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.waterLakeChance, Integer.valueOf(1), Integer.valueOf(100), GuiCustomizeWorldScreenRTG.Category.VANILLASURFACELAKES),
      useLavaLakes(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useLavaLakes, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.VANILLASURFACELAKES),
      lavaLakeChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lavaLakeChance, Integer.valueOf(1), Integer.valueOf(100), GuiCustomizeWorldScreenRTG.Category.VANILLASURFACELAKES),
      useWaterUndLakes(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useWaterUndLakes, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.VANILLAUNDERGROUNDLAKES),
      waterUndLakeChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.waterUndLakeChance, Integer.valueOf(1), Integer.valueOf(100), GuiCustomizeWorldScreenRTG.Category.VANILLAUNDERGROUNDLAKES),
      useLavaUndLakes(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useLavaUndLakes, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.VANILLAUNDERGROUNDLAKES),
      lavaUndLakeChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lavaUndLakeChance, Integer.valueOf(1), Integer.valueOf(100), GuiCustomizeWorldScreenRTG.Category.VANILLAUNDERGROUNDLAKES),
      waterSpoutChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.waterSpoutChance, Integer.valueOf(0), Integer.valueOf(200), GuiCustomizeWorldScreenRTG.Category.VANILLASPOUTS),
      lavaSpoutChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lavaSpoutChance, Integer.valueOf(0), Integer.valueOf(200), GuiCustomizeWorldScreenRTG.Category.VANILLASPOUTS),
      useVillages(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useVillages, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.VILLAGES),
      villageSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.villageSize, Integer.valueOf(0), Integer.valueOf(5), GuiCustomizeWorldScreenRTG.Category.VILLAGES),
      villageDistance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.villageDistance, Integer.valueOf(24), Integer.valueOf(1024), GuiCustomizeWorldScreenRTG.Category.VILLAGES),
      useMineShafts(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useMineShafts, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.MINESHAFTS),
      mineShaftChance(GuiCustomizeWorldScreenRTG.SettingType.FLOAT, GuiCustomizeWorldScreenRTG.defaults.mineShaftChance, 0.001F, 0.01F, GuiCustomizeWorldScreenRTG.Category.MINESHAFTS),
      useDungeons(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useDungeons, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.DUNGEONS),
      dungeonChance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dungeonChance, Integer.valueOf(5), Integer.valueOf(20), GuiCustomizeWorldScreenRTG.Category.DUNGEONS),
      useTemples(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useTemples, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.TEMPLES),
      templeDistance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.templeDistance, Integer.valueOf(16), Integer.valueOf(64), GuiCustomizeWorldScreenRTG.Category.TEMPLES),
      useMonuments(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useMonuments, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.MONUMENTS),
      monumentSpacing(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.monumentSpacing, Integer.valueOf(16), Integer.valueOf(64), GuiCustomizeWorldScreenRTG.Category.MONUMENTS),
      monumentSeparation(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.monumentSeparation, Integer.valueOf(3), Integer.valueOf(8), GuiCustomizeWorldScreenRTG.Category.MONUMENTS),
      useMansions(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useMansions, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.MANSIONS),
      mansionSpacing(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.mansionSpacing, Integer.valueOf(40), Integer.valueOf(160), GuiCustomizeWorldScreenRTG.Category.MANSIONS),
      mansionSeparation(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.mansionSeparation, Integer.valueOf(10), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.MANSIONS),
      useStrongholds(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN, GuiCustomizeWorldScreenRTG.defaults.useStrongholds, (Object)null, (Object)null, GuiCustomizeWorldScreenRTG.Category.STRONGHOLDS),
      strongholdCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.strongholdCount, Integer.valueOf(0), Integer.valueOf(256), GuiCustomizeWorldScreenRTG.Category.STRONGHOLDS),
      strongholdDistance(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.strongholdDistance, Integer.valueOf(16), Integer.valueOf(64), GuiCustomizeWorldScreenRTG.Category.STRONGHOLDS),
      strongholdSpread(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.strongholdSpread, Integer.valueOf(2), Integer.valueOf(4), GuiCustomizeWorldScreenRTG.Category.STRONGHOLDS),
      dirtSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dirtSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.DIRT),
      dirtCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dirtCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.DIRT),
      dirtMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dirtMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIRT),
      dirtMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dirtMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIRT),
      gravelSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.gravelSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.GRAVEL),
      gravelCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.gravelCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.GRAVEL),
      gravelMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.gravelMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GRAVEL),
      gravelMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.gravelMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GRAVEL),
      graniteSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.graniteSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.GRANITE),
      graniteCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.graniteCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.GRANITE),
      graniteMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.graniteMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GRANITE),
      graniteMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.graniteMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GRANITE),
      dioriteSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dioriteSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.DIORITE),
      dioriteCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dioriteCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.DIORITE),
      dioriteMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dioriteMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIORITE),
      dioriteMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.dioriteMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIORITE),
      andesiteSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.andesiteSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.ANDESITE),
      andesiteCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.andesiteCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.ANDESITE),
      andesiteMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.andesiteMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.ANDESITE),
      andesiteMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.andesiteMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.ANDESITE),
      coalSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.coalSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.COAL),
      coalCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.coalCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.COAL),
      coalMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.coalMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.COAL),
      coalMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.coalMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.COAL),
      ironSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.ironSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.IRON),
      ironCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.ironCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.IRON),
      ironMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.ironMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.IRON),
      ironMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.ironMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.IRON),
      goldSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.goldSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.GOLD),
      goldCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.goldCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.GOLD),
      goldMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.goldMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GOLD),
      goldMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.goldMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.GOLD),
      redstoneSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.redstoneSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.REDSTONE),
      redstoneCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.redstoneCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.REDSTONE),
      redstoneMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.redstoneMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.REDSTONE),
      redstoneMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.redstoneMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.REDSTONE),
      diamondSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.diamondSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.DIAMOND),
      diamondCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.diamondCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.DIAMOND),
      diamondMinHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.diamondMinHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIAMOND),
      diamondMaxHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.diamondMaxHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.DIAMOND),
      lapisSize(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lapisSize, Integer.valueOf(1), Integer.valueOf(50), GuiCustomizeWorldScreenRTG.Category.LAPIS),
      lapisCount(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lapisCount, Integer.valueOf(0), Integer.valueOf(40), GuiCustomizeWorldScreenRTG.Category.LAPIS),
      lapisCenterHeight(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lapisCenterHeight, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.LAPIS),
      lapisSpread(GuiCustomizeWorldScreenRTG.SettingType.INTEGER, GuiCustomizeWorldScreenRTG.defaults.lapisSpread, Integer.valueOf(0), Integer.valueOf(255), GuiCustomizeWorldScreenRTG.Category.LAPIS);

      private final GuiCustomizeWorldScreenRTG.SettingType settingType;
      private final int id;
      private final GuiCustomizeWorldScreenRTG.Category category;
      private final Object defValue;
      private final Object minValue;
      private final Object maxValue;
      private boolean isPage1;
      private Object curValue;
      private Object returnValue;

      private Setting(GuiCustomizeWorldScreenRTG.SettingType settingType, Object defValue, @Nullable Object minValue, @Nullable Object maxValue, GuiCustomizeWorldScreenRTG.Category category) {
         this.settingType = settingType;
         this.id = 127 + this.ordinal();
         this.category = category;
         this.defValue = defValue;
         this.minValue = minValue;
         this.maxValue = maxValue;
         this.curValue = this.defValue;
      }

      public static List<GuiCustomizeWorldScreenRTG.Setting> getSettingsForCategory(GuiCustomizeWorldScreenRTG.Category category) {
         List<GuiCustomizeWorldScreenRTG.Setting> ret = Lists.newArrayList();

         for(GuiCustomizeWorldScreenRTG.Setting setting : values()) {
            if (setting.getCategory().equals(category)) {
               ret.add(setting);
            }
         }

         return ret;
      }

      @Nullable
      public static GuiCustomizeWorldScreenRTG.Setting getByID(int id) {
         for(GuiCustomizeWorldScreenRTG.Setting setting : values()) {
            if (setting.getID() == id) {
               return setting;
            }
         }

         return null;
      }

      public static boolean isModified() {
         for(GuiCustomizeWorldScreenRTG.Setting setting : values()) {
            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN) && setting.curValue().getBool() != setting.defValue().getBool()) {
               return true;
            }

            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.INTEGER) && setting.curValue().getInt() != setting.defValue().getInt()) {
               return true;
            }

            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.FLOAT) && setting.curValue().getFloat() != setting.defValue().getFloat()) {
               return true;
            }
         }

         return false;
      }

      public static JsonObject buildJson() {
         JsonObject ret = new JsonObject();

         for(GuiCustomizeWorldScreenRTG.Setting setting : values()) {
            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN) && setting.curValue().getBool() != setting.defValue().getBool()) {
               ret.addProperty(setting.name(), Boolean.valueOf(setting.curValue().getBool()));
            }

            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.INTEGER) && setting.curValue().getInt() != setting.defValue().getInt()) {
               ret.addProperty(setting.name(), Integer.valueOf(setting.curValue().getInt()));
            }

            if (setting.getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.FLOAT) && setting.curValue().getFloat() != setting.defValue().getFloat()) {
               ret.addProperty(setting.name(), Float.valueOf(setting.curValue().getFloat()));
            }
         }

         return ret;
      }

      public static void resetToDefaults() {
         for(GuiCustomizeWorldScreenRTG.Setting setting : values()) {
            setting.setCurValue(setting.defValue);
         }

      }

      public static void parseSettings(String generatorSettings) {
         RTGChunkGenSettings.Factory factory = RTGChunkGenSettings.Factory.jsonToFactory(generatorSettings);
         if (factory == null) {
            Logger.error("Error parsing RTGChunkGenSettings settings from string: {}", generatorSettings);
         } else {
            JsonObject json = (new JsonParser()).parse(factory.toString()).getAsJsonObject();
            GuiCustomizeWorldScreenRTG.Setting[] setting = new GuiCustomizeWorldScreenRTG.Setting[1];
            json.entrySet().forEach((entry) -> {
               try {
                  setting[0] = valueOf((String)entry.getKey());
               } catch (IllegalArgumentException var3) {
                  Logger.error("GuiCustomizeWorldScreenRTG$Setting#parseSettings: Illegal argument. No enum exists for the setting: " + (String)entry.getKey());
                  setting[0] = null;
               }

               if (setting[0] != null) {
                  if (setting[0].getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.BOOLEAN)) {
                     setting[0].setCurValue(((JsonElement)entry.getValue()).getAsBoolean());
                  }

                  if (setting[0].getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.INTEGER)) {
                     setting[0].setCurValue(((JsonElement)entry.getValue()).getAsInt());
                  }

                  if (setting[0].getSettingType().equals(GuiCustomizeWorldScreenRTG.SettingType.FLOAT)) {
                     setting[0].setCurValue(((JsonElement)entry.getValue()).getAsFloat());
                  }
               }

            });
         }
      }

      public GuiCustomizeWorldScreenRTG.SettingType getSettingType() {
         return this.settingType;
      }

      public int getID() {
         return this.id;
      }

      public GuiCustomizeWorldScreenRTG.Category getCategory() {
         return this.category;
      }

      public String getLangKey() {
         return "gui.createWorld.customize.setting." + this.name();
      }

      public boolean isPage1() {
         return this.isPage1;
      }

      public void setPage1() {
         this.isPage1 = true;
      }

      public GuiCustomizeWorldScreenRTG.Setting defValue() {
         this.returnValue = this.defValue;
         return this;
      }

      public GuiCustomizeWorldScreenRTG.Setting minValue() {
         this.returnValue = this.minValue;
         return this;
      }

      public GuiCustomizeWorldScreenRTG.Setting maxValue() {
         this.returnValue = this.maxValue;
         return this;
      }

      public GuiCustomizeWorldScreenRTG.Setting curValue() {
         this.returnValue = this.curValue;
         return this;
      }

      public boolean getBool() {
         return ((Boolean)this.returnValue).booleanValue();
      }

      public int getInt() {
         return this.returnValue instanceof Float ? ((Float)this.returnValue).intValue() : ((Integer)this.returnValue).intValue();
      }

      public float getFloat() {
         return this.returnValue instanceof Integer ? ((Integer)this.returnValue).floatValue() : ((Float)this.returnValue).floatValue();
      }

      public void setCurValue(boolean val) {
         this.curValue = val;
      }

      public void setCurValue(int val) {
         this.curValue = val;
      }

      public void setCurValue(float val) {
         this.curValue = val;
      }

      public void setCurValue(Object val) {
         this.curValue = val;
      }
   }

   static enum SettingType {
      BOOLEAN,
      INTEGER,
      FLOAT;

      private SettingType() {
      }
   }
}
