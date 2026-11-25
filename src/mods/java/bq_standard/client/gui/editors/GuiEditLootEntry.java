package bq_standard.client.gui.editors;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.nbt_doc.INbtDoc;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterNumber;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.IThemeRegistry;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNBT;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetGUIs;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.network.handlers.NetLootImport;
import bq_standard.rewards.loot.LootGroup;
import bq_standard.rewards.loot.LootRegistry;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiEditLootEntry extends GuiScreenCanvas implements IVolatileScreen {
   private LootGroup lootGroup;
   private final int groupID;
   private LootGroup.LootEntry selEntry;
   private int selectedID = -1;
   private CanvasScrolling lootList;
   private PanelTextBox fieldName;
   private PanelTextField<Integer> fieldWeight;
   private PanelTextBox textWeight;
   private final DecimalFormat numFormat = new DecimalFormat("0.##");

   public GuiEditLootEntry(GuiScreen parent, LootGroup group) {
      super(parent);
      this.lootGroup = group;
      this.groupID = LootRegistry.INSTANCE.getID(group);
   }

   public void initPanel() {
      super.initPanel();
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      cvBackground.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bq_standard.title.edit_loot_groups", new Object[0]))).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));
      CanvasEmpty cvLeft = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 8, 24), 0));
      cvBackground.addPanel(cvLeft);
      this.lootList = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 8, 24), 0));
      cvLeft.addPanel(this.lootList);
      PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 24), 0));
      cvLeft.addPanel(scList);
      this.lootList.setScrollDriverY(scList);
      cvLeft.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(0, -16, 0, 0), 0), -1, QuestTranslation.translate("betterquesting.btn.new", new Object[0])) {
         public void onButtonClick() {
            GuiEditLootEntry.this.lootGroup.add(GuiEditLootEntry.this.lootGroup.nextID(), new LootGroup.LootEntry());
            GuiEditLootEntry.this.sendChanges();
         }
      });
      CanvasEmpty cvRight = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 24), 0));
      cvBackground.addPanel(cvRight);
      cvRight.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 4, 0, -16), 0), QuestTranslation.translate("betterquesting.gui.name", new Object[0]))).setColor(PresetColor.TEXT_MAIN.getColor()));
      this.fieldName = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), this.selEntry != null ? "#" + this.selectedID : "#--")).setColor(PresetColor.TEXT_MAIN.getColor());
      cvRight.addPanel(this.fieldName);
      cvRight.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 36, 0, -48), 0), QuestTranslation.translate("bq_standard.gui.weight", new Object[0]))).setColor(PresetColor.TEXT_MAIN.getColor()));
      this.fieldWeight = new PanelTextField(new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 0.0F), new GuiPadding(0, 48, 0, -64), 0), "" + (this.selEntry != null ? this.selEntry.weight : 1), FieldFilterNumber.INT);
      this.fieldWeight.setCallback((value) -> {
         if (this.selEntry != null) {
            if (((Integer)this.fieldWeight.getValue()).intValue() <= 0) {
               this.fieldWeight.setText("1");
            }

            this.selEntry.weight = ((Integer)this.fieldWeight.getValue()).intValue();
            int totalWeight = this.lootGroup.getTotalWeight();
            float chance = (float)this.selEntry.weight / (float)totalWeight * 100.0F;
            this.textWeight.setText("/" + totalWeight + " (" + this.numFormat.format((double)chance) + "%)");
         }
      });
      cvRight.addPanel(this.fieldWeight);
      this.textWeight = (new PanelTextBox(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(4, 52, 0, -64), 0), "/1 (100%)")).setColor(PresetColor.TEXT_MAIN.getColor());
      cvRight.addPanel(this.textWeight);
      cvRight.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(0, -16, 0, 0), 0), -1, QuestTranslation.translate("bq_standard.btn.add_remove_drops", new Object[0])) {
         public void onButtonClick() {
            if (GuiEditLootEntry.this.selEntry != null) {
               NBTTagCompound eTag = GuiEditLootEntry.this.selEntry.writeToNBT(new NBTTagCompound());
               GuiEditLootEntry.this.mc.displayGuiScreen(((IThemeRegistry)QuestingAPI.getAPI(ApiReference.THEME_REG)).getGui(PresetGUIs.EDIT_NBT, new GArgsNBT(GuiEditLootEntry.this, eTag.getTagList("items", 10), (value) -> {
                  LootGroup lg = (LootGroup)LootRegistry.INSTANCE.getValue(GuiEditLootEntry.this.groupID);
                  LootGroup.LootEntry le = lg == null ? null : (LootGroup.LootEntry)lg.getValue(GuiEditLootEntry.this.selectedID);
                  if (le != null) {
                     le.readFromNBT(eTag);
                     GuiEditLootEntry.this.sendChanges();
                  }

               }, (INbtDoc)null)));
            }

         }
      });
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done", new Object[0])) {
         public void onButtonClick() {
            GuiEditLootEntry.this.sendChanges();
            GuiEditLootEntry.this.mc.displayGuiScreen(GuiEditLootEntry.this.parent);
         }
      });
      IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
      ls0.setParent(cvBackground.getTransform());
      IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -24, 0, 0, 0);
      le0.setParent(cvBackground.getTransform());
      PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
      cvBackground.addPanel(paLine0);
      this.refreshEntries();
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (LootRegistry.INSTANCE.updateUI) {
         LootRegistry.INSTANCE.updateUI = false;
         this.lootGroup = (LootGroup)LootRegistry.INSTANCE.getValue(this.groupID);
         if (this.lootGroup == null) {
            this.mc.displayGuiScreen(this.parent);
            return;
         }

         if (this.selectedID >= 0) {
            this.selEntry = (LootGroup.LootEntry)this.lootGroup.getValue(this.selectedID);
            if (this.selEntry == null) {
               this.selectedID = -1;
               this.fieldName.setText("");
               this.fieldWeight.setText("1");
               this.textWeight.setText("/1 (100%)");
            } else {
               this.selEntry.weight = ((Integer)this.fieldWeight.getValue()).intValue();
               int totalWeight = this.lootGroup.getTotalWeight();
               float chance = (float)this.selEntry.weight / (float)totalWeight * 100.0F;
               this.textWeight.setText("/" + totalWeight + " (" + this.numFormat.format((double)chance) + "%)");
            }
         }

         this.refreshEntries();
      }

      super.drawPanel(mx, my, partialTick);
   }

   private void refreshEntries() {
      this.lootList.resetCanvas();
      int lWidth = this.lootList.getTransform().getWidth();
      List<DBEntry<LootGroup.LootEntry>> lgAry = this.lootGroup.getEntries();

      for(int i = 0; i < lgAry.size(); ++i) {
         this.lootList.addPanel((new PanelButtonStorage(new GuiRectangle(0, i * 16, 16, 16, 0), -1, "", lgAry.get(i))).setCallback((value) -> {
            this.lootGroup.removeID(value.getID());
            this.refreshEntries();
            this.sendChanges();
         }).setIcon(PresetIcon.ICON_TRASH.getTexture()));
         this.lootList.addPanel((new PanelButtonStorage(new GuiRectangle(16, i * 16, lWidth - 16, 16, 0), -1, "#" + ((DBEntry)lgAry.get(i)).getID(), lgAry.get(i))).setCallback((value) -> {
            if (this.selEntry != null) {
               this.sendChanges();
            }

            this.selectedID = value.getID();
            this.selEntry = (LootGroup.LootEntry)value.getValue();
            this.fieldName.setText("#" + this.selectedID);
            this.fieldWeight.setText("" + this.selEntry.weight);
            int totalWeight = this.lootGroup.getTotalWeight();
            float chance = (float)this.selEntry.weight / (float)totalWeight * 100.0F;
            this.textWeight.setText("/" + totalWeight + " (" + this.numFormat.format((double)chance) + "%)");
         }));
      }

   }

   private void sendChanges() {
      NetLootImport.importLoot(LootRegistry.INSTANCE.writeToNBT(new NBTTagCompound(), (List)null));
   }
}
