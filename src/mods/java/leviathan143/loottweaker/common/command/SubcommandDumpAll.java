package leviathan143.loottweaker.common.command;

import leviathan143.loottweaker.common.lib.LootTableDumper;
import leviathan143.loottweaker.common.lib.LootTableFinder;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubcommandDumpAll implements Subcommand {
   private static final Logger LOGGER = LogManager.getLogger();

   public SubcommandDumpAll() {
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      if (!LootTableFinder.DEFAULT.fullScanPerformed()) {
         sender.sendMessage(new TextComponentTranslation("loottweaker.messages.info.locatingLootTables", new Object[0]));
      }

      for(ResourceLocation tableId : LootTableFinder.DEFAULT.findAll()) {
         try {
            LootTableDumper.DEFAULT.dump(sender.getEntityWorld(), tableId);
         } catch (Exception var7) {
            sender.sendMessage(new TextComponentTranslation("loottweaker.commands.dump.all.exception", new Object[]{tableId}));
            LOGGER.error("Unable to dump {}", tableId, this.unwrap(var7));
         }
      }

      sender.sendMessage(new TextComponentTranslation("loottweaker.commands.dump.all.done", new Object[0]));
   }

   private Throwable unwrap(Throwable t) {
      Throwable unwrapped;
      for(unwrapped = t; unwrapped.getCause() != null && unwrapped.getMessage().equals(unwrapped.getCause().toString()); unwrapped = unwrapped.getCause()) {
         ;
      }

      return unwrapped;
   }
}
