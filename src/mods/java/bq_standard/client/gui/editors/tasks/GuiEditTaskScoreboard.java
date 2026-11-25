package bq_standard.client.gui.editors.tasks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.nbt_doc.INbtDoc;
import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterNumber;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.IThemeRegistry;
import betterquesting.api2.client.gui.themes.gui_args.GArgsNBT;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetGUIs;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.tasks.TaskScoreboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class GuiEditTaskScoreboard extends GuiScreenCanvas implements IVolatileScreen {
   private final DBEntry<IQuest> quest;
   private final TaskScoreboard task;
   private static final ResourceLocation QUEST_EDIT = new ResourceLocation("betterquesting:quest_edit");

   public GuiEditTaskScoreboard(GuiScreen parent, DBEntry<IQuest> quest, TaskScoreboard task) {
      super(parent);
      this.quest = quest;
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      cvBackground.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 16, -32), 0), QuestTranslation.translate("bq_standard.title.edit_scoreboard", new Object[0]))).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor()));
      cvBackground.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.MID_CENTER, -100, -28, 50, 12, 0), QuestTranslation.translate("betterquesting.gui.name", new Object[0]))).setColor(PresetColor.TEXT_MAIN.getColor()));
      cvBackground.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.MID_CENTER, -100, -12, 50, 12, 0), "ID")).setColor(PresetColor.TEXT_MAIN.getColor()));
      cvBackground.addPanel((new PanelTextField(new GuiTransform(GuiAlign.MID_CENTER, -50, -32, 150, 16, 0), this.task.scoreDisp, FieldFilterString.INSTANCE)).setCallback((value) -> {
         this.task.scoreDisp = value;
      }));
      cvBackground.addPanel((new PanelTextField(new GuiTransform(GuiAlign.MID_CENTER, -50, -16, 150, 16, 0), this.task.scoreName, FieldFilterString.INSTANCE)).setCallback((value) -> {
         this.task.scoreName = value;
      }));
      cvBackground.addPanel(new PanelButtonStorage<TaskScoreboard.ScoreOperation>(new GuiTransform(GuiAlign.MID_CENTER, -100, 0, 50, 16, 0), -1, this.task.operation.GetText(), this.task.operation) {
         public void onButtonClick() {
            TaskScoreboard.ScoreOperation[] v = TaskScoreboard.ScoreOperation.values();
            TaskScoreboard.ScoreOperation n = v[(((TaskScoreboard.ScoreOperation)this.getStoredValue()).ordinal() + 1) % v.length];
            this.setStoredValue(n);
            this.setText(n.GetText());
            GuiEditTaskScoreboard.this.task.operation = n;
         }
      });
      cvBackground.addPanel((new PanelTextField(new GuiTransform(GuiAlign.MID_CENTER, -50, 0, 150, 16, 0), "" + this.task.target, FieldFilterNumber.INT)).setCallback((value) -> {
         this.task.target = value.intValue();
      }));
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -100, 16, 200, 16, 0), -1, QuestTranslation.translate("betterquesting.btn.advanced", new Object[0])) {
         public void onButtonClick() {
            GuiEditTaskScoreboard.this.mc.displayGuiScreen(((IThemeRegistry)QuestingAPI.getAPI(ApiReference.THEME_REG)).getGui(PresetGUIs.EDIT_NBT, new GArgsNBT(GuiEditTaskScoreboard.this, GuiEditTaskScoreboard.this.task.writeToNBT(new NBTTagCompound()), GuiEditTaskScoreboard.this.task::readFromNBT, (INbtDoc)null)));
         }
      });
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.back", new Object[0])) {
         public void onButtonClick() {
            GuiEditTaskScoreboard.this.sendChanges();
            GuiEditTaskScoreboard.this.mc.displayGuiScreen(GuiEditTaskScoreboard.this.parent);
         }
      });
   }

   private void sendChanges() {
      NBTTagCompound payload = new NBTTagCompound();
      NBTTagList dataList = new NBTTagList();
      NBTTagCompound entry = new NBTTagCompound();
      entry.setInteger("questID", this.quest.getID());
      entry.setTag("config", ((IQuest)this.quest.getValue()).writeToNBT(new NBTTagCompound()));
      dataList.appendTag(entry);
      payload.setTag("data", dataList);
      payload.setInteger("action", 0);
      ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToServer(new QuestingPacket(QUEST_EDIT, payload));
   }
}
