package net.silentchaos512.wit.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class WitEntityInfoEvent extends WitHudInfoEvent {
   public final Entity entity;
   public final EntityLivingBase entityLiving;

   public WitEntityInfoEvent(EntityPlayer player, boolean advanced, Entity entity) {
      super(player, advanced);
      this.entity = entity;
      this.entityLiving = entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
   }
}
