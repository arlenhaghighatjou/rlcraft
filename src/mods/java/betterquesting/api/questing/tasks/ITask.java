package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.INBTProgress;
import betterquesting.api2.storage.INBTSaveLoad;
import betterquesting.api2.utils.ParticipantInfo;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITask extends INBTSaveLoad<NBTTagCompound>, INBTProgress<NBTTagCompound> {
   String getUnlocalisedName();

   ResourceLocation getFactoryID();

   void detect(ParticipantInfo var1, DBEntry<IQuest> var2);

   boolean isComplete(EaglercraftUUID var1);

   void setComplete(EaglercraftUUID var1);

   void resetUser(@Nullable EaglercraftUUID var1);

   @Nullable
   @SideOnly(Side.CLIENT)
   IGuiPanel getTaskGui(IGuiRect var1, DBEntry<IQuest> var2);

   @Nullable
   @SideOnly(Side.CLIENT)
   GuiScreen getTaskEditor(GuiScreen var1, DBEntry<IQuest> var2);
}
