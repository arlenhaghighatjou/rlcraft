package bq_standard;

import betterquesting.api2.storage.INBTPartial;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ScoreBQ implements INBTPartial<NBTTagList, EaglercraftUUID> {
   private final HashMap<EaglercraftUUID, Integer> playerScores = new HashMap();

   public ScoreBQ() {
   }

   public int getScore(@Nonnull EaglercraftUUID uuid) {
      Integer value = (Integer)this.playerScores.get(uuid);
      return value == null ? 0 : value.intValue();
   }

   public void setScore(@Nonnull EaglercraftUUID uuid, int value) {
      this.playerScores.put(uuid, Integer.valueOf(value));
   }

   public boolean hasEntry(@Nonnull EaglercraftUUID uuid) {
      return this.playerScores.containsKey(uuid);
   }

   public NBTTagList writeToNBT(NBTTagList nbt, @Nullable List<EaglercraftUUID> subset) {
      for(Entry<EaglercraftUUID, Integer> entry : this.playerScores.entrySet()) {
         if (subset == null || subset.contains(entry.getKey())) {
            NBTTagCompound jObj = new NBTTagCompound();
            jObj.setString("uuid", ((EaglercraftUUID)entry.getKey()).toString());
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
            this.playerScores.put(EaglercraftUUID.fromString(jObj.getString("uuid")), Integer.valueOf(jObj.getInteger("value")));
         } catch (Exception var5) {
            ;
         }
      }

   }
}
