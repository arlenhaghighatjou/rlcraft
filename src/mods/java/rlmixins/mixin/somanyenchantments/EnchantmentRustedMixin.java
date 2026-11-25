package rlmixins.mixin.somanyenchantments;

import com.Shultrea.Rin.Ench0_1_0.EnchantmentRusted;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantmentRusted.class})
public abstract class EnchantmentRustedMixin {
   public EnchantmentRustedMixin() {
   }

   @ModifyArg(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lcom/Shultrea/Rin/Enchantment_Base_Sector/EnchantmentBase;<init>(Lnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnumEnchantmentType;[Lnet/minecraft/inventory/EntityEquipmentSlot;)V"
)
   )
   private static Rarity rlmixins_soManyEnchantmentsEnchantmentRusted_initRarity(Rarity rarity) {
      return Rarity.RARE;
   }

   @Inject(
      method = {"rustEvent"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_soManyEnchantmentsEnchantmentRusted_rustEvent(PlayerTickEvent e, CallbackInfo ci) {
      ci.cancel();
   }

   @Inject(
      method = {"onEntityDamagedAlt"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void rlmixins_soManyEnchantmentsEnchantmentRusted_onEntityDamagedAlt(EntityLivingBase user, Entity entiti, ItemStack stack, int level, CallbackInfo ci) {
      ci.cancel();
   }
}
