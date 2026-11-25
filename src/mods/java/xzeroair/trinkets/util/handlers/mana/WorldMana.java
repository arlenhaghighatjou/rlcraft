package xzeroair.trinkets.util.handlers.mana;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class WorldMana extends WorldSavedData {
   private static String NAME = "ManaData";
   private final Map<ChunkPos, ManaSphere> spheres = new HashMap();
   private int ticker = 10;

   public WorldMana() {
      super(NAME);
   }

   public static WorldMana get(World world) {
      MapStorage storage = world.getPerWorldStorage();
      WorldMana instance = (WorldMana)storage.getOrLoadData(WorldMana.class, NAME);
      if (instance == null) {
         instance = new WorldMana();
         storage.setData(NAME, instance);
      }

      return instance;
   }

   public float getManaInfluence(World world, BlockPos pos) {
      ChunkPos chunkPos = new ChunkPos(pos);
      float mana = 0.0F;

      for(int dx = -4; dx <= 4; ++dx) {
         for(int dz = -4; dz <= 4; ++dz) {
            ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
            ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
            if (sphere.getRadius() > 0.0F) {
               double distanceSq = pos.distanceSq(sphere.getCenter());
               if (distanceSq < (double)(sphere.getRadius() * sphere.getRadius())) {
                  double distance = Math.sqrt(distanceSq);
                  mana = (float)((double)mana + ((double)sphere.getRadius() - distance) / (double)sphere.getRadius());
               }
            }
         }
      }

      return mana;
   }

   public float getManaStrength(World world, BlockPos pos) {
      ChunkPos chunkPos = new ChunkPos(pos);
      float mana = 0.0F;

      for(int dx = -4; dx <= 4; ++dx) {
         for(int dz = -4; dz <= 4; ++dz) {
            ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
            ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
            if (sphere.getRadius() > 0.0F) {
               double distanceSq = pos.distanceSq(sphere.getCenter());
               if (distanceSq < (double)(sphere.getRadius() * sphere.getRadius())) {
                  double distance = Math.sqrt(distanceSq);
                  double influence = ((double)sphere.getRadius() - distance) / (double)sphere.getRadius();
                  mana = (float)((double)mana + influence * (double)sphere.getCurrentMana());
               }
            }
         }
      }

      return mana;
   }

   public float extractMana(World world, BlockPos pos) {
      float manaInfluence = this.getManaInfluence(world, pos);
      if (manaInfluence <= 0.0F) {
         return 0.0F;
      } else {
         ChunkPos chunkPos = new ChunkPos(pos);
         float extracted = 0.0F;

         for(int dx = -4; dx <= 4; ++dx) {
            for(int dz = -4; dz <= 4; ++dz) {
               ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
               ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
               if (sphere.getRadius() > 0.0F) {
                  double distanceSq = pos.distanceSq(sphere.getCenter());
                  if (distanceSq < (double)(sphere.getRadius() * sphere.getRadius())) {
                     double distance = Math.sqrt(distanceSq);
                     double influence = ((double)sphere.getRadius() - distance) / (double)sphere.getRadius();
                     float currentMana = sphere.getCurrentMana();
                     if (influence > (double)currentMana) {
                        influence = (double)currentMana;
                     }

                     currentMana = (float)((double)currentMana - influence);
                     extracted = (float)((double)extracted + influence);
                     sphere.setCurrentMana(currentMana);
                     this.markDirty();
                  }
               }
            }
         }

         return extracted;
      }
   }

   public void tick(World world) {
      --this.ticker;
      if (this.ticker <= 0) {
         this.ticker = 10;
         this.growMana(world);
         this.sendMana(world);
      }
   }

   private void growMana(World world) {
      for(Entry<ChunkPos, ManaSphere> entry : this.spheres.entrySet()) {
         ManaSphere sphere = (ManaSphere)entry.getValue();
         if (sphere.getRadius() > 0.0F && world.isBlockLoaded(sphere.getCenter())) {
            float currentMana = sphere.getCurrentMana();
            currentMana = currentMana + 0.01F;
            if (currentMana >= 5.0F) {
               currentMana = 5.0F;
            }

            sphere.setCurrentMana(currentMana);
            this.markDirty();
         }
      }

   }

   private void sendMana(World world) {
      for(EntityPlayer player : world.playerEntities) {
         this.getManaStrength(world, player.getPosition());
         this.getManaInfluence(world, player.getPosition());
      }

   }

   private ManaSphere getOrCreateSphereAt(World world, ChunkPos cp) {
      ManaSphere sphere = (ManaSphere)this.spheres.get(cp);
      if (sphere == null) {
         BlockPos center = cp.getBlock(8, ManaSphere.getRandomYOffset(Long.valueOf(world.getSeed()), cp.x, cp.z), 8);
         float radius = 0.0F;
         if (ManaSphere.isCenterChunk(Long.valueOf(world.getSeed()), cp.x, cp.z)) {
            radius = ManaSphere.getRadius(Long.valueOf(world.getSeed()), cp.x, cp.z);
         }

         sphere = new ManaSphere(center, radius);
         this.spheres.put(cp, sphere);
         this.markDirty();
      }

      return sphere;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      NBTTagList list = nbt.getTagList("spheres", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound sphereNBT = list.getCompoundTagAt(i);
         ChunkPos pos = new ChunkPos(sphereNBT.getInteger("cx"), sphereNBT.getInteger("cz"));
         ManaSphere sphere = new ManaSphere(new BlockPos(sphereNBT.getInteger("posx"), sphereNBT.getInteger("posy"), sphereNBT.getInteger("posz")), sphereNBT.getFloat("radius"));
         sphere.setCurrentMana(sphereNBT.getFloat("mana"));
         this.spheres.put(pos, sphere);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagList list = new NBTTagList();

      for(Entry<ChunkPos, ManaSphere> entry : this.spheres.entrySet()) {
         NBTTagCompound sphereNBT = new NBTTagCompound();
         ChunkPos pos = (ChunkPos)entry.getKey();
         ManaSphere sphere = (ManaSphere)entry.getValue();
         sphereNBT.setInteger("cx", pos.x);
         sphereNBT.setInteger("cz", pos.z);
         sphereNBT.setInteger("posx", sphere.getCenter().getX());
         sphereNBT.setInteger("posy", sphere.getCenter().getY());
         sphereNBT.setInteger("posz", sphere.getCenter().getZ());
         sphereNBT.setFloat("radius", sphere.getRadius());
         sphereNBT.setFloat("mana", sphere.getCurrentMana());
         list.appendTag(sphereNBT);
      }

      compound.setTag("spheres", list);
      return compound;
   }
}
