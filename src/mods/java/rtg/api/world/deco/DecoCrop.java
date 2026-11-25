package rtg.api.world.deco;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenCrops;

public class DecoCrop extends DecoBase {
   private int type = 3;
   private int size = 5;
   private int density = 50;
   private int height = 2;
   private float strengthFactor;
   private int minY;
   private int maxY;
   private int chance;
   private boolean water;

   public DecoCrop() {
      this.setStrengthFactor(2.0F);
      this.setMinY(63);
      this.setMaxY(255);
      this.setChance(10);
      this.water = true;
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.WHEAT});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (this.chance > 1 && rand.nextInt(this.chance) == 0 && TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.CUSTOM)) {
         for(int i = 0; (float)i < this.strengthFactor; ++i) {
            BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
            int y = rtgWorld.world().getHeight(pos).getY();
            if (y >= this.minY && y <= this.maxY) {
               (new WorldGenCrops(this.type, this.size, this.density, this.height, this.water)).generate(rtgWorld.world(), rand, pos.up(y));
            }
         }
      }

   }

   public int getType() {
      return this.type;
   }

   public DecoCrop setType(int type) {
      this.type = type;
      return this;
   }

   public int getSize() {
      return this.size;
   }

   public DecoCrop setSize(int size) {
      this.size = size;
      return this;
   }

   public int getDensity() {
      return this.density;
   }

   public DecoCrop setDensity(int density) {
      this.density = density;
      return this;
   }

   public int getHeight() {
      return this.height;
   }

   public DecoCrop setHeight(int height) {
      this.height = height;
      return this;
   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoCrop setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoCrop setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoCrop setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoCrop setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public boolean isWater() {
      return this.water;
   }

   public DecoCrop setWater(boolean water) {
      this.water = water;
      return this;
   }
}
