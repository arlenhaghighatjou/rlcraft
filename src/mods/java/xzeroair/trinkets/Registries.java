package xzeroair.trinkets;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import xzeroair.trinkets.init.ModBlocks;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.init.ModPotionTypes;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.traits.elements.Element;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

@EventBusSubscriber(
   modid = "xat"
)
public class Registries {
   private static final ResourceLocation ELEMENTS_ID = new ResourceLocation("xat", "elements");
   private static final ResourceLocation RACES_ID = new ResourceLocation("xat", "races");
   private static ForgeRegistry<Element> elementRegistry;
   private static ForgeRegistry<EntityRace> raceRegistry;

   public Registries() {
   }

   @SubscribeEvent
   public static void registerNewRegistry(NewRegistry event) {
      RegistryBuilder<Element> elementReg = new RegistryBuilder();
      ForgeRegistry<Element> elements = (ForgeRegistry)elementReg.setType(Element.class).allowModification().setName(ELEMENTS_ID).create();
      elementRegistry = elements;
      RegistryBuilder<EntityRace> racesReg = new RegistryBuilder();
      ForgeRegistry<EntityRace> races = (ForgeRegistry)racesReg.setType(EntityRace.class).allowModification().setName(RACES_ID).create();
      raceRegistry = races;
   }

   public static ForgeRegistry<Element> getElementRegistry() {
      return elementRegistry;
   }

   public static ForgeRegistry<EntityRace> getRaceRegistry() {
      return raceRegistry;
   }

   @SubscribeEvent
   public static void registerItem(Register<Item> event) {
      Trinkets.log.info("Registering Crafting");
      event.getRegistry().registerAll((IForgeRegistryEntry[])ModItems.crafting.ITEMS.toArray(new Item[0]));
      if (TrinketsConfig.SERVER.Food.foods_enabled) {
         Trinkets.log.info("Registering Foods");
         event.getRegistry().registerAll((IForgeRegistryEntry[])ModItems.foods.ITEMS.toArray(new Item[0]));
      }

      Trinkets.log.info("Registering Misc");
      event.getRegistry().registerAll((IForgeRegistryEntry[])ModItems.misc.ITEMS.toArray(new Item[0]));
      if (Trinkets.Baubles && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly) {
         Trinkets.log.info("Registering Baubles");

         for(Item item : ModItems.baubles.ITEMS) {
            if (item instanceof IAccessoryInterface && ((IAccessoryInterface)item).ItemEnabled()) {
               event.getRegistry().register(item);
            }
         }
      } else {
         Trinkets.log.info("Registering Trinkets");

         for(Item item : ModItems.trinkets.ITEMS) {
            if (item instanceof IAccessoryInterface && ((IAccessoryInterface)item).ItemEnabled()) {
               event.getRegistry().register(item);
            }
         }

         Trinkets.log.info("Registering Race Trinkets");

         for(Item item : ModItems.RaceTrinkets.ITEMS) {
            if (item instanceof IAccessoryInterface && ((IAccessoryInterface)item).ItemEnabled()) {
               event.getRegistry().register(item);
            }
         }
      }

   }

   @SubscribeEvent
   public static void registerBlock(Register<Block> event) {
      Trinkets.log.info("Registering Flower Blocks");
      event.getRegistry().registerAll((IForgeRegistryEntry[])ModBlocks.Flowers.BLOCKS.toArray(new Block[0]));
      Trinkets.log.info("Registering Placeable Blocks");
      event.getRegistry().registerAll((IForgeRegistryEntry[])ModBlocks.Placeables.BLOCKS.toArray(new Block[0]));
   }

   @SubscribeEvent
   public static void registerPotion(Register<PotionType> event) {
      if (TrinketsConfig.SERVER.Potion.potions_enabled) {
         Trinkets.log.info("Registering Potions");
         ModPotionTypes.registerPotionTypes();
      }

   }

   @SubscribeEvent
   public static void registerModel(ModelRegistryEvent event) {
      for(Item item : ModItems.misc.ITEMS) {
         if (item instanceof IsModelLoaded) {
            ((IsModelLoaded)item).registerModels();
         }
      }

      for(Item item : ModItems.crafting.ITEMS) {
         if (item instanceof IsModelLoaded) {
            ((IsModelLoaded)item).registerModels();
         }
      }

      for(Block block : ModBlocks.Flowers.BLOCKS) {
         if (block instanceof IsModelLoaded) {
            ((IsModelLoaded)block).registerModels();
         }
      }

      for(Block block : ModBlocks.Placeables.BLOCKS) {
         if (block instanceof IsModelLoaded) {
            ((IsModelLoaded)block).registerModels();
         }
      }

      if (TrinketsConfig.SERVER.Food.foods_enabled) {
         for(Item item : ModItems.foods.ITEMS) {
            if (item instanceof IsModelLoaded) {
               ((IsModelLoaded)item).registerModels();
            }
         }
      }

      if (Trinkets.Baubles && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly) {
         for(Item item : ModItems.baubles.ITEMS) {
            if (item instanceof IsModelLoaded) {
               ((IsModelLoaded)item).registerModels();
            }
         }
      } else {
         for(Item item : ModItems.trinkets.ITEMS) {
            if (item instanceof IsModelLoaded) {
               ((IsModelLoaded)item).registerModels();
            }
         }

         for(Item item : ModItems.RaceTrinkets.ITEMS) {
            if (item instanceof IsModelLoaded) {
               ((IsModelLoaded)item).registerModels();
            }
         }
      }

   }
}
