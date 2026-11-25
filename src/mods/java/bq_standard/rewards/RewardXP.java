package bq_standard.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import bq_standard.XPHelper;
import bq_standard.client.gui.rewards.PanelRewardXP;
import bq_standard.rewards.factory.FactoryRewardXP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RewardXP implements IReward {
   public int amount = 1;
   public boolean levels = true;

   public RewardXP() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardXP.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.reward.xp";
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return true;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      XPHelper.addXP(player, !this.levels ? (long)this.amount : XPHelper.getLevelXP(this.amount));
   }

   public void readFromNBT(NBTTagCompound json) {
      this.amount = json.getInteger("amount");
      this.levels = json.getBoolean("isLevels");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setInteger("amount", this.amount);
      json.setBoolean("isLevels", this.levels);
      return json;
   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardXP(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getRewardEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }
}
