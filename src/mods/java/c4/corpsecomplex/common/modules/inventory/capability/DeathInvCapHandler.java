package c4.corpsecomplex.common.modules.inventory.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathInvCapHandler {
   private static final ResourceLocation DEATH_INV_CAP = new ResourceLocation("corpsecomplex", "deathInventory");

   public DeathInvCapHandler() {
   }

   @SubscribeEvent
   public void attachCapability(AttachCapabilitiesEvent<Entity> e) {
      if (e.getObject() instanceof EntityPlayer) {
         e.addCapability(DEATH_INV_CAP, new DeathInventory.Provider());
      }
   }
}
