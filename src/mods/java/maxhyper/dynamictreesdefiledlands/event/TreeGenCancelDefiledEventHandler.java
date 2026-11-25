package maxhyper.dynamictreesdefiledlands.event;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
import java.util.Arrays;
import lykrast.defiledlands.common.init.ModBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class TreeGenCancelDefiledEventHandler {
   Biome[] defiledBiomes = new Biome[]{ModBiomes.desertDefiled, ModBiomes.forestTenebra, ModBiomes.forestVilespine, ModBiomes.hillsDefiled, ModBiomes.icePlainsDefiled, ModBiomes.plainsDefiled, ModBiomes.swampDefiled};

   public TreeGenCancelDefiledEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(Decorate event) {
      int dimensionId = event.getWorld().provider.getDimension();
      BiomeDataBase dbase = TreeGenerator.getTreeGenerator().getBiomeDataBase(dimensionId);
      if (dbase != TreeGenerator.DIMENSIONBLACKLISTED && !ModConfigs.dimensionBlacklist.contains(Integer.valueOf(dimensionId))) {
         Biome biome = event.getWorld().getBiome(event.getPos());
         if (Arrays.asList(this.defiledBiomes).contains(biome) && event.getType() == EventType.TREE) {
            event.setResult(Result.DENY);
         }
      }

   }
}
