package snownee.kiwi.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import snownee.kiwi.IModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.block.BlockMod;
import snownee.kiwi.potion.PotionMod;

@KiwiModule(
   modid = "kiwi",
   optional = true,
   disabledByDefault = true
)
public class TestModule implements IModule {
   public static final ItemTest FIRST_ITEM = new ItemTest("my_first_item");
   public static final ItemVariantTest<Block, VariantTest> MISC = new ItemVariantTest<Block, VariantTest>("misc");
   public static final BlockMod FIRST_BLOCK = new BlockMod("my_first_block", Material.ROCK);
   public static final PotionMod FIRST_POTION = new PotionMod("my_first_potion", false, 0, false, 16711680, -1, true);

   public TestModule() {
   }

   public void init() {
      FIRST_BLOCK.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      FIRST_ITEM.setCreativeTab(CreativeTabs.MISC);
      MISC.setCreativeTab(CreativeTabs.MISC);
   }
}
