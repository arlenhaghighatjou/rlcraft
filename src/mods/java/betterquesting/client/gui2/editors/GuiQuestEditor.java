package betterquesting.client.gui2.editors;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.misc.ICallback;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.nbt.GuiItemSelection;
import betterquesting.client.gui2.editors.nbt.GuiNbtEditor;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.input.Keyboard;

public class GuiQuestEditor extends GuiScreenCanvas implements IPEventListener, IVolatileScreen, INeedsRefresh {
   private final int questID;
   private IQuest quest;
   private PanelTextBox pnTitle;
   private PanelTextField<String> flName;
   private PanelTextField<String> flDesc;
   private PanelButton btnLogic;
   private PanelButton btnVis;

   public GuiQuestEditor(GuiScreen parent, int questID) {
      super(parent);
      this.questID = questID;
   }

   public void refreshGui() {
      this.quest = (IQuest)QuestDatabase.INSTANCE.getValue(this.questID);
      if (this.quest == null) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         this.pnTitle.setText(QuestTranslation.translate("betterquesting.title.edit_quest", QuestTranslation.translate((String)this.quest.getProperty(NativeProps.NAME))));
         if (!this.flName.isFocused()) {
            this.flName.setText((String)this.quest.getProperty(NativeProps.NAME));
         }

         if (!this.flDesc.isFocused()) {
            this.flDesc.setText((String)this.quest.getProperty(NativeProps.DESC));
         }

         this.btnLogic.setText(QuestTranslation.translate("betterquesting.btn.logic") + ": " + this.quest.getProperty(NativeProps.LOGIC_QUEST));
         this.btnVis.setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + this.quest.getProperty(NativeProps.VISIBILITY));
      }

   }

   public void initPanel() {
      super.initPanel();
      this.quest = (IQuest)QuestDatabase.INSTANCE.getValue(this.questID);
      if (this.quest == null) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
         Keyboard.enableRepeatEvents(true);
         CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
         this.addPanel(cvBackground);
         this.pnTitle = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.edit_quest", QuestTranslation.translate((String)this.quest.getProperty(NativeProps.NAME))))).setAlignment(1);
         this.pnTitle.setColor(PresetColor.TEXT_HEADER.getColor());
         cvBackground.addPanel(this.pnTitle);
         cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back")));
         PanelTextBox pnName = new PanelTextBox(new GuiTransform(GuiAlign.MID_CENTER, -100, -60, 200, 12, 0), QuestTranslation.translate("betterquesting.gui.name"));
         pnName.setColor(PresetColor.TEXT_MAIN.getColor());
         cvBackground.addPanel(pnName);
         this.flName = new PanelTextField<String>(new GuiTransform(GuiAlign.MID_CENTER, -100, -48, 200, 16, 0), (String)this.quest.getProperty(NativeProps.NAME), FieldFilterString.INSTANCE);
         this.flName.setMaxLength(Integer.MAX_VALUE);
         cvBackground.addPanel(this.flName);
         PanelTextBox pnDesc = new PanelTextBox(new GuiTransform(GuiAlign.MID_CENTER, -100, -28, 200, 12, 0), QuestTranslation.translate("betterquesting.gui.description"));
         pnDesc.setColor(PresetColor.TEXT_MAIN.getColor());
         cvBackground.addPanel(pnDesc);
         this.flDesc = new PanelTextField<String>(new GuiTransform(GuiAlign.MID_CENTER, -100, -16, 184, 16, 0), (String)this.quest.getProperty(NativeProps.DESC), FieldFilterString.INSTANCE);
         this.flDesc.setMaxLength(Integer.MAX_VALUE);
         cvBackground.addPanel(this.flDesc);
         PanelButton btnDesc = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, 84, -16, 16, 16, 0), 7, "Aa");
         cvBackground.addPanel(btnDesc);
         PanelButton btnTsk = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -100, 16, 100, 16, 0), 1, QuestTranslation.translate("betterquesting.btn.tasks"));
         cvBackground.addPanel(btnTsk);
         PanelButton btnRew = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, 0, 16, 100, 16, 0), 2, QuestTranslation.translate("betterquesting.btn.rewards"));
         cvBackground.addPanel(btnRew);
         PanelButton btnReq = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -100, 32, 100, 16, 0), 3, QuestTranslation.translate("betterquesting.btn.requirements"));
         cvBackground.addPanel(btnReq);
         PanelButton btnIco = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, 0, 32, 100, 16, 0), 8, QuestTranslation.translate("betterquesting.btn.icon"));
         cvBackground.addPanel(btnIco);
         this.btnVis = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -100, 48, 100, 16, 0), 5, QuestTranslation.translate("betterquesting.btn.show") + ": " + this.quest.getProperty(NativeProps.VISIBILITY));
         cvBackground.addPanel(this.btnVis);
         this.btnLogic = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, 0, 48, 100, 16, 0), 6, QuestTranslation.translate("betterquesting.btn.logic") + ": " + this.quest.getProperty(NativeProps.LOGIC_QUEST));
         cvBackground.addPanel(this.btnLogic);
         PanelButton btnAdv = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -100, 64, 200, 16, 0), 4, QuestTranslation.translate("betterquesting.btn.advanced"));
         cvBackground.addPanel(btnAdv);
      }
   }

   public boolean onMouseClick(int mx, int my, int button) {
      boolean result = super.onMouseClick(mx, my, button);
      boolean flag = false;
      if (!((String)this.quest.getProperty(NativeProps.NAME)).equals(this.flName.getValue())) {
         this.quest.setProperty(NativeProps.NAME, this.flName.getValue());
         flag = true;
      }

      if (!((String)this.quest.getProperty(NativeProps.DESC)).equals(this.flDesc.getValue())) {
         this.quest.setProperty(NativeProps.DESC, this.flDesc.getValue());
         flag = true;
      }

      if (flag) {
         this.SendChanges();
      }

      return result;
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      IPanelButton btn = event.getButton();
      switch(btn.getButtonID()) {
      case 0:
         this.mc.displayGuiScreen(this.parent);
         break;
      case 1:
         this.mc.displayGuiScreen(new GuiTaskEditor(this, this.quest));
         break;
      case 2:
         this.mc.displayGuiScreen(new GuiRewardEditor(this, this.quest));
         break;
      case 3:
         this.mc.displayGuiScreen(new GuiPrerequisiteEditor(this, this.quest));
         break;
      case 4:
         this.mc.displayGuiScreen(new GuiNbtEditor(this, (NBTTagCompound)this.quest.writeToNBT(new NBTTagCompound()), (value) -> {
            this.quest.readFromNBT(value);
            this.SendChanges();
         }));
         break;
      case 5:
         EnumQuestVisibility[] visList = EnumQuestVisibility.values();
         EnumQuestVisibility vis = (EnumQuestVisibility)this.quest.getProperty(NativeProps.VISIBILITY);
         vis = visList[(vis.ordinal() + 1) % visList.length];
         this.quest.setProperty(NativeProps.VISIBILITY, vis);
         ((PanelButton)btn).setText(QuestTranslation.translate("betterquesting.btn.show") + ": " + vis);
         this.SendChanges();
         break;
      case 6:
         EnumLogic[] logicList = EnumLogic.values();
         EnumLogic logic = (EnumLogic)this.quest.getProperty(NativeProps.LOGIC_QUEST);
         logic = logicList[(logic.ordinal() + 1) % logicList.length];
         this.quest.setProperty(NativeProps.LOGIC_QUEST, logic);
         ((PanelButton)btn).setText(QuestTranslation.translate("betterquesting.btn.logic") + ": " + logic);
         this.SendChanges();
         break;
      case 7:
         this.mc.displayGuiScreen(new GuiTextEditor(this, (String)this.quest.getProperty(NativeProps.DESC), (value) -> {
            this.quest.setProperty(NativeProps.DESC, value);
            this.SendChanges();
         }));
         break;
      case 8:
         this.mc.displayGuiScreen(new GuiItemSelection(this, (BigItemStack)this.quest.getProperty(NativeProps.ICON), (value) -> {
            this.quest.setProperty(NativeProps.ICON, value);
            this.SendChanges();
         }));
      }

   }

   private void SendChanges() {
      NBTTagCompound payload = new NBTTagCompound();
      NBTTagList dataList = new NBTTagList();
      NBTTagCompound entry = new NBTTagCompound();
      entry.setInteger("questID", this.questID);
      entry.setTag("config", this.quest.writeToNBT(new NBTTagCompound()));
      dataList.appendTag(entry);
      payload.setTag("data", dataList);
      payload.setInteger("action", 0);
      NetQuestEdit.sendEdit(payload);
   }
}
