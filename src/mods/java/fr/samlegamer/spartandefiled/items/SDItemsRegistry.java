package fr.samlegamer.spartandefiled.items;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanweaponry.api.IWeaponCallback;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import fr.samlegamer.spartandefiled.utils.ModelRenderRegistrySD;
import fr.samlegamer.spartandefiled.utils.SDMatConvert;
import fr.samlegamer.spartandefiled.utils.SDUtils;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lykrast.defiledlands.common.init.ModItems;
import lykrast.defiledlands.common.util.CreativeTabDL;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class SDItemsRegistry {
   public static final Set<SDMatConvert> MATERIALS_TO_REGISTER = new LinkedHashSet();
   public static final String UMBRIUM = "umbrium";
   private static final Set<Item> ALL_ITEMS = new HashSet();

   public SDItemsRegistry() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, SDItemsRegistry::registerItems);
      bus.register(ModelRegistryEvent.class, SDItemsRegistry::registerModels);
   }

   public static void registerItems(Register<Item> ev) {
      Set<Item> item_set = new LinkedHashSet();
      OreDictionary.registerOre("ingotUmbrium", ModItems.umbriumIngot);

      for(SDMatConvert mat : MATERIALS_TO_REGISTER) {
         if (!ConfigHandler.disableKatana) {
            Item katana = SpartanWeaponryAPI.createKatana(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(katana, "katana_" + mat.material.getUnlocName());
            item_set.add(katana);
         }

         if (!ConfigHandler.disableGreatsword) {
            Item greatsword = SpartanWeaponryAPI.createGreatsword(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(greatsword, "greatsword_" + mat.material.getUnlocName());
            item_set.add(greatsword);
         }

         if (!ConfigHandler.disableLongsword) {
            Item longsword = SpartanWeaponryAPI.createLongsword(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(longsword, "longsword_" + mat.material.getUnlocName());
            item_set.add(longsword);
         }

         if (!ConfigHandler.disableSaber) {
            Item saber = SpartanWeaponryAPI.createSaber(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(saber, "saber_" + mat.material.getUnlocName());
            item_set.add(saber);
         }

         if (!ConfigHandler.disableRapier) {
            Item rapier = SpartanWeaponryAPI.createRapier(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(rapier, "rapier_" + mat.material.getUnlocName());
            item_set.add(rapier);
         }

         if (!ConfigHandler.disableDagger) {
            Item dagger = SpartanWeaponryAPI.createDagger(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(dagger, "dagger_" + mat.material.getUnlocName());
            item_set.add(dagger);
         }

         if (!ConfigHandler.disableSpear) {
            Item spear = SpartanWeaponryAPI.createSpear(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(spear, "spear_" + mat.material.getUnlocName());
            item_set.add(spear);
         }

         if (!ConfigHandler.disablePike) {
            Item pike = SpartanWeaponryAPI.createPike(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(pike, "pike_" + mat.material.getUnlocName());
            item_set.add(pike);
         }

         if (!ConfigHandler.disableLance) {
            Item lance = SpartanWeaponryAPI.createLance(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(lance, "lance_" + mat.material.getUnlocName());
            item_set.add(lance);
         }

         if (!ConfigHandler.disableHalberd) {
            Item halberd = SpartanWeaponryAPI.createHalberd(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(halberd, "halberd_" + mat.material.getUnlocName());
            item_set.add(halberd);
         }

         if (!ConfigHandler.disableWarhammer) {
            Item warhammer = SpartanWeaponryAPI.createWarhammer(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(warhammer, "warhammer_" + mat.material.getUnlocName());
            item_set.add(warhammer);
         }

         if (!ConfigHandler.disableHammer) {
            Item hammer = SpartanWeaponryAPI.createHammer(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(hammer, "hammer_" + mat.material.getUnlocName());
            item_set.add(hammer);
         }

         if (!ConfigHandler.disableThrowingAxe) {
            Item throwing_axe = SpartanWeaponryAPI.createThrowingAxe(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(throwing_axe, "throwing_axe_" + mat.material.getUnlocName());
            item_set.add(throwing_axe);
         }

         if (!ConfigHandler.disableThrowingKnife) {
            Item throwing_knife = SpartanWeaponryAPI.createThrowingKnife(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(throwing_knife, "throwing_knife_" + mat.material.getUnlocName());
            item_set.add(throwing_knife);
         }

         if (!ConfigHandler.disableLongbow && !ConfigHandler.woodenLongbowOnly) {
            Item longbow = SpartanWeaponryAPI.createLongbow(mat.material, "spartandefiled", CreativeTabDL.instance, (IWeaponCallback)null);
            ModelRenderRegistrySD.addItemToRegistry(longbow, "longbow_" + mat.material.getUnlocName());
            item_set.add(longbow);
         }

         if (!ConfigHandler.disableCrossbow && !ConfigHandler.woodenCrossbowOnly) {
            Item crossbow = SpartanWeaponryAPI.createCrossbow(mat.material, "spartandefiled", CreativeTabDL.instance, (IWeaponCallback)null);
            ModelRenderRegistrySD.addItemToRegistry(crossbow, "crossbow_" + mat.material.getUnlocName());
            item_set.add(crossbow);
         }

         if (!ConfigHandler.disableJavelin) {
            Item javelin = SpartanWeaponryAPI.createJavelin(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(javelin, "javelin_" + mat.material.getUnlocName());
            item_set.add(javelin);
         }

         if (!ConfigHandler.disableBattleaxe) {
            Item battleaxe = SpartanWeaponryAPI.createBattleaxe(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(battleaxe, "battleaxe_" + mat.material.getUnlocName());
            item_set.add(battleaxe);
         }

         if (!ConfigHandler.disableBoomerang && !ConfigHandler.woodenBoomerangOnly) {
            Item boomerang = SpartanWeaponryAPI.createBoomerang(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(boomerang, "boomerang_" + mat.material.getUnlocName());
            item_set.add(boomerang);
         }

         if (!ConfigHandler.disableMace) {
            Item mace = SpartanWeaponryAPI.createMace(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(mace, "mace_" + mat.material.getUnlocName());
            item_set.add(mace);
         }

         if (!ConfigHandler.disableQuarterstaff) {
            Item quarterstaff = SpartanWeaponryAPI.createQuarterstaff(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(quarterstaff, "staff_" + mat.material.getUnlocName());
            item_set.add(quarterstaff);
         }

         if (!ConfigHandler.disableGlaive) {
            Item glaive = SpartanWeaponryAPI.createGlaive(mat.material, "spartandefiled", CreativeTabDL.instance, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySD.addItemToRegistry(glaive, "glaive_" + mat.material.getUnlocName());
            item_set.add(glaive);
         }

         if (ConfigHandler.enableExperimentalWeapons && !ConfigHandler.disableParryingDagger) {
            ;
         }
      }

      for(Item it : item_set) {
         ev.getRegistry().register(it);
      }

      ALL_ITEMS.forEach(ev.getRegistry()::register);
   }

   @SideOnly(Side.CLIENT)
   public static void registerModels(ModelRegistryEvent event) {
      ALL_ITEMS.stream().filter((item) -> {
         return item.getRegistryName() != null;
      }).forEach((item) -> {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      });
   }

   static {
      MATERIALS_TO_REGISTER.add(new SDMatConvert("umbrium", SDUtils.spartanMatFromToolMat("umbrium", ModItems.materialUmbrium, 9867904, 14999238, "ingotUmbrium"), new WeaponProperty[0]));
   }
}
