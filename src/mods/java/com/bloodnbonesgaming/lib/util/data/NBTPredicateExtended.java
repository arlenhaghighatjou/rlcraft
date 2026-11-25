package com.bloodnbonesgaming.lib.util.data;

import com.bloodnbonesgaming.lib.util.NbtUtilExtended;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTPredicateExtended extends NBTPredicate {
   public NBTPredicateExtended(NBTTagCompound tag) {
      super(tag);
   }

   public boolean test(@Nullable NBTBase nbt) {
      if (nbt == null) {
         return this == ANY;
      } else {
         return this.tag == null || NbtUtilExtended.areNBTEquals(this.tag, nbt, true);
      }
   }
}
