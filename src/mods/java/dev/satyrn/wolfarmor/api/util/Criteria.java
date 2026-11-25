package dev.satyrn.wolfarmor.api.util;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("wolfarmor")
public abstract class Criteria {
   public static ICriterionTrigger EQUIP_WOLF_ARMOR;
   public static ICriterionTrigger EQUIP_WOLF_CHEST;

   public Criteria() {
   }
}
