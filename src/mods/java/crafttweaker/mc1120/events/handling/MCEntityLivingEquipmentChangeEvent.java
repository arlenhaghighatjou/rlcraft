package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingEquipmentChangeEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;

public class MCEntityLivingEquipmentChangeEvent implements EntityLivingEquipmentChangeEvent {
   private final LivingEquipmentChangeEvent event;

   public MCEntityLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
      this.event = event;
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }

   public IItemStack getFrom() {
      return CraftTweakerMC.getIItemStack(this.event.getFrom());
   }

   public IItemStack getTo() {
      return CraftTweakerMC.getIItemStack(this.event.getTo());
   }

   public IEntityEquipmentSlot getSlot() {
      return CraftTweakerMC.getIEntityEquipmentSlot(this.event.getSlot());
   }
}
