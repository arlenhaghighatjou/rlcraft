package noppes.vc.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.VCBlocks;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileCandle;

public class BlockCandle extends BlockBasicLightable {
   public static final AxisAlignedBB AABB_NORMAL = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.5D, 0.699999988079071D);
   public static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.10000000149011612D, 0.10000000149011612D, 0.10000000149011612D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.20000000298023224D, 0.4000000059604645D, 0.20000000298023224D, 0.800000011920929D, 0.8999999761581421D, 0.800000011920929D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.20000000298023224D, 0.4000000059604645D, 0.4000000059604645D, 0.800000011920929D, 0.8999999761581421D, 1.0D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.20000000298023224D, 0.4000000059604645D, 0.0D, 0.800000011920929D, 0.8999999761581421D, 0.6000000238418579D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.4000000059604645D, 0.4000000059604645D, 0.20000000298023224D, 1.0D, 0.8999999761581421D, 0.800000011920929D);
   public static final AxisAlignedBB AABB4 = new AxisAlignedBB(0.0D, 0.4000000059604645D, 0.20000000298023224D, 0.6000000238418579D, 0.8999999761581421D, 0.800000011920929D);

   public BlockCandle(boolean lit) {
      super(Blocks.PLANKS, lit);
      this.setSoundType(SoundType.WOOD);
   }

   public int maxRotation() {
      return 8;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBasicRotation)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBasicRotation tile = (TileBasicRotation)tileentity;
         if (tile.color == 2) {
            if (tile.rotation == 0) {
               return AABB1;
            } else if (tile.rotation == 4) {
               return AABB2;
            } else if (tile.rotation == 6) {
               return AABB3;
            } else {
               return tile.rotation == 2 ? AABB4 : AABB0;
            }
         } else {
            return tile.color == 1 ? AABB_UP : AABB_NORMAL;
         }
      }
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileCandle();
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      if (this != this.unlitBlock()) {
         TileCandle tile = (TileCandle)world.getTileEntity(pos);
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         if (tile.color == 1) {
            if (tile.rotation % 2 == 0) {
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.5F), (double)((float)y + 0.66F), (double)((float)z + 0.13F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.5F), (double)((float)y + 0.65F), (double)((float)z + 0.13F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.5F), (double)((float)y + 0.66F), (double)((float)z + 0.87F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.5F), (double)((float)y + 0.65F), (double)((float)z + 0.87F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.13F), (double)((float)y + 0.66F), (double)((float)z + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.13F), (double)((float)y + 0.65F), (double)((float)z + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.87F), (double)((float)y + 0.66F), (double)((float)z + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.87F), (double)((float)y + 0.65F), (double)((float)z + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
            } else {
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.24F), (double)((float)y + 0.66F), (double)((float)z + 0.24F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.24F), (double)((float)y + 0.65F), (double)((float)z + 0.24F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.76F), (double)((float)y + 0.66F), (double)((float)z + 0.76F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.76F), (double)((float)y + 0.65F), (double)((float)z + 0.76F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.24F), (double)((float)y + 0.66F), (double)((float)z + 0.76F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.24F), (double)((float)y + 0.65F), (double)((float)z + 0.76F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)x + 0.76F), (double)((float)y + 0.66F), (double)((float)z + 0.24F), 0.0D, 0.0D, 0.0D, new int[0]);
               world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)x + 0.76F), (double)((float)y + 0.65F), (double)((float)z + 0.24F), 0.0D, 0.0D, 0.0D, new int[0]);
            }
         } else {
            float xOffset = 0.5F;
            float yOffset = 0.45F;
            float zOffset = 0.5F;
            if (tile.color == 2) {
               yOffset = 1.05F;
               if (tile.rotation == 0) {
                  zOffset += 0.12F;
               }

               if (tile.rotation == 4) {
                  zOffset -= 0.12F;
               }

               if (tile.rotation == 6) {
                  xOffset += 0.12F;
               }

               if (tile.rotation == 2) {
                  xOffset -= 0.12F;
               }
            }

            double d0 = (double)((float)x + xOffset);
            double d1 = (double)((float)y + yOffset);
            double d2 = (double)((float)z + zOffset);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }

   public Block unlitBlock() {
      return VCBlocks.candle_unlit;
   }

   public Block litBlock() {
      return VCBlocks.candle;
   }
}
