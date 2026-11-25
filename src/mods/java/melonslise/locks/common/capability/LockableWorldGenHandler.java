package melonslise.locks.common.capability;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.util.Orientation;
import melonslise.locks.mixin.TileEntityLockableLootAccessor;
import net.minecraft.block.BlockChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class LockableWorldGenHandler implements ILockableWorldGenHandler {
   public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_worldgen_handler");
   public static final int NONE = 0;
   public static final int SHOULD_GENERATE = 1;
   public static final int FINISHED_GENERATING = 2;
   public final Chunk chunk;
   public AtomicInteger phase = new AtomicInteger();

   public LockableWorldGenHandler(Chunk chunk) {
      this.chunk = chunk;
   }

   public void setChunkShouldGenerateChests() {
      if (this.phase.get() != 2) {
         this.phase.set(1);
      }

   }

   public void tryGeneratingLocks() {
      if (this.phase.get() == 1) {
         World world = this.chunk.getWorld();
         EaglercraftRandom rand = world.rand;
         ILockableHandler lockables = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);

         for(Entry<BlockPos, TileEntity> entry : this.chunk.getTileEntityMap().entrySet()) {
            BlockPos pos = (BlockPos)entry.getKey();
            if (entry.getValue() instanceof TileEntityChest && world.getBlockState(pos).getBlock() instanceof BlockChest && !lockables.getInChunk(pos).values().stream().anyMatch((lockable1) -> {
               return lockable1.box.intersects(pos);
            })) {
               TileEntityChest te = (TileEntityChest)entry.getValue();
               boolean isForced = false;
               boolean isEmpty = true;
               if (te.getLootTable() == null) {
                  for(ItemStack stack : ((TileEntityLockableLootAccessor)te).getItems()) {
                     if (!stack.isEmpty()) {
                        if (LocksConfig.isItemAlwaysLocked(stack)) {
                           isForced = true;
                           break;
                        }

                        if (isEmpty && !LocksConfig.isItemSkipped(stack)) {
                           isEmpty = false;
                        }
                     }
                  }
               } else {
                  isForced = false;
                  isEmpty = false;
               }

               if ((!isEmpty || !LocksConfig.COMMON.skipGenerationEmptyChests) && (isForced || LocksConfig.canGen(rand))) {
                  BlockPos adjPos = LocksUtil.getAdjacentChest((TileEntityChest)entry.getValue());
                  ItemStack stack = LocksConfig.getRandomLock(rand);
                  lockables.add(new Lockable(new Cuboid6i(pos, adjPos == null ? pos : adjPos), Lock.from(stack), Orientation.fromDirection((EnumFacing)world.getBlockState(pos).getValue(BlockChest.FACING), EnumFacing.NORTH), stack, world));
               }
            }
         }

         this.phase.set(2);
         this.chunk.markDirty();
      }

   }

   public NBTTagInt serializeNBT() {
      return new NBTTagInt(this.phase.get());
   }

   public void deserializeNBT(NBTTagInt nbt) {
      this.phase.set(nbt.getInt());
   }
}
