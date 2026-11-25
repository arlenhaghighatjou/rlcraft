package xzeroair.trinkets.items.potions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;
import xzeroair.trinkets.items.base.BasePotion;

public class TransformationPotion extends BasePotion {
   protected String uuid;

   public TransformationPotion(String name, int color, int duration, String UUID, boolean isBadEffect) {
      this("xat", name, color, duration, UUID, isBadEffect);
   }

   public TransformationPotion(String modid, String name, int color, int duration, String UUID, boolean isBadEffect) {
      super(modid, name, color, duration, isBadEffect);
      this.registerPotionAttributeModifier(RaceAttribute.ENTITY_RACE, UUID, 1.0D, 0);
      this.uuid = UUID;
   }

   public TransformationPotion(String modid, String name, int color, int duration, String UUID, boolean isBadEffect, int IconX, int IconY, ResourceLocation texture) {
      super(modid, name, color, duration, isBadEffect, IconX, IconY, texture);
      this.registerPotionAttributeModifier(RaceAttribute.ENTITY_RACE, UUID, 1.0D, 0);
      this.uuid = UUID;
   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
      List<Potion> removal = new ArrayList();

      for(Entry<Potion, PotionEffect> effect : entity.getActivePotionMap().entrySet()) {
         if (effect.getKey() instanceof TransformationPotion) {
            TransformationPotion pot = (TransformationPotion)effect.getKey();
            if (!pot.getRaceUUID().contentEquals(this.getRaceUUID())) {
               removal.add(pot);
            }
         }
      }

      for(Potion pot : removal) {
         entity.removePotionEffect(pot);
      }

      RaceAttribute.removeAllModifiers(entity);
      super.applyAttributesModifiersToEntity(entity, attributeMapIn, amplifier);
   }

   public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public boolean isInstant() {
      return false;
   }

   public boolean isBadEffect() {
      return super.isBadEffect();
   }

   public boolean isBeneficial() {
      return super.isBeneficial();
   }

   public List<ItemStack> getCurativeItems() {
      List<ItemStack> cures = new ArrayList();
      return cures;
   }

   public String getRaceUUID() {
      return this.uuid;
   }
}
