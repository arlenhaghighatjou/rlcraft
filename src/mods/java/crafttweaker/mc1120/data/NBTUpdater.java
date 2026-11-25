package crafttweaker.mc1120.data;

import crafttweaker.api.data.IData;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUpdater extends NBTConverter {
   private final NBTTagCompound nbt;

   public NBTUpdater(NBTTagCompound compound) {
      this.nbt = compound;
   }

   public NBTBase fromMap(Map<String, IData> map) {
      for(Entry<String, IData> entry : map.entrySet()) {
         if (this.nbt.hasKey((String)entry.getKey())) {
            if (entry.getValue() == null) {
               this.nbt.removeTag((String)entry.getKey());
            } else if (this.nbt.getTag((String)entry.getKey()).getId() == 10) {
               this.nbt.setTag((String)entry.getKey(), (NBTBase)((IData)entry.getValue()).convert(new NBTUpdater(this.nbt.getCompoundTag((String)entry.getKey()))));
            } else {
               this.nbt.setTag((String)entry.getKey(), from((IData)entry.getValue()));
            }
         } else if (entry.getValue() != null) {
            this.nbt.setTag((String)entry.getKey(), from((IData)entry.getValue()));
         }
      }

      return this.nbt;
   }
}
