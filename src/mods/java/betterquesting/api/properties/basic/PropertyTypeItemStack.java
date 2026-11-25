package betterquesting.api.properties.basic;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PropertyTypeItemStack extends PropertyTypeBase<BigItemStack> {
   public PropertyTypeItemStack(ResourceLocation key, BigItemStack def) {
      super(key, def);
   }

   public BigItemStack readValue(NBTBase nbt) {
      return nbt != null && nbt.getId() == 10 ? JsonHelper.JsonToItemStack((NBTTagCompound)nbt) : (BigItemStack)this.getDefault();
   }

   public NBTBase writeValue(BigItemStack value) {
      NBTTagCompound nbt = new NBTTagCompound();
      if (value != null && value.getBaseStack() != null) {
         value.writeToNBT(nbt);
      } else {
         ((BigItemStack)this.getDefault()).writeToNBT(nbt);
      }

      return nbt;
   }
}
