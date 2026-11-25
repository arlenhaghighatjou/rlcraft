package betterquesting.client.gui2.editors;

import betterquesting.api.misc.ICallback;
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
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasFileDirectory;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiFileBrowser extends GuiScreenCanvas implements IPEventListener {
   private final ICallback<File[]> callback;
   private final FileFilter filter;
   private final List<File> selList = new ArrayList();
   private PanelTextBox txtTitle;
   private File curDirectory;
   private CanvasScrolling cvSelected;
   private CanvasFileDirectory cvDirectory;
   private boolean multiSelect = true;

   public GuiFileBrowser(GuiScreen parent, ICallback<File[]> callback, File directory, @Nullable FileFilter filter) {
      super(parent);
      this.callback = callback;
      this.curDirectory = directory == null ? null : directory.getAbsoluteFile();
      this.filter = filter;
   }

   public GuiFileBrowser allowMultiSelect(boolean enable) {
      this.multiSelect = enable;
      return this;
   }

   public void initPanel() {
      super.initPanel();
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      this.txtTitle = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), this.curDirectory == null ? "*" : this.curDirectory.getAbsolutePath())).setAlignment(1);
      this.txtTitle.setColor(PresetColor.TEXT_HEADER.getColor());
      cvBackground.addPanel(this.txtTitle);
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.done")));
      CanvasEmpty cvLeft = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 8, 24), 0));
      cvBackground.addPanel(cvLeft);
      PanelTextBox txtQuest = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), QuestTranslation.translate("betterquesting.gui.selection"))).setAlignment(1);
      txtQuest.setColor(PresetColor.TEXT_HEADER.getColor());
      cvLeft.addPanel(txtQuest);
      this.cvSelected = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 8, 0), 0));
      cvLeft.addPanel(this.cvSelected);
      PanelVScrollBar scReq = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 16, 0, 0), 0));
      cvLeft.addPanel(scReq);
      this.cvSelected.setScrollDriverY(scReq);
      CanvasEmpty cvRight = new CanvasEmpty(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 32, 16, 24), 0));
      cvBackground.addPanel(cvRight);
      PanelTextBox txtDb = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), QuestTranslation.translate("betterquesting.gui.folder"))).setAlignment(1);
      txtDb.setColor(PresetColor.TEXT_HEADER.getColor());
      cvRight.addPanel(txtDb);
      PanelTextField<String> searchBox = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(16, 16, 24, -32), 0), "", FieldFilterString.INSTANCE);
      searchBox.setWatermark("Search...");
      cvRight.addPanel(searchBox);
      this.cvDirectory = new CanvasFileDirectory(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 32, 8, 0), 0), this.curDirectory, this.filter) {
         protected boolean addResult(File entry, int index, int width) {
            if (!entry.isDirectory()) {
               PanelButtonStorage<File> btnAdd = new PanelButtonStorage<File>(new GuiRectangle(0, index * 16, 16, 16, 0), -1, "", entry);
               btnAdd.setIcon(PresetIcon.ICON_POSITIVE.getTexture());
               btnAdd.setActive(!GuiFileBrowser.this.selList.contains(entry));
               btnAdd.setCallback((value) -> {
                  if (!GuiFileBrowser.this.multiSelect) {
                     GuiFileBrowser.this.selList.clear();
                  }

                  GuiFileBrowser.this.selList.add(value);
                  GuiFileBrowser.this.refreshSelected();
                  this.refreshSearch();
               });
               this.addPanel(btnAdd);
            } else {
               PanelGeneric pnDummy = new PanelGeneric(new GuiRectangle(0, index * 16, 16, 16, 0), (IGuiTexture)null);
               this.addPanel(pnDummy);
            }

            PanelButtonStorage<File> btnEdit = new PanelButtonStorage<File>(new GuiRectangle(16, index * 16, width - 32, 16, 0), -1, GuiFileBrowser.this.curDirectory == null ? entry.getAbsolutePath() : entry.getName(), entry);
            btnEdit.setActive(entry.isDirectory());
            btnEdit.setCallback((value) -> {
               GuiFileBrowser.this.curDirectory = value;
               this.setCurDirectory(GuiFileBrowser.this.curDirectory);
               GuiFileBrowser.this.txtTitle.setText(GuiFileBrowser.this.curDirectory == null ? "*" : GuiFileBrowser.this.curDirectory.getAbsolutePath());
            });
            this.addPanel(btnEdit);
            PanelGeneric pnIco = new PanelGeneric(new GuiRectangle(width - 16, index * 16, 16, 16, 0), entry.isDirectory() ? PresetIcon.ICON_FOLDER_OPEN.getTexture() : PresetIcon.ICON_FILE.getTexture());
            this.addPanel(pnIco);
            return true;
         }
      };
      cvRight.addPanel(this.cvDirectory);
      PanelButton selAll = new PanelButton(new GuiTransform(GuiAlign.TOP_RIGHT, -24, 16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            if (GuiFileBrowser.this.multiSelect) {
               boolean changed = false;

               for(File file : GuiFileBrowser.this.cvDirectory.getResults()) {
                  if (!file.isDirectory() && !GuiFileBrowser.this.selList.contains(file)) {
                     GuiFileBrowser.this.selList.add(file);
                     changed = true;
                  }
               }

               if (changed) {
                  GuiFileBrowser.this.cvDirectory.refreshSearch();
                  GuiFileBrowser.this.refreshSelected();
               }

            }
         }
      };
      selAll.setActive(this.multiSelect);
      selAll.setTooltip(Collections.singletonList("Select All"));
      selAll.setIcon(PresetIcon.ICON_SELECTION.getTexture());
      cvRight.addPanel(selAll);
      CanvasFileDirectory var10001 = this.cvDirectory;
      this.cvDirectory.getClass();
      searchBox.setCallback(var10001::setSearchFilter);
      PanelVScrollBar scDb = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 32, 0, 0), 0));
      cvRight.addPanel(scDb);
      this.cvDirectory.setScrollDriverY(scDb);
      PanelButton btnNew = new PanelButton(new GuiTransform(GuiAlign.TOP_LEFT, 0, 16, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            if (GuiFileBrowser.this.curDirectory != null) {
               GuiFileBrowser.this.curDirectory = GuiFileBrowser.this.curDirectory.getParentFile();
               GuiFileBrowser.this.cvDirectory.setCurDirectory(GuiFileBrowser.this.curDirectory);
               GuiFileBrowser.this.txtTitle.setText(GuiFileBrowser.this.curDirectory == null ? "*" : GuiFileBrowser.this.curDirectory.getAbsolutePath());
            }
         }
      };
      btnNew.setIcon(PresetIcon.ICON_DIR_UP.getTexture());
      cvRight.addPanel(btnNew);
      IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
      ls0.setParent(cvBackground.getTransform());
      IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -24, 0, 0, 0);
      le0.setParent(cvBackground.getTransform());
      PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
      cvBackground.addPanel(paLine0);
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      IPanelButton btn = event.getButton();
      if (btn.getButtonID() == 0) {
         if (this.callback != null) {
            this.callback.setValue(this.selList.toArray(new File[0]));
         }

         this.mc.displayGuiScreen(this.parent);
      }

   }

   private void refreshSelected() {
      this.cvSelected.resetCanvas();
      int width = this.cvSelected.getTransform().getWidth();

      for(int i = 0; i < this.selList.size(); ++i) {
         File f = (File)this.selList.get(i);
         PanelButton btnSel = new PanelButton(new GuiRectangle(0, i * 16, width - 16, 16, 0), -1, f.getName());
         btnSel.setActive(false);
         this.cvSelected.addPanel(btnSel);
         PanelButtonStorage<File> btnFile = new PanelButtonStorage<File>(new GuiRectangle(width - 16, i * 16, 16, 16, 0), -1, "", f);
         btnFile.setIcon(PresetIcon.ICON_NEGATIVE.getTexture());
         btnFile.setCallback((value) -> {
            this.selList.remove(value);
            this.refreshSelected();
            this.cvDirectory.refreshSearch();
         });
         this.cvSelected.addPanel(btnFile);
      }

   }
}
