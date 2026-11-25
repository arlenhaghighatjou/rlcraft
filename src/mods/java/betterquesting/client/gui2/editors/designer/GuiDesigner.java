package betterquesting.client.gui2.editors.designer;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.client.toolbox.IToolTab;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.toolbox.ToolboxRegistry;
import betterquesting.questing.QuestLineDatabase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector4f;

public class GuiDesigner extends GuiScreenCanvas implements IVolatileScreen, INeedsRefresh, IPEventListener {
   private IQuestLine questLine;
   private final int lineID;
   private PanelToolController toolController;
   private IGuiCanvas cvTray;
   private final List<IToolTab> tabList = new ArrayList();
   private PanelTextBox tabTitle;
   private IGuiPanel lastTabPanel;
   private int tabIdx = 0;
   private CanvasQuestLine cvQuest;

   public GuiDesigner(GuiScreen parent, IQuestLine line) {
      super(parent);
      this.questLine = line;
      this.lineID = QuestLineDatabase.INSTANCE.getID(line);
      this.tabList.addAll(ToolboxRegistry.INSTANCE.getAllTabs());
   }

   public void refreshGui() {
      this.questLine = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(this.lineID);
      if (this.questLine == null) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         int sx = this.cvQuest.getScrollX();
         int sy = this.cvQuest.getScrollY();
         float z = this.cvQuest.getZoom();
         this.cvQuest.setQuestLine(this.questLine);
         this.cvQuest.setZoom(z);
         this.cvQuest.setScrollX(sx);
         this.cvQuest.setScrollY(sy);
         this.toolController.refreshCanvas();
      }
   }

   public void initPanel() {
      super.initPanel();
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 96, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      this.cvTray = new CanvasTextured(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-96, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(this.cvTray);
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.done")));
      PanelGeneric pnFrame = new PanelGeneric(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 16, 16, 16), 0), PresetTexture.AUX_FRAME_0.getTexture());
      cvBackground.addPanel(pnFrame);
      this.cvQuest = new CanvasQuestLine(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 16, 16, 16), 0), 1);
      cvBackground.addPanel(this.cvQuest);
      this.cvQuest.setQuestLine(this.questLine);
      PanelButton btnTabLeft = new PanelButton(new GuiTransform(new Vector4f(0.0F, 0.0F, 0.5F, 0.0F), new GuiPadding(16, 32, 0, -40), 0), 2, "") {
         public void onButtonClick() {
            GuiDesigner.this.tabIdx--;
            GuiDesigner.this.refreshToolTab();
         }
      };
      btnTabLeft.setIcon(PresetIcon.ICON_LEFT.getTexture());
      this.cvTray.addPanel(btnTabLeft);
      PanelButton btnTabRight = new PanelButton(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(0, 32, 16, -40), 0), 3, "") {
         public void onButtonClick() {
            GuiDesigner.this.tabIdx++;
            GuiDesigner.this.refreshToolTab();
         }
      };
      btnTabRight.setIcon(PresetIcon.ICON_RIGHT.getTexture());
      this.cvTray.addPanel(btnTabRight);
      this.tabTitle = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 20, 16, -32), 0), "?")).setAlignment(1).setColor(PresetColor.TEXT_HEADER.getColor());
      this.cvTray.addPanel(this.tabTitle);
      if (this.toolController != null) {
         cvBackground.addPanel(this.toolController);
         this.cvQuest.setScrollDriverX(this.toolController.getScrollX());
         this.cvQuest.setScrollDriverY(this.toolController.getScrollY());
         this.toolController.changeCanvas(this.cvQuest);
      } else {
         this.toolController = new PanelToolController(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 16, 16, 16), -1), this.cvQuest);
         cvBackground.addPanel(this.toolController);
         this.cvQuest.setScrollDriverX(this.toolController.getScrollX());
         this.cvQuest.setScrollDriverY(this.toolController.getScrollY());
      }

      this.refreshToolTab();
   }

   private void refreshToolTab() {
      if (this.lastTabPanel != null) {
         this.cvTray.removePanel(this.lastTabPanel);
      }

      if (this.tabList.size() > 0) {
         if (this.tabIdx < 0) {
            while(this.tabIdx < 0) {
               this.tabIdx += this.tabList.size();
            }
         }

         this.tabIdx %= this.tabList.size();
         this.lastTabPanel = ((IToolTab)this.tabList.get(this.tabIdx)).getTabGui(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(16, 48, 16, 16), 0), this.cvQuest, this.toolController);
         this.tabTitle.setText(QuestTranslation.translate(((IToolTab)this.tabList.get(this.tabIdx)).getUnlocalisedName()));
         if (this.lastTabPanel != null) {
            this.cvTray.addPanel(this.lastTabPanel);
         }

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
      }

   }
}
