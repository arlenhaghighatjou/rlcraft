package paulevs.betternether.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import paulevs.betternether.world.BNWorldGenerator;

public class NetherBiome {
   static NoiseGeneratorOctaves featureScatter = new NoiseGeneratorOctaves(new Random(1337L), 3);
   float plantDensity = 1.0F;
   String name;
   NetherBiome edge;
   int edgeSize;
   List<NetherBiome> subbiomes;
   int sl;

   public NetherBiome(String name) {
      this.name = name;
      this.edge = this;
      this.edgeSize = 0;
      this.sl = 0;
      this.subbiomes = new ArrayList();
   }

   public void genSurfColumn(World world, BlockPos pos, Random random) {
   }

   public void genFloorObjects(World world, BlockPos pos, Random random) {
   }

   public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random) {
   }

   public void genCeilObjects(World world, BlockPos pos, Random random) {
   }

   protected double getFeatureNoise(BlockPos pos) {
      double[] value = new double[1];
      value = featureScatter.generateNoiseOctaves(value, pos.getX(), pos.getZ(), 1, 1, 0.1D, 0.1D, 1.0D);
      return value[0];
   }

   public void setDensity(float density) {
      this.plantDensity = density;
   }

   public String getName() {
      return this.name;
   }

   public NetherBiome getEdge() {
      return this.edge;
   }

   public void setEdge(NetherBiome edge) {
      this.edge = edge;
   }

   public int getEdgeSize() {
      return this.edgeSize;
   }

   public void setEdgeSize(int size) {
      this.edgeSize = size;
   }

   public void addSubBiome(NetherBiome biome) {
      this.subbiomes.add(biome);
      this.sl = this.subbiomes.size() << 3;
   }

   public NetherBiome getSubBiome(int x, int y, int z) {
      if (this.sl > 0) {
         int id = BNWorldGenerator.getSubBiome(x, y, z, this.sl);
         return id < this.subbiomes.size() ? (NetherBiome)this.subbiomes.get(id) : this;
      } else {
         return this;
      }
   }
}
