package vazkii.quark.base.command;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageChangeConfig;

public class CommandConfig extends CommandBase {
   private final Multimap<String, ConfigPropertyDescriptor> configKeys = HashMultimap.create();
   private final List<String> categoryNames = Lists.newArrayList();
   private final Multimap<String, String> keyNames = HashMultimap.create();

   public CommandConfig() {
      Configuration config = ModuleLoader.config;
      List<ConfigCategoryDescriptor> toProcess = (List)config.getCategoryNames().stream().map((name) -> {
         return new ConfigCategoryDescriptor((ConfigCategory)null, config, name);
      }).collect(Collectors.toList());

      while(!toProcess.isEmpty()) {
         ConfigCategoryDescriptor key = (ConfigCategoryDescriptor)toProcess.remove(0);
         ConfigCategory category = key.category();
         if (category != null) {
            String fullKey = key.getFullKey();
            this.categoryNames.add(fullKey);

            for(Property property : category.getOrderedValues()) {
               this.keyNames.put(fullKey, property.getName());
               this.configKeys.put(fullKey, new ConfigPropertyDescriptor(property.getName(), property.getType()));
            }

            for(String subKey : key.subCategories()) {
               toProcess.add(new ConfigCategoryDescriptor(category, config, subKey));
            }
         }
      }

   }

   @Nonnull
   public String getName() {
      return "quark-config";
   }

   @Nonnull
   public List<String> getAliases() {
      return Collections.singletonList("quarkconfig");
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "commands.quarkconfig.usage";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length < 3) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         boolean save = args.length >= 5 && args[4].equals("save");
         String category = args[0];
         String key = args[1];
         String value = args[2];
         if (!this.configKeys.containsKey(category)) {
            throw new CommandException("commands.quarkconfig.invalid_category", new Object[]{category});
         } else {
            Optional<ConfigPropertyDescriptor> descriptor = this.configKeys.get(category).stream().filter((desc) -> {
               return desc.key.equals(key);
            }).findFirst();
            if (descriptor.isPresent()) {
               Type type = ((ConfigPropertyDescriptor)descriptor.get()).type;
               GlobalConfig.changeConfig(category, key, value, type, save);
               EntityPlayerMP player = args.length >= 4 ? getPlayer(server, sender, args[3]) : null;
               if (player != null) {
                  NetworkHandler.INSTANCE.sendTo(new MessageChangeConfig(category, key, value, type, save), player);
               } else {
                  NetworkHandler.INSTANCE.sendToAll(new MessageChangeConfig(category, key, value, type, false));
               }

            } else {
               throw new CommandException("commands.quarkconfig.invalid_property", new Object[]{category, key});
            }
         }
      }
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.categoryNames);
      } else if (args.length == 2) {
         Collection<String> keyNameSet = this.keyNames.get(args[0]);
         return keyNameSet == null ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, keyNameSet);
      } else {
         return args.length == 4 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
      }
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 3;
   }
}
