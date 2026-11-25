package betterquesting.client.gui2;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.misc.ICallback;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasHoverTray;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.colors.GuiColorPulse;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.GuiTextureColored;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.OreDictTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.GuiQuestLinesEditor;
import betterquesting.client.gui2.editors.designer.GuiDesigner;
import betterquesting.network.handlers.NetQuestAction;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Tuple;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiQuestLines extends GuiScreenCanvas implements IPEventListener, INeedsRefresh {
   private IQuestLine selectedLine = null;
   private static int selectedLineId = -1;
   private final List<Tuple<DBEntry<IQuestLine>, Integer>> visChapters = new ArrayList();
   private CanvasQuestLine cvQuest;
   private static CanvasHoverTray cvChapterTray;
   private static CanvasHoverTray cvDescTray;
   private static CanvasHoverTray cvFrame;
   private CanvasScrolling cvDesc;
   private PanelVScrollBar scDesc;
   private CanvasScrolling cvLines;
   private PanelVScrollBar scLines;
   private PanelGeneric icoChapter;
   private PanelTextBox txTitle;
   private PanelTextBox txDesc;
   private PanelButton claimAll;
   private static boolean trayLock = false;
   private final List<PanelButtonStorage<DBEntry<IQuestLine>>> btnListRef = new ArrayList();

   public GuiQuestLines(GuiScreen parent) {
      super(parent);
   }

   public void refreshGui() {
      this.refreshChapterVisibility();
      this.refreshContent();
   }

   public void initPanel() {
      super.initPanel();
      if (selectedLineId >= 0) {
         this.selectedLine = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(selectedLineId);
         if (this.selectedLine == null) {
            selectedLineId = -1;
         }
      } else {
         this.selectedLine = null;
      }

      boolean canEdit = ((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(this.mc.player);
      boolean preOpen = false;
      if (trayLock && cvChapterTray != null && cvChapterTray.isTrayOpen()) {
         preOpen = true;
      }

      if (trayLock && cvDescTray != null && cvDescTray.isTrayOpen()) {
         preOpen = true;
      }

      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      PanelButton btnExit = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 8, -24, 32, 16, 0), -1, "")).setIcon(PresetIcon.ICON_PG_PREV.getTexture());
      btnExit.setClickAction((b) -> {
         this.mc.displayGuiScreen(this.parent);
      });
      btnExit.setTooltip(Collections.singletonList(QuestTranslation.translate("gui.back")));
      cvBackground.addPanel(btnExit);
      if (canEdit) {
         PanelButton btnEdit = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 8, -40, 16, 16, 0), -1, "")).setIcon(PresetIcon.ICON_GEAR.getTexture());
         btnEdit.setClickAction((b) -> {
            this.mc.displayGuiScreen(new GuiQuestLinesEditor(this));
         });
         btnEdit.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.btn.edit")));
         cvBackground.addPanel(btnEdit);
         PanelButton btnDesign = (new PanelButton(new GuiTransform(GuiAlign.BOTTOM_LEFT, 24, -40, 16, 16, 0), -1, "")).setIcon(PresetIcon.ICON_SORT.getTexture());
         btnDesign.setClickAction((b) -> {
            if (this.selectedLine != null) {
               this.mc.displayGuiScreen(new GuiDesigner(this, this.selectedLine));
            }

         });
         btnDesign.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.btn.designer")));
         cvBackground.addPanel(btnDesign);
      }

      this.txTitle = new PanelTextBox(new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 0.0F), new GuiPadding(60, 12, 0, -24), 0), "");
      this.txTitle.setColor(PresetColor.TEXT_HEADER.getColor());
      cvBackground.addPanel(this.txTitle);
      this.icoChapter = new PanelGeneric(new GuiTransform(GuiAlign.TOP_LEFT, 40, 8, 16, 16, 0), (IGuiTexture)null);
      cvBackground.addPanel(this.icoChapter);
      cvFrame = new CanvasHoverTray(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(214, 24, 8, 8), 0), new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(40, 24, 8, 8), 0), PresetTexture.AUX_FRAME_0.getTexture());
      cvFrame.setManualOpen(true);
      cvBackground.addPanel(cvFrame);
      cvFrame.setTrayState(!preOpen, 1L);
      boolean oldState1 = trayLock && cvChapterTray != null && cvChapterTray.isTrayOpen();
      cvChapterTray = new CanvasHoverTray(new GuiTransform(GuiAlign.LEFT_EDGE, new GuiPadding(40, 24, -24, 8), -1), new GuiTransform(GuiAlign.LEFT_EDGE, new GuiPadding(40, 24, -214, 8), -1), PresetTexture.PANEL_INNER.getTexture());
      cvChapterTray.setManualOpen(true);
      cvChapterTray.setOpenAction(() -> {
         cvDescTray.setTrayState(false, 200L);
         cvFrame.setTrayState(false, 200L);
         this.buildChapterList();
      });
      cvBackground.addPanel(cvChapterTray);
      this.cvLines = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(8, 8, 16, 8), 0));
      cvChapterTray.getCanvasOpen().addPanel(this.cvLines);
      this.scLines = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-16, 8, 8, 8), 0));
      this.cvLines.setScrollDriverY(this.scLines);
      cvChapterTray.getCanvasOpen().addPanel(this.scLines);
      boolean oldState2 = trayLock && cvDescTray != null && cvDescTray.isTrayOpen();
      cvDescTray = new CanvasHoverTray(new GuiTransform(GuiAlign.LEFT_EDGE, new GuiPadding(40, 24, -24, 8), -1), new GuiTransform(GuiAlign.LEFT_EDGE, new GuiPadding(40, 24, -214, 8), -1), PresetTexture.PANEL_INNER.getTexture());
      cvDescTray.setManualOpen(true);
      cvDescTray.setOpenAction(() -> {
         cvChapterTray.setTrayState(false, 200L);
         cvFrame.setTrayState(false, 200L);
         this.cvDesc.resetCanvas();
         if (this.selectedLine != null) {
            this.txDesc = new PanelTextBox(new GuiRectangle(0, 0, this.cvDesc.getTransform().getWidth(), 0, 0), QuestTranslation.translate(this.selectedLine.getUnlocalisedDescription()), true);
            this.txDesc.setColor(PresetColor.TEXT_AUX_0.getColor());
            this.cvDesc.addCulledPanel(this.txDesc, false);
            this.cvDesc.refreshScrollBounds();
            this.scDesc.setEnabled(this.cvDesc.getScrollBounds().getHeight() > 0);
         } else {
            this.scDesc.setEnabled(false);
         }

      });
      cvBackground.addPanel(cvDescTray);
      this.cvDesc = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(8, 8, 20, 8), 0));
      cvDescTray.getCanvasOpen().addPanel(this.cvDesc);
      this.scDesc = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-16, 8, 8, 8), 0));
      this.cvDesc.setScrollDriverY(this.scDesc);
      cvDescTray.getCanvasOpen().addPanel(this.scDesc);
      PanelButton btnTrayToggle = new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 8, 24, 32, 16, 0), -1, "");
      btnTrayToggle.setIcon(PresetIcon.ICON_BOOKMARK.getTexture(), (IGuiColor)(selectedLineId < 0 ? new GuiColorPulse(-1, -12303292, 2.0D, 0.0F) : new GuiColorStatic(-1)), 0);
      btnTrayToggle.setClickAction((b) -> {
         cvFrame.setTrayState(cvChapterTray.isTrayOpen(), 200L);
         cvChapterTray.setTrayState(!cvChapterTray.isTrayOpen(), 200L);
         btnTrayToggle.setIcon(PresetIcon.ICON_BOOKMARK.getTexture());
      });
      btnTrayToggle.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.title.quest_lines")));
      cvBackground.addPanel(btnTrayToggle);
      PanelButton btnDescToggle = (new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 8, 40, 32, 16, 0), -1, "")).setIcon(PresetIcon.ICON_DESC.getTexture());
      btnDescToggle.setClickAction((b) -> {
         cvFrame.setTrayState(cvDescTray.isTrayOpen(), 200L);
         cvDescTray.setTrayState(!cvDescTray.isTrayOpen(), 200L);
      });
      btnDescToggle.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.gui.description")));
      cvBackground.addPanel(btnDescToggle);
      PanelButton fitView = new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 8, 72, 32, 16, -2), 5, "");
      fitView.setIcon(PresetIcon.ICON_BOX_FIT.getTexture());
      fitView.setClickAction((b) -> {
         if (this.cvQuest.getQuestLine() != null) {
            this.cvQuest.fitToWindow();
         }

      });
      fitView.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.btn.zoom_fit")));
      cvBackground.addPanel(fitView);
      this.claimAll = new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 8, 56, 32, 16, -2), -1, "");
      this.claimAll.setIcon(PresetIcon.ICON_CHEST_ALL.getTexture());
      this.claimAll.setClickAction((b) -> {
         if (this.cvQuest.getQuestButtons().size() > 0) {
            List<Integer> claimIdList = new ArrayList();

            for(PanelButtonQuest pbQuest : this.cvQuest.getQuestButtons()) {
               IQuest q = (IQuest)((DBEntry)pbQuest.getStoredValue()).getValue();
               if (q.getRewards().size() > 0 && q.canClaim(this.mc.player)) {
                  claimIdList.add(Integer.valueOf(((DBEntry)pbQuest.getStoredValue()).getID()));
               }
            }

            int[] cIDs = new int[claimIdList.size()];

            for(int i = 0; i < cIDs.length; ++i) {
               cIDs[i] = ((Integer)claimIdList.get(i)).intValue();
            }

            NetQuestAction.requestClaim(cIDs);
            this.claimAll.setIcon(PresetIcon.ICON_CHEST_ALL.getTexture(), new GuiColorStatic(-12303292), 0);
         }
      });
      this.claimAll.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.btn.claim_all")));
      cvBackground.addPanel(this.claimAll);
      PanelButton btnTrayLock = (new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 8, 88, 32, 16, -2), -1, "")).setIcon(trayLock ? PresetIcon.ICON_LOCKED.getTexture() : PresetIcon.ICON_UNLOCKED.getTexture());
      btnTrayLock.setClickAction((b) -> {
         trayLock = !trayLock;
         b.setIcon(trayLock ? PresetIcon.ICON_LOCKED.getTexture() : PresetIcon.ICON_UNLOCKED.getTexture());
      });
      btnTrayLock.setTooltip(Collections.singletonList(QuestTranslation.translate("betterquesting.btn.lock_tray")));
      cvBackground.addPanel(btnTrayLock);
      CanvasQuestLine oldCvQuest = this.cvQuest;
      this.cvQuest = new CanvasQuestLine(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), 2);
      cvFrame.addPanel(this.cvQuest);
      if (this.selectedLine != null) {
         this.cvQuest.setQuestLine(this.selectedLine);
         if (oldCvQuest != null) {
            this.cvQuest.setZoom(oldCvQuest.getZoom());
            this.cvQuest.setScrollX(oldCvQuest.getScrollX());
            this.cvQuest.setScrollY(oldCvQuest.getScrollY());
            this.cvQuest.refreshScrollBounds();
            this.cvQuest.updatePanelScroll();
         }

         this.txTitle.setText(QuestTranslation.translate(this.selectedLine.getUnlocalisedName()));
         this.icoChapter.setTexture(new OreDictTexture(1.0F, (BigItemStack)this.selectedLine.getProperty(NativeProps.ICON), false, true), (IGuiColor)null);
      }

      cvChapterTray.setTrayState(oldState1, 1L);
      cvDescTray.setTrayState(oldState2, 1L);
      this.refreshChapterVisibility();
      this.refreshClaimAll();
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      Minecraft mc = Minecraft.getMinecraft();
      IPanelButton btn = event.getButton();
      if (btn.getButtonID() == 2 && btn instanceof PanelButtonStorage) {
         DBEntry<IQuest> quest = (DBEntry)((PanelButtonStorage)btn).getStoredValue();
         GuiHome.bookmark = new GuiQuest(this, quest.getID());
         mc.displayGuiScreen(GuiHome.bookmark);
      }

   }

   private void refreshChapterVisibility() {
      boolean canEdit = ((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(this.mc.player);
      List<DBEntry<IQuestLine>> lineList = QuestLineDatabase.INSTANCE.getSortedEntries();
      this.visChapters.clear();
      EaglercraftUUID playerID = QuestingAPI.getQuestingUUID(this.mc.player);

      for(DBEntry<IQuestLine> dbEntry : lineList) {
         IQuestLine ql = dbEntry.getValue();
         EnumQuestVisibility vis = (EnumQuestVisibility)ql.getProperty(NativeProps.VISIBILITY);
         if (canEdit || vis != EnumQuestVisibility.HIDDEN) {
            boolean show = false;
            boolean unlocked = false;
            boolean complete = false;
            boolean allComplete = true;
            boolean pendingClaim = false;
            if (canEdit) {
               show = true;
               unlocked = true;
               complete = true;
            }

            for(DBEntry<IQuestLineEntry> qID : ql.getEntries()) {
               IQuest q = (IQuest)QuestDatabase.INSTANCE.getValue(qID.getID());
               if (q != null) {
                  if (allComplete && !q.isComplete(playerID)) {
                     allComplete = false;
                  }

                  if (!pendingClaim && q.isComplete(playerID) && !q.hasClaimed(playerID)) {
                     pendingClaim = true;
                  }

                  if (!unlocked && q.isUnlocked(playerID)) {
                     unlocked = true;
                  }

                  if (!complete && q.isComplete(playerID)) {
                     complete = true;
                  }

                  if (!show && QuestCache.isQuestShown(q, playerID, this.mc.player)) {
                     show = true;
                  }

                  if (unlocked && complete && show && pendingClaim && !allComplete) {
                     break;
                  }
               }
            }

            if ((vis != EnumQuestVisibility.COMPLETED || complete) && (vis != EnumQuestVisibility.UNLOCKED || unlocked)) {
               int val = pendingClaim ? 1 : 0;
               if (allComplete) {
                  val |= 2;
               }

               if (!show) {
                  val |= 4;
               }

               this.visChapters.add(new Tuple(dbEntry, val));
            }
         }
      }

      if (cvChapterTray.isTrayOpen()) {
         this.buildChapterList();
      }

   }

   private void buildChapterList() {
      this.cvLines.resetCanvas();
      this.btnListRef.clear();
      int listW = this.cvLines.getTransform().getWidth();

      for(int n = 0; n < this.visChapters.size(); ++n) {
         DBEntry<IQuestLine> entry = (DBEntry)((Tuple)this.visChapters.get(n)).getFirst();
         int vis = ((Integer)((Tuple)this.visChapters.get(n)).getSecond()).intValue();
         this.cvLines.addPanel(new PanelGeneric(new GuiRectangle(0, n * 16, 16, 16, 0), new OreDictTexture(1.0F, (BigItemStack)((IQuestLine)entry.getValue()).getProperty(NativeProps.ICON), false, true)));
         if ((vis & 1) > 0) {
            this.cvLines.addPanel(new PanelGeneric(new GuiRectangle(8, n * 16 + 8, 8, 8, -1), new GuiTextureColored(PresetIcon.ICON_NOTICE.getTexture(), new GuiColorStatic(-256))));
         } else if ((vis & 2) > 0) {
            this.cvLines.addPanel(new PanelGeneric(new GuiRectangle(8, n * 16 + 8, 8, 8, -1), new GuiTextureColored(PresetIcon.ICON_TICK.getTexture(), new GuiColorStatic(-16711936))));
         }

         PanelButtonStorage<DBEntry<IQuestLine>> btnLine = new PanelButtonStorage<DBEntry<IQuestLine>>(new GuiRectangle(16, n * 16, listW - 16, 16, 0), 1, QuestTranslation.translate(((IQuestLine)entry.getValue()).getUnlocalisedName()), entry);
         btnLine.setTextAlignment(0);
         btnLine.setActive((vis & 4) == 0 && entry.getID() != selectedLineId);
         btnLine.setCallback((q) -> {
            this.btnListRef.forEach((b) -> {
               if (((DBEntry)b.getStoredValue()).getID() == selectedLineId) {
                  b.setActive(true);
               }

            });
            btnLine.setActive(false);
            this.selectedLine = (IQuestLine)q.getValue();
            selectedLineId = q.getID();
            this.cvQuest.setQuestLine((IQuestLine)q.getValue());
            this.icoChapter.setTexture(new OreDictTexture(1.0F, (BigItemStack)((IQuestLine)q.getValue()).getProperty(NativeProps.ICON), false, true), (IGuiColor)null);
            this.txTitle.setText(QuestTranslation.translate(((IQuestLine)q.getValue()).getUnlocalisedName()));
            if (!trayLock) {
               cvFrame.setTrayState(true, 200L);
               cvChapterTray.setTrayState(false, 200L);
               this.cvQuest.fitToWindow();
            }

            this.refreshClaimAll();
         });
         this.cvLines.addPanel(btnLine);
         this.btnListRef.add(btnLine);
      }

      this.cvLines.refreshScrollBounds();
      this.scLines.setEnabled(this.cvLines.getScrollBounds().getHeight() > 0);
   }

   private void refreshContent() {
      if (selectedLineId >= 0) {
         this.selectedLine = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(selectedLineId);
         if (this.selectedLine == null) {
            selectedLineId = -1;
         }
      } else {
         this.selectedLine = null;
      }

      float zoom = this.cvQuest.getZoom();
      int sx = this.cvQuest.getScrollX();
      int sy = this.cvQuest.getScrollY();
      this.cvQuest.setQuestLine(this.selectedLine);
      this.cvQuest.setZoom(zoom);
      this.cvQuest.setScrollX(sx);
      this.cvQuest.setScrollY(sy);
      this.cvQuest.refreshScrollBounds();
      this.cvQuest.updatePanelScroll();
      if (this.selectedLine != null) {
         this.txTitle.setText(QuestTranslation.translate(this.selectedLine.getUnlocalisedName()));
         this.icoChapter.setTexture(new OreDictTexture(1.0F, (BigItemStack)this.selectedLine.getProperty(NativeProps.ICON), false, true), (IGuiColor)null);
      } else {
         this.txTitle.setText("");
         this.icoChapter.setTexture((IGuiTexture)null, (IGuiColor)null);
      }

      this.refreshClaimAll();
   }

   private void refreshClaimAll() {
      if (this.cvQuest.getQuestLine() != null && this.cvQuest.getQuestButtons().size() > 0) {
         for(PanelButtonQuest btn : this.cvQuest.getQuestButtons()) {
            if (((IQuest)((DBEntry)btn.getStoredValue()).getValue()).canClaim(this.mc.player)) {
               this.claimAll.setActive(true);
               this.claimAll.setIcon(PresetIcon.ICON_CHEST_ALL.getTexture(), new GuiColorPulse(-1, -12303292, 2.0D, 0.0F), 0);
               return;
            }
         }

         this.claimAll.setIcon(PresetIcon.ICON_CHEST_ALL.getTexture(), new GuiColorStatic(-12303292), 0);
         this.claimAll.setActive(false);
      } else {
         this.claimAll.setActive(false);
         this.claimAll.setIcon(PresetIcon.ICON_CHEST_ALL.getTexture(), new GuiColorStatic(-12303292), 0);
      }
   }
}
