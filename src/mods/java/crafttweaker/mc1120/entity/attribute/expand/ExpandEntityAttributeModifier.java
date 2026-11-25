package crafttweaker.mc1120.entity.attribute.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.attribute.IEntityAttributeModifier;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenMethodStatic;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenExpansion("crafttweaker.entity.AttributeModifier")
@ZenRegister
public class ExpandEntityAttributeModifier {
   public ExpandEntityAttributeModifier() {
   }

   private static AttributeModifier getInternal(IEntityAttributeModifier expanded) {
      return CraftTweakerMC.getAttributeModifier(expanded);
   }

   @ZenMethodStatic
   public static IEntityAttributeModifier createModifier(String name, double amount, int operation, @Optional String uuid) {
      AttributeModifier modifier = uuid == null ? new AttributeModifier(name, amount, operation) : new AttributeModifier(EaglercraftUUID.fromString(uuid), name, amount, operation);
      return CraftTweakerMC.getIEntityAttributeModifier(modifier);
   }

   @ZenGetter("saved")
   @ZenMethod
   public static boolean isSaved(IEntityAttributeModifier attributeModifier) {
      return getInternal(attributeModifier).isSaved();
   }

   @ZenSetter("saved")
   @ZenMethod
   public static void setSaved(IEntityAttributeModifier attributeModifier, boolean saved) {
      getInternal(attributeModifier).setSaved(saved);
   }
}
