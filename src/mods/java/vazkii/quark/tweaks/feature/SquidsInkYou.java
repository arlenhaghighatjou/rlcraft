package vazkii.quark.tweaks.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class SquidsInkYou extends Feature {
   public static int time = 80;

   public SquidsInkYou() {
   }

   public void setupConfig() {
      time = this.loadPropInt("Blindness Time", "How long should blindness last upon hitting a squid, in ticks", time);
   }

   @SubscribeEvent
   public void onHurt(LivingHurtEvent event) {
      Entity e = event.getEntity();
      if (e instanceof EntitySquid && !e.world.isRemote && event.getSource().getTrueSource() instanceof EntityPlayer) {
         for(EntityPlayer player : e.world.getEntitiesWithinAABB(EntityPlayer.class, e.getEntityBoundingBox().grow(4.0D, 4.0D, 4.0D))) {
            player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, time, 0));
         }

         WorldServer ws = (WorldServer)e.world;
         ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, e.posX + (double)(e.width / 2.0F), e.posY + (double)(e.height / 2.0F), e.posZ + (double)(e.width / 2.0F), 100, 0.0D, 0.0D, 0.0D, 0.02D, new int[0]);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
