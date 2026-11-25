package ivorius.reccomplex..shadow.mcopts.commands.parameters;

import ivorius.reccomplex..shadow.mcopts.MCOpts;
import ivorius.reccomplex..shadow.mcopts.accessor.AccessorBiomeDictionary;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MCP {
   public MCP() {
   }

   public static BlockPos parseBlockPos(BlockPos blockpos, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException {
      return new BlockPos(CommandBase.parseDouble((double)blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), CommandBase.parseDouble((double)blockpos.getY(), args[startIndex + 1], 0, 256, false), CommandBase.parseDouble((double)blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
   }

   public static Function<Parameter<String>, Parameter<BlockPos>> pos(Parameter<String> yp, Parameter<String> zp, BlockPos ref, boolean centerBlock) {
      return (xp) -> {
         return xp.orElse("~").flatMap((x) -> {
            return yp.orElse("~").flatMap((y) -> {
               return zp.orElse("~").map((z) -> {
                  return parseBlockPos(ref, new String[]{x, y, z}, 0, centerBlock);
               });
            });
         });
      };
   }

   public static Function<Parameter<String>, Parameter<BlockPos>> pos(BlockPos ref, boolean centerBlock) {
      return (p) -> {
         return (Parameter)pos(p.move(1), p.move(2), ref, centerBlock).apply(p);
      };
   }

   public static Function<Parameters, Parameter<BlockPos>> pos(String x, String y, String z, BlockPos ref, boolean centerBlock) {
      return (p) -> {
         return p.get(x).to(pos(p.get(y), p.get(z), ref, centerBlock));
      };
   }

   public static Parameter<Biome> biome(Parameter<String> p) {
      Parameter var10000 = p.map(ResourceLocation::new);
      RegistryNamespaced var10001 = Biome.REGISTRY;
      Biome.REGISTRY.getClass();
      return var10000.<Biome>map(var10001::getObject, (t) -> {
         return MCOpts.translations.commandException("commands.parameters.biome.invalid");
      });
   }

   public static Parameter<Type> biomeDictionaryType(Parameter<String> p) {
      return p.<Type>map(AccessorBiomeDictionary::getTypeWeak, (s) -> {
         return MCOpts.translations.commandException("commands.parameters.biometype.invalid", s);
      });
   }

   public static Parameter<WorldServer> dimension(Parameter<String> p, MinecraftServer server) {
      return p.filter((d) -> {
         return !d.equals("~");
      }, (Parameter.Function)null).<WorldServer>map(CommandBase::parseInt).<WorldServer>map(server::getWorld, (t) -> {
         return MCOpts.translations.commandException("commands.parameters.dimension.invalid");
      });
   }

   public static Function<Parameter<String>, Parameter<WorldServer>> dimension(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return dimension(p, server).orElse((WorldServer)sender.getEntityWorld());
      };
   }

   public static Parameter<Block> block(Parameter<String> p, ICommandSender commandSender) {
      return p.<Block>map((s) -> {
         return CommandBase.getBlockByText(commandSender, s);
      });
   }

   public static Parameter<Item> item(Parameter<String> p, ICommandSender commandSender) {
      return p.<Item>map((s) -> {
         return CommandBase.getItemByText(commandSender, s);
      });
   }

   public static Parameter<ICommand> command(Parameter<String> p, MinecraftServer server) {
      return p.<ICommand>map(server.getCommandManager().getCommands()::get, (s) -> {
         return new CommandNotFoundException();
      });
   }

   public static Function<Parameter<String>, Parameter<Entity>> entity(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getEntity(server, sender, s);
         });
      };
   }

   public static <T extends Entity> Function<Parameter<String>, Parameter<Entity>> entity(MinecraftServer server, ICommandSender sender, Class<T> targetClass) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getEntity(server, sender, s, targetClass);
         });
      };
   }

   public static Function<Parameter<String>, Parameter<List<Entity>>> entities(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getEntityList(server, sender, s);
         });
      };
   }

   public static Function<Parameter<String>, Parameter<EntityPlayerMP>> player(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getPlayer(server, sender, s);
         });
      };
   }

   public static Function<Parameter<String>, Parameter<List<EntityPlayerMP>>> players(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getPlayers(server, sender, s);
         });
      };
   }

   public static Function<Parameter<String>, Parameter<String>> playerName(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getPlayerName(server, sender, s);
         });
      };
   }

   public static Function<Parameter<String>, Parameter<String>> entityName(MinecraftServer server, ICommandSender sender) {
      return (p) -> {
         return p.map((s) -> {
            return CommandBase.getEntityName(server, sender, s);
         });
      };
   }

   public static Parameter<Rotation> rotation(Parameter<String> p) {
      return p.<Rotation>map(CommandBase::parseInt).<Rotation>map((i) -> {
         return i.intValue() > 40 ? i.intValue() / 90 : i.intValue();
      }).<Rotation>map(MCP::rotationFromInt);
   }

   public static Rotation rotationFromInt(int rotation) {
      switch((rotation % 4 + 4) % 4) {
      case 0:
         return Rotation.NONE;
      case 1:
         return Rotation.CLOCKWISE_90;
      case 2:
         return Rotation.CLOCKWISE_180;
      case 3:
         return Rotation.COUNTERCLOCKWISE_90;
      default:
         throw new IllegalStateException();
      }
   }
}
