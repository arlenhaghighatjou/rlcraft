package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import vazkii.quark.base.module.Feature;

public class JumpBoostStepAssist extends Feature {
   public static final List<String> playersWithStepup = new ArrayList();
   public static int minimumLevel;
   public static boolean canToggleWithSneak;

   public JumpBoostStepAssist() {
   }

   public void setupConfig() {
      minimumLevel = this.loadPropInt("Minimum Jump Boost level", "", 2);
      canToggleWithSneak = this.loadPropBool("Can toggle with sneaking", "", true);
      --minimumLevel;
   }

   @SubscribeEvent
   public void updatePlayerStepStatus(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         String s = playerStr(player);
         if (playersWithStepup.contains(s)) {
            if (this.shouldPlayerHaveStepup(player)) {
               if (canToggleWithSneak && player.isSneaking()) {
                  player.stepHeight = 0.50001F;
               } else {
                  player.stepHeight = 1.25F;
               }
            } else {
               player.stepHeight = 0.5F;
               playersWithStepup.remove(s);
            }
         } else if (this.shouldPlayerHaveStepup(player)) {
            playersWithStepup.add(s);
            player.stepHeight = 1.25F;
         }
      }

   }

   private boolean shouldPlayerHaveStepup(EntityPlayer player) {
      PotionEffect jumpBoost = player.getActivePotionEffect(MobEffects.JUMP_BOOST);
      return jumpBoost != null && jumpBoost.getAmplifier() >= minimumLevel;
   }

   @SubscribeEvent
   public void playerLoggedOut(PlayerLoggedOutEvent event) {
      String username = event.player.getGameProfile().getName();
      playersWithStepup.remove(username + ":false");
      playersWithStepup.remove(username + ":true");
   }

   public static String playerStr(EntityPlayer player) {
      return player.getGameProfile().getName() + ":" + player.getEntityWorld().isRemote;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Jump Boost Step Assist";
   }
}
