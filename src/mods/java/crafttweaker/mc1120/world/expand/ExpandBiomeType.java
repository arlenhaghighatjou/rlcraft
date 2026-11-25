package crafttweaker.mc1120.world.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBiomeType;
import crafttweaker.mc1120.world.MCBiome;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.world.IBiomeType")
@ZenRegister
public class ExpandBiomeType {
   public ExpandBiomeType() {
   }

   private static Type getInternal(IBiomeType expanded) {
      return CraftTweakerMC.getBiomeType(expanded);
   }

   @ZenGetter("biomes")
   @ZenMethod
   public static List<IBiome> getBiomes(IBiomeType biomeType) {
      return (List)BiomeDictionary.getBiomes(getInternal(biomeType)).stream().map(MCBiome::new).collect(Collectors.toList());
   }
}
