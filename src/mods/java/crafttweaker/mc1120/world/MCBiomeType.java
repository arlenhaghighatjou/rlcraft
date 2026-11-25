package crafttweaker.mc1120.world;

import crafttweaker.api.world.IBiome;
import crafttweaker.api.world.IBiomeType;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MCBiomeType implements IBiomeType {
   private final Type type;

   public MCBiomeType(Type type) {
      this.type = type;
   }

   public Type getInternal() {
      return this.type;
   }

   public String getName() {
      return this.type.getName();
   }

   public List<IBiome> getBiomes() {
      return (List)BiomeDictionary.getBiomes(this.type).stream().map(MCBiome::new).collect(Collectors.toList());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBiomeType that = (MCBiomeType)o;
         return Objects.equals(this.type, that.type);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type});
   }
}
