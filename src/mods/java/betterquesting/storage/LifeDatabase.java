package betterquesting.storage;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.storage.ILifeDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public final class LifeDatabase implements ILifeDatabase {
   public static final LifeDatabase INSTANCE = new LifeDatabase();
   private final HashMap<UUID, Integer> playerLives = new HashMap();

   public LifeDatabase() {
   }

   public synchronized int getLives(@Nonnull UUID uuid) {
      return ((Integer)this.playerLives.computeIfAbsent(uuid, (k) -> {
         return (Integer)QuestSettings.INSTANCE.getProperty(NativeProps.LIVES_DEF);
      })).intValue();
   }

   public synchronized void setLives(@Nonnull UUID uuid, int value) {
      this.playerLives.put(uuid, Integer.valueOf(MathHelper.clamp(value, 0, ((Integer)QuestSettings.INSTANCE.getProperty(NativeProps.LIVES_MAX)).intValue())));
   }

   public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jul = new NBTTagList();

      for(Entry<UUID, Integer> entry : this.playerLives.entrySet()) {
         if (users == null || users.contains(entry.getKey())) {
            NBTTagCompound j = new NBTTagCompound();
            j.setString("uuid", ((UUID)entry.getKey()).toString());
            j.setInteger("lives", ((Integer)entry.getValue()).intValue());
            jul.appendTag(j);
         }
      }

      nbt.setTag("playerLives", jul);
      return nbt;
   }

   public synchronized void readFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.playerLives.clear();
      }

      NBTTagList tagList = nbt.getTagList("playerLives", 10);

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound j = tagList.getCompoundTagAt(i);

         try {
            UUID uuid = UUID.fromString(j.getString("uuid"));
            int lives = j.getInteger("lives");
            this.playerLives.put(uuid, Integer.valueOf(lives));
         } catch (Exception var8) {
            ;
         }
      }

   }

   public synchronized void reset() {
      this.playerLives.clear();
   }
}
