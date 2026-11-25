package bedbreakbegone.core;

import bedbreakbegone.handlers.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(
   modid = "bedbreakbegone",
   version = "1.0.2",
   name = "BedBreakBegone",
   acceptableRemoteVersions = "*"
)
public class BedBreakBegone {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         bedbreakbegone.handlers.ConfigHandler.register();
         bedbreakbegone.handlers.ConfigHandler.EventHandler.register(MinecraftForge.EVENT_BUS);
      }

   }

   public static final String MODID = "bedbreakbegone";
   public static final String VERSION = "1.0.2";
   public static final String NAME = "BedBreakBegone";
   public static final String CHANNEL = "BEDBREAKBEGONE";
   public static BedBreakBegone instance;

   public BedBreakBegone() {
   }

   public static BlockPos getBedSpawnLocation(World worldIn, BlockPos bedLocation, boolean forceSpawn) {
      IBlockState state = worldIn.getBlockState(bedLocation);
      Block block = state.getBlock();
      if (!block.isBed(state, worldIn, bedLocation, (Entity)null)) {
         if (!forceSpawn) {
            if (ConfigHandler.server.spawnWarning) {
               System.out.println("BBB Non-Forced Spawn failed at: " + bedLocation.getX() + "X " + bedLocation.getY() + "Y " + bedLocation.getZ() + "Z");
            }

            return null;
         } else {
            return iterateSpawnPoint(worldIn, bedLocation);
         }
      } else {
         return iterateBedPoint(worldIn, bedLocation);
      }
   }

   private static BlockPos iterateSpawnPoint(World world, BlockPos pos) {
      if (isValidSpawnPos(world, pos, false)) {
         return pos;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         for(int xIter = x - 1; xIter <= x + 1; ++xIter) {
            for(int zIter = z - 1; zIter <= z + 1; ++zIter) {
               BlockPos blockPos = new BlockPos(xIter, y, zIter);
               if (isValidSpawnPos(world, blockPos, false)) {
                  return blockPos;
               }
            }
         }

         if (ConfigHandler.server.spawnWarning) {
            System.out.println("BBB Non-Bed Spawnpoint failed at: " + x + "X " + y + "Y " + z + "Z");
         }

         return null;
      }
   }

   private static BlockPos iterateBedPoint(World world, BlockPos pos) {
      EnumFacing enumfacing = (EnumFacing)world.getBlockState(pos).getValue(BlockHorizontal.FACING);
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();

      for(int yOffset = 0; yOffset <= 1; ++yOffset) {
         for(int facingOffset = 0; facingOffset <= 1; ++facingOffset) {
            int xOffset = x - enumfacing.getFrontOffsetX() * facingOffset;
            int zOffset = z - enumfacing.getFrontOffsetZ() * facingOffset;

            for(int xIter = xOffset - 1; xIter <= xOffset + 1; ++xIter) {
               for(int zIter = zOffset - 1; zIter <= zOffset + 1; ++zIter) {
                  BlockPos blockPos = new BlockPos(xIter, y + yOffset, zIter);
                  if (isValidSpawnPos(world, blockPos, true)) {
                     return blockPos;
                  }
               }
            }
         }
      }

      if (ConfigHandler.server.spawnWarning) {
         System.out.println("BBB Bed Spawnpoint failed at: " + x + "X " + y + "Y " + z + "Z");
      }

      return null;
   }

   private static boolean isValidSpawnPos(World worldIn, BlockPos blockPos, boolean requireFloor) {
      return (worldIn.getBlockState(blockPos.down()).getMaterial().isSolid() || !requireFloor) && worldIn.getBlockState(blockPos).getBlock().canSpawnInBlock() && worldIn.getBlockState(blockPos.up()).getBlock().canSpawnInBlock();
   }
}
