package codersafterdark.reskillable.commands;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.LockUnlockableEvent;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent;
import codersafterdark.reskillable.api.toast.ToastHelper;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

public class CmdToggleTrait extends CommandBase {
   public CmdToggleTrait() {
   }

   @Nonnull
   public String getName() {
      return "toggletrait";
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "reskillable.command.toggletrait.usage";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length == 0) {
         throw new CommandException("reskillable.command.invalid.missing.playertrait", new Object[0]);
      } else if (args.length == 1) {
         throw new CommandException("reskillable.command.invalid.missing.trait", new Object[0]);
      } else {
         EntityPlayerMP player = getPlayer(server, sender, args[0]);
         args[1] = args[1].replaceAll(":", ".");
         String[] parts = args[1].split("\\.");
         ResourceLocation traitName = parts.length > 1 ? new ResourceLocation(parts[0], args[1].substring(parts[0].length() + 1)) : new ResourceLocation(args[1]);
         if (!ReskillableRegistries.UNLOCKABLES.containsKey(traitName)) {
            throw new CommandException("reskillable.command.invalid.trait", new Object[]{traitName});
         } else {
            Unlockable trait = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(traitName);
            PlayerData data = PlayerDataHandler.get(player);
            PlayerSkillInfo skillInfo = data.getSkillInfo(trait.getParentSkill());
            if (skillInfo.isUnlocked(trait)) {
               if (!MinecraftForge.EVENT_BUS.post(new LockUnlockableEvent.Pre(player, trait))) {
                  skillInfo.lock(trait, player);
                  data.saveAndSync();
                  MinecraftForge.EVENT_BUS.post(new LockUnlockableEvent.Post(player, trait));
                  sender.sendMessage(new TextComponentTranslation("reskillable.command.success.locktrait", new Object[]{traitName, player.getDisplayName()}));
               } else {
                  sender.sendMessage(new TextComponentTranslation("reskillable.command.fail.locktrait", new Object[]{traitName, player.getDisplayName()}));
               }
            } else if (!MinecraftForge.EVENT_BUS.post(new UnlockUnlockableEvent.Pre(player, trait))) {
               skillInfo.unlock(trait, player);
               data.saveAndSync();
               MinecraftForge.EVENT_BUS.post(new UnlockUnlockableEvent.Post(player, trait));
               ToastHelper.sendUnlockableToast(player, trait);
               sender.sendMessage(new TextComponentTranslation("reskillable.command.success.unlocktrait", new Object[]{traitName, player.getDisplayName()}));
            } else {
               sender.sendMessage(new TextComponentTranslation("reskillable.command.fail.unlocktrait", new Object[]{traitName, player.getDisplayName()}));
            }

         }
      }
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (args.length == 0) {
         return Arrays.asList(server.getPlayerList().getOnlinePlayerNames());
      } else if (args.length == 1) {
         String partialName = args[0];
         return (List)Arrays.stream(server.getPlayerList().getOnlinePlayerNames()).filter((name) -> {
            return name.startsWith(partialName);
         }).collect(Collectors.toList());
      } else if (args.length == 2) {
         String partial = args[1].replaceAll(":", ".");
         return (List)ReskillableRegistries.UNLOCKABLES.getValuesCollection().stream().map(Unlockable::getKey).filter((traitName) -> {
            return traitName.startsWith(partial);
         }).collect(Collectors.toList());
      } else {
         return new ArrayList();
      }
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }
}
