package bq_standard.importers.ftbq.converters.tasks;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import bq_standard.core.BQ_Standard;
import bq_standard.importers.ftbq.FTBQQuestImporter;
import bq_standard.importers.ftbq.FTBQUtils;
import bq_standard.tasks.TaskRetrieval;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class FtbqTaskItem {
   public FtbqTaskItem() {
   }

   public ITask[] convertTask(NBTTagCompound nbt) {
      TaskRetrieval task = new TaskRetrieval();
      task.consume = nbt.getBoolean("consume_items");
      long count = !nbt.hasKey("count") ? 1L : nbt.getLong("count");
      if (!nbt.hasKey("item", 8) && !nbt.hasKey("item", 10)) {
         if (nbt.hasKey("items", 9)) {
            NBTTagList tagList = nbt.getTagList("items", 10);

            for(int i = 0; i < tagList.tagCount(); ++i) {
               NBTTagCompound tagItemBase = tagList.getCompoundTagAt(i);
               long rem = count;
               BigItemStack item;
               if (tagItemBase.hasKey("item", 8)) {
                  item = FTBQUtils.convertItem(tagItemBase.getTag("item"));
               } else {
                  item = FTBQUtils.convertItem(tagItemBase);
               }

               while(rem > 0L) {
                  int split = (int)(rem % 2147483647L);
                  item.stackSize = split;
                  task.requiredItems.add(item.copy());
                  rem -= (long)split;
               }

               FTBQQuestImporter.provideQuestIcon(item);
            }
         } else {
            BQ_Standard.logger.error("Unable read item tag!");
         }
      } else {
         BigItemStack item = FTBQUtils.convertItem(nbt.getTag("item"));

         int split;
         for(long rem = count; rem > 0L; rem -= (long)split) {
            split = (int)(rem % 2147483647L);
            item.stackSize = split;
            task.requiredItems.add(item.copy());
         }

         FTBQQuestImporter.provideQuestIcon(item);
      }

      return new ITask[]{task};
   }
}
