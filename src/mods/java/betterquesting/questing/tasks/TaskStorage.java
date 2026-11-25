package betterquesting.questing.tasks;

import betterquesting.api.placeholders.tasks.FactoryTaskPlaceholder;
import betterquesting.api.placeholders.tasks.TaskPlaceholder;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabaseNBT;
import betterquesting.api2.storage.SimpleDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class TaskStorage extends SimpleDatabase<ITask> implements IDatabaseNBT<ITask, NBTTagList, NBTTagList> {
   public TaskStorage() {
   }

   public NBTTagList writeToNBT(NBTTagList json, @Nullable List<Integer> subset) {
      for(DBEntry<ITask> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            ResourceLocation taskID = ((ITask)entry.getValue()).getFactoryID();
            NBTTagCompound qJson = (NBTTagCompound)((ITask)entry.getValue()).writeToNBT(new NBTTagCompound());
            qJson.setString("taskID", taskID.toString());
            qJson.setInteger("index", entry.getID());
            json.appendTag(qJson);
         }
      }

      return json;
   }

   public void readFromNBT(NBTTagList json, boolean merge) {
      if (!merge) {
         this.reset();
      }

      List<ITask> unassigned = new ArrayList();

      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound jsonTask = json.getCompoundTagAt(i);
         ResourceLocation loc = new ResourceLocation(jsonTask.getString("taskID"));
         int index = jsonTask.hasKey("index", 99) ? jsonTask.getInteger("index") : -1;
         ITask task = TaskRegistry.INSTANCE.createNew(loc);
         if (task instanceof TaskPlaceholder) {
            NBTTagCompound jt2 = jsonTask.getCompoundTag("orig_data");
            ResourceLocation loc2 = new ResourceLocation(jt2.getString("taskID"));
            ITask t2 = TaskRegistry.INSTANCE.createNew(loc2);
            if (t2 != null) {
               jsonTask = jt2;
               task = t2;
            }
         }

         if (task != null) {
            task.readFromNBT(jsonTask);
         } else {
            task = new TaskPlaceholder();
            ((TaskPlaceholder)task).setTaskConfigData(jsonTask);
         }

         if (index >= 0) {
            this.add(index, task);
         } else {
            unassigned.add(task);
         }
      }

      for(ITask t : unassigned) {
         this.add(this.nextID(), t);
      }

   }

   public NBTTagList writeProgressToNBT(NBTTagList json, @Nullable List<UUID> user) {
      for(DBEntry<ITask> entry : this.getEntries()) {
         ResourceLocation taskID = ((ITask)entry.getValue()).getFactoryID();
         NBTTagCompound qJson = (NBTTagCompound)((ITask)entry.getValue()).writeProgressToNBT(new NBTTagCompound(), user);
         qJson.setString("taskID", taskID.toString());
         qJson.setInteger("index", entry.getID());
         json.appendTag(qJson);
      }

      return json;
   }

   public void readProgressFromNBT(NBTTagList json, boolean merge) {
      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound jsonTask = json.getCompoundTagAt(i);
         int index = jsonTask.hasKey("index", 99) ? jsonTask.getInteger("index") : -1;
         ResourceLocation loc = new ResourceLocation(jsonTask.getString("taskID"));
         ITask task = (ITask)this.getValue(index);
         if (task instanceof TaskPlaceholder) {
            if (!task.getFactoryID().equals(loc)) {
               ((TaskPlaceholder)task).setTaskProgressData(jsonTask);
            } else {
               task.readProgressFromNBT(jsonTask, merge);
            }
         } else if (task != null) {
            if (task.getFactoryID().equals(loc)) {
               task.readProgressFromNBT(jsonTask, merge);
            } else if (FactoryTaskPlaceholder.INSTANCE.getRegistryName().equals(loc)) {
               task.readProgressFromNBT(jsonTask.getCompoundTag("orig_prog"), merge);
            }
         }
      }

   }
}
