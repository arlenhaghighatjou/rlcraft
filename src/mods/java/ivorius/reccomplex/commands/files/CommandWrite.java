package ivorius.reccomplex.commands.files;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.files.saving.FileSaver;
import ivorius.reccomplex.files.saving.FileSaverAdapter;
import ivorius.reccomplex.utils.RawResourceLocation;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.ResourceExpression;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandWrite extends CommandExpecting {
   public CommandWrite() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "write";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public void expect(Expect expect) {
      expect.next(RecurrentComplex.saver.keySet()).descriptionU("file type").required().next((params) -> {
         Optional var10000 = params.get(0).tryGet();
         FileSaver var10001 = RecurrentComplex.saver;
         RecurrentComplex.saver.getClass();
         return var10000.map(var10001::get).map((a) -> {
            return a.getRegistry().ids();
         });
      }).descriptionU("resource expression").repeat().named("directory", "d").then(RCE::resourceDirectory);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      String adapterID = (String)parameters.get(0).require();
      if (!RecurrentComplex.saver.has(adapterID)) {
         throw RecurrentComplex.translations.commandException("commands.rcsave.noregistry");
      } else {
         ResourceDirectory directory = (ResourceDirectory)parameters.get("directory").to(RCP::resourceDirectory).optional().orElse(ResourceDirectory.ACTIVE);
         Optional<FileSaverAdapter<?>> adapterOptional = Optional.ofNullable(RecurrentComplex.saver.get(adapterID));
         Set<String> ids = (Set)adapterOptional.map((a) -> {
            return a.getRegistry().ids();
         }).orElse(Collections.emptySet());
         ResourceExpression resourceExpression = (ResourceExpression)ExpressionCache.of(new ResourceExpression((idx) -> {
            return ((Boolean)adapterOptional.map((a) -> {
               return a.getRegistry().has(id);
            }).orElse(Boolean.valueOf(false))).booleanValue();
         }), (String)parameters.get(1).rest(NaP::join).require());
         int saved = 0;
         int failed = 0;

         for(String id : ids) {
            if (resourceExpression.test(new RawResourceLocation((String)adapterOptional.map((a) -> {
               return a.getRegistry().status(id).getDomain();
            }).orElseThrow(IllegalStateException::new), id))) {
               boolean success = RecurrentComplex.saver.trySave(directory.toPath(), adapterID, id);
               if (success) {
                  ++saved;
               } else {
                  ++failed;
               }
            }
         }

         commandSender.sendMessage(RecurrentComplex.translations.format("commands.rcsaveall.result", saved, RCTextStyle.path(directory), failed));
         RCCommands.tryReload(RecurrentComplex.loader, LeveledRegistry.Level.CUSTOM);
         RCCommands.tryReload(RecurrentComplex.loader, LeveledRegistry.Level.SERVER);
      }
   }
}
