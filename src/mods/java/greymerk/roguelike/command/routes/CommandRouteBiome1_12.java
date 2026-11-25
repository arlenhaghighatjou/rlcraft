package greymerk.roguelike.command.routes;

import greymerk.roguelike.command.CommandBase;
import greymerk.roguelike.command.CommandContext1_12;
import greymerk.roguelike.command.CommandRouteBase;
import greymerk.roguelike.util.ArgumentParser;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CommandRouteBiome1_12 extends CommandRouteBase {
   public CommandRouteBiome1_12(CommandBase commandBase) {
      super(commandBase);
   }

   public void execute(CommandContext1_12 context, List<String> args) {
      Coord pos = this.getCoord(context, args);
      if (pos != null) {
         context.sendSpecial("Biome Information for " + pos);
         WorldEditor editor = context.createEditor();
         Biome biome = editor.getInfo(pos).getBiome();
         context.sendSpecial(biome.getBiomeName());
         Set<Type> biomeTypes = BiomeDictionary.getTypes(biome);
         String types = (String)biomeTypes.stream().map((type) -> {
            return (void)(type.getName() + " ");
         }).collect(Collectors.joining());
         context.sendSpecial(types);
      }
   }

   private Coord getCoord(CommandContext1_12 context, List<String> args) {
      ArgumentParser ap = new ArgumentParser(args);
      Coord pos;
      if (!ap.hasEntry(0)) {
         pos = context.getPos();
      } else {
         int x;
         int z;
         try {
            x = net.minecraft.command.CommandBase.parseInt(ap.get(0));
            z = net.minecraft.command.CommandBase.parseInt(ap.get(1));
         } catch (NumberInvalidException var8) {
            context.sendFailure("Invalid Coords: X Z");
            return null;
         }

         pos = new Coord(x, 0, z);
      }

      return pos;
   }
}
