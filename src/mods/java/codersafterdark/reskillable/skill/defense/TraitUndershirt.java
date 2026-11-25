package codersafterdark.reskillable.skill.defense;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TraitUndershirt extends Trait {
   private static final String TAG_COOLDOWN = "skillable:UndershirtCD";

   public TraitUndershirt() {
      super(new ResourceLocation("reskillable", "undershirt"), 1, 2, new ResourceLocation("reskillable", "defense"), 6, "reskillable:defense|12", "reskillable:agility|4");
   }

   public void onHurt(LivingHurtEvent event) {
      if (!event.isCanceled()) {
         EntityLivingBase e = event.getEntityLiving();
         if (e.getEntityData().getInteger("skillable:UndershirtCD") == 0 && e.getHealth() >= 6.0F && event.getAmount() >= e.getHealth() && !event.getSource().isUnblockable()) {
            event.setAmount(e.getHealth() - 1.0F);
            e.getEntityData().setInteger("skillable:UndershirtCD", 200);
         }

      }
   }

   public void onPlayerTick(PlayerTickEvent event) {
      int cd = event.player.getEntityData().getInteger("skillable:UndershirtCD");
      if (cd > 0) {
         event.player.getEntityData().setInteger("skillable:UndershirtCD", cd - 1);
      }

   }
}
