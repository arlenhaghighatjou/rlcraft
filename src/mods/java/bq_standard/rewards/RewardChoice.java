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
import bq_standard.client.gui.rewards.PanelRewardChoice;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.factory.FactoryRewardChoice;
import java.util.ArrayList;
import java.util.HashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class RewardChoice implements IReward {
   public final ArrayList<BigItemStack> choices = new ArrayList();
   private final HashMap<EaglercraftUUID, Integer> selected = new HashMap();

   public RewardChoice() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardChoice.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.reward.choice";
   }

   public int getSelecton(EaglercraftUUID uuid) {
      return !this.selected.containsKey(uuid) ? -1 : ((Integer)this.selected.get(uuid)).intValue();
   }

   public void setSelection(EaglercraftUUID uuid, int value) {
      this.selected.put(uuid, Integer.valueOf(value));
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      if (!this.selected.containsKey(QuestingAPI.getQuestingUUID(player))) {
         return false;
      } else {
         int tmp = ((Integer)this.selected.get(QuestingAPI.getQuestingUUID(player))).intValue();
         return this.choices.size() <= 0 || tmp >= 0 && tmp < this.choices.size();
      }
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      EaglercraftUUID playerID = QuestingAPI.getQuestingUUID(player);
      if (this.choices.size() > 0) {
         if (this.selected.containsKey(playerID)) {
            int tmp = ((Integer)this.selected.get(playerID)).intValue();
            if (tmp >= 0 && tmp < this.choices.size()) {
               BigItemStack stack = (BigItemStack)this.choices.get(tmp);
               stack = stack == null ? null : stack.copy();
               if (stack != null && stack.stackSize > 0) {
                  for(ItemStack s : stack.getCombinedStacks()) {
                     if (s.getTagCompound() != null) {
                        s.setTagCompound((NBTTagCompound)NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_NAME", player.getName()));
                        s.setTagCompound((NBTTagCompound)NBTReplaceUtil.replaceStrings(s.getTagCompound(), "VAR_UUID", QuestingAPI.getQuestingUUID(player).toString()));
                     }

                     if (!player.inventory.addItemStackToInventory(s)) {
                        player.dropItem(s, true, false);
                     }
                  }

               } else {
                  BQ_Standard.logger.log(Level.WARN, "Claimed reward choice was null or was 0 in size!");
               }
            } else {
               BQ_Standard.logger.log(Level.ERROR, "Choice reward was forcibly claimed with invalid choice", new IllegalStateException());
            }
         }
      }
   }

   public void readFromNBT(NBTTagCompound json) {
      this.choices.clear();
      NBTTagList cList = json.getTagList("choices", 10);

      for(int i = 0; i < cList.tagCount(); ++i) {
         this.choices.add(JsonHelper.JsonToItemStack(cList.getCompoundTagAt(i)));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      NBTTagList rJson = new NBTTagList();

      for(BigItemStack stack : this.choices) {
         rJson.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
      }

      json.setTag("choices", rJson);
      return json;
   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardChoice(rect, quest, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getRewardEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }
}
