package ivorius.reccomplex.commands.info;

import com.google.common.collect.Lists;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandSplit;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import ivorius.reccomplex.commands.RCTextStyle;
import ivorius.reccomplex.commands.parameters.expect.IvE;
import ivorius.reccomplex.dimensions.DimensionDictionary;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandDimensionDict extends CommandSplit {
   public CommandDimensionDict() {
      super(RCConfig.commandPrefix + "dimension");
      this.add(new SimpleCommand("types", (expect) -> {
         expect.then(MCE::dimension).required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            WorldProvider provider = ((WorldServer)parameters.get(0).to(MCP.dimension(server, sender)).require()).provider;
            sender.sendMessage(RecurrentComplex.translations.format("commands.dimensiondict.get", RCTextStyle.dimension(provider.getDimension()), ServerTranslations.join(Lists.newArrayList(DimensionDictionary.getDimensionTypes(provider)).stream().map(RCTextStyle::dimensionType).toArray())));
         }
      });
      this.add(new SimpleCommand("list", (expect) -> {
         expect.then(IvE::dimensionType).required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            String type = (String)parameters.get(0).require();
            sender.sendMessage(RecurrentComplex.translations.format("commands.dimensiondict.list", RCTextStyle.dimensionType(type), ServerTranslations.join(Arrays.stream(CommandDimensionDict.allDimensionsOfType(type).toArray()).mapToObj(RCTextStyle::dimension).toArray())));
         }
      });
      this.permitFor(2);
   }

   public static TIntList allDimensionsOfType(String type) {
      TIntList intList = new TIntArrayList();
      Integer[] var2 = DimensionManager.getIDs();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int d = var2[var4].intValue();
         if (DimensionDictionary.dimensionMatchesType(DimensionManager.getProvider(d), type)) {
            intList.add(d);
         }
      }

      return intList;
   }
}
