package bq_standard.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import bq_standard.client.gui.rewards.PanelRewardRecipe;
import bq_standard.rewards.factory.FactoryRewardRecipe;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RewardRecipe implements IReward {
   public String recipeNames = "minecraft:crafting_table\nminecraft:chest";

   public RewardRecipe() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.reward.recipe";
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardRecipe.INSTANCE.getRegistryName();
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return true;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      String[] recSplit = this.recipeNames.split("\n");
      ResourceLocation[] loc = new ResourceLocation[recSplit.length];

      for(int i = 0; i < recSplit.length; ++i) {
         loc[i] = new ResourceLocation(recSplit[i]);
      }

      player.unlockRecipes(loc);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardRecipe(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getRewardEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("recipes", this.recipeNames);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.recipeNames = nbt.getString("recipes");
   }
}
