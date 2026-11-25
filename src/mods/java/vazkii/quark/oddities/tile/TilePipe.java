package vazkii.quark.oddities.tile;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.oddities.block.BlockPipe;
import vazkii.quark.oddities.feature.Pipes;

public class TilePipe extends TileSimpleInventory implements ITickable {
   private static final String TAG_PIPE_ITEMS = "pipeItems";
   private boolean needsSync = false;
   private boolean iterating = false;
   public final List<TilePipe.PipeItem> pipeItems = new LinkedList();
   public final List<TilePipe.PipeItem> queuedItems = new LinkedList();

   public TilePipe() {
   }

   public static boolean isTheGoodDay(World world) {
      Calendar calendar = world.getCurrentDate();
      return calendar.get(2) + 1 == 4 && calendar.get(5) == 1;
   }

   public void update() {
      if (!this.isPipeEnabled() && this.world.getTotalWorldTime() % 10L == 0L && this.world instanceof WorldServer) {
         ((WorldServer)this.world).spawnParticle(EnumParticleTypes.REDSTONE, (double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D, 3, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
      }

      IBlockState blockAt = this.world.getBlockState(this.pos);
      if (this.isPipeEnabled() && blockAt.getBlock() instanceof BlockPipe) {
         IBlockState actualState = blockAt.getActualState(this.world, this.pos);

         for(EnumFacing side : EnumFacing.VALUES) {
            BlockPos offset = this.pos.offset(side);
            if (this.world.getBlockState(offset).getBlockFaceShape(this.world, offset, side.getOpposite()) == BlockFaceShape.UNDEFINED && !this.world.isRemote && BlockPipe.getType(actualState, side) == null) {
               double minX = (double)this.pos.getX() + 0.25D + 0.5D * (double)Math.min(0, side.getFrontOffsetX());
               double minY = (double)this.pos.getY() + 0.25D + 0.5D * (double)Math.min(0, side.getFrontOffsetY());
               double minZ = (double)this.pos.getZ() + 0.25D + 0.5D * (double)Math.min(0, side.getFrontOffsetZ());
               double maxX = (double)this.pos.getX() + 0.75D + 0.5D * (double)Math.max(0, side.getFrontOffsetX());
               double maxY = (double)this.pos.getY() + 0.75D + 0.5D * (double)Math.max(0, side.getFrontOffsetY());
               double maxZ = (double)this.pos.getZ() + 0.75D + 0.5D * (double)Math.max(0, side.getFrontOffsetZ());
               EnumFacing opposite = side.getOpposite();
               boolean any = false;

               for(EntityItem item : this.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ), (entity) -> {
                  return entity != null && entity.isEntityAlive() && EnumFacing.getFacingFromVector((float)entity.motionX, (float)entity.motionY, (float)entity.motionZ) == opposite;
               })) {
                  this.passIn(item.getItem().copy(), side);
                  if (Pipes.doPipesWhoosh) {
                     if (isTheGoodDay(this.world)) {
                        this.world.playSound((EntityPlayer)null, item.posX, item.posY, item.posZ, QuarkSounds.BLOCK_PIPE_PICKUP_LENNY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                     } else {
                        this.world.playSound((EntityPlayer)null, item.posX, item.posY, item.posZ, QuarkSounds.BLOCK_PIPE_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                     }
                  }

                  any = true;
                  item.setDead();
               }

               if (any) {
                  this.sync();
               }
            }
         }
      }

      int currentOut = this.getComparatorOutput();
      if (!this.pipeItems.isEmpty()) {
         if (Pipes.maxPipeItems > 0 && this.pipeItems.size() > Pipes.maxPipeItems && !this.world.isRemote) {
            this.world.playEvent(2001, this.pos, Block.getStateId(this.world.getBlockState(this.pos)));
            this.dropItem(new ItemStack(this.getBlockType()));
            this.world.setBlockToAir(this.getPos());
         }

         ListIterator<TilePipe.PipeItem> itemItr = this.pipeItems.listIterator();
         this.iterating = true;
         this.needsSync = false;

         while(itemItr.hasNext()) {
            TilePipe.PipeItem item = (TilePipe.PipeItem)itemItr.next();
            EnumFacing lastFacing = item.outgoingFace;
            if (item.tick(this)) {
               this.needsSync = true;
               itemItr.remove();
               if (item.valid) {
                  this.passOut(item);
               } else {
                  this.dropItem(item.stack, lastFacing, true);
               }
            }
         }

         this.iterating = false;
         this.pipeItems.addAll(this.queuedItems);
         if (this.needsSync || !this.queuedItems.isEmpty()) {
            this.sync();
         }

         this.needsSync = false;
         this.queuedItems.clear();
      }

      if (this.getComparatorOutput() != currentOut) {
         this.world.updateComparatorOutputLevel(this.getPos(), this.getBlockType());
      }

   }

   public int getComparatorOutput() {
      return Math.min(15, this.pipeItems.size());
   }

   public Iterator<TilePipe.PipeItem> getItemIterator() {
      return this.pipeItems.iterator();
   }

   public boolean passIn(ItemStack stack, EnumFacing face, long seed, int time) {
      TilePipe.PipeItem item = new TilePipe.PipeItem(stack, face, seed);
      if (!this.iterating) {
         int currentOut = this.getComparatorOutput();
         this.pipeItems.add(item);
         item.timeInWorld = time;
         if (this.getComparatorOutput() != currentOut) {
            this.world.updateComparatorOutputLevel(this.getPos(), this.getBlockType());
         }
      } else {
         this.queuedItems.add(item);
      }

      return true;
   }

   public boolean passIn(ItemStack stack, EnumFacing face) {
      return this.passIn(stack, face, this.world.rand.nextLong(), 0);
   }

   protected void passOut(TilePipe.PipeItem item) {
      BlockPos targetPos = this.getPos().offset(item.outgoingFace);
      TileEntity tile = this.world.getTileEntity(targetPos);
      boolean did = false;
      if (tile != null) {
         if (tile instanceof TilePipe) {
            did = ((TilePipe)tile).passIn(item.stack, item.outgoingFace.getOpposite(), item.rngSeed, item.timeInWorld);
         } else if (!this.world.isRemote) {
            ItemStack result = this.putIntoInv(item.stack, tile, item.outgoingFace.getOpposite(), false);
            if (result.getCount() != item.stack.getCount()) {
               did = true;
               if (!result.isEmpty()) {
                  this.bounceBack(item, result);
               }
            }
         }
      }

      if (!did) {
         this.bounceBack(item, (ItemStack)null);
      }

   }

   private void bounceBack(TilePipe.PipeItem item, ItemStack stack) {
      if (!this.world.isRemote) {
         this.passIn(stack == null ? item.stack : stack, item.outgoingFace, item.rngSeed, item.timeInWorld);
      }

   }

   public void dropItem(ItemStack stack) {
      this.dropItem(stack, (EnumFacing)null, false);
   }

   public void dropItem(ItemStack stack, EnumFacing facing, boolean playSound) {
      if (!this.world.isRemote) {
         double posX = (double)this.pos.getX() + 0.5D;
         double posY = (double)this.pos.getY() + 0.25D;
         double posZ = (double)this.pos.getZ() + 0.5D;
         if (facing != null) {
            posX -= (double)facing.getFrontOffsetX() * 0.4D;
            posY -= (double)facing.getFrontOffsetY() * 0.65D;
            posZ -= (double)facing.getFrontOffsetZ() * 0.4D;
         }

         boolean shootOut = this.isPipeEnabled();
         float pitch = 1.0F;
         if (!shootOut) {
            pitch = 0.025F;
         }

         if (playSound && Pipes.doPipesWhoosh) {
            if (isTheGoodDay(this.world)) {
               this.world.playSound((EntityPlayer)null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT_LENNY, SoundCategory.BLOCKS, 1.0F, pitch);
            } else {
               this.world.playSound((EntityPlayer)null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT, SoundCategory.BLOCKS, 1.0F, pitch);
            }
         }

         EntityItem entity = new EntityItem(this.world, posX, posY, posZ, stack);
         entity.setDefaultPickupDelay();
         double velocityMod = 0.5D;
         if (!shootOut) {
            velocityMod = 0.125D;
         }

         if (facing != null) {
            entity.motionX = (double)(-facing.getFrontOffsetX()) * velocityMod;
            entity.motionY = (double)(-facing.getFrontOffsetY()) * velocityMod;
            entity.motionZ = (double)(-facing.getFrontOffsetZ()) * velocityMod;
         }

         this.world.spawnEntity(entity);
      }

   }

   public void dropAllItems() {
      for(TilePipe.PipeItem item : this.pipeItems) {
         this.dropItem(item.stack);
      }

      this.pipeItems.clear();
   }

   public void readSharedNBT(NBTTagCompound cmp) {
      super.readSharedNBT(cmp);
      NBTTagList pipeItemList = cmp.getTagList("pipeItems", cmp.getId());
      this.pipeItems.clear();
      pipeItemList.forEach((listCmp) -> {
         TilePipe.PipeItem item = TilePipe.PipeItem.readFromNBT((NBTTagCompound)listCmp);
         this.pipeItems.add(item);
      });
   }

   public void writeSharedNBT(NBTTagCompound cmp) {
      super.writeSharedNBT(cmp);
      NBTTagList pipeItemList = new NBTTagList();

      for(TilePipe.PipeItem item : this.pipeItems) {
         NBTTagCompound listCmp = new NBTTagCompound();
         item.writeToNBT(listCmp);
         pipeItemList.appendTag(listCmp);
      }

      cmp.setTag("pipeItems", pipeItemList);
   }

   protected boolean canFit(ItemStack stack, BlockPos pos, EnumFacing face) {
      TileEntity tile = this.world.getTileEntity(pos);
      if (tile == null) {
         return false;
      } else if (tile instanceof TilePipe) {
         return ((TilePipe)tile).isPipeEnabled();
      } else {
         ItemStack result = this.putIntoInv(stack, tile, face, true);
         return result.isEmpty();
      }
   }

   protected boolean isPipeEnabled() {
      IBlockState state = this.world.getBlockState(this.pos);
      return state.getBlock() instanceof BlockPipe && ((Boolean)state.getValue(BlockPipe.ENABLED)).booleanValue();
   }

   protected ItemStack putIntoInv(ItemStack stack, TileEntity tile, EnumFacing face, boolean simulate) {
      IItemHandler handler = null;
      if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)) {
         handler = (IItemHandler)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
      } else if (tile instanceof ISidedInventory) {
         handler = new SidedInvWrapper((ISidedInventory)tile, face);
      } else if (tile instanceof IInventory) {
         handler = new InvWrapper((IInventory)tile);
      }

      return handler != null ? ItemHandlerHelper.insertItem(handler, stack, simulate) : stack;
   }

   public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
      return index == direction.ordinal() && this.isPipeEnabled();
   }

   public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
      if (!itemstack.isEmpty()) {
         EnumFacing side = EnumFacing.VALUES[i];
         this.passIn(itemstack, side);
         this.sync();
      }

   }

   public int getSizeInventory() {
      return 6;
   }

   protected boolean needsToSyncInventory() {
      return false;
   }

   public static class PipeItem {
      private static final String TAG_TICKS = "ticksInPipe";
      private static final String TAG_INCOMING = "incomingFace";
      private static final String TAG_OUTGOING = "outgoingFace";
      private static final String TAG_RNG_SEED = "rngSeed";
      private static final String TAG_TIME_IN_WORLD = "timeInWorld";
      private static final List<EnumFacing> HORIZONTAL_SIDES_LIST = Arrays.asList(EnumFacing.HORIZONTALS);
      public final ItemStack stack;
      public int ticksInPipe;
      public final EnumFacing incomingFace;
      public EnumFacing outgoingFace;
      public long rngSeed;
      public int timeInWorld = 0;
      public boolean valid = true;

      public PipeItem(ItemStack stack, EnumFacing face, long rngSeed) {
         this.stack = stack;
         this.ticksInPipe = 0;
         this.incomingFace = this.outgoingFace = face;
         this.rngSeed = rngSeed;
      }

      protected boolean tick(TilePipe pipe) {
         ++this.ticksInPipe;
         ++this.timeInWorld;
         if (!pipe.world.isRemote && this.ticksInPipe == Pipes.pipeSpeed / 2 - 1) {
            EnumFacing target = this.getTargetFace(pipe);
            if (this.outgoingFace != target) {
               pipe.needsSync = true;
            }

            this.outgoingFace = target;
         }

         if (this.outgoingFace == null) {
            this.valid = false;
            return true;
         } else {
            return this.ticksInPipe >= Pipes.pipeSpeed;
         }
      }

      protected EnumFacing getTargetFace(TilePipe pipe) {
         BlockPos pipePos = pipe.getPos();
         if (this.incomingFace != EnumFacing.DOWN && pipe.canFit(this.stack, pipePos.offset(EnumFacing.DOWN), EnumFacing.UP)) {
            return EnumFacing.DOWN;
         } else {
            EnumFacing incomingOpposite = this.incomingFace;
            if (this.incomingFace.getAxis() != Axis.Y) {
               incomingOpposite = this.incomingFace.getOpposite();
               if (pipe.canFit(this.stack, pipePos.offset(incomingOpposite), this.incomingFace)) {
                  return incomingOpposite;
               }
            }

            List<EnumFacing> sides = new ArrayList(HORIZONTAL_SIDES_LIST);
            sides.remove(this.incomingFace);
            sides.remove(incomingOpposite);
            EaglercraftRandom rng = new EaglercraftRandom(this.rngSeed);
            this.rngSeed = rng.nextLong();
            Collections.shuffle(sides, rng);

            for(EnumFacing side : sides) {
               if (pipe.canFit(this.stack, pipePos.offset(side), side.getOpposite())) {
                  return side;
               }
            }

            if (this.incomingFace != EnumFacing.UP && pipe.canFit(this.stack, pipePos.offset(EnumFacing.UP), EnumFacing.DOWN)) {
               return EnumFacing.UP;
            } else {
               return null;
            }
         }
      }

      public float getTimeFract(float partial) {
         return ((float)this.ticksInPipe + partial) / (float)Pipes.pipeSpeed;
      }

      public void writeToNBT(NBTTagCompound cmp) {
         this.stack.writeToNBT(cmp);
         cmp.setInteger("ticksInPipe", this.ticksInPipe);
         cmp.setInteger("incomingFace", this.incomingFace.ordinal());
         cmp.setInteger("outgoingFace", this.outgoingFace.ordinal());
         cmp.setLong("rngSeed", this.rngSeed);
         cmp.setInteger("timeInWorld", this.timeInWorld);
      }

      public static TilePipe.PipeItem readFromNBT(NBTTagCompound cmp) {
         ItemStack stack = new ItemStack(cmp);
         EnumFacing inFace = EnumFacing.VALUES[cmp.getInteger("incomingFace")];
         long rngSeed = cmp.getLong("rngSeed");
         TilePipe.PipeItem item = new TilePipe.PipeItem(stack, inFace, rngSeed);
         item.ticksInPipe = cmp.getInteger("ticksInPipe");
         item.outgoingFace = EnumFacing.VALUES[cmp.getInteger("outgoingFace")];
         item.timeInWorld = cmp.getInteger("timeInWorld");
         return item;
      }
   }
}
