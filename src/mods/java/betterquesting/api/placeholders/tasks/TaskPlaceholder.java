package betterquesting.api.placeholders.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TaskPlaceholder implements ITask {
   private NBTTagCompound nbtData = new NBTTagCompound();

   public TaskPlaceholder() {
   }

   public void setTaskConfigData(NBTTagCompound nbt) {
      this.nbtData.setTag("orig_data", nbt);
   }

   public void setTaskProgressData(NBTTagCompound nbt) {
      this.nbtData.setTag("orig_prog", nbt);
   }

   public NBTTagCompound getTaskConfigData() {
      return this.nbtData.getCompoundTag("orig_data");
   }

   public NBTTagCompound getTaskProgressData() {
      return this.nbtData.getCompoundTag("orig_prog");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("orig_data", this.nbtData.getCompoundTag("orig_data"));
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.nbtData.setTag("orig_data", nbt.getCompoundTag("orig_data"));
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
      nbt.setTag("orig_prog", this.nbtData.getCompoundTag("orig_prog"));
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      this.nbtData.setTag("orig_prog", nbt.getCompoundTag("orig_prog"));
   }

   public String getUnlocalisedName() {
      return "betterquesting.placeholder";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskPlaceholder.INSTANCE.getRegistryName();
   }

   public void detect(ParticipantInfo participant, DBEntry<IQuest> quest) {
   }

   public boolean isComplete(EaglercraftUUID uuid) {
      return false;
   }

   public void setComplete(EaglercraftUUID uuid) {
   }

   public void resetUser(EaglercraftUUID uuid) {
   }

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return null;
   }

   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
