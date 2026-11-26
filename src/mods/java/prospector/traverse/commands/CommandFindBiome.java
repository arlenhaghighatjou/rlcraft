package prospector.traverse.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import prospector.traverse.config.TraverseConfig;

public class CommandFindBiome extends CommandBase {
   public CommandFindBiome() {
   }

   public static BlockPos spiralOutwardsLookingForBiome(ICommandSender sender, World world, Biome biomeToFind, double startX, double startZ, int timeout) {
      double a = 16.0D / Math.sqrt(3.141592653589793D);
      double b = 2.0D * Math.sqrt(3.141592653589793D);
      double dist = 0.0D;
      long start = System.currentTimeMillis();
      PooledMutableBlockPos pos = PooledMutableBlockPos.retain();
      int previous = 0;
      int i = 0;

      for(int n = 0; dist < 2.147483647E9D; ++n) {
         if (System.currentTimeMillis() - start > (long)timeout) {
            return null;
         }

         double rootN = Math.sqrt((double)n);
         dist = a * rootN;
         double x = startX + dist * Math.sin(b * rootN);
         double z = startZ + dist * Math.cos(b * rootN);
         pos.setPos(x, 0.0D, z);
         if (sender instanceof EntityPlayer) {
            if (previous == 3) {
               previous = 0;
            }

            String s = previous == 0 ? "." : (previous == 1 ? ".." : "...");
            ((EntityPlayer)sender).sendStatusMessage(new TextComponentString("Scanning" + s), true);
            if (i == 1501) {
               ++previous;
               i = 0;
            }

            ++i;
         }

         if (world.getBiome(pos).equals(biomeToFind)) {
            pos.release();
            if (sender instanceof EntityPlayer) {
               ((EntityPlayer)sender).sendStatusMessage(new TextComponentString("Found Biome"), true);
            }

            return new BlockPos((int)x, 0, (int)z);
         }
      }

      return null;
   }

   public String getName() {
      return "findbiome";
   }

   public String getUsage(ICommandSender sender) {
      return "/findbiome <biome>";
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
      if (args.length == 1) {
         List<String> strings = new ArrayList();

         for(Biome b : ForgeRegistries.BIOMES.getValues()) {
            String s = b.getRegistryName().toString();
            if (s.toLowerCase().contains(args[0].toLowerCase())) {
               strings.add(s);
            }
         }

         return strings;
      } else {
         return Collections.emptyList();
      }
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Biome biome = null;
      if (args.length == 0) {
         sender.sendMessage(new TextComponentString("No biome specified"));
      } else {
         for(Biome b : ForgeRegistries.BIOMES.getValues()) {
            String name = b.getRegistryName().toString().replaceAll(" ", "_").toLowerCase();
            if (args[0].equalsIgnoreCase(name)) {
               biome = b;
            }
         }

         if (biome == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error! Biome '" + args[0] + "' cannot be found!"));
         } else {
            long start = System.currentTimeMillis();
            Biome finalBiome = biome;
            BlockPos pos = spiralOutwardsLookingForBiome(sender, sender.getEntityWorld(), finalBiome, (double)sender.getPosition().getX(), (double)sender.getPosition().getZ(), TraverseConfig.findBiomeCommandTimeout);
               if (pos == null) {
                  server.addScheduledTask(() -> {
                     sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error! Biome '" + args[0] + "' could not be found after " + TextFormatting.GRAY + TraverseConfig.findBiomeCommandTimeout + "ms" + TextFormatting.RED + "."));
                  });
               } else if (sender instanceof EntityPlayerMP) {
                  server.addScheduledTask(() -> {
                     EntityPlayerMP playerMP = (EntityPlayerMP)sender;
                     playerMP.connection.setPlayerLocation((double)pos.getX(), 150.0D, (double)pos.getZ(), 0.0F, 0.0F);
                     sender.sendMessage(new TextComponentString(TextFormatting.WHITE + "Found '" + finalBiome.getRegistryName().toString() + "' Biome! " + TextFormatting.GRAY + "(" + (System.currentTimeMillis() - start) + "ms)"));
                  });
               } else {
                  server.addScheduledTask(() -> {
                     sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error! An unknown error occurred."));
                  });
               }
         }
      }
   }
}
