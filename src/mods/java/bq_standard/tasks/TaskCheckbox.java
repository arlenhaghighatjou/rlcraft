package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskCheckbox;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskCheckbox;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskCheckbox implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();

   public TaskCheckbox() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskCheckbox.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.checkbox";
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable UUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
      } else {
         this.completeUsers.remove(uuid);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      this.completeUsers.forEach((uuid) -> {
         if (users == null || users.contains(uuid)) {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         }

      });
      nbt.setTag("completeUsers", jArray);
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load UUID for task", var6);
         }
      }

   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskCheckbox(rect, quest, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
