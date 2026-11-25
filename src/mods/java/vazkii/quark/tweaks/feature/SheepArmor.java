package vazkii.quark.tweaks.feature;

import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class SheepArmor extends Feature {
   public static AttributeModifier sheepArmor = new AttributeModifier(UUID.fromString("6e915cea-3f18-485d-a818-373fe4f75f7f"), "sheep_armor", 1.0D, 0);

   public SheepArmor() {
   }

   public void setupConfig() {
      double armorAmount = this.loadPropDouble("Sheep Armor Amount", "The amount of armor points to give to a sheep when it is not sheared.", 1.0D);
      sheepArmor = new AttributeModifier(UUID.fromString("6e915cea-3f18-485d-a818-373fe4f75f7f"), "sheep_armor", armorAmount, 0);
   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntitySheep) {
         EntitySheep entity = (EntitySheep)event.getEntityLiving();
         ModifiableAttributeInstance armorAttribute = (ModifiableAttributeInstance)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
         boolean hasModifier = armorAttribute.hasModifier(sheepArmor);
         boolean isSheared = entity.getSheared();
         if (!isSheared && !hasModifier) {
            armorAttribute.applyModifier(sheepArmor);
         } else if (isSheared && hasModifier) {
            armorAttribute.removeModifier(sheepArmor);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Sheep Armor";
   }
}
