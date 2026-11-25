package noppes.vc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.VCBlocks;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileLamp;

public class BlockLamp extends BlockBasicLightable {
   public static final AxisAlignedBB AABB_NORMAL = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.30000001192092896D, 0.20000000298023224D, 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.699999988079071D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.30000001192092896D, 0.20000000298023224D, 0.5D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.30000001192092896D, 0.20000000298023224D, 0.10000000149011612D, 0.699999988079071D, 0.699999988079071D, 0.5D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.5D, 0.20000000298023224D, 0.30000001192092896D, 0.8999999761581421D, 0.699999988079071D, 0.699999988079071D);
   public static final AxisAlignedBB AABB4 = new AxisAlignedBB(0.10000000149011612D, 0.20000000298023224D, 0.30000001192092896D, 0.5D, 0.699999988079071D, 0.699999988079071D);

   public BlockLamp(boolean lit) {
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
            return AABB_NORMAL;
         }
      }
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileLamp();
   }

   public Block unlitBlock() {
      return VCBlocks.lamp_unlit;
   }

   public Block litBlock() {
      return VCBlocks.lamp;
   }
}
