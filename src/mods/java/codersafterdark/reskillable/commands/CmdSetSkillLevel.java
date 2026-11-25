package codersafterdark.reskillable.commands;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.toast.ToastHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

public class CmdSetSkillLevel extends CommandBase {
   public CmdSetSkillLevel() {
   }

   @Nonnull
   public String getName() {
      return "setskilllevel";
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "reskillable.command.setskilllevel.usage";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length == 0) {
         throw new CommandException("reskillable.command.invalid.missing.playerskilllevel", new Object[0]);
      } else if (args.length == 1) {
         throw new CommandException("reskillable.command.invalid.missing.skilllevel", new Object[0]);
      } else if (args.length == 2) {
         throw new CommandException("reskillable.command.invalid.missing.level", new Object[0]);
      } else {
         EntityPlayerMP player = getPlayer(server, sender, args[0]);
         args[1] = args[1].replaceAll(":", ".");
         String[] parts = args[1].split("\\.");
         ResourceLocation skillName = parts.length > 1 ? new ResourceLocation(parts[0], args[1].substring(parts[0].length() + 1)) : new ResourceLocation(args[1]);
         if (!ReskillableRegistries.SKILLS.containsKey(skillName)) {
            throw new CommandException("reskillable.command.invalid.skill", new Object[]{skillName});
         } else {
            int level;
            try {
               level = Integer.parseInt(args[2]);
            } catch (NumberFormatException var12) {
               throw new CommandException("reskillable.command.invalid.missing.level", new Object[]{args[2]});
            }

            if (level < 1) {
               throw new CommandException("reskillable.command.invalid.belowmin", new Object[]{level});
            } else {
               Skill skill = (Skill)ReskillableRegistries.SKILLS.getValue(skillName);
               if (level > skill.getCap()) {
                  throw new CommandException("reskillable.command.invalid.pastcap", new Object[]{level, skill.getCap()});
               } else {
                  PlayerData data = PlayerDataHandler.get(player);
                  PlayerSkillInfo skillInfo = data.getSkillInfo(skill);
                  int oldLevel = skillInfo.getLevel();
                  if (!MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Pre(player, skill, level, oldLevel))) {
                     skillInfo.setLevel(level);
                     data.saveAndSync();
                     MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Post(player, skill, level, oldLevel));
                     ToastHelper.sendSkillToast(player, skill, level);
                     sender.sendMessage(new TextComponentTranslation("reskillable.command.success.setskilllevel", new Object[]{skillName, level, player.getDisplayName()}));
                  } else {
                     sender.sendMessage(new TextComponentTranslation("reskillable.command.fail.setskilllevel", new Object[]{skillName, level, player.getDisplayName()}));
                  }

               }
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
         return (List)ReskillableRegistries.SKILLS.getValuesCollection().stream().map(Skill::getKey).filter((skillNamex) -> {
            return skillName.startsWith(partial);
         }).collect(Collectors.toList());
      } else {
         if (args.length == 3) {
            args[1] = args[1].replaceAll(":", ".");
            String[] parts = args[1].split("\\.");
            ResourceLocation skillName = parts.length > 1 ? new ResourceLocation(parts[0], args[1].substring(parts[0].length() + 1)) : new ResourceLocation(args[1]);
            if (ReskillableRegistries.SKILLS.containsKey(skillName)) {
               Skill skill = (Skill)ReskillableRegistries.SKILLS.getValue(skillName);
               String level = args[2];
               return (List)IntStream.rangeClosed(1, skill.getCap()).mapToObj(Integer::toString).filter((iString) -> {
                  return iString.startsWith(level);
               }).collect(Collectors.toList());
            }
         }

         return new ArrayList();
      }
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }
}
