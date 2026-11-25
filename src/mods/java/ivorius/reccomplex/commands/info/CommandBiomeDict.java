package ivorius.reccomplex.commands.info;

import com.google.common.collect.Lists;
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
import ivorius.reccomplex.commands.structure.CommandSearchStructure;
import ivorius.reccomplex.commands.structure.StructureSearch;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CommandBiomeDict extends CommandSplit {
   public CommandBiomeDict() {
      super(RCConfig.commandPrefix + "biome");
      this.add(new SimpleCommand("search", (expect) -> {
         expect.skip().descriptionU("terms").required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            List<String> terms = (List)parameters.get(0).varargsList().require();
            CommandSearchStructure.postResultMessage("Biomes:", sender, RCTextStyle::biome, CommandSearchStructure.search(Biome.REGISTRY.getKeys(), (loc) -> {
               return StructureSearch.searchRank(terms, CommandBiomeDict.keywords(loc, (Biome)Biome.REGISTRY.getObject(loc)));
            }));
         }
      });
      this.add(new SimpleCommand("types", (expect) -> {
         expect.then(MCE::biome).required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            Biome biome = (Biome)parameters.get(0).to(MCP::biome).require();
            sender.sendMessage(RecurrentComplex.translations.format("commands.biomedict.get", RCTextStyle.biome(biome), ServerTranslations.join(Lists.newArrayList(BiomeDictionary.getTypes(biome)).stream().map(RCTextStyle::biomeType).toArray())));
         }
      });
      this.add(new SimpleCommand("list", (expect) -> {
         expect.then(MCE::biomeType).required();
      }) {
         public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            Expect var10001 = this.expect();
            var10001.getClass();
            Parameters parameters = Parameters.of(args, var10001::declare);
            Type type = (Type)parameters.get(0).to(MCP::biomeDictionaryType).require();
            sender.sendMessage(RecurrentComplex.translations.format("commands.biomedict.list", RCTextStyle.biomeType(type), ServerTranslations.join(Lists.newArrayList(BiomeDictionary.getBiomes(type)).stream().map(RCTextStyle::biome).toArray())));
         }
      });
      this.permitFor(2);
   }

   @Nonnull
   public static Collection<String> keywords(ResourceLocation id, Biome biome) {
      return Arrays.asList(id.toString(), biome.getBiomeName());
   }
}
