package svenhjol.charm.world.decorator.theme;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillagePriestTheme extends CharmDecoratorTheme {
   public VillagePriestTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getStorageBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(super.getStorageBlock());
      if (this.valuable()) {
         states.add(Blocks.ENDER_CHEST.getDefaultState());
      }

      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      if (this.valuable()) {
         states.add(Blocks.ENCHANTING_TABLE.getDefaultState());
      }

      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.BOOKSHELF.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      if (this.valuable()) {
         states.add(Blocks.QUARTZ_BLOCK.getDefaultState());
      }

      if (this.valuable()) {
         states.add(Blocks.LAPIS_BLOCK.getDefaultState());
      }

      if (this.common()) {
         states.add(Blocks.OBSIDIAN.getDefaultState());
      }

      states.add(Blocks.BOOKSHELF.getDefaultState());
      states.add(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.BLUE_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.LIME_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.GREEN_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.RED_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.PURPLE_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.ORANGE_GLAZED_TERRACOTTA.getDefaultState());
      states.add(Blocks.YELLOW_GLAZED_TERRACOTTA.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      if (this.valuable()) {
         items.add(Items.CHORUS_FRUIT);
      }

      if (this.valuable()) {
         items.add(Items.DIAMOND);
      }

      if (this.valuable()) {
         items.add(Items.ENDER_PEARL);
      }

      if (this.uncommon()) {
         items.add(Items.EXPERIENCE_BOTTLE);
      }

      if (this.uncommon()) {
         items.add(Items.BLAZE_POWDER);
      }

      if (this.common()) {
         items.add(Items.GOLD_INGOT);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.GOLDEN_AXE);
      }

      if (VillageDecorations.vanillaTools) {
         items.add(Items.GOLDEN_SHOVEL);
      }

      items.add(Items.GLASS_BOTTLE);
      items.add(Items.CLOCK);
      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      if (this.valuable()) {
         locations.add(LootTableList.CHESTS_STRONGHOLD_CROSSING);
      }

      locations.add(CharmLootTables.VILLAGE_PRIEST);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
