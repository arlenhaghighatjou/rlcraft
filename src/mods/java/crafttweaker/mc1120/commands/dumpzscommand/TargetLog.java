package crafttweaker.mc1120.commands.dumpzscommand;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.SpecialMessagesChat;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.function.BiConsumer;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.util.Pair;

public class TargetLog extends DumpZsTarget {
   public TargetLog() {
      super("log");
   }

   public String getDescription() {
      return "Dumps to the log";
   }

   public void execute(ICommandSender sender, MinecraftServer Server) {
      CraftTweakerAPI.logCommand("\nBracket Handlers:");

      for(Pair<Integer, IBracketHandler> pair : GlobalRegistry.getPrioritizedBracketHandlers()) {
         CraftTweakerAPI.logCommand(((IBracketHandler)pair.getValue()).getClass().getName() + ", priority: " + pair.getKey());
      }

      CraftTweakerAPI.logCommand("\nTypes:");
      GlobalRegistry.getTypes().getTypeMap().forEach((aClass, zenType) -> {
         CraftTweakerAPI.logCommand(aClass.getName() + ": " + zenType.getName());
      });
      CraftTweakerAPI.logCommand("\nGlobals:");
      GlobalRegistry.getGlobals().forEach((s, iZenSymbol) -> {
         CraftTweakerAPI.logCommand(s + ": " + iZenSymbol.toString());
      });
      CraftTweakerAPI.logCommand("\nExpansions:");
      GlobalRegistry.getExpansions().forEach((s, typeExpansion) -> {
         CraftTweakerAPI.logCommand(s + ": " + typeExpansion.toString());
      });
      CraftTweakerAPI.logCommand("\nRoot (Symbol Package):");
      GlobalRegistry.getRoot().getPackages().forEach(this::printZenSymbol);
      sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Dumped content of the GlobalRegistry", sender));
   }

   private void printZenSymbol(String s, IZenSymbol zenSymbol) {
      if (zenSymbol instanceof SymbolPackage) {
         this.printZenSymbolHelper(zenSymbol, 0);
      } else {
         CraftTweakerAPI.logCommand(s + ": " + zenSymbol.toString());
      }

   }

   private void printZenSymbolHelper(IZenSymbol zenSymbol, int depth) {
      int finalDepth = depth + 1;
      if (zenSymbol instanceof SymbolPackage) {
         SymbolPackage symbolPackage = (SymbolPackage)zenSymbol;
         symbolPackage.getPackages().forEach((s1, symbol) -> {
            CraftTweakerAPI.logCommand(StringUtils.repeat("\t", finalDepth) + s1 + ": " + symbol.toString());
            this.printZenSymbolHelper(symbol, finalDepth);
         });
      } else if (zenSymbol instanceof SymbolType || zenSymbol instanceof ZenTypeNative) {
         ZenTypeNative typeNative = null;
         if (zenSymbol instanceof SymbolType) {
            ZenType type = ((SymbolType)zenSymbol).getType();
            if (type instanceof ZenTypeNative) {
               typeNative = (ZenTypeNative)type;
            }
         } else {
            typeNative = (ZenTypeNative)zenSymbol;
         }

         if (typeNative != null) {
            for(String s : typeNative.dumpTypeInfo()) {
               CraftTweakerAPI.logCommand(StringUtils.repeat("\t", finalDepth) + s);
            }
         }
      }

   }
}
