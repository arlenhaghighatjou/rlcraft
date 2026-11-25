package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTGroup extends ITag {
   public final List<ITag> data = new ArrayList();

   NBTGroup(String label, String name, String data) {
      super(label, name);
      this.parseData(data);
   }

   public void write(NBTTagCompound cmp) {
      for(ITag child : this.data) {
         child.write(cmp);
      }

   }

   public void write(NBTTagList cmp) {
      for(ITag child : this.data) {
         child.write(cmp);
      }

   }

   private void parseData(String in) {
      Tokenizer tokens = new Tokenizer(in, ",");

      while(tokens.hasMoreTokens()) {
         this.data.add(Tags.registry.get(tokens.nextToken()));
      }

   }

   public NBTType getType() {
      return NBTType.GROUP;
   }
}
