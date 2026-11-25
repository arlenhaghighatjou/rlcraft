package vazkii.quark.base.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommonReflectiveAccessor {
   private static final MethodHandle BOW_IS_ARROW;
   private static final MethodHandle NAVIGATOR_SPEED;
   private static final MethodHandle DISPENSER_SUCCESS;

   public CommonReflectiveAccessor() {
   }

   public static boolean isArrow(ItemBow bow, ItemStack arrow) {
      try {
         return BOW_IS_ARROW.invokeExact(bow, arrow);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static double getSpeed(PathNavigate navigator) {
      try {
         return NAVIGATOR_SPEED.invokeExact(navigator);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static boolean getSuccess(BehaviorDispenseOptional behavior) {
      try {
         return DISPENSER_SUCCESS.invokeExact(behavior);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   static {
      try {
         Method m = ObfuscationReflectionHelper.findMethod(ItemBow.class, "isArrow", Boolean.TYPE, new Class[]{ItemStack.class});
         BOW_IS_ARROW = MethodHandles.lookup().unreflect(m);
         Field f = ObfuscationReflectionHelper.findField(PathNavigate.class, "speed");
         NAVIGATOR_SPEED = MethodHandles.lookup().unreflectGetter(f);
         f = ObfuscationReflectionHelper.findField(BehaviorDispenseOptional.class, "successful");
         DISPENSER_SUCCESS = MethodHandles.lookup().unreflectGetter(f);
      } catch (IllegalAccessException var2) {
         throw new RuntimeException(var2);
      }
   }
}
