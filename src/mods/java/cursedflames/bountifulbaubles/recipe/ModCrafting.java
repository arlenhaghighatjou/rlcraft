package cursedflames.bountifulbaubles.recipe;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.ModConfig;
import cursedflames.bountifulbaubles.block.ModBlocks;
import cursedflames.bountifulbaubles.item.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class ModCrafting {
   public ModCrafting() {
   }

   private static boolean doesOreExist(String oreName) {
      return !OreDictionary.getOres(oreName, false).isEmpty();
   }

   @SubscribeEvent
   public static void registerRecipes(Register<IRecipe> event) {
      IForgeRegistry<IRecipe> r = event.getRegistry();
      boolean copperIngot = doesOreExist("ingotCopper");
      boolean steelIngot = doesOreExist("ingotSteel");
      boolean ingotEnderium = doesOreExist("ingotEnderium");
      boolean ingotCobalt = doesOreExist("ingotCobalt");
      boolean runeMana = doesOreExist("runeManaB");
      boolean runeFire = doesOreExist("runeFireB");
      boolean runeWater = doesOreExist("runeWaterB");
      boolean runeAir = doesOreExist("runeAirB");
      boolean runeEarth = doesOreExist("runeEarthB");
      boolean runeSummer = doesOreExist("runeSummerB");
      boolean runeAutumn = doesOreExist("runeAutumnB");
      boolean runeWinter = doesOreExist("runeWinterB");
      boolean runeSpring = doesOreExist("runeSpringB");
      boolean runeGluttony = doesOreExist("runeGluttonyB");
      boolean runePride = doesOreExist("runePrideB");
      boolean runeWrath = doesOreExist("runeWrathB");
      boolean runeGreed = doesOreExist("runeGreedB");
      boolean runeEnvy = doesOreExist("runeEnvyB");
      boolean runeSloth = doesOreExist("runeSlothB");
      boolean runeLust = doesOreExist("runeLustB");
      boolean gaiaSpirit = doesOreExist("eternalLifeEssence");
      boolean ruby = doesOreExist("gemRuby");
      if (ModConfig.recipesEnabled.getBoolean(true)) {
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "ringiron"), ModItems.ironRing, new Object[]{new String[]{" i ", "i i", " i "}, 'i', "ingotIron"})).setRegistryName(new ResourceLocation("bountifulbaubles", "ringiron")));
         if (runeSummer && runeAutumn && runeWinter && runeSpring) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "trinketankhcharm"), ModItems.trinketAnkhCharm, new Object[]{new String[]{"SlA", "fsa", "PvW"}, 's', ModItems.trinketMixedDragonScale, 'f', ModItems.ringFreeAction, 'a', ModItems.trinketApple, 'v', ModItems.trinketVitamins, 'l', ModItems.trinketMagicLenses, 'S', "runeSummerB", 'A', "runeAutumnB", 'W', "runeWinterB", 'P', "runeSpringB"})).setRegistryName(new ResourceLocation("bountifulbaubles", "trinketankhcharm")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "trinketankhcharm"), ModItems.trinketAnkhCharm, new Object[]{new String[]{"glg", "fsa", "gvg"}, 's', ModItems.trinketMixedDragonScale, 'f', ModItems.ringFreeAction, 'a', ModItems.trinketApple, 'v', ModItems.trinketVitamins, 'l', ModItems.trinketMagicLenses, 'g', "ingotGold"})).setRegistryName(new ResourceLocation("bountifulbaubles", "trinketankhcharm")));
         }

         r.register((new ShapelessOreRecipe(new ResourceLocation("bountifulbaubles", "reforger"), ModBlocks.reforger, new Object[]{Item.getItemFromBlock(Blocks.CRAFTING_TABLE), Item.getItemFromBlock(Blocks.ANVIL), Items.LAVA_BUCKET})).setRegistryName(new ResourceLocation("bountifulbaubles", "reforger")));
         NBTTagCompound temp = new NBTTagCompound();
         temp.setString("Potion", "minecraft:fire_resistance");
         ItemStack potionFireRes = new ItemStack(Items.POTIONITEM);
         potionFireRes.setTagCompound(temp.copy());
         ItemStack potionFireResSplash = new ItemStack(Items.SPLASH_POTION);
         potionFireResSplash.setTagCompound(temp.copy());
         ItemStack potionFireResLinger = new ItemStack(Items.LINGERING_POTION);
         potionFireResLinger.setTagCompound(temp.copy());
         temp.setString("Potion", "minecraft:long_fire_resistance");
         ItemStack potionFireResLong = new ItemStack(Items.POTIONITEM);
         potionFireResLong.setTagCompound(temp.copy());
         ItemStack potionFireResSplashLong = new ItemStack(Items.SPLASH_POTION);
         potionFireResSplashLong.setTagCompound(temp.copy());
         ItemStack potionFireResLingerLong = new ItemStack(Items.LINGERING_POTION);
         potionFireResLingerLong.setTagCompound(temp.copy());
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "trinketobsidianskull"), ModItems.trinketObsidianSkull, new Object[]{new String[]{"oro", "psp", "odo"}, 'o', Item.getItemFromBlock(Blocks.OBSIDIAN), 'r', runeFire ? "runeFireB" : Items.BLAZE_POWDER, 'd', Items.BLAZE_POWDER, 's', new CLIngredient(new ItemStack[]{new ItemStack(Items.SKULL, 1, 0), new ItemStack(Items.SKULL, 1, 1)}), 'p', new CLIngredient(new ItemStack[]{potionFireRes, potionFireResSplash, potionFireResLinger, potionFireResLong, potionFireResSplashLong, potionFireResLingerLong})})).setRegistryName(new ResourceLocation("bountifulbaubles", "trinketobsidianskull")));
         if (gaiaSpirit) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "trinketblackdragonscale"), ModItems.trinketBlackDragonScale, new Object[]{new String[]{" e ", "nbg"}, 'n', Items.NETHER_STAR, 'e', "scaleDragonEnder", 'b', ModItems.brokenBlackDragonScale, 'g', "eternalLifeEssence"})).setRegistryName(new ResourceLocation("bountifulbaubles", "trinketblackdragonscale")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "trinketblackdragonscale"), ModItems.trinketBlackDragonScale, new Object[]{new String[]{"n", "e", "b"}, 'n', Items.NETHER_STAR, 'e', "scaleDragonEnder", 'b', ModItems.brokenBlackDragonScale})).setRegistryName(new ResourceLocation("bountifulbaubles", "trinketblackdragonscale")));
         }

         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "ringflywheel"), ModItems.ringFlywheel, new Object[]{new String[]{"scs", "crc", "scs"}, 'c', copperIngot ? "ingotCopper" : "ingotGold", 's', steelIngot ? "ingotSteel" : "ingotIron", 'r', "ringIron"})).setRegistryName(new ResourceLocation("bountifulbaubles", "ringflywheel")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "ringflywheeladvanced"), ModItems.ringFlywheelAdvanced, new Object[]{new String[]{" s ", "iri", " e "}, 'i', ingotEnderium ? "ingotEnderium" : Items.ENDER_PEARL, 'e', Items.ENDER_EYE, 's', "scaleDragonEnder", 'r', ModItems.ringFlywheel})).setRegistryName(new ResourceLocation("bountifulbaubles", "ringflywheeladvanced")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "amuletSinGluttony"), ModItems.sinPendantGluttony, new Object[]{new String[]{"c", "a", "g"}, 'c', runeGluttony ? "runeGluttonyB" : Items.CAKE, 'a', ModItems.sinPendantEmpty, 'g', new ItemStack(Items.GOLDEN_APPLE, 1, 0)})).setRegistryName(new ResourceLocation("bountifulbaubles", "amuletSinGluttony")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "crownGold"), ModItems.crownGold, new Object[]{new String[]{" g ", "iii", "i i"}, 'i', "ingotGold", 'g', ruby ? "gemRuby" : "gemDiamond"})).setRegistryName(new ResourceLocation("bountifulbaubles", "crownGold")));
         if (runePride) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "amuletSinPride"), ModItems.sinPendantPride, new Object[]{new String[]{"r", "a", "c"}, 'r', "runePrideB", 'c', ModItems.crownGold, 'a', ModItems.sinPendantEmpty})).setRegistryName(new ResourceLocation("bountifulbaubles", "amuletSinPride")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "amuletSinPride"), ModItems.sinPendantPride, new Object[]{new String[]{"a", "c"}, 'c', ModItems.crownGold, 'a', ModItems.sinPendantEmpty})).setRegistryName(new ResourceLocation("bountifulbaubles", "amuletSinPride")));
         }

         if (runeWrath) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "amuletSinWrath"), ModItems.sinPendantWrath, new Object[]{new String[]{"r", "a", "h"}, 'r', "runeWrathB", 'h', Items.SKULL, 'a', ModItems.sinPendantEmpty})).setRegistryName(new ResourceLocation("bountifulbaubles", "amuletSinWrath")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "amuletSinWrath"), ModItems.sinPendantWrath, new Object[]{new String[]{" b ", "bab", " h "}, 'h', Items.SKULL, 'b', Items.BONE, 'a', ModItems.sinPendantEmpty})).setRegistryName(new ResourceLocation("bountifulbaubles", "amuletSinWrath")));
         }

         if (ingotEnderium) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "wormholeMirror"), ModItems.wormholeMirror, new Object[]{new String[]{"iei", "pmp", "ipi"}, 'e', Items.ENDER_EYE, 'm', ModItems.magicMirror, 'p', ModItems.potionWormhole, 'i', "ingotEnderium"})).setRegistryName(new ResourceLocation("bountifulbaubles", "wormholeMirror")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "wormholeMirror"), ModItems.wormholeMirror, new Object[]{new String[]{" e ", "pmp", " p "}, 'e', Items.ENDER_EYE, 'm', ModItems.magicMirror, 'p', ModItems.potionWormhole})).setRegistryName(new ResourceLocation("bountifulbaubles", "wormholeMirror")));
         }

         if (!BountifulBaubles.isBotaniaLoaded) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "phantomPrism"), ModItems.phantomPrism, new Object[]{new String[]{" e ", "gdg", "gog"}, 'e', Items.ENDER_EYE, 'd', Items.DIAMOND, 'o', "obsidian", 'g', "blockGlass"})).setRegistryName(new ResourceLocation("bountifulbaubles", "phantomPrism")));
         }

         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "flare_red"), new ItemStack(ModItems.flareRed, 8), new Object[]{new String[]{"i", "g", "G"}, 'i', "ingotIron", 'g', Items.GUNPOWDER, 'G', Items.GLOWSTONE_DUST})).setRegistryName(new ResourceLocation("bountifulbaubles", "flare_red")));
      }

      if (ModConfig.brewingRecipesEnabled.getBoolean(true)) {
         NBTTagCompound temp = new NBTTagCompound();
         temp.setString("Potion", "minecraft:mundane");
         ItemStack mundanePotion = new ItemStack(Items.POTIONITEM);
         mundanePotion.setTagCompound(temp.copy());
         BrewingRecipeRegistry.addRecipe(mundanePotion, new ItemStack(Items.QUARTZ), new ItemStack(ModItems.potionRecall));
         BrewingRecipeRegistry.addRecipe(new ItemStack(ModItems.potionRecall), new ItemStack(Items.ENDER_PEARL), new ItemStack(ModItems.potionWormhole));
      }

      if (ModConfig.spectralSiltRecipesEnabled.getBoolean(true)) {
         if (runeAir) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_balloon"), ModItems.balloon, new Object[]{new String[]{"sws", "wRw", "sws"}, 's', ModItems.spectralSilt, 'w', "blockWool", 'R', "runeAirB"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_balloon")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_balloon"), ModItems.balloon, new Object[]{new String[]{"sws", "wsw", "sws"}, 's', ModItems.spectralSilt, 'w', "blockWool"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_balloon")));
         }

         if (ingotCobalt) {
            if (runeEarth) {
               r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt"), ModItems.shieldCobalt, new Object[]{new String[]{"sis", "iSi", "sRs"}, 's', ModItems.spectralSilt, 'S', Items.SHIELD, 'i', "ingotCobalt", 'R', "runeEarthB"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt")));
            } else {
               r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt"), ModItems.shieldCobalt, new Object[]{new String[]{"sis", "iSi", "sis"}, 's', ModItems.spectralSilt, 'S', Items.SHIELD, 'i', "ingotCobalt"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt")));
            }
         } else if (runeEarth) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt"), ModItems.shieldCobalt, new Object[]{new String[]{"sss", "sSs", "sRs"}, 's', ModItems.spectralSilt, 'S', Items.SHIELD, 'R', "runeEarthB"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt"), ModItems.shieldCobalt, new Object[]{new String[]{"sss", "sSs", "sss"}, 's', ModItems.spectralSilt, 'S', Items.SHIELD})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_shieldCobalt")));
         }

         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_magicMirror"), ModItems.magicMirror, new Object[]{new String[]{"sps", "gdg", "sps"}, 's', ModItems.spectralSilt, 'd', Items.DIAMOND, 'g', "blockGlass", 'p', ModItems.potionRecall})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_magicMirror")));
         if (runeEarth && runeAir) {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_luckyHorseshoe"), ModItems.trinketLuckyHorseshoe, new Object[]{new String[]{"gsg", "EsA", "sgs"}, 's', ModItems.spectralSilt, 'g', "ingotGold", 'E', "runeEarthB", 'A', "runeAirB"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_luckyHorseshoe")));
         } else {
            r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_luckyHorseshoe"), ModItems.trinketLuckyHorseshoe, new Object[]{new String[]{"gsg", "gsg", "sgs"}, 's', ModItems.spectralSilt, 'g', "ingotGold"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_luckyHorseshoe")));
         }

         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_magicLenses"), ModItems.trinketMagicLenses, new Object[]{new String[]{"s s", "gSg", "s s"}, 's', ModItems.spectralSilt, 'g', "blockGlass", 'S', Items.STICK})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_magicLenses")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_amuletCross"), ModItems.amuletCross, new Object[]{new String[]{"sgs", "ggg", "sgs"}, 's', ModItems.spectralSilt, 'g', "ingotGold"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_amuletCross")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_sinPendantEmpty"), ModItems.sinPendantEmpty, new Object[]{new String[]{"sSs", "sis", "sss"}, 's', ModItems.spectralSilt, 'i', "ingotIron", 'S', Items.STRING})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_sinPendantEmpty")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_flareGun"), ModItems.flareGun, new Object[]{new String[]{"sss", "iii", "iss"}, 's', ModItems.spectralSilt, 'i', "ingotIron"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_flareGun")));
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_brokenBlackDragonScale"), ModItems.brokenBlackDragonScale, new Object[]{new String[]{"sss", "sSs", "sss"}, 's', ModItems.spectralSilt, 'S', "scaleDragonEnder"})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_brokenBlackDragonScale")));
      }

      if (ModConfig.spectralSiltEnabled.getBoolean(true)) {
         r.register((new ShapedOreRecipe(new ResourceLocation("bountifulbaubles", "spectralSilt_disintegrationTablet"), ModItems.disintegrationTablet, new Object[]{new String[]{"qbq", "brb", "qbq"}, 'r', Items.REDSTONE, 'b', Items.BLAZE_POWDER, 'q', Items.QUARTZ})).setRegistryName(new ResourceLocation("bountifulbaubles", "spectralSilt_disintegrationTablet")));
         addDisintegrationRecipe(r, ModItems.trinketBrokenHeart);
         addDisintegrationRecipe(r, ModItems.balloon);
         addDisintegrationRecipe(r, ModItems.shieldCobalt);
         addDisintegrationRecipe(r, ModItems.shieldObsidian);
         addDisintegrationRecipe(r, ModItems.magicMirror);
         addDisintegrationRecipe(r, ModItems.wormholeMirror);
         addDisintegrationRecipe(r, ModItems.trinketLuckyHorseshoe);
         addDisintegrationRecipe(r, ModItems.trinketMagicLenses);
         addDisintegrationRecipe(r, ModItems.amuletCross);
         addDisintegrationRecipe(r, ModItems.sinPendantEmpty);
         addDisintegrationRecipe(r, ModItems.flareGun);
         addDisintegrationRecipe(r, ModItems.brokenBlackDragonScale);
      }

   }

   private static void addDisintegrationRecipe(IForgeRegistry<IRecipe> r, Item item) {
      addDisintegrationRecipe(r, item, 1);
   }

   private static void addDisintegrationRecipe(IForgeRegistry<IRecipe> r, Item item, int amt) {
      r.register((new ShapelessOreRecipe(new ResourceLocation("bountifulbaubles", "disintegrate_" + item.getUnlocalizedName()), new ItemStack(ModItems.spectralSilt, amt), new Object[]{item, ModItems.disintegrationTablet})).setRegistryName(new ResourceLocation("bountifulbaubles", "disintegrate_" + item.getUnlocalizedName())));
   }
}
