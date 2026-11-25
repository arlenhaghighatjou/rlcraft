package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.integration.Integrations;
import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.util.Pair;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class BigTunnel extends MineshaftPiece {
   private final List<BlockPos> smallShaftLeftEntrances = Lists.newLinkedList();
   private final List<BlockPos> smallShaftRightEntrances = Lists.newLinkedList();
   private final List<StructureBoundingBox> sideRoomEntrances = Lists.newLinkedList();
   private final List<Integer> bigSupports = Lists.newLinkedList();
   private final List<Integer> smallSupports = Lists.newLinkedList();
   private final List<Pair<Integer, Integer>> gravelDeposits = Lists.newLinkedList();
   private static final int SECONDARY_AXIS_LEN = 9;
   private static final int Y_AXIS_LEN = 8;
   private static final int MAIN_AXIS_LEN = 24;
   private static final int LOCAL_X_END = 8;
   private static final int LOCAL_Y_END = 7;
   private static final int LOCAL_Z_END = 23;

   public BigTunnel() {
   }

   public BigTunnel(int i, int pieceChainLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      NBTTagList listTag1 = new NBTTagList();
      NBTTagList listTag2 = new NBTTagList();
      NBTTagList listTag3 = new NBTTagList();
      NBTTagList listTag4 = new NBTTagList();
      NBTTagList listTag5 = new NBTTagList();
      NBTTagList listTag6 = new NBTTagList();
      this.smallShaftLeftEntrances.forEach((pos) -> {
         listTag1.appendTag(new NBTTagIntArray(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
      });
      this.smallShaftRightEntrances.forEach((pos) -> {
         listTag2.appendTag(new NBTTagIntArray(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
      });
      this.sideRoomEntrances.forEach((blockBox) -> {
         listTag3.appendTag(blockBox.toNBTTagIntArray());
      });
      this.bigSupports.forEach((z) -> {
         listTag4.appendTag(new NBTTagInt(z.intValue()));
      });
      this.smallSupports.forEach((z) -> {
         listTag5.appendTag(new NBTTagInt(z.intValue()));
      });
      this.gravelDeposits.forEach((pair) -> {
         listTag6.appendTag(new NBTTagIntArray(new int[]{((Integer)pair.getLeft()).intValue(), ((Integer)pair.getRight()).intValue()}));
      });
      tag.setTag("SmallShaftLeftEntrances", listTag1);
      tag.setTag("SmallShaftRightEntrances", listTag2);
      tag.setTag("SideRoomEntrances", listTag3);
      tag.setTag("BigSupports", listTag4);
      tag.setTag("SmallSupports", listTag5);
      tag.setTag("GravelDeposits", listTag6);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      NBTTagList listTag1 = tag.getTagList("SmallShaftLeftEntrances", 11);
      NBTTagList listTag2 = tag.getTagList("SmallShaftRightEntrances", 11);
      NBTTagList listTag3 = tag.getTagList("SideRoomEntrances", 11);
      NBTTagList listTag4 = tag.getTagList("BigSupports", 3);
      NBTTagList listTag5 = tag.getTagList("SmallSupports", 3);
      NBTTagList listTag6 = tag.getTagList("GravelDeposits", 11);

      for(int i = 0; i < listTag1.tagCount(); ++i) {
         this.smallShaftLeftEntrances.add(new BlockPos(listTag1.getIntArrayAt(i)[0], listTag1.getIntArrayAt(i)[1], listTag1.getIntArrayAt(i)[2]));
      }

      for(int i = 0; i < listTag2.tagCount(); ++i) {
         this.smallShaftRightEntrances.add(new BlockPos(listTag2.getIntArrayAt(i)[0], listTag2.getIntArrayAt(i)[1], listTag2.getIntArrayAt(i)[2]));
      }

      for(int i = 0; i < listTag3.tagCount(); ++i) {
         this.sideRoomEntrances.add(new StructureBoundingBox(listTag3.getIntArrayAt(i)));
      }

      for(int i = 0; i < listTag4.tagCount(); ++i) {
         this.bigSupports.add(Integer.valueOf(listTag4.getIntAt(i)));
      }

      for(int i = 0; i < listTag5.tagCount(); ++i) {
         this.smallSupports.add(Integer.valueOf(listTag5.getIntAt(i)));
      }

      for(int i = 0; i < listTag6.tagCount(); ++i) {
         this.gravelDeposits.add(new Pair(listTag6.getIntArrayAt(i)[0], listTag6.getIntArrayAt(i)[1]));
      }

   }

   public static StructureBoundingBox determineBoxPosition(int x, int y, int z, EnumFacing direction) {
      return BoxUtil.boxFromCoordsWithRotation(x, y, z, 9, 8, 24, direction);
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ + 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, direction, this.getComponentType(), this.pieceChainLen);
         }

         int pieceLen = this.getCoordBaseMode().getAxis() == Axis.Z ? this.boundingBox.getZSize() : this.boundingBox.getXSize();
         this.buildSideRoomsLeft(structurePiece, list, random, direction, pieceLen);
         this.buildSideRoomsRight(structurePiece, list, random, direction, pieceLen);
         this.buildSmallShaftsLeft(structurePiece, list, random, direction, pieceLen);
         this.buildSmallShaftsRight(structurePiece, list, random, direction, pieceLen);
         this.buildSupports(random);
         this.buildGravelDeposits(random);
      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 8, 23)) {
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 8, 7, 23, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 8, 0, 23, this.getFloorSelector());
         this.fill(world, box, 1, 1, 0, 7, 4, 23, AIR);
         this.fill(world, box, 2, 4, 0, 6, 5, 23, AIR);
         this.fill(world, box, 3, 6, 0, 5, 6, 23, AIR);
         this.replaceAir(world, box, 1, 0, 0, 7, 0, 23, this.getMainBlock());
         this.generateSmallShaftEntrances(world, box, random);
         this.generateSideRoomOpenings(world, box, random);
         this.generateLegs(world, box, random);
         this.generateBigSupports(world, box, random);
         this.generateSmallSupports(world, box, random);
         this.generateRails(world, box, random);
         this.generateChestCarts(world, box, random, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         this.generateTntCarts(world, box, random);
         this.generateGravelDeposits(world, box, random);
         this.addBiomeDecorations(world, box, random, 1, 0, 0, 7, 6, 23);
         this.addVines(world, box, random, 1, 0, 1, 7, 7, 22);
         this.generateLanterns(world, box, random);
         this.generateCobwebs(world, box, random);
         return true;
      } else {
         return false;
      }
   }

   private void generateLegs(World world, StructureBoundingBox box, Random random) {
      if (this.settings.legVariant == 1) {
         this.generateLegsVariant1(world, box, random);
      } else {
         this.generateLegsVariant2(world, box, random);
      }

   }

   private void generateLegsVariant1(World world, StructureBoundingBox box, Random random) {
      IBlockState supportBlock = this.getSupportBlock();
      if (supportBlock.getBlock() instanceof BlockFence) {
         supportBlock = supportBlock.withProperty(BlockFence.NORTH, Boolean.valueOf(true)).withProperty(BlockFence.SOUTH, Boolean.valueOf(true));
      } else if (supportBlock.getBlock() instanceof BlockWall) {
         supportBlock = supportBlock.withProperty(BlockWall.NORTH, Boolean.valueOf(true)).withProperty(BlockWall.SOUTH, Boolean.valueOf(true));
      }

      this.generateLeg(world, random, box, 1, 0, this.getLegSelector());
      this.replaceAir(world, box, 1, -1, 1, 1, -1, 5, supportBlock);
      this.replaceAir(world, box, 1, -2, 1, 1, -2, 3, supportBlock);
      this.replaceAir(world, box, 1, -3, 1, 1, -3, 2, supportBlock);
      this.replaceAir(world, box, 1, -5, 1, 1, -4, 1, supportBlock);
      this.replaceAir(world, box, 1, -1, 6, 1, -1, 10, supportBlock);
      this.replaceAir(world, box, 1, -2, 8, 1, -2, 10, supportBlock);
      this.replaceAir(world, box, 1, -3, 9, 1, -3, 10, supportBlock);
      this.replaceAir(world, box, 1, -5, 10, 1, -4, 10, supportBlock);
      this.generateLeg(world, random, box, 1, 11, this.getLegSelector());
      this.generateLeg(world, random, box, 1, 12, this.getLegSelector());
      this.replaceAir(world, box, 1, -1, 13, 1, -1, 17, supportBlock);
      this.replaceAir(world, box, 1, -2, 13, 1, -2, 15, supportBlock);
      this.replaceAir(world, box, 1, -3, 13, 1, -3, 14, supportBlock);
      this.replaceAir(world, box, 1, -5, 13, 1, -4, 13, supportBlock);
      this.replaceAir(world, box, 1, -1, 18, 1, -1, 22, supportBlock);
      this.replaceAir(world, box, 1, -2, 20, 1, -2, 22, supportBlock);
      this.replaceAir(world, box, 1, -3, 21, 1, -3, 22, supportBlock);
      this.replaceAir(world, box, 1, -5, 22, 1, -4, 22, supportBlock);
      this.generateLeg(world, random, box, 1, 23, this.getLegSelector());
      this.generateLeg(world, random, box, 7, 0, this.getLegSelector());
      this.replaceAir(world, box, 7, -1, 1, 7, -1, 5, supportBlock);
      this.replaceAir(world, box, 7, -2, 1, 7, -2, 3, supportBlock);
      this.replaceAir(world, box, 7, -3, 1, 7, -3, 2, supportBlock);
      this.replaceAir(world, box, 7, -5, 1, 7, -4, 1, supportBlock);
      this.replaceAir(world, box, 7, -1, 6, 7, -1, 10, supportBlock);
      this.replaceAir(world, box, 7, -2, 8, 7, -2, 10, supportBlock);
      this.replaceAir(world, box, 7, -3, 9, 7, -3, 10, supportBlock);
      this.replaceAir(world, box, 7, -5, 10, 7, -4, 10, supportBlock);
      this.generateLeg(world, random, box, 7, 11, this.getLegSelector());
      this.generateLeg(world, random, box, 7, 12, this.getLegSelector());
      this.replaceAir(world, box, 7, -1, 13, 7, -1, 17, supportBlock);
      this.replaceAir(world, box, 7, -2, 13, 7, -2, 15, supportBlock);
      this.replaceAir(world, box, 7, -3, 13, 7, -3, 14, supportBlock);
      this.replaceAir(world, box, 7, -5, 13, 7, -4, 13, supportBlock);
      this.replaceAir(world, box, 7, -1, 18, 7, -1, 22, supportBlock);
      this.replaceAir(world, box, 7, -2, 20, 7, -2, 22, supportBlock);
      this.replaceAir(world, box, 7, -3, 21, 7, -3, 22, supportBlock);
      this.replaceAir(world, box, 7, -5, 22, 7, -4, 22, supportBlock);
      this.generateLeg(world, random, box, 7, 23, this.getLegSelector());
   }

   private void generateLegsVariant2(World world, StructureBoundingBox box, Random random) {
      for(int z = 0; z <= 23; z += 7) {
         this.generateLeg(world, random, box, 2, z + 1, this.getLegSelector());
         this.generateLeg(world, random, box, 6, z + 1, this.getLegSelector());
         this.replaceAir(world, box, random, 1, -1, z, 7, -1, z + 2, this.getLegSelector());
         this.replaceAir(world, box, random, 2, -1, z + 3, 2, -1, z + 3, this.getLegSelector());
         this.replaceAir(world, box, random, 6, -1, z + 3, 6, -1, z + 3, this.getLegSelector());
         this.replaceAir(world, box, random, 3, -1, z + 3, 5, -1, z + 6, this.getLegSelector());
         this.replaceAir(world, box, random, 2, -1, z + 6, 2, -1, z + 6, this.getLegSelector());
         this.replaceAir(world, box, random, 6, -1, z + 6, 6, -1, z + 6, this.getLegSelector());
         this.replaceAir(world, box, random, 2, -2, z, 2, -2, z, this.getLegSelector());
         this.replaceAir(world, box, random, 6, -2, z, 6, -2, z, this.getLegSelector());
         this.replaceAir(world, box, random, 2, -2, z + 2, 2, -2, z + 2, this.getLegSelector());
         this.replaceAir(world, box, random, 6, -2, z + 2, 6, -2, z + 2, this.getLegSelector());
         this.replaceAir(world, box, random, 1, -2, z + 1, 1, -2, z + 1, this.getLegSelector());
         this.replaceAir(world, box, random, 7, -2, z + 1, 7, -2, z + 1, this.getLegSelector());
         this.replaceAir(world, box, random, 3, -2, z + 1, 3, -2, z + 1, this.getLegSelector());
         this.replaceAir(world, box, random, 5, -2, z + 1, 5, -2, z + 1, this.getLegSelector());
      }

   }

   private void generateGravelDeposits(World world, StructureBoundingBox box, Random random) {
      this.gravelDeposits.forEach((pair) -> {
         int z = ((Integer)pair.getLeft()).intValue();
         int side = ((Integer)pair.getRight()).intValue();
         switch(side) {
         case 0:
         default:
            this.replaceAir(world, box, 1, 1, z, 1, 2, z + 2, this.getGravel());
            this.replaceAir(world, box, 1, 3, z + 1, 1, 3 + random.nextInt(2), z + 1, this.getGravel());
            this.chanceReplaceAir(world, box, random, 0.5F, 1, 3, z, 1, 3, z + 2, this.getGravel());
            this.replaceAir(world, box, 2, 1, z + 1, 2, 2 + random.nextInt(2), z + 1, this.getGravel());
            this.replaceAir(world, box, 2, 1, z, 2, 1 + random.nextInt(2), z + 2, this.getGravel());
            this.chanceReplaceAir(world, box, random, 0.5F, 3, 1, z, 3, 1, z + 2, this.getGravel());
            break;
         case 1:
            this.replaceAir(world, box, 7, 1, z, 7, 2, z + 2, this.getGravel());
            this.replaceAir(world, box, 7, 3, z + 1, 7, 3 + random.nextInt(2), z + 1, this.getGravel());
            this.chanceReplaceAir(world, box, random, 0.5F, 7, 3, z, 7, 3, z + 2, this.getGravel());
            this.replaceAir(world, box, 6, 1, z + 1, 6, 2 + random.nextInt(2), z + 1, this.getGravel());
            this.replaceAir(world, box, 6, 1, z, 6, 1 + random.nextInt(2), z + 2, this.getGravel());
            this.chanceReplaceAir(world, box, random, 0.5F, 5, 1, z, 5, 1, z + 2, this.getGravel());
         }

      });
   }

   private void generateCobwebs(World world, StructureBoundingBox box, Random random) {
      this.smallSupports.forEach((z) -> {
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 2, 3, z.intValue() - 1, 6, 4, z.intValue() + 1, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 3, 5, z.intValue(), 5, 5, z.intValue(), Blocks.WEB.getDefaultState());
      });
      this.bigSupports.forEach((z) -> {
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 1, 1, z.intValue(), 1, 4, z.intValue() + 2, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 7, 1, z.intValue(), 7, 4, z.intValue() + 2, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 2, 5, z.intValue(), 6, 5, z.intValue() + 2, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 2, 4, z.intValue() + 1, 6, 4, z.intValue() + 1, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 3, 6, z.intValue() + 1, 5, 6, z.intValue() + 1, Blocks.WEB.getDefaultState());
      });
   }

   private void generateChestCarts(World world, StructureBoundingBox box, Random random, ResourceLocation lootTableId) {
      for(int z = 0; z <= 23; ++z) {
         if (random.nextFloat() < Configuration.spawnRates.mainShaftChestMinecartSpawnRate) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(4, z), this.getYWithOffset(1), this.getZWithOffset(4, z));
            if (box.isVecInside(blockPos) && world.getBlockState(blockPos.down()) != AIR) {
               EntityMinecartChest chestMinecartEntity = new EntityMinecartChest(world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F));
               chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
               world.spawnEntity(chestMinecartEntity);
            }
         }
      }

   }

   private void generateTntCarts(World world, StructureBoundingBox box, Random random) {
      for(int z = 0; z <= 23; ++z) {
         if (random.nextFloat() < Configuration.spawnRates.mainShaftTntMinecartSpawnRate) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(4, z), this.getYWithOffset(1), this.getZWithOffset(4, z));
            if (box.isVecInside(blockPos) && world.getBlockState(blockPos.down()) != AIR) {
               EntityMinecartTNT tntMinecartEntity = new EntityMinecartTNT(world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F));
               world.spawnEntity(tntMinecartEntity);
            }
         }
      }

   }

   private void generateBigSupports(World world, StructureBoundingBox box, Random random) {
      this.bigSupports.forEach((z) -> {
         this.chanceFill(world, box, random, 0.6F, 1, 1, z.intValue(), 2, 1, z.intValue() + 2, this.getMainSlab());
         this.chanceFill(world, box, random, 0.6F, 6, 1, z.intValue(), 7, 1, z.intValue() + 2, this.getMainSlab());
         this.setBlockState(world, this.getMainBlock(), 1, 1, z.intValue() + 1, box);
         this.setBlockState(world, this.getMainBlock(), 7, 1, z.intValue() + 1, box);
         this.setBlockState(world, this.getMainBlock(), 1, 4, z.intValue() + 1, box);
         this.setBlockState(world, this.getMainBlock(), 7, 4, z.intValue() + 1, box);
         this.fill(world, box, 2, 5, z.intValue() + 1, 6, 5, z.intValue() + 1, this.getMainBlock());
         this.fill(world, box, 1, 2, z.intValue() + 1, 1, 3, z.intValue() + 1, this.getSupportBlock());
         this.fill(world, box, 7, 2, z.intValue() + 1, 7, 3, z.intValue() + 1, this.getSupportBlock());
         this.chanceReplaceNonAir(world, box, random, 0.4F, 2, 5, z.intValue() + 1, 6, 5, z.intValue() + 1, this.getSupportBlock());
      });
   }

   private void generateSmallSupports(World world, StructureBoundingBox box, Random random) {
      IBlockState supportBlock = this.getSupportBlock();
      if (supportBlock.getBlock() instanceof BlockFence) {
         supportBlock = this.getSupportBlock().withProperty(BlockFence.WEST, Boolean.valueOf(true)).withProperty(BlockFence.EAST, Boolean.valueOf(true));
      } else if (supportBlock.getBlock() instanceof BlockWall) {
         supportBlock = this.getSupportBlock().withProperty(BlockWall.WEST, Boolean.valueOf(true)).withProperty(BlockWall.EAST, Boolean.valueOf(true));
      }

      Iterator var5 = this.smallSupports.iterator();

      while(var5.hasNext()) {
         int z = ((Integer)var5.next()).intValue();
         this.setBlockState(world, this.getMainBlock(), 2, 1, z, box);
         this.setBlockState(world, this.getMainBlock(), 6, 1, z, box);
         this.setBlockState(world, this.getSupportBlock(), 2, 2, z, box);
         this.setBlockState(world, this.getSupportBlock(), 6, 2, z, box);
         this.setBlockState(world, this.getMainBlock(), 2, 3, z, box);
         this.setBlockState(world, this.getMainBlock(), 6, 3, z, box);
         this.fill(world, box, 3, 4, z, 5, 4, z, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.5F, 3, 4, z, 5, 4, z, supportBlock);
         this.chanceFill(world, box, random, 0.4F, 2, 3, z, 6, 3, z, supportBlock);
         this.setBlockState(world, supportBlock, 3, 3, z, box);
         this.setBlockState(world, supportBlock, 5, 3, z, box);
      }

   }

   private void generateLanterns(World world, StructureBoundingBox box, Random random) {
      IBlockState lantern = Integrations.getLantern(random);
      if (lantern != null) {
         for(int z = 0; z <= 23; ++z) {
            if (random.nextFloat() < Configuration.spawnRates.lanternSpawnRate * 3.0F && this.getBlockStateFromPos(world, 4, 7, z, box) != AIR) {
               this.setBlockState(world, lantern, 4, 6, z, box);
               if (Integrations.VARIED_COMMODITIES.isLanternVariedCommoditiesCandle(lantern)) {
                  Integrations.VARIED_COMMODITIES.spawnCandleLanternEntity(world, this.getXWithOffset(4, z), this.getYWithOffset(6), this.getZWithOffset(4, z));
               } else if (Integrations.VARIED_COMMODITIES.isLanternVariedCommoditiesLamp(lantern)) {
                  Integrations.VARIED_COMMODITIES.spawnLampLanternEntity(world, this.getXWithOffset(4, z), this.getYWithOffset(6), this.getZWithOffset(4, z));
               }

               z += 20;
            }
         }

      }
   }

   private void generateRails(World world, StructureBoundingBox box, Random random) {
      for(int z = 0; z <= 23; ++z) {
         if (random.nextFloat() < 0.3F) {
            this.fill(world, box, 4, 1, z, 4, 1, z + 1, Blocks.RAIL.getDefaultState());
            ++z;
         }
      }

   }

   private void generateSmallShaftEntrances(World world, StructureBoundingBox box, Random random) {
      this.smallShaftLeftEntrances.forEach((entrancePos) -> {
         int x = entrancePos.getX();
         int y = entrancePos.getY();
         int z = entrancePos.getZ();
         this.fill(world, box, x, y, z, x, y, z + 2, AIR);
         this.setBlockState(world, this.getSupportBlock(), x, y + 1, z, box);
         this.setBlockState(world, this.getSupportBlock(), x, y + 1, z + 2, box);
         this.fill(world, box, x + 1, y, z, x + 1, y + 1, z, this.getSupportBlock());
         this.fill(world, box, x + 1, y, z + 2, x + 1, y + 1, z + 2, this.getSupportBlock());
         this.chanceFill(world, box, random, 0.75F, x, y + 2, z, x + 1, y + 2, z + 2, this.getMainBlock());
         this.fill(world, box, x, y + 1, z + 1, x + 1, y + 1, z + 1, AIR);
         this.replaceAir(world, box, x, y - 1, z, x + 1, y - 1, z + 2, this.getMainBlock());
      });
      this.smallShaftRightEntrances.forEach((entrancePos) -> {
         int x = entrancePos.getX();
         int y = entrancePos.getY();
         int z = entrancePos.getZ();
         this.replaceAir(world, box, x, y - 1, z, x + 1, y - 1, z + 2, this.getMainBlock());
         this.fill(world, box, x + 1, y, z, x + 1, y, z + 2, AIR);
         this.setBlockState(world, this.getSupportBlock(), x + 1, y + 1, z, box);
         this.setBlockState(world, this.getSupportBlock(), x + 1, y + 1, z + 2, box);
         this.fill(world, box, x, y, z, x, y + 1, z, this.getSupportBlock());
         this.fill(world, box, x, y, z + 2, x, y + 1, z + 2, this.getSupportBlock());
         this.chanceFill(world, box, random, 0.75F, x, y + 2, z, x + 1, y + 2, z + 2, this.getMainBlock());
         this.fill(world, box, x, y + 1, z + 1, x + 1, y + 1, z + 1, AIR);
      });
   }

   private void generateSideRoomOpenings(World world, StructureBoundingBox box, Random random) {
      this.sideRoomEntrances.forEach((entranceBox) -> {
         this.replaceAir(world, box, random, entranceBox.minX, 0, entranceBox.minZ, entranceBox.maxX, 0, entranceBox.maxZ, this.getBrickSelector());
         switch(random.nextInt(3)) {
         case 0:
            this.fill(world, box, entranceBox.minX, entranceBox.minY, entranceBox.minZ + 2, entranceBox.maxX, entranceBox.maxY, entranceBox.maxZ - 2, AIR);
            return;
         case 1:
            this.fill(world, box, entranceBox.minX, entranceBox.minY, entranceBox.minZ + 2, entranceBox.maxX, entranceBox.maxY - 1, entranceBox.minZ + 2, AIR);
            this.fill(world, box, entranceBox.minX, entranceBox.minY, entranceBox.minZ + 4, entranceBox.maxX, entranceBox.maxY - 1, entranceBox.minZ + 5, AIR);
            this.fill(world, box, entranceBox.minX, entranceBox.minY, entranceBox.minZ + 7, entranceBox.maxX, entranceBox.maxY - 1, entranceBox.minZ + 7, AIR);
            return;
         case 2:
         default:
         }
      });
   }

   private void buildGravelDeposits(Random random) {
      for(int z = 0; z <= 21; ++z) {
         int r = random.nextInt(20);
         if (r == 0) {
            this.gravelDeposits.add(new Pair(z, Integer.valueOf(0)));
            z += 5;
         } else if (r == 1) {
            this.gravelDeposits.add(new Pair(z, Integer.valueOf(1)));
            z += 5;
         }
      }

   }

   private void buildSupports(Random random) {
      int counter = 0;
      int MAX_COUNT = 10;

      for(int z = 0; z <= 21; ++z) {
         ++counter;
         boolean blockingEntrance = false;

         for(BlockPos entrancePos : this.smallShaftLeftEntrances) {
            if (entrancePos.getZ() <= z + 2 && z <= entrancePos.getZ() + 2) {
               blockingEntrance = true;
               break;
            }
         }

         for(BlockPos entrancePos : this.smallShaftRightEntrances) {
            if (entrancePos.getZ() <= z + 2 && z <= entrancePos.getZ() + 2) {
               blockingEntrance = true;
               break;
            }
         }

         if (!blockingEntrance) {
            int r = random.nextInt(8);
            if (r != 0 && counter < 10) {
               if (r == 1) {
                  this.smallSupports.add(Integer.valueOf(z));
                  counter = 0;
                  z += 3;
               }
            } else {
               this.bigSupports.add(Integer.valueOf(z));
               counter = 0;
               z += 3;
            }
         }
      }

   }

   private void buildSideRoomsLeft(StructureComponent structurePiece, List<StructureComponent> list, Random random, EnumFacing direction, int pieceLen) {
      for(int n = 0; n < pieceLen - 1 - 10; ++n) {
         if (random.nextFloat() < Configuration.spawnRates.workstationSpawnRate) {
            switch(direction) {
            case NORTH:
            default:
               EnumFacing nextPieceDirection = EnumFacing.EAST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.minX - 5, this.boundingBox.minY, this.boundingBox.maxZ - n - 9, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(0, 1, n, 0, 3, n + 9));
               }
               break;
            case SOUTH:
               EnumFacing nextPieceDirection = EnumFacing.WEST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.maxX + 5, this.boundingBox.minY, this.boundingBox.minZ + n + 9, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(8, 1, n, 8, 3, n + 9));
               }
               break;
            case WEST:
               EnumFacing nextPieceDirection = EnumFacing.NORTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.maxX - n - 9, this.boundingBox.minY, this.boundingBox.maxZ + 5, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(8, 1, n, 8, 3, n + 9));
               }
               break;
            case EAST:
               EnumFacing nextPieceDirection = EnumFacing.SOUTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.minX + n + 9, this.boundingBox.minY, this.boundingBox.minZ - 5, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(0, 1, n, 0, 3, n + 9));
               }
            }

            n += 10;
         }
      }

   }

   private void buildSideRoomsRight(StructureComponent structurePiece, List<StructureComponent> list, Random random, EnumFacing direction, int pieceLen) {
      for(int n = 0; n < pieceLen - 1 - 10; ++n) {
         if (random.nextFloat() < Configuration.spawnRates.workstationSpawnRate) {
            switch(direction) {
            case NORTH:
            default:
               EnumFacing nextPieceDirection = EnumFacing.WEST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.maxX + 5, this.boundingBox.minY, this.boundingBox.maxZ - n, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(8, 1, n, 8, 3, n + 9));
               }
               break;
            case SOUTH:
               EnumFacing nextPieceDirection = EnumFacing.EAST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.minX - 5, this.boundingBox.minY, this.boundingBox.minZ + n, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(0, 1, n, 0, 3, n + 9));
               }
               break;
            case WEST:
               EnumFacing nextPieceDirection = EnumFacing.SOUTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.maxX - n, this.boundingBox.minY, this.boundingBox.minZ - 5, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(0, 1, n, 0, 3, n + 9));
               }
               break;
            case EAST:
               EnumFacing nextPieceDirection = EnumFacing.NORTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSideRoomPiece(structurePiece, list, random, this.boundingBox.minX + n, this.boundingBox.minY, this.boundingBox.maxZ + 5, nextPieceDirection, this.getComponentType(), this.pieceChainLen);
               if (newPiece != null) {
                  this.sideRoomEntrances.add(new StructureBoundingBox(8, 1, n, 8, 3, n + 9));
               }
            }

            n += 10;
         }
      }

   }

   private void buildSmallShaftsLeft(StructureComponent structurePiece, List<StructureComponent> list, Random random, EnumFacing direction, int pieceLen) {
      for(int n = 0; n < pieceLen - 1 - 4; ++n) {
         if (random.nextFloat() < Configuration.spawnRates.smallShaftSpawnRate) {
            switch(direction) {
            case NORTH:
            default:
               EnumFacing nextPieceDirection = EnumFacing.WEST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - n, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n + 1));
               }
               break;
            case SOUTH:
               EnumFacing nextPieceDirection = EnumFacing.EAST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + n, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftRightEntrances.add(new BlockPos(7, 1, n + 1));
               }
               break;
            case WEST:
               EnumFacing nextPieceDirection = EnumFacing.SOUTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX - n, this.boundingBox.minY, this.boundingBox.maxZ + 1, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftRightEntrances.add(new BlockPos(7, 1, n + 1));
               }
               break;
            case EAST:
               EnumFacing nextPieceDirection = EnumFacing.NORTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX + n, this.boundingBox.minY, this.boundingBox.minZ - 1, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n + 1));
               }
            }

            n += random.nextInt(7) + 5;
         }
      }

   }

   private void buildSmallShaftsRight(StructureComponent structurePiece, List<StructureComponent> list, Random random, EnumFacing direction, int pieceLen) {
      for(int n = 5; n < pieceLen; ++n) {
         if (random.nextFloat() < Configuration.spawnRates.smallShaftSpawnRate) {
            switch(direction) {
            case NORTH:
            default:
               EnumFacing nextPieceDirection = EnumFacing.EAST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - n, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftRightEntrances.add(new BlockPos(7, 1, n - 3));
               }
               break;
            case SOUTH:
               EnumFacing nextPieceDirection = EnumFacing.WEST;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + n, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n - 3));
               }
               break;
            case WEST:
               EnumFacing nextPieceDirection = EnumFacing.NORTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX - n, this.boundingBox.minY, this.boundingBox.minZ - 1, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftLeftEntrances.add(new BlockPos(0, 1, n - 3));
               }
               break;
            case EAST:
               EnumFacing nextPieceDirection = EnumFacing.SOUTH;
               StructureComponent newPiece = BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX + n, this.boundingBox.minY, this.boundingBox.maxZ + 1, nextPieceDirection, this.getComponentType(), 0);
               if (newPiece != null) {
                  this.smallShaftRightEntrances.add(new BlockPos(7, 1, n - 3));
               }
            }

            n += random.nextInt(7) + 5;
         }
      }

   }
}
