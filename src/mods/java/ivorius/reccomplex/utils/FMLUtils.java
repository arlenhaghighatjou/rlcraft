package ivorius.reccomplex.utils;

import com.google.common.collect.ObjectArrays;
import ivorius.reccomplex.RecurrentComplex;
import java.lang.reflect.Constructor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class FMLUtils {
   public FMLUtils() {
   }

   public static String addPrefix(String name) {
      int index = name.lastIndexOf(58);
      String oldPrefix = index == -1 ? "" : name.substring(0, index);
      ModContainer mc = Loader.instance().activeModContainer();
      String prefix;
      if (mc != null) {
         prefix = mc.getModId();
      } else {
         prefix = "minecraft";
      }

      if (!oldPrefix.equals(prefix)) {
         name = prefix + ":" + name;
      }

      return name;
   }

   public static ItemBlock constructItem(Block block, Class<? extends ItemBlock> itemClass, Object... itemArgs) {
      try {
         Class<?>[] ctorArgClasses = new Class[itemArgs.length + 1];
         ctorArgClasses[0] = Block.class;

         for(int idx = 1; idx < ctorArgClasses.length; ++idx) {
            ctorArgClasses[idx] = itemArgs[idx - 1].getClass();
         }

         Constructor<? extends ItemBlock> itemCtor = itemClass.getConstructor(ctorArgClasses);
         return (ItemBlock)itemCtor.newInstance(ObjectArrays.concat(block, itemArgs));
      } catch (Exception var5) {
         RecurrentComplex.logger.warn("Error constructing secret item", var5);
         return null;
      }
   }
}
