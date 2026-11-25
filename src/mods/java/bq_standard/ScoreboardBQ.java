package bq_standard;

import betterquesting.api2.storage.INBTPartial;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ScoreboardBQ implements INBTPartial<NBTTagList, UUID> {
   public static final ScoreboardBQ INSTANCE = new ScoreboardBQ();
   private final HashMap<String, ScoreBQ> objectives = new HashMap();

   public ScoreboardBQ() {
   }

   public synchronized int getScore(@Nonnull UUID uuid, @Nonnull String scoreName) {
      ScoreBQ score = (ScoreBQ)this.objectives.get(scoreName);
      return score == null ? 0 : score.getScore(uuid);
   }

   public synchronized void setScore(@Nonnull UUID uuid, @Nonnull String scoreName, int value) {
      ScoreBQ score = (ScoreBQ)this.objectives.computeIfAbsent(scoreName, (key) -> {
         return new ScoreBQ();
      });
      score.setScore(uuid, value);
   }

   public synchronized void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.objectives.clear();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound jObj = nbt.getCompoundTagAt(i);
         ScoreBQ score = (ScoreBQ)this.objectives.computeIfAbsent(jObj.getString("name"), (key) -> {
            return new ScoreBQ();
         });
         score.readFromNBT(jObj.getTagList("scores", 10), merge);
      }

   }

   public synchronized NBTTagList writeToNBT(NBTTagList nbt, @Nullable List<UUID> users) {
      for(Entry<String, ScoreBQ> entry : this.objectives.entrySet()) {
         NBTTagCompound jObj = new NBTTagCompound();
         jObj.setString("name", (String)entry.getKey());
         jObj.setTag("scores", ((ScoreBQ)entry.getValue()).writeToNBT(new NBTTagList(), users));
         nbt.appendTag(jObj);
      }

      return nbt;
   }
}
