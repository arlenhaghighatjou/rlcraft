package com.github.alexthe666.iceandfire.structures;

import com.github.alexthe666.iceandfire.block.BlockMyrmexConnectedResin;
import com.github.alexthe666.iceandfire.block.BlockMyrmexResin;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMyrmexHive extends WorldGenerator {
   private static final IBlockState DESERT_RESIN = ModBlocks.myrmex_resin.getDefaultState();
   private static final IBlockState STICKY_DESERT_RESIN = ModBlocks.myrmex_resin_sticky.getDefaultState();
   private static final IBlockState JUNGLE_RESIN = ModBlocks.myrmex_resin.getDefaultState().withProperty(BlockMyrmexResin.VARIANT, BlockMyrmexResin.EnumType.JUNGLE);
   private static final IBlockState STICKY_JUNGLE_RESIN = ModBlocks.myrmex_resin_sticky.getDefaultState().withProperty(BlockMyrmexResin.VARIANT, BlockMyrmexResin.EnumType.JUNGLE);
   public MyrmexHive hive;
   private int entrances = 0;
   private int totalRooms;
   private boolean hasFoodRoom;
   private boolean hasNursery;
   private boolean small;
   private boolean jungle;
   private BlockPos centerOfHive;

   public WorldGenMyrmexHive(boolean small, boolean jungle) {
      this.small = small;
      this.jungle = jungle;
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      this.hasFoodRoom = false;
      this.hasNursery = false;
      this.totalRooms = 0;
      BlockPos undergroundPos = new BlockPos(position.getX(), position.getY(), position.getZ());
      this.entrances = 0;
      this.centerOfHive = undergroundPos;
      this.generateMainRoom(worldIn, rand, undergroundPos);
      this.small = false;
      return false;
   }

   private void generateMainRoom(World world, EaglercraftRandom rand, BlockPos position) {
      this.hive = new MyrmexHive(world, position, 100);
      MyrmexWorldData.addHive(world, this.hive);
      IBlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
      IBlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
      this.generateSphere(world, rand, position, 14, 7, resin, sticky_resin);
      this.generateSphere(world, rand, position, 12, 5, Blocks.AIR.getDefaultState());
      this.decorateSphere(world, rand, position, 12, 5, WorldGenMyrmexHive.RoomType.QUEEN);
      this.generatePath(world, rand, position.offset(EnumFacing.NORTH, 9).down(), 15 + rand.nextInt(10), EnumFacing.NORTH, 100);
      this.generatePath(world, rand, position.offset(EnumFacing.SOUTH, 9).down(), 15 + rand.nextInt(10), EnumFacing.SOUTH, 100);
      this.generatePath(world, rand, position.offset(EnumFacing.WEST, 9).down(), 15 + rand.nextInt(10), EnumFacing.WEST, 100);
      this.generatePath(world, rand, position.offset(EnumFacing.EAST, 9).down(), 15 + rand.nextInt(10), EnumFacing.EAST, 100);
      if (!this.small) {
         EntityMyrmexQueen queen = new EntityMyrmexQueen(world);
         BlockPos ground = MyrmexHive.getGroundedPos(world, position);
         queen.onInitialSpawn(world.getDifficultyForLocation(ground), (IEntityLivingData)null);
         queen.setHive(this.hive);
         queen.setJungleVariant(this.jungle);
         queen.setPositionAndRotation((double)ground.getX() + 0.5D, (double)ground.getY() + 1.0D, (double)ground.getZ() + 0.5D, 0.0F, 0.0F);
         if (!world.isRemote) {
            world.spawnEntity(queen);
         }

         for(int i = 0; i < 4 + rand.nextInt(3); ++i) {
            EntityMyrmexBase myrmex = new EntityMyrmexWorker(world);
            myrmex.onInitialSpawn(world.getDifficultyForLocation(ground), (IEntityLivingData)null);
            myrmex.setHive(this.hive);
            myrmex.setPositionAndRotation((double)ground.getX() + 0.5D, (double)ground.getY() + 1.0D, (double)ground.getZ() + 0.5D, 0.0F, 0.0F);
            myrmex.setJungleVariant(this.jungle);
            if (!world.isRemote) {
               world.spawnEntity(myrmex);
            }
         }

         for(int i = 0; i < 2 + rand.nextInt(2); ++i) {
            EntityMyrmexBase myrmex = new EntityMyrmexSoldier(world);
            myrmex.onInitialSpawn(world.getDifficultyForLocation(ground), (IEntityLivingData)null);
            myrmex.setHive(this.hive);
            myrmex.setPositionAndRotation((double)ground.getX() + 0.5D, (double)ground.getY() + 1.0D, (double)ground.getZ() + 0.5D, 0.0F, 0.0F);
            myrmex.setJungleVariant(this.jungle);
            if (!world.isRemote) {
               world.spawnEntity(myrmex);
            }
         }

         for(int i = 0; i < rand.nextInt(2); ++i) {
            EntityMyrmexBase myrmex = new EntityMyrmexSentinel(world);
            myrmex.onInitialSpawn(world.getDifficultyForLocation(ground), (IEntityLivingData)null);
            myrmex.setHive(this.hive);
            myrmex.setPositionAndRotation((double)ground.getX() + 0.5D, (double)ground.getY() + 1.0D, (double)ground.getZ() + 0.5D, 0.0F, 0.0F);
            myrmex.setJungleVariant(this.jungle);
            if (!world.isRemote) {
               world.spawnEntity(myrmex);
            }
         }
      }

   }

   private void generatePath(World world, EaglercraftRandom rand, BlockPos offset, int length, EnumFacing direction, int roomChance) {
      if (roomChance != 0) {
         if (this.small) {
            length = length / 2;
            if (this.entrances < 1) {
               for(int i = 0; i < length; ++i) {
                  this.generateCircle(world, rand, offset.offset(direction, i), 3, 5, direction);
               }

               this.generateEntrance(world, rand, offset.offset(direction, length), 4, 4, direction);
            } else if (this.totalRooms < 2) {
               for(int i = 0; i < length; ++i) {
                  this.generateCircle(world, rand, offset.offset(direction, i), 3, 5, direction);
               }

               this.generateRoom(world, rand, offset.offset(direction, length), 6, 4, roomChance / 2, direction);

               for(int i = -3; i < 3; ++i) {
                  this.generateCircleAir(world, rand, offset.offset(direction, i), 3, 5, direction);
                  this.generateCircleAir(world, rand, offset.offset(direction, length + i), 3, 5, direction);
               }

               ++this.totalRooms;
            }
         } else if (rand.nextInt(100) < roomChance) {
            for(int i = 0; i < length; ++i) {
               this.generateCircle(world, rand, offset.offset(direction, i), 3, 5, direction);
            }

            if (this.entrances < 3 && rand.nextInt(1 + this.entrances * 2) == 0 && this.hasFoodRoom && this.hasNursery && this.totalRooms > 3) {
               this.generateEntrance(world, rand, offset.offset(direction, length), 4, 4, direction);
            } else {
               this.generateRoom(world, rand, offset.offset(direction, length), 7, 4, roomChance / 2, direction);

               for(int i = -3; i < 3; ++i) {
                  this.generateCircleAir(world, rand, offset.offset(direction, length + i), 3, 5, direction);
               }

               ++this.totalRooms;
            }
         }

      }
   }

   private void generateRoom(World world, EaglercraftRandom rand, BlockPos position, int size, int height, int roomChance, EnumFacing direction) {
      IBlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
      IBlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
      WorldGenMyrmexHive.RoomType type = WorldGenMyrmexHive.RoomType.random(rand);
      if (!this.hasFoodRoom) {
         type = WorldGenMyrmexHive.RoomType.FOOD;
         this.hasFoodRoom = true;
      } else if (!this.hasNursery) {
         type = WorldGenMyrmexHive.RoomType.NURSERY;
         this.hasNursery = true;
      }

      this.generateSphereRespectResin(world, rand, position, size + 2, height + 2, resin, sticky_resin);
      this.generateSphere(world, rand, position, size, height - 1, Blocks.AIR.getDefaultState());
      this.decorateSphere(world, rand, position, size, height - 1, type);
      this.hive.addRoom(position, type);
      if (!this.small) {
         if (rand.nextInt(3) == 0 && direction.getOpposite() != EnumFacing.NORTH) {
            this.generatePath(world, rand, position.offset(EnumFacing.NORTH, size - 2), 5 + rand.nextInt(20), EnumFacing.NORTH, roomChance);
         }

         if (rand.nextInt(3) == 0 && direction.getOpposite() != EnumFacing.SOUTH) {
            this.generatePath(world, rand, position.offset(EnumFacing.SOUTH, size - 2), 5 + rand.nextInt(20), EnumFacing.SOUTH, roomChance);
         }

         if (rand.nextInt(3) == 0 && direction.getOpposite() != EnumFacing.WEST) {
            this.generatePath(world, rand, position.offset(EnumFacing.WEST, size - 2), 5 + rand.nextInt(20), EnumFacing.WEST, roomChance);
         }

         if (rand.nextInt(3) == 0 && direction.getOpposite() != EnumFacing.EAST) {
            this.generatePath(world, rand, position.offset(EnumFacing.EAST, size - 2), 5 + rand.nextInt(20), EnumFacing.EAST, roomChance);
         }
      }

   }

   private void generateEntrance(World world, EaglercraftRandom rand, BlockPos position, int size, int height, EnumFacing direction) {
      BlockPos up = position.up();
      this.hive.getEntranceBottoms().put(up, direction);

      while(!world.canBlockSeeSky(up)) {
         this.generateCircleRespectSky(world, rand, up, size, height, direction);
         up = up.up().offset(direction);
      }

      IBlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
      IBlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
      this.generateSphereRespectAir(world, rand, up, size + 2, height + 2, resin, sticky_resin);
      this.generateSphere(world, rand, up.up(), size, height - 1, Blocks.AIR.getDefaultState());
      this.decorateSphere(world, rand, up.up(), size, height - 1, WorldGenMyrmexHive.RoomType.ENTERANCE);
      this.hive.getEntrances().put(up, direction);
      ++this.entrances;
   }

   private void generateCircle(World world, EaglercraftRandom rand, BlockPos position, int size, int height, EnumFacing direction) {
      IBlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
      IBlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
      int radius = size + 2;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i);
            int z = (int)Math.floor(Math.cos((double)j) * (double)i);
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               world.setBlockState(position.add(x, z, 0), rand.nextInt(3) == 0 ? sticky_resin : resin);
            } else {
               world.setBlockState(position.add(0, x, z), rand.nextInt(3) == 0 ? sticky_resin : resin);
            }
         }
      }

      radius = radius - 2;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            int z = (int)Math.floor(Math.cos((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState());
            } else {
               world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState());
            }
         }
      }

      this.decorateCircle(world, rand, position, size, height, direction);
   }

   private void generateCircleRespectSky(World world, EaglercraftRandom rand, BlockPos position, int size, int height, EnumFacing direction) {
      IBlockState resin = this.jungle ? JUNGLE_RESIN : DESERT_RESIN;
      IBlockState sticky_resin = this.jungle ? STICKY_JUNGLE_RESIN : STICKY_DESERT_RESIN;
      int radius = size + 2;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i);
            int z = (int)Math.floor(Math.cos((double)j) * (double)i);
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               if (!world.canBlockSeeSky(position.add(x, z, 0))) {
                  world.setBlockState(position.add(x, z, 0), rand.nextInt(3) == 0 ? sticky_resin : resin);
               }
            } else if (!world.canBlockSeeSky(position.add(0, x, z))) {
               world.setBlockState(position.add(0, x, z), rand.nextInt(3) == 0 ? sticky_resin : resin);
            }
         }
      }

      radius = radius - 2;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            int z = (int)Math.floor(Math.cos((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState());
            } else {
               world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState());
            }
         }
      }

      this.decorateCircle(world, rand, position, size, height, direction);
   }

   private void generateCircleAir(World world, EaglercraftRandom rand, BlockPos position, int size, int height, EnumFacing direction) {
      int radius = size;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            int z = (int)Math.floor(Math.cos((double)j) * (double)i * (double)MathHelper.clamp(rand.nextFloat(), 0.5F, 1.0F));
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               world.setBlockState(position.add(x, z, 0), Blocks.AIR.getDefaultState());
            } else {
               world.setBlockState(position.add(0, x, z), Blocks.AIR.getDefaultState());
            }
         }
      }

      this.decorateCircle(world, rand, position, size, height, direction);
   }

   public void generateSphere(World world, EaglercraftRandom rand, BlockPos position, int size, int height, IBlockState fill) {
      int ySize = rand.nextInt(2);
      int j = size + rand.nextInt(2);
      int k = height + ySize;
      int l = size + rand.nextInt(2);
      float f = (float)(j + k + l) * 0.333F;

      for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
         if (blockpos.distanceSq(position) <= (double)(f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F)) && !world.isAirBlock(blockpos)) {
            world.setBlockState(blockpos, fill, 3);
         }
      }

   }

   public void generateSphere(World world, EaglercraftRandom rand, BlockPos position, int size, int height, IBlockState fill, IBlockState fill2) {
      int ySize = rand.nextInt(2);
      int j = size + rand.nextInt(2);
      int k = height + ySize;
      int l = size + rand.nextInt(2);
      float f = (float)(j + k + l) * 0.333F;

      for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
         if (blockpos.distanceSq(position) <= (double)(f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F))) {
            world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 3);
         }
      }

   }

   public void generateSphereRespectResin(World world, EaglercraftRandom rand, BlockPos position, int size, int height, IBlockState fill, IBlockState fill2) {
      int ySize = rand.nextInt(2);
      int j = size + rand.nextInt(2);
      int k = height + ySize;
      int l = size + rand.nextInt(2);
      float f = (float)(j + k + l) * 0.333F;

      for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
         if (blockpos.distanceSq(position) <= (double)(f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F)) && (!world.isAirBlock(blockpos) || world.isAirBlock(blockpos) && !this.hasResinUnder(blockpos, world))) {
            world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 3);
         }
      }

   }

   public void generateSphereRespectAir(World world, EaglercraftRandom rand, BlockPos position, int size, int height, IBlockState fill, IBlockState fill2) {
      int ySize = rand.nextInt(2);
      int j = size + rand.nextInt(2);
      int k = height + ySize;
      int l = size + rand.nextInt(2);
      float f = (float)(j + k + l) * 0.333F;

      for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l))) {
         if (blockpos.distanceSq(position) <= (double)(f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F)) && !world.isAirBlock(blockpos)) {
            world.setBlockState(blockpos, rand.nextInt(3) == 0 ? fill2 : fill, 3);
         }
      }

   }

   private boolean hasResinUnder(BlockPos pos, World world) {
      BlockPos copy;
      for(copy = pos.down(); world.isAirBlock(copy) && copy.getY() > 1; copy = copy.down()) {
         ;
      }

      return world.getBlockState(copy).getBlock() instanceof BlockMyrmexResin || world.getBlockState(copy).getBlock() instanceof BlockMyrmexConnectedResin;
   }

   private void decorateCircle(World world, EaglercraftRandom rand, BlockPos position, int size, int height, EnumFacing direction) {
      int radius = size + 2;

      for(float i = 0.0F; i < (float)radius; i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i; j = (float)((double)j + 0.5D)) {
            int x = (int)Math.floor(Math.sin((double)j) * (double)i);
            int z = (int)Math.floor(Math.cos((double)j) * (double)i);
            if (direction != EnumFacing.WEST && direction != EnumFacing.EAST) {
               if (world.isAirBlock(position.add(x, z, 0))) {
                  this.decorate(world, position.add(x, z, 0), position, size, rand, WorldGenMyrmexHive.RoomType.TUNNEL);
               }

               if (world.isAirBlock(position.add(0, x, z))) {
                  this.decorateTubers(world, position.add(0, x, z), rand, WorldGenMyrmexHive.RoomType.TUNNEL);
               }
            } else {
               if (world.isAirBlock(position.add(0, x, z))) {
                  this.decorate(world, position.add(0, x, z), position, size, rand, WorldGenMyrmexHive.RoomType.TUNNEL);
               }

               if (world.isAirBlock(position.add(0, x, z))) {
                  this.decorateTubers(world, position.add(0, x, z), rand, WorldGenMyrmexHive.RoomType.TUNNEL);
               }
            }
         }
      }

   }

   private void decorateSphere(World world, EaglercraftRandom rand, BlockPos position, int size, int height, WorldGenMyrmexHive.RoomType roomType) {
      int ySize = rand.nextInt(2);
      int j = size + rand.nextInt(2);
      int k = height + ySize;
      int l = size + rand.nextInt(2);
      float f = (float)(j + k + l) * 0.333F;

      for(BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k + 2, l))) {
         if (blockpos.distanceSq(position) <= (double)(f * f)) {
            if (world.getBlockState(blockpos.down()).isFullCube() && world.isAirBlock(blockpos)) {
               this.decorate(world, blockpos, position, size, rand, roomType);
            }

            if (world.isAirBlock(blockpos)) {
               this.decorateTubers(world, blockpos, rand, roomType);
            }
         }
      }

   }

   private void decorate(World world, BlockPos blockpos, BlockPos center, int size, EaglercraftRandom random, WorldGenMyrmexHive.RoomType roomType) {
      switch(roomType) {
      case FOOD:
         if (random.nextInt(45) == 0 && world.getBlockState(blockpos.down()).getBlock() instanceof BlockMyrmexResin) {
            WorldGenMyrmexDecoration.generateSkeleton(world, blockpos, center, size, random);
         }

         if (random.nextInt(13) == 0) {
            WorldGenMyrmexDecoration.generateLeaves(world, blockpos, center, size, random);
         }

         if (random.nextInt(12) == 0) {
            WorldGenMyrmexDecoration.generatePumpkins(world, blockpos, center, size, random);
         }

         if (random.nextInt(6) == 0) {
            WorldGenMyrmexDecoration.generateMushrooms(world, blockpos, center, size, random);
         }

         if (random.nextInt(12) == 0) {
            WorldGenMyrmexDecoration.generateCocoon(world, blockpos, random, this.jungle, this.jungle ? WorldGenMyrmexDecoration.JUNGLE_MYRMEX_FOOD_CHEST : WorldGenMyrmexDecoration.DESERT_MYRMEX_FOOD_CHEST);
         }
         break;
      case NURSERY:
         if (random.nextInt(4) == 0 && !this.small) {
            EntityMyrmexBase baby;
            switch(random.nextInt(4)) {
            case 1:
               baby = new EntityMyrmexSoldier(world);
               break;
            case 2:
               baby = new EntityMyrmexSentinel(world);
               break;
            case 3:
               baby = new EntityMyrmexRoyal(world);
               break;
            default:
               baby = new EntityMyrmexWorker(world);
            }

            baby.setGrowthStage(random.nextInt(2));
            baby.setLocationAndAngles((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), random.nextFloat() * 360.0F, 0.0F);
            baby.setRotationYawHead(random.nextFloat() * 360.0F);
            baby.setHive(this.hive);
            baby.setJungleVariant(this.jungle);
            if (!world.isRemote && !baby.isEntityInsideOpaqueBlock()) {
               world.spawnEntity(baby);
            }
         }
         break;
      case SHINY:
         if (random.nextInt(12) == 0) {
            WorldGenMyrmexDecoration.generateGold(world, blockpos, center, size, random);
         }
         break;
      case TRASH:
         if (random.nextInt(24) == 0) {
            WorldGenMyrmexDecoration.generateTrashHeap(world, blockpos, center, size, random);
         }

         if (random.nextBoolean()) {
            WorldGenMyrmexDecoration.generateTrashOre(world, blockpos, center, size, random);
         }

         if (random.nextInt(12) == 0) {
            WorldGenMyrmexDecoration.generateCocoon(world, blockpos, random, this.jungle, WorldGenMyrmexDecoration.MYRMEX_TRASH_CHEST);
         }
      }

   }

   private void decorateTubers(World world, BlockPos blockpos, EaglercraftRandom random, WorldGenMyrmexHive.RoomType roomType) {
      if (world.getBlockState(blockpos.up()).isOpaqueCube() && random.nextInt(roomType != WorldGenMyrmexHive.RoomType.ENTERANCE && roomType != WorldGenMyrmexHive.RoomType.TUNNEL ? 6 : 20) == 0) {
         int tuberLength = roomType != WorldGenMyrmexHive.RoomType.ENTERANCE && roomType != WorldGenMyrmexHive.RoomType.TUNNEL ? (roomType == WorldGenMyrmexHive.RoomType.QUEEN ? 1 + random.nextInt(5) : 1 + random.nextInt(3)) : 1;

         for(int i = 0; i < tuberLength; ++i) {
            if (world.isAirBlock(blockpos.down(i))) {
               world.setBlockState(blockpos.down(i), this.jungle ? ModBlocks.myrmex_jungle_biolight.getDefaultState() : ModBlocks.myrmex_desert_biolight.getDefaultState());
            }
         }
      }

   }

   public static enum RoomType {
      DEFAULT(false),
      TUNNEL(false),
      ENTERANCE(false),
      QUEEN(false),
      FOOD(true),
      EMPTY(true),
      NURSERY(true),
      SHINY(true),
      TRASH(true);

      boolean random;

      private RoomType(boolean random) {
         this.random = random;
      }

      public static WorldGenMyrmexHive.RoomType random(EaglercraftRandom rand) {
         List<WorldGenMyrmexHive.RoomType> list = new ArrayList();

         for(WorldGenMyrmexHive.RoomType type : values()) {
            if (type.random) {
               list.add(type);
            }
         }

         return (WorldGenMyrmexHive.RoomType)list.get(rand.nextInt(list.size()));
      }
   }
}
