package knightminer.inspirations.plugins.tan;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
   id = "ToughAsNailsPlugin",
   description = "Adds support between TAN thirst and the cauldron overrides",
   modsRequired = "toughasnails",
   pulsesRequired = "InspirationsRecipes"
)
public class ToughAsNailsPlugin extends PulseBase {
   public static final String pulseID = "ToughAsNailsPlugin";
   @ObjectHolder("toughasnails:purified_water_bottle")
   public static final Item waterBottle = null;
   @ObjectHolder("toughasnails:charcoal_filter")
   public static final Item charcoalFilter = null;
   @ObjectHolder("toughasnails:fruit_juice")
   public static final Item fruitJuice = null;
   @ObjectHolder("toughasnails:canteen")
   public static final Item canteen = null;
   public static Fluid sweetenedWater;
   public static Fluid[] juices;

   public ToughAsNailsPlugin() {
   }

   @Subscribe
   public void preInit(FMLPreInitializationEvent event) {
      if (Config.enableExtendedCauldron) {
         if (Config.tanJuiceInCauldron) {
            sweetenedWater = registerColoredFluid("sweetened_water", -13259534);
            juices = new Fluid[]{registerColoredFluid("apple_juice", -279996), registerColoredFluid("beetroot_juice", -5631450), registerColoredFluid("cactus_juice", -8408259), registerColoredFluid("carrot_juice", -2792660), registerColoredFluid("chorus_fruit_juice", -6069837), registerColoredFluid("glistering_melon_juice", -47289), registerColoredFluid("golden_apple_juice", -25271), registerColoredFluid("golden_carrot_juice", -37290), registerColoredFluid("melon_juice", -3327949), registerColoredFluid("pumpkin_juice", -3242959)};
         }

      }
   }

   @Subscribe
   public void init(FMLInitializationEvent event) {
      if (Config.enableExtendedCauldron) {
         Fluid purifiedWater = FluidRegistry.getFluid("purified_water");
         if (purifiedWater != null) {
            InspirationsRegistry.addCauldronWater(purifiedWater);
            if (waterBottle != null) {
               InspirationsRegistry.addCauldronFluidItem(new ItemStack(waterBottle, 1), new ItemStack(Items.GLASS_BOTTLE), purifiedWater);
            }

            if (charcoalFilter != null) {
               InspirationsRegistry.addCauldronScaledTransformRecipe(new ItemStack(charcoalFilter), FluidRegistry.WATER, purifiedWater, (Boolean)null);
            }

            if (canteen != null) {
               for(int i = 0; i < 3; ++i) {
                  InspirationsRegistry.addCauldronRecipe(new FillCanteenRecipe(canteen, i, 1, FluidRegistry.WATER));
                  InspirationsRegistry.addCauldronRecipe(new FillCanteenRecipe(canteen, i, 2, purifiedWater));
               }

               InspirationsRegistry.addCauldronBlacklist(canteen, 32767);
            }

            if (Config.tanJuiceInCauldron && fruitJuice != null) {
               InspirationsRegistry.addCauldronScaledTransformRecipe(new ItemStack(Items.SUGAR), purifiedWater, sweetenedWater, (Boolean)null);
               Item[] items = new Item[]{Items.APPLE, Items.BEETROOT, Item.getItemFromBlock(Blocks.CACTUS), Items.CARROT, Items.CHORUS_FRUIT, Items.SPECKLED_MELON, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT, Items.MELON, Item.getItemFromBlock(Blocks.PUMPKIN)};

               for(int i = 0; i < items.length; ++i) {
                  addJuiceRecipe(juices[i], i, items[i]);
               }
            }
         } else {
            Inspirations.log.error("Unable to find Tough as Nails purified water fluid, skipping recipes");
         }

      }
   }

   private static void addJuiceRecipe(Fluid fluid, int meta, Item ingredient) {
      InspirationsRegistry.addCauldronScaledTransformRecipe(new ItemStack(ingredient), sweetenedWater, fluid, (Boolean)null);
      InspirationsRegistry.addCauldronFluidItem(new ItemStack(fruitJuice, 1, meta), new ItemStack(Items.GLASS_BOTTLE), fluid);
   }
}
