package crafttweaker.mc1120.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

public class CraftTweakerHacks {
   private static final Field OREDICTIONARY_IDTOSTACK = getField(OreDictionary.class, "idToStack");
   private static final Field OREDICTIONARY_IDTOSTACKUN = getField(OreDictionary.class, "idToStackUn");
   private static final Field SEEDENTRY_SEED;
   private static final Constructor<? extends Item> SEEDENTRY_CONSTRUCTOR;

   private CraftTweakerHacks() {
   }

   public static List<Item> getSeeds() {
      return (List)getPrivateStaticObject(ForgeHooks.class, "seedList");
   }

   public static List<NonNullList<ItemStack>> getOreIdStacks() {
      try {
         return (List)OREDICTIONARY_IDTOSTACK.get((Object)null);
      } catch (IllegalAccessException var1) {
         CraftTweakerAPI.logError("ERROR - could not load ore dictionary stacks!");
         return null;
      }
   }

   public static List<NonNullList<ItemStack>> getOreIdStacksUn() {
      try {
         return (List)OREDICTIONARY_IDTOSTACKUN.get((Object)null);
      } catch (IllegalAccessException var1) {
         CraftTweakerAPI.logError("ERROR - could not load ore dictionary stacks!");
         return null;
      }
   }

   public static ItemStack getSeedEntrySeed(Object entry) {
      try {
         return (ItemStack)SEEDENTRY_SEED.get(entry);
      } catch (IllegalAccessException var2) {
         CraftTweakerAPI.logError("could not get SeedEntry seed");
         return null;
      }
   }

   public static Item constructSeedEntry(WeightedItemStack stack) {
      try {
         return (Item)SEEDENTRY_CONSTRUCTOR.newInstance(CraftTweakerMC.getItemStack(stack.getStack()), (int)stack.getPercent());
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var2) {
         CraftTweakerAPI.logError("could not construct SeedEntry");
         return null;
      }
   }

   public static Map<String, String> getTranslations() {
      return (Map)getPrivateObject(getPrivateStaticObject(I18n.class, "localizedName", "localizedName"), "languageList", "languageList");
   }

   public static <T> T getPrivateObject(Object object, String... names) {
      Class<?> cls = object.getClass();

      for(String name : names) {
         try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return (T)field.get(object);
         } catch (SecurityException | IllegalAccessException | NoSuchFieldException var8) {
            ;
         }
      }

      return (T)null;
   }

   public static <T> T getPrivateStaticObject(Class<?> cls, String... names) {
      for(String name : names) {
         try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return (T)field.get((Object)null);
         } catch (SecurityException | IllegalAccessException | NoSuchFieldException var7) {
            ;
         }
      }

      return (T)null;
   }

   private static Field getField(Class<?> cls, String... names) {
      for(String name : names) {
         try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
         } catch (SecurityException | NoSuchFieldException var7) {
            ;
         }
      }

      return null;
   }

   static {
      Class<? extends Item> forgeSeedEntry = null;

      try {
         forgeSeedEntry = Class.forName("net.minecraftforge.common.ForgeHooks$SeedEntry");
      } catch (ClassNotFoundException var4) {
         ;
      }

      SEEDENTRY_SEED = getField(forgeSeedEntry, "seed");
      Constructor<? extends Item> seedEntryConstructor = null;

      try {
         seedEntryConstructor = forgeSeedEntry.getConstructor(ItemStack.class, Integer.TYPE);
         seedEntryConstructor.setAccessible(true);
      } catch (SecurityException | NoSuchMethodException var3) {
         Logger.getLogger(CraftTweakerHacks.class.getName()).log(Level.SEVERE, (String)null, var3);
      }

      SEEDENTRY_CONSTRUCTOR = seedEntryConstructor;
   }
}
