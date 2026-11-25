package rlmixins.mixin.somanyenchantments;

import com.Shultrea.Rin.Ench0_1_0.EnchantmentHeavyWeight;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantmentHeavyWeight.class})
public abstract class EnchantmentHeavyWeightMixin {
   public EnchantmentHeavyWeightMixin() {
   }

   @ModifyArg(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lcom/Shultrea/Rin/Enchantment_Base_Sector/EnchantmentBase;<init>(Lnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnumEnchantmentType;[Lnet/minecraft/inventory/EntityEquipmentSlot;)V"
)
   )
   private static Rarity rlmixins_soManyEnchantmentsEnchantmentHeavyWeight_initRarity(Rarity rarity) {
      return Rarity.RARE;
   }

   @Inject(
      method = {"HandleEnchant"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_soManyEnchantmentsEnchantmentHeavyWeight_handleEnchant(LivingUpdateEvent fEvent, CallbackInfo ci) {
      ci.cancel();
   }
}
