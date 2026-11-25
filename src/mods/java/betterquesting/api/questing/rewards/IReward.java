package betterquesting.api.questing.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.INBTSaveLoad;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IReward extends INBTSaveLoad<NBTTagCompound> {
   String getUnlocalisedName();

   ResourceLocation getFactoryID();

   boolean canClaim(EntityPlayer var1, DBEntry<IQuest> var2);

   void claimReward(EntityPlayer var1, DBEntry<IQuest> var2);

   @SideOnly(Side.CLIENT)
   IGuiPanel getRewardGui(IGuiRect var1, DBEntry<IQuest> var2);

   @Nullable
   @SideOnly(Side.CLIENT)
   GuiScreen getRewardEditor(GuiScreen var1, DBEntry<IQuest> var2);
}
