package crafttweaker.api.world;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("crafttweaker.world.IBiomeType")
public interface IBiomeType {
   Object getInternal();

   @ZenGetter("name")
   String getName();

   @ZenGetter("biomes")
   List<IBiome> getBiomes();
}
