package ivorius.ivtoolkit.tools;

import ivorius.ivtoolkit.api.IvSaveAPI;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class NBTCompoundObjectsMC {
   public NBTCompoundObjectsMC() {
   }

   public static NBTTagCompound write(Entity entity) {
      return write(entity, false);
   }

   public static NBTTagCompound write(Entity entity, boolean worldIndependent) {
      NBTTagCompound compound = new NBTTagCompound();
      if (IvSaveAPI.canWriteEntityToTag(entity)) {
         IvSaveAPI.writeEntityToTag(entity, compound, worldIndependent);
      } else {
         entity.writeToNBTOptional(compound);
      }

      return compound;
   }

   public static NBTTagCompound write(TileEntity entity) {
      return write(entity, false);
   }

   public static NBTTagCompound write(TileEntity entity, boolean worldIndependent) {
      NBTTagCompound compound = new NBTTagCompound();
      if (IvSaveAPI.canWriteTileEntityToTag(entity)) {
         IvSaveAPI.writeTileEntityToTag(entity, compound, worldIndependent);
      } else {
         entity.writeToNBT(compound);
      }

      return compound;
   }
}
