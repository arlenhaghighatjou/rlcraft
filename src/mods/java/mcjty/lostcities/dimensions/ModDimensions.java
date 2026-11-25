package mcjty.lostcities.dimensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcjty.lostcities.config.LostCityConfiguration;
import mcjty.lostcities.dimensions.world.LostWoodlandMansion;
import mcjty.lostcities.dimensions.world.LostWorldProvider;
import mcjty.lostcities.dimensions.world.LostWorldType;
import mcjty.lostcities.dimensions.world.LostWorldTypeAdapter;
import mcjty.lostcities.dimensions.world.LostWorldTypeBOP;
import mcjty.lostcities.setup.ModSetup;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

public class ModDimensions {
   public static LostWorldType worldType;
   public static LostWorldTypeBOP worldTypeBOP;
   public static List<LostWorldTypeAdapter> worldTypeAdapterList = new ArrayList();
   public static DimensionType lostDimensionType;
   public static Map<Integer, String> dimensionProfileMap = new HashMap();

   public ModDimensions() {
   }

   public static void init() {
      worldType = new LostWorldType();
      if (ModSetup.biomesoplenty) {
         worldTypeBOP = new LostWorldTypeBOP();
      }

      for(String worldtype : LostCityConfiguration.ADAPTING_WORLDTYPES) {
         worldTypeAdapterList.add(new LostWorldTypeAdapter(worldtype));
      }

      if (LostCityConfiguration.DIMENSION_ID != -1) {
         lostDimensionType = DimensionType.register("lostcities", "_lost", LostCityConfiguration.DIMENSION_ID, LostWorldProvider.class, false);
         DimensionManager.registerDimension(LostCityConfiguration.DIMENSION_ID, lostDimensionType);
         dimensionProfileMap.put(Integer.valueOf(LostCityConfiguration.DIMENSION_ID), LostCityConfiguration.DIMENSION_PROFILE);
      }

      for(String dimInfo : LostCityConfiguration.ADDITIONAL_DIMENSIONS) {
         String[] split = StringUtils.split(dimInfo, ':');
         int id = Integer.parseInt(split[0]);
         String profile = split[1];
         if (lostDimensionType == null) {
            lostDimensionType = DimensionType.register("lostcities", "_lost", id, LostWorldProvider.class, false);
         }

         DimensionManager.registerDimension(id, lostDimensionType);
         dimensionProfileMap.put(Integer.valueOf(id), profile);
      }

      MapGenStructureIO.registerStructure(LostWoodlandMansion.Start.class, "LostMansion");
   }
}
