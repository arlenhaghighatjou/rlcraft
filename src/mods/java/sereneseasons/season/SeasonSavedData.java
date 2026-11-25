package sereneseasons.season;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class SeasonSavedData extends WorldSavedData {
   public static final String DATA_IDENTIFIER = "seasons";
   public int seasonCycleTicks;

   public SeasonSavedData() {
      this("seasons");
   }

   public SeasonSavedData(String identifier) {
      super(identifier);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.seasonCycleTicks = nbt.getInteger("SeasonCycleTicks");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("SeasonCycleTicks", this.seasonCycleTicks);
      return nbt;
   }
}
