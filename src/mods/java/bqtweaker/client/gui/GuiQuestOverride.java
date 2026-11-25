package bqtweaker.client.gui;

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
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.popups.PopContextMenu;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.SimpleTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.GuiQuestEditor;
import betterquesting.client.gui2.editors.GuiTaskEditor;
import betterquesting.network.handlers.NetQuestAction;
import betterquesting.questing.QuestDatabase;
import bqtweaker.client.util.MobendPatcher;
import bqtweaker.handlers.ConfigHandler;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.util.vector.Vector4f;

public class GuiQuestOverride extends GuiScreenCanvas implements IPEventListener, INeedsRefresh {
   private final int questID;
   private IQuest quest;
   private PanelButton btnDetect;
   private PanelButton btnClaim;
   private CanvasScrolling cvDesc;
   private PanelVScrollBar paDescScroll;
   private CanvasEmpty cvInner;
   private IGuiRect rectReward;
   private IGuiRect rectTask;
   private CanvasScrolling tskScroll;
   private PanelVScrollBar tskScrollBar;

   public GuiQuestOverride(GuiScreen parent, int questID) {
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
         PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate((String)this.quest.getProperty(NativeProps.NAME), new Object[0]))).setAlignment(1);
         panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
         cvBackground.addPanel(panTxt);
         if (((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(this.mc.player)) {
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 100, 16, 0), 0, QuestTranslation.translate("gui.back", new Object[0])));
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -16, 100, 16, 0), 1, QuestTranslation.translate("betterquesting.btn.edit", new Object[0])));
         } else {
            cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back", new Object[0])));
         }

         this.cvInner = new CanvasEmpty(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 32, 16, 24), 0));
         cvBackground.addPanel(this.cvInner);
         if (this.quest.getRewards().size() > 0) {
            this.btnClaim = new PanelButton(new GuiTransform(new Vector4f(0.0F, 1.0F, 0.5F, 1.0F), new GuiPadding(16, -16, 24, 0), 0), 6, QuestTranslation.translate("betterquesting.btn.claim", new Object[0]));
            this.btnClaim.setActive(false);
            this.cvInner.addPanel(this.btnClaim);
         }

         this.rectReward = new GuiTransform(new Vector4f(0.0F, 0.5F, 0.5F, 1.0F), new GuiPadding(0, 0, 16, 16), 0);
         this.rectReward.setParent(this.cvInner.getTransform());
         this.refreshRewardDescPanel();
         this.btnDetect = new PanelButton(new GuiTransform(new Vector4f(0.5F, 1.0F, 1.0F, 1.0F), new GuiPadding(24, -16, 16, 0), 0), 7, QuestTranslation.translate("betterquesting.btn.detect_submit", new Object[0]));
         this.btnDetect.setActive(false);
         this.cvInner.addPanel(this.btnDetect);
         this.rectTask = new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 0, 0, 18), 0);
         this.rectTask.setParent(this.cvInner.getTransform());
         CanvasEmpty cvTaskPopup = new CanvasEmpty(this.rectTask) {
            public boolean onMouseClick(int mx, int my, int click) {
               if (click != 1) {
                  return false;
               } else if (GuiQuestOverride.this.rectTask.getX() < mx && GuiQuestOverride.this.rectTask.getX() + GuiQuestOverride.this.rectTask.getWidth() > mx && GuiQuestOverride.this.rectTask.getY() < my && GuiQuestOverride.this.rectTask.getY() + GuiQuestOverride.this.rectTask.getHeight() > my) {
                  if (((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(GuiQuestOverride.this.mc.player)) {
                     PopContextMenu popup = new PopContextMenu(new GuiRectangle(mx, my, 64, 16), true);
                     final GuiTaskEditor editor = new GuiTaskEditor(new GuiQuestOverride(GuiQuestOverride.this.parent, GuiQuestOverride.this.questID), GuiQuestOverride.this.quest);
                     Runnable action = new Runnable() {
                        public void run() {
                           GuiQuestOverride.this.mc.displayGuiScreen(editor);
                        }
                     };
                     popup.addButton(QuestTranslation.translate("bqtweaker.context.task", new Object[0]), (IGuiTexture)null, action);
                     GuiQuestOverride.this.openPopup(popup);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }
         };
         this.cvInner.addPanel(cvTaskPopup);
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
      this.refreshRewardDescPanel();
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
      } else if (btn.getButtonID() == 6) {
         NetQuestAction.requestClaim(new int[]{this.questID});
      } else if (btn.getButtonID() == 7) {
         NetQuestAction.requestDetect(new int[]{this.questID});
      }

   }

   private void refreshRewardDescPanel() {
      if (this.cvDesc != null) {
         this.cvInner.removePanel(this.cvDesc);
         this.cvInner.removePanel(this.paDescScroll);
      }

      this.cvDesc = new CanvasScrolling(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(0, 0, 16, 18), 0));
      this.cvInner.addPanel(this.cvDesc);
      String desc = QuestTranslation.translate((String)this.quest.getProperty(NativeProps.DESC), new Object[0]);
      int panelPrevY = 0;
      int panelPrevHeight = 0;
      if (desc.contains("{Embed}")) {
         String[] descSplit = desc.split("\\{Embed\\}");

         for(String split : descSplit) {
            if (!split.startsWith("TypeImage") && !split.startsWith("TypeLink")) {
               PanelTextBox paDesc = new PanelTextBox(new GuiRectangle(0, panelPrevY + panelPrevHeight + 12, this.cvDesc.getTransform().getWidth(), 0), split, true);
               paDesc.setColor(PresetColor.TEXT_MAIN.getColor());
               this.cvDesc.addCulledPanel(paDesc, false);
               panelPrevY = panelPrevY + panelPrevHeight + 12;
               panelPrevHeight = paDesc.getTransform().getHeight();
            } else {
               try {
                  String[] embedEntry = split.split(";");
                  int width = Math.min(this.cvDesc.getTransform().getWidth(), Integer.parseInt(embedEntry[2]));
                  int height = Integer.parseInt(embedEntry[3]);
                  if (embedEntry[0].contentEquals("TypeImage")) {
                     IGuiTexture embedImage = new SimpleTexture(new ResourceLocation(embedEntry[1]), new GuiRectangle(0, 0, Integer.parseInt(embedEntry[4]), Integer.parseInt(embedEntry[5])));
                     this.cvDesc.addCulledPanel(new PanelGeneric(new GuiRectangle((this.cvDesc.getTransform().getWidth() - width) / 2, panelPrevY + panelPrevHeight + 8, width, height), embedImage), false);
                  } else if (embedEntry[0].contentEquals("TypeLink")) {
                     PanelButton btnLink = new PanelButton(new GuiRectangle((this.cvDesc.getTransform().getWidth() - width) / 2, panelPrevY + panelPrevHeight + 8, width, height), 69, embedEntry[4]);
                     btnLink.setClickAction((b) -> {
                        TextComponentString component = new TextComponentString(embedEntry[1]);
                        component.setStyle((new Style()).setClickEvent(new ClickEvent(Action.OPEN_URL, embedEntry[1])));
                        this.handleComponentClick(component);
                     });
                     btnLink.setTextAlignment(1);
                     this.cvDesc.addCulledPanel(btnLink, false);
                  }

                  panelPrevY = panelPrevY + panelPrevHeight + 8;
                  panelPrevHeight = height;
               } catch (Exception var14) {
                  ;
               }
            }
         }

         panelPrevY = panelPrevY + 32;
      } else {
         PanelTextBox paDesc = new PanelTextBox(new GuiRectangle(0, 0, this.cvDesc.getTransform().getWidth(), 0), desc, true);
         paDesc.setColor(PresetColor.TEXT_MAIN.getColor());
         this.cvDesc.addCulledPanel(paDesc, false);
         panelPrevY = 32;
         panelPrevHeight = paDesc.getTransform().getHeight() + 8;
      }

      this.paDescScroll = new PanelVScrollBar(new GuiTransform(GuiAlign.quickAnchor(GuiAlign.TOP_CENTER, GuiAlign.BOTTOM_CENTER), new GuiPadding(-16, 0, 8, 18), 0));
      this.cvInner.addPanel(this.paDescScroll);
      this.cvDesc.setScrollDriverY(this.paDescScroll);
      this.paDescScroll.setEnabled(this.cvDesc.getScrollBounds().getHeight() > 0);
      if (this.quest.getRewards().size() <= 0) {
         this.updateButtons();
      } else {
         for(int index = 0; index < this.quest.getRewards().size(); ++index) {
            IReward rew = (IReward)((DBEntry)this.quest.getRewards().getEntries().get(index)).getValue();
            PanelTextBox rwdTitle = (new PanelTextBox(new GuiRectangle(0, panelPrevY + panelPrevHeight, this.cvDesc.getTransform().getWidth(), 16, 0), QuestTranslation.translate(rew.getUnlocalisedName(), new Object[0]))).setColor(PresetColor.TEXT_HEADER.getColor()).setAlignment(1);
            IGuiPanel pnReward = rew.getRewardGui(new GuiRectangle(0, panelPrevY + panelPrevHeight + 16, this.cvDesc.getTransform().getWidth(), 32, 0), new DBEntry(this.questID, this.quest));
            this.cvDesc.addCulledPanel(rwdTitle, false);
            this.cvDesc.addCulledPanel(pnReward, false);
            int height = this.rectReward.getHeight() - 32;
            CanvasEmpty pnRewardCanvas = (CanvasEmpty)pnReward;

            for(IGuiPanel pnl : pnRewardCanvas.getChildren()) {
               if (pnl.getClass().isAssignableFrom(CanvasScrolling.class)) {
                  CanvasScrolling pnlScrolling = (CanvasScrolling)pnl;
                  height = pnlScrolling.getScrollBounds().getHeight() + 32;
               }
            }

            IGuiPanel pnRewardReplacement = rew.getRewardGui(new GuiRectangle(0, panelPrevY + panelPrevHeight + 16, this.rectReward.getWidth(), height, 0), new DBEntry(this.questID, this.quest));
            this.cvDesc.removePanel(pnReward);
            this.cvDesc.addCulledPanel(pnRewardReplacement, false);
            CanvasEmpty pnRewardReplacementCanvas = (CanvasEmpty)pnRewardReplacement;

            for(IGuiPanel pnlRepl : pnRewardReplacementCanvas.getChildren()) {
               if (pnlRepl.getClass().isAssignableFrom(PanelVScrollBar.class)) {
                  pnRewardReplacementCanvas.removePanel(pnlRepl);
               }
            }

            panelPrevY = panelPrevY + panelPrevHeight + 16;
            panelPrevHeight = height + 8;
         }

         this.paDescScroll.setEnabled(this.cvDesc.getScrollBounds().getHeight() > 0);
         this.updateButtons();
      }
   }

   private void refreshTaskPanel() {
      if (this.tskScroll != null) {
         this.cvInner.removePanel(this.tskScroll);
         this.cvInner.removePanel(this.tskScrollBar);
      }

      if (this.quest.getTasks().size() <= 0) {
         this.updateButtons();
      } else {
         this.tskScroll = new CanvasScrolling(this.rectTask);
         this.cvInner.addPanel(this.tskScroll);
         this.tskScrollBar = new PanelVScrollBar(new GuiRectangle(this.cvInner.getTransform().getWidth() - 8, 0, 8, this.rectTask.getHeight(), 0));
         this.cvInner.addPanel(this.tskScrollBar);
         this.tskScroll.setScrollDriverY(this.tskScrollBar);
         int panelPrevY = 0;
         int panelPrevHeight = 0;

         for(int index = 0; index < this.quest.getTasks().size(); ++index) {
            ITask tsk = (ITask)((DBEntry)this.quest.getTasks().getEntries().get(index)).getValue();
            PanelTextBox tskTitle = (new PanelTextBox(new GuiRectangle(0, panelPrevY + panelPrevHeight, this.rectTask.getWidth(), 16, 0), QuestTranslation.translate(tsk.getUnlocalisedName(), new Object[0]))).setColor(PresetColor.TEXT_HEADER.getColor()).setAlignment(1);
            IGuiPanel pnTask = tsk.getTaskGui(new GuiRectangle(0, panelPrevY + panelPrevHeight + 16, this.rectTask.getWidth(), 32, 0), new DBEntry(this.questID, this.quest));
            this.tskScroll.addCulledPanel(tskTitle, false);
            this.tskScroll.addCulledPanel(pnTask, false);
            int height = this.rectTask.getHeight() - 16;
            if (pnTask instanceof CanvasEmpty) {
               CanvasEmpty pnTaskCanvas = (CanvasEmpty)pnTask;

               for(IGuiPanel pnl : pnTaskCanvas.getChildren()) {
                  if (pnl.getClass().isAssignableFrom(CanvasScrolling.class)) {
                     CanvasScrolling pnlScrolling = (CanvasScrolling)pnl;
                     height = pnlScrolling.getScrollBounds().getHeight() + 32;
                  }
               }
            }

            IGuiPanel pnTaskReplacement = tsk.getTaskGui(new GuiRectangle(0, panelPrevY + panelPrevHeight + 16, this.rectTask.getWidth(), height, 0), new DBEntry(this.questID, this.quest));
            this.tskScroll.removePanel(pnTask);
            if (pnTaskReplacement.getClass().getName().contentEquals("bq_standard.client.gui.tasks.PanelTaskHunt") && Loader.isModLoaded("mobends") && ConfigHandler.client.bqMobendPatch) {
               pnTaskReplacement = MobendPatcher.patchPanel(pnTaskReplacement);
            }

            this.tskScroll.addCulledPanel(pnTaskReplacement, false);
            if (pnTaskReplacement instanceof CanvasEmpty) {
               CanvasEmpty pnTaskReplacementCanvas = (CanvasEmpty)pnTaskReplacement;

               for(IGuiPanel pnlRepl : pnTaskReplacementCanvas.getChildren()) {
                  if (pnlRepl.getClass().isAssignableFrom(PanelVScrollBar.class)) {
                     pnTaskReplacementCanvas.removePanel(pnlRepl);
                  }
               }
            }

            panelPrevY = panelPrevY + panelPrevHeight + 16;
            panelPrevHeight = height + 8;
         }

         this.tskScrollBar.setEnabled(this.tskScroll.getScrollBounds().getHeight() > 0);
         this.updateButtons();
      }
   }

   private void updateButtons() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.btnClaim != null) {
         this.btnClaim.setActive(this.quest.getRewards().size() > 0 && this.quest.canClaim(mc.player));
      }

      if (this.btnDetect != null) {
         this.btnDetect.setActive(this.quest.canSubmit(mc.player));
      }

   }
}
