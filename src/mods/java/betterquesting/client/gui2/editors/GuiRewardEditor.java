package betterquesting.client.gui2.editors;

import betterquesting.api.client.gui.misc.INeedsRefresh;
import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
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
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.panels.lists.CanvasSearch;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.registry.IFactoryData;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.nbt.GuiNbtEditor;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.rewards.RewardRegistry;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiRewardEditor extends GuiScreenCanvas implements IPEventListener, IVolatileScreen, INeedsRefresh {
   private CanvasScrolling qrList;
   private IQuest quest;
   private final int qID;

   public GuiRewardEditor(GuiScreen parent, IQuest quest) {
      super(parent);
      this.quest = quest;
      this.qID = QuestDatabase.INSTANCE.getID(quest);
   }

   public void refreshGui() {
      this.quest = (IQuest)QuestDatabase.INSTANCE.getValue(this.qID);
      if (this.quest == null) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         this.refreshRewards();
      }
   }

   public void initPanel() {
      super.initPanel();
      if (this.qID < 0) {
         this.mc.displayGuiScreen(this.parent);
      } else {
         PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
         CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
         this.addPanel(cvBackground);
         PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.edit_rewards"))).setAlignment(1);
         panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
         cvBackground.addPanel(panTxt);
         cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 200, 16, 0), 0, QuestTranslation.translate("gui.back")));
         CanvasSearch<IFactoryData<IReward, NBTTagCompound>, IFactoryData<IReward, NBTTagCompound>> cvRegSearch = new CanvasSearch<IFactoryData<IReward, NBTTagCompound>, IFactoryData<IReward, NBTTagCompound>>(new GuiTransform(GuiAlign.HALF_RIGHT, new GuiPadding(8, 48, 24, 32), 0)) {
            protected Iterator<IFactoryData<IReward, NBTTagCompound>> getIterator() {
               List<IFactoryData<IReward, NBTTagCompound>> list = RewardRegistry.INSTANCE.getAll();
               list.sort(Comparator.comparing((o) -> {
                  return o.getRegistryName().toString().toLowerCase();
               }));
               return list.iterator();
            }

            protected void queryMatches(IFactoryData<IReward, NBTTagCompound> value, String query, ArrayDeque<IFactoryData<IReward, NBTTagCompound>> results) {
               if (value.getRegistryName().toString().toLowerCase().contains(query.toLowerCase())) {
                  results.add(value);
               }

            }

            protected boolean addResult(IFactoryData<IReward, NBTTagCompound> entry, int index, int cachedWidth) {
               this.addPanel(new PanelButtonStorage(new GuiRectangle(0, index * 16, cachedWidth, 16, 0), 1, entry.getRegistryName().toString(), entry));
               return true;
            }
         };
         cvBackground.addPanel(cvRegSearch);
         PanelVScrollBar scReg = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-24, 48, 16, 32), 0));
         cvBackground.addPanel(scReg);
         cvRegSearch.setScrollDriverY(scReg);
         PanelTextField<String> tfSearch = new PanelTextField<String>(new GuiTransform(new Vector4f(0.5F, 0.0F, 1.0F, 0.0F), new GuiPadding(8, 32, 16, -48), 0), "", FieldFilterString.INSTANCE);
         tfSearch.setCallback(cvRegSearch::setSearchFilter);
         tfSearch.setWatermark("Search...");
         cvBackground.addPanel(tfSearch);
         this.qrList = new CanvasScrolling(new GuiTransform(GuiAlign.HALF_LEFT, new GuiPadding(16, 32, 16, 32), 0));
         cvBackground.addPanel(this.qrList);
         PanelVScrollBar scRew = new PanelVScrollBar(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 1.0F), new GuiPadding(-16, 32, 8, 32), 0));
         cvBackground.addPanel(scRew);
         this.qrList.setScrollDriverY(scRew);
         IGuiRect ls0 = new GuiTransform(GuiAlign.TOP_CENTER, 0, 32, 0, 0, 0);
         ls0.setParent(cvBackground.getTransform());
         IGuiRect le0 = new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -32, 0, 0, 0);
         le0.setParent(cvBackground.getTransform());
         PanelLine paLine0 = new PanelLine(ls0, le0, PresetLine.GUI_DIVIDER.getLine(), 1, PresetColor.GUI_DIVIDER.getColor(), 1);
         cvBackground.addPanel(paLine0);
         this.refreshRewards();
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
      } else if (btn.getButtonID() == 1 && btn instanceof PanelButtonStorage) {
         IFactoryData<IReward, NBTTagCompound> fact = (IFactoryData)((PanelButtonStorage)btn).getStoredValue();
         this.quest.getRewards().add(this.quest.getRewards().nextID(), fact.createNew());
         this.SendChanges();
      } else if (btn.getButtonID() == 2 && btn instanceof PanelButtonStorage) {
         IReward reward = (IReward)((PanelButtonStorage)btn).getStoredValue();
         if (this.quest.getRewards().removeValue(reward)) {
            this.SendChanges();
         }
      } else if (btn.getButtonID() == 3 && btn instanceof PanelButtonStorage) {
         IReward reward = (IReward)((PanelButtonStorage)btn).getStoredValue();
         GuiScreen editor = reward.getRewardEditor(this, new DBEntry(this.qID, this.quest));
         if (editor != null) {
            this.mc.displayGuiScreen(editor);
         } else {
            this.mc.displayGuiScreen(new GuiNbtEditor(this, (NBTTagCompound)reward.writeToNBT(new NBTTagCompound()), (value) -> {
               reward.readFromNBT(value);
               this.SendChanges();
            }));
         }
      }

   }

   private void refreshRewards() {
      List<DBEntry<IReward>> dbRew = this.quest.getRewards().getEntries();
      this.qrList.resetCanvas();
      int w = this.qrList.getTransform().getWidth();

      for(int i = 0; i < dbRew.size(); ++i) {
         IReward reward = (IReward)((DBEntry)dbRew.get(i)).getValue();
         this.qrList.addPanel(new PanelButtonStorage(new GuiRectangle(0, i * 16, w - 16, 16, 0), 3, QuestTranslation.translate(reward.getUnlocalisedName()), reward));
         this.qrList.addPanel(new PanelButtonStorage(new GuiRectangle(w - 16, i * 16, 16, 16, 0), 2, "" + TextFormatting.RED + TextFormatting.BOLD + "x", reward));
      }

   }

   private void SendChanges() {
      NBTTagCompound payload = new NBTTagCompound();
      NBTTagList dataList = new NBTTagList();
      NBTTagCompound entry = new NBTTagCompound();
      entry.setInteger("questID", this.qID);
      entry.setTag("config", this.quest.writeToNBT(new NBTTagCompound()));
      dataList.appendTag(entry);
      payload.setTag("data", dataList);
      payload.setInteger("action", 0);
      NetQuestEdit.sendEdit(payload);
   }
}
