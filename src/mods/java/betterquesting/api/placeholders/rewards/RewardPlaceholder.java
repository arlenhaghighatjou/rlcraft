package betterquesting.api.placeholders.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class RewardPlaceholder implements IReward {
   private NBTTagCompound nbtSaved = new NBTTagCompound();

   public RewardPlaceholder() {
   }

   public void setRewardConfigData(NBTTagCompound nbt) {
      this.nbtSaved = nbt;
   }

   public NBTTagCompound getRewardConfigData() {
      return this.nbtSaved;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("orig_data", this.nbtSaved);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.nbtSaved = nbt.getCompoundTag("orig_data");
   }

   public String getUnlocalisedName() {
      return "betterquesting.placeholder";
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardPlaceholder.INSTANCE.getRegistryName();
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return false;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
   }

   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return null;
   }

   public GuiScreen getRewardEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
