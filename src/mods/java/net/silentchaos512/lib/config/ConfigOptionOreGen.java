package net.silentchaos512.lib.config;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigOptionOreGen {
   public static final float VEIN_COUNT_MIN = 0.0F;
   public static final float VEIN_COUNT_MAX = 1000.0F;
   public static final int VEIN_SIZE_MIN = 0;
   public static final int VEIN_SIZE_MAX = 1000;
   public static final int Y_MIN = 0;
   public static final int Y_MAX = 255;
   public final String name;
   public float veinCount;
   public int veinSize;
   public int minY;
   public int maxY;
   public final int dimension;
   public int[] dimensionBlacklist = new int[0];

   public ConfigOptionOreGen(String name, int dimension, float veinCount, int veinSize, int minY, int maxY) {
      this.name = name;
      this.dimension = dimension;
      this.veinCount = veinCount;
      this.veinSize = veinSize;
      this.minY = minY;
      this.maxY = maxY;
   }

   public int getVeinCount(Random random) {
      float diff = this.veinCount - (float)((int)this.veinCount);
      return (int)this.veinCount + (random.nextFloat() < diff ? 1 : 0);
   }

   public ConfigOptionOreGen loadValue(Configuration c, String category) {
      return this.loadValue(c, category + "." + this.name, "World generation for " + this.name);
   }

   public ConfigOptionOreGen loadValue(Configuration c, String category, String comment) {
      c.setCategoryComment(category, comment);
      this.veinCount = (float)c.get(category, "Vein Count", (double)this.veinCount).getDouble();
      this.veinSize = c.get(category, "Vein Size", this.veinSize).getInt();
      this.minY = c.get(category, "Min Y", this.minY).getInt();
      this.maxY = c.get(category, "Max Y", this.maxY).getInt();
      this.dimensionBlacklist = c.get(category, "Dimension Blacklist", new int[0]).getIntList();
      return this.validate();
   }

   public ConfigOptionOreGen validate() {
      this.veinCount = MathHelper.clamp(this.veinCount, 0.0F, 1000.0F);
      this.veinSize = MathHelper.clamp(this.veinSize, 0, 1000);
      this.minY = MathHelper.clamp(this.minY, 0, 255);
      this.maxY = MathHelper.clamp(this.maxY, 0, 255);
      if (this.maxY <= this.minY) {
         this.maxY = this.minY + 1;
      }

      return this;
   }

   public boolean isEnabled() {
      return this.veinCount > 0.0F && this.veinSize > 0;
   }

   public BlockPos getRandomPos(Random random, int posX, int posZ) {
      return new BlockPos(posX + random.nextInt(16), this.minY + random.nextInt(this.maxY - this.minY), posZ + random.nextInt(16));
   }

   public boolean canSpawnInDimension(int dim) {
      for(int i : this.dimensionBlacklist) {
         if (i == dim) {
            return false;
         }
      }

      return true;
   }
}
