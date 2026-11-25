package com.oblivioussp.spartanshields.init;

import com.oblivioussp.spartanshields.item.ItemShieldExtraMaterial;
import com.oblivioussp.spartanshields.item.crafting.RecipeShieldBanners;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import com.oblivioussp.spartanshields.util.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber
public class RecipeRegistry {
   public RecipeRegistry() {
   }

   @SubscribeEvent
   public static void addRecipes(Register<IRecipe> ev) {
      IForgeRegistry<IRecipe> reg = ev.getRegistry();
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerWood)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_wood")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerStone)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_stone")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerIron)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_iron")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerGold)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_gold")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerDiamond)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_diamond")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerObsidian)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_obsidian")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerBronze)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_bronze")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerSteel)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_steel")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerCopper)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_copper")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerTin)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_tin")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerSilver)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_silver")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerInvar)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_invar")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerPlatinum)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_platinum")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerElectrum)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_electrum")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerNickel)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_nickel")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerLead)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_lead")));
      reg.register((new RecipeShieldBanners(ItemRegistrySS.shieldTowerConstantan)).setRegistryName(new ResourceLocation("spartanshields", "shield_banner_constantan")));
      LogHelper.info("Recipes added!");
   }

   public static void addCompatRecipes() {
      if (!ConfigHandler.vanillaOnly) {
         ;
      }
   }

   public static void addRecipe(IForgeRegistry<IRecipe> registry, String recipePath, Item result, Object... input) {
      ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation("recipePath"), result, input);
      recipe.setRegistryName(recipePath);
      registry.register(recipe);
   }

   public static void addRecipe(IForgeRegistry<IRecipe> registry, String recipePath, ItemStack result, Object... input) {
      ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation("recipePath"), result, input);
      recipe.setRegistryName(recipePath);
      registry.register(recipe);
   }

   public static boolean addShieldRecipeIfValid(IForgeRegistry<IRecipe> registry, String recipePath, ItemShieldExtraMaterial shield, String oreDict, boolean enabledFlag) {
      addRecipe(registry, recipePath, shield, " i ", "isi", " i ", 'i', oreDict, 's', ItemRegistrySS.shieldWood);
      return true;
   }
}
