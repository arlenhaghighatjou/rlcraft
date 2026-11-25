package betterquesting.client.gui2.inventory;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.IFluidTask;
import betterquesting.api.questing.tasks.IItemTask;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.GuiContainerCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.blocks.TileSubmitStation;
import betterquesting.network.handlers.NetStationEdit;
import betterquesting.questing.QuestDatabase;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector4f;

public class GuiSubmitStation extends GuiContainerCanvas implements INeedsRefresh {
   private final ContainerSubmitStation ssContainer;
   private final TileSubmitStation tile;
   private final NonNullList<DBEntry<IQuest>> quests = NonNullList.create();
   private final NonNullList<DBEntry<ITask>> tasks = NonNullList.create();
   private IGuiCanvas cvBackground;
   private IPanelButton btnSet;
   private IPanelButton btnRem;
   private IPanelButton btnQstLeft;
   private IPanelButton btnQstRight;
   private IPanelButton btnTskLeft;
   private IPanelButton btnTskRight;
   private PanelTextBox txtQstTitle;
   private PanelTextBox txtTskTitle;
   private IGuiPanel taskPanel;
   private int selQuest = 0;
   private int selTask = 0;

   public GuiSubmitStation(GuiScreen parent, InventoryPlayer playerInvo, TileSubmitStation submitStation) {
      super(parent, new ContainerSubmitStation(playerInvo, submitStation));
      this.ssContainer = (ContainerSubmitStation)this.inventorySlots;
      this.tile = submitStation;
   }

   public void refreshGui() {
      this.quests.clear();
      QuestCache qc = (QuestCache)this.mc.player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      if (qc != null) {
         this.quests.addAll(QuestDatabase.INSTANCE.bulkLookup(qc.getActiveQuests()));
      }

      this.filterQuests();
      this.refreshTaskPanel();
   }

   public void initPanel() {
      super.initPanel();
      Keyboard.enableRepeatEvents(true);
      this.quests.clear();
      this.taskPanel = null;
      QuestCache qc = (QuestCache)this.mc.player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      if (qc != null) {
         this.quests.addAll(QuestDatabase.INSTANCE.bulkLookup(qc.getActiveQuests()));
      }

      this.filterQuests();
      this.cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(this.cvBackground);
      PanelTextBox txtTitle = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.submit_station"))).setAlignment(1);
      txtTitle.setColor(PresetColor.TEXT_HEADER.getColor());
      this.cvBackground.addPanel(txtTitle);
      this.cvBackground.addPanel((new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), -1, QuestTranslation.translate("gui.done"))).setClickAction((b) -> {
         this.mc.displayGuiScreen(this.parent);
      }));
      this.btnQstLeft = new PanelButton(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 0.0F), 8, 32, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.selQuest--;
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnQstLeft).setIcon(PresetIcon.ICON_LEFT.getTexture());
      this.cvBackground.addPanel(this.btnQstLeft);
      this.btnQstRight = new PanelButton(new GuiTransform(new Vector4f(1.0F, 0.0F, 1.0F, 0.0F), -32, 32, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.selQuest++;
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnQstRight).setIcon(PresetIcon.ICON_RIGHT.getTexture());
      this.cvBackground.addPanel(this.btnQstRight);
      this.btnTskLeft = new PanelButton(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 0.0F), 8, 48, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.selTask--;
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnTskLeft).setIcon(PresetIcon.ICON_LEFT.getTexture());
      this.cvBackground.addPanel(this.btnTskLeft);
      this.btnTskRight = new PanelButton(new GuiTransform(new Vector4f(1.0F, 0.0F, 1.0F, 0.0F), -32, 48, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.selTask++;
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnTskRight).setIcon(PresetIcon.ICON_RIGHT.getTexture());
      this.cvBackground.addPanel(this.btnTskRight);
      this.btnSet = new PanelButton(new GuiTransform(new Vector4f(0.75F, 0.0F, 0.75F, 0.0F), -16, 64, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.tile.setupTask(QuestingAPI.getQuestingUUID(GuiSubmitStation.this.mc.player), (IQuest)((DBEntry)GuiSubmitStation.this.quests.get(GuiSubmitStation.this.selQuest)).getValue(), (ITask)((DBEntry)GuiSubmitStation.this.tasks.get(GuiSubmitStation.this.selTask)).getValue());
            NetStationEdit.setupStation(GuiSubmitStation.this.tile.getPos(), GuiSubmitStation.this.selQuest, GuiSubmitStation.this.selTask);
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnSet).setIcon(PresetIcon.ICON_TICK.getTexture(), new GuiColorStatic(-16711936), 0);
      this.cvBackground.addPanel(this.btnSet);
      this.btnRem = new PanelButton(new GuiTransform(new Vector4f(0.75F, 0.0F, 0.75F, 0.0F), 0, 64, 16, 16, 0), -1, "") {
         public void onButtonClick() {
            GuiSubmitStation.this.tile.reset();
            NetStationEdit.resetStation(GuiSubmitStation.this.tile.getPos());
            GuiSubmitStation.this.refreshTaskPanel();
         }
      };
      ((PanelButton)this.btnRem).setIcon(PresetIcon.ICON_CROSS.getTexture(), new GuiColorStatic(-65536), 0);
      this.cvBackground.addPanel(this.btnRem);
      this.txtQstTitle = new PanelTextBox(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(24, 36, 32, -48), 0), "");
      this.txtQstTitle.setColor(PresetColor.TEXT_MAIN.getColor()).setAlignment(1);
      this.cvBackground.addPanel(this.txtQstTitle);
      this.txtTskTitle = new PanelTextBox(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(24, 52, 32, -64), 0), "");
      this.txtTskTitle.setColor(PresetColor.TEXT_MAIN.getColor()).setAlignment(1);
      this.cvBackground.addPanel(this.txtTskTitle);
      this.setInventoryPosition((this.xSize - 64) / 4 + 16 - 81, (this.ySize - 64) / 2 + 16 - 49);
      this.refreshTaskPanel();
   }

   private void setInventoryPosition(int x, int y) {
      this.ssContainer.moveInventorySlots(x + 17, y + 39);
      this.ssContainer.moveSubmitSlot(x + 71, y + 17);
      this.ssContainer.moveReturnSlot(x + 107, y + 17);

      for(int i = 0; i < 36; ++i) {
         int j = i % 9 * 18;
         int k = i / 9 * 18;
         if (i >= 27) {
            k += 4;
         }

         this.addPanel(new PanelGeneric(new GuiTransform(GuiAlign.TOP_LEFT, x + j, y + k + 22, 18, 18, -1), PresetTexture.ITEM_FRAME.getTexture()));
      }

      this.addPanel(new PanelGeneric(new GuiTransform(GuiAlign.TOP_LEFT, x + 54, y, 18, 18, -1), PresetTexture.ITEM_FRAME.getTexture()));
      this.addPanel(new PanelGeneric(new GuiTransform(GuiAlign.TOP_LEFT, x + 72, y, 18, 18, -1), PresetIcon.ICON_RIGHT.getTexture()));
      this.addPanel(new PanelGeneric(new GuiTransform(GuiAlign.TOP_LEFT, x + 90, y, 18, 18, -1), PresetTexture.ITEM_FRAME.getTexture()));
   }

   private void filterQuests() {
      Iterator<DBEntry<IQuest>> iter = this.quests.iterator();

      while(iter.hasNext()) {
         DBEntry<IQuest> entry = (DBEntry)iter.next();
         boolean valid = false;

         for(DBEntry<ITask> tsk : ((IQuest)entry.getValue()).getTasks().getEntries()) {
            if (tsk.getValue() instanceof IItemTask || tsk.getValue() instanceof IFluidTask) {
               valid = true;
               break;
            }
         }

         if (!valid) {
            iter.remove();
         }
      }

   }

   private void refreshTaskPanel() {
      if (this.taskPanel != null) {
         this.cvBackground.removePanel(this.taskPanel);
      }

      if (this.tile.isSetup()) {
         DBEntry<IQuest> qdbe = null;

         for(int i = 0; i < this.quests.size(); ++i) {
            DBEntry<IQuest> entry = (DBEntry)this.quests.get(i);
            if (entry.getID() == this.tile.questID) {
               this.selQuest = i;
               qdbe = entry;
               break;
            }
         }

         if (qdbe != null) {
            List<DBEntry<ITask>> tmpTasks = ((IQuest)qdbe.getValue()).getTasks().getEntries();

            for(int i = 0; i < tmpTasks.size(); ++i) {
               if (((DBEntry)tmpTasks.get(i)).getID() == this.tile.taskID) {
                  this.selTask = i;
                  break;
               }
            }
         }

         this.btnRem.setActive(true);
         this.btnSet.setActive(false);
         this.btnQstLeft.setActive(false);
         this.btnQstRight.setActive(false);
         this.btnTskLeft.setActive(false);
         this.btnTskRight.setActive(false);
      } else {
         this.btnRem.setActive(false);
         this.btnSet.setActive(false);
         this.btnQstLeft.setActive(true);
         this.btnQstRight.setActive(true);
         this.btnTskLeft.setActive(true);
         this.btnTskRight.setActive(true);
      }

      if (this.quests.size() <= 0) {
         this.selQuest = -1;
         this.selTask = -1;
         this.txtQstTitle.setText("");
         this.txtTskTitle.setText("");
      } else {
         this.selQuest = this.lazyPosMod(this.selQuest, this.quests.size());
         DBEntry<IQuest> entry = (DBEntry)this.quests.get(this.selQuest);
         this.txtQstTitle.setText(QuestTranslation.translate((String)((IQuest)entry.getValue()).getProperty(NativeProps.NAME)));
         this.tasks.clear();
         this.tasks.addAll(((IQuest)entry.getValue()).getTasks().getEntries());
         if (this.tasks.size() <= 0) {
            this.selTask = -1;
            this.txtTskTitle.setText("");
         } else {
            this.selTask = this.lazyPosMod(this.selTask, this.tasks.size());
            DBEntry<ITask> curTask = (DBEntry)this.tasks.get(this.selTask);
            this.txtTskTitle.setText(QuestTranslation.translate(((ITask)curTask.getValue()).getUnlocalisedName()));
            this.btnSet.setActive(!this.tile.isSetup() && (curTask.getValue() instanceof IItemTask || curTask.getValue() instanceof IFluidTask));
            this.taskPanel = (curTask.getValue()).getTaskGui(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 88, 16, 24), 0), entry);
            if (this.taskPanel != null) {
               this.cvBackground.addPanel(this.taskPanel);
            }

         }
      }
   }

   private int lazyPosMod(int a, int b) {
      return (a % b + b) % b;
   }
}
