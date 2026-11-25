package net.darkhax.attributefix;

import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "attributefix",
   name = "Attribute Fix",
   version = "1.0.12"
)
public class AttributeFix {
   public static final IAttribute[] ATTRIBUTES = new IAttribute[]{SharedMonsterAttributes.MAX_HEALTH, SharedMonsterAttributes.FOLLOW_RANGE, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, SharedMonsterAttributes.MOVEMENT_SPEED, SharedMonsterAttributes.FLYING_SPEED, SharedMonsterAttributes.ATTACK_DAMAGE, SharedMonsterAttributes.ATTACK_SPEED, SharedMonsterAttributes.ARMOR, SharedMonsterAttributes.ARMOR_TOUGHNESS, SharedMonsterAttributes.LUCK};

   public AttributeFix() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      ConfigurationHandler config = new ConfigurationHandler(event.getSuggestedConfigurationFile());

      for(IAttribute attribute : ATTRIBUTES) {
         if (attribute instanceof RangedAttribute) {
            config.configureRangedAttribute((RangedAttribute)attribute);
         }
      }

      config.syncConfigData();
   }
}
