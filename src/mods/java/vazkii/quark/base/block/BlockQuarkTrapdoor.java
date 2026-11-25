package vazkii.quark.base.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockQuarkTrapdoor extends BlockTrapDoor implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;

   public BlockQuarkTrapdoor(String name) {
      super(Material.WOOD);
      this.setHardness(3.0F);
      this.setSoundType(SoundType.WOOD);
      this.variants = new String[]{name};
      this.bareName = name;
      this.setUnlocalizedName(name);
      this.useNeighborBrightness = true;
   }

   public boolean isLadder(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EntityLivingBase entity) {
      if (((Boolean)state.getValue(OPEN)).booleanValue()) {
         IBlockState down = world.getBlockState(pos.down());
         return down.getBlock().isLadder(down, world, pos.down(), entity);
      } else {
         return false;
      }
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[0];
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }
}
