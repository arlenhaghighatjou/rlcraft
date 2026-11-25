package betterquesting.client.gui2;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
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
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.GuiQuestEditor;
import betterquesting.network.handlers.NetQuestAction;
import betterquesting.questing.QuestDatabase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiQuest extends GuiScreenCanvas implements IPEventListener, INeedsRefresh {
   private final int questID;
   private IQuest quest;
   private PanelButton btnTaskLeft;
   private PanelButton btnTaskRight;
   private PanelButton btnRewardLeft;
   private PanelButton btnRewardRight;
   private PanelButton btnDetect;
   private PanelButton btnClaim;
   private PanelTextBox titleReward;
   private PanelTextBox titleTask;
   private CanvasEmpty cvInner;
   private IGuiRect rectReward;
   private IGuiRect rectTask;
   private IGuiPanel pnReward;
   private IGuiPanel pnTask;
   private int rewardIndex = 0;
   private int taskIndex = 0;

   public GuiQuest(GuiScreen parent, int questID) {
      super(parent);
      this.questID = questID;
   }

   public void initPanel() {
      super.initPanel();
      this.quest = (IQuest)QuestDatabase.INSTANCE.getValue(this.questID);
      if (this.quest == null) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
         CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
         this.addPanel(cvBackground);
         PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate((String)this.quest.getProperty(NativeProps.NAME)))).setAlignment(1);
         panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
         cvBackground.addPanel(panTxt);
         if (((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(this.mc.player)) {
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 100, 16, 0), 0, QuestTranslation.translate("gui.back")));
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -16, 100, 16, 0), 1, QuestTranslation.translate("betterquesting.btn.edit")));
         } else {
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back")));
         }

         this.cvInner = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 32, 16, 24), 0));
         cvBackground.addPanel(this.cvInner);
         if (this.quest.getRewards().size() > 0) {
            CanvasScrolling cvDesc = new CanvasScrolling(new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 0.5F), new GuiPadding(0, 0, 16, 16), 0));
            this.cvInner.addPanel(cvDesc);
            PanelTextBox paDesc = new PanelTextBox(new GuiRectangle(0, 0, cvDesc.getTransform().getWidth(), 0), QuestTranslation.translate((String)this.quest.getProperty(NativeProps.DESC)), true);
            paDesc.setColor(PresetColor.TEXT_MAIN.getColor());
            cvDesc.addCulledPanel(paDesc, false);
            PanelVScrollBar paDescScroll = new PanelVScrollBar(new GuiTransform(GuiAlign.quickAnchor(GuiAlign.TOP_CENTER, GuiAlign.MID_CENTER), new GuiPadding(-16, 0, 8, 16), 0));
            this.cvInner.addPanel(paDescScroll);
            cvDesc.setScrollDriverY(paDescScroll);
            paDescScroll.setEnabled(cvDesc.getScrollBounds().getHeight() > 0);
            this.btnClaim = new PanelButton(new GuiTransform(new Vector4f(0.0F, 1.0F, 0.5F, 1.0F), new GuiPadding(16, -16, 24, 0), 0), 6, QuestTranslation.translate("betterquesting.btn.claim"));
            this.btnClaim.setActive(false);
            this.cvInner.addPanel(this.btnClaim);
            this.btnRewardLeft = new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, new GuiPadding(0, -16, -16, 0), 0), 2, "<");
            this.btnRewardLeft.setActive(this.rewardIndex > 0);
            this.cvInner.addPanel(this.btnRewardLeft);
            this.btnRewardRight = new PanelButton(new GuiTransform(new Vector4f(0.5F, 1.0F, 0.5F, 1.0F), new GuiPadding(-24, -16, 8, 0), 0), 3, ">");
            this.btnRewardRight.setActive(this.rewardIndex < this.quest.getRewards().size() - 1);
            this.cvInner.addPanel(this.btnRewardRight);
            this.rectReward = new GuiTransform(new Vector4f(0.0F, 0.5F, 0.5F, 1.0F), new GuiPadding(0, 0, 8, 16), 0);
            this.rectReward.setParent(this.cvInner.getTransform());
            this.titleReward = new PanelTextBox(new GuiTransform(new Vector4f(0.0F, 0.5F, 0.5F, 0.5F), new GuiPadding(0, -16, 8, 0), 0), "?");
            this.titleReward.setColor(PresetColor.TEXT_HEADER.getColor()).setAlignment(1);
            this.cvInner.addPanel(this.titleReward);
            this.refreshRewardPanel();
         } else {
            CanvasScrolling cvDesc = new CanvasScrolling(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(0, 0, 16, 0), 0));
            this.cvInner.addPanel(cvDesc);
            PanelTextBox paDesc = new PanelTextBox(new GuiRectangle(0, 0, cvDesc.getTransform().getWidth(), 0), QuestTranslation.translate((String)this.quest.getProperty(NativeProps.DESC)), true);
            paDesc.setColor(PresetColor.TEXT_MAIN.getColor());
            cvDesc.addCulledPanel(paDesc, false);
            PanelVScrollBar paDescScroll = new PanelVScrollBar(new GuiTransform(GuiAlign.quickAnchor(GuiAlign.TOP_CENTER, GuiAlign.BOTTOM_CENTER), new GuiPadding(-16, 0, 8, 0), 0));
            this.cvInner.addPanel(paDescScroll);
            cvDesc.setScrollDriverY(paDescScroll);
            paDescScroll.setEnabled(cvDesc.getScrollBounds().getHeight() > 0);
         }

         this.btnDetect = new PanelButton(new GuiTransform(new Vector4f(0.5F, 1.0F, 1.0F, 1.0F), new GuiPadding(24, -16, 16, 0), 0), 7, QuestTranslation.translate("betterquesting.btn.detect_submit"));
         this.btnDetect.setActive(false);
         this.cvInner.addPanel(this.btnDetect);
         this.btnTaskLeft = new PanelButton(new GuiTransform(new Vector4f(0.5F, 1.0F, 0.5F, 1.0F), new GuiPadding(8, -16, -24, 0), 0), 4, "<");
         this.btnTaskLeft.setActive(this.taskIndex > 0);
         this.cvInner.addPanel(this.btnTaskLeft);
         this.btnTaskRight = new PanelButton(new GuiTransform(GuiAlign.BOTTOM_RIGHT, new GuiPadding(-16, -16, 0, 0), 0), 5, ">");
         this.btnTaskRight.setActive(this.taskIndex < this.quest.getTasks().size() - 1);
         this.cvInner.addPanel(this.btnTaskRight);
         this.rectTask = new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 16, 0, 16), 0);
         this.rectTask.setParent(this.cvInner.getTransform());
         this.titleTask = new PanelTextBox(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(8, 0, 0, -16), 0), "?");
         this.titleTask.setColor(PresetColor.TEXT_HEADER.getColor()).setAlignment(1);
         this.cvInner.addPanel(this.titleTask);
         this.refreshTaskPanel();
         IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 0, 0, 0, 0);
         ls0.setParent(this.cvInner.getTransform());
         IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, 0, 0, 0, 0);
         le0.setParent(this.cvInner.getTransform());
         PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
         this.cvInner.addPanel(paLine0);
      }
   }

   public void refreshGui() {
      this.refreshTaskPanel();
      this.refreshRewardPanel();
      this.updateButtons();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (super.onMouseClick(mx, my, click)) {
         this.updateButtons();
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      if (super.onMouseScroll(mx, my, scroll)) {
         this.updateButtons();
         return true;
      } else {
         return false;
      }
   }

   public boolean onKeyTyped(char c, int keycode) {
      if (super.onKeyTyped(c, keycode)) {
         this.updateButtons();
         return true;
      } else {
         return false;
      }
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
         this.mc.displayGuiScreen(new GuiQuestEditor(this, this.questID));
      } else if (btn.getButtonID() == 2) {
         this.rewardIndex = MathHelper.clamp(this.rewardIndex - 1, 0, this.quest.getRewards().size() - 1);
         this.refreshRewardPanel();
      } else if (btn.getButtonID() == 3) {
         this.rewardIndex = MathHelper.clamp(this.rewardIndex + 1, 0, this.quest.getRewards().size() - 1);
         this.refreshRewardPanel();
      } else if (btn.getButtonID() == 4) {
         this.taskIndex = MathHelper.clamp(this.taskIndex - 1, 0, this.quest.getTasks().size() - 1);
         this.refreshTaskPanel();
      } else if (btn.getButtonID() == 5) {
         this.taskIndex = MathHelper.clamp(this.taskIndex + 1, 0, this.quest.getTasks().size() - 1);
         this.refreshTaskPanel();
      } else if (btn.getButtonID() == 6) {
         NetQuestAction.requestClaim(new int[]{this.questID});
      } else if (btn.getButtonID() == 7) {
         NetQuestAction.requestDetect(new int[]{this.questID});
      }

   }

   private void refreshRewardPanel() {
      if (this.pnReward != null) {
         this.cvInner.removePanel(this.pnReward);
      }

      if (this.rewardIndex >= 0 && this.rewardIndex < this.quest.getRewards().size()) {
         if (this.rectReward == null) {
            this.initPanel();
         } else {
            IReward rew = (IReward)((DBEntry)this.quest.getRewards().getEntries().get(this.rewardIndex)).getValue();
            this.pnReward = rew.getRewardGui(this.rectReward, new DBEntry(this.questID, this.quest));
            if (this.pnReward != null) {
               this.cvInner.addPanel(this.pnReward);
            }

            if (this.titleReward != null) {
               this.titleReward.setText(QuestTranslation.translate(rew.getUnlocalisedName()));
               this.titleReward.setEnabled(true);
            }

            this.updateButtons();
         }
      } else {
         if (this.titleReward != null) {
            this.titleReward.setText("?");
            this.titleReward.setEnabled(false);
         }

         this.updateButtons();
      }
   }

   private void refreshTaskPanel() {
      if (this.pnTask != null) {
         this.cvInner.removePanel(this.pnTask);
      }

      if (this.taskIndex >= 0 && this.taskIndex < this.quest.getTasks().size()) {
         ITask tsk = (ITask)((DBEntry)this.quest.getTasks().getEntries().get(this.taskIndex)).getValue();
         this.pnTask = tsk.getTaskGui(this.rectTask, new DBEntry(this.questID, this.quest));
         if (this.pnTask != null) {
            this.cvInner.addPanel(this.pnTask);
         }

         this.titleTask.setText(QuestTranslation.translate(tsk.getUnlocalisedName()));
         this.titleTask.setEnabled(true);
         this.updateButtons();
      } else {
         this.titleTask.setText("?");
         this.titleTask.setEnabled(false);
         this.updateButtons();
      }
   }

   private void updateButtons() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.btnRewardLeft != null && this.btnRewardRight != null && this.btnClaim != null) {
         this.btnRewardLeft.setActive(this.rewardIndex > 0);
         this.btnRewardRight.setActive(this.rewardIndex < this.quest.getRewards().size() - 1);
         this.btnClaim.setActive(this.quest.getRewards().size() > 0 && this.quest.canClaim(mc.player));
      }

      if (this.btnTaskLeft != null && this.btnTaskRight != null && this.btnDetect != null) {
         this.btnTaskLeft.setActive(this.taskIndex > 0);
         this.btnTaskRight.setActive(this.taskIndex < this.quest.getTasks().size() - 1);
         this.btnDetect.setActive(this.quest.canSubmit(mc.player));
      }

   }
}
