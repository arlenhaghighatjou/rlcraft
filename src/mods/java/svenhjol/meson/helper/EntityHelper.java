package svenhjol.meson.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class EntityHelper {
   public EntityHelper() {
   }

   public static Biome[] getBiomesWithMob(Class<? extends Entity> clazz) {
      List<Biome> biomes = new ArrayList();

      label23:
      for(Biome b : Biome.REGISTRY) {
         List<SpawnListEntry> spawnList = b.getSpawnableList(EnumCreatureType.MONSTER);
         Iterator var5 = spawnList.iterator();

         while(true) {
            if (!var5.hasNext()) {
               continue label23;
            }

            SpawnListEntry e = (SpawnListEntry)var5.next();
            if (e.entityClass == clazz) {
               break;
            }
         }

         biomes.add(b);
      }

      return (Biome[])biomes.toArray(new Biome[0]);
   }

   public static boolean canMobsSpawnInPos(World world, BlockPos pos) {
      if (world.isSideSolid(pos.down(), EnumFacing.UP) && !world.isBlockNormalCube(pos, false) && !world.isBlockNormalCube(pos.up(), false) && !world.getBlockState(pos).getMaterial().isLiquid()) {
         IBlockState state = world.getBlockState(pos);
         int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, pos);
         return lightLevel < 8 && (world.isAirBlock(pos) || state.getCollisionBoundingBox(world, pos) == null);
      } else {
         return false;
      }
   }

   public static boolean itemHasEntityTag(ItemStack stack) {
      NBTTagCompound tag = stack.getTagCompound();
      return tag != null && tag.hasKey("EntityTag") && tag.getCompoundTag("EntityTag").hasKey("id");
   }

   public static void spawnEntityFromItem(ItemStack stack, World world, BlockPos pos) {
      if (!world.isRemote) {
         if (itemHasEntityTag(stack)) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
               String s = tag.getCompoundTag("EntityTag").getString("id");
               ResourceLocation id = new ResourceLocation(s);
               if (EntityList.ENTITY_EGGS.containsKey(id)) {
                  Entity entityFromId = EntityList.createEntityByIDFromName(id, world);
                  Entity entity = spawnEntity(entityFromId, world, pos);
                  if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                     entity.setCustomNameTag(stack.getDisplayName());
                  }
               }

            }
         }
      }
   }

   public static void spawnEntityItem(World world, BlockPos pos, ItemStack stack) {
      EntityItem item = new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
      item.setLocationAndAngles((double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), 0.0F, 0.0F);
      world.spawnEntity(item);
   }

   public static void spawnEntityNearPlayer(EntityPlayer player, int range, ResourceLocation entityName) {
      spawnEntityNearPlayer(player, 0, range, entityName);
   }

   public static void spawnEntityNearPlayer(EntityPlayer player, int minDistance, int range, ResourceLocation entityName) {
      World world = player.world;
      Entity entity = EntityList.createEntityByIDFromName(entityName, world);
      if (entity instanceof EntityLiving) {
         ArrayList<BlockPos> shuffled = new ArrayList();
         Iterable<BlockPos> positions = BlockPos.getAllInBox(player.getPosition().add(-range, -range, -range), player.getPosition().add(range, range, range));
         positions.forEach(shuffled::add);
         Collections.shuffle(shuffled);

         for(BlockPos p : shuffled) {
            if (WorldHelper.getDistanceSq(player.getPosition(), p) > (double)minDistance && canMobsSpawnInPos(world, p)) {
               spawnEntity(entity, world, new BlockPos((double)((float)p.getX() + 0.5F), (double)p.getY(), (double)((float)p.getZ() + 0.5F)));
               break;
            }
         }
      }

   }

   @Nullable
   public static Entity spawnEntity(Entity entity, World world, BlockPos pos) {
      if (entity == null) {
         return null;
      } else {
         entity.setLocationAndAngles((double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
         if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving)entity;
            living.rotationYawHead = living.rotationYaw;
            living.renderYawOffset = living.rotationYaw;
            living.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(living)), (IEntityLivingData)null);
            world.spawnEntity(living);
            living.playLivingSound();
            return living;
         } else {
            world.spawnEntity(entity);
            return entity;
         }
      }
   }

   public static Entity spawnEntity(ResourceLocation name, World world, BlockPos pos) {
      Entity entity = EntityList.createEntityByIDFromName(name, world);
      if (entity == null) {
         return null;
      } else {
         double x = (double)pos.getX();
         double y = (double)pos.getY();
         double z = (double)pos.getZ();
         entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
         return spawnEntity(entity, world, new BlockPos(x, y, z));
      }
   }

   public static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
      WorldProvider pOld = oldWorld.provider;
      WorldProvider pNew = newWorld.provider;
      double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
      double x = entity.posX * moveFactor;
      double z = entity.posZ * moveFactor;
      oldWorld.profiler.startSection("placing");
      x = MathHelper.clamp(x, -2.9999872E7D, 2.9999872E7D);
      z = MathHelper.clamp(z, -2.9999872E7D, 2.9999872E7D);
      if (entity.isEntityAlive()) {
         entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
         newWorld.spawnEntity(entity);
         newWorld.updateEntityWithOptionalForce(entity, false);
      }

      oldWorld.profiler.endSection();
      entity.setWorld(newWorld);
   }
}
