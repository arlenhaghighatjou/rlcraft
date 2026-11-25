package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTCompound extends ITag {
   public final List<ITag> data = new ArrayList();

   NBTCompound(String label, String name, String data) {
      super(label, name);
      this.parseData(data);
   }

   public void write(NBTTagCompound in) {
      NBTTagCompound sub = in.getCompoundTag(this.name);
      in.setTag(this.name, sub);

      for(ITag child : this.data) {
         child.write(sub);
      }

   }

   public void write(NBTTagList in) {
      NBTTagCompound sub = new NBTTagCompound();

      for(ITag child : this.data) {
         child.write(sub);
      }

      in.appendTag(sub);
   }

   private void parseData(String in) {
      Tokenizer tokens = new Tokenizer(in, ",");

      while(tokens.hasMoreTokens()) {
         this.data.add(Tags.registry.get(tokens.nextToken()));
      }

   }

   public NBTType getType() {
      return NBTType.COMPOUND;
   }
}
