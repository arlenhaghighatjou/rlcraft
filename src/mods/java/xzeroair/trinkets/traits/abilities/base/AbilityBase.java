package xzeroair.trinkets.traits.abilities.base;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.util.Reference;

public abstract class AbilityBase {
   private AbilityHandler handler;
   private EntityLivingBase entity;
   private boolean removeAbility = false;
   protected EaglercraftRandom random = Reference.random;
   protected int value = -1;
   protected boolean enabled = false;

   public AbilityBase() {
   }

   public AbilityHandler getHandler() {
      return this.handler;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public AbilityBase setHandler(AbilityHandler handler) {
      this.handler = handler;
      return this;
   }

   public AbilityBase setEntity(EntityLivingBase entity) {
      this.entity = entity;
      return this;
   }

   public boolean isCreativePlayer(Entity entity) {
      boolean flag = entity instanceof EntityPlayer && (((EntityPlayer)entity).isCreative() || ((EntityPlayer)entity).isSpectator());
      return flag;
   }
}
