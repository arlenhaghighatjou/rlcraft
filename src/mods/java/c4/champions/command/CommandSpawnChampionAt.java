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

public class CommandSpawnChampionAt extends CommandBase {
   public CommandSpawnChampionAt() {
   }

   @Nonnull
   public String getName() {
      return "spawnchampionat";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Nonnull
   public String getUsage(@Nonnull ICommandSender sender) {
      return "champions.commands.spawnchampionat.usage";
   }

   public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
      if (args.length < 5) {
         throw new WrongUsageException(this.getUsage(sender), new Object[0]);
      } else {
         double posX = parseDouble(args[0]);
         double posY = parseDouble(args[1]);
         double posZ = parseDouble(args[2]);
         BlockPos blockPos = new BlockPos(posX, posY, posZ);
         Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(args[3]), sender.getEntityWorld());
         if (!(entity instanceof EntityLiving)) {
            throw new CommandException("champions.commands.spawnchampion.entityError", new Object[]{args[3]});
         } else {
            EntityLiving living = (EntityLiving)entity;

            int tier;
            try {
               tier = Integer.parseInt(args[4]);
            } catch (NumberFormatException var21) {
               throw new CommandException("champions.commands.spawnchampion.tierError", new Object[]{args[4]});
            }

            Set<String> argAffix = Sets.newHashSet();

            for(int i = 5; i < args.length; ++i) {
               String affix = args[i];
               AffixBase affixBase = AffixRegistry.getAffix(affix);
               if (affixBase == null) {
                  throw new CommandException("champions.commands.spawnchampion.affixError", new Object[]{args[i]});
               }

               argAffix.add(args[i]);
            }

            World world = sender.getEntityWorld();
            living.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
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
                  UnmodifiableIterator var25 = chp.getAffixes().iterator();

                  while(var25.hasNext()) {
                     String s = (String)var25.next();
                     IAffix affix = AffixRegistry.getAffix(s);
                     if (affix != null) {
                        affix.onInitialSpawn(living, chp);
                     }
                  }
               }
            }

            living.onInitialSpawn(world.getDifficultyForLocation(blockPos), (IEntityLivingData)null);
            world.spawnEntity(living);
            notifyCommandListener(sender, this, "champions.commands.spawnchampion.success", new Object[]{blockPos});
         }
      }
   }

   @Nonnull
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      return Collections.emptyList();
   }
}
