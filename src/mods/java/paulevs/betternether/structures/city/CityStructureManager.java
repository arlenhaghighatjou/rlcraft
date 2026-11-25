package paulevs.betternether.structures.city;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.noise.WorleyNoiseOctaved;
import paulevs.betternether.structures.big.BigStructure;
import paulevs.betternether.structures.big.StructureManager;

public class CityStructureManager extends StructureManager {
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
   protected CityGenerator generator = new CityGenerator();
   protected WorleyNoiseOctaved noiseX;
   protected WorleyNoiseOctaved noiseY;
   protected WorleyNoiseOctaved noiseZ;

   public CityStructureManager(long seed) {
      super("city", 80, seed);
      this.random.setSeed(seed);
      this.noiseX = new WorleyNoiseOctaved(this.random.nextLong(), 2);
      this.noiseY = new WorleyNoiseOctaved(this.random.nextLong(), 2);
      this.noiseZ = new WorleyNoiseOctaved(this.random.nextLong(), 2);
   }

   protected BigStructure makeStructure(int cx, int cz) {
      this.setSeed(cx, cz);
      BlockPos pos = new BlockPos(cx << 4, 40, cz << 4);
      BigStructureCity cave = new BigStructureCity(pos, cx, cz, this.generator, this.random);
      int caveSize = (int)((double)cave.getCitySide() * 0.6D) + this.random.nextInt(50);
      this.makeCave(caveSize, 40, cave);
      List<BlockPos> positions = cave.getPosList(pos);
      List<Integer> radiuses = cave.getRadiuses();

      for(int i = 0; i < positions.size(); ++i) {
         BlockPos cp = (BlockPos)positions.get(i);
         int r = ((Integer)radiuses.get(i)).intValue();
         this.makeCave(r, cp.getX(), cp.getY(), cp.getZ(), cave);
      }

      return cave;
   }

   protected void makeCave(int radius, int centerY, BigStructure structure) {
      int bounds = (int)((double)radius * 1.5D);
      int rr = radius * radius;
      int minY = 5 - centerY;
      int lavaH = 31 - centerY;

      for(int x = -bounds; x < bounds; ++x) {
         for(int y = minY; y < radius; ++y) {
            int wy = y + centerY - 40;
            int y2 = y * 2;

            for(int z = -bounds; z < bounds; ++z) {
               double dx = (double)x + this.noiseX.GetValue((double)y2 * 0.02D, (double)z * 0.02D) * 20.0D;
               double dy = (double)y2 + this.noiseX.GetValue((double)x * 0.02D, (double)z * 0.02D) * 20.0D;
               double dz = (double)z + this.noiseX.GetValue((double)y * 0.02D, (double)x * 0.02D) * 20.0D;
               double xx = dx * dx;
               double yy = dy * dy;
               double zz = dz * dz;
               if (xx + yy + zz < (double)rr) {
                  if (wy > lavaH) {
                     structure.setBlock(AIR, new BlockPos(x, wy, z));
                  } else {
                     structure.setBlock(LAVA, new BlockPos(x, wy, z));
                  }
               }
            }
         }
      }

   }

   protected void makeCave(int radius, int centerX, int centerY, int centerZ, BigStructure structure) {
      int bounds = (int)((double)radius * 1.5D);
      int rr = radius * radius;
      int minY = 5 - centerY;
      int lavaH = 31 - centerY;

      for(int x = -bounds; x < bounds; ++x) {
         int wx = x + centerX;

         for(int y = minY; y < bounds; ++y) {
            int wy = y + centerY - 40;

            for(int z = -bounds; z < bounds; ++z) {
               int wz = z + centerZ;
               double dx = (double)x + this.noiseX.GetValue((double)y * 0.02D, (double)z * 0.02D) * 20.0D - 10.0D;
               double dy = (double)y + this.noiseX.GetValue((double)x * 0.02D, (double)z * 0.02D) * 20.0D - 10.0D;
               double dz = (double)z + this.noiseX.GetValue((double)y * 0.02D, (double)x * 0.02D) * 20.0D - 10.0D;
               double xx = dx * dx;
               double yy = dy * dy;
               double zz = dz * dz;
               if (xx + yy + zz < (double)rr) {
                  if (wy > lavaH) {
                     structure.setBlock(AIR, new BlockPos(wx, wy, wz));
                  } else {
                     structure.setBlock(LAVA, new BlockPos(wx, wy, wz));
                  }
               }
            }
         }
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
               BigStructureCity city = new BigStructureCity(structureData.getCompoundTagAt(i), this.generator);
               this.structures.add(city);
            }
         } catch (IOException var9) {
            var9.printStackTrace();
         }
      }

   }
}
