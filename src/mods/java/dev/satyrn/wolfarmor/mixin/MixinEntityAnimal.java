package dev.satyrn.wolfarmor.mixin;

import javax.annotation.Nullable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityAnimal.class})
public abstract class MixinEntityAnimal extends MixinEntityAgeable {
   public MixinEntityAnimal() {
   }

   @Shadow
   public abstract void setInLove(@Nullable EntityPlayer var1);

   @Shadow
   protected abstract void consumeItemFromStack(EntityPlayer var1, ItemStack var2);

   @Shadow
   public abstract boolean isInLove();

   @Shadow
   public abstract boolean isBreedingItem(ItemStack var1);
}
