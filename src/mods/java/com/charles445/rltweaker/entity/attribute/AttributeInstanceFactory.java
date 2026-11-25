package com.charles445.rltweaker.entity.attribute;

import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public interface AttributeInstanceFactory {
   IAttributeInstance createInstance(AbstractAttributeMap var1, IAttribute var2);
}
