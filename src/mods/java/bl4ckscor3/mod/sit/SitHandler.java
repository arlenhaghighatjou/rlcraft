package bl4ckscor3.mod.sit;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class SitHandler {
   public SitHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(RightClickBlock.class, SitHandler::onRightClickBlock);
      bus.register(BreakEvent.class, SitHandler::onBreak);
      bus.register(EntityMountEvent.class, SitHandler::onEntityMount);
   }

   public static void onRightClickBlock(RightClickBlock event) {
      if (!event.getWorld().isRemote && event.getFace() == EnumFacing.UP && !SitUtil.isPlayerSitting(event.getEntityPlayer())) {
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         IBlockState state = world.getBlockState(pos);
         Block block = world.getBlockState(pos).getBlock();
         EntityPlayer player = event.getEntityPlayer();
         if (isValidBlock(world, pos, state, block) && isPlayerInRange(player, pos) && !SitUtil.isOccupied(world, pos) && player.getHeldItemMainhand().isEmpty()) {
            IBlockState stateAbove = world.getBlockState(pos.up());
            if (!stateAbove.getBlock().isAir(stateAbove, world, pos.up())) {
               return;
            }

            if (block instanceof BlockSlab && (!state.getProperties().containsKey(BlockSlab.HALF) || state.getValue(BlockSlab.HALF) != EnumBlockHalf.BOTTOM)) {
               return;
            }

            if (block instanceof BlockStairs && (!state.getProperties().containsKey(BlockStairs.HALF) || state.getValue(BlockStairs.HALF) != EnumHalf.BOTTOM)) {
               return;
            }

            EntitySit sit = new EntitySit(world, pos);
            if (SitUtil.addSitEntity(world, pos, sit)) {
               world.spawnEntity(sit);
               player.startRiding(sit);
            }
         }
      }

   }

   public static void onBreak(BreakEvent event) {
      if (!event.getWorld().isRemote) {
         EntitySit entity = SitUtil.getSitEntity(event.getWorld(), event.getPos());
         if (entity != null && SitUtil.removeSitEntity(event.getWorld(), event.getPos())) {
            entity.setDead();
         }
      }

   }

   public static void onEntityMount(EntityMountEvent event) {
      if (!event.getWorldObj().isRemote && event.isDismounting()) {
         Entity entity = event.getEntityBeingMounted();
         if (entity instanceof EntitySit && SitUtil.removeSitEntity(event.getWorldObj(), entity.getPosition())) {
            entity.setDead();
         }
      }

   }

   private static boolean isValidBlock(World world, BlockPos pos, IBlockState state, Block block) {
      boolean isValid = block instanceof BlockSlab || block instanceof BlockStairs;
      if (!isValid && block instanceof BlockBed) {
         state = world.getBlockState(pos.offset(state.getValue(BlockBed.PART) == EnumPartType.HEAD ? ((EnumFacing)state.getValue(BlockBed.FACING)).getOpposite() : (EnumFacing)state.getValue(BlockBed.FACING)));
         if (!(state.getBlock() instanceof BlockBed)) {
            isValid = true;
         }
      }

      return isValid;
   }

   private static boolean isPlayerInRange(EntityPlayer player, BlockPos pos) {
      BlockPos playerPos = player.getPosition();
      if (Configuration.blockReachDistance == 0) {
         return playerPos.getY() - pos.getY() <= 1 && playerPos.getX() - pos.getX() == 0 && playerPos.getZ() - pos.getZ() == 0;
      } else {
         pos = pos.add(0.5D, 0.5D, 0.5D);
         AxisAlignedBB range = new AxisAlignedBB((double)(pos.getX() + Configuration.blockReachDistance), (double)(pos.getY() + Configuration.blockReachDistance), (double)(pos.getZ() + Configuration.blockReachDistance), (double)(pos.getX() - Configuration.blockReachDistance), (double)(pos.getY() - Configuration.blockReachDistance), (double)(pos.getZ() - Configuration.blockReachDistance));
         playerPos = playerPos.add(0.5D, 0.5D, 0.5D);
         return range.minX <= (double)playerPos.getX() && range.minY <= (double)playerPos.getY() && range.minZ <= (double)playerPos.getZ() && range.maxX >= (double)playerPos.getX() && range.maxY >= (double)playerPos.getY() && range.maxZ >= (double)playerPos.getZ();
      }
   }
}
