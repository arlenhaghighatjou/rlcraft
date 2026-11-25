package vazkii.quark.world.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockElderSeaLantern extends BlockMod implements IQuarkBlock {
   public BlockElderSeaLantern() {
      super("elder_sea_lantern", Material.GLASS, new String[0]);
      this.setHardness(0.3F);
      this.setSoundType(SoundType.GLASS);
      this.setLightLevel(1.0F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 2 + random.nextInt(2);
   }

   public int quantityDroppedWithBonus(int fortune, @Nonnull EaglercraftRandom random) {
      return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.PRISMARINE_CRYSTALS;
   }

   @Nonnull
   public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return MapColor.QUARTZ;
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
