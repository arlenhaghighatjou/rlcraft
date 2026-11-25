package rustic.common.blocks.fluids;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidRustic extends BlockFluidClassic {
   public BlockFluidRustic(String name, Fluid fluid, Material material) {
      super(fluid, material);
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      GameRegistry.findRegistry(Item.class).register((new ItemBlock(this)).setRegistryName(this.getRegistryName()));
      fluid.setBlock(this);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      Item item = Item.getItemFromBlock(this);
      FluidStateMapper stateMapper = new FluidStateMapper(this.stack.getFluid());
      ModelLoader.registerItemVariants(item, new ResourceLocation[0]);
      ModelLoader.setCustomMeshDefinition(item, stateMapper);
      ModelLoader.setCustomStateMapper(this, stateMapper);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getBlockState().getBaseState().withProperty(LEVEL, Integer.valueOf(meta));
   }

   public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
      IBlockState neighbor = world.getBlockState(pos.offset(side));
      return neighbor.getBlock() != state.getBlock();
   }

   protected boolean canFlowInto(IBlockAccess world, BlockPos pos) {
      if (world.isAirBlock(pos)) {
         return true;
      } else {
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() == this) {
            return true;
         } else if (this.displacements.containsKey(state.getBlock())) {
            return ((Boolean)this.displacements.get(state.getBlock())).booleanValue();
         } else {
            Material material = state.getMaterial();
            if (!material.blocksMovement() && material != Material.WATER && material != Material.LAVA && material != Material.PORTAL) {
               if (state.getBlock() instanceof BlockFluidBase) {
                  return false;
               } else {
                  int density = getDensity(world, pos);
                  if (density == Integer.MAX_VALUE) {
                     return true;
                  } else {
                     return this.density > density;
                  }
               }
            } else {
               return false;
            }
         }
      }
   }

   public float getFluidHeightAverage(float... flow) {
      float total = 0.0F;
      int count = 0;
      float end = 0.0F;

      for(int i = 0; i < flow.length; ++i) {
         if (flow[i] >= 1.0F) {
            return 1.0F;
         }

         if (flow[i] >= 0.875F) {
            total += flow[i] * 10.0F;
            count += 10;
         }

         if (flow[i] >= 0.0F) {
            total += flow[i];
            ++count;
         }
      }

      if (end == 0.0F) {
         end = total / (float)count;
      }

      return end;
   }

   public boolean displaceIfPossible(World world, BlockPos pos) {
      if (world.isAirBlock(pos)) {
         return true;
      } else {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (block == this) {
            return false;
         } else if (block instanceof BlockFluidBase && ((Integer)state.getValue(LEVEL)).intValue() == 0) {
            return false;
         } else if (this.displacements.containsKey(block)) {
            if (((Boolean)this.displacements.get(block)).booleanValue()) {
               if (state.getBlock() != Blocks.SNOW_LAYER) {
                  block.dropBlockAsItem(world, pos, state, 0);
               }

               return true;
            } else {
               return false;
            }
         } else {
            Material material = state.getMaterial();
            if (!material.blocksMovement() && material != Material.PORTAL) {
               int density = getDensity(world, pos);
               if (density == Integer.MAX_VALUE) {
                  block.dropBlockAsItem(world, pos, state, 0);
                  return true;
               } else {
                  return this.density > density;
               }
            } else {
               return false;
            }
         }
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }
}
