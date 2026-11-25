package ichttt.mods.firstaid.common;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import ichttt.mods.firstaid.common.damagesystem.distribution.DirectDamageDistribution;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;

public class DebugDamageCommand extends CommandBase {
   public DebugDamageCommand() {
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (!(sender instanceof EntityPlayer)) {
         return Collections.emptyList();
      } else if (args.length != 1) {
         if (args.length == 3) {
            List<String> values = new ArrayList(2);
            values.add("true");
            values.add("false");
            return values;
         } else {
            return Collections.emptyList();
         }
      } else {
         EnumPlayerPart[] parts = EnumPlayerPart.values();
         List<String> values = new ArrayList(parts.length);

         for(EnumPlayerPart part : parts) {
            values.add(part.toString());
         }

         values.add("ALL");
         return getListOfStringsMatchingLastWord(args, values);
      }
   }

   @Nonnull
   public String getName() {
      return "damagePlayerPart";
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return sender instanceof EntityPlayer ? "/damage [part] [damage] (invoke debuffs)" : "Only usable by players";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (!(sender instanceof EntityPlayer)) {
         throw new CommandException(this.getUsage(sender), new Object[0]);
      } else if (args.length != 2 && args.length != 3) {
         throw new CommandException("Missing arguments. Usage: " + this.getUsage(sender), new Object[0]);
      } else {
         try {
            float damage = Float.parseFloat(args[1]);
            boolean debuff = true;
            if (args.length == 3) {
               debuff = Boolean.parseBoolean(args[2]);
            }

            if (args[0].equalsIgnoreCase("ALL")) {
               for(EnumPlayerPart part : EnumPlayerPart.VALUES) {
                  damage(part, damage, debuff, (EntityPlayer)sender);
               }
            } else {
               EnumPlayerPart part = EnumPlayerPart.valueOf(args[0].toUpperCase(Locale.ENGLISH));
               damage(part, damage, debuff, (EntityPlayer)sender);
            }

            if (sender instanceof EntityPlayerMP && !(sender instanceof FakePlayer)) {
               FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel((AbstractPlayerDamageModel)Objects.requireNonNull(((EntityPlayer)sender).getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)), false), (EntityPlayerMP)sender);
            }

         } catch (RuntimeException var10) {
            throw new CommandException(var10.toString(), new Object[0]);
         }
      }
   }

   private static void damage(EnumPlayerPart part, float damage, boolean debuff, EntityPlayer player) {
      if (damage != 0.0F) {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (damage > 0.0F) {
            DamageDistribution.handleDamageTaken(new DirectDamageDistribution(part, debuff), damageModel, damage, player, DamageSource.OUT_OF_WORLD, false, false);
         } else {
            damageModel.getFromEnum(part).heal(-damage, player, debuff);
         }

      }
   }
}
