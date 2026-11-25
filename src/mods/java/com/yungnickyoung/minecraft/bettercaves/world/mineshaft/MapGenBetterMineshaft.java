package com.yungnickyoung.minecraft.bettercaves.world.mineshaft;

import com.yungnickyoung.minecraft.bettercaves.config.io.ConfigLoader;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCavesUtils;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureMineshaftStart;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.MapGenMineshaft.Type;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class MapGenBetterMineshaft extends MapGenMineshaft {
   private MapGenBase defaultMineshaftGen;
   private int liquidAltitude;

   public MapGenBetterMineshaft(InitMapGenEvent event) {
      this.defaultMineshaftGen = event.getOriginalGen();
   }

   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      MapGenStructureIO.registerStructure(MapGenBetterMineshaft.StructureBetterMineshaftStart.class, "Mineshaft");
      Biome biome = this.world.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
      Type mapgenmineshaft$type = biome instanceof BiomeMesa ? Type.MESA : Type.NORMAL;
      return new MapGenBetterMineshaft.StructureBetterMineshaftStart(this.world, this.rand, chunkX, chunkZ, mapgenmineshaft$type);
   }

   public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
      if (!BetterCavesUtils.isDimensionWhitelisted(worldIn.provider.getDimension())) {
         this.defaultMineshaftGen.generate(worldIn, x, z, primer);
      } else {
         if (this.world == null) {
            this.initialize(worldIn);
         }

         super.generate(worldIn, x, z, primer);
      }
   }

   private void initialize(World worldIn) {
      this.world = worldIn;
      ConfigHolder config = ConfigLoader.loadConfigFromFileForDimension(worldIn.provider.getDimension());
      this.liquidAltitude = ((Integer)config.liquidAltitude.get()).intValue();
   }

   private class StructureBetterMineshaftStart extends StructureMineshaftStart {
      public StructureBetterMineshaftStart(World worldIn, EaglercraftRandom rand, int chunkX, int chunkZ, Type type) {
         super(worldIn, rand, chunkX, chunkZ, type);
      }

      public void generateStructure(World worldIn, EaglercraftRandom rand, StructureBoundingBox structurebb) {
         this.components.removeIf((component) -> {
            return component.getBoundingBox().minY < MapGenBetterMineshaft.this.liquidAltitude + 5 || component.getBoundingBox().intersectsWith(structurebb) && !component.addComponentParts(worldIn, rand, structurebb);
         });
      }
   }
}
