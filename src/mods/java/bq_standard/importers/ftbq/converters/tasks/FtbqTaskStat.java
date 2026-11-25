package bq_standard.importers.ftbq.converters.tasks;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import bq_standard.importers.ftbq.FTBQQuestImporter;
import bq_standard.tasks.TaskScoreboard;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;

public class FtbqTaskStat {
   public FtbqTaskStat() {
   }

   public ITask[] convertTask(NBTTagCompound tag) {
      TaskScoreboard task = new TaskScoreboard();
      task.scoreName = tag.getString("stat");
      task.scoreDisp = tag.hasKey("title", 8) ? tag.getString("title") : task.scoreName;
      task.operation = TaskScoreboard.ScoreOperation.MORE_OR_EQUAL;
      task.target = tag.getInteger("value");
      FTBQQuestImporter.provideQuestIcon(new BigItemStack(Items.PAPER));
      return new ITask[]{task};
   }
}
