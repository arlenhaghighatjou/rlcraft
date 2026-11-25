package rtg.api.world.deco;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.util.Logger;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;
import rtg.api.world.gen.feature.WorldGenDoublePlantRTG;
import rtg.api.world.gen.feature.WorldGenFlowersRTG;

public class DecoFlowersRTG extends DecoBase {
   private final Collection<EnumFlowerType> flowers = Sets.newHashSet();
   private final Collection<EnumPlantType> plants = Sets.newHashSet();
   private float strengthFactor;
   private int minY;
   private int maxY;
   /** @deprecated */
   @Deprecated
   private DecoFlowersRTG.HeightType heightType;
   private int chance;
   private int notEqualsZeroChance;
   private int loops;

   public DecoFlowersRTG() {
      this.setChance(1);
      this.setNotEqualsZeroChance(1);
      this.setMinY(1);
      this.setMaxY(255);
      this.setHeightType(DecoFlowersRTG.HeightType.NEXT_INT);
      this.setStrengthFactor(0.0F);
      this.setLoops(1);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.FLOWER});
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, Random rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      if (this.flowers != null && !this.flowers.isEmpty() || this.plants != null && !this.plants.isEmpty()) {
         if (TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, EventType.FLOWERS)) {
            int loopCount = this.strengthFactor > 0.0F ? (int)this.strengthFactor : this.loops;

            for(int i = 0; i < loopCount * 16; ++i) {
               BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
               int y;
               switch(this.heightType) {
               case NEXT_INT:
                  y = getRangedRandom(rand, this.minY, this.maxY);
                  break;
               case GET_HEIGHT_VALUE:
                  y = rtgWorld.world().getHeight(pos).getY();
                  break;
               default:
                  y = rand.nextInt(this.maxY);
               }

               if (this.flowers != null && !this.flowers.isEmpty()) {
                  if (this.notEqualsZeroChance > 1) {
                     if (rand.nextInt(this.notEqualsZeroChance) != 0) {
                        (new WorldGenFlowersRTG(this.flowers)).generate(rtgWorld.world(), rand, pos.up(y));
                     }
                  } else if (rand.nextInt(this.chance) == 0) {
                     (new WorldGenFlowersRTG(this.flowers)).generate(rtgWorld.world(), rand, pos.up(y));
                  }
               }

               if (this.plants != null && !this.plants.isEmpty()) {
                  if (this.notEqualsZeroChance > 1) {
                     if (rand.nextInt(this.notEqualsZeroChance) != 0) {
                        (new WorldGenDoublePlantRTG((EnumPlantType)Iterables.get(this.plants, rand.nextInt(this.plants.size())))).generate(rtgWorld.world(), rand, pos.up(y));
                     }
                  } else if (rand.nextInt(this.chance) == 0) {
                     (new WorldGenDoublePlantRTG((EnumPlantType)Iterables.get(this.plants, rand.nextInt(this.plants.size())))).generate(rtgWorld.world(), rand, pos.up(y));
                  }
               }
            }
         }

      } else {
         Logger.error("DecoFlowersRTG called with a null or empty flower/plant list in biome {} at chunk {}", Biome.REGISTRY.getNameForObject(biome.baseBiome()), chunkPos.toString());
      }
   }

   public DecoFlowersRTG addFlowers(EnumFlowerType... flowers) {
      this.flowers.addAll(Lists.newArrayList(flowers));
      return this;
   }

   public DecoFlowersRTG addPlants(EnumPlantType... plants) {
      this.plants.addAll(Lists.newArrayList(plants));
      return this;
   }

   public Collection<EnumFlowerType> getFlowers() {
      return Collections.unmodifiableCollection(this.flowers);
   }

   public float getStrengthFactor() {
      return this.strengthFactor;
   }

   public DecoFlowersRTG setStrengthFactor(float strengthFactor) {
      this.strengthFactor = strengthFactor;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoFlowersRTG setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoFlowersRTG setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }

   public DecoFlowersRTG.HeightType getHeightType() {
      return this.heightType;
   }

   public DecoFlowersRTG setHeightType(DecoFlowersRTG.HeightType heightType) {
      this.heightType = heightType;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoFlowersRTG setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getNotEqualsZeroChance() {
      return this.notEqualsZeroChance;
   }

   public DecoFlowersRTG setNotEqualsZeroChance(int notEqualsZeroChance) {
      this.notEqualsZeroChance = notEqualsZeroChance;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoFlowersRTG setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   /** @deprecated */
   @Deprecated
   public static enum HeightType {
      NEXT_INT,
      GET_HEIGHT_VALUE;

      private HeightType() {
      }
   }
}
