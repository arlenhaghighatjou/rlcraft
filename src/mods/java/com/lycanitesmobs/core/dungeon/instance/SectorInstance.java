package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.block.effect.BlockFrostCloud;
import com.lycanitesmobs.core.block.effect.BlockPoisonCloud;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.SectorLayer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.spawner.MobSpawn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

public class SectorInstance {
   public DungeonLayout layout;
   public DungeonSector dungeonSector;
   public SectorConnector parentConnector;
   public List<SectorConnector> connectors = new ArrayList();
   protected Vec3i roomSize;
   protected Vec3i occupiedSize;
   public DungeonTheme theme;
   public IBlockState lightBlock;
   public IBlockState torchBlock;
   public IBlockState stairBlock;
   public IBlockState pitBlock;
   public IBlockState airBlock;
   public int chunksBuilt = 0;

   public SectorInstance(DungeonLayout layout, DungeonSector dungeonSector, Random random) {
      this.layout = layout;
      this.dungeonSector = dungeonSector;
      this.roomSize = this.dungeonSector.getRandomSize(random);
      this.occupiedSize = new Vec3i(this.roomSize.getX() + Math.max(1, this.dungeonSector.padding.getX()), this.roomSize.getY() + this.dungeonSector.padding.getY(), this.roomSize.getZ() + Math.max(1, this.dungeonSector.padding.getZ()));
   }

   public void connect(SectorConnector parentConnector) {
      this.parentConnector = parentConnector;
   }

   private int stairOffset() {
      int offset = -(this.getRoomSize().getY() * 2);
      if (this.parentConnector.position.getY() > 62) {
         offset += -(this.parentConnector.position.getY() - 62);
      }

      return offset;
   }

   public void init(Random random) {
      if (this.parentConnector == null) {
         throw new RuntimeException("[Dungeon] Tried to initialise a Sector Instance with a null Parent Connector: " + this);
      } else {
         this.parentConnector.childSector = this;
         this.parentConnector.closed = true;
         this.layout.openConnectors.remove(this.parentConnector);
         if (!this.dungeonSector.changeTheme && this.parentConnector.parentSector != null) {
            this.theme = this.parentConnector.parentSector.theme;
         } else {
            this.theme = this.layout.dungeonInstance.schematic.getRandomTheme(random);
         }

         this.lightBlock = this.theme.getLight('B', random);
         this.torchBlock = this.theme.getTorch('B', random);
         this.stairBlock = this.theme.getStairs('B', random);
         this.pitBlock = this.theme.getPit('B', random);
         this.airBlock = this.theme.getAir('B', random);
         BlockPos boundsMin = this.getRoomBoundsMin();
         BlockPos boundsMax = this.getRoomBoundsMax();
         Vec3i size = this.getRoomSize();
         int centerX = boundsMin.getX() + Math.round((float)size.getX() / 2.0F);
         int centerZ = boundsMin.getZ() + Math.round((float)size.getZ() / 2.0F);
         int upperConnectorY = this.parentConnector.position.getY() + this.getRoomSize().getY();
         if (upperConnectorY < 255) {
            BlockPos blockPos = new BlockPos(centerX, upperConnectorY, centerZ);
            this.addConnector(blockPos, this.parentConnector.level + 1, EnumFacing.UP);
         }

         if ("corridor".equalsIgnoreCase(this.dungeonSector.type) || "room".equalsIgnoreCase(this.dungeonSector.type) || "tower".equalsIgnoreCase(this.dungeonSector.type) || "entrance".equalsIgnoreCase(this.dungeonSector.type) || "bossRoom".equalsIgnoreCase(this.dungeonSector.type)) {
            BlockPos frontPos = this.parentConnector.position;
            EnumFacing frontFacing = EnumFacing.SOUTH;
            BlockPos backPos = this.parentConnector.position;
            EnumFacing backFacing = EnumFacing.NORTH;
            if (this.parentConnector.facing != EnumFacing.SOUTH && this.parentConnector.facing != EnumFacing.UP) {
               if (this.parentConnector.facing == EnumFacing.EAST) {
                  frontPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  frontFacing = EnumFacing.EAST;
                  backPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  backFacing = EnumFacing.WEST;
               } else if (this.parentConnector.facing == EnumFacing.NORTH) {
                  frontPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
                  frontFacing = EnumFacing.NORTH;
                  backPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
                  backFacing = EnumFacing.SOUTH;
               } else if (this.parentConnector.facing == EnumFacing.WEST) {
                  frontPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  frontFacing = EnumFacing.WEST;
                  backPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  backFacing = EnumFacing.EAST;
               }
            } else {
               frontPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
               frontFacing = EnumFacing.SOUTH;
               backPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
               backFacing = EnumFacing.NORTH;
            }

            this.addConnector(frontPos, this.parentConnector.level, frontFacing);
            if ("tower".equalsIgnoreCase(this.dungeonSector.type)) {
               this.addConnector(backPos, this.parentConnector.level, backFacing);
            }

            if ("room".equalsIgnoreCase(this.dungeonSector.type) || "tower".equalsIgnoreCase(this.dungeonSector.type)) {
               BlockPos leftPos = this.parentConnector.position;
               EnumFacing leftFacing = EnumFacing.WEST;
               BlockPos rightPos = this.parentConnector.position;
               EnumFacing rightFacing = EnumFacing.EAST;
               if (this.parentConnector.facing != EnumFacing.SOUTH && this.parentConnector.facing != EnumFacing.NORTH && this.parentConnector.facing != EnumFacing.UP) {
                  if (this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
                     leftPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMax.getZ() + 1);
                     leftFacing = EnumFacing.SOUTH;
                     rightPos = new BlockPos(this.getConnectorOffset(random, size.getX(), boundsMin.getX()), this.parentConnector.position.getY(), boundsMin.getZ() - 1);
                     rightFacing = EnumFacing.NORTH;
                  }
               } else {
                  leftPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  leftFacing = EnumFacing.WEST;
                  rightPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), this.getConnectorOffset(random, size.getZ(), boundsMin.getZ()));
                  rightFacing = EnumFacing.EAST;
               }

               this.addConnector(leftPos, this.parentConnector.level, leftFacing);
               this.addConnector(rightPos, this.parentConnector.level, rightFacing);
            }
         }

         if ("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
            int y = this.parentConnector.position.getY() + this.stairOffset();
            if (y > 0) {
               BlockPos blockPos = new BlockPos(centerX, y, boundsMax.getZ() + 1);
               if (this.parentConnector.facing == EnumFacing.EAST) {
                  blockPos = new BlockPos(boundsMax.getX() + 1, y, centerZ);
               } else if (this.parentConnector.facing == EnumFacing.NORTH) {
                  blockPos = new BlockPos(centerX, y, boundsMin.getZ() - 1);
               } else if (this.parentConnector.facing == EnumFacing.WEST) {
                  blockPos = new BlockPos(boundsMin.getX() - 1, y, centerZ);
               }

               this.addConnector(blockPos, this.parentConnector.level - 1, this.parentConnector.facing);
            }
         }

      }
   }

   public int getConnectorOffset(Random random, int length, int start) {
      int entrancePadding = 2;
      return "room".equalsIgnoreCase(this.dungeonSector.type) && length > entrancePadding * 2 ? start + entrancePadding + random.nextInt(length - entrancePadding * 2) + 1 : start + Math.round((float)length / 2.0F);
   }

   public SectorConnector addConnector(BlockPos blockPos, int level, EnumFacing facing) {
      SectorConnector connector = new SectorConnector(blockPos, this, level, facing);
      this.connectors.add(connector);
      return connector;
   }

   public SectorConnector getRandomConnector(Random random, SectorInstance sectorInstance) {
      List<SectorConnector> openConnectors = this.getOpenConnectors(sectorInstance);
      if (openConnectors.isEmpty()) {
         return null;
      } else {
         return openConnectors.size() == 1 ? (SectorConnector)openConnectors.get(0) : (SectorConnector)openConnectors.get(random.nextInt(openConnectors.size()));
      }
   }

   public List<SectorConnector> getOpenConnectors(SectorInstance sectorInstance) {
      List<SectorConnector> openConnectors = new ArrayList();

      for(SectorConnector connector : this.connectors) {
         if (connector.canConnect(this.layout, sectorInstance)) {
            openConnectors.add(connector);
         }
      }

      return openConnectors;
   }

   public List<ChunkPos> getChunkPositions() {
      ChunkPos minChunkPos = new ChunkPos(this.getOccupiedBoundsMin().add(-9, 0, -9));
      ChunkPos maxChunkPos = new ChunkPos(this.getOccupiedBoundsMax().add(-7, 0, -7));
      List<ChunkPos> chunkPosList = new ArrayList();

      for(int x = minChunkPos.x; x <= maxChunkPos.x; ++x) {
         for(int z = minChunkPos.z; z <= maxChunkPos.z; ++z) {
            chunkPosList.add(new ChunkPos(x, z));
         }
      }

      return chunkPosList;
   }

   public List<SectorInstance> getNearbySectors() {
      List<SectorInstance> nearbySectors = new ArrayList();

      for(SectorInstance nearbySector : this.layout.sectors) {
         if (!nearbySectors.contains(nearbySector)) {
            nearbySectors.add(nearbySector);
         }
      }

      return nearbySectors;
   }

   public boolean collidesWith(SectorInstance sectorInstance) {
      if (sectorInstance != this && sectorInstance != this.parentConnector.parentSector) {
         BlockPos boundsMin = this.getOccupiedBoundsMin();
         BlockPos boundsMax = this.getOccupiedBoundsMax();
         BlockPos targetMin = sectorInstance.getOccupiedBoundsMin();
         BlockPos targetMax = sectorInstance.getOccupiedBoundsMax();
         if (boundsMin.getY() != targetMin.getY() && !sectorInstance.dungeonSector.type.equals("stairs")) {
            return false;
         } else {
            boolean withinX = boundsMin.getX() >= targetMin.getX() && boundsMin.getX() <= targetMax.getX();
            if (!withinX) {
               withinX = boundsMax.getX() >= targetMin.getX() && boundsMax.getX() <= targetMax.getX();
            }

            if (!withinX) {
               return false;
            } else {
               boolean withinZ = boundsMin.getZ() >= targetMin.getZ() && boundsMin.getZ() <= targetMax.getZ();
               if (!withinZ) {
                  withinZ = boundsMax.getZ() >= targetMin.getZ() && boundsMax.getZ() <= targetMax.getZ();
               }

               return withinZ;
            }
         }
      } else {
         return false;
      }
   }

   public Vec3i getRoomSize() {
      return this.parentConnector.facing != EnumFacing.EAST && this.parentConnector.facing != EnumFacing.WEST ? this.roomSize : new Vec3i(this.roomSize.getZ(), this.roomSize.getY(), this.roomSize.getX());
   }

   public Vec3i getOccupiedSize() {
      return this.parentConnector.facing != EnumFacing.EAST && this.parentConnector.facing != EnumFacing.WEST ? this.occupiedSize : new Vec3i(this.occupiedSize.getZ(), this.occupiedSize.getY(), this.occupiedSize.getX());
   }

   public BlockPos getBoundsMin(Vec3i boundsSize) {
      BlockPos bounds = new BlockPos(this.parentConnector.position);
      if (this.parentConnector.facing == EnumFacing.UP) {
         bounds = bounds.add(-((int)Math.ceil((double)boundsSize.getX() / 2.0D)), 0, -((int)Math.ceil((double)boundsSize.getZ() / 2.0D)));
      } else if (this.parentConnector.facing == EnumFacing.SOUTH) {
         bounds = bounds.add(-((int)Math.ceil((double)boundsSize.getX() / 2.0D)), 0, 0);
      } else if (this.parentConnector.facing == EnumFacing.EAST) {
         bounds = bounds.add(0, 0, -((int)Math.ceil((double)boundsSize.getZ() / 2.0D)));
      } else if (this.parentConnector.facing == EnumFacing.NORTH) {
         bounds = bounds.add(-((int)Math.ceil((double)boundsSize.getX() / 2.0D)), 0, -boundsSize.getZ());
      } else if (this.parentConnector.facing == EnumFacing.WEST) {
         bounds = bounds.add(-boundsSize.getX(), 0, -((int)Math.ceil((double)boundsSize.getZ() / 2.0D)));
      }

      return bounds;
   }

   public BlockPos getBoundsMax(Vec3i boundsSize) {
      BlockPos bounds = new BlockPos(this.parentConnector.position);
      if (this.parentConnector.facing == EnumFacing.UP) {
         bounds = bounds.add((int)Math.ceil((double)boundsSize.getX() / 2.0D), boundsSize.getY(), (int)Math.ceil((double)boundsSize.getZ() / 2.0D));
      } else if (this.parentConnector.facing == EnumFacing.SOUTH) {
         bounds = bounds.add((int)Math.floor((double)boundsSize.getX() / 2.0D), boundsSize.getY(), boundsSize.getZ());
      } else if (this.parentConnector.facing == EnumFacing.EAST) {
         bounds = bounds.add(boundsSize.getX(), boundsSize.getY(), (int)Math.floor((double)boundsSize.getZ() / 2.0D));
      } else if (this.parentConnector.facing == EnumFacing.NORTH) {
         bounds = bounds.add((int)Math.floor((double)boundsSize.getX() / 2.0D), boundsSize.getY(), 0);
      } else if (this.parentConnector.facing == EnumFacing.WEST) {
         bounds = bounds.add(0, boundsSize.getY(), (int)Math.floor((double)boundsSize.getZ() / 2.0D));
      }

      return bounds;
   }

   public BlockPos getOccupiedBoundsMin() {
      BlockPos occupiedBoundsMin = this.getBoundsMin(this.getOccupiedSize());
      if ("stairs".equals(this.dungeonSector.type)) {
         occupiedBoundsMin = occupiedBoundsMin.add(0, this.stairOffset(), 0);
      }

      return occupiedBoundsMin;
   }

   public BlockPos getOccupiedBoundsMax() {
      return this.getBoundsMax(this.getOccupiedSize());
   }

   public BlockPos getRoomBoundsMin() {
      return this.getBoundsMin(this.getRoomSize());
   }

   public BlockPos getRoomBoundsMax() {
      return this.getBoundsMax(this.getRoomSize());
   }

   public BlockPos getCenter() {
      BlockPos startPos = this.getRoomBoundsMin();
      Vec3i size = this.getRoomSize();
      int centerX = startPos.getX() + Math.round((float)size.getX() / 2.0F);
      int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2.0F);
      return new BlockPos(centerX, startPos.getY(), centerZ);
   }

   public void placeBlock(World world, ChunkPos chunkPos, BlockPos blockPos, IBlockState blockState, EnumFacing facing, Random random) {
      int chunkOffset = 8;
      if (blockPos.getX() >= chunkPos.getXStart() + chunkOffset && blockPos.getX() <= chunkPos.getXEnd() + chunkOffset) {
         if (blockPos.getY() > 0 && blockPos.getY() < world.getHeight()) {
            if (blockPos.getZ() >= chunkPos.getZStart() + chunkOffset && blockPos.getZ() <= chunkPos.getZEnd() + chunkOffset) {
               int flags = 3;
               if (blockState.getBlock() == Blocks.TORCH) {
                  blockState = blockState.withProperty(BlockTorch.FACING, facing);
                  flags = 0;
               }

               if (blockState.getBlock() == Blocks.CHEST) {
                  blockState = blockState.withProperty(BlockChest.FACING, facing);
               }

               if (blockState.getBlock() == Blocks.AIR || blockState.getBlock() instanceof BlockFluidBase || blockState.getBlock() instanceof BlockFire || blockState.getBlock() instanceof BlockFireBase || blockState.getBlock() instanceof BlockPoisonCloud || blockState.getBlock() instanceof BlockFrostCloud) {
                  flags = 0;
               }

               world.setBlockState(blockPos, blockState, flags);
               if (blockState.getBlock() == Blocks.MOB_SPAWNER) {
                  TileEntity tileEntity = world.getTileEntity(blockPos);
                  if (tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
                     TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileEntity;
                     MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(this.parentConnector.level, false, random);
                     if (mobSpawn != null) {
                        ResourceLocation entityId = EntityList.getKey(mobSpawn.entityClass);
                        if (entityId != null) {
                           spawner.getSpawnerBaseLogic().setEntityId(entityId);
                        }
                     }
                  }

               } else {
                  if (blockState.getBlock() == Blocks.CHEST) {
                     TileEntity tileEntity = world.getTileEntity(blockPos);
                     if (tileEntity instanceof TileEntityChest) {
                        TileEntityChest chest = (TileEntityChest)tileEntity;
                        ResourceLocation lootTable = this.layout.dungeonInstance.schematic.getRandomLootTable(this.parentConnector.level, random);
                        if (lootTable != null) {
                           chest.setLootTable(lootTable, (long)Objects.hash(new Object[]{blockPos.hashCode(), random}));
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public void build(World world, ChunkPos chunkPos, Random random) {
      this.clearArea(world, chunkPos, random);
      this.buildFloor(world, chunkPos, random, 0);
      this.buildWalls(world, chunkPos, random);
      this.buildCeiling(world, chunkPos, random);
      if ("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
         this.buildStairs(world, chunkPos, random);
         this.buildFloor(world, chunkPos, random, this.stairOffset());
      }

      if ("tower".equalsIgnoreCase(this.dungeonSector.type)) {
         this.buildStairs(world, chunkPos, random);
      }

      this.buildEntrances(world, chunkPos, random);
      ++this.chunksBuilt;
      if ("bossRoom".equalsIgnoreCase(this.dungeonSector.type)) {
         MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(this.parentConnector.level, true, random);
         if (mobSpawn != null) {
            this.spawnMob(world, chunkPos, this.getCenter().add(0, 1, 0), mobSpawn, random);
         }
      }

   }

   public void clearArea(World world, ChunkPos chunkPos, Random random) {
      BlockPos startPos = this.getRoomBoundsMin();
      BlockPos stopPos = this.getRoomBoundsMax();
      int startX = Math.min(startPos.getX(), stopPos.getX());
      int stopX = Math.max(startPos.getX(), stopPos.getX());
      int startY = Math.min(startPos.getY(), stopPos.getY());
      int stopY = Math.max(startPos.getY(), stopPos.getY());
      int startZ = Math.min(startPos.getZ(), stopPos.getZ());
      int stopZ = Math.max(startPos.getZ(), stopPos.getZ());
      if ("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
         startY = Math.max(1, startPos.getY() + this.stairOffset());
      }

      MutableBlockPos buildPos = new MutableBlockPos();

      for(int x = startX; x <= stopX; ++x) {
         for(int y = startY; y <= stopY; ++y) {
            for(int z = startZ; z <= stopZ; ++z) {
               buildPos.setPos(x, y, z);
               IBlockState blockState = this.getAirBlockForPos(buildPos);
               if (blockState != null) {
                  this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.SOUTH, random);
               }
            }
         }
      }

   }

   public void buildFloor(World world, ChunkPos chunkPos, Random random, int offsetY) {
      BlockPos startPos = this.getRoomBoundsMin().add(0, offsetY, 0);
      BlockPos stopPos = this.getRoomBoundsMax().add(0, offsetY, 0);
      int startX = Math.min(startPos.getX(), stopPos.getX());
      int stopX = Math.max(startPos.getX(), stopPos.getX());
      int startY = Math.min(startPos.getY(), stopPos.getY());
      int startZ = Math.min(startPos.getZ(), stopPos.getZ());
      int stopZ = Math.max(startPos.getZ(), stopPos.getZ());
      MutableBlockPos buildPos = new MutableBlockPos();
      Iterator var13 = this.dungeonSector.floor.layers.keySet().iterator();

      while(var13.hasNext()) {
         int layerIndex = ((Integer)var13.next()).intValue();
         int y = startY + layerIndex;
         if (y > 0 && y < world.getHeight()) {
            SectorLayer layer = (SectorLayer)this.dungeonSector.floor.layers.get(Integer.valueOf(layerIndex));

            for(int x = startX; x <= stopX; ++x) {
               List<Character> row = layer.getRow(x - startX, stopX - startX);

               for(int z = startZ; z <= stopZ; ++z) {
                  char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row).charValue();
                  buildPos.setPos(x, y, z);
                  IBlockState blockState = this.theme.getFloor(this, buildChar, random);
                  if (blockState != null) {
                     this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
                  }
               }
            }
         }
      }

   }

   public void buildWalls(World world, ChunkPos chunkPos, Random random) {
      BlockPos startPos = this.getRoomBoundsMin();
      BlockPos stopPos = this.getRoomBoundsMax();
      int startX = Math.min(startPos.getX(), stopPos.getX());
      int stopX = Math.max(startPos.getX(), stopPos.getX());
      int startY = Math.min(startPos.getY() + 1, stopPos.getY());
      int stopY = Math.max(startPos.getY() - 1, stopPos.getY());
      int startZ = Math.min(startPos.getZ(), stopPos.getZ());
      int stopZ = Math.max(startPos.getZ(), stopPos.getZ());
      if ("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
         startY = Math.max(1, startPos.getY() + this.stairOffset());
      }

      MutableBlockPos buildPos = new MutableBlockPos();
      Iterator var13 = this.dungeonSector.wall.layers.keySet().iterator();

      while(var13.hasNext()) {
         int layerIndex = ((Integer)var13.next()).intValue();
         SectorLayer layer = (SectorLayer)this.dungeonSector.wall.layers.get(Integer.valueOf(layerIndex));

         for(int y = startY; y <= stopY; ++y) {
            if (y > 0 && y < world.getHeight()) {
               int progressY = y - startY;
               int fullY = stopY - startY;
               List<Character> row = layer.getRow(progressY, fullY);

               for(int x = startX; x <= stopX; ++x) {
                  char buildChar = layer.getColumn(progressY, fullY, x - startX, stopX - startX, row).charValue();
                  IBlockState blockState = this.theme.getWall(this, buildChar, random);
                  if (blockState != null) {
                     this.placeBlock(world, chunkPos, buildPos.setPos(x, y, startZ + layerIndex), blockState, EnumFacing.SOUTH, random);
                     this.placeBlock(world, chunkPos, buildPos.setPos(x, y, stopZ - layerIndex), blockState, EnumFacing.NORTH, random);
                  }
               }

               for(int z = startZ; z <= stopZ; ++z) {
                  char buildChar = layer.getColumn(progressY, fullY, z - startZ, stopZ - startZ, row).charValue();
                  IBlockState blockState = this.theme.getWall(this, buildChar, random);
                  if (blockState != null) {
                     this.placeBlock(world, chunkPos, buildPos.setPos(startX + layerIndex, y, z), blockState, EnumFacing.EAST, random);
                     this.placeBlock(world, chunkPos, buildPos.setPos(stopX - layerIndex, y, z), blockState, EnumFacing.WEST, random);
                  }
               }
            }
         }
      }

   }

   public void buildCeiling(World world, ChunkPos chunkPos, Random random) {
      BlockPos startPos = this.getRoomBoundsMin();
      BlockPos stopPos = this.getRoomBoundsMax();
      int startX = Math.min(startPos.getX(), stopPos.getX());
      int stopX = Math.max(startPos.getX(), stopPos.getX());
      int stopY = Math.max(startPos.getY(), stopPos.getY());
      int startZ = Math.min(startPos.getZ(), stopPos.getZ());
      int stopZ = Math.max(startPos.getZ(), stopPos.getZ());
      MutableBlockPos buildPos = new MutableBlockPos();
      Iterator var12 = this.dungeonSector.ceiling.layers.keySet().iterator();

      while(var12.hasNext()) {
         int layerIndex = ((Integer)var12.next()).intValue();
         int y = stopY + layerIndex;
         if (y > 0 && y < world.getHeight()) {
            SectorLayer layer = (SectorLayer)this.dungeonSector.ceiling.layers.get(Integer.valueOf(layerIndex));

            for(int x = startX; x <= stopX; ++x) {
               List<Character> row = layer.getRow(x - startX, stopX - startX);

               for(int z = startZ; z <= stopZ; ++z) {
                  char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row).charValue();
                  buildPos.setPos(x, y, z);
                  IBlockState blockState = this.theme.getCeiling(this, buildChar, random);
                  if (blockState != null) {
                     this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.DOWN, random);
                  }
               }
            }
         }
      }

   }

   public void buildEntrances(World world, ChunkPos chunkPos, Random random) {
      this.parentConnector.buildEntrance(world, chunkPos, random);
   }

   public void buildStairs(World world, ChunkPos chunkPos, Random random) {
      BlockPos startPos = this.getRoomBoundsMin();
      BlockPos stopPos = this.getRoomBoundsMax();
      Vec3i size = this.getRoomSize();
      int centerX = startPos.getX() + Math.round((float)size.getX() / 2.0F);
      int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2.0F);
      int stairsHeight = this.parentConnector.parentSector.getOccupiedSize().getY() - 1;
      if (this.dungeonSector.type.equalsIgnoreCase("stairs")) {
         stairsHeight = -this.stairOffset();
      }

      int startX = centerX - 1;
      int stopX = centerX + 1;
      int startY = Math.min(startPos.getY(), stopPos.getY());
      int stopY = Math.max(1, startPos.getY() - stairsHeight);
      int startZ = centerZ - 1;
      int stopZ = centerZ + 1;
      IBlockState floorBlockState = this.theme.getFloor(this, 'B', random);
      IBlockState stairsBlockState = this.stairBlock;
      MutableBlockPos buildPos = new MutableBlockPos();

      for(int y = startY; y >= stopY; --y) {
         for(int x = startX; x <= stopX; ++x) {
            for(int z = startZ; z <= stopZ; ++z) {
               IBlockState blockState = this.getAirBlockForPos(buildPos);
               if (x == centerX && z == centerZ) {
                  blockState = this.theme.getWall(this, 'B', random);
               }

               int step = y % 8;
               int offsetX = x - startX;
               int offsetZ = z - startZ;
               if (step % 4 == 3) {
                  if (offsetX == 0 && offsetZ == 0) {
                     blockState = floorBlockState;
                  } else if (offsetX == 0 && offsetZ == 1) {
                     blockState = stairsBlockState;
                  }
               }

               if (step % 4 == 2) {
                  if (offsetX == 0 && offsetZ == 2) {
                     blockState = floorBlockState;
                  } else if (offsetX == 1 && offsetZ == 2) {
                     blockState = stairsBlockState.withRotation(Rotation.COUNTERCLOCKWISE_90);
                  }
               }

               if (step % 4 == 1) {
                  if (offsetX == 2 && offsetZ == 2) {
                     blockState = floorBlockState;
                  } else if (offsetX == 2 && offsetZ == 1) {
                     blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_180);
                  }
               }

               if (step % 4 == 0) {
                  if (offsetX == 2 && offsetZ == 0) {
                     blockState = floorBlockState;
                  } else if (offsetX == 1 && offsetZ == 0) {
                     blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_90);
                  }
               }

               buildPos.setPos(x, y, z);
               this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
            }
         }
      }

   }

   public void spawnMob(World world, ChunkPos chunkPos, BlockPos blockPos, MobSpawn mobSpawn, Random random) {
      int chunkOffset = 8;
      if (blockPos.getX() >= chunkPos.getXStart() + chunkOffset && blockPos.getX() <= chunkPos.getXEnd() + chunkOffset) {
         if (blockPos.getY() > 0 && blockPos.getY() < world.getHeight()) {
            if (blockPos.getZ() >= chunkPos.getZStart() + chunkOffset && blockPos.getZ() <= chunkPos.getZEnd() + chunkOffset) {
               LycanitesMobs.logDebug("Dungeon", "Spawning mob " + mobSpawn + " at: " + blockPos + " level: " + this.parentConnector.level);
               EntityLiving entityLiving = mobSpawn.createEntity(world);
               entityLiving.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
               if (entityLiving instanceof BaseCreatureEntity) {
                  BaseCreatureEntity entityCreature = (BaseCreatureEntity)entityLiving;
                  entityCreature.setHome(blockPos.getX(), blockPos.getY(), blockPos.getZ(), (float)Math.max(3, Math.max(this.roomSize.getX(), this.roomSize.getZ())));
               }

               mobSpawn.onSpawned(entityLiving, (EntityPlayer)null);
               world.spawnEntity(entityLiving);
            }
         }
      }
   }

   public String toString() {
      String bounds = "";
      String size = "";
      if (this.parentConnector != null) {
         bounds = " Bounds: " + this.getOccupiedBoundsMin() + " to " + this.getOccupiedBoundsMax();
         size = " Occupies: " + this.getOccupiedSize();
      }

      return "Sector Instance Type: " + (this.dungeonSector == null ? "Unset" : this.dungeonSector.type) + " Parent Connector Pos: " + (this.parentConnector == null ? "Unset" : this.parentConnector.position) + size + bounds;
   }

   public IBlockState getAirBlockForPos(BlockPos pos) {
      return this.theme.fillEntranceAndAboveWithAir && pos.getY() > this.layout.dungeonInstance.originPos.getY() ? Blocks.AIR.getDefaultState() : this.airBlock;
   }
}
