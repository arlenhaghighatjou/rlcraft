package com.github.fnar.minecraft;

import com.github.fnar.minecraft.block.BlockMapper1_12;
import com.github.fnar.minecraft.block.BlockType;
import com.github.fnar.minecraft.block.SingleBlockBrush;
import com.github.fnar.minecraft.block.decorative.Plant;
import com.github.fnar.minecraft.block.decorative.Skull;
import com.github.fnar.minecraft.block.normal.StairsBlock;
import com.github.fnar.minecraft.block.redstone.DoorBlock;
import com.github.fnar.minecraft.block.spawner.SpawnPotentialMapper1_12;
import com.github.fnar.minecraft.block.spawner.Spawner;
import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.minecraft.item.mapper.ItemMapper1_12;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import greymerk.roguelike.dungeon.Dungeon;
import greymerk.roguelike.treasure.TreasureChest;
import greymerk.roguelike.treasure.TreasureManager;
import greymerk.roguelike.util.DyeColor;
import greymerk.roguelike.worldgen.BlockBrush;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.Direction;
import greymerk.roguelike.worldgen.MetaBlock1_12;
import greymerk.roguelike.worldgen.PositionInfo;
import greymerk.roguelike.worldgen.VanillaStructure;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class WorldEditor1_12 implements WorldEditor {
   private static final Logger logger = LogManager.getLogger("roguelike");
   private static final Set<Material> validGroundBlocks = Sets.newHashSet(new Material[]{Material.GRASS, Material.GROUND, Material.ROCK, Material.SAND, Material.ICE, Material.PACKED_ICE, Material.SNOW, Material.CLAY});
   private final World world;
   private final Map<BlockType, Integer> stats = new HashMap();
   private final EaglercraftRandom random;
   private final TreasureManager treasureManager;

   public WorldEditor1_12(World world) {
      this.world = world;
      this.random = new EaglercraftRandom((long)Objects.hash(new Object[]{this.getSeed()}));
      this.treasureManager = new TreasureManager(this.random);
   }

   public void setSkull(WorldEditor editor, Coord cursor, Direction dir, Skull type) {
      SingleBlockBrush skullBlock = BlockType.SKULL.getBrush();
      skullBlock.setFacing(Direction.UP);
      if (skullBlock.stroke(editor, cursor)) {
         TileEntity tileEntity = editor.getTileEntity(cursor);
         if (tileEntity != null) {
            if (tileEntity instanceof TileEntitySkull) {
               TileEntitySkull tileEntitySkull = (TileEntitySkull)tileEntity;
               setSkullType(tileEntitySkull, type);
               setSkullRotation(editor.getRandom(), tileEntitySkull, dir);
            }
         }
      }
   }

   public static void setSkullType(TileEntitySkull skull, Skull type) {
      skull.setType(getSkullId(type));
   }

   public static void setSkullRotation(EaglercraftRandom rand, TileEntitySkull skull, Direction dir) {
      int directionValue = getDirectionValue(dir);
      directionValue = directionValue + -1 + rand.nextInt(3);
      directionValue = directionValue % 16;
      skull.setSkullRotation(directionValue);
   }

   public static int getSkullId(Skull type) {
      switch(type) {
      case SKELETON:
      default:
         return 0;
      case WITHER:
         return 1;
      case ZOMBIE:
         return 2;
      case STEVE:
         return 3;
      case CREEPER:
         return 4;
      }
   }

   public static int getDirectionValue(Direction dir) {
      switch(dir) {
      case NORTH:
      default:
         return 0;
      case EAST:
         return 4;
      case SOUTH:
         return 8;
      case WEST:
         return 12;
      }
   }

   public boolean isSolidBlock(Coord coord) {
      return this.getBlockStateAt(coord).getMaterial().isSolid();
   }

   private IBlockState getBlockStateAt(Coord coord) {
      return this.world.getBlockState(this.getBlockPos(coord));
   }

   public boolean isOpaqueBlock(Coord coord) {
      return this.getBlockStateAt(coord).getMaterial().isOpaque();
   }

   public boolean isOpaqueCubeBlock(Coord coord) {
      return this.getBlockStateAt(coord).isOpaqueCube();
   }

   public boolean isBlockOfTypeAt(BlockType blockType, Coord coord) {
      return BlockMapper1_12.map(blockType).getBlock() == this.getBlockStateAt(coord).getBlock();
   }

   public boolean isMaterialAt(com.github.fnar.minecraft.block.Material material, Coord coord) {
      return false;
   }

   public EaglercraftRandom getRandom() {
      return this.random;
   }

   public EaglercraftRandom getRandom(Coord pos) {
      this.random.setSeed((long)this.getSeed(pos));
      return this.random;
   }

   private BlockPos getBlockPos(Coord pos) {
      return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
   }

   public boolean setBlock(Coord coord, SingleBlockBrush singleBlockBrush, boolean fillAir, boolean replaceSolid) {
      if (!this.isBlockOfTypeAt(BlockType.CHEST, coord) && !this.isBlockOfTypeAt(BlockType.TRAPPED_CHEST, coord) && !this.isBlockOfTypeAt(BlockType.MOB_SPAWNER, coord)) {
         boolean isAir = this.isBlockOfTypeAt(BlockType.AIR, coord);
         if (!fillAir && isAir) {
            return false;
         } else if (!replaceSolid && !isAir) {
            return false;
         } else {
            MetaBlock1_12 metaBlock = getMetaBlock(singleBlockBrush);

            try {
               this.world.setBlockState(this.getBlockPos(coord), metaBlock.getState(), metaBlock.getFlag());
            } catch (NullPointerException var8) {
               LogManager.getLogger("roguelike").error(var8);
            }

            BlockType blockType = singleBlockBrush.getBlockType();
            if (blockType != null) {
               this.stats.merge(blockType, Integer.valueOf(1), Integer::sum);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static MetaBlock1_12 getMetaBlock(SingleBlockBrush singleBlockBrush) {
      JsonElement json = singleBlockBrush.getJson();
      if (json == null) {
         return BlockMapper1_12.map(singleBlockBrush);
      } else if (singleBlockBrush instanceof StairsBlock) {
         return BlockMapper1_12.mapStairs((StairsBlock)singleBlockBrush);
      } else {
         return singleBlockBrush instanceof DoorBlock ? BlockMapper1_12.mapDoor((DoorBlock)singleBlockBrush) : new MetaBlock1_12(json);
      }
   }

   public boolean isAirBlock(Coord pos) {
      return this.world.isAirBlock(this.getBlockPos(pos));
   }

   public long getSeed() {
      return this.world.getSeed();
   }

   public void fillDown(Coord origin, BlockBrush blocks) {
      Coord cursor = origin.copy();

      while(!this.isOpaqueCubeBlock(cursor) && cursor.getY() > 1) {
         blocks.stroke(this, cursor);
         cursor.down();
      }

   }

   public TileEntity getTileEntity(Coord pos) {
      return this.world.getTileEntity(this.getBlockPos(pos));
   }

   public boolean isValidGroundBlock(Coord pos) {
      return validGroundBlocks.contains(this.getBlockStateAt(pos).getMaterial());
   }

   public Map<BlockType, Integer> getStats() {
      return this.stats;
   }

   public boolean canPlace(SingleBlockBrush block, Coord pos, Direction dir) {
      return this.isAirBlock(pos) && BlockMapper1_12.map(block.getBlockType()).getBlock().canPlaceBlockOnSide(this.world, this.getBlockPos(pos), BlockMapper1_12.getFacing(dir));
   }

   public PositionInfo getInfo(Coord pos) {
      return new PositionInfo1_12(this.world, pos);
   }

   public Coord findNearestStructure(VanillaStructure type, Coord pos) {
      ChunkProviderServer chunkProvider = ((WorldServer)this.world).getChunkProvider();
      String structureName = VanillaStructure.getName(type);
      BlockPos structurebp = null;

      try {
         structurebp = chunkProvider.getNearestStructurePos(this.world, structureName, this.getBlockPos(pos), false);
      } catch (NullPointerException var7) {
         ;
      }

      return structurebp == null ? null : new Coord(structurebp.getX(), structurebp.getY(), structurebp.getZ());
   }

   public String toString() {
      return (String)this.stats.entrySet().stream().map((pair) -> {
         return ((BlockType)pair.getKey()).toString() + ": " + pair.getValue() + "\n";
      }).collect(Collectors.joining());
   }

   public void setBedColorAt(Coord cursor, DyeColor color) {
      TileEntity tileEntity = this.getTileEntity(cursor);
      if (tileEntity instanceof TileEntityBed) {
         ((TileEntityBed)tileEntity).setColor(BlockMapper1_12.toEnumDyeColor(color));
      } else {
         logger.error("Failed to paint bed at position {} to become color {}. Current block at position is {}.", cursor, color, this.getBlockStateAt(cursor));
      }

   }

   public void setItem(Coord pos, int slot, RldItemStack itemStack) {
      this.setItem(pos, slot, (new ItemMapper1_12()).map(itemStack));
   }

   public void setItem(Coord pos, int slot, ItemStack itemStack) {
      TileEntity tileEntity = this.getTileEntity(pos);
      if (tileEntity != null) {
         if (tileEntity instanceof TileEntityLockableLoot) {
            try {
               ((TileEntityLockableLoot)tileEntity).setInventorySlotContents(slot, itemStack);
            } catch (NullPointerException var6) {
               logger.error("Could not place item {} at position {}. BlockState at pos: {}.", itemStack, pos, this.getBlockStateAt(pos));
            }

         }
      }
   }

   public void setFlowerPotContent(Coord pos, Plant choice) {
      TileEntity potEntity = this.getTileEntity(pos);
      if (potEntity != null) {
         if (potEntity instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot flowerPot = (TileEntityFlowerPot)potEntity;
            ItemStack flowerItem = getFlowerItem(choice);
            flowerPot.setItemStack(flowerItem);
         }
      }
   }

   private static ItemStack getFlowerItem(Plant type) {
      switch(type) {
      case POPPY:
         return new ItemStack(Blocks.RED_FLOWER, 1, 0);
      case ORCHID:
         return new ItemStack(Blocks.RED_FLOWER, 1, 1);
      case ALLIUM:
         return new ItemStack(Blocks.RED_FLOWER, 1, 2);
      case BLUET:
         return new ItemStack(Blocks.RED_FLOWER, 1, 3);
      case REDTULIP:
         return new ItemStack(Blocks.RED_FLOWER, 1, 4);
      case ORANGETULIP:
         return new ItemStack(Blocks.RED_FLOWER, 1, 5);
      case WHITETULIP:
         return new ItemStack(Blocks.RED_FLOWER, 1, 6);
      case PINKTULIP:
         return new ItemStack(Blocks.RED_FLOWER, 1, 7);
      case DAISY:
         return new ItemStack(Blocks.RED_FLOWER, 1, 8);
      case RED_MUSHROOM:
         return new ItemStack(Blocks.RED_MUSHROOM);
      case BROWN_MUSHROOM:
         return new ItemStack(Blocks.BROWN_MUSHROOM);
      case CACTUS:
         return new ItemStack(Blocks.CACTUS);
      case OAK_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 0);
      case SPRUCE_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 1);
      case BIRCH_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 2);
      case JUNGLE_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 3);
      case ACACIA_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 4);
      case DARKOAK_SAPLING:
         return new ItemStack(Blocks.SAPLING, 1, 5);
      case SHRUB:
         return new ItemStack(Blocks.TALLGRASS, 1, 0);
      case FERN:
         return new ItemStack(Blocks.TALLGRASS, 1, 2);
      case DANDELION:
      default:
         return new ItemStack(Blocks.YELLOW_FLOWER);
      }
   }

   public void setLootTable(Coord pos, String table) {
      ((TileEntityChest)this.getTileEntity(pos)).setLootTable(new ResourceLocation(table), (long)this.getSeed(pos));
   }

   public int getCapacity(TreasureChest treasureChest) {
      return ((TileEntityLockableLoot)this.getTileEntity(treasureChest.getCoord())).getSizeInventory();
   }

   public boolean isEmptySlot(TreasureChest treasureChest, int slot) {
      return ((TileEntityLockableLoot)this.getTileEntity(treasureChest.getCoord())).getStackInSlot(slot).isEmpty();
   }

   public void generateSpawner(Spawner spawner, Coord cursor) {
      Coord pos = cursor.copy();
      spawner.stroke(this, pos);
      TileEntity tileentity = this.getTileEntity(pos);
      if (tileentity instanceof TileEntityMobSpawner) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setInteger("x", pos.getX());
         nbt.setInteger("y", pos.getY());
         nbt.setInteger("z", pos.getZ());
         nbt.setTag("SpawnPotentials", SpawnPotentialMapper1_12.mapToNbt(spawner.getPotentials(), this.getRandom(), Dungeon.getLevel(pos.getY())));
         TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)tileentity;
         MobSpawnerBaseLogic spawnerLogic = tileEntity.getSpawnerBaseLogic();
         spawnerLogic.readFromNBT(nbt);
         spawnerLogic.updateSpawner();
         tileentity.markDirty();
      }
   }

   public TreasureManager getTreasureManager() {
      return this.treasureManager;
   }
}
