package c4.champions.common.rank;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import org.apache.logging.log4j.Level;

public class Rank {
   private final int tier;
   private final int color;
   private final int affixes;
   private final int growthFactor;
   private final float chance;
   private final String[] potions;

   public Rank() {
      this(0, 0, 0, 0.0F, 0, new String[0]);
   }

   public Rank(int tier, int affixes, int growthFactor, float chance, int color, String[] potions) {
      this.tier = tier;
      this.affixes = affixes;
      this.growthFactor = growthFactor;
      this.chance = chance;
      this.color = color;
      this.potions = potions;
   }

   public int getTier() {
      return this.tier;
   }

   public int getColor() {
      String[] colors = ConfigHandler.client.colors;
      if (colors.length >= this.tier && this.tier > 0) {
         String s = colors[this.tier - 1];

         try {
            return Integer.parseInt(s);
         } catch (NumberFormatException var4) {
            Champions.logger.log(Level.ERROR, "Non-integer in color config! " + s);
            return this.color;
         }
      } else {
         return this.color;
      }
   }

   public int getAffixes() {
      return this.affixes;
   }

   public int getGrowthFactor() {
      return this.growthFactor;
   }

   public float getChance() {
      return this.chance;
   }

   public String[] getPotions() {
      return this.potions;
   }

   public void applyGrowth(EntityLivingBase entityLivingBase) {
      this.applyGrowth(entityLivingBase, SharedMonsterAttributes.MAX_HEALTH, ConfigHandler.growth.health, 2);
      entityLivingBase.setHealth(entityLivingBase.getMaxHealth());
      this.applyGrowth(entityLivingBase, SharedMonsterAttributes.ATTACK_DAMAGE, ConfigHandler.growth.attackDamage, 2);
      this.applyGrowth(entityLivingBase, SharedMonsterAttributes.ARMOR, ConfigHandler.growth.armor, 0);
      this.applyGrowth(entityLivingBase, SharedMonsterAttributes.ARMOR_TOUGHNESS, ConfigHandler.growth.armorToughness, 0);
      this.applyGrowth(entityLivingBase, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, ConfigHandler.growth.knockbackResist, 0);
   }

   private void applyGrowth(EntityLivingBase entityLivingBase, IAttribute attribute, double amount, int operation) {
      IAttributeInstance att = entityLivingBase.getEntityAttribute(attribute);
      if (att != null) {
         double oldMax = entityLivingBase.getEntityAttribute(attribute).getBaseValue();
         double newMax = 0.0D;
         amount = amount * (double)this.growthFactor;
         switch(operation) {
         case 0:
            newMax = oldMax + amount;
            break;
         case 1:
            newMax = oldMax * amount;
            break;
         case 2:
            newMax = oldMax * (1.0D + amount);
         }

         entityLivingBase.getEntityAttribute(attribute).setBaseValue(newMax);
      }

   }
}
