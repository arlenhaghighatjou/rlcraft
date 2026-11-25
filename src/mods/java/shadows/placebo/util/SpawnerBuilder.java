package shadows.placebo.util;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerBuilder {
   public static final String SPAWN_DELAY = "Delay";
   public static final String SPAWN_POTENTIALS = "SpawnPotentials";
   public static final String SPAWN_DATA = "SpawnData";
   public static final String MIN_SPAWN_DELAY = "MinSpawnDelay";
   public static final String MAX_SPAWN_DELAY = "MaxSpawnDelay";
   public static final String SPAWN_COUNT = "SpawnCount";
   public static final String MAX_NEARBY_ENTITIES = "MaxNearbyEntities";
   public static final String REQUIRED_PLAYER_RANGE = "RequiredPlayerRange";
   public static final String SPAWN_RANGE = "SpawnRange";
   public static final String ID = "id";
   public static final String ENTITY = "Entity";
   public static final NBTTagCompound BASE_TAG;
   NBTTagCompound tag;
   boolean hasPotentials;
   WeightedSpawnerEntity baseEntity;

   public SpawnerBuilder() {
      this.tag = BASE_TAG.copy();
      this.hasPotentials = false;
      this.baseEntity = new WeightedSpawnerEntity();
      this.tag.setTag("SpawnData", this.baseEntity.getNbt());
   }

   public SpawnerBuilder setType(Class<? extends Entity> entity) {
      return this.setType(EntityList.getKey(entity));
   }

   public SpawnerBuilder setType(ResourceLocation entity) {
      this.baseEntity.getNbt().setString("id", entity.toString());
      return this;
   }

   public SpawnerBuilder setDelay(int delay) {
      this.tag.setShort("Delay", (short)delay);
      return this;
   }

   public SpawnerBuilder setMinDelay(int delay) {
      this.tag.setShort("MinSpawnDelay", (short)delay);
      return this;
   }

   public SpawnerBuilder setMaxDelay(int delay) {
      this.tag.setShort("MaxSpawnDelay", (short)delay);
      return this;
   }

   public SpawnerBuilder setMinAndMaxDelay(int min, int max) {
      this.setMinDelay(min);
      this.setMaxDelay(max);
      return this;
   }

   public SpawnerBuilder setSpawnCount(int count) {
      this.tag.setShort("SpawnCount", (short)count);
      return this;
   }

   public SpawnerBuilder setMaxNearbyEntities(int max) {
      this.tag.setShort("MaxNearbyEntities", (short)max);
      return this;
   }

   public SpawnerBuilder setPlayerRange(int range) {
      this.tag.setShort("RequiredPlayerRange", (short)range);
      return this;
   }

   public SpawnerBuilder setSpawnRange(int range) {
      this.tag.setShort("SpawnRange", (short)range);
      return this;
   }

   public SpawnerBuilder setSpawnData(NBTTagCompound data) {
      if (data == null) {
         data = new NBTTagCompound();
         data.setString("id", "minecraft:pig");
      }

      this.baseEntity.nbt = data.copy();
      return this;
   }

   public SpawnerBuilder setPotentials(WeightedSpawnerEntity... entries) {
      this.hasPotentials = true;
      this.tag.setTag("SpawnPotentials", new NBTTagList());
      NBTTagList list = this.tag.getTagList("SpawnPotentials", 10);

      for(WeightedSpawnerEntity e : entries) {
         list.appendTag(e.toCompoundTag());
      }

      return this;
   }

   public SpawnerBuilder addPotentials(WeightedSpawnerEntity... entries) {
      this.hasPotentials = true;
      NBTTagList list = this.tag.getTagList("SpawnPotentials", 10);

      for(WeightedSpawnerEntity e : entries) {
         list.appendTag(e.toCompoundTag());
      }

      return this;
   }

   public NBTTagCompound getSpawnData() {
      return this.tag.getCompoundTag("SpawnData");
   }

   public NBTTagList getPotentials() {
      return this.tag.getTagList("SpawnPotentials", 10);
   }

   public TileEntityMobSpawner build(World world, BlockPos pos) {
      TileEntityMobSpawner s = (TileEntityMobSpawner)((ITileEntityProvider)Blocks.MOB_SPAWNER).createNewTileEntity((World)null, 0);
      if (!this.hasPotentials) {
         NBTTagList list = new NBTTagList();
         list.appendTag(this.baseEntity.toCompoundTag());
         this.tag.setTag("SpawnPotentials", list);
      }

      s.setWorld(world);
      s.setPos(pos);
      s.readFromNBT(this.tag);
      return s;
   }

   static {
      TileEntityMobSpawner te = (TileEntityMobSpawner)((ITileEntityProvider)Blocks.MOB_SPAWNER).createNewTileEntity((World)null, 0);
      te.getSpawnerBaseLogic().setEntityId(new ResourceLocation("minecraft:pig"));
      BASE_TAG = te.writeToNBT(new NBTTagCompound());
   }
}
