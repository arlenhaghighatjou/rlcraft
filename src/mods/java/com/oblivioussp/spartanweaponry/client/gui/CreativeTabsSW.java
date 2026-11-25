package com.oblivioussp.spartanweaponry.client.gui;

import com.oblivioussp.spartanweaponry.enchantment.EnchantmentSW;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CreativeTabsSW {
   public static CreativeTabs TAB_SW = null;
   public static CreativeTabs TAB_SW_MOD = null;
   public static CreativeTabs TAB_SW_ARROWS_BOLTS = null;

   public CreativeTabsSW() {
   }

   public static void preInit() {
      TAB_SW = (new CreativeTabs("spartanweaponry:tabBasic.name") {
         public ItemStack getTabIconItem() {
            return new ItemStack(CreativeTabsSW.getVanillaTabItem());
         }
      }).setRelevantEnchantmentTypes(new EnumEnchantmentType[]{EnchantmentSW.TYPE_THROWING_WEAPON, EnchantmentSW.TYPE_CROSSBOW});
      if (ConfigHandler.enableModdedMaterialWeapons) {
         TAB_SW_MOD = new CreativeTabs("spartanweaponry:tabModded.name") {
            public ItemStack getTabIconItem() {
               String itemId = CreativeTabsSW.getModdedTabId();
               return itemId != "" ? GameRegistry.makeItemStack(CreativeTabsSW.getModdedTabId(), 0, 1, (String)null) : new ItemStack(ItemRegistrySW.material, 1, 1);
            }
         };
      }

      TAB_SW_ARROWS_BOLTS = new CreativeTabs("spartanweaponry:tabArrowsAndBolts.name") {
         public ItemStack getTabIconItem() {
            return !ConfigHandler.disableNewArrows && !ConfigHandler.disableDiamondArrowsAndBolts ? new ItemStack(ItemRegistrySW.arrowDiamond) : new ItemStack(ItemRegistrySW.bolt);
         }
      };
   }

   protected static Item getVanillaTabItem() {
      if (!ConfigHandler.disableLongsword) {
         return ItemRegistrySW.longswordDiamond;
      } else if (!ConfigHandler.disableGreatsword) {
         return ItemRegistrySW.greatswordDiamond;
      } else if (!ConfigHandler.disableKatana) {
         return ItemRegistrySW.katanaDiamond;
      } else if (!ConfigHandler.disableSaber) {
         return ItemRegistrySW.saberDiamond;
      } else if (!ConfigHandler.disableRapier) {
         return ItemRegistrySW.rapierDiamond;
      } else if (!ConfigHandler.disableHammer) {
         return ItemRegistrySW.hammerDiamond;
      } else if (!ConfigHandler.disableWarhammer) {
         return ItemRegistrySW.warhammerDiamond;
      } else if (!ConfigHandler.disableSpear) {
         return ItemRegistrySW.spearDiamond;
      } else if (!ConfigHandler.disableHalberd) {
         return ItemRegistrySW.halberdDiamond;
      } else if (!ConfigHandler.disablePike) {
         return ItemRegistrySW.pikeDiamond;
      } else if (!ConfigHandler.disableLance) {
         return ItemRegistrySW.lanceDiamond;
      } else if (!ConfigHandler.disableLongbow) {
         return ItemRegistrySW.longbowDiamond;
      } else if (!ConfigHandler.disableCrossbow) {
         return ItemRegistrySW.crossbowDiamond;
      } else if (!ConfigHandler.disableThrowingKnife) {
         return ItemRegistrySW.throwingKnifeDiamond;
      } else if (!ConfigHandler.disableThrowingAxe) {
         return ItemRegistrySW.throwingAxeDiamond;
      } else if (!ConfigHandler.disableJavelin) {
         return ItemRegistrySW.javelinDiamond;
      } else if (!ConfigHandler.disableDagger) {
         return ItemRegistrySW.daggerDiamond;
      } else if (!ConfigHandler.disableCaestus) {
         return ItemRegistrySW.caestusBasic;
      } else {
         return (Item)(!ConfigHandler.disableClub ? ItemRegistrySW.clubWood : ItemRegistrySW.material);
      }
   }

   protected static String getModdedTabId() {
      if (!ConfigHandler.disableGreatsword) {
         return "spartanweaponry:greatsword_copper";
      } else if (!ConfigHandler.disableLongsword) {
         return "spartanweaponry:longsword_copper";
      } else if (!ConfigHandler.disableKatana) {
         return "spartanweaponry:katana_copper";
      } else if (!ConfigHandler.disableSaber) {
         return "spartanweaponry:saber_copper";
      } else if (!ConfigHandler.disableRapier) {
         return "spartanweaponry:rapier_copper";
      } else if (!ConfigHandler.disableHammer) {
         return "spartanweaponry:hammer_copper";
      } else if (!ConfigHandler.disableWarhammer) {
         return "spartanweaponry:warhammer_copper";
      } else if (!ConfigHandler.disableSpear) {
         return "spartanweaponry:spear_copper";
      } else if (!ConfigHandler.disableHalberd) {
         return "spartanweaponry:halberd_copper";
      } else if (!ConfigHandler.disablePike) {
         return "spartanweaponry:pike_copper";
      } else if (!ConfigHandler.disableLance) {
         return "spartanweaponry:lance_copper";
      } else if (!ConfigHandler.disableLongbow) {
         return "spartanweaponry:longbow_copper";
      } else if (!ConfigHandler.disableCrossbow) {
         return "spartanweaponry:crossbow_copper";
      } else if (!ConfigHandler.disableThrowingKnife) {
         return "spartanweaponry:throwing_knife_copper";
      } else if (!ConfigHandler.disableThrowingAxe) {
         return "spartanweaponry:throwing_axe_copper";
      } else if (!ConfigHandler.disableJavelin) {
         return "spartanweaponry:javelin_copper";
      } else {
         return !ConfigHandler.disableDagger ? "spartanweaponry:dagger_copper" : "";
      }
   }
}
