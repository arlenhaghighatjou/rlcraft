package ivorius.reccomplex.commands.parameters.expect;

import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.CommandVirtual;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.random.Person;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class RCE {
   public RCE() {
   }

   public static void structurePredicate(Expect e) {
      e.then(RCE::structure).descriptionU("resource expression");
   }

   public static void structure(Expect e) {
      e.next(StructureRegistry.INSTANCE.ids()).descriptionU("structure");
   }

   public static void generationType(Expect e, Function<Parameters, Parameter<String>> fun) {
      e.next((params) -> {
         return (void)((Parameter)fun.apply(params)).to(RCP::structure).tryGet().map((structure) -> {
            return (void)structure.generationTypes(GenerationType.class).stream().map(GenerationType::id);
         });
      }).descriptionU("generation type id");
   }

   public static void schematic(Expect e) {
      e.next(SchematicLoader.currentSchematicFileNames()).descriptionU("schematic");
   }

   public static void resourceDirectory(Expect e) {
      e.any((Object[])ResourceDirectory.values()).descriptionU("directory");
   }

   public static void metadata(Expect e) {
      e.next(IntStream.range(0, 16).mapToObj(String::valueOf).collect(Collectors.toList())).descriptionU("metadata");
   }

   public static void virtualCommand(Expect ex) {
      ex.next((server, sender, args, pos) -> {
         return (List)server.getCommandManager().getCommands().entrySet().stream().filter((e) -> {
            return e.getValue() instanceof CommandVirtual;
         }).map(Entry::getKey).collect(Collectors.toList());
      });
      ex.descriptionU("virtual command");
   }

   public static void directionExpression(Expect ex) {
      List<String> ret = new ArrayList();
      Collections.addAll(ret, new String[]{"true", "false"});
      ret.addAll((Collection)Arrays.stream(EnumFacing.values()).map(EnumFacing::getName2).collect(Collectors.toList()));
      ret.addAll((Collection)Arrays.stream(Axis.values()).map(Axis::getName).collect(Collectors.toList()));
      Collections.addAll(ret, new String[]{"horizontal", "vertical"});
      ex.next(ret).descriptionU("direction expression");
   }

   public static void randomString(Expect e) {
      Random rand = new Random();
      e.any(Person.chaoticName(rand, rand.nextBoolean()));
   }

   public static void shape(Expect expect) {
      expect.any((Object[])RCP.Shape.values()).descriptionU("shape");
   }
}
