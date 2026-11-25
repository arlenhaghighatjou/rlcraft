package c4.champions.command;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSpawnChampion extends CommandBase {
   public CommandSpawnChampion() {
   }

   @Nonnull
   public String getName() {
      return "spawnchampion";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "champions.commands.spawnchampion.usage";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(args[0]), sender.getEntityWorld());
         if (!(entity instanceof EntityLiving)) {
            throw new CommandException("champions.commands.spawnchampion.entityError", new Object[]{args[0]});
         } else {
            EntityLiving living = (EntityLiving)entity;

            int tier;
            try {
               tier = Integer.parseInt(args[1]);
            } catch (NumberFormatException var15) {
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

            World world = sender.getEntityWorld();
            BlockPos pos = sender.getPosition();
            living.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
            IChampionship chp = CapabilityChampionship.getChampionship(living);
            if (chp != null) {
               Rank rank = RankManager.getRankForTier(tier);
               chp.setRank(rank);
               if (rank.getTier() > 0) {
                  if (argAffix.isEmpty()) {
                     Set<String> affixes = ChampionHelper.generateAffixes(rank, living);
                     chp.setAffixes(affixes);
                  } else {
                     chp.setAffixes(argAffix);
                  }

                  chp.setName(ChampionHelper.generateRandomName());
                  chp.getRank().applyGrowth(living);
                  UnmodifiableIterator var19 = chp.getAffixes().iterator();

                  while(var19.hasNext()) {
                     String s = (String)var19.next();
                     IAffix affix = AffixRegistry.getAffix(s);
                     if (affix != null) {
                        affix.onInitialSpawn(living, chp);
                     }
                  }
               }
            }

            living.onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
            world.spawnEntity(living);
            notifyCommandListener(sender, this, "champions.commands.spawnchampion.success", new Object[]{pos});
         }
      }
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return Collections.emptyList();
   }
}
