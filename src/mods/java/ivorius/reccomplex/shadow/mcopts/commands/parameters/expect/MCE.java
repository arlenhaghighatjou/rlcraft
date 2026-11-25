package ivorius.reccomplex..shadow.mcopts.commands.parameters.expect;

import ivorius.reccomplex..shadow.mcopts.accessor.AccessorBiomeDictionary;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;

public class MCE {
   public MCE() {
   }

   public static void xyz(Expect e) {
      e.then(MCE::x).then(MCE::y).then(MCE::z).atOnce(3);
   }

   public static void xz(Expect e) {
      e.then(MCE::x).then(MCE::z).atOnce(3);
   }

   public static Consumer<Expect> pos(String x, String y, String z) {
      return (e) -> {
         e.named(x).then(MCE::x).named(y).then(MCE::y).named(z).then(MCE::z).atOnce(3);
      };
   }

   public static void x(Expect e) {
      e.nextRaw((ser, sen, args, pos) -> {
         return CommandBase.getTabCompletionCoordinate(args.lastAsArray(), 0, pos);
      }).description("commands.parameters.x");
   }

   public static void y(Expect e) {
      e.nextRaw((ser, sen, args, pos) -> {
         return CommandBase.getTabCompletionCoordinate(args.lastAsArray(), -1, pos);
      }).description("commands.parameters.y");
   }

   public static void z(Expect e) {
      e.nextRaw((ser, sen, args, pos) -> {
         return CommandBase.getTabCompletionCoordinate(args.lastAsArray(), -2, pos);
      }).description("commands.parameters.z");
   }

   public static void biome(Expect e) {
      e.next(Biome.REGISTRY.getKeys()).description("commands.parameters.biome");
   }

   public static void biomeType(Expect e) {
      e.next(AccessorBiomeDictionary.getMap().keySet()).description("commands.parameters.biometype");
   }

   public static void dimension(Expect e) {
      e.next(Arrays.stream(DimensionManager.getIDs())).description("commands.parameters.dimension");
   }

   public static void block(Expect e) {
      e.next(Block.REGISTRY.getKeys()).description("commands.parameters.block");
   }

   public static void item(Expect e) {
      e.next(Item.REGISTRY.getKeys()).description("commands.parameters.item");
   }

   public static void command(Expect e) {
      e.next((server, sender, args, pos) -> {
         return server.getCommandManager().getCommands().keySet();
      }).description("commands.parameters.command");
   }

   public static void commandArguments(Expect e, Function<Parameters, Parameter<String>> start) {
      e.nextRaw((server1, sender, params, pos1) -> {
         Parameter<String> commandParameter = (Parameter)start.apply(params);
         Optional var10000 = commandParameter.tryGet();
         Map var10001 = server1.getCommandManager().getCommands();
         var10001.getClass();
         Optional<ICommand> other = var10000.map(var10001::get);
         return (void)other.map((c) -> {
            return (void)c.getTabCompletions(server1, sender, (String[])commandParameter.move(1).to(NaP::varargs).get(), pos1);
         }).orElse(Collections.emptyList());
      }).descriptionU("args...");
   }

   public static void entity(Expect e) {
      e.next((server, sender, parameters, pos) -> {
         return Arrays.stream(server.getOnlinePlayerNames());
      }).description("commands.parameters.entity");
   }

   public static void player(Expect e) {
      e.next((server, sender, parameters, pos) -> {
         return Arrays.stream(server.getOnlinePlayerNames());
      }).description("commands.parameters.player");
   }

   public static void rotation(Expect e) {
      e.any("0", "90", "180", "270").description("commands.parameters.rotation");
   }
}
