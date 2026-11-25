package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.capability.ITweakerCapability;
import com.charles445.rltweaker.capability.RLCapabilities;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.TANReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class TANHandler {
   private TANReflect reflector;

   public TANHandler() {
      try {
         this.reflector = new TANReflect();
         if (ModConfig.server.toughasnails.fixExtraAttackBug) {
            if (Loader.isModLoaded("iseedragons")) {
               RLTweaker.logger.info("Skipping Tough as Nails Fix Extra Attack Bug, as ISeeDragons is in the pack.");
               RLTweaker.logger.info("Configure this option in ISeeDragons instead.");
            } else {
               CompatUtil.wrapSpecificHandler("TANAttackEntity", (x$0) -> {
                  new TANHandler.TANAttackEntity(x$0);
               }, "toughasnails.handler.thirst.ThirstStatHandler", "onAttackEntity");
            }
         }

         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup TANHandler!", var2);
         ErrorUtil.logSilent("TAN Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onDismount(EntityMountEvent event) {
      if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && ModConfig.server.toughasnails.fixDismountThirstDrainBug) {
         ((EntityPlayer)event.getEntityMounting()).setJumping(false);
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      World world = player.getEntityWorld();
      if (!world.isRemote) {
         try {
            Object data = this.reflector.getThirstData(player);
            float currentExhaustion = this.reflector.readExhaustionFromData(data);
            ITweakerCapability tweakData = RLCapabilities.getTweakerData(player);
            if (ModConfig.server.toughasnails.fixTeleportThirst) {
               float storedExhaustion = tweakData.getTANExhaustion();
               float exhaustionChange = currentExhaustion - storedExhaustion;
               if (event.phase == Phase.START) {
                  if (exhaustionChange > (float)ModConfig.server.toughasnails.teleportThirstThreshold) {
                     this.reflector.setExhaustionInData(data, storedExhaustion);
                  }
               } else {
                  tweakData.setTANExhaustion(currentExhaustion);
               }
            }

            if (event.phase == Phase.START && ModConfig.server.toughasnails.sendExtraThirstPackets) {
               int tanPackets = tweakData.getThirstPacketTicks();
               if (tanPackets >= ModConfig.server.toughasnails.extraThirstPacketFrequency) {
                  tweakData.setThirstPacketTicks(0);
                  SimpleNetworkWrapper packetHandler = this.reflector.getPacketHandlerInstance();
                  if (packetHandler != null && player instanceof EntityPlayerMP) {
                     packetHandler.sendTo(this.reflector.createUpdateMessageWithStat(data), (EntityPlayerMP)player);
                  }
               } else {
                  tweakData.incrementThirstPacketTicks();
               }
            }
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var9) {
            RLTweaker.logger.error("Error in onPlayerTick TANHandler Invoke", var9);
            ErrorUtil.logSilent("TANHandler onPlayerTick");
            throw new RuntimeException(var9);
         }
      }

   }

   public class TANAttackEntity {
      private IEventListener handler;

      public TANAttackEntity(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onAttackEntity(AttackEntityEvent event) {
         EntityPlayer player = event.getEntityPlayer();
         Entity monster = event.getTarget();
         if (monster != null && player != null) {
            World world = monster.getEntityWorld();
            if (world != null && !world.isRemote) {
               if (monster.canBeAttackedWithItem() && !player.isCreative() && !monster.hitByEntity(player)) {
                  try {
                     Object thirstHandler = TANHandler.this.reflector.getThirstData(player);
                     if (TANHandler.this.reflector.isThirstHandlerInstance(thirstHandler)) {
                        TANHandler.this.reflector.addExhaustion(thirstHandler, 0.3F);
                     }
                  } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var6) {
                     ErrorUtil.logSilent("TANHandler Extra Attack Invocation");
                  }
               }

            }
         }
      }
   }
}
