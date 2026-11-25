package ivorius.reccomplex.commands.structure;

import com.google.common.primitives.Doubles;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

public class CommandSearchStructure extends CommandExpecting {
   public static final int MAX_RESULTS = 20;

   public CommandSearchStructure() {
   }

   public static <T> void postResultMessage(String prefix, ICommandSender sender, Function<T, ? extends ITextComponent> toComponent, Queue<T> list) {
      if (list.size() > 0) {
         boolean cut = list.size() > 20;
         ITextComponent[] components = new TextComponentBase[cut ? 20 : list.size()];

         for(int i = 0; i < components.length; ++i) {
            if (cut && i == components.length - 1) {
               components[i] = new TextComponentString("... (" + list.size() + ")");
            } else {
               components[i] = (ITextComponent)toComponent.apply(list.remove());
            }
         }

         sender.sendMessage(ServerTranslations.join("", new TextComponentString(prefix), ServerTranslations.join((Object[])components)));
      } else {
         sender.sendMessage(RecurrentComplex.translations.get("commands.rcsearch.empty"));
      }

   }

   @Nonnull
   public static <T> PriorityQueue<T> search(Set<T> omega, ToDoubleFunction<T> rank) {
      PriorityQueue<T> strucs = new PriorityQueue(10, (o1, o2) -> {
         return Doubles.compare(rank.applyAsDouble(o1), rank.applyAsDouble(o2));
      });
      strucs.addAll((Collection)omega.stream().filter((s) -> {
         return rank.applyAsDouble(s) > 0.0D;
      }).collect(Collectors.toList()));
      return strucs;
   }

   public static void consider(List<ToDoubleFunction<String>> ranks, Parameter<String> parameter, Parameter.Function<Parameter<String>, ToDoubleFunction<Structure<?>>> supplier) throws CommandException {
      if (parameter.has(1)) {
         ToDoubleFunction<Structure<?>> fun = (ToDoubleFunction)supplier.apply(parameter);
         ranks.add((name) -> {
            return (void)fun.applyAsDouble(StructureRegistry.INSTANCE.get(name));
         });
      }
   }

   public static <T> void consider(List<ToDoubleFunction<String>> ranks, Parameter<String> parameter, Function<Parameter<String>, Parameter<T>> fun, ToDoubleBiFunction<Structure<?>, T> rank) throws CommandException {
      consider(ranks, parameter, (param) -> {
         T t = (T)param.to(fun).require();
         return (void)((structure) -> {
            return (void)rank.applyAsDouble(structure, t);
         });
      });
   }

   public String getName() {
      return "search";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.skip().descriptionU("terms").optional().repeat().named("containing", "c").words(MCE::block).descriptionU("block expression").named("biome", "b").then(MCE::biome).descriptionU("biome id").named("dimension", "d").then(MCE::dimension).descriptionU("dimension id").named("maze").skip().descriptionU("maze id").named("list").skip().descriptionU("structure list id").named("author").skip().descriptionU("author").flag("all", "a");
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      List<ToDoubleFunction<String>> ranks = new ArrayList();
      consider(ranks, parameters.get(0), Parameter::varargsList, (s, t) -> {
         return StructureSearch.searchRank(t, StructureSearch.keywords(StructureRegistry.INSTANCE.id(s), s));
      });
      consider(ranks, parameters.get("containing"), (e) -> {
         return RCP.expression(e, new BlockExpression(RecurrentComplex.specialRegistry));
      }, StructureSearch::containedBlocks);
      consider(ranks, parameters.get("biome"), MCP::biome, StructureSearch::biome);
      consider(ranks, parameters.get("dimension"), MCP.dimension(server, sender), StructureSearch::dimension);
      consider(ranks, parameters.get("maze"), (p) -> {
         return p;
      }, StructureSearch::maze);
      consider(ranks, parameters.get("list"), (p) -> {
         return p;
      }, StructureSearch::list);
      consider(ranks, parameters.get("author"), (p) -> {
         return p;
      }, StructureSearch::author);
      boolean all = parameters.has("all");
      if (ranks.stream().noneMatch(Objects::nonNull)) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         postResultMessage("Results: ", sender, RCTextStyle::structure, search(all ? StructureRegistry.INSTANCE.ids() : StructureRegistry.INSTANCE.activeIDs(), (name) -> {
            return ranks.stream().filter(Objects::nonNull).mapToDouble((f) -> {
               return f.applyAsDouble(name);
            }).reduce(1.0D, (a, b) -> {
               return a * b;
            });
         }));
      }
   }
}
