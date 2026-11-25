package codersafterdark.reskillable.skill.magic;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TraitSafePort extends Trait {
   public TraitSafePort() {
      super(new ResourceLocation("reskillable", "safe_port"), 1, 1, new ResourceLocation("reskillable", "magic"), 6, "reskillable:magic|20", "reskillable:agility|16", "reskillable:defense|16");
   }

   public void onEnderTeleport(EnderTeleportEvent event) {
      if (!event.isCanceled()) {
         event.setAttackDamage(0.0F);
      }

   }
}
