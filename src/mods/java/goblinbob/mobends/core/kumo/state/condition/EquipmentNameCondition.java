package goblinbob.mobends.core.kumo.state.condition;

import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EquipmentNameCondition implements ITriggerCondition {
   private String namePattern;
   private EntityEquipmentSlot slot;

   public EquipmentNameCondition(EquipmentNameCondition.Template template) {
      this.namePattern = template.namePattern;
      this.slot = template.slot;
   }

   public boolean isConditionMet(ITriggerConditionContext context) {
      Entity entity = context.getEntityData().getEntity();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         ItemStack itemStack = player.getItemStackFromSlot(this.slot);
         return itemStack.getDisplayName().matches(this.namePattern);
      } else {
         return false;
      }
   }

   public static class Template extends TriggerConditionTemplate {
      public String namePattern;
      public EntityEquipmentSlot slot;

      public Template() {
      }
   }
}
