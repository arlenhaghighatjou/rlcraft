package betterquesting.api2.supporter;

import betterquesting.api2.storage.INBTSaveLoad;
import java.util.HashMap;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

public class SupporterEntry implements INBTSaveLoad<NBTTagCompound> {
   private final HashMap<String, HashMap<String, Integer>> services = new HashMap();

   public SupporterEntry() {
   }

   @Nonnull
   public HashMap<String, Integer> getServices(@Nonnull String token) {
      return (HashMap)this.services.computeIfAbsent(token, (t) -> {
         return new HashMap();
      });
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
   }
}
