package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.ItemBlockVariant;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockParasiteStain extends BlockBase implements IMetaName {
   public static final PropertyEnum<BlockParasiteStain.EnumType> VARIANT = PropertyEnum.create("variant", BlockParasiteStain.EnumType.class);

   public BlockParasiteStain(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setHarvestLevel("shovel", 0);
      this.setSoundType(new SoundType(1.0F, 0.5F, SRPSounds.BLOCKINFEST_BREAK, SRPSounds.BLOCKINFEST_STEP, SRPSounds.BLOCKINFEST_PLACE, SRPSounds.BLOCKINFEST_HIT, SoundEvents.BLOCK_SLIME_FALL));
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockParasiteStain.EnumType.DIRT));
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockParasiteStain.EnumType variant : BlockParasiteStain.EnumType.values()) {
         items.add(new ItemStack(this, 1, variant.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockParasiteStain.EnumType.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockParasiteStain.EnumType)state.getValue(VARIANT)).ordinal();
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(world.getBlockState(pos)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public Enum[] getVariants() {
      return BlockParasiteStain.EnumType.values();
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (worldIn.getBiome(pos) instanceof BiomeParasite) {
            ParasiteEventWorld.spreadBiomeBlockStain(worldIn, pos, rand);
         } else {
            int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
            if (heart > 0) {
               ParasiteEventWorld.SpreadBiome(worldIn, pos, heart);
            }
         }

      }
   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
   }

   public ItemBlock getItemBlock() {
      return new ItemBlockVariant(this);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.up()).getBlock() == SRPBlocks.ParasiteBush) {
         if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + 2.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0D, 0.0D, 0.0D, 0, 0, 0);
         }

      }
   }

   public static enum EnumType implements IStringSerializable {
      DIRT,
      MUD,
      FLESH,
      FEELER;

      private EnumType() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
