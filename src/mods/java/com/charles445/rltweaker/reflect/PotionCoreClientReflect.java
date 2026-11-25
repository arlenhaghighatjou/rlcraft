package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PotionCoreClientReflect {
   public final IAttribute MAGIC_SHIELDING;
   public final ResourceLocation ARMOR_MAGIC_SHIELD;
   public final Class c_PotionCoreAttributes = Class.forName("com.tmtravlr.potioncore.PotionCoreAttributes");
   public final Field f_PotionCoreAttributes_MAGIC_SHIELDING;
   public final Class c_PotionCoreEventHandlerClient;
   public final Method m_drawEnchantmentGlint;
   public final Field f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD;

   public PotionCoreClientReflect() throws Exception {
      this.f_PotionCoreAttributes_MAGIC_SHIELDING = ReflectUtil.findField(this.c_PotionCoreAttributes, "MAGIC_SHIELDING");
      this.MAGIC_SHIELDING = (IAttribute)this.f_PotionCoreAttributes_MAGIC_SHIELDING.get((Object)null);
      this.c_PotionCoreEventHandlerClient = Class.forName("com.tmtravlr.potioncore.PotionCoreEventHandlerClient");
      this.m_drawEnchantmentGlint = ReflectUtil.findMethod(this.c_PotionCoreEventHandlerClient, "drawEnchantmentGlint");
      this.f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD = ReflectUtil.findField(this.c_PotionCoreEventHandlerClient, "ARMOR_MAGIC_SHIELD");
      this.ARMOR_MAGIC_SHIELD = (ResourceLocation)this.f_PotionCoreEventHandlerClient_ARMOR_MAGIC_SHIELD.get((Object)null);
   }

   public void drawEnchantmentGlint(double x, double y, double z, double width, double height) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_drawEnchantmentGlint.invoke((Object)null, x, y, z, width, height);
   }
}
