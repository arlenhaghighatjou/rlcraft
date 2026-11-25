package ivorius.reccomplex..shadow.mcopts.commands;

import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

public class CommandSplit extends CommandBase {
   protected final Map<String, ICommand> commands;
   protected String name;
   protected int requiredPermission;

   public CommandSplit() {
      this.commands = new HashMap();
      this.add(new SimpleCommand("help", (expect) -> {
         expect.next(this.commands.keySet()).description("commands.parameters.command").required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Parameters parameters = Parameters.of(args, (Function)null);
            Parameter var10002 = parameters.get(0);
            Map var10003 = CommandSplit.this.commands;
            CommandSplit.this.commands.getClass();
            throw new WrongUsageException(((ICommand)var10002.map(var10003::get, (s) -> {
               return new CommandNotFoundException();
            }).optional().orElse(CommandSplit.this)).getUsage(sender), new Object[0]);
         }
      });
   }

   public CommandSplit(String name, ICommand... commands) {
      this();
      this.name = name;
      Arrays.stream(commands).forEach(this::add);
   }

   public CommandSplit(String name) {
      this();
      this.name = name;
   }

   public void add(ICommand command) {
      this.commands.put(command.getName(), command);
   }

   public Optional<ICommand> get(String name) {
      return Optional.ofNullable(this.commands.get(name));
   }

   public CommandSplit permitFor(int permission) {
      this.requiredPermission = permission;
      return this;
   }

   @Nullable
   public Pair<ICommand, String[]> leaf(MinecraftServer server, ICommandSender sender, String[] args) {
      return (Pair)Optional.ofNullable(args.length > 0 ? args[0] : null).flatMap(this::get).filter((c) -> {
         return c.checkPermission(server, sender);
      }).map((c) -> {
         return c instanceof CommandSplit ? ((CommandSplit)c).leaf(server, sender, this.splitParameters(args)) : Pair.of(c, this.splitParameters(args));
      }).orElse((Object)null);
   }

   public String getUsage(ICommandSender sender) {
      return String.format("%s %s<%s%s%s>", this.getName(), TextFormatting.RESET, TextFormatting.YELLOW, String.join(TextFormatting.RESET + "|" + TextFormatting.YELLOW, this.commands.keySet()), TextFormatting.RESET);
   }

   public String getName() {
      return this.name;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      this.validatedCommand(server, sender, args).execute(server, sender, this.splitParameters(args));
   }

   @Nonnull
   public String[] splitParameters(String[] args) {
      return (String[])Arrays.copyOfRange(args, 1, args.length);
   }

   @Nonnull
   public ICommand validatedCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         ICommand iCommand = (ICommand)this.get(args[0]).orElseThrow(CommandNotFoundException::new);
         if (!iCommand.checkPermission(server, sender)) {
            throw new CommandException("commands.generic.permission", new Object[0]);
         } else {
            return iCommand;
         }
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, (Collection)this.commands.entrySet().stream().filter((e) -> {
         return ((ICommand)e.getValue()).checkPermission(server, sender);
      }).map(Entry::getKey).collect(Collectors.toList())) : (List)this.get(args[0]).filter((e) -> {
         return e.checkPermission(server, sender);
      }).map((c) -> {
         return c.getTabCompletions(server, sender, this.splitParameters(args), targetPos);
      }).orElse(Collections.emptyList());
   }

   public int getRequiredPermissionLevel() {
      return this.requiredPermission;
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return super.checkPermission(server, sender) && this.commands.entrySet().stream().filter((e) -> {
         return !((String)e.getKey()).equals("help");
      }).map(Entry::getValue).anyMatch((c) -> {
         return c.checkPermission(server, sender);
      });
   }
}
