package lellson.roughTweaks.misc;

import lellson.roughTweaks.items.RoughItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class RoughEvents {
   public void register(EventBus bus) {
      bus.register(PlayerLoggedInEvent.class, this::logIn);
      bus.register(PlayerTickEvent.class, this::tickEvent);
      bus.register(LivingDropsEvent.class, this::dropEvent);
   }

   public void logIn(PlayerLoggedInEvent event) {
      if (RoughConfig.healthRegenOff && event.player.world.getGameRules().getBoolean("naturalRegeneration")) {
         event.player.world.getGameRules().setOrCreateGameRule("naturalRegeneration", "false");
      }

   }

   public void tickEvent(PlayerTickEvent event) {
      if (event.player.isPlayerFullyAsleep()) {
         event.player.heal((float)RoughConfig.sleepHealAmount);
      }

   }

   public void dropEvent(LivingDropsEvent event) {
      if (RoughConfig.dropChance > 0 && event.getEntityLiving() instanceof EntityMob && this.getEnabledItems() > 0) {
         ItemStack stack = null;
         switch(event.getEntity().world.rand.nextInt(RoughConfig.dropChance * this.getEnabledItems())) {
         case 0:
            if (RoughConfig.salveHealCount > 0) {
               stack = new ItemStack(RoughItems.SALVE);
            }
            break;
         case 1:
            if (RoughConfig.plasterHealCount > 0) {
               stack = new ItemStack(RoughItems.PLASTER);
            }
            break;
         case 2:
            if (RoughConfig.bandageHealCount > 0) {
               stack = new ItemStack(RoughItems.BANDAGE);
            }
         }

         if (stack != null) {
            event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack));
         }
      }

   }

   private int getEnabledItems() {
      int items = 0;
      if (RoughConfig.salveHealCount > 0) {
         ++items;
      }

      if (RoughConfig.plasterHealCount > 0) {
         ++items;
      }

      if (RoughConfig.bandageHealCount > 0) {
         ++items;
      }

      return items;
   }
}
