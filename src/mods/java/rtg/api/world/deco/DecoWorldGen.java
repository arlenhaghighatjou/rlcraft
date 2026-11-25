package rtg.api.world.deco;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.IRealisticBiome;

public class DecoWorldGen extends DecoBase {
   private WorldGenerator worldgen;
   private EventType eventtype;
   private int loops;
   private int chance;
   private int minY;
   private int maxY;

   public DecoWorldGen(WorldGenerator worldgen) {
      this.setWorldGen(worldgen);
      this.setEventType(EventType.CUSTOM);
      this.setLoops(1);
      this.setChance(1);
      this.setMinY(1);
      this.setMaxY(255);
      this.addDecoTypes(new DecoBase.DecoType[]{DecoBase.DecoType.WORLDGEN});
   }

   public DecoWorldGen(WorldGenerator worldgen, EventType eventtype) {
      this(worldgen);
      this.setEventType(eventtype);
   }

   public void generate(IRealisticBiome biome, RTGWorld rtgWorld, EaglercraftRandom rand, ChunkPos chunkPos, float river, boolean hasVillage) {
      for(int i = 0; i <= this.loops; ++i) {
         BlockPos pos = getOffsetPos(chunkPos).add(rand.nextInt(16), 0, rand.nextInt(16));
         int y = rtgWorld.world().getHeight(pos).getY();
         if (y >= this.minY && y <= this.maxY && rand.nextInt(this.chance) == 0 && TerrainGen.decorate(rtgWorld.world(), rand, chunkPos, this.eventtype)) {
            this.worldgen.generate(rtgWorld.world(), rand, pos.up(y));
         }
      }

   }

   public WorldGenerator getWorldGen() {
      return this.worldgen;
   }

   public DecoWorldGen setWorldGen(WorldGenerator worldgen) {
      this.worldgen = worldgen;
      return this;
   }

   public EventType getEventType() {
      return this.eventtype;
   }

   public DecoWorldGen setEventType(EventType eventtype) {
      this.eventtype = eventtype;
      return this;
   }

   public int getLoops() {
      return this.loops;
   }

   public DecoWorldGen setLoops(int loops) {
      this.loops = loops;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   public DecoWorldGen setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getMinY() {
      return this.minY;
   }

   public DecoWorldGen setMinY(int minY) {
      this.minY = minY;
      return this;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public DecoWorldGen setMaxY(int maxY) {
      this.maxY = maxY;
      return this;
   }
}
