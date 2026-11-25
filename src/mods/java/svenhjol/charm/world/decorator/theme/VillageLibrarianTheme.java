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
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.crafting.feature.BookshelfChest;
import svenhjol.charm.world.feature.TotemOfReturning;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillageLibrarianTheme extends CharmDecoratorTheme {
   public VillageLibrarianTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      if (this.valuable()) {
         states.add(Blocks.ENCHANTING_TABLE.getDefaultState());
      }

      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      if (Charm.hasFeature(BookshelfChest.class)) {
         states.add(BookshelfChest.bookshelfChest.getDefaultState());
      }

      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.BOOKSHELF.getDefaultState());
      states.add(Blocks.PLANKS.getDefaultState());
      states.add(Blocks.OAK_STAIRS.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      if (Charm.hasFeature(TotemOfReturning.class) && this.rare()) {
         items.add(TotemOfReturning.totem);
      }

      items.add(Items.WRITABLE_BOOK);
      items.add(Items.BOOK);
      items.add(Items.COMPASS);
      items.add(Items.CLOCK);
      items.add(Items.PAPER);
      items.add(Items.MAP);
      items.add(Items.FEATHER);
      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      if (this.valuable()) {
         locations.add(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
      }

      if (this.valuable()) {
         locations.add(LootTableList.CHESTS_WOODLAND_MANSION);
      }

      locations.add(CharmLootTables.VILLAGE_LIBRARIAN);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
