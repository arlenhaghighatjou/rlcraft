package invtweaks.integration;

import invtweaks.InvTweaks;
import invtweaks.forge.CommonProxy;
import invtweaks.forge.InvTweaksMod;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.Instance;

public class ItemListSorter {
   @Instance
   public static InvTweaksMod instance;
   @SidedProxy(
      clientSide = "invtweaks.forge.ClientProxy",
      serverSide = "invtweaks.forge.CommonProxy"
   )
   public static CommonProxy proxy;

   public ItemListSorter() {
   }

   public static void LinkJEITComparator() {
      if (Loader.isModLoaded("jei")) {
         try {
            Class<?> ingredientListElementComparator = Class.forName("mezz.jei.ingredients.IngredientListElementComparator");
            Class<?> clientConfig = Class.forName("mezz.jei.config.Config");
            if (ingredientListElementComparator != null && clientConfig != null) {
               Class[] cArg = new Class[2];
               cArg[0] = String.class;
               cArg[1] = Comparator.class;
               Method addItemStackComparison = ingredientListElementComparator.getMethod("addItemStackComparison", cArg);
               Method updateSortOrder = clientConfig.getMethod("updateSortOrder");
               if (addItemStackComparison != null && updateSortOrder != null) {
                  Object[] oArg = new Object[2];
                  oArg[0] = "invtweakstree";
                  oArg[1] = new ItemListSorter.ItemListComparator();
                  addItemStackComparison.invoke((Object)null, oArg);
                  oArg[0] = "invtweaksall";
                  oArg[1] = new ItemListSorter.ItemListComparator2();
                  addItemStackComparison.invoke((Object)null, oArg);
                  updateSortOrder.invoke((Object)null);
               }
            }
         } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | ClassNotFoundException var6) {
            return;
         }
      }

   }

   public static void ReloadItemList() {
      if (Loader.isModLoaded("jei")) {
         try {
            Class<?> ProxyCommonClient = Class.forName("mezz.jei.startup.ProxyCommonClient");
            if (ProxyCommonClient != null) {
               Method reloadItemList = ProxyCommonClient.getMethod("reloadItemList");
               if (reloadItemList != null) {
                  reloadItemList.invoke((Object)null);
               }
            }
         } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | ClassNotFoundException var2) {
            return;
         }
      }

   }

   public static class ItemListComparator implements Comparator<ItemStack> {
      public ItemListComparator() {
      }

      public int compare(ItemStack o1, ItemStack o2) {
         if (o1 == null && o2 == null) {
            return 0;
         } else if (o1 == null) {
            return 1;
         } else {
            return o2 == null ? -1 : InvTweaks.getInstance().compareItems(o1, o2, true);
         }
      }
   }

   public static class ItemListComparator2 implements Comparator<ItemStack> {
      public ItemListComparator2() {
      }

      public int compare(ItemStack o1, ItemStack o2) {
         if (o1 == null && o2 == null) {
            return 0;
         } else if (o1 == null) {
            return 1;
         } else {
            return o2 == null ? -1 : InvTweaks.getInstance().compareItems(o1, o2, false);
         }
      }
   }
}
