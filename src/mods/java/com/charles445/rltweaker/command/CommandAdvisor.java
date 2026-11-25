package com.charles445.rltweaker.command;

import com.charles445.rltweaker.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class CommandAdvisor extends CommandBase {
   public CommandAdvisor() {
   }

   public String getName() {
      return "rladvisor";
   }

   public String getUsage(ICommandSender sender) {
      return "/rladvisor";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      World world = sender.getEntityWorld();
      this.inform("RLTweaker Advisor", sender);
      this.informPatches(server, sender, world);
   }

   public void informPatches(MinecraftServer server, ICommandSender sender, World world) {
      boolean bigRadius = World.MAX_ENTITY_RADIUS > 2.0D;
      if (!ModConfig.patches.particleThreading) {
         this.inform("Safety patch, set patches / particleThreading to true", sender);
      }

      if (ModConfig.patches.lessCollisions && !bigRadius) {
         this.inform("Unnecessary patch, set patches / lessCollisions to false", sender);
      } else if (!ModConfig.patches.lessCollisions && bigRadius) {
         this.inform("Performance patch, set patches / lessCollisions to true", sender);
      }

      if (!ModConfig.patches.betterCombatMountFix && Loader.isModLoaded("bettercombatmod")) {
         this.inform("Improvement patch, set patches / betterCombatMountFix to true", sender);
      }

      if (!ModConfig.patches.realBenchDupeBugFix) {
         try {
            Class.forName("pw.prok.realbench.WorkbenchTile");
            this.inform("Improvement patch, set patches / realBenchDupeBugFix to true", sender);
         } catch (Exception var6) {
            ;
         }
      }

      if (!Loader.isModLoaded("iceandfire") && !ModConfig.patches.iafFixMyrmexQueenHiveSpam) {
         this.inform("Improvement patch, set patches / iafFixMyrmexQueenHiveSpam to true", sender);
      }

      if (!ModConfig.patches.doorPathfindingFix) {
         this.inform("Improvement patch, set patches / doorPathfindingFix to true", sender);
      }

      if (ModConfig.patches.reducedSearchSize && !bigRadius) {
         this.inform("Unnecessary patch, set patches / reducedSearchSize to false", sender);
      } else if (!ModConfig.patches.reducedSearchSize && bigRadius) {
         this.inform("Performance patch, set patches / reducedSearchSize to true", sender);
      }

      if (!ModConfig.patches.patchBroadcastSounds) {
         this.inform("Optional patch, set patches / patchBroadcastSounds to true", sender);
      }

      if (ModConfig.patches.patchEnchantments && this.hasExamplesOnly(ModConfig.server.minecraft.blacklistedEnchantments)) {
         this.inform("Unnecessary patch, set patches / patchEnchantments to false", sender);
      } else if (!ModConfig.patches.patchEnchantments && !this.hasExamplesOnly(ModConfig.server.minecraft.blacklistedEnchantments)) {
         this.inform("Necessary patch for blacklisting enchantments, set patches / patchEnchantments to true", sender);
      }

      if (!ModConfig.patches.aggressiveMotionChecker) {
         this.inform("Improvement patch, set patches / aggressiveMotionChecker to true", sender);
      }

      if (ModConfig.patches.patchEntityBlockDestroy && this.hasExamplesOnly(ModConfig.server.minecraft.entityBlockDestroyBlacklist)) {
         this.inform("Unnecessary patch, set patches / patchEntityBlockDestroy to false", sender);
      } else if (!ModConfig.patches.patchEntityBlockDestroy && !this.hasExamplesOnly(ModConfig.server.minecraft.entityBlockDestroyBlacklist)) {
         this.inform("Necessary patch for blacklisting entity block destruction, set patches / patchEntityBlockDestroy to true", sender);
      }

      if (!ModConfig.patches.patchItemFrameDupe) {
         this.inform("Improvement patch, set patches patchItemFrameDupe to true", sender);
      }

      if (ModConfig.patches.patchPushReaction && this.hasExamplesOnly(ModConfig.server.minecraft.entityPushPrevention)) {
         this.inform("Unnecessary patch, set patches / patchPushReaction to false", sender);
      } else if (!ModConfig.patches.patchPushReaction && !this.hasExamplesOnly(ModConfig.server.minecraft.entityPushPrevention)) {
         this.inform("Necessary patch for entity push prevention, set patches / patchPushReaction to true", sender);
      }

      if (!ModConfig.patches.patchAnvilDupe) {
         this.inform("Improvement patch, set patches / patchAnvilDupe to true", sender);
      }

      if (!ModConfig.patches.patchOverlayMessage) {
         this.inform("Optional patch, set patches / patchOverlayMessage to true", sender);
      }

      if (ModConfig.patches.patchHopper && this.hasExamplesOnly(ModConfig.server.minecraft.hopperBlockBlacklist)) {
         this.inform("Unnecessary patch, set patches / patchHopper to false", sender);
      } else if (!ModConfig.patches.patchHopper && !this.hasExamplesOnly(ModConfig.server.minecraft.hopperBlockBlacklist)) {
         this.inform("Necessary patch for hopper blacklist, set patches / patchHopper to true", sender);
      }

      if (!ModConfig.patches.betterCombatCriticalsFix && Loader.isModLoaded("bettercombatmod")) {
         this.inform("Improvement patch, set patches / betterCombatCriticalsFix to true", sender);
      }

      if (!ModConfig.patches.ENABLED) {
         this.inform("All patches disabled, set patches / ENABLED to true", sender);
      }

   }

   private boolean hasExamplesOnly(String[] list) {
      if (list.length == 0) {
         return true;
      } else {
         for(String s : list) {
            if (!s.startsWith("example")) {
               return false;
            }
         }

         return true;
      }
   }

   public void inform(String s, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(s));
   }
}
