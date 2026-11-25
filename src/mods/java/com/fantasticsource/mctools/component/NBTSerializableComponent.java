package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.Component;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class NBTSerializableComponent extends Component implements INBTSerializable {
   public NBTSerializableComponent() {
   }

   public static NBTTagCompound serializeMarked(NBTSerializableComponent component) {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("class", component.getClass().getName());
      compound.setTag("data", component.serializeNBT());
      return compound;
   }

   public static NBTSerializableComponent deserializeMarked(NBTTagCompound compound) {
      try {
         NBTSerializableComponent component = (NBTSerializableComponent)Class.forName(compound.getString("class")).newInstance();
         component.deserializeNBT(compound.getTag("data"));
         return component;
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
