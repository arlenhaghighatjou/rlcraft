package svenhjol.charm.world.decorator.theme;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillageCarpenterTheme extends CharmDecoratorTheme {
   public VillageCarpenterTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      if (this.rare()) {
         states.add(Blocks.STICKY_PISTON.getDefaultState());
      }

      if (this.uncommon()) {
         states.add(Blocks.NOTEBLOCK.getDefaultState());
      }

      if (this.uncommon()) {
         states.add(Blocks.JUKEBOX.getDefaultState());
      }

      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.BOOKSHELF.getDefaultState());
      states.add(Blocks.PISTON.getDefaultState().withProperty(BlockPistonBase.FACING, EnumFacing.UP));
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.BOOKSHELF.getDefaultState());
      states.add(Blocks.DOUBLE_WOODEN_SLAB.getDefaultState());
      states.add(Blocks.PLANKS.getDefaultState());
      states.add(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.SPRUCE));
      states.add(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.JUNGLE));
      states.add(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.DARK_OAK));
      states.add(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.BIRCH));
      states.add(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.ACACIA));
      states.add(Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, EnumType.SPRUCE));
      states.add(Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, EnumType.JUNGLE));
      states.add(Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, EnumType.DARK_OAK));
      states.add(Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, EnumType.BIRCH));
      states.add(Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.VARIANT, EnumType.ACACIA));
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      if (this.common() && VillageDecorations.vanillaTools) {
         items.add(Items.IRON_AXE);
      }

      if (this.common() && VillageDecorations.vanillaTools) {
         items.add(Items.IRON_SHOVEL);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.STONE_AXE);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.STONE_SHOVEL);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.WOODEN_SHOVEL);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.WOODEN_AXE);
      }

      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      locations.add(CharmLootTables.VILLAGE_CARPENTER);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
