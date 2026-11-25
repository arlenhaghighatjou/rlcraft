package knightminer.inspirations.library.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindClassException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.registries.IRegistryDelegate;

public final class ReflectionUtil {
   private static final Map<String, Field> FIELDS = new HashMap();
   private static final Map<String, Method> METHODS = new HashMap();
   private static final Map<String, Class<?>> CLASS = new HashMap();

   private ReflectionUtil() {
   }

   @Nullable
   public static PotionType getMixPredicateInput(@Nonnull Object mixPredicate) {
      Object val = getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "input", "input");
      return val instanceof IRegistryDelegate ? (PotionType)((IRegistryDelegate)val).get() : (PotionType)val;
   }

   @Nullable
   public static Ingredient getMixPredicateReagent(@Nonnull Object mixPredicate) {
      return (Ingredient)getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "reagent", "reagent");
   }

   @Nullable
   public static PotionType getMixPredicateOutput(@Nonnull Object mixPredicate) {
      Object val = getFieldValue(getClass("net.minecraft.potion.PotionHelper$MixPredicate"), mixPredicate, "output", "output");
      return val instanceof IRegistryDelegate ? (PotionType)((IRegistryDelegate)val).get() : (PotionType)val;
   }

   @Nullable
   public static ItemStack invokeGetSilkTouchDrop(@Nonnull Block block, IBlockState state) {
      return (ItemStack)invokeMethod(Block.class, block, "getSilkTouchDrop", "getSilkTouchDrop", new Class[]{IBlockState.class}, state);
   }

   public static Class<?> getClass(String className) {
      try {
         return (Class)CLASS.computeIfAbsent(className, (key) -> {
            return ReflectionHelper.getClass(InspirationsRegistry.class.getClassLoader(), new String[]{key});
         });
      } catch (UnableToFindClassException var2) {
         InspirationsRegistry.log.error(var2);
         CLASS.putIfAbsent(className, (Object)null);
         return null;
      }
   }

   @Nullable
   private static <T> T invokeMethod(Class<?> classToSearch, Object instance, String mcpName, String srgName, Class<?>[] paramTypes, Object... params) {
      try {
         Method m = (Method)METHODS.computeIfAbsent(srgName, (key) -> {
            return (T)ReflectionHelper.findMethod(classToSearch, mcpName, key, paramTypes);
         });
         return (T)m.invoke(instance, params);
      } catch (IllegalArgumentException | NullPointerException | InvocationTargetException | ClassCastException | UnableToFindMethodException | IllegalAccessException var7) {
         InspirationsRegistry.log.error(var7);
         METHODS.putIfAbsent(srgName, (Object)null);
         return (T)null;
      }
   }

   @Nullable
   private static <T> T getFieldValue(Class<?> classToSearch, Object instance, String mcpName, String srgName) {
      try {
         Field f = (Field)FIELDS.computeIfAbsent(srgName, (key) -> {
            return (T)ReflectionHelper.findField(classToSearch, new String[]{key, mcpName});
         });
         return (T)(f != null ? f.get(instance) : null);
      } catch (ClassCastException | NullPointerException | UnableToFindFieldException | IllegalAccessException var5) {
         InspirationsRegistry.log.error(var5);
         FIELDS.putIfAbsent(srgName, (Object)null);
         return (T)null;
      }
   }
}
