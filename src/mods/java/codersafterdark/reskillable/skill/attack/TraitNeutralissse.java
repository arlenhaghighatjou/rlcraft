package codersafterdark.reskillable.skill.attack;

import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.lib.LibObfuscation;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TraitNeutralissse extends Trait {
   private static final String TAG_DEFUSED = "skillable:defuse";

   public TraitNeutralissse() {
      super(new ResourceLocation("reskillable", "neutralissse"), 1, 2, new ResourceLocation("reskillable", "attack"), 10, "reskillable:attack|24", "reskillable:agility|8");
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void onAttackMob(LivingHurtEvent event) {
      if (!event.isCanceled()) {
         if (event.getEntity() instanceof EntityCreeper) {
            EntityCreeper creeper = (EntityCreeper)event.getEntity();
            int time = ((Integer)ReflectionHelper.getPrivateValue(EntityCreeper.class, creeper, LibObfuscation.TIME_SINCE_IGNITED)).intValue();
            if (time < 5) {
               creeper.getEntityData().setInteger("skillable:defuse", 40);
            }
         }

      }
   }

   @SubscribeEvent
   public void entityTick(LivingUpdateEvent event) {
      if (!event.isCanceled() && event.getEntity() instanceof EntityCreeper) {
         EntityCreeper creeper = (EntityCreeper)event.getEntity();
         int defuseTime = creeper.getEntityData().getInteger("skillable:defuse");
         if (defuseTime > 0) {
            creeper.getEntityData().setInteger("skillable:defuse", defuseTime - 1);
            ReflectionHelper.setPrivateValue(EntityCreeper.class, creeper, Integer.valueOf(6), LibObfuscation.TIME_SINCE_IGNITED);
         }
      }

   }
}
