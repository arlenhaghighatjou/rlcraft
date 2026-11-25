package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;

public class ConvertedAttribute extends BetterAttribute {
   public final IAttribute mcAttribute;

   public ConvertedAttribute(IAttribute mcAttribute) {
      super(mcAttribute.getName(), mcAttribute.getDefaultValue());
      this.canUseTotalAmountCaching = false;
      this.mcAttribute = mcAttribute;
   }

   public double getBaseAmount(Entity entity) {
      return entity instanceof EntityLivingBase ? MCTools.getAttribute((EntityLivingBase)entity, this.mcAttribute) : this.defaultBaseAmount;
   }
}
