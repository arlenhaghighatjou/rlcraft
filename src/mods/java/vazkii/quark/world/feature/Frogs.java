package vazkii.quark.world.feature;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.ItemQuarkFood;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderFrog;
import vazkii.quark.world.entity.EntityFrog;

public class Frogs extends Feature {
   public static boolean frogsDoTheFunny;
   public static int weight;
   public static int min;
   public static int max;
   public static boolean jumpBoost;
   public static ItemQuarkFood frogLeg;
   public static ItemQuarkFood cookedFrogLeg;
   public static ItemQuarkFood gildedFrogLeg;

   public Frogs() {
   }

   public void setupConfig() {
      frogsDoTheFunny = this.loadPropBool("Frogs know what day it is", "", false);
      jumpBoost = this.loadPropBool("Frog legs can be made into jump boost reagent", "", true);
      weight = this.loadPropInt("Spawn Weight", "The higher, the more will spawn", 40);
      min = this.loadPropInt("Smallest spawn group", "", 1);
      max = this.loadPropInt("Largest spawn group", "", 3);
   }

   public void preInit(FMLPreInitializationEvent event) {
      frogLeg = new ItemQuarkFood("frog_leg", 2, 0.3F, true, new String[0]);
      cookedFrogLeg = new ItemQuarkFood("cooked_frog_leg", 4, 1.25F, true, new String[0]);
      if (jumpBoost) {
         gildedFrogLeg = new ItemQuarkFood("golden_frog_leg", 4, 2.5F, new String[0]);
         gildedFrogLeg.setCreativeTab(CreativeTabs.BREWING);
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(gildedFrogLeg), new Object[]{"GGG", "GLG", "GGG", 'G', "nuggetGold", 'L', ProxyRegistry.newStack(frogLeg)});
         PotionHelper.addMix(PotionTypes.WATER, gildedFrogLeg, PotionTypes.MUNDANE);
         PotionHelper.addMix(PotionTypes.AWKWARD, gildedFrogLeg, PotionTypes.LEAPING);
      }

      String frogName = "quark:frog";
      LootTableList.register(EntityFrog.FROG_LOOT_TABLE);
      EntityRegistry.registerModEntity(new ResourceLocation(frogName), EntityFrog.class, frogName, 18, Quark.instance, 80, 3, true, 12359785, 16770733);
   }

   public void init() {
      EntityRegistry.addSpawn(EntityFrog.class, weight, min, max, EnumCreatureType.CREATURE, new Biome[]{Biomes.SWAMPLAND});
   }

   public void postInit() {
      FurnaceRecipes.instance().addSmelting(frogLeg, new ItemStack(cookedFrogLeg), 0.35F);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, RenderFrog.FACTORY);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
