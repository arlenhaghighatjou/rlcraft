package goblinbob.mobends.core.network;

import java.util.LinkedList;
import net.minecraft.nbt.NBTTagCompound;

public class SharedConfig {
   private LinkedList<SharedProperty<?>> properties = new LinkedList();

   public SharedConfig() {
   }

   public void addProperty(SharedProperty<?> property) {
      this.properties.add(property);
   }

   public Iterable<SharedProperty<?>> getProperties() {
      return this.properties;
   }

   public void writeToNBT(NBTTagCompound tag) {
      for(SharedProperty<?> property : this.properties) {
         property.writeToNBT(tag);
      }

   }

   public void readFromNBT(NBTTagCompound tag) {
      for(SharedProperty<?> property : this.properties) {
         property.readFromNBT(tag);
      }

   }
}
