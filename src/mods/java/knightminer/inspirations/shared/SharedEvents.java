package knightminer.inspirations.shared;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class SharedEvents {
   public static final String TAG_MILKCOOLDOWN = "milk_cooldown";

   public SharedEvents() {
   }

   public static void register(EventBus bus) {
      bus.register(LivingUpdateEvent.class, SharedEvents::updateMilkCooldown);
   }

   public static void updateMilkCooldown(LivingUpdateEvent event) {
      if (InspirationsShared.milkCooldownCow || InspirationsShared.milkCooldownSquid) {
         EntityLivingBase entity = event.getEntityLiving();
         World world = entity.getEntityWorld();
         if (!world.isRemote && world.getTotalWorldTime() % 20L == 0L) {
            if (InspirationsShared.milkCooldownCow && entity instanceof EntityCow && !((EntityCow)entity).isChild() || InspirationsShared.milkCooldownSquid && entity instanceof EntitySquid) {
               NBTTagCompound tags = entity.getEntityData();
               short cooldown = tags.getShort("milk_cooldown");
               if (cooldown > 0) {
                  tags.setShort("milk_cooldown", (short)(cooldown - 1));
                  if (cooldown == 1) {
                     InspirationsNetwork.sendToClients(world, entity.getPosition(), new MilkablePacket(entity, true));
                  }
               }
            }

         }
      }
   }
}
