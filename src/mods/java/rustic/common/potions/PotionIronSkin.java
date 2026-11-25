package rustic.common.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.world.WorldServer;

public class PotionIronSkin extends PotionBase {
   protected PotionIronSkin() {
      super(false, 16777148, "ironskin");
      this.setIconIndex(0, 0);
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "D666C8fD-8AC4-451D-9A06-777947832156", 3.0D, 0);
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, "D774E354-E3AB-42C4-9716-d2280CD7D988", 2.0D, 0);
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn.isServerWorld()) {
         ((WorldServer)entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketRemoveEntityEffect(entityLivingBaseIn.getEntityId(), this));
         ((WorldServer)entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), entityLivingBaseIn.getAttributeMap().getAllAttributes()));
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn.isServerWorld()) {
         ((WorldServer)entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityEffect(entityLivingBaseIn.getEntityId(), entityLivingBaseIn.getActivePotionEffect(this)));
         ((WorldServer)entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), entityLivingBaseIn.getAttributeMap().getAllAttributes()));
      }

   }
}
