package svenhjol.charm.crafting.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.crafting.feature.RottenFleshBlock;
import svenhjol.meson.MesonBlock;

public class BlockRottenFlesh extends MesonBlock {
   protected static List<IBlockState> transformables = new ArrayList<IBlockState>() {
      {
         this.add(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
         this.add(Blocks.GRASS.getDefaultState());
         this.add(Blocks.GRASS_PATH.getDefaultState());
      }
   };
   protected static IBlockState podzol = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL);
   protected static IBlockState mycelium = Blocks.MYCELIUM.getDefaultState();

   public BlockRottenFlesh() {
      super(Material.SPONGE, "rotten_flesh_block");
      this.setHardness(RottenFleshBlock.hardness);
      this.setResistance(RottenFleshBlock.resistance);
      this.setHarvestLevel("sword", RottenFleshBlock.harvestLevel);
      this.setSoundType(SoundType.GROUND);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHarvestLevel("shovel", 0);
   }

   public String getModId() {
      return "charm";
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      super.updateTick(world, pos, state, rand);
      if (!world.isRemote) {
         if (!RottenFleshBlock.transformToPodzol && !RottenFleshBlock.transformToSoil) {
            return;
         }

         if (!world.isAreaLoaded(pos, 2)) {
            return;
         }

         ArrayList<IBlockState> transforms = new ArrayList(Arrays.asList(podzol, mycelium));
         transforms.forEach((blockType) -> {
            if (world.getBlockState(pos) != blockType && world.getBiome(pos).topBlock == blockType && transformables.contains(world.getBlockState(pos.up()))) {
               world.setBlockState(pos.up(), blockType, 2);
            }

         });
         if (RottenFleshBlock.transformToSoil) {
            for(EnumFacing facing : EnumFacing.values()) {
               if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.WATER) {
                  IBlockState transformTo = Blocks.DIRT.getDefaultState();
                  world.setBlockState(pos, transformTo, 2);
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (RottenFleshBlock.showParticles && rand.nextInt(10) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.TOWN_AURA, (double)((float)pos.getX() + rand.nextFloat()), (double)((float)pos.getY() + 1.1F), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
