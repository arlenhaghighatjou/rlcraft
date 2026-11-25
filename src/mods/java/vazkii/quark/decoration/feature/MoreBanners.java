package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.EnumHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;

public class MoreBanners extends Feature {
   public static boolean dragon;
   public static boolean eye;
   public static boolean shield;
   public static boolean sword;

   public MoreBanners() {
   }

   public void setupConfig() {
      dragon = this.loadPropBool("Dragon", "", true);
      eye = this.loadPropBool("Eye", "", true);
      shield = this.loadPropBool("Shield", "", true);
      sword = this.loadPropBool("Sword", "", true);
   }

   public void init() {
      addPattern(dragon, "dragon", "dr", ProxyRegistry.newStack(Items.SKULL, 1, 5));
      addPattern(eye, "eye", "ey", ProxyRegistry.newStack(Items.ENDER_EYE));
      addPattern(shield, "shield", "sh", ProxyRegistry.newStack(Items.IRON_CHESTPLATE));
      addPattern(sword, "sword", "sw", ProxyRegistry.newStack(Items.IRON_SWORD));
   }

   public static void addPattern(boolean doit, String name, String id, ItemStack craftingItem) {
      if (doit) {
         name = "quark_" + name;
         id = "q_" + id;
         EnumHelper.addEnum(BannerPattern.class, name.toUpperCase(), new Class[]{String.class, String.class, ItemStack.class}, new Object[]{name, id, craftingItem});
      }
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
