package betterquesting.client.gui2.editors;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
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
import betterquesting.api2.client.gui.panels.lists.CanvasQuestDatabase;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.GuiQuest;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import betterquesting.questing.QuestLineEntry;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class GuiQuestLineAddRemove extends GuiScreenCanvas implements IPEventListener, IVolatileScreen, INeedsRefresh {
   @Nullable
   private IQuestLine questLine;
   private final int lineID;
   private CanvasQuestDatabase canvasDB;
   private CanvasScrolling canvasQL;

   public GuiQuestLineAddRemove(GuiScreen parent, @Nullable IQuestLine questLine) {
      super(parent);
      this.questLine = questLine;
      this.lineID = QuestLineDatabase.INSTANCE.getID(questLine);
   }

   public void refreshGui() {
      this.questLine = this.lineID < 0 ? null : (IQuestLine)QuestLineDatabase.INSTANCE.getValue(this.lineID);
      this.canvasDB.refreshSearch();
      if (this.questLine != null) {
         this.refreshQuestList();
      }

   }

   public void initPanel() {
      super.initPanel();
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.edit_line2", this.questLine == null ? "" : QuestTranslation.translate(this.questLine.getUnlocalisedName())))).setAlignment(1);
      panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
      cvBackground.addPanel(panTxt);
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back")));
      CanvasEmpty cvLeft = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 8, 24), 8));
      cvBackground.addPanel(cvLeft);
      if (this.questLine != null) {
         PanelTextBox txtQuest = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), QuestTranslation.translate(this.questLine.getUnlocalisedName()))).setAlignment(1).setColor(PresetColor.TEXT_MAIN.getColor());
         cvLeft.addPanel(txtQuest);
      }

      this.canvasQL = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 0));
      cvLeft.addPanel(this.canvasQL);
      PanelVScrollBar scReq = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 0), 0));
      cvLeft.addPanel(scReq);
      this.canvasQL.setScrollDriverY(scReq);
      CanvasEmpty cvRight = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 24), 0));
      cvBackground.addPanel(cvRight);
      PanelTextBox txtDb = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), QuestTranslation.translate("betterquesting.gui.database"))).setAlignment(1).setColor(PresetColor.TEXT_MAIN.getColor());
      cvRight.addPanel(txtDb);
      PanelTextField<String> searchBox = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 8, -32), 0), "", FieldFilterString.INSTANCE);
      searchBox.setWatermark("Search...");
      cvRight.addPanel(searchBox);
      this.canvasDB = new CanvasQuestDatabase(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 32, 8, 24), 0)) {
         protected boolean addResult(DBEntry<IQuest> entry, int index, int width) {
            PanelButtonStorage<DBEntry<IQuest>> btnAdd = new PanelButtonStorage<DBEntry<IQuest>>(new GuiRectangle(0, index * 16, 16, 16, 0), 2, "", entry);
            btnAdd.setIcon(PresetIcon.ICON_POSITIVE.getTexture());
            btnAdd.setActive(GuiQuestLineAddRemove.this.questLine != null && GuiQuestLineAddRemove.this.questLine.getValue(entry.getID()) == null);
            this.addPanel(btnAdd);
            PanelButtonStorage<DBEntry<IQuest>> btnEdit = new PanelButtonStorage<DBEntry<IQuest>>(new GuiRectangle(16, index * 16, width - 32, 16, 0), 1, QuestTranslation.translate((String)((IQuest)entry.getValue()).getProperty(NativeProps.NAME)), entry);
            this.addPanel(btnEdit);
            PanelButtonStorage<DBEntry<IQuest>> btnDel = new PanelButtonStorage<DBEntry<IQuest>>(new GuiRectangle(width - 16, index * 16, 16, 16, 0), 4, "", entry);
            btnDel.setIcon(PresetIcon.ICON_TRASH.getTexture());
            this.addPanel(btnDel);
            return true;
         }
      };
      cvRight.addPanel(this.canvasDB);
      CanvasQuestDatabase var10001 = this.canvasDB;
      this.canvasDB.getClass();
      searchBox.setCallback(var10001::setSearchFilter);
      PanelVScrollBar scDb = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 32, 0, 24), 0));
      cvRight.addPanel(scDb);
      this.canvasDB.setScrollDriverY(scDb);
      PanelButton btnNew = new PanelButton(new GuiTransform(GuiAlign.BOTTOM_EDGE, new GuiPadding(0, -16, 0, 0), 0), 5, QuestTranslation.translate("betterquesting.btn.new"));
      cvRight.addPanel(btnNew);
      IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
      ls0.setParent(cvBackground.getTransform());
      IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -24, 0, 0, 0);
      le0.setParent(cvBackground.getTransform());
      PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
      cvBackground.addPanel(paLine0);
      this.refreshQuestList();
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
         DBEntry<IQuest> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         this.mc.displayGuiScreen(new GuiQuest(this, entry.getID()));
      } else if (btn.getButtonID() == 2) {
         DBEntry<IQuest> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         IQuestLineEntry qe = new QuestLineEntry(0, 0);
         int x1 = 0;

         int y1;
         int s2;
         label54:
         for(y1 = 0; this.questLine != null; y1 += s2) {
            Iterator var7 = this.questLine.getEntries().iterator();

            while(true) {
               if (!var7.hasNext()) {
                  break label54;
               }

               DBEntry<IQuestLineEntry> qe2 = (DBEntry)var7.next();
               int x2 = ((IQuestLineEntry)qe2.getValue()).getPosX();
               int y2 = ((IQuestLineEntry)qe2.getValue()).getPosY();
               s2 = Math.max(((IQuestLineEntry)qe2.getValue()).getSizeX(), ((IQuestLineEntry)qe2.getValue()).getSizeY());
               if (x1 >= x2 && x1 < x2 + s2 && y1 >= y2 && y1 < y2 + s2) {
                  break;
               }
            }

            x1 += s2;
         }

         qe.setPosition(x1, y1);
         this.questLine.add(entry.getID(), qe);
         this.SendChanges();
      } else if (btn.getButtonID() == 3 && this.questLine != null) {
         DBEntry<IQuest> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         this.questLine.removeID(entry.getID());
         this.SendChanges();
      } else if (btn.getButtonID() == 4) {
         DBEntry<IQuest> entry = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         NBTTagCompound payload = new NBTTagCompound();
         payload.setIntArray("questIDs", new int[]{entry.getID()});
         payload.setInteger("action", 1);
         NetQuestEdit.sendEdit(payload);
      } else if (btn.getButtonID() == 5) {
         NBTTagCompound payload = new NBTTagCompound();
         NBTTagList dataList = new NBTTagList();
         NBTTagCompound entry = new NBTTagCompound();
         entry.setInteger("questID", -1);
         dataList.appendTag(entry);
         payload.setTag("data", dataList);
         payload.setInteger("action", 3);
         NetQuestEdit.sendEdit(payload);
      } else if (btn.getButtonID() == 6) {
         NBTTagCompound payload = new NBTTagCompound();
         payload.setIntArray("questIDs", new int[]{((Integer)((PanelButtonStorage)btn).getStoredValue()).intValue()});
         payload.setInteger("action", 1);
         NetQuestEdit.sendEdit(payload);
      }

   }

   private void refreshQuestList() {
      this.canvasQL.resetCanvas();
      if (this.questLine != null) {
         int width = this.canvasQL.getTransform().getWidth();
         List<DBEntry<IQuestLineEntry>> qles = this.questLine.getEntries();

         for(int i = 0; i < qles.size(); ++i) {
            DBEntry<IQuestLineEntry> entry = (DBEntry)qles.get(i);
            IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(entry.getID());
            if (quest == null) {
               PanelButtonStorage<Integer> btnErr = new PanelButtonStorage<Integer>(new GuiRectangle(width - 16, i * 16, 16, 16, 0), 6, "[ERROR]", entry.getID());
               btnErr.setActive(true);
               this.canvasQL.addPanel(btnErr);
            } else {
               PanelButtonStorage<DBEntry<IQuest>> btnEdit = new PanelButtonStorage<DBEntry<IQuest>>(new GuiRectangle(0, i * 16, width - 16, 16, 0), 1, QuestTranslation.translate((String)quest.getProperty(NativeProps.NAME)), new DBEntry(entry.getID(), quest));
               this.canvasQL.addPanel(btnEdit);
               PanelButtonStorage<DBEntry<IQuest>> btnRem = new PanelButtonStorage<DBEntry<IQuest>>(new GuiRectangle(width - 16, i * 16, 16, 16, 0), 3, "", new DBEntry(entry.getID(), quest));
               btnRem.setIcon(PresetIcon.ICON_NEGATIVE.getTexture());
               this.canvasQL.addPanel(btnRem);
            }
         }

      }
   }

   private void SendChanges() {
      if (this.questLine != null) {
         NBTTagCompound payload = new NBTTagCompound();
         NBTTagList dataList = new NBTTagList();
         NBTTagCompound entry = new NBTTagCompound();
         entry.setInteger("chapterID", this.lineID);
         entry.setTag("config", this.questLine.writeToNBT(new NBTTagCompound(), (List)null));
         dataList.appendTag(entry);
         payload.setTag("data", dataList);
         payload.setInteger("action", 0);
         NetChapterEdit.sendEdit(payload);
      }
   }
}
