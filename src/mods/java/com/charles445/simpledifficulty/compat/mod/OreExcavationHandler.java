package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.CompatUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OreExcavationHandler {
   private Class EventExcavate;
   private Class MiningAgent;
   private Method m_EventExcavate_getAgent;
   private Field f_MiningAgent_player;
   private boolean working = true;

   public OreExcavationHandler() {
      try {
         this.EventExcavate = Class.forName("oreexcavation.events.EventExcavate");
         this.MiningAgent = Class.forName("oreexcavation.handlers.MiningAgent");
         this.m_EventExcavate_getAgent = this.EventExcavate.getDeclaredMethod("getAgent");
         this.f_MiningAgent_player = this.MiningAgent.getDeclaredField("player");
         Class<?> c_Break = Class.forName("oreexcavation.events.EventExcavate$Break");
         boolean sub = CompatUtil.subscribeEventManually(c_Break, this, this.getClass().getDeclaredMethod("onBreak", Object.class));
         if (sub) {
            SimpleDifficulty.logger.info("OreExcavationHandler Manual SubscribeEvent Success");
         } else {
            SimpleDifficulty.logger.error("OreExcavationHandler Manual SubscribeEvent Failed");
         }
      } catch (Exception var3) {
         SimpleDifficulty.logger.error("OreExcavationHandler failed to start", var3);
         this.working = false;
      }

   }

   @SubscribeEvent
   public void onBreak(Object event) {
      if (QuickConfig.isThirstEnabled() && ModConfig.server.compatibility.toggles.oreExcavation && this.working) {
         try {
            Object o = this.f_MiningAgent_player.get(this.m_EventExcavate_getAgent.invoke(event));
            if (o instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)o;
               if (player.getEntityWorld().isRemote) {
                  return;
               }

               if (!this.shouldSkipThirst(player)) {
                  SDCapabilities.getThirstData(player).addThirstExhaustion((float)ModConfig.server.thirst.thirstBreakBlock);
               }
            }
         } catch (Exception var4) {
            SimpleDifficulty.logger.error("OreExcavationHandler encountered an error, disabling self to avoid error spam.", var4);
            this.working = false;
         }

      }
   }

   private boolean shouldSkipThirst(EntityPlayer player) {
      return player.isCreative() || player.isSpectator();
   }
}
