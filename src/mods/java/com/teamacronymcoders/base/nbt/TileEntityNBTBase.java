package com.teamacronymcoders.base.nbt;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityNBTBase extends TileEntity {
   public TileEntityNBTBase() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      Arrays.stream(this.getClass().getFields()).filter((f) -> {
         return f.isAnnotationPresent(NBT.class);
      }).forEach((f) -> {
         try {
            NBT nbt = (NBT)f.getAnnotation(NBT.class);
            INBTConverter converter = ((INBTConverter)nbt.value().getConverter().newInstance()).setKey(f.getName());
            f.set(this, converter.convert(compound));
         } catch (IllegalAccessException var5) {
            var5.printStackTrace();
         } catch (InstantiationException var6) {
            var6.printStackTrace();
         }

      });
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      Arrays.stream(this.getClass().getFields()).filter((f) -> {
         return f.isAnnotationPresent(NBT.class);
      }).forEach((f) -> {
         try {
            NBT nbt = (NBT)f.getAnnotation(NBT.class);
            INBTConverter converter = ((INBTConverter)nbt.value().getConverter().newInstance()).setKey(f.getName());
            converter.convert(compound, f.get(this));
         } catch (IllegalAccessException var5) {
            var5.printStackTrace();
         } catch (InstantiationException var6) {
            var6.printStackTrace();
         }

      });
      return compound;
   }
}
