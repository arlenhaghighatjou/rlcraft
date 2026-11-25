package c4.corpsecomplex.common.modules.effects;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoriPotion extends Potion {
   private static final String name = "Memento Mori";
   private static final double intIncrement = 1.0D;
   private static final double percentIncrement = 0.05D;
   private ResourceLocation icon;
   private static Map<IAttribute, Double> changeAmountMap = Maps.newHashMap();
   private static Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.newHashMap();
   private static double tickHealth;
   private static double tickArmor;
   private static double tickTough;
   private static double tickMove;
   private static double tickSpeed;
   private static double tickDamage;

   public MoriPotion() {
      super(true, 1);
      this.setRegistryName("mori");
      this.setPotionName("effect.corpsecomplex.mori");
      this.icon = new ResourceLocation("corpsecomplex", "textures/icons/mori.png");
   }

   public boolean isReady(int duration, int amplifier) {
      return duration < MoriModule.duration * 20 && MoriModule.doRecover && this.isChangeTick(duration);
   }

   public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
      int duration = entityLivingBaseIn.getActivePotionEffect(MoriModule.moriPotion).getDuration();
      if (MoriModule.modHealth != 0.0D && (double)duration % tickHealth == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.MAX_HEALTH);
         if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
         }
      }

      if (MoriModule.modArmor != 0.0D && (double)duration % tickArmor == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.ARMOR);
      }

      if (MoriModule.modToughness != 0.0D && (double)duration % tickTough == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.ARMOR_TOUGHNESS);
      }

      if (MoriModule.modMove != 0.0D && (double)duration % tickMove == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.MOVEMENT_SPEED);
      }

      if (MoriModule.modSpeed != 0.0D && (double)duration % tickSpeed == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.ATTACK_SPEED);
      }

      if (MoriModule.modDamage != 0.0D && (double)duration % tickDamage == 0.0D) {
         this.incrementRecover(entityLivingBaseIn, SharedMonsterAttributes.ATTACK_DAMAGE);
      }

   }

   public static void setModifiers() {
      attributeModifierMap.put(SharedMonsterAttributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("ca572ca7-d11e-4054-b225-f4c797cdf69b"), "Memento Mori", MoriModule.modHealth, 0));
      attributeModifierMap.put(SharedMonsterAttributes.ARMOR, new AttributeModifier(UUID.fromString("b3bd0150-1953-4971-a822-8445953c4195"), "Memento Mori", MoriModule.modArmor, 0));
      attributeModifierMap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID.fromString("5113ef1e-5200-4d6a-a898-946f0e4b5d26"), "Memento Mori", MoriModule.modToughness, 0));
      attributeModifierMap.put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("f9a9495d-89b5-4676-8345-bc2e92936821"), "Memento Mori", MoriModule.modMove, 2));
      attributeModifierMap.put(SharedMonsterAttributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("9fe627b8-3477-4ccf-9587-87776259172f"), "Memento Mori", MoriModule.modSpeed, 2));
      attributeModifierMap.put(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("ae5b003a-65f3-41f2-b104-4c71a2261d5b"), "Memento Mori", MoriModule.modDamage, 0));
      tickHealth = setIncrement(SharedMonsterAttributes.MAX_HEALTH, MoriModule.modHealth, false);
      tickArmor = setIncrement(SharedMonsterAttributes.ARMOR, MoriModule.modArmor, false);
      tickTough = setIncrement(SharedMonsterAttributes.ARMOR_TOUGHNESS, MoriModule.modToughness, false);
      tickMove = setIncrement(SharedMonsterAttributes.MOVEMENT_SPEED, MoriModule.modMove, true);
      tickSpeed = setIncrement(SharedMonsterAttributes.ATTACK_SPEED, MoriModule.modSpeed, true);
      tickDamage = setIncrement(SharedMonsterAttributes.ATTACK_DAMAGE, MoriModule.modDamage, false);
   }

   private boolean isChangeTick(int duration) {
      return (double)duration % tickHealth == 0.0D || (double)duration % tickArmor == 0.0D || (double)duration % tickTough == 0.0D || (double)duration % tickMove == 0.0D || (double)duration % tickSpeed == 0.0D || (double)duration % tickDamage == 0.0D;
   }

   private void incrementRecover(EntityLivingBase entityIn, IAttribute attribute) {
      IAttributeInstance iAttributeInstance = entityIn.getAttributeMap().getAttributeInstance(attribute);
      AttributeModifier attributeModifier = iAttributeInstance.getModifier(((AttributeModifier)attributeModifierMap.get(attribute)).getID());
      if (attributeModifier != null && !entityIn.getEntityWorld().isRemote) {
         iAttributeInstance.removeModifier(attributeModifier);
         double changeAmount = ((Double)changeAmountMap.get(attribute)).doubleValue();
         iAttributeInstance.applyModifier(new AttributeModifier(attributeModifier.getID(), attributeModifier.getName(), attributeModifier.getAmount() + changeAmount, attributeModifier.getOperation()));
      }
   }

   private static double setIncrement(IAttribute attribute, double amount, boolean isPercent) {
      if (amount == 0.0D) {
         return 0.0D;
      } else {
         double sign = Math.signum(amount) * -1.0D;
         double changeTick = Math.abs((double)MoriModule.duration / amount) * 20.0D;
         double changeAmount;
         if (isPercent) {
            changeAmount = sign * 0.05D;
            changeTick = changeTick * 0.05D;
         } else {
            changeAmount = sign * 1.0D;
            changeTick = changeTick * 1.0D;
         }

         if (changeTick >= 1.0D) {
            changeTick = Math.floor(changeTick);
         } else if (isPercent) {
            changeAmount = sign * (1.0D / changeTick);
            changeTick = 1.0D;
         } else {
            changeAmount = sign * Math.floor(1.0D / changeTick);
            changeTick = 1.0D;
         }

         changeAmountMap.put(attribute, Double.valueOf(changeAmount));
         return changeTick;
      }
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      for(Entry<IAttribute, AttributeModifier> entry : attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = attributeMapIn.getAttributeInstance((IAttribute)entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
         }
      }

   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      for(Entry<IAttribute, AttributeModifier> entry : attributeModifierMap.entrySet()) {
         IAttributeInstance iattributeinstance = attributeMapIn.getAttributeInstance((IAttribute)entry.getKey());
         if (iattributeinstance != null) {
            AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
            iattributeinstance.removeModifier(attributemodifier);
            iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), attributemodifier.getName(), attributemodifier.getAmount(), attributemodifier.getOperation()));
         }
      }

   }

   public boolean hasStatusIcon() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         mc.getTextureManager().bindTexture(this.icon);
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(this.icon);
      Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
   }
}
