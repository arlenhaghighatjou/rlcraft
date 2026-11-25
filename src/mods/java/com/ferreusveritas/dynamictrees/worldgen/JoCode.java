package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.cells.LeafClusters;
import com.ferreusveritas.dynamictrees.event.SpeciesPostGenerationEvent;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeCoder;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeCollector;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeFindEnds;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;

public class JoCode {
   private static final String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
   protected static final byte forkCode = 6;
   protected static final byte returnCode = 7;
   public static boolean secondChanceRegen = false;
   public byte[] instructions;
   protected boolean careful;
   private final byte[][] dirmap;
   private byte[] facingMap;
   private byte[] unfacingMap;

   public JoCode(World world, BlockPos rootPos, EnumFacing facing) {
      this.instructions = new byte[0];
      this.careful = false;
      this.dirmap = new byte[][]{{0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 2, 5, 4, 6, 7}, {0, 1, 5, 4, 2, 3, 6, 7}, {0, 1, 4, 5, 3, 2, 6, 7}};
      this.facingMap = this.dirmap[2];
      this.unfacingMap = this.dirmap[2];
      Optional<BlockBranch> branch = TreeHelper.getBranchOpt(world.getBlockState(rootPos.up()));
      if (branch.isPresent()) {
         NodeCoder coder = new NodeCoder();
         ((BlockBranch)branch.get()).analyse(world.getBlockState(rootPos), world, rootPos, EnumFacing.DOWN, new MapSignal(new INodeInspector[]{coder}));
         this.instructions = coder.compile(this);
         this.rotate(facing);
      }

   }

   public JoCode(World world, BlockPos pos) {
      this(world, pos, EnumFacing.SOUTH);
   }

   public JoCode(String code) {
      this.instructions = new byte[0];
      this.careful = false;
      this.dirmap = new byte[][]{{0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 2, 3, 4, 5, 6, 7}, {0, 1, 3, 2, 5, 4, 6, 7}, {0, 1, 5, 4, 2, 3, 6, 7}, {0, 1, 4, 5, 3, 2, 6, 7}};
      this.facingMap = this.dirmap[2];
      this.unfacingMap = this.dirmap[2];
      this.instructions = decode(code);
   }

   public JoCode setCareful(boolean c) {
      this.careful = c;
      return this;
   }

   protected int getCode(int pos) {
      return this.unfacingMap[this.instructions[pos]];
   }

   public JoCode setFacing(EnumFacing facing) {
      int faceNum = facing.ordinal();
      this.facingMap = this.dirmap[faceNum];
      faceNum = faceNum == 4 ? 5 : (faceNum == 5 ? 4 : faceNum);
      this.unfacingMap = this.dirmap[faceNum];
      return this;
   }

   public JoCode rotate(EnumFacing dir) {
      this.setFacing(dir);

      for(int c = 0; c < this.instructions.length; ++c) {
         this.instructions[c] = this.facingMap[this.instructions[c]];
      }

      return this;
   }

   public void generate(World world, Species species, BlockPos rootPosIn, Biome biome, EnumFacing facing, int radius, SafeChunkBounds safeBounds) {
      boolean worldGen = safeBounds != SafeChunkBounds.ANY;
      radius = MathHelper.clamp(radius, 2, 8);
      this.setFacing(facing);
      BlockPos rootPos = species.preGeneration(world, rootPosIn, radius, facing, safeBounds, this);
      if (rootPos != BlockPos.ORIGIN) {
         IBlockState initialDirtState = world.getBlockState(rootPos);
         species.placeRootyDirtBlock(world, rootPos, 0);
         this.generateFork(world, species, 0, rootPos, false);
         BlockPos treePos = rootPos.up();
         IBlockState treeState = world.getBlockState(treePos);
         BlockBranch branch = TreeHelper.getBranch(treeState);
         if (branch != null) {
            ILeavesProperties leavesProperties = species.getLeavesProperties();
            SimpleVoxmap leafMap = (new SimpleVoxmap(radius * 2 + 1, species.getWorldGenLeafMapHeight(), radius * 2 + 1)).setMapAndCenter(treePos, new BlockPos(radius, 0, radius));
            INodeInspector inflator = species.getNodeInflator(leafMap);
            NodeFindEnds endFinder = new NodeFindEnds();
            MapSignal signal = new MapSignal(new INodeInspector[]{inflator, endFinder});
            signal.destroyLoopedNodes = this.careful;
            branch.analyse(treeState, world, treePos, EnumFacing.DOWN, signal);
            if (signal.found || signal.overflow) {
               DynamicTrees.log.debug("Non-viable branch network detected during world generation @ " + treePos);
               DynamicTrees.log.debug("Species: " + species);
               DynamicTrees.log.debug("Radius: " + radius);
               DynamicTrees.log.debug("JoCode: " + this);
               this.cleanupFrankentree(world, treePos, treeState, endFinder.getEnds(), safeBounds);
               if (!secondChanceRegen) {
                  secondChanceRegen = true;
                  this.generate(world, species, rootPosIn, biome, facing, radius, safeBounds);
               }

               secondChanceRegen = false;
               return;
            }

            List<BlockPos> endPoints = endFinder.getEnds();
            this.smother(leafMap, leavesProperties);

            for(SimpleVoxmap.Cell cell : leafMap.getAllNonZeroCells((byte)15)) {
               MutableBlockPos cellPos = cell.getPos();
               if (safeBounds.inBounds(cellPos, false)) {
                  IBlockState testBlockState = world.getBlockState(cellPos);
                  Block testBlock = testBlockState.getBlock();
                  if (testBlock.isReplaceable(world, cellPos)) {
                     world.setBlockState(cellPos, leavesProperties.getDynamicLeavesState(cell.getValue()), worldGen ? 16 : 2);
                  }
               } else {
                  leafMap.setVoxel(cellPos, (byte)0);
               }
            }

            for(SimpleVoxmap.Cell cell : leafMap.getAllNonZeroCells()) {
               MutableBlockPos cellPos = cell.getPos();
               if (!safeBounds.inBounds(cellPos, true)) {
                  leafMap.setVoxel(cellPos, (byte)0);
               }
            }

            TreeHelper.ageVolume(world, leafMap, species.getWorldGenAgeIterations(), safeBounds);
            if (species.handleRot(world, endPoints, rootPos, treePos, 0, safeBounds)) {
               return;
            }

            species.postGeneration(world, rootPos, biome, radius, endPoints, safeBounds, initialDirtState);
            MinecraftForge.EVENT_BUS.post(new SpeciesPostGenerationEvent(world, species, rootPos, endPoints, safeBounds, initialDirtState));
            this.addSnow(leafMap, world, rootPos, biome);
         } else {
            world.setBlockState(rootPos, initialDirtState, this.careful ? 3 : 2);
         }
      }

   }

   protected void cleanupFrankentree(World world, BlockPos treePos, IBlockState treeState, List<BlockPos> endPoints, SafeChunkBounds safeBounds) {
      Set<BlockPos> blocksToDestroy = new HashSet();
      BlockBranch branch = TreeHelper.getBranch(treeState);
      MapSignal signal = new MapSignal(new INodeInspector[]{new NodeCollector(blocksToDestroy)});
      signal.destroyLoopedNodes = false;
      signal.trackVisited = true;
      branch.analyse(treeState, world, treePos, (EnumFacing)null, signal);
      BlockBranch.destroyMode = BlockBranch.EnumDestroyMode.IGNORE;

      for(BlockPos pos : blocksToDestroy) {
         if (safeBounds.inBounds(pos, false)) {
            IBlockState branchState = world.getBlockState(pos);
            Optional<BlockBranch> branchBlock = TreeHelper.getBranchOpt(branchState);
            if (branchBlock.isPresent()) {
               int radius = ((BlockBranch)branchBlock.get()).getRadius(branchState);
               TreeFamily family = ((BlockBranch)branchBlock.get()).getFamily();
               Species species = family.getCommonSpecies();
               if (family.getPrimaryThickness() == (float)radius) {
                  ILeavesProperties leavesProperties = species.getLeavesProperties();
                  if (leavesProperties != LeavesProperties.NULLPROPERTIES) {
                     SimpleVoxmap leafCluster = leavesProperties.getCellKit().getLeafCluster();
                     if (leafCluster != LeafClusters.NULLMAP) {
                        for(SimpleVoxmap.Cell cell : leafCluster.getAllNonZeroCells()) {
                           BlockPos delPos = pos.add(cell.getPos());
                           if (safeBounds.inBounds(delPos, false)) {
                              IBlockState leavesState = world.getBlockState(delPos);
                              if (TreeHelper.isLeaves(leavesState)) {
                                 BlockDynamicLeaves leavesBlock = (BlockDynamicLeaves)leavesState.getBlock();
                                 if (leavesProperties.getTree() == leavesBlock.getProperties(leavesState).getTree()) {
                                    world.setBlockState(delPos, ModBlocks.blockStates.air, 2);
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               world.setBlockState(pos, ModBlocks.blockStates.air, 2);
            }
         }
      }

      BlockBranch.destroyMode = BlockBranch.EnumDestroyMode.HARVEST;
   }

   protected int generateFork(World world, Species species, int codePos, BlockPos pos, boolean disabled) {
      while(codePos < this.instructions.length) {
         int code = this.getCode(codePos);
         switch(code) {
         case 6:
            codePos = this.generateFork(world, species, codePos + 1, pos, disabled);
            break;
         case 7:
            return codePos + 1;
         default:
            EnumFacing dir = EnumFacing.getFront(code);
            pos = pos.offset(dir);
            if (!disabled) {
               disabled = this.setBlockForGeneration(world, species, pos, dir, this.careful);
            }

            ++codePos;
         }
      }

      return codePos;
   }

   protected boolean setBlockForGeneration(World world, Species species, BlockPos pos, EnumFacing dir, boolean careful) {
      if (world.getBlockState(pos).getBlock().isReplaceable(world, pos) && (!careful || this.isClearOfNearbyBranches(world, pos, dir.getOpposite()))) {
         species.getFamily().getDynamicBranch().setRadius(world, pos, (int)species.getFamily().getPrimaryThickness(), (EnumFacing)null, careful ? 3 : 2);
         return false;
      } else {
         return true;
      }
   }

   protected void smother(SimpleVoxmap leafMap, ILeavesProperties leavesProperties) {
      int smotherMax = leavesProperties.getSmotherLeavesMax();
      if (smotherMax != 0) {
         BlockPos saveCenter = leafMap.getCenter();
         leafMap.setCenter(new BlockPos(0, 0, 0));

         int startY;
         for(startY = leafMap.getLenY() - 1; startY >= 0 && !leafMap.isYTouched(startY); --startY) {
            ;
         }

         for(int iz = 0; iz < leafMap.getLenZ(); ++iz) {
            for(int ix = 0; ix < leafMap.getLenX(); ++ix) {
               int count = 0;

               for(int iy = startY; iy >= 0; --iy) {
                  int v = leafMap.getVoxel(new BlockPos(ix, iy, iz));
                  if (v == 0) {
                     count = 0;
                  } else if ((v & 15) != 0) {
                     ++count;
                     if (count > smotherMax) {
                        leafMap.setVoxel(new BlockPos(ix, iy, iz), (byte)0);
                     }
                  } else if ((v & 16) != 0) {
                     ++count;
                     leafMap.setVoxel(new BlockPos(ix, iy + 1, iz), (byte)4);
                  }
               }
            }
         }

         leafMap.setCenter(saveCenter);
      }

   }

   protected boolean isClearOfNearbyBranches(World world, BlockPos pos, EnumFacing except) {
      for(EnumFacing dir : EnumFacing.VALUES) {
         if (dir != except && TreeHelper.getBranch(world.getBlockState(pos.offset(dir))) != null) {
            return false;
         }
      }

      return true;
   }

   protected void addSnow(SimpleVoxmap leafMap, World world, BlockPos rootPos, Biome biome) {
      if (biome.getDefaultTemperature() < 0.4F) {
         for(MutableBlockPos top : leafMap.getTops()) {
            if (world.canSnowAt(top, false)) {
               MutableBlockPos iPos = new MutableBlockPos(top);
               int yOffset = 0;

               while(true) {
                  IBlockState state = world.getBlockState(iPos);
                  if (state.getMaterial() == Material.AIR) {
                     world.setBlockState(iPos, ModBlocks.blockStates.snowLayer.get(), 2);
                     break;
                  }

                  if (state.getBlock() == ModBlocks.blockLeavesSnow.get() || state.getBlock() == Blocks.SNOW_LAYER) {
                     break;
                  }

                  iPos.setY(iPos.getY() + 1);
                  if (yOffset++ >= 4) {
                     break;
                  }
               }
            }
         }
      }

   }

   public static String encode(byte[] array) {
      ArrayList<Byte> instructions = new ArrayList(array.length + (array.length & 1));

      for(byte b : array) {
         instructions.add(Byte.valueOf(b));
      }

      if ((instructions.size() & 1) == 1) {
         instructions.add(Byte.valueOf((byte)7));
      }

      String code = "";

      for(int b = 0; b < instructions.size(); b += 2) {
         code = code + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(((Byte)instructions.get(b)).byteValue() << 3 | ((Byte)instructions.get(b + 1)).byteValue());
      }

      return code;
   }

   public static byte[] decode(String code) {
      return (new JoCode.CodeCompiler(code)).compile();
   }

   public String toString() {
      return encode(this.instructions);
   }

   public static class CodeCompiler {
      ArrayList<Byte> instructions;

      public CodeCompiler() {
         this.instructions = new ArrayList();
      }

      public CodeCompiler(int size) {
         this.instructions = new ArrayList(size);
      }

      public CodeCompiler(String code) {
         this.instructions = new ArrayList(code.length() * 2);

         for(int i = 0; i < code.length(); ++i) {
            int sixbits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(code.charAt(i));
            if (sixbits != -1) {
               this.addInstruction((byte)(sixbits >> 3));
               this.addInstruction((byte)(sixbits & 7));
            }
         }

      }

      public void addDirection(byte dir) {
         if (dir >= 0) {
            this.instructions.add(Byte.valueOf((byte)(dir & 7)));
         }

      }

      public void addInstruction(byte instruction) {
         this.instructions.add(Byte.valueOf(instruction));
      }

      public void addReturn() {
         this.instructions.add(Byte.valueOf((byte)7));
      }

      public void addFork() {
         this.instructions.add(Byte.valueOf((byte)6));
      }

      public byte[] compile() {
         byte[] array = new byte[this.instructions.size()];
         Iterator<Byte> i = this.instructions.iterator();

         for(int pos = 0; i.hasNext(); array[pos++] = ((Byte)i.next()).byteValue()) {
            ;
         }

         return array;
      }
   }
}
