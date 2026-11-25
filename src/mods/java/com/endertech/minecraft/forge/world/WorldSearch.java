package com.endertech.minecraft.forge.world;

import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.units.StateHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldSearch {
   public WorldSearch() {
   }

   public abstract static class BlockChain {
      private static List<EnumFacing> DIRECTIONS = Arrays.asList(EnumFacing.VALUES.clone());
      protected final World world;
      protected final BlockPos startPos;
      protected final List<BlockPos> blockChain;
      protected final List<BlockPos> foundBlocks;
      protected final int maxLength;
      protected boolean breakSearch = false;
      @Nullable
      protected EnumFacing lastUsedDirection = null;

      public BlockChain(World world, BlockPos startPos, int maxLength) {
         this.world = world;
         this.startPos = startPos;
         this.maxLength = maxLength;
         this.blockChain = new ArrayList();
         this.foundBlocks = new ArrayList();
      }

      public static WorldSearch.BlockChain create(World world, BlockPos startPos, int maxLength, final WorldSearch.BlockChain.BlockFunc validPath, final WorldSearch.BlockChain.BlockFunc validBlock, final WorldSearch.BlockChain.BlockFunc validFound) {
         WorldSearch.BlockChain chain = new WorldSearch.BlockChain(world, startPos, maxLength) {
            protected boolean onValidFound(BlockPos pos) {
               return validFound.apply(this.world, pos);
            }

            protected boolean isValidPath(BlockPos pos) {
               return validPath.apply(this.world, pos);
            }

            protected boolean isValidBlock(BlockPos pos) {
               return validBlock.apply(this.world, pos);
            }
         };
         chain.build();
         return chain;
      }

      protected abstract boolean isValidPath(BlockPos var1);

      protected abstract boolean isValidBlock(BlockPos var1);

      protected abstract boolean onValidFound(BlockPos var1);

      public void build() {
         this.lastUsedDirection = null;
         this.breakSearch = false;
         this.blockChain.clear();
         this.foundBlocks.clear();
         this.search();
      }

      protected Collection<EnumFacing> getDirections() {
         return DIRECTIONS;
      }

      public BlockPos getStartPos() {
         return this.startPos;
      }

      public World getWorld() {
         return this.world;
      }

      public List<BlockPos> getChain() {
         return this.blockChain;
      }

      public List<BlockPos> getFound() {
         return this.foundBlocks;
      }

      public int length() {
         return this.blockChain.size();
      }

      protected void search() {
         Deque<WorldSearch.BlockChain.StackEntry> stack = new ArrayDeque();
         stack.push(new WorldSearch.BlockChain.StackEntry(this.startPos));

         while(!stack.isEmpty()) {
            if (this.length() >= this.maxLength) {
               return;
            }

            WorldSearch.BlockChain.StackEntry entry = (WorldSearch.BlockChain.StackEntry)stack.peek();
            BlockPos pos = entry.getPos();
            if (!entry.isChecked()) {
               if (!this.world.isBlockLoaded(pos)) {
                  stack.pop();
                  continue;
               }

               if (this.isValidBlock(pos) && !this.foundBlocks.contains(pos)) {
                  this.foundBlocks.add(pos);
                  this.breakSearch = !this.onValidFound(pos);
               }

               if (!this.isValidPath(pos) || this.blockChain.contains(pos)) {
                  stack.pop();
                  continue;
               }

               this.blockChain.add(pos);
               entry.setChecked();
            }

            if (this.breakSearch) {
               return;
            }

            EnumFacing direction = (EnumFacing)entry.getNextDirection().orElse((Object)null);
            if (direction != null) {
               stack.push(new WorldSearch.BlockChain.StackEntry(pos.offset(direction)));
               this.lastUsedDirection = direction;
            } else {
               stack.pop();
            }
         }

      }

      @FunctionalInterface
      public interface BlockFunc {
         boolean apply(World var1, BlockPos var2);
      }

      protected class StackEntry {
         protected final BlockPos pos;
         protected Deque<EnumFacing> directions;
         protected boolean checked = false;

         public StackEntry(BlockPos pos) {
            this.pos = pos;
         }

         public BlockPos getPos() {
            return this.pos;
         }

         public Optional<EnumFacing> getNextDirection() {
            if (this.directions == null) {
               this.directions = new ArrayDeque(BlockChain.this.getDirections());
            }

            return this.directions.isEmpty() ? Optional.empty() : Optional.of(this.directions.pop());
         }

         public boolean isChecked() {
            return this.checked;
         }

         public void setChecked() {
            this.checked = true;
         }
      }
   }

   public static class Column extends WorldSearch.BlockChain {
      private static final List<EnumFacing> DIRECTIONS = Arrays.asList(EnumFacing.UP, EnumFacing.DOWN);
      private final BlockState validState;
      private BlockPos top;
      private BlockPos bottom;

      public static WorldSearch.Column from(World world, BlockPos pos) {
         WorldSearch.Column column = new WorldSearch.Column(world, pos, ChunkBounds.DEFAULT_HEIGHT);
         column.build();
         return column;
      }

      protected Column(World world, BlockPos pos, int maxLength) {
         super(world, pos, maxLength);
         this.validState = ForgeWorld.getBlockState(world, pos);
         this.top = pos;
         this.bottom = pos;
      }

      protected Collection<EnumFacing> getDirections() {
         return DIRECTIONS;
      }

      protected boolean isValidPath(BlockPos pos) {
         return this.isValidBlock(pos);
      }

      protected boolean isValidBlock(BlockPos pos) {
         IBlockState state = this.world.getBlockState(pos);
         return this.validState.getBlock() == state.getBlock();
      }

      protected boolean onValidFound(BlockPos pos) {
         if (pos.getY() > this.top.getY()) {
            this.top = pos;
         }

         if (pos.getY() < this.bottom.getY()) {
            this.bottom = pos;
         }

         return true;
      }

      public BlockPos getTop() {
         return this.top;
      }

      public BlockPos getBottom() {
         return this.bottom;
      }
   }

   public static class TileNeighbors extends WorldSearch.BlockChain {
      protected final List<BlockPos> aboveBlocks = new ArrayList();
      protected final List<BlockPos> sideBlocks = new ArrayList();
      protected final List<BlockPos> underBlocks = new ArrayList();
      protected final Set<BlockState> relatedBlocks = new StateHashSet<BlockState>();
      protected IBlockState masterState;

      protected TileNeighbors(World world, BlockPos startPos, int maxBlocksInTile, Set<BlockState> relatedBlocks) {
         super(world, startPos, maxBlocksInTile);
         this.relatedBlocks.addAll(relatedBlocks);
      }

      public static WorldSearch.TileNeighbors from(TileEntity tile, Set<BlockState> relatedBlocks) {
         WorldSearch.TileNeighbors neighbours = new WorldSearch.TileNeighbors(tile.getWorld(), tile.getPos(), ForgeWorld.SmokeContainers.maxBlocksInMultiblock, relatedBlocks);
         neighbours.build();
         return neighbours;
      }

      public static WorldSearch.TileNeighbors from(World world, BlockPos startPos, Set<BlockState> relatedBlocks) {
         WorldSearch.TileNeighbors neighbours = new WorldSearch.TileNeighbors(world, startPos, ForgeWorld.SmokeContainers.maxBlocksInMultiblock, relatedBlocks);
         neighbours.build();
         return neighbours;
      }

      public void build() {
         this.masterState = this.world.getBlockState(this.startPos);
         this.aboveBlocks.clear();
         this.sideBlocks.clear();
         this.underBlocks.clear();
         super.build();
      }

      public boolean isMultiblockHollow(BlockPos pos) {
         int minSideLen = 3;
         int maxSideLen = this.maxLength / 9;
         if (maxSideLen < 3) {
            return false;
         } else {
            int maxHollowLen = maxSideLen - 2;

            label26:
            for(EnumFacing facing : this.getDirections()) {
               for(int n = 1; n <= maxHollowLen; ++n) {
                  if (this.getChain().contains(pos.offset(facing, n))) {
                     continue label26;
                  }
               }

               return false;
            }

            return true;
         }
      }

      protected boolean isValidPath(BlockPos pos) {
         if (this.lastUsedDirection == null) {
            return true;
         } else {
            IBlockState state = this.world.getBlockState(pos);
            if (!this.relatedBlocks.isEmpty() && this.relatedBlocks.contains(BlockState.from(state))) {
               return true;
            } else {
               TileEntity tile = this.world.getTileEntity(pos);
               return tile != null && this.masterState == state;
            }
         }
      }

      protected boolean isValidBlock(BlockPos pos) {
         return !this.isValidPath(pos);
      }

      protected boolean onValidFound(BlockPos pos) {
         if (this.lastUsedDirection != null) {
            switch(this.lastUsedDirection) {
            case DOWN:
               this.underBlocks.add(pos);
               break;
            case UP:
               this.aboveBlocks.add(pos);
               break;
            default:
               this.sideBlocks.add(pos);
            }
         }

         return true;
      }

      public List<BlockPos> getAboveBlocks() {
         return this.aboveBlocks;
      }

      public List<BlockPos> getSideBlocks() {
         return this.sideBlocks;
      }

      public List<BlockPos> getUnderBlocks() {
         return this.underBlocks;
      }

      public List<BlockPos> getActiveVents() {
         List<BlockPos> activeVents = new ArrayList();

         for(BlockPos pos : this.getUnderBlocks()) {
            if (ForgeWorld.SmokeContainers.isChimney(this.getWorld(), pos)) {
               pos = ForgeWorld.SmokeContainers.getBottommostChimney(this.getWorld(), pos).down();
            }

            if (ForgeWorld.SmokeContainers.isActivePump(this.getWorld(), pos)) {
               activeVents.addAll(ForgeWorld.SmokeContainers.getVentsAround(this.getWorld(), pos));
            }
         }

         return activeVents;
      }

      public List<BlockPos> getTopChimneys() {
         List<BlockPos> chimneys = new ArrayList();

         for(BlockPos pos : this.getAboveBlocks()) {
            if (ForgeWorld.SmokeContainers.isChimney(this.getWorld(), pos)) {
               chimneys.add(pos);
            }
         }

         return chimneys;
      }

      public List<BlockPos> getSideChimneys() {
         List<BlockPos> chimneys = new ArrayList();

         for(BlockPos pos : this.getSideBlocks()) {
            if (ForgeWorld.SmokeContainers.isChimney(this.getWorld(), pos)) {
               chimneys.add(pos);
            }
         }

         return chimneys;
      }

      public List<BlockPos> getPassiveVents() {
         List<BlockPos> passiveVents = new ArrayList();

         for(BlockPos pos : this.getSideBlocks()) {
            if (ForgeWorld.SmokeContainers.isVent(this.getWorld(), pos)) {
               passiveVents.add(pos);
            }
         }

         return passiveVents;
      }
   }

   public abstract static class VentPipe extends WorldSearch.BlockChain {
      public static final WorldSearch.BlockChain.BlockFunc VALID_CHIMNEY_BLOCK = (world, pos) -> {
         return ForgeWorld.SmokeContainers.isChimney(world, pos);
      };
      public static final WorldSearch.BlockChain.BlockFunc VALID_CONDUCTOR_BLOCK = (world, pos) -> {
         return ForgeWorld.SmokeContainers.isVent(world, pos);
      };
      private static List<EnumFacing> DIRECTIONS = Arrays.asList(ForgeWorld.Direction.CLOCKWISE_HORIZONTALS.clone());
      protected final int pumpsRequired;
      protected int pumpsCount;

      public VentPipe(World world, BlockPos startPos, int pumpsRequired) {
         super(world, startPos, ForgeWorld.SmokeContainers.maxVentPipeLength);
         this.pumpsRequired = pumpsRequired;
      }

      public static int pump(World world, List<BlockPos> startPositions, int pumpsRequired, WorldSearch.BlockChain.BlockFunc validOutlet, WorldSearch.VentPipe.PumpFunc onPump) {
         return pump(world, startPositions, pumpsRequired, VALID_CONDUCTOR_BLOCK, validOutlet, onPump);
      }

      public static int pump(World world, List<BlockPos> startPositions, int pumpsRequired, final WorldSearch.BlockChain.BlockFunc validVent, final WorldSearch.BlockChain.BlockFunc validOutlet, final WorldSearch.VentPipe.PumpFunc onPump) {
         int count = 0;
         if (pumpsRequired <= 0) {
            return count;
         } else {
            List<WorldSearch.VentPipe> ventPipes = new ArrayList();

            label30:
            for(BlockPos pos : startPositions) {
               for(WorldSearch.VentPipe pipe : ventPipes) {
                  if (pipe.getChain().contains(pos)) {
                     continue label30;
                  }
               }

               WorldSearch.VentPipe pipe = new WorldSearch.VentPipe(world, pos, pumpsRequired) {
                  protected boolean isValidPath(BlockPos pos) {
                     return validVent.apply(this.getWorld(), pos);
                  }

                  protected boolean isValidBlock(BlockPos pos) {
                     return validOutlet.apply(this.getWorld(), pos);
                  }

                  protected int onPump(BlockPos pos, int maxAmount) {
                     return onPump.apply(this.getWorld(), pos, maxAmount);
                  }
               };
               count += pipe.pump().getPumpsCount();
               ventPipes.add(pipe);
               if (count >= pumpsRequired) {
                  return count;
               }
            }

            return count;
         }
      }

      public WorldSearch.VentPipe pump() {
         this.pumpsCount = 0;
         super.build();
         return this;
      }

      protected Collection<EnumFacing> getDirections() {
         return DIRECTIONS;
      }

      protected boolean isValidPath(BlockPos pos) {
         return ForgeWorld.SmokeContainers.isVent(this.world, pos);
      }

      protected boolean onValidFound(BlockPos pos) {
         if (this.pumpsCount >= this.pumpsRequired) {
            return false;
         } else {
            this.pumpsCount += this.onPump(pos, this.pumpsRequired - this.pumpsCount);
            return this.pumpsCount < this.pumpsRequired;
         }
      }

      public int getPumpsCount() {
         return this.pumpsCount;
      }

      protected abstract int onPump(BlockPos var1, int var2);

      @FunctionalInterface
      public interface PumpFunc {
         int apply(World var1, BlockPos var2, int var3);
      }
   }
}
