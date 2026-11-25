package com.oblivioussp.spartanweaponry.util;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IInternalMethodHandler;
import com.oblivioussp.spartanweaponry.api.IWeaponCallback;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.init.ModelRenderRegistry;
import com.oblivioussp.spartanweaponry.item.ItemBattleaxe;
import com.oblivioussp.spartanweaponry.item.ItemBoomerang;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.item.ItemDagger;
import com.oblivioussp.spartanweaponry.item.ItemGlaive;
import com.oblivioussp.spartanweaponry.item.ItemGreatsword;
import com.oblivioussp.spartanweaponry.item.ItemHalberd;
import com.oblivioussp.spartanweaponry.item.ItemHammer;
import com.oblivioussp.spartanweaponry.item.ItemJavelin;
import com.oblivioussp.spartanweaponry.item.ItemKatana;
import com.oblivioussp.spartanweaponry.item.ItemLance;
import com.oblivioussp.spartanweaponry.item.ItemLongbow;
import com.oblivioussp.spartanweaponry.item.ItemLongsword;
import com.oblivioussp.spartanweaponry.item.ItemMace;
import com.oblivioussp.spartanweaponry.item.ItemParryingDagger;
import com.oblivioussp.spartanweaponry.item.ItemPike;
import com.oblivioussp.spartanweaponry.item.ItemQuarterstaff;
import com.oblivioussp.spartanweaponry.item.ItemRapier;
import com.oblivioussp.spartanweaponry.item.ItemSaber;
import com.oblivioussp.spartanweaponry.item.ItemSpear;
import com.oblivioussp.spartanweaponry.item.ItemThrowingAxe;
import com.oblivioussp.spartanweaponry.item.ItemThrowingKnife;
import com.oblivioussp.spartanweaponry.item.ItemWarhammer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class APIInternalMethodHandler implements IInternalMethodHandler {
   public APIInternalMethodHandler() {
   }

   public Item addDagger(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableDagger) {
         return null;
      } else {
         ItemDagger dagger = new ItemDagger("dagger_" + material.getUnlocName(), modId, material);
         dagger.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            dagger.addWeaponProperty(prop);
         }

         return dagger;
      }
   }

   public Item addLongsword(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableLongsword) {
         return null;
      } else {
         ItemLongsword longsword = new ItemLongsword("longsword_" + material.getUnlocName(), modId, material);
         longsword.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            longsword.addWeaponProperty(prop);
         }

         return longsword;
      }
   }

   public Item addKatana(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableKatana) {
         return null;
      } else {
         ItemKatana katana = new ItemKatana("katana_" + material.getUnlocName(), modId, material);
         katana.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            katana.addWeaponProperty(prop);
         }

         return katana;
      }
   }

   public Item addSaber(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableSaber) {
         return null;
      } else {
         ItemSaber saber = new ItemSaber("saber_" + material.getUnlocName(), modId, material);
         saber.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            saber.addWeaponProperty(prop);
         }

         return saber;
      }
   }

   public Item addRapier(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableRapier) {
         return null;
      } else {
         ItemRapier rapier = new ItemRapier("rapier_" + material.getUnlocName(), modId, material);
         rapier.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            rapier.addWeaponProperty(prop);
         }

         return rapier;
      }
   }

   public Item addGreatsword(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableGreatsword) {
         return null;
      } else {
         ItemGreatsword greatsword = new ItemGreatsword("greatsword_" + material.getUnlocName(), modId, material);
         greatsword.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            greatsword.addWeaponProperty(prop);
         }

         return greatsword;
      }
   }

   public Item addHammer(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableHammer) {
         return null;
      } else {
         ItemHammer hammer = new ItemHammer("hammer_" + material.getUnlocName(), modId, material);
         hammer.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            hammer.addWeaponProperty(prop);
         }

         return hammer;
      }
   }

   public Item addWarhammer(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableWarhammer) {
         return null;
      } else {
         ItemWarhammer warhammer = new ItemWarhammer("warhammer_" + material.getUnlocName(), modId, material);
         warhammer.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            warhammer.addWeaponProperty(prop);
         }

         return warhammer;
      }
   }

   public Item addSpear(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableSpear) {
         return null;
      } else {
         ItemSpear spear = new ItemSpear("spear_" + material.getUnlocName(), modId, material);
         spear.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            spear.addWeaponProperty(prop);
         }

         return spear;
      }
   }

   public Item addHalberd(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableHalberd) {
         return null;
      } else {
         ItemHalberd halberd = new ItemHalberd("halberd_" + material.getUnlocName(), modId, material);
         halberd.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            halberd.addWeaponProperty(prop);
         }

         return halberd;
      }
   }

   public Item addPike(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disablePike) {
         return null;
      } else {
         ItemPike pike = new ItemPike("pike_" + material.getUnlocName(), modId, material);
         pike.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            pike.addWeaponProperty(prop);
         }

         return pike;
      }
   }

   public Item addLance(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableLance) {
         return null;
      } else {
         ItemLance lance = new ItemLance("lance_" + material.getUnlocName(), modId, material);
         lance.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            lance.addWeaponProperty(prop);
         }

         return lance;
      }
   }

   public Item addLongbow(ToolMaterialEx material, String modId, CreativeTabs tab, IWeaponCallback callback) {
      if (ConfigHandler.disableLongbow) {
         return null;
      } else {
         ItemLongbow longbow = new ItemLongbow("longbow_" + material.getUnlocName(), modId, material, callback);
         longbow.setCreativeTab(tab);
         return longbow;
      }
   }

   public Item addCrossbow(ToolMaterialEx material, String modId, CreativeTabs tab, IWeaponCallback callback) {
      if (ConfigHandler.disableCrossbow) {
         return null;
      } else {
         ItemCrossbow crossbow = new ItemCrossbow("crossbow_" + material.getUnlocName(), modId, material, callback);
         crossbow.setCreativeTab(tab);
         return crossbow;
      }
   }

   public Item addThrowingKnife(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableThrowingKnife) {
         return null;
      } else {
         ItemThrowingKnife throwingKnife = new ItemThrowingKnife("throwing_knife_" + material.getUnlocName(), modId, material);
         throwingKnife.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            throwingKnife.addWeaponProperty(prop);
         }

         return throwingKnife;
      }
   }

   public Item addThrowingAxe(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableThrowingAxe) {
         return null;
      } else {
         ItemThrowingAxe throwingAxe = new ItemThrowingAxe("throwing_axe_" + material.getUnlocName(), modId, material);
         throwingAxe.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            throwingAxe.addWeaponProperty(prop);
         }

         return throwingAxe;
      }
   }

   public Item addJavelin(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableJavelin) {
         return null;
      } else {
         ItemJavelin javelin = new ItemJavelin("javelin_" + material.getUnlocName(), modId, material);
         javelin.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            javelin.addWeaponProperty(prop);
         }

         return javelin;
      }
   }

   public Item addBoomerang(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableBoomerang) {
         return null;
      } else {
         ItemBoomerang boomerang = new ItemBoomerang("boomerang_" + material.getUnlocName(), modId, material);
         boomerang.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            boomerang.addWeaponProperty(prop);
         }

         return boomerang;
      }
   }

   public Item addBattleaxe(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableBattleaxe) {
         return null;
      } else {
         ItemBattleaxe battleaxe = new ItemBattleaxe("battleaxe_" + material.getUnlocName(), modId, material);
         battleaxe.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            battleaxe.addWeaponProperty(prop);
         }

         return battleaxe;
      }
   }

   public Item addMace(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableMace) {
         return null;
      } else {
         ItemMace mace = new ItemMace("mace_" + material.getUnlocName(), modId, material);
         mace.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            mace.addWeaponProperty(prop);
         }

         return mace;
      }
   }

   public Item addGlaive(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableGlaive) {
         return null;
      } else {
         ItemGlaive glaive = new ItemGlaive("glaive_" + material.getUnlocName(), modId, material);
         glaive.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            glaive.addWeaponProperty(prop);
         }

         return glaive;
      }
   }

   public Item addQuarterstaff(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableQuarterstaff) {
         return null;
      } else {
         ItemQuarterstaff quarterstaff = new ItemQuarterstaff("staff_" + material.getUnlocName(), modId, material);
         quarterstaff.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            quarterstaff.addWeaponProperty(prop);
         }

         return quarterstaff;
      }
   }

   public Item addParryingDagger(ToolMaterialEx material, String modId, CreativeTabs tab, WeaponProperty... properties) {
      if (ConfigHandler.disableParryingDagger) {
         return null;
      } else {
         ItemParryingDagger parryingDagger = new ItemParryingDagger("parrying_dagger_" + material.getUnlocName(), modId, material);
         parryingDagger.setCreativeTab(tab);

         for(WeaponProperty prop : properties) {
            parryingDagger.addWeaponProperty(prop);
         }

         return parryingDagger;
      }
   }

   public void registerColourHandler(Item item, ToolMaterialEx material) {
      ModSpartanWeaponry var10000 = ModSpartanWeaponry.instance;
      ModSpartanWeaponry.proxy.addColourHandler(item, material);
   }

   public void addItemModelToRegistry(Item item) {
      ModelRenderRegistry.addItemToRegistry(item, item.getRegistryName());
   }

   public void addItemModelToRegistry(Item item, ResourceLocation modelLocation) {
      ModelRenderRegistry.addItemToRegistry(item, modelLocation);
   }

   public void addItemModelToRegistry(Item item, String modId, String modelLocation) {
      ModelRenderRegistry.addItemToRegistry(item, new ResourceLocation(modId, modelLocation));
   }

   public String translateString(String key, String type, String modId) {
      return StringHelper.translateString(key, type, modId);
   }

   public String translateFormattedString(String key, String type, String modId, Object... parameters) {
      return StringHelper.translateFormattedString(key, type, modId, parameters);
   }

   public boolean hasTranslateKey(String key, String type, String modId) {
      return StringHelper.hasTranslateKey(key, type, modId);
   }
}
