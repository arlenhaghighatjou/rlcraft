package ivorius.reccomplex.utils.accessor;

import java.io.DataOutput;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RCAccessorNBT {
   private static Method methodSetNBTTagName;

   public RCAccessorNBT() {
   }

   public static void writeEntry(String name, NBTBase nbt, DataOutput dataOutput) {
      if (methodSetNBTTagName == null) {
         methodSetNBTTagName = ReflectionHelper.findMethod(NBTTagCompound.class, "writeEntry", "writeEntry", new Class[]{String.class, NBTBase.class, DataOutput.class});
      }

      try {
         methodSetNBTTagName.invoke((Object)null, name, nbt, dataOutput);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         var4.printStackTrace();
      }

   }
}
