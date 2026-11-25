package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.network.IServerMessageReceiver;
import com.charles445.rltweaker.network.MessageReskillableLockSkill;
import com.charles445.rltweaker.network.ServerMessageHandler;
import com.charles445.rltweaker.reflect.ReskillableReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ReskillableHandler {
   private ReskillableReflect reflector;
   private ReskillableHandler.ManualSubscriber manualSubscriber;

   public ReskillableHandler() {
      try {
         this.reflector = new ReskillableReflect();
         this.manualSubscriber = new ReskillableHandler.ManualSubscriber(this.reflector);
         if (ModConfig.server.reskillable.toggleableTraits) {
            ServerMessageHandler.registerMessage(MessageReskillableLockSkill.class, new ReskillableHandler.LockSkillReceiver());
            this.manualSubscriber.subscribeToggleableTraits();
         }

         MinecraftForge.EVENT_BUS.register(this);
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup ReskillableHandler!", var2);
         ErrorUtil.logSilent("Reskillable Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   public void registerTransmutations() {
      if (JsonConfig.reskillableTransmutation != null) {
         for(Entry<String, List<JsonDoubleBlockState>> entry : JsonConfig.reskillableTransmutation.entrySet()) {
            Item activator = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation((String)entry.getKey()));
            if (activator == null) {
               RLTweaker.logger.warn("Skipping unregistered item in registerTransmutations: " + (String)entry.getKey());
            } else {
               for(JsonDoubleBlockState jdbs : (List)entry.getValue()) {
                  try {
                     this.reflector.addEntryToReagent(activator, jdbs.input.getAsBlockState(), jdbs.output.getAsBlockState());
                  } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
                     RLTweaker.logger.error("Invocation error in registerTransmutations", var7);
                     ErrorUtil.logSilent("Reskillable registerTransmutations Invoke Failure");
                  }
               }
            }
         }
      }

   }

   public class LockSkillReceiver implements IServerMessageReceiver {
      public LockSkillReceiver() {
      }

      public void receiveMessage(IMessage msgIn, EntityPlayer player) {
         try {
            if (msgIn instanceof MessageReskillableLockSkill) {
               MessageReskillableLockSkill message = (MessageReskillableLockSkill)msgIn;
               IForgeRegistry<?> SKILLS = ReskillableHandler.this.reflector.getSkillsRegistry();
               IForgeRegistry<?> UNLOCKABLES = ReskillableHandler.this.reflector.getUnlockablesRegistry();
               if (!SKILLS.containsKey(message.skill)) {
                  return;
               }

               if (!UNLOCKABLES.containsKey(message.unlockable)) {
                  return;
               }

               Object skill = SKILLS.getValue(message.skill);
               Object unlockable = UNLOCKABLES.getValue(message.unlockable);
               Object playerData = ReskillableHandler.this.reflector.getPlayerData(player);
               Object skillInfo = ReskillableHandler.this.reflector.getSkillInfo(playerData, skill);
               boolean unlocked = ReskillableHandler.this.reflector.isUnlocked(skillInfo, unlockable);
               if (unlocked) {
                  if (ReskillableHandler.this.reflector.postLockUnlockableEventPre(player, unlockable)) {
                     return;
                  }

                  ReskillableHandler.this.reflector.lockPlayerSkill(skillInfo, unlockable, player);
                  ReskillableHandler.this.reflector.saveAndSyncPlayerData(playerData);
                  ReskillableHandler.this.reflector.postLockUnlockableEventPost(player, unlockable);
               } else {
                  ErrorUtil.logSilent("Reskillable LockSkillReceiver Unlock Asked Lock");
               }
            }
         } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException var11) {
            var11.printStackTrace();
            ErrorUtil.logSilent("Reskillable LockSkillReceiver Invoke");
         }

      }
   }

   public static class ManualSubscriber {
      private ReskillableReflect reflector;
      private boolean canReflectOldElenai = false;
      private Class c_oldElenai_WalljumpProvider;
      private Field f_oldElenai_WalljumpProvider_WALLJUMP_CAP;
      private Class c_oldElenai_IWalljump;
      private Method m_oldElenai_IWalljump_set;
      private Class c_oldElenai_LedgegrabProvider;
      private Field f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP;
      private Class c_oldElenai_ILedgegrab;
      private Method m_oldElenai_ILedgegrab_set;
      private Class c_oldElenai_DodgeProvider;
      private Field f_oldElenai_DodgeProvider_DODGE_CAP;
      private Class c_oldElenai_IDodge;
      private Method m_oldElenai_IDodge_set;

      public ManualSubscriber(ReskillableReflect reflector) {
         this.reflector = reflector;

         try {
            this.c_oldElenai_WalljumpProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.walljump.WalljumpProvider");
            this.f_oldElenai_WalljumpProvider_WALLJUMP_CAP = ReflectUtil.findField(this.c_oldElenai_WalljumpProvider, "WALLJUMP_CAP");
            this.c_oldElenai_IWalljump = Class.forName("com.elenai.elenaidodge.modpacks.capability.walljump.IWalljump");
            this.m_oldElenai_IWalljump_set = ReflectUtil.findMethod(this.c_oldElenai_IWalljump, "set");
            this.c_oldElenai_LedgegrabProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.ledgegrab.LedgegrabProvider");
            this.f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP = ReflectUtil.findField(this.c_oldElenai_LedgegrabProvider, "LEDGEGRAB_CAP");
            this.c_oldElenai_ILedgegrab = Class.forName("com.elenai.elenaidodge.modpacks.capability.ledgegrab.ILedgegrab");
            this.m_oldElenai_ILedgegrab_set = ReflectUtil.findMethod(this.c_oldElenai_ILedgegrab, "set");
            this.c_oldElenai_DodgeProvider = Class.forName("com.elenai.elenaidodge.modpacks.capability.trait.TraitProvider");
            this.f_oldElenai_DodgeProvider_DODGE_CAP = ReflectUtil.findField(this.c_oldElenai_DodgeProvider, "TRAIT_CAP");
            this.c_oldElenai_IDodge = Class.forName("com.elenai.elenaidodge.modpacks.capability.trait.ITrait");
            this.m_oldElenai_IDodge_set = ReflectUtil.findMethod(this.c_oldElenai_IDodge, "set");
            this.canReflectOldElenai = true;
         } catch (Exception var3) {
            ;
         }

      }

      private void subscribeToggleableTraits() throws Exception {
         CompatUtil.subscribeEventManually(this.reflector.c_LockUnlockableEvent$Post, this, ReflectUtil.findMethod(this.getClass(), "onToggleableTraitLockedPost"));
      }

      @SubscribeEvent
      public void onToggleableTraitLockedPost(Object uncastedEvent) {
         if (uncastedEvent instanceof PlayerEvent) {
            PlayerEvent event = (PlayerEvent)uncastedEvent;

            try {
               String lockedName = this.reflector.getUnlockableName(this.reflector.getUnlockableFromLockedEvent(event));
               if (lockedName != null && event.getEntityPlayer() != null) {
                  this.synchronizeUnlockable(event.getEntityPlayer(), lockedName);
               }
            } catch (Exception var4) {
               ErrorUtil.logSilent("Reskillable Toggleable Trait Post Handler");
            }
         }

      }

      public void synchronizeUnlockable(EntityPlayer player, String name) {
         byte var4 = -1;
         switch(name.hashCode()) {
         case 67492743:
            if (name.equals("elenaidodge.walljumptrait")) {
               var4 = 2;
            }
            break;
         case 181933766:
            if (name.equals("elenaidodge.dodgetrait")) {
               var4 = 0;
            }
            break;
         case 2095474729:
            if (name.equals("elenaidodge.wallgrabtrait")) {
               var4 = 1;
            }
         }

         switch(var4) {
         case 0:
            this.clearOldElenaiDodgeTrait(player);
            break;
         case 1:
            this.clearOldElenaiLedgeGrabTrait(player);
            break;
         case 2:
            this.clearOldElenaiWallJumpTrait(player);
         }

      }

      private void clearOldElenaiDodgeTrait(EntityPlayer player) {
         if (this.canReflectOldElenai) {
            try {
               Object provided = player.getCapability((Capability)this.f_oldElenai_DodgeProvider_DODGE_CAP.get((Object)null), (EnumFacing)null);
               this.m_oldElenai_IDodge_set.invoke(provided, false);
            } catch (Exception var3) {
               ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Dodge");
            }

         }
      }

      private void clearOldElenaiLedgeGrabTrait(EntityPlayer player) {
         if (this.canReflectOldElenai) {
            try {
               Object provided = player.getCapability((Capability)this.f_oldElenai_LedgegrabProvider_LEDGEGRAB_CAP.get((Object)null), (EnumFacing)null);
               this.m_oldElenai_ILedgegrab_set.invoke(provided, false);
            } catch (Exception var3) {
               ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Wall Grab");
            }

         }
      }

      private void clearOldElenaiWallJumpTrait(EntityPlayer player) {
         if (this.canReflectOldElenai) {
            try {
               Object provided = player.getCapability((Capability)this.f_oldElenai_WalljumpProvider_WALLJUMP_CAP.get((Object)null), (EnumFacing)null);
               this.m_oldElenai_IWalljump_set.invoke(provided, false);
            } catch (Exception var3) {
               ErrorUtil.logSilent("Reskillable Toggleable Trait Old Elenai Dodge Wall Jump");
            }

         }
      }
   }
}
