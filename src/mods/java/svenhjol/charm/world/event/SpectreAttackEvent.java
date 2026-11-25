package svenhjol.charm.world.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;
import svenhjol.charm.world.entity.EntitySpectre;

public class SpectreAttackEvent extends Event {
   private EntitySpectre attacker;
   private EntityLivingBase attacked;

   public SpectreAttackEvent(EntitySpectre attacker, EntityLivingBase attacked) {
      this.attacker = attacker;
      this.attacked = attacked;
   }

   public EntitySpectre getAttacker() {
      return this.attacker;
   }

   public EntityLivingBase getAttacked() {
      return this.attacked;
   }

   public boolean isCancelable() {
      return true;
   }
}
