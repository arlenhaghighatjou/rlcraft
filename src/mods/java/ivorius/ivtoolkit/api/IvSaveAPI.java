package ivorius.ivtoolkit.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class IvSaveAPI {
   public IvSaveAPI() {
   }

   private static Method getWriteTileEntityToTag(TileEntity entity) throws NoSuchMethodException {
      return entity.getClass().getDeclaredMethod("writeTileEntityToTag", NBTTagCompound.class, Boolean.TYPE);
   }

   public static boolean canWriteTileEntityToTag(TileEntity entity) {
      try {
         getWriteTileEntityToTag(entity);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   public static NBTTagCompound writeTileEntityToTag(TileEntity entity, NBTTagCompound compound, boolean worldIndependent) {
      try {
         return (NBTTagCompound)getWriteTileEntityToTag(entity).invoke(entity, compound, worldIndependent);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var4) {
         throw new IllegalStateException(var4);
      }
   }

   private static Method getWriteEntityToTag(Entity entity) throws NoSuchMethodException {
      return entity.getClass().getDeclaredMethod("writeEntityToTag", NBTTagCompound.class, Boolean.TYPE);
   }

   public static boolean canWriteEntityToTag(Entity entity) {
      try {
         getWriteEntityToTag(entity);
         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   public static NBTTagCompound writeEntityToTag(Entity entity, NBTTagCompound compound, boolean worldIndependent) {
      try {
         return (NBTTagCompound)getWriteEntityToTag(entity).invoke(entity, compound, worldIndependent);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var4) {
         throw new IllegalStateException(var4);
      }
   }
}
