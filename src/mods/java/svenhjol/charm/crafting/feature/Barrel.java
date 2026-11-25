package svenhjol.charm.crafting.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import svenhjol.charm.crafting.block.BlockBarrel;
import svenhjol.meson.Feature;
import svenhjol.meson.MesonBlock;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.helper.ForgeHelper;
import svenhjol.meson.helper.LootHelper;
import svenhjol.meson.registry.ProxyRegistry;

public class Barrel extends Feature {
   public static BlockBarrel block;
   public static float hardness;
   public static boolean useCharmBarrels;
   public static Map<Barrel.RARITY, List<Barrel.BarrelType>> types = new HashMap();

   public Barrel() {
   }

   public String getDescription() {
      return "A compact storage solution borrowed lovingly from Minecraft 1.14.";
   }

   public boolean isEnabled() {
      return this.enabled && (!ForgeHelper.areModsLoaded("futuremc") || useCharmBarrels);
   }

   public void configure() {
      super.configure();
      useCharmBarrels = this.propBoolean("Use Charm barrels", "Charm's barrels will be enabled even if barrels from other mods are present.", true);
      hardness = 2.5F;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockBarrel();
      GameRegistry.registerTileEntity(block.getTileEntityClass(), new ResourceLocation("charm:barrel"));
      Map<Barrel.RARITY, List<ResourceLocation>> map = new HashMap<Barrel.RARITY, List<ResourceLocation>>() {
         {
            this.put(Barrel.RARITY.COMMON, LootHelper.getLootTables(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC));
            this.put(Barrel.RARITY.UNCOMMON, LootHelper.getLootTables(LootHelper.RARITY.UNCOMMON, LootHelper.TYPE.MISC));
         }
      };

      for(Barrel.RARITY rarity : map.keySet()) {
         List<ResourceLocation> tables = (List)map.get(rarity);
         List<Barrel.BarrelType> barrels = new ArrayList();

         for(ResourceLocation res : tables) {
            String[] a = res.getResourcePath().split("/");
            barrels.add(new Barrel.BarrelType(a.length > 1 ? a[1] : a[0], res.toString()));
         }

         types.put(rarity, barrels);
      }

      if (!ForgeHelper.areModsLoaded("futuremc")) {
         for(int i = 0; i < MesonBlock.WoodVariant.values().length; ++i) {
            RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1, i), "WSW", "W W", "WSW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i), 'S', ProxyRegistry.newStack(Blocks.WOODEN_SLAB, 1, i));
         }
      } else {
         for(int i = 0; i < MesonBlock.WoodVariant.values().length; ++i) {
            RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1, i), "WWW", "S S", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i), 'S', ProxyRegistry.newStack(Blocks.WOODEN_SLAB, 1, i));
         }
      }

   }

   public void postInit(FMLPostInitializationEvent event) {
      OreDictionary.registerOre("barrel", ProxyRegistry.newStack(block, 1, 32767));
   }

   public static Barrel.BarrelType getRandomBarrelType(Barrel.RARITY r) {
      EaglercraftRandom rand = new EaglercraftRandom();
      List<Barrel.BarrelType> t = (List)types.get(r);
      return (Barrel.BarrelType)t.get(rand.nextInt(types.size()));
   }

   public class BarrelType {
      public String name;
      public String id;
      public ResourceLocation pool;

      public BarrelType(String id, String pool) {
         this.id = id;
         this.pool = new ResourceLocation(pool);
      }
   }

   public static enum RARITY {
      COMMON,
      UNCOMMON;

      private RARITY() {
      }
   }
}
