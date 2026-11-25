package bq_standard.rewards;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import bq_standard.NBTReplaceUtil;
import bq_standard.client.gui.rewards.PanelRewardItem;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.factory.FactoryRewardItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

public class RewardItem implements IReward {
   public final List<BigItemStack> items = new ArrayList();

   public RewardItem() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardItem.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.reward.item";
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return true;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      for(BigItemStack r : this.items) {
         BigItemStack stack = r.copy();

         for(ItemStack s : stack.getCombinedStacks()) {
            if (s.getTagCompound() != null) {
               s.setTagCompound((NBTTagCompound)NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_NAME", player.getName()));
               s.setTagCompound((NBTTagCompound)NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_UUID", QuestingAPI.getQuestingUUID(player).toString()));
            }

            if (!player.inventory.addItemStackToInventory(s)) {
               player.dropItem(s, true, false);
            }
         }
      }

   }

   public void readFromNBT(NBTTagCompound json) {
      this.items.clear();
      NBTTagList rList = json.getTagList("rewards", 10);

      for(int i = 0; i < rList.tagCount(); ++i) {
         try {
            BigItemStack item = JsonHelper.JsonToItemStack(rList.getCompoundTagAt(i));
            if (item != null) {
               this.items.add(item);
            }
         } catch (Exception var5) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load reward item data", var5);
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      NBTTagList rJson = new NBTTagList();

      for(BigItemStack stack : this.items) {
         rJson.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
      }

      json.setTag("rewards", rJson);
      return json;
   }

   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardItem(rect, this);
   }

   public GuiScreen getRewardEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }
}
