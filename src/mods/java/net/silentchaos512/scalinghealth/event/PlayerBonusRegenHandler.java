package net.silentchaos512.scalinghealth.event;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.scalinghealth.config.Config;

@EventBusSubscriber(
   modid = "scalinghealth"
)
public final class PlayerBonusRegenHandler {
   private static final Map<String, Integer> TIMERS = new HashMap();

   private PlayerBonusRegenHandler() {
   }

   static int getTimerForPlayer(EntityPlayer player) {
      return player != null && TIMERS.containsKey(player.getName()) ? ((Integer)TIMERS.get(player.getName())).intValue() : -1;
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.side != Side.CLIENT && Config.Player.BonusRegen.enabled) {
         EntityPlayer player = event.player;
         int health = (int)Math.ceil((double)player.getHealth());
         if ((float)health < player.getMaxHealth()) {
            String name = player.getName();
            if (!TIMERS.containsKey(name)) {
               TIMERS.put(name, Integer.valueOf(Config.Player.BonusRegen.initialDelay));
            }

            int foodLevel = player.getFoodStats().getFoodLevel();
            boolean foodLevelOk = MathUtils.inRangeInclusive(foodLevel, Config.Player.BonusRegen.minFood, Config.Player.BonusRegen.maxFood);
            boolean healthLevelOk = MathUtils.inRangeExclusive(health, Config.Player.BonusRegen.minHealth, Config.Player.BonusRegen.maxHealth);
            if (foodLevelOk && healthLevelOk) {
               int timer = ((Integer)TIMERS.get(name)).intValue();
               --timer;
               if (timer <= 0) {
                  player.heal(getRegenTickHealAmount(player));
                  player.addExhaustion(Config.Player.BonusRegen.exhaustion);
                  timer = Config.Player.BonusRegen.delay;
               }

               TIMERS.put(name, Integer.valueOf(timer));
            }

         }
      }
   }

   private static float getRegenTickHealAmount(EntityLivingBase entity) {
      if (Config.Player.BonusRegen.scaleWithMaxHealth) {
         IAttributeInstance attr = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
         double baseHealth = attr.getBaseValue();
         double maxHealth = attr.getAttributeValue();
         return (float)(maxHealth / baseHealth);
      } else {
         return 1.0F;
      }
   }

   @SubscribeEvent
   public static void onPlayerHurt(LivingHurtEvent event) {
      EntityLivingBase entityLiving = event.getEntityLiving();
      if (!entityLiving.world.isRemote && entityLiving instanceof EntityPlayer) {
         TIMERS.put(entityLiving.getName(), Integer.valueOf(Config.Player.BonusRegen.initialDelay));
      }

   }
}
