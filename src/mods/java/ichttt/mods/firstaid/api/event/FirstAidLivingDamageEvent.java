package ichttt.mods.firstaid.api.event;

import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class FirstAidLivingDamageEvent extends PlayerEvent {
   private final AbstractPlayerDamageModel afterDamageDone;
   private final AbstractPlayerDamageModel beforeDamageDone;
   private final DamageSource source;
   private final float undistributedDamage;

   public FirstAidLivingDamageEvent(EntityPlayer entity, AbstractPlayerDamageModel afterDamageDone, AbstractPlayerDamageModel beforeDamageDone, DamageSource source, float undistributedDamage) {
      super(entity);
      this.afterDamageDone = afterDamageDone;
      this.beforeDamageDone = beforeDamageDone;
      this.source = source;
      this.undistributedDamage = undistributedDamage;
   }

   public AbstractPlayerDamageModel getAfterDamage() {
      return this.afterDamageDone;
   }

   public AbstractPlayerDamageModel getBeforeDamage() {
      return this.beforeDamageDone;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public float getUndistributedDamage() {
      return this.undistributedDamage;
   }
}
