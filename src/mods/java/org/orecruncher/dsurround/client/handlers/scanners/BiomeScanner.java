package org.orecruncher.dsurround.client.handlers.scanners;

import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public final class BiomeScanner implements ITickable {
   private static final int BIOME_SURVEY_RANGE = 20;
   private static final int MAX_BIOME_AREA = (int)Math.pow(41.0D, 2.0D);
   private final MutableBlockPos mutable = new MutableBlockPos();
   private int biomeArea;
   private Reference2FloatOpenHashMap<BiomeInfo> weights = new Reference2FloatOpenHashMap();
   private BiomeInfo surveyedBiome = null;
   private int surveyedDimension = 0;
   private BlockPos surveyedPosition = BlockPos.ORIGIN;

   public BiomeScanner() {
   }

   public void update() {
      BlockPos position = EnvironStateHandler.EnvironState.getPlayerPosition();
      if (this.surveyedBiome != EnvironStateHandler.EnvironState.getPlayerBiome() || this.surveyedDimension != EnvironStateHandler.EnvironState.getDimensionId() || this.surveyedPosition.compareTo(position) != 0) {
         this.surveyedBiome = EnvironStateHandler.EnvironState.getPlayerBiome();
         this.surveyedDimension = EnvironStateHandler.EnvironState.getDimensionId();
         this.surveyedPosition = position;
         this.biomeArea = 0;
         this.weights = new Reference2FloatOpenHashMap();
         if (EnvironStateHandler.EnvironState.getPlayerBiome().isFake()) {
            this.biomeArea = 1;
            this.weights.put(EnvironStateHandler.EnvironState.getPlayerBiome(), 1.0F);
         } else {
            IBlockAccessEx provider = ClientChunkCache.instance();

            for(int dX = -20; dX <= 20; ++dX) {
               for(int dZ = -20; dZ <= 20; ++dZ) {
                  this.mutable.setPos(this.surveyedPosition.getX() + dX, 0, this.surveyedPosition.getZ() + dZ);
                  Biome biome = provider.getBiome(this.mutable);
                  BiomeInfo info = BiomeUtil.getBiomeData(biome);
                  this.weights.addTo(info, 1.0F);
               }
            }

            this.biomeArea = MAX_BIOME_AREA;
         }
      }

   }

   public int getBiomeArea() {
      return this.biomeArea;
   }

   public Reference2FloatOpenHashMap<BiomeInfo> getBiomes() {
      return this.weights;
   }
}
