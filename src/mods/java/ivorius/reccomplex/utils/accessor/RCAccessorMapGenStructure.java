package ivorius.reccomplex.utils.accessor;

import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RCAccessorMapGenStructure {
   private static Method getStructureAt;

   public RCAccessorMapGenStructure() {
   }

   public static StructureStart getStructureAt(MapGenStructure gen, BlockPos pos) {
      if (getStructureAt == null) {
         getStructureAt = ReflectionHelper.findMethod(MapGenStructure.class, "getStructureAt", "getStructureAt", new Class[]{BlockPos.class});
      }

      return (StructureStart)SafeReflector.invoke(gen, getStructureAt, (Object)null, pos);
   }
}
