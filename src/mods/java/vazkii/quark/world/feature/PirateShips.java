package vazkii.quark.world.feature;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderPirate;
import vazkii.quark.world.entity.EntityPirate;
import vazkii.quark.world.item.ItemPirateHat;
import vazkii.quark.world.world.PirateShipGenerator;

public class PirateShips extends Feature {
   public static final ResourceLocation PIRATE_CHEST_LOOT_TABLE = new ResourceLocation("quark", "chests/pirate_chest");
   public static final ResourceLocation SHIP_STRUCTURE = new ResourceLocation("quark", "pirate_ship");
   public static Item pirate_hat;
   public static boolean onlyHat;
   public static int rarity;
   public static DimensionConfig dims;

   public PirateShips() {
   }

   public void setupConfig() {
      onlyHat = this.loadPropBool("Only hat", "Disables the pirate mob and generator, only adds the hat", false);
      rarity = this.loadPropInt("Pirate Ship Rarity", "Given this value as X, 1 ship will spawn in X ocean biome chunks", 4000);
      dims = new DimensionConfig(this.configCategory);
   }

   public void preInit(FMLPreInitializationEvent event) {
      pirate_hat = new ItemPirateHat();
      if (!onlyHat) {
         String pirateName = "quark:pirate";
         EntityRegistry.registerModEntity(new ResourceLocation(pirateName), EntityPirate.class, pirateName, 3, Quark.instance, 80, 3, true, 5053716, 11310615);
         GameRegistry.registerWorldGenerator(new PirateShipGenerator(dims), 0);
         LootTableList.register(PIRATE_CHEST_LOOT_TABLE);
      }
   }

   public void preInitClient() {
      if (!onlyHat) {
         RenderingRegistry.registerEntityRenderingHandler(EntityPirate.class, RenderPirate.FACTORY);
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
