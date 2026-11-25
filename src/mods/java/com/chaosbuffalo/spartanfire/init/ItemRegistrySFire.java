package com.chaosbuffalo.spartanfire.init;

import com.chaosbuffalo.spartanfire.ForgeConfigHandler;
import com.chaosbuffalo.spartanfire.IAFMatConverter;
import com.chaosbuffalo.spartanfire.Utils;
import com.chaosbuffalo.spartanfire.integrations.FireSwordWeaponProperty;
import com.chaosbuffalo.spartanfire.integrations.IceSwordWeaponProperty;
import com.chaosbuffalo.spartanfire.integrations.MyrmexPoisonSwordProperty;
import com.chaosbuffalo.spartanfire.integrations.MyrmexSwordProperty;
import com.chaosbuffalo.spartanfire.items.SFItem;
import com.chaosbuffalo.spartanfire.recipes.FireIceThrowingRecipes;
import com.chaosbuffalo.spartanfire.recipes.VenomThrowingRecipes;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.oblivioussp.spartanweaponry.api.IWeaponCallback;
import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class ItemRegistrySFire {
   public static final Set<IAFMatConverter> MATERIALS_TO_REGISTER = new LinkedHashSet();
   public static final String DRAGONBONE = "dragonbone";
   public static final String FIRE_DRAGONBONE = "fire_dragonbone";
   public static final String ICE_DRAGONBONE = "ice_dragonbone";
   public static final String JUNGLE = "jungle";
   public static final String JUNGLE_VENOM = "jungle_venom";
   public static final String DESERT = "desert";
   public static final String DESERT_VENOM = "desert_venom";
   private static final Set<Item> ALL_ITEMS = new HashSet();

   public ItemRegistrySFire() {
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> ev) {
      OreDictionary.registerOre("ingotDragonbone", ModItems.dragonbone);
      OreDictionary.registerOre("ingotJungleChitin", ModItems.myrmex_jungle_chitin);
      OreDictionary.registerOre("ingotDesertChitin", ModItems.myrmex_desert_chitin);
      Set<Item> item_set = new LinkedHashSet();
      Item witherboneHandle = new SFItem(new ResourceLocation("spartanfire", "witherbone_handle"), IceAndFire.TAB);
      Item witherbonePole = new SFItem(new ResourceLocation("spartanfire", "witherbone_pole"), IceAndFire.TAB);
      ALL_ITEMS.add(witherboneHandle);
      ALL_ITEMS.add(witherbonePole);

      for(IAFMatConverter mat : MATERIALS_TO_REGISTER) {
         if (!ConfigHandler.disableKatana) {
            Item katana = SpartanWeaponryAPI.createKatana(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(katana, "katana_" + mat.material.getUnlocName());
            item_set.add(katana);
         }

         if (!ConfigHandler.disableGreatsword) {
            Item greatsword = SpartanWeaponryAPI.createGreatsword(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(greatsword, "greatsword_" + mat.material.getUnlocName());
            item_set.add(greatsword);
         }

         if (!ConfigHandler.disableLongsword) {
            Item longsword = SpartanWeaponryAPI.createLongsword(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(longsword, "longsword_" + mat.material.getUnlocName());
            item_set.add(longsword);
         }

         if (!ConfigHandler.disableSaber) {
            Item saber = SpartanWeaponryAPI.createSaber(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(saber, "saber_" + mat.material.getUnlocName());
            item_set.add(saber);
         }

         if (!ConfigHandler.disableRapier) {
            Item rapier = SpartanWeaponryAPI.createRapier(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(rapier, "rapier_" + mat.material.getUnlocName());
            item_set.add(rapier);
         }

         if (!ConfigHandler.disableDagger) {
            Item dagger = SpartanWeaponryAPI.createDagger(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(dagger, "dagger_" + mat.material.getUnlocName());
            item_set.add(dagger);
         }

         if (!ConfigHandler.disableSpear) {
            Item spear = SpartanWeaponryAPI.createSpear(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(spear, "spear_" + mat.material.getUnlocName());
            item_set.add(spear);
         }

         if (!ConfigHandler.disablePike) {
            Item pike = SpartanWeaponryAPI.createPike(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(pike, "pike_" + mat.material.getUnlocName());
            item_set.add(pike);
         }

         if (!ConfigHandler.disableLance) {
            Item lance = SpartanWeaponryAPI.createLance(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(lance, "lance_" + mat.material.getUnlocName());
            item_set.add(lance);
         }

         if (!ConfigHandler.disableHalberd) {
            Item halberd = SpartanWeaponryAPI.createHalberd(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(halberd, "halberd_" + mat.material.getUnlocName());
            item_set.add(halberd);
         }

         if (!ConfigHandler.disableWarhammer) {
            Item warhammer = SpartanWeaponryAPI.createWarhammer(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(warhammer, "warhammer_" + mat.material.getUnlocName());
            item_set.add(warhammer);
         }

         if (!ConfigHandler.disableHammer) {
            Item hammer = SpartanWeaponryAPI.createHammer(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(hammer, "hammer_" + mat.material.getUnlocName());
            item_set.add(hammer);
         }

         if (!ConfigHandler.disableThrowingAxe) {
            Item throwing_axe = SpartanWeaponryAPI.createThrowingAxe(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(throwing_axe, "throwing_axe_" + mat.material.getUnlocName());
            item_set.add(throwing_axe);
         }

         if (!ConfigHandler.disableThrowingKnife) {
            Item throwing_knife = SpartanWeaponryAPI.createThrowingKnife(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(throwing_knife, "throwing_knife_" + mat.material.getUnlocName());
            item_set.add(throwing_knife);
         }

         if (!ConfigHandler.disableLongbow && !ConfigHandler.woodenLongbowOnly) {
            Item longbow = SpartanWeaponryAPI.createLongbow(mat.material, "spartanfire", IceAndFire.TAB, (IWeaponCallback)null);
            ModelRenderRegistrySFire.addItemToRegistry(longbow, "longbow_" + mat.material.getUnlocName());
            item_set.add(longbow);
         }

         if (!ConfigHandler.disableCrossbow && !ConfigHandler.woodenCrossbowOnly) {
            Item crossbow = SpartanWeaponryAPI.createCrossbow(mat.material, "spartanfire", IceAndFire.TAB, (IWeaponCallback)null);
            ModelRenderRegistrySFire.addItemToRegistry(crossbow, "crossbow_" + mat.material.getUnlocName());
            item_set.add(crossbow);
         }

         if (!ConfigHandler.disableJavelin) {
            Item javelin = SpartanWeaponryAPI.createJavelin(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(javelin, "javelin_" + mat.material.getUnlocName());
            item_set.add(javelin);
         }

         if (!ConfigHandler.disableBattleaxe) {
            Item battleaxe = SpartanWeaponryAPI.createBattleaxe(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(battleaxe, "battleaxe_" + mat.material.getUnlocName());
            item_set.add(battleaxe);
         }

         if (!ConfigHandler.disableBoomerang && !ConfigHandler.woodenBoomerangOnly) {
            Item boomerang = SpartanWeaponryAPI.createBoomerang(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(boomerang, "boomerang_" + mat.material.getUnlocName());
            item_set.add(boomerang);
         }

         if (!ConfigHandler.disableMace) {
            Item mace = SpartanWeaponryAPI.createMace(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(mace, "mace_" + mat.material.getUnlocName());
            item_set.add(mace);
         }

         if (!ConfigHandler.disableQuarterstaff) {
            Item quarterstaff = SpartanWeaponryAPI.createQuarterstaff(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(quarterstaff, "staff_" + mat.material.getUnlocName());
            item_set.add(quarterstaff);
         }

         if (!ConfigHandler.disableGlaive) {
            Item glaive = SpartanWeaponryAPI.createGlaive(mat.material, "spartanfire", IceAndFire.TAB, (WeaponProperty[])mat.properties.toArray(new WeaponProperty[0]));
            ModelRenderRegistrySFire.addItemToRegistry(glaive, "glaive_" + mat.material.getUnlocName());
            item_set.add(glaive);
         }

         if (ConfigHandler.enableExperimentalWeapons && !ConfigHandler.disableParryingDagger) {
            ;
         }
      }

      for(Item it : item_set) {
         ev.getRegistry().register(it);
      }

      Set var10000 = ALL_ITEMS;
      IForgeRegistry var10001 = ev.getRegistry();
      var10000.forEach(var10001::register);
   }

   @SubscribeEvent
   public static void registerRecipeEvent(Register<IRecipe> event) {
      if (ForgeConfigHandler.general.registerFlameIceThrowingRecipes) {
         event.getRegistry().register((new FireIceThrowingRecipes()).setRegistryName(new ResourceLocation("spartanfire", "fireice_throwing")));
      }

      if (ForgeConfigHandler.general.registerVenomThrowingRecipes) {
         event.getRegistry().register((new VenomThrowingRecipes()).setRegistryName(new ResourceLocation("spartanfire", "venom_throwing")));
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      ALL_ITEMS.stream().filter((item) -> {
         return item.getRegistryName() != null;
      }).forEach((item) -> {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      });
   }

   static {
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("dragonbone", Utils.spartanMatFromToolMat("dragonbone", ModItems.boneTools, 9867904, 14999238, "ingotDragonbone"), new WeaponProperty[0]));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("fire_dragonbone", Utils.spartanMatFromToolMat("fire_dragonbone", ModItems.fireBoneTools, 9867904, 14999238, "ingotDragonbone"), new WeaponProperty[]{new FireSwordWeaponProperty("fire_dragonbone", "spartanfire")}));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("ice_dragonbone", Utils.spartanMatFromToolMat("ice_dragonbone", ModItems.iceBoneTools, 9867904, 14999238, "ingotDragonbone"), new WeaponProperty[]{new IceSwordWeaponProperty("ice_dragonbone", "spartanfire")}));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("jungle", Utils.spartanMatFromToolMat("jungle", ModItems.myrmexChitin, 9867904, 14999238, "ingotJungleChitin"), new WeaponProperty[]{new MyrmexSwordProperty("jungle", "spartanfire")}));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("desert", Utils.spartanMatFromToolMat("desert", ModItems.myrmexChitin, 9867904, 14999238, "ingotDesertChitin"), new WeaponProperty[]{new MyrmexSwordProperty("desert", "spartanfire")}));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("jungle_venom", Utils.spartanMatFromToolMat("jungle_venom", ModItems.myrmexChitin, 9867904, 14999238, "ingotJungleChitin"), new WeaponProperty[]{new MyrmexSwordProperty("jungle", "spartanfire"), new MyrmexPoisonSwordProperty("jungle_venom", "spartanfire")}));
      MATERIALS_TO_REGISTER.add(new IAFMatConverter("desert_venom", Utils.spartanMatFromToolMat("desert_venom", ModItems.myrmexChitin, 9867904, 14999238, "ingotDesertChitin"), new WeaponProperty[]{new MyrmexSwordProperty("desert", "spartanfire"), new MyrmexPoisonSwordProperty("desert_venom", "spartanfire")}));
   }
}
