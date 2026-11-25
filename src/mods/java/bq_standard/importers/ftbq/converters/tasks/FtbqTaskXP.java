package bq_standard.importers.ftbq.converters.tasks;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import bq_standard.importers.ftbq.FTBQQuestImporter;
import bq_standard.tasks.TaskXP;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;

public class FtbqTaskXP {
   public FtbqTaskXP() {
   }

   public ITask[] convertTask(NBTTagCompound tag) {
      TaskXP task = new TaskXP();
      task.consume = true;
      task.amount = (int)(tag.getLong("value") % 2147483647L);
      FTBQQuestImporter.provideQuestIcon(new BigItemStack(Items.EXPERIENCE_BOTTLE));
      return new ITask[]{task};
   }
}
