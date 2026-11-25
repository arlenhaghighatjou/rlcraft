package svenhjol.charm.world.decorator.theme;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmDecoratorTheme;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class VillageShepherdTheme extends CharmDecoratorTheme {
   public VillageShepherdTheme(MesonInnerDecorator structure) {
      super(structure);
   }

   public IBlockState getFunctionalBlock() {
      List<IBlockState> states = new ArrayList();
      states.add(Blocks.CRAFTING_TABLE.getDefaultState());
      states.add(Blocks.FURNACE.getDefaultState());
      states.add(Blocks.DISPENSER.getDefaultState());
      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public IBlockState getDecorationBlock() {
      List<IBlockState> states = new ArrayList();

      for(int i = 0; i < 16; ++i) {
         states.add(Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byDyeDamage(i)));
      }

      return (IBlockState)states.get(this.getRand().nextInt(states.size()));
   }

   public ItemStack getFramedItem() {
      List<Item> items = new ArrayList();
      items.add(Item.getItemFromBlock(Blocks.WOOL));
      items.add(Items.STRING);
      items.add(Items.MUTTON);
      items.add(Items.COOKED_MUTTON);
      if (this.common() && VillageDecorations.vanillaTools) {
         items.add(Items.SHEARS);
      }

      return new ItemStack((Item)items.get(this.getRand().nextInt(items.size())));
   }

   public ResourceLocation getLootTable() {
      List<ResourceLocation> locations = new ArrayList();
      locations.add(CharmLootTables.VILLAGE_SHEPHERD);
      return (ResourceLocation)locations.get(this.getRand().nextInt(locations.size()));
   }
}
