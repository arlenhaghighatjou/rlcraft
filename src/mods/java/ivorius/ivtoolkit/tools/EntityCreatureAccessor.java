package ivorius.ivtoolkit.tools;

import java.lang.reflect.Field;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityCreatureAccessor {
   protected static Field maximumHomeDistance;

   public EntityCreatureAccessor() {
   }

   protected static Field maximumHomeDistance() {
      return maximumHomeDistance != null ? maximumHomeDistance : (maximumHomeDistance = ReflectionHelper.findField(EntityCreature.class, new String[]{"maximumHomeDistance", "maximumHomeDistance"}));
   }

   public static void setHomePosition(EntityCreature creature, BlockPos pos) {
      float currentHomeDistance = creature.getMaximumHomeDistance();
      creature.setHomePosAndDistance(pos, (int)currentHomeDistance);
      setMaximumHomeDistance(creature, currentHomeDistance);
   }

   public static float getMaximumHomeDistance(EntityCreature creature) {
      try {
         return maximumHomeDistance().getFloat(creature);
      } catch (IllegalAccessException var2) {
         var2.printStackTrace();
         return -1.0F;
      }
   }

   public static void setMaximumHomeDistance(EntityCreature creature, float maximumHomeDistance) {
      try {
         maximumHomeDistance().setFloat(creature, maximumHomeDistance);
      } catch (IllegalAccessException var3) {
         var3.printStackTrace();
      }

   }
}
