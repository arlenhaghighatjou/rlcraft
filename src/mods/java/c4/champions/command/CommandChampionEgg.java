package c4.champions.command;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.item.ItemChampionPlacer;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;

public class CommandChampionEgg extends CommandBase {
   public CommandChampionEgg() {
   }

   @Nonnull
   public String getName() {
      return "championegg";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "champions.commands.championegg.usage";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         ResourceLocation rl = new ResourceLocation(args[0]);
         Entity entity = EntityList.createEntityByIDFromName(rl, sender.getEntityWorld());
         if (entity != null && entity instanceof EntityLiving) {
            int tier;
            try {
               tier = Integer.parseInt(args[1]);
            } catch (NumberFormatException var11) {
               throw new CommandException("champions.commands.spawnchampion.tierError", new Object[]{args[1]});
            }

            Set<String> argAffix = Sets.newHashSet();

            for(int i = 2; i < args.length; ++i) {
               String affix = args[i];
               AffixBase affixBase = AffixRegistry.getAffix(affix);
               if (affixBase == null) {
                  throw new CommandException("champions.commands.spawnchampion.affixError", new Object[]{args[i]});
               }

               argAffix.add(args[i]);
            }

            if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
               ItemStack stack = new ItemStack(ChampionsRegistry.championEgg);
               ItemChampionPlacer.applyEntityInfoToItemStack(stack, rl, tier, argAffix);
               ItemHandlerHelper.giveItemToPlayer(player, stack);
               notifyCommandListener(sender, this, "champions.commands.championegg.success", new Object[]{player.getName()});
            }

         } else {
            throw new CommandException("champions.commands.spawnchampion.entityError", new Object[]{args[0]});
         }
      }
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return Collections.emptyList();
   }
}
