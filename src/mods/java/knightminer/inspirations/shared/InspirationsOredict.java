package knightminer.inspirations.shared;

import slimeknights.mantle.pulsar.pulse.IPulse;
import javax.annotation.Nonnull;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

public class InspirationsOredict implements IPulse {
   public void handlePulseEvent(Object evt) {
      if (evt instanceof FMLInitializationEvent) {
         this.doTheOredict((FMLInitializationEvent)evt);
      }

   }

   private static final String[] DYES = new String[]{"White", "Orange", "Magenta", "LightBlue", "Yellow", "Lime", "Pink", "Gray", "LightGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};

   public InspirationsOredict() {
   }

   public void doTheOredict(FMLInitializationEvent event) {
      this.ensureVanilla();
      registerBuilding();
      registerRecipes();
      this.registerShared();
      registerUtility();
   }

   private void ensureVanilla() {
      oredict(Items.BOOK, "book");
      oredict(Blocks.TRAPDOOR, "trapdoorWood");
      oredict(Blocks.RED_MUSHROOM, "mushroomAny");
      oredict(Blocks.BROWN_MUSHROOM, "mushroomAny");
   }

   private void registerShared() {
      oredict(InspirationsShared.witherBone, "boneWithered");
      oredict(InspirationsShared.stoneRod, "rodStone");
   }

   private static void registerBuilding() {
      oredict(InspirationsBuilding.books, "book");
   }

   private static void registerRecipes() {
      for(EnumDyeColor color : EnumDyeColor.values()) {
         oredict(InspirationsRecipes.dyedWaterBottle, color.getDyeDamage(), dyeNameFor(color));
      }

      oredict(InspirationsShared.splashBottle, "bottleSplash");
      oredict(InspirationsShared.lingeringBottle, "bottleLingering");
   }

   private static void registerUtility() {
      if (InspirationsUtility.carpetedTrapdoors != null) {
         for(Block block : InspirationsUtility.carpetedTrapdoors) {
            oredict(block, "trapdoorCarpeted");
         }
      }

   }

   public static String dyeNameFor(@Nonnull EnumDyeColor color) {
      return "dye" + DYES[color.getMetadata()];
   }

   public static void oredict(Item item, String... name) {
      oredict(item, 32767, name);
   }

   public static void oredict(Block block, String... name) {
      oredict(block, 32767, name);
   }

   public static void oredict(Item item, int meta, String... name) {
      oredict(new ItemStack(item, 1, meta), name);
   }

   public static void oredict(Block block, int meta, String... name) {
      oredict(new ItemStack(block, 1, meta), name);
   }

   public static void oredict(ItemStack stack, String... names) {
      if (stack != null && !stack.isEmpty()) {
         for(String name : names) {
            OreDictionary.registerOre(name, stack);
         }
      }

   }
}
