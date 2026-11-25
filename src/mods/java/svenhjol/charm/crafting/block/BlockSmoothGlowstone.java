package svenhjol.charm.crafting.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import svenhjol.charm.crafting.feature.SmoothGlowstone;
import svenhjol.meson.MesonBlock;

public class BlockSmoothGlowstone extends MesonBlock {
   public BlockSmoothGlowstone() {
      super(Material.GLASS, "smooth_glowstone");
      this.setLightLevel(1.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHardness(SmoothGlowstone.hardness);
   }

   public String getModId() {
      return "charm";
   }

   public int quantityDroppedWithBonus(int fortune, EaglercraftRandom random) {
      return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 4);
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 2 + random.nextInt(3);
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.GLOWSTONE_DUST;
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this, 1);
   }
}
