package paulevs.betternether.structures.big;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StructureManager {
   private static final long RANDOM_D = 132897987541L;
   private static final long RANDOM_C = 341873128712L;
   private static final long RANDOM_B = 132897987541L;
   private static final long RANDOM_A = 341873128712L;
   protected ArrayList<BigStructure> structures = new ArrayList();
   protected ArrayList<BigStructure> toRemove = new ArrayList();
   protected EaglercraftRandom random = new EaglercraftRandom();
   protected int distance;
   protected int variance;
   protected long seed;
   protected String name;

   public StructureManager(String name, int distance, long seed) {
      this.seed = seed;
      this.name = name;
      this.setDistance(distance);
   }

   public void setDistance(int distance) {
      this.distance = distance;
      this.variance = distance / 3;
   }

   protected void makeStructure(World world, int cx, int cz) {
      BlockPos nearest = this.getNearestStructure(world, cx, cz);
      if (!this.hasStructure(world, nearest.getX(), nearest.getZ(), cx, cz)) {
         this.setSeed(nearest.getX(), nearest.getZ(), world.getSeed());
         this.structures.add(this.makeStructure(nearest.getX(), nearest.getZ()));
      }

   }

   protected abstract BigStructure makeStructure(int var1, int var2);

   protected boolean hasStructure(World world, int cx, int cz, int sourceX, int sourceZ) {
      for(BigStructure s : this.structures) {
         if (s.cx == cx && s.cz == cz) {
            return true;
         }
      }

      if (sourceX == cx && sourceZ == cz) {
         return false;
      } else {
         return world.isChunkGeneratedAt(cx, cz);
      }
   }

   public BlockPos getNearestStructure(World world, int cx, int cz) {
      int fx = cx / this.distance;
      int fz = cz / this.distance;
      this.setSeed(fx, fz, world.getSeed());
      return new BlockPos(fx * this.distance + this.random.nextInt(this.variance) + this.variance, 0, fz * this.distance + this.random.nextInt(this.variance) + this.variance);
   }

   protected void placeStructures(World world, int cx, int cz) {
      for(BigStructure structure : this.structures) {
         if (structure.generationComplete() && !this.toRemove.contains(this.toRemove)) {
            this.toRemove.add(structure);
         } else {
            structure.setChunk(world, cx, cz);
         }
      }

      this.structures.removeAll(this.toRemove);
      this.toRemove.clear();
   }

   private void setSeed(int cx, int cz, long worldSeed) {
      long rseed = (long)cx * 341873128712L + (long)cz * 132897987541L + worldSeed + this.seed;
      this.random.setSeed(rseed);
   }

   protected void setSeed(int cx, int cz) {
      this.random.setSeed((long)cx * 341873128712L + (long)cz * 132897987541L);
   }

   protected NBTTagCompound toNBT() {
      NBTTagCompound root = new NBTTagCompound();
      NBTTagList structureData = new NBTTagList();
      root.setTag("structures", structureData);

      for(BigStructure s : this.structures) {
         structureData.appendTag(s.toNBT());
      }

      return root;
   }

   public void save(World world) {
      String path = world.getSaveHandler().getWorldDirectory().getAbsolutePath() + "/data/bn_" + this.name + ".nbt";

      try {
         FileOutputStream fs = new FileOutputStream(new File(path));
         CompressedStreamTools.writeCompressed(this.toNBT(), fs);
         fs.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void load(World world) {
      String path = world.getSaveHandler().getWorldDirectory().getAbsolutePath() + "/data/bn_" + this.name + ".nbt";
      File file = new File(path);
      if (file.exists()) {
         try {
            FileInputStream fs = new FileInputStream(file);
            NBTTagCompound root = CompressedStreamTools.readCompressed(fs);
            fs.close();
            NBTTagList structureData = root.getTagList("structures", 10);

            for(int i = 0; i < structureData.tagCount(); ++i) {
               this.structures.add(new BigStructure(structureData.getCompoundTagAt(i)));
            }
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

   }

   public void generate(World world, int cx, int cz) {
      this.makeStructure(world, cx, cz);
      this.placeStructures(world, cx, cz);
   }
}
