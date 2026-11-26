package codersafterdark.reskillable.skill.attack;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.lib.LibObfuscation;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class TraitNeutralissse extends Trait {
   private static final String TAG_DEFUSED = "skillable:defuse";

   public TraitNeutralissse() {
      super(new ResourceLocation("reskillable", "neutralissse"), 1, 2, new ResourceLocation("reskillable", "attack"), 10, "reskillable:attack|24", "reskillable:agility|8");
      this.register(MinecraftForge.EVENT_BUS);
   }

   public void onAttackMob(LivingHurtEvent event) {
      if (!event.isCanceled()) {
         if (event.getEntity() instanceof EntityCreeper) {
            EntityCreeper creeper = (EntityCreeper)event.getEntity();
            int time = creeper.timeSinceIgnited;
            if (time < 5) {
               creeper.getEntityData().setInteger("skillable:defuse", 40);
            }
         }

      }
   }

   public void register(EventBus bus) {
      bus.register(LivingUpdateEvent.class, this::entityTick);
   }

   public void entityTick(LivingUpdateEvent event) {
      if (!event.isCanceled() && event.getEntity() instanceof EntityCreeper) {
         EntityCreeper creeper = (EntityCreeper)event.getEntity();
         int defuseTime = creeper.getEntityData().getInteger("skillable:defuse");
         if (defuseTime > 0) {
            creeper.getEntityData().setInteger("skillable:defuse", defuseTime - 1);
            creeper.timeSinceIgnited = 6;
         }
      }

   }
}
