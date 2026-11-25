package com.charles445.rltweaker.asm;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.patch.IPatch;
import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchAggressiveMotionChecker;
import com.charles445.rltweaker.asm.patch.PatchAmphithereTameDamage;
import com.charles445.rltweaker.asm.patch.PatchAnvilDupe;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatCriticalsFix;
import com.charles445.rltweaker.asm.patch.PatchBetterCombatMountFix;
import com.charles445.rltweaker.asm.patch.PatchBetterNether;
import com.charles445.rltweaker.asm.patch.PatchBountifulBaubles;
import com.charles445.rltweaker.asm.patch.PatchBroadcastSounds;
import com.charles445.rltweaker.asm.patch.PatchChunkTicks;
import com.charles445.rltweaker.asm.patch.PatchConcurrentParticles;
import com.charles445.rltweaker.asm.patch.PatchCurePotion;
import com.charles445.rltweaker.asm.patch.PatchCustomAttributeInstances;
import com.charles445.rltweaker.asm.patch.PatchDoorPathfinding;
import com.charles445.rltweaker.asm.patch.PatchEnchant;
import com.charles445.rltweaker.asm.patch.PatchEntityBlockDestroy;
import com.charles445.rltweaker.asm.patch.PatchFallingBlockPortalDupe;
import com.charles445.rltweaker.asm.patch.PatchFixOldGorgon;
import com.charles445.rltweaker.asm.patch.PatchFixOldHippocampus;
import com.charles445.rltweaker.asm.patch.PatchHopper;
import com.charles445.rltweaker.asm.patch.PatchItemFrameDupe;
import com.charles445.rltweaker.asm.patch.PatchLessCollisions;
import com.charles445.rltweaker.asm.patch.PatchLycanitesDupe;
import com.charles445.rltweaker.asm.patch.PatchMyrmexQueenHiveSpam;
import com.charles445.rltweaker.asm.patch.PatchOverlayMessage;
import com.charles445.rltweaker.asm.patch.PatchPathfindingChunkCache;
import com.charles445.rltweaker.asm.patch.PatchPotionCoreResistance;
import com.charles445.rltweaker.asm.patch.PatchPushReaction;
import com.charles445.rltweaker.asm.patch.PatchRealBench;
import com.charles445.rltweaker.asm.patch.PatchReducedSearchSize;
import com.charles445.rltweaker.asm.patch.PatchRustic;
import com.charles445.rltweaker.asm.patch.PatchWaystoneScroll;
import com.charles445.rltweaker.asm.patch.compat.PatchBrokenTransformers;
import com.charles445.rltweaker.asm.patch.compat.PatchCatServer;
import com.charles445.rltweaker.asm.patch.compat.PatchCraftBukkit;
import com.charles445.rltweaker.asm.patch.compat.PatchLootManagement;
import com.charles445.rltweaker.asm.util.ASMInfo;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ServerType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.tree.ClassNode;

public class RLTweakerASM implements IClassTransformer {
   private static boolean run = true;
   private static boolean setup = false;
   private static boolean firstrun = false;
   protected static Map<String, List<IPatch>> transformMap = new HashMap();

   public RLTweakerASM() {
      if (!setup) {
         setup = true;
         ASMInfo.cacheServerType(this.getClass().getClassLoader());
         ASMLogger.info("Server Type: " + ASMInfo.serverType.name());
         run = ASMConfig.getBoolean("general.patches.ENABLED", true);
         if (run) {
            ASMLogger.info("Patcher is enabled");
            this.createPatches();
         } else {
            ASMLogger.info("Patcher has been disabled");
         }
      }
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      byte[] result = this.doTransform(name, transformedName, basicClass);
      return result;
   }

   public byte[] doTransform(String name, String transformedName, byte[] basicClass) {
      if (!run) {
         return basicClass;
      } else {
         if (!firstrun) {
            firstrun = true;
            if (this.getClass().getClassLoader() instanceof LaunchClassLoader) {
               LaunchClassLoader loader = (LaunchClassLoader)this.getClass().getClassLoader();

               for(IClassTransformer transformer : loader.getTransformers()) {
                  if (transformer != null) {
                     Patch.loadedTransformers.add(transformer.getClass().getName());
                  }
               }
            } else {
               ASMLogger.warn("WARNING: RLTweaker transformer firstrun is not loaded by LaunchClassLoader! Some mod and server compatibility will fail!");
            }
         }

         if (!transformMap.containsKey(transformedName)) {
            return basicClass;
         } else {
            ASMLogger.info("Patch exists for " + transformedName);
            int flags = 0;
            int oldFlags = 0;
            boolean ranAnyPatch = false;
            ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

            for(IPatch patch : (List)transformMap.get(transformedName)) {
               oldFlags = flags;
               flags |= patch.getFlags();

               try {
                  patch.patch(clazzNode);
                  if (patch.isCancelled()) {
                     flags = oldFlags;
                  } else {
                     ranAnyPatch = true;
                  }
               } catch (Exception var12) {
                  ASMLogger.warn("Failed at patch " + patch.getPatchManager().getName());
                  ASMLogger.warn("Failed to write " + transformedName);
                  ASMLogger.error("Failed Patch Trace: ", var12);
                  return basicClass;
               }
            }

            if (ranAnyPatch) {
               ASMLogger.info("Writing class " + transformedName + " with flags " + flagsAsString(flags));
               if (ASMConfig.getBoolean("general.patches.export", false)) {
                  try {
                     File file = new File(".rltweaker/" + transformedName.replace('.', '/') + ".class");
                     file.getParentFile().mkdirs();
                     if (file.exists()) {
                        file.delete();
                     }

                     file.createNewFile();
                     ASMHelper.writeClassToFile(clazzNode, file);
                  } catch (IOException var11) {
                     ASMLogger.error("Failed writing class to file!", var11);
                  }
               }

               return ASMHelper.writeClassToBytes(clazzNode, flags);
            } else {
               ASMLogger.info("All patches for class " + transformedName + " were cancelled, skipping...");
               return basicClass;
            }
         }
      }
   }

   public static String flagsAsString(int flags) {
      switch(flags) {
      case 0:
         return "None";
      case 1:
         return "MAXS";
      case 2:
         return "FRAMES";
      case 3:
         return "MAXS | FRAMES";
      default:
         return "(unknown " + flags + ")";
      }
   }

   public static void addPatch(IPatch patch) {
      String target = patch.getTargetClazz();
      if (!transformMap.containsKey(target)) {
         transformMap.put(target, new ArrayList());
      }

      List<IPatch> patches = (List)transformMap.get(target);
      patches.add(patch);
   }

   private void createPatches() {
      if (ASMConfig.getBoolean("general.patches.particleThreading", true)) {
         new PatchConcurrentParticles();
      }

      if (ASMConfig.getBoolean("general.patches.lessCollisions", true)) {
         new PatchLessCollisions();
      }

      if (ASMConfig.getBoolean("general.patches.betterCombatMountFix", true)) {
         new PatchBetterCombatMountFix();
      }

      if (ASMConfig.getBoolean("general.patches.realBenchDupeBugFix", true)) {
         new PatchRealBench();
      }

      if (ASMConfig.getBoolean("general.patches.iafFixMyrmexQueenHiveSpam", true)) {
         new PatchMyrmexQueenHiveSpam();
      }

      if (ASMConfig.getBoolean("general.patches.lycanitesPetDupeFix", false)) {
         new PatchLycanitesDupe();
      }

      if (ASMConfig.getBoolean("general.patches.doorPathfindingFix", true)) {
         new PatchDoorPathfinding();
      }

      if (ASMConfig.getBoolean("general.patches.reducedSearchSize", false)) {
         new PatchReducedSearchSize();
      }

      if (ASMConfig.getBoolean("general.patches.patchBroadcastSounds", false)) {
         new PatchBroadcastSounds();
      }

      if (ASMConfig.getBoolean("general.patches.patchEnchantments", false)) {
         new PatchEnchant();
      }

      if (ASMConfig.getBoolean("general.patches.aggressiveMotionChecker", true)) {
         new PatchAggressiveMotionChecker();
      }

      if (ASMConfig.getBoolean("general.patches.patchEntityBlockDestroy", false)) {
         new PatchEntityBlockDestroy();
      }

      if (ASMConfig.getBoolean("general.patches.patchItemFrameDupe", true)) {
         new PatchItemFrameDupe();
      }

      if (ASMConfig.getBoolean("general.patches.patchPushReaction", false)) {
         new PatchPushReaction();
      }

      if (ASMConfig.getBoolean("general.patches.patchOverlayMessage", false)) {
         new PatchOverlayMessage();
      }

      if (ASMConfig.getBoolean("general.patches.patchAnvilDupe", true)) {
         new PatchAnvilDupe();
      }

      if (ASMConfig.getBoolean("general.patches.patchHopper", false)) {
         new PatchHopper();
      }

      if (ASMConfig.getBoolean("general.patches.betterCombatCriticalsFix", true)) {
         new PatchBetterCombatCriticalsFix();
      }

      if (ASMConfig.getBoolean("general.patches.fixOldHippocampus", false)) {
         new PatchFixOldHippocampus();
      }

      if (ASMConfig.getBoolean("general.patches.fixOldGorgon", false)) {
         new PatchFixOldGorgon();
      }

      if (ASMConfig.getBoolean("general.patches.fixWaystoneScrolls", true)) {
         new PatchWaystoneScroll();
      }

      if (ASMConfig.getBoolean("general.patches.pathfindingChunkCacheFix", true)) {
         new PatchPathfindingChunkCache();
      }

      if (ASMConfig.getBoolean("general.patches.serverCompatibility", true)) {
         boolean hasSponge = ASMInfo.hasSponge;
         boolean catServer = ASMInfo.serverType == ServerType.CATSERVER;
         boolean mohist = ASMInfo.serverType == ServerType.MOHIST;
         if (hasSponge || catServer || mohist) {
            new PatchLootManagement();
         }

         if (catServer || mohist) {
            new PatchBrokenTransformers();
            new PatchCraftBukkit();
         }

         if (catServer) {
            new PatchCatServer();
         }
      }

      if (ASMConfig.getBoolean("general.patches.chunkTicks", true)) {
         new PatchChunkTicks();
      }

      if (ASMConfig.getBoolean("general.patches.patchCurePotion", true)) {
         new PatchCurePotion();
      }

      if (ASMConfig.getBoolean("general.patches.patchRusticWineEffects", true)) {
         new PatchRustic();
      }

      if (ASMConfig.getBoolean("general.patches.patchModifierBooks", true)) {
         new PatchBountifulBaubles();
      }

      if (ASMConfig.getBoolean("general.patches.patchChestOfDrawers", true)) {
         new PatchBetterNether();
      }

      if (ASMConfig.getBoolean("general.patches.patchAmphithereTameDamage", false)) {
         new PatchAmphithereTameDamage();
      }

      if (ASMConfig.getBoolean("general.patches.patchPotionCoreResistance", false)) {
         new PatchPotionCoreResistance();
      }

      if (ASMConfig.getBoolean("general.patches.patchCustomAttributeInstances", false)) {
         new PatchCustomAttributeInstances();
      }

      if (ASMConfig.getBoolean("general.patches.patchFallingBlockPortalDupe", false)) {
         new PatchFallingBlockPortalDupe();
      }

   }
}
