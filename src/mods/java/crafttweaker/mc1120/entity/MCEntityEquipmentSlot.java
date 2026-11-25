package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Objects;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MCEntityEquipmentSlot implements IEntityEquipmentSlot {
   private final EntityEquipmentSlot slot;

   public MCEntityEquipmentSlot(EntityEquipmentSlot slot) {
      this.slot = slot;
   }

   public int getIndex() {
      return this.slot.getIndex();
   }

   public int getSlotIndex() {
      return this.slot.getSlotIndex();
   }

   public String getName() {
      return this.slot.getName();
   }

   public int compareTo(IEntityEquipmentSlot other) {
      return this.slot.compareTo(CraftTweakerMC.getEntityEquipmentSlot(other));
   }

   public Object getInternal() {
      return this.slot;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCEntityEquipmentSlot that = (MCEntityEquipmentSlot)o;
         return this.slot == that.slot;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.slot});
   }
}
