package bq_standard;

import betterquesting.api2.storage.INBTPartial;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ScoreBQ implements INBTPartial<NBTTagList, UUID> {
   private final HashMap<UUID, Integer> playerScores = new HashMap();

   public ScoreBQ() {
   }

   public int getScore(@Nonnull UUID uuid) {
      Integer value = (Integer)this.playerScores.get(uuid);
      return value == null ? 0 : value.intValue();
   }

   public void setScore(@Nonnull UUID uuid, int value) {
      this.playerScores.put(uuid, Integer.valueOf(value));
   }

   public boolean hasEntry(@Nonnull UUID uuid) {
      return this.playerScores.containsKey(uuid);
   }

   public NBTTagList writeToNBT(NBTTagList nbt, @Nullable List<UUID> subset) {
      for(Entry<UUID, Integer> entry : this.playerScores.entrySet()) {
         if (subset == null || subset.contains(entry.getKey())) {
            NBTTagCompound jObj = new NBTTagCompound();
            jObj.setString("uuid", ((UUID)entry.getKey()).toString());
            jObj.setInteger("value", ((Integer)entry.getValue()).intValue());
            nbt.appendTag(jObj);
         }
      }

      return nbt;
   }

   public void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.playerScores.clear();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         try {
            NBTTagCompound jObj = nbt.getCompoundTagAt(i);
            this.playerScores.put(UUID.fromString(jObj.getString("uuid")), Integer.valueOf(jObj.getInteger("value")));
         } catch (Exception var5) {
            ;
         }
      }

   }
}
