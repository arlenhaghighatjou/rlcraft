package svenhjol.charm.world.decorator.theme;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillageButcherTheme extends CharmDecoratorTheme {
   public VillageButcherTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      states.add(Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(this.getRand().nextInt(3))));
      states.add(Blocks.HOPPER.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE_SMOOTH));
      states.add(Blocks.COAL_BLOCK.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      if (this.valuable()) {
         items.add(Items.SADDLE);
      }

      if (this.valuable()) {
         items.add(Items.RABBIT_FOOT);
      }

      if (this.uncommon() && VillageDecorations.vanillaTools) {
         items.add(Items.IRON_AXE);
      }

      if (this.uncommon() && VillageDecorations.vanillaTools) {
         items.add(Items.IRON_SWORD);
      }

      if (this.common()) {
         items.add(Items.LEATHER_HELMET);
      }

      if (this.common()) {
         items.add(Items.LEATHER_CHESTPLATE);
      }

      if (this.common()) {
         items.add(Items.LEATHER_LEGGINGS);
      }

      if (this.common()) {
         items.add(Items.LEATHER_BOOTS);
      }

      items.add(Items.LEATHER);
      items.add(Items.RABBIT_HIDE);
      items.add(Items.BEEF);
      items.add(Items.PORKCHOP);
      items.add(Items.COAL);
      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      locations.add(CharmLootTables.VILLAGE_BUTCHER);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
