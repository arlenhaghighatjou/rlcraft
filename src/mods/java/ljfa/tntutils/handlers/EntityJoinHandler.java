package ljfa.tntutils.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import ljfa.tntutils.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntityJoinHandler {
   public EntityJoinHandler() {
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, this::onEntityJoin);
   }

   public void onEntityJoin(EntityJoinWorldEvent event) {
      Entity ent = event.getEntity();
      if (Config.disableTNT && ent instanceof EntityTNTPrimed) {
         event.setCanceled(true);
         event.getWorld().spawnEntity(new EntityItem(event.getWorld(), ent.posX, ent.posY, ent.posZ, new ItemStack(Blocks.TNT)));
      } else if (Config.disableTNTMinecart && ent instanceof EntityMinecartTNT) {
         event.setCanceled(true);
         event.getWorld().spawnEntity(new EntityItem(event.getWorld(), ent.posX, ent.posY, ent.posZ, new ItemStack(Blocks.TNT)));
         event.getWorld().spawnEntity(new EntityItem(event.getWorld(), ent.posX, ent.posY, ent.posZ, new ItemStack(Items.MINECART)));
      }

   }
}
