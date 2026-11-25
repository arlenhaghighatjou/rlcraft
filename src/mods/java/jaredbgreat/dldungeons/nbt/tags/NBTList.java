package jaredbgreat.dldungeons.nbt.tags;

import jaredbgreat.dldungeons.nbt.NBTType;
import jaredbgreat.dldungeons.util.parser.Tokenizer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTList extends ITag {
   private final List<ITag> data = new ArrayList();
   private final int type;

   NBTList(String label, String name, String data) {
      super(label, name);
      this.parseData(data);
      this.type = ((ITag)this.data.get(0)).getType().ordinal();
   }

   public void write(NBTTagCompound in) {
      if (!in.hasKey(this.name, this.type)) {
         in.setTag(this.name, new NBTTagList());
      }

      for(ITag child : this.data) {
         NBTTagList list = in.getTagList(this.name, child.getType().ordinal());
         child.write(list);
      }

   }

   private void writeHelper(NBTTagCompound in, ITag child) {
   }

   public void write(NBTTagList in) {
      for(ITag child : this.data) {
         child.write(in);
      }

   }

   private void parseData(String in) {
      Tokenizer tokens = new Tokenizer(in, ",");

      while(tokens.hasMoreTokens()) {
         this.data.add(Tags.registry.get(tokens.nextToken()));
      }

   }

   public NBTType getType() {
      return NBTType.LIST;
   }
}
