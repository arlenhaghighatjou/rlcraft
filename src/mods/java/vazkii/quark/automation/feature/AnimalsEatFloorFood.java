package vazkii.quark.automation.feature;

import com.google.common.base.Predicate;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class AnimalsEatFloorFood extends Feature {
   public static double maxDensity;

   public AnimalsEatFloorFood() {
   }

   public void setupConfig() {
      maxDensity = this.loadPropDouble("Maximum entity density", "Prevents entities from proliferating infinitely. Set to 0 or less to disable checking.", 2.0D);
   }

   @SubscribeEvent
   public void onEntityTick(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityAnimal && event.getEntityLiving().world instanceof WorldServer) {
         EntityAnimal animal = (EntityAnimal)event.getEntityLiving();
         if (animal.getGrowingAge() == 0 && animal.ticksExisted % 20 == 0 && !animal.isInLove() && !animal.isDead) {
            double range = 2.0D;
            double density = 0.0D;
            if (maxDensity > 0.0D) {
               for(Entity other : animal.world.getEntitiesWithinAABBExcludingEntity(animal, animal.getEntityBoundingBox().grow(10.0D))) {
                  double dSq = other.getDistanceSq(animal);
                  if (dSq <= 100.0D) {
                     density += 1.0D / dSq;
                     if (density > maxDensity) {
                        return;
                     }
                  }
               }
            }

            List<EntityItem> nearbyFood = animal.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, animal.getEntityBoundingBox().expand(range, 0.0D, range), (i) -> {
               return i != null && !i.getItem().isEmpty() && !i.isDead && animal.isBreedingItem(i.getItem()) && i.getItem().getItem() != Items.ROTTEN_FLESH;
            });
            if (!nearbyFood.isEmpty()) {
               nearbyFood.sort(Comparator.comparingDouble((ent) -> {
                  return ent.getDistanceSq(animal);
               }));
               EntityItem e = (EntityItem)nearbyFood.get(0);
               ItemStack stack = e.getItem();
               ItemStack original = stack.copy();
               stack.shrink(1);
               e.setItem(stack);
               if (stack.isEmpty()) {
                  e.setDead();
               }

               if (animal instanceof EntityWolf && original.getItem() instanceof ItemFood && animal.getHealth() < animal.getMaxHealth()) {
                  animal.heal((float)((ItemFood)original.getItem()).getHealAmount(original));
               } else {
                  animal.setInLove((EntityPlayer)null);
               }
            }
         }
      }

   }

   public String[] getIncompatibleMods() {
      return new String[]{"betterwithmods", "easybreeding", "animania"};
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
