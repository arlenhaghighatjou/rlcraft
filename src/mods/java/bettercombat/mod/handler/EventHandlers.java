package bettercombat.mod.handler;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.combat.IOffHandAttack;
import bettercombat.mod.combat.ISecondHurtTimer;
import bettercombat.mod.util.BetterCombatMod;
import bettercombat.mod.util.ConfigurationHandler;
import bettercombat.mod.util.Helpers;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlers {
   public static final EventHandlers INSTANCE = new EventHandlers();
   @CapabilityInject(IOffHandAttack.class)
   public static final Capability<IOffHandAttack> OFFHAND_CAP = (Capability)Helpers.getNull();
   @CapabilityInject(ISecondHurtTimer.class)
   public static final Capability<ISecondHurtTimer> SECONDHURTTIMER_CAP = (Capability)Helpers.getNull();
   @CapabilityInject(CapabilityOffhandCooldown.class)
   public static final Capability<CapabilityOffhandCooldown> TUTO_CAP = (Capability)Helpers.getNull();

   private EventHandlers() {
   }

   @SubscribeEvent
   public void onAttack(AttackEntityEvent event) {
      if (event.getTarget() != null) {
         if (event.getTarget().hurtResistantTime <= 10 && ConfigurationHandler.moreSweep) {
            BetterCombatMod.proxy.spawnSweep((EntityPlayer)event.getEntityLiving());
         }

      }
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onLivingUpdate(LivingUpdateEvent event) {
      ISecondHurtTimer sht = (ISecondHurtTimer)event.getEntityLiving().getCapability(SECONDHURTTIMER_CAP, (EnumFacing)null);
      if (sht != null && sht.getHurtTimerBCM() > 0) {
         sht.tick();
      }

      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         IOffHandAttack oha = (IOffHandAttack)event.getEntityLiving().getCapability(OFFHAND_CAP, (EnumFacing)null);
         CapabilityOffhandCooldown cof = (CapabilityOffhandCooldown)player.getCapability(TUTO_CAP, (EnumFacing)null);
         Helpers.execNullable(oha, IOffHandAttack::tick);
         if (cof != null && (cof.getOffhandCooldown() > 0 || cof.getOffhandBeginningCooldown() > 0)) {
            cof.tick();
         }
      }

   }

   @SubscribeEvent
   public void onEntityConstruct(AttachCapabilitiesEvent event) {
      if (event.getGenericType() == Entity.class) {
         if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation("bettercombatmod", "TUTO_CAP"), new CapabilityOffhandCooldown((EntityPlayer)event.getObject()));
         }

         event.addCapability(new ResourceLocation("bettercombatmod", "IOffHandAttack"), new ICapabilitySerializable() {
            IOffHandAttack inst = (IOffHandAttack)EventHandlers.OFFHAND_CAP.getDefaultInstance();

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == EventHandlers.OFFHAND_CAP;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == EventHandlers.OFFHAND_CAP ? EventHandlers.OFFHAND_CAP.cast(this.inst) : null);
            }

            public NBTPrimitive serializeNBT() {
               return (NBTPrimitive)EventHandlers.OFFHAND_CAP.getStorage().writeNBT(EventHandlers.OFFHAND_CAP, this.inst, (EnumFacing)null);
            }

            public void deserializeNBT(NBTBase nbt) {
               EventHandlers.OFFHAND_CAP.getStorage().readNBT(EventHandlers.OFFHAND_CAP, this.inst, (EnumFacing)null, nbt);
            }
         });
         event.addCapability(new ResourceLocation("bettercombatmod", "ISecondHurtTimer"), new ICapabilitySerializable() {
            ISecondHurtTimer inst = (ISecondHurtTimer)EventHandlers.SECONDHURTTIMER_CAP.getDefaultInstance();

            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
               return capability == EventHandlers.SECONDHURTTIMER_CAP;
            }

            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
               return (T)(capability == EventHandlers.SECONDHURTTIMER_CAP ? EventHandlers.SECONDHURTTIMER_CAP.cast(this.inst) : null);
            }

            public NBTPrimitive serializeNBT() {
               return (NBTPrimitive)EventHandlers.SECONDHURTTIMER_CAP.getStorage().writeNBT(EventHandlers.SECONDHURTTIMER_CAP, this.inst, (EnumFacing)null);
            }

            public void deserializeNBT(NBTBase nbt) {
               EventHandlers.SECONDHURTTIMER_CAP.getStorage().readNBT(EventHandlers.SECONDHURTTIMER_CAP, this.inst, (EnumFacing)null, nbt);
            }
         });
      }
   }
}
