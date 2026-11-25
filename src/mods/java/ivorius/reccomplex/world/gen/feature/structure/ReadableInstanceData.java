package ivorius.reccomplex.world.gen.feature.structure;

import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ReadableInstanceData<T extends NBTStorable> {
   private NBTBase instanceDataNBT;
   private T instanceData;

   public ReadableInstanceData() {
   }

   public boolean exists() {
      return this.instanceData != null || this.instanceDataNBT != null;
   }

   public void setInstanceData(T instanceData) {
      this.instanceData = instanceData;
   }

   public void writeToNBT(String key, NBTTagCompound compound) {
      NBTBase instanceDataNBT = this.instanceData != null ? this.instanceData.writeToNBT() : (this.instanceDataNBT != null ? this.instanceDataNBT.copy() : null);
      if (instanceDataNBT != null) {
         compound.setTag(key, instanceDataNBT);
      }

   }

   public void readFromNBT(String key, NBTTagCompound compound) {
      this.instanceDataNBT = compound.hasKey(key) ? compound.getTag(key).copy() : null;
   }

   public boolean load(StructureGenerator<T> generator) {
      if (this.instanceDataNBT != null) {
         generator.instanceData(this.instanceDataNBT);
         this.instanceData = generator.instanceData().get();
         this.instanceDataNBT = null;
      }

      return this.instanceData != null;
   }

   public StructureGenerator<T> register(StructureGenerator<T> generator) {
      if (this.instanceData != null) {
         return generator.instanceData(this.instanceData);
      } else {
         return this.instanceDataNBT != null ? generator.instanceData(this.instanceDataNBT) : generator;
      }
   }

   public void set(ReadableInstanceData<T> instanceData) {
      this.instanceData = instanceData.instanceData;
      this.instanceDataNBT = instanceData.instanceDataNBT;
   }
}
