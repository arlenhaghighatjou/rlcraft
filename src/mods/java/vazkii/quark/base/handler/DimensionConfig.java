package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.World;
import vazkii.quark.base.module.ModuleLoader;

public class DimensionConfig {
   private final boolean blacklist;
   private final List<Integer> dims;

   public DimensionConfig(String parent) {
      this(parent, false, "0");
   }

   public DimensionConfig(String parent, String dimStr) {
      this(parent, false, dimStr.split(","));
   }

   public DimensionConfig(String parent, boolean blacklist, String... defaultValues) {
      String category = parent + ".dimensions";
      this.blacklist = ModuleLoader.config.getBoolean("Is Blacklist", category, blacklist, "");
      String[] dimensionValues = ModuleLoader.config.getStringList("Dimensions", category, defaultValues, "");
      this.dims = new ArrayList();

      for(String s : dimensionValues) {
         try {
            this.dims.add(Integer.valueOf(Integer.parseInt(s)));
         } catch (NumberFormatException var11) {
            ;
         }
      }

   }

   public boolean canSpawnHere(World world) {
      return this.dims.contains(Integer.valueOf(world.provider.getDimension())) != this.blacklist;
   }
}
