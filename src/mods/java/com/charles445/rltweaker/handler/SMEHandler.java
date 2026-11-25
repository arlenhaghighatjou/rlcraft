package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.SMEReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import com.charles445.rltweaker.util.VersionDelimiter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SMEHandler {
   public static long protectCounter = 0L;
   private SMEReflect reflector;

   public SMEHandler() {
      try {
         Class c_somanyenchantments = Class.forName("com.Shultrea.Rin.Main_Sector.somanyenchantments");
         Field f_version = ReflectUtil.findField(c_somanyenchantments, "VERSION");
         VersionDelimiter vd = VersionDelimiter.parse((String)f_version.get((Object)null));
         if (vd.isSameOrNewerVersion(0, 5)) {
            RLTweaker.logger.info("Skipping SMEHandler setup, SME version is 0.5.0 or higher");
            return;
         }

         this.reflector = new SMEReflect();
         this.tryRegister(SMEHandler.AdvancedMending.class, "advancedmending", this.reflector.c_Enchantmentadvancedmending, this.reflector.m_Enchantmentadvancedmending_handler, ModConfig.server.somanyenchantments.manageAdvancedMending);
         this.tryRegister(SMEHandler.ArcSlash.class, "swiper", this.reflector.c_EnchantmentSwiper, this.reflector.m_EnchantmentSwiper_handler, ModConfig.server.somanyenchantments.manageArcSlash);
         this.tryRegister(SMEHandler.CurseOfPossession.class, "CurseofPossession", this.reflector.c_EnchantmentCurseofPossession, this.reflector.m_EnchantmentCurseofPossession_onExist, ModConfig.server.somanyenchantments.manageCurseOfPossession);
         this.tryRegister(SMEHandler.EmpoweredDefence.class, "empowereddefence", this.reflector.c_EnchantmentEmpoweredDefence, this.reflector.m_EnchantmentEmpoweredDefence_handler, ModConfig.server.somanyenchantments.manageEmpoweredDefence);
         this.tryRegister(SMEHandler.Evasion.class, "evasion", this.reflector.c_EnchantmentEvasion, this.reflector.m_EnchantmentEvasion_handler, ModConfig.server.somanyenchantments.manageEvasion);
         this.tryRegister(SMEHandler.Freezing.class, "freezing", this.reflector.c_EnchantmentFreezing, this.reflector.m_EnchantmentFreezing_handler, ModConfig.server.somanyenchantments.manageFreezing);
         this.tryRegister(SMEHandler.Parry.class, "parry", this.reflector.c_EnchantmentParry, this.reflector.m_EnchantmentParry_handler, ModConfig.server.somanyenchantments.manageParry);
         this.tryRegister(SMEHandler.Unreasonable.class, "frenzy", this.reflector.c_EnchantmentFrenzy, this.reflector.m_EnchantmentFrenzy_handler, ModConfig.server.somanyenchantments.manageUnreasonable);
         this.tryRegister(SMEHandler.UpgradedPotentials.class, "upgrade", this.reflector.c_EnchantmentUpgradedPotentials, this.reflector.m_EnchantmentUpgradedPotentials_handler, ModConfig.server.somanyenchantments.manageUpgradedPotentials);
      } catch (Exception var4) {
         RLTweaker.logger.error("Failed to setup SMEHandler!", var4);
         ErrorUtil.logSilent("SME Critical Setup Failure");
         if (var4 instanceof CriticalException) {
            throw new RuntimeException(var4);
         }
      }

   }

   private void tryRegister(Class toRegisterClazz, String enchName, Class reflectorClazz, Method reflectorMethod, boolean config) throws Exception {
      if (config) {
         Object handler = CompatUtil.findAndRemoveHandlerFromEventBus(reflectorClazz);
         if (handler != null) {
            RLTweaker.logger.info("Registering " + toRegisterClazz.getName() + " to the event bus");
            MinecraftForge.EVENT_BUS.register(toRegisterClazz.getConstructor(this.getClass(), Object.class, String.class, Method.class).newInstance(this, handler, enchName, reflectorMethod));
         }
      }

   }

   private Enchantment getEnchantment(String name) {
      ResourceLocation loc = new ResourceLocation("somanyenchantments", name);
      Enchantment enchantment = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(loc);
      if (enchantment == null) {
         RLTweaker.logger.error("Failed to find enchantment " + loc);
      }

      return enchantment;
   }

   public class AdvancedMending extends SMEHandler.Wrapper {
      public AdvancedMending(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent
      public void advancedMendingEnchant(PlayerPickupXpEvent event) {
         EntityPlayer player = event.getEntityPlayer();
         EntityXPOrb orb = event.getOrb();
         if (ModConfig.server.somanyenchantments.advancedMendingDoubleXP && orb.xpValue > 0) {
            player.addExperience(orb.xpValue);
         }

         ItemStack itemstack = EnchantmentHelper.getEnchantedItem(this.enchantment, player);
         if (!itemstack.isEmpty() && itemstack.isItemDamaged()) {
            int value = Math.min(orb.xpValue * 3, itemstack.getItemDamage());
            itemstack.setItemDamage(itemstack.getItemDamage() - value);
            orb.xpValue -= value / 2;
            if (orb.xpValue < 0) {
               orb.xpValue = 0;
            }
         }

      }
   }

   public class ArcSlash extends SMEHandler.Wrapper {
      public ArcSlash(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST,
         receiveCanceled = true
      )
      public void arcSlashEnchant(LivingDamageEvent event) {
         try {
            Entity attacker = event.getSource().getTrueSource();
            if (!(attacker instanceof EntityLivingBase)) {
               return;
            }

            ItemStack weapon = ((EntityLivingBase)attacker).getHeldItemMainhand();
            if (weapon.isEmpty()) {
               return;
            }

            if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, weapon) <= 0) {
               return;
            }

            this.invokeOriginal(event);
            this.protectMotion(event.getEntityLiving(), attacker);
         } catch (Exception var4) {
            RLTweaker.logger.error("Error in ArcSlash Invoke", var4);
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class CurseOfPossession extends SMEHandler.Wrapper {
      private final Method onExist;
      private final Method toss;
      private final Map<Integer, Integer> dimensionMap;

      public CurseOfPossession(Object handler, String ench, Method original) {
         super(handler, ench, original);
         this.onExist = SMEHandler.this.reflector.m_EnchantmentCurseofPossession_onExist;
         this.toss = SMEHandler.this.reflector.m_EnchantmentCurseofPossession_toss;
         this.dimensionMap = new HashMap();
      }

      @SubscribeEvent
      public void onExist(WorldTickEvent event) {
         if (event.phase != Phase.START) {
            int dimension = event.world.provider.getDimension();
            Integer dimensionCounterObj = (Integer)this.dimensionMap.get(Integer.valueOf(dimension));
            int dimCount = dimensionCounterObj == null ? 0 : dimensionCounterObj.intValue();
            ++dimCount;
            if (dimCount >= ModConfig.server.somanyenchantments.curseOfPossessionDelay) {
               this.dimensionMap.put(Integer.valueOf(dimension), Integer.valueOf(0));

               try {
                  this.onExist.invoke(this.handler, event);
               } catch (Exception var6) {
                  RLTweaker.logger.error("Error in CurseOfPossession onExist Invoke", var6);
                  ErrorUtil.logEnchantmentHandlerError(this.enchantment);
               }
            } else {
               this.dimensionMap.put(Integer.valueOf(dimension), Integer.valueOf(dimCount));
            }

         }
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public void toss(ItemTossEvent event) {
         try {
            this.toss.invoke(this.handler, event);
         } catch (Exception var3) {
            RLTweaker.logger.error("Error in CurseOfPossession toss Invoke", var3);
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class EmpoweredDefence extends SMEHandler.Wrapper {
      public EmpoweredDefence(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public void empoweredDefenceEnchant(LivingAttackEvent event) {
         try {
            EntityLivingBase victim = event.getEntityLiving();
            ItemStack shield = victim.getHeldItemOffhand();
            if (shield.isEmpty()) {
               shield = victim.getHeldItemMainhand();
            }

            if (shield.isEmpty()) {
               return;
            }

            if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, shield) <= 0) {
               return;
            }

            Entity attacker = event.getSource().getTrueSource();
            if (attacker == null) {
               return;
            }

            this.invokeOriginal(event);
            this.protectMotion(victim, attacker);
         } catch (Exception var5) {
            RLTweaker.logger.error("Error in EmpoweredDefence Invoke", var5);
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class Evasion extends SMEHandler.Wrapper {
      public Evasion(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public void evasionEnchant(LivingAttackEvent event) {
         try {
            EntityLivingBase victim = event.getEntityLiving();
            if (EnchantmentHelper.getMaxEnchantmentLevel(this.enchantment, victim) <= 0) {
               return;
            }

            Entity attacker = event.getSource().getTrueSource();
            if (attacker == null) {
               return;
            }

            this.invokeOriginal(event);
            this.protectMotion(victim, attacker);
         } catch (Exception var4) {
            RLTweaker.logger.error("Error in Evasion Invoke", var4);
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class Freezing extends SMEHandler.Wrapper {
      public Freezing(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public void freezingEnchant(LivingDamageEvent event) {
         try {
            if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
               return;
            }

            EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon.isEmpty()) {
               return;
            }

            if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, weapon) <= 0) {
               return;
            }

            this.invokeOriginal(event);
            EntityLivingBase victim = event.getEntityLiving();
            this.protectMotion(victim, attacker);
         } catch (Exception var5) {
            RLTweaker.logger.error("Error in Freezing Invoke", var5);
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class Parry extends SMEHandler.Wrapper {
      public Parry(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST,
         receiveCanceled = false
      )
      public void parryEnchant(LivingAttackEvent event) {
         try {
            EntityLivingBase victim = event.getEntityLiving();
            ItemStack weapon = victim.getHeldItemMainhand();
            if (weapon.isEmpty()) {
               return;
            }

            if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, weapon) <= 0) {
               return;
            }

            Entity attacker = event.getSource().getTrueSource();
            if (attacker == null) {
               return;
            }

            this.invokeOriginal(event);
            this.protectMotion(victim, attacker);
         } catch (Exception var5) {
            RLTweaker.logger.error("Error in Parry Invoke");
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class Unreasonable extends SMEHandler.Wrapper {
      public Unreasonable(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public void unreasonableEnchant(LivingAttackEvent event) {
         try {
            if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
               return;
            }

            EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon.isEmpty()) {
               return;
            }

            if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, weapon) <= 0) {
               return;
            }

            this.invokeOriginal(event);
            if (event.isCanceled()) {
               event.setCanceled(false);
               EntityLivingBase victim = event.getEntityLiving();
               if (victim.getMaxHealth() > victim.getHealth()) {
                  return;
               }

               EntityLivingBase revenge = victim.getRevengeTarget();
               if (revenge == null) {
                  return;
               }

               World world = victim.getEntityWorld();
               if (world instanceof WorldServer) {
                  ((WorldServer)world).addScheduledTask(() -> {
                     try {
                        if (victim != null && attacker != null && revenge != null && !victim.isDead && victim instanceof EntityLiving && !victim.getUniqueID().equals(revenge.getUniqueID())) {
                           EntityLiving victimLiving = (EntityLiving)victim;
                           victimLiving.setRevengeTarget(revenge);
                           victimLiving.setAttackTarget(revenge);
                        }
                     } catch (Exception var4) {
                        RLTweaker.logger.error("Error in scheduled Unreasonable task", var4);
                        ErrorUtil.logSilent("Unreasonable Scheduled");
                     }

                  });
               }
            }
         } catch (Exception var7) {
            RLTweaker.logger.error("Error in Unreasonable Invoke");
            ErrorUtil.logEnchantmentHandlerError(this.enchantment);
         }

      }
   }

   public class UpgradedPotentials extends SMEHandler.Wrapper {
      public UpgradedPotentials(Object handler, String ench, Method original) {
         super(handler, ench, original);
      }

      @SubscribeEvent
      public void upgradedPotentialsEnchant(AnvilUpdateEvent event) {
         ItemStack left = event.getLeft();
         ItemStack right = event.getRight();
         if (!left.isEmpty() && !right.isEmpty()) {
            if (right.getItem() == Items.ENCHANTED_BOOK) {
               Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(right);
               if (enchantments.containsKey(this.enchantment) && ((Integer)enchantments.get(this.enchantment)).intValue() >= 1) {
                  if (left.isStackable()) {
                     event.setOutput(ItemStack.EMPTY);
                     return;
                  }

                  if (EnchantmentHelper.getEnchantments(left).size() == 0) {
                     event.setOutput(ItemStack.EMPTY);
                     return;
                  }

                  if (EnchantmentHelper.getEnchantmentLevel(this.enchantment, left) >= 1) {
                     event.setOutput(ItemStack.EMPTY);
                     return;
                  }

                  int cost = left.getRepairCost();
                  cost = Math.max(0, cost / 4 - 20);
                  ItemStack output = left.copy();
                  output.setRepairCost(cost);
                  output.addEnchantment(this.enchantment, 1);
                  event.setOutput(output);
                  event.setCost(10);
               }
            }

         }
      }
   }

   private abstract class Wrapper {
      protected final Object handler;
      protected final Enchantment enchantment;
      protected final Method original;

      public Wrapper(Object handler, String ench, Method original) {
         this.handler = handler;
         this.enchantment = SMEHandler.this.getEnchantment(ench);
         this.original = original;
      }

      protected void invokeOriginal(Event event) throws Exception {
         this.original.invoke(this.handler, event);
      }

      protected void protectMotion(Entity a, Entity b) {
         if (!Double.isFinite(a.motionX) || !Double.isFinite(a.motionY) || !Double.isFinite(a.motionZ)) {
            a.motionX = 0.0D;
            a.motionY = 0.0D;
            a.motionZ = 0.0D;
            a.velocityChanged = true;
            ++SMEHandler.protectCounter;
            ErrorUtil.logEnchantmentError(this.enchantment);
         }

         if (!Double.isFinite(b.motionX) || !Double.isFinite(b.motionY) || !Double.isFinite(b.motionZ)) {
            b.motionX = 0.0D;
            b.motionY = 0.0D;
            b.motionZ = 0.0D;
            b.velocityChanged = true;
            ++SMEHandler.protectCounter;
            ErrorUtil.logEnchantmentError(this.enchantment);
         }

      }
   }
}
