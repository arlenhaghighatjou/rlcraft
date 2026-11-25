package rlmixins.mixin.icenfire;

import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemMyrmexEgg.class})
public abstract class ItemMyrmexEggOffhandMixin {
   public ItemMyrmexEggOffhandMixin() {
   }

   @Redirect(
      method = {"onItemUse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/InventoryPlayer;setInventorySlotContents(ILnet/minecraft/item/ItemStack;)V"
)
   )
   public void rlmixins_iceAndFireItemMyrmexEgg_onItemUse(InventoryPlayer instance, int i, ItemStack stack) {
   }
}
