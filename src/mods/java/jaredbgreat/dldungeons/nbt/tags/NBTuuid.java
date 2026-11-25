package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

public class NBTuuid extends ITag {
   public final UUID data;

   NBTuuid(String label, String name, String data) {
      super(label, name);
      this.data = UUID.fromString(data);
   }

   NBTuuid(String label, String name, UUID data) {
      super(label, name);
      this.data = data;
   }

   public void write(NBTTagCompound cmp) {
      cmp.setUniqueId(this.name, this.data);
   }

   public void write(NBTTagList cmp) {
      cmp.appendTag(new NBTTagLong(this.data.getLeastSignificantBits()));
      cmp.appendTag(new NBTTagLong(this.data.getMostSignificantBits()));
   }

   public NBTType getType() {
      return NBTType.INT;
   }
}
