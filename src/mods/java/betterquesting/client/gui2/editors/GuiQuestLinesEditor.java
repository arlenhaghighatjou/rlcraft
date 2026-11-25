package betterquesting.client.gui2.editors;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.misc.ICallback;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
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
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.designer.GuiDesigner;
import betterquesting.client.gui2.editors.nbt.GuiItemSelection;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.questing.QuestLineDatabase;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiQuestLinesEditor extends GuiScreenCanvas implements IPEventListener, IVolatileScreen, INeedsRefresh {
   private CanvasScrolling lineList;
   private PanelTextField<String> tfName;
   private PanelTextField<String> tfDesc;
   private PanelButton btnDesign;
   private PanelButton btnVis;
   private PanelButton btnIcon;
   private IQuestLine selected;
   private int selID = -1;

   public GuiQuestLinesEditor(GuiScreen parent) {
      super(parent);
   }

   public void refreshGui() {
      if (this.selID >= 0) {
         this.selected = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(this.selID);
         if (this.selected == null) {
            this.selID = -1;
            this.btnDesign.setActive(false);
            this.btnIcon.setActive(false);
            this.btnVis.setActive(false);
            this.tfName.setText("");
            this.tfDesc.setText("");
         } else {
            this.btnDesign.setActive(true);
            this.btnIcon.setActive(true);
            this.btnVis.setActive(true);
            this.btnVis.setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + this.selected.getProperty(NativeProps.VISIBILITY));
            if (!this.tfName.isFocused()) {
               this.tfName.setText(this.selected.getUnlocalisedName());
            }

            if (!this.tfDesc.isFocused()) {
               this.tfDesc.setText(this.selected.getUnlocalisedDescription());
            }
         }
      }

      this.reloadList();
   }

   public void initPanel() {
      super.initPanel();
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.edit_line1"))).setAlignment(1);
      panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
      cvBackground.addPanel(panTxt);
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back")));
      this.lineList = new CanvasScrolling(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 16, 48), 0));
      cvBackground.addPanel(this.lineList);
      PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 1.0F), new GuiPadding(-16, 32, 8, 48), 0));
      cvBackground.addPanel(scList);
      this.lineList.setScrollDriverY(scList);
      PanelButton btnAdd = new PanelButton(new GuiTransform(new Vector4f(0.0F, 1.0F, 0.25F, 1.0F), new GuiPadding(16, -40, 0, 24), 0), 1, QuestTranslation.translate("betterquesting.btn.new"));
      cvBackground.addPanel(btnAdd);
      PanelButton btnImport = new PanelButton(new GuiTransform(new Vector4f(0.25F, 1.0F, 0.5F, 1.0F), new GuiPadding(0, -40, 16, 24), 0), 2, QuestTranslation.translate("betterquesting.btn.import"));
      cvBackground.addPanel(btnImport);
      CanvasEmpty cvRight = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 24), 0));
      cvBackground.addPanel(cvRight);
      PanelTextBox txtName = new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 4, 0, -16), 0), QuestTranslation.translate("betterquesting.gui.name"));
      txtName.setColor(PresetColor.TEXT_MAIN.getColor());
      cvRight.addPanel(txtName);
      this.tfName = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), "", FieldFilterString.INSTANCE);
      cvRight.addPanel(this.tfName);
      PanelTextBox txtDesc = new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 36, 0, -48), 0), QuestTranslation.translate("betterquesting.gui.description"));
      txtDesc.setColor(PresetColor.TEXT_MAIN.getColor());
      cvRight.addPanel(txtDesc);
      this.tfDesc = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 48, 16, -64), 0), "", FieldFilterString.INSTANCE);
      this.tfDesc.setMaxLength(Integer.MAX_VALUE);
      cvRight.addPanel(this.tfDesc);
      this.btnIcon = new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 80, 0, -96), 0), -1, QuestTranslation.translate("betterquesting.btn.icon")) {
         public void onButtonClick() {
            if (GuiQuestLinesEditor.this.selected != null) {
               GuiQuestLinesEditor.this.mc.displayGuiScreen(new GuiItemSelection(GuiQuestLinesEditor.this, (BigItemStack)GuiQuestLinesEditor.this.selected.getProperty(NativeProps.ICON), (value) -> {
                  GuiQuestLinesEditor.this.selected.setProperty(NativeProps.ICON, value);
                  GuiQuestLinesEditor.this.SendChanges(new DBEntry(GuiQuestLinesEditor.this.selID, GuiQuestLinesEditor.this.selected));
               }));
            }
         }
      };
      this.btnIcon.setActive(this.selected != null);
      cvRight.addPanel(this.btnIcon);
      this.btnVis = new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 96, 0, -112), 0), -1, QuestTranslation.translate("betterquesting.btn.show") + ": " + (this.selected == null ? EnumQuestVisibility.NORMAL : (EnumQuestVisibility)this.selected.getProperty(NativeProps.VISIBILITY))) {
         public void onButtonClick() {
            if (GuiQuestLinesEditor.this.selected != null) {
               EnumQuestVisibility[] visList = EnumQuestVisibility.values();
               EnumQuestVisibility vis = (EnumQuestVisibility)GuiQuestLinesEditor.this.selected.getProperty(NativeProps.VISIBILITY);
               vis = visList[(vis.ordinal() + 1) % visList.length];
               GuiQuestLinesEditor.this.selected.setProperty(NativeProps.VISIBILITY, vis);
               this.setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + vis);
            }
         }
      };
      this.btnVis.setActive(this.selected != null);
      cvRight.addPanel(this.btnVis);
      PanelButton btnManage = new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 112, 0, -128), 0), 3, QuestTranslation.translate("betterquesting.btn.add_remove_quests"));
      cvRight.addPanel(btnManage);
      this.btnDesign = new PanelButton(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 128, 0, -144), 0), 4, QuestTranslation.translate("betterquesting.btn.designer"));
      this.btnDesign.setActive(this.selected != null);
      cvRight.addPanel(this.btnDesign);
      PanelButton btnTextEditor = new PanelButton(new GuiTransform(GuiAlign.TOP_RIGHT, new GuiPadding(-16, 48, 0, -64), 0), 8, "Aa");
      cvRight.addPanel(btnTextEditor);
      IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
      ls0.setParent(cvBackground.getTransform());
      IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -24, 0, 0, 0);
      le0.setParent(cvBackground.getTransform());
      PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
      cvBackground.addPanel(paLine0);
      if (this.selID >= 0) {
         this.selected = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(this.selID);
         if (this.selected == null) {
            this.selID = -1;
            this.btnDesign.setActive(false);
            this.btnIcon.setActive(false);
            this.btnVis.setActive(false);
            this.tfName.setText("");
            this.tfDesc.setText("");
         } else {
            this.btnDesign.setActive(true);
            this.btnIcon.setActive(true);
            this.btnVis.setActive(true);
            this.btnVis.setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + this.selected.getProperty(NativeProps.VISIBILITY));
            if (!this.tfName.isFocused()) {
               this.tfName.setText(this.selected.getUnlocalisedName());
            }

            if (!this.tfDesc.isFocused()) {
               this.tfDesc.setText(this.selected.getUnlocalisedDescription());
            }
         }
      }

      this.reloadList();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (this.selected != null) {
         boolean changed = false;
         if (!((String)this.tfName.getValue()).equals(this.selected.getUnlocalisedName())) {
            this.selected.setProperty(NativeProps.NAME, this.tfName.getValue());
            changed = true;
         }

         if (!((String)this.tfDesc.getValue()).equals(this.selected.getUnlocalisedDescription())) {
            this.selected.setProperty(NativeProps.DESC, this.tfDesc.getValue());
            changed = true;
         }

         if (changed) {
            this.SendChanges(new DBEntry(this.selID, this.selected));
         }
      }

      return super.onMouseClick(mx, my, click);
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      IPanelButton btn = event.getButton();
      if (btn.getButtonID() == 0) {
         this.mc.displayGuiScreen(this.parent);
      } else if (btn.getButtonID() == 1) {
         NBTTagCompound payload = new NBTTagCompound();
         NBTTagList dataList = new NBTTagList();
         NBTTagCompound entry = new NBTTagCompound();
         entry.setInteger("chapterID", -1);
         dataList.appendTag(entry);
         payload.setTag("data", dataList);
         payload.setInteger("action", 3);
         NetChapterEdit.sendEdit(payload);
      } else if (btn.getButtonID() == 2) {
         this.mc.displayGuiScreen(new GuiImporters(this));
      } else if (btn.getButtonID() == 3) {
         this.mc.displayGuiScreen(new GuiQuestLineAddRemove(this, this.selected));
      } else if (btn.getButtonID() == 4 && this.selected != null) {
         this.mc.displayGuiScreen(new GuiDesigner(this, this.selected));
      } else if (btn.getButtonID() == 5 && btn instanceof PanelButtonStorage) {
         DBEntry<IQuestLine> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         this.selected = entry.getValue();
         this.selID = entry.getID();
         this.tfName.setText(this.selected.getUnlocalisedName());
         this.tfDesc.setText(this.selected.getUnlocalisedDescription());
         this.btnDesign.setActive(true);
         this.btnIcon.setActive(true);
         this.btnVis.setActive(true);
         this.btnVis.setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + this.selected.getProperty(NativeProps.VISIBILITY));
         this.reloadList();
      } else if (btn.getButtonID() == 6 && btn instanceof PanelButtonStorage) {
         DBEntry<IQuestLine> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         NBTTagCompound payload = new NBTTagCompound();
         payload.setIntArray("chapterIDs", new int[]{entry.getID()});
         payload.setInteger("action", 1);
         NetChapterEdit.sendEdit(payload);
      } else if (btn.getButtonID() == 7 && btn instanceof PanelButtonStorage) {
         DBEntry<IQuestLine> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         int order = QuestLineDatabase.INSTANCE.getOrderIndex(entry.getID());
         if (order > 0) {
            this.SendReorder(order);
         }
      } else if (btn.getButtonID() == 8) {
         this.mc.displayGuiScreen(new GuiTextEditor(this, this.tfDesc.getRawText(), (value) -> {
            if (this.selected != null) {
               this.tfDesc.setText(value);
               this.selected.setProperty(NativeProps.DESC, value);
               this.SendChanges(new DBEntry(this.selID, this.selected));
            }

         }));
      }

   }

   private void reloadList() {
      this.lineList.resetCanvas();
      int w = this.lineList.getTransform().getWidth();
      int i = 0;

      for(DBEntry<IQuestLine> entry : QuestLineDatabase.INSTANCE.getSortedEntries()) {
         IQuestLine ql = entry.getValue();
         PanelButtonStorage<DBEntry<IQuestLine>> tmp = new PanelButtonStorage<DBEntry<IQuestLine>>(new GuiRectangle(0, i * 16, w - 32, 16, 0), 5, QuestTranslation.translate(ql.getUnlocalisedName()), entry);
         tmp.setActive(entry.getID() != this.selID);
         this.lineList.addPanel(tmp);
         this.lineList.addPanel((new PanelButtonStorage(new GuiRectangle(w - 32, i * 16, 16, 16, 0), 6, "", entry)).setIcon(PresetIcon.ICON_TRASH.getTexture()));
         PanelButton btnUp = (new PanelButtonStorage(new GuiRectangle(w - 16, i * 16, 16, 16, 0), 7, "", entry)).setIcon(PresetIcon.ICON_UP.getTexture());
         btnUp.setActive(i > 0);
         this.lineList.addPanel(btnUp);
         ++i;
      }

   }

   private void SendChanges(DBEntry<IQuestLine> chapter) {
      NBTTagCompound payload = new NBTTagCompound();
      NBTTagList dataList = new NBTTagList();
      NBTTagCompound entry = new NBTTagCompound();
      entry.setInteger("chapterID", chapter.getID());
      entry.setTag("config", ((IQuestLine)chapter.getValue()).writeToNBT(new NBTTagCompound(), (List)null));
      dataList.appendTag(entry);
      payload.setTag("data", dataList);
      payload.setInteger("action", 0);
      NetChapterEdit.sendEdit(payload);
   }

   private void SendReorder(int indexToShift) {
      if (indexToShift > 0) {
         List<DBEntry<IQuestLine>> entries = QuestLineDatabase.INSTANCE.getSortedEntries();
         if (indexToShift < entries.size()) {
            int[] chapterIDs = new int[entries.size()];

            for(int i = 0; i < entries.size(); ++i) {
               chapterIDs[i] = ((DBEntry)entries.get(i)).getID();
            }

            int tmp = chapterIDs[indexToShift];
            chapterIDs[indexToShift] = chapterIDs[indexToShift - 1];
            chapterIDs[indexToShift - 1] = tmp;
            NBTTagCompound payload = new NBTTagCompound();
            payload.setIntArray("chapterIDs", chapterIDs);
            payload.setInteger("action", 2);
            NetChapterEdit.sendEdit(payload);
         }
      }
   }
}
