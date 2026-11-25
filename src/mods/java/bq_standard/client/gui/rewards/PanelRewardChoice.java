package bq_standard.client.gui.rewards;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.misc.ICallback;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.storage.DBEntry;
import bq_standard.network.handlers.NetRewardChoice;
import bq_standard.rewards.RewardChoice;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class PanelRewardChoice extends CanvasEmpty {
   private final DBEntry<IQuest> quest;
   private final RewardChoice reward;

   public PanelRewardChoice(IGuiRect rect, DBEntry<IQuest> quest, RewardChoice reward) {
      super(rect);
      this.quest = quest;
      this.reward = reward;
   }

   public void initPanel() {
      super.initPanel();
      CanvasScrolling cvList = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(40, 0, 8, 0), 0));
      this.addPanel(cvList);
      PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 0), 0));
      this.addPanel(scList);
      cvList.setScrollDriverY(scList);
      EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
      int sel = this.reward.getSelecton(uuid);
      PanelItemSlot slot = new PanelItemSlot(new GuiTransform(new Vector4f(0.0F, 0.5F, 0.0F, 0.5F), 0, -16, 32, 32, 0), -1, sel < 0 ? null : (BigItemStack)this.reward.choices.get(sel));
      this.addPanel(slot);
      int qID = this.quest.getID();
      int rID = ((IQuest)this.quest.getValue()).getRewards().getID(this.reward);
      int listWidth = cvList.getTransform().getWidth();

      for(int i = 0; i < this.reward.choices.size(); ++i) {
         BigItemStack stack = (BigItemStack)this.reward.choices.get(i);
         PanelItemSlot is = new PanelItemSlot(new GuiRectangle(0, i * 18, 18, 18, 0), -1, stack, true);
         cvList.addPanel(is);
         cvList.addPanel((new PanelTextBox(new GuiRectangle(22, i * 18 + 4, listWidth - 22, 14, 0), stack.stackSize + " " + stack.getBaseStack().getDisplayName())).setColor(PresetColor.TEXT_MAIN.getColor()));
         is.setCallback((value) -> {
            NetRewardChoice.requestChoice(qID, rID, sID);
         });
      }

   }
}
