package com.oblivioussp.spartanweaponry.mixin;

import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityArrow.class})
public class EntityArrowMixin extends EntityMixin {
   @Shadow
   protected boolean inGround;
   @Shadow
   public int arrowShake;
   @Shadow
   public PickupStatus pickupStatus;

   public EntityArrowMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"onCollideWithPlayer(Lnet/minecraft/entity/player/EntityPlayer;)V"},
      cancellable = true
   )
   private void onCollideWithPlayer(EntityPlayer entityIn, CallbackInfo callback) {
      if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
         Log.debug("Player collision with arrow entity intercepted!");
         if (this.pickupStatus == PickupStatus.ALLOWED) {
            List<ItemStack> quivers = QuiverHelper.findValidQuivers(entityIn);
            ItemStack arrowStack = this.getArrowStack();
            ItemStack mainHand = entityIn.getHeldItemMainhand();
            ItemStack offHand = entityIn.getHeldItemOffhand();
            if (offHand.getItem() == arrowStack.getItem() && offHand.getCount() < offHand.getMaxStackSize() || mainHand.getItem() == arrowStack.getItem() && mainHand.getCount() < mainHand.getMaxStackSize()) {
               return;
            }

            if (!quivers.isEmpty()) {
               for(ItemStack quiver : quivers) {
                  if (!arrowStack.isEmpty() && !quiver.isEmpty() && ((ItemQuiverBase)quiver.getItem()).isAmmoValid(arrowStack, quiver)) {
                     if (!quiver.hasTagCompound()) {
                        quiver.setTagCompound(new NBTTagCompound());
                     }

                     if (quiver.getTagCompound().getBoolean("ammoCollect")) {
                        IItemHandler quiverHandler = (IItemHandler)quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

                        for(int i = 0; i < quiverHandler.getSlots(); ++i) {
                           arrowStack = quiverHandler.insertItem(i, arrowStack, false);
                        }
                     }
                  }

                  if (arrowStack.isEmpty()) {
                     Log.debug("Picked up arrow on the ground and placed it in the quiver!");
                     Entity thisEntity = this.world.getEntityByID(this.getEntityId());
                     entityIn.onItemPickup(thisEntity, 1);
                     this.setDead();
                     this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 0.0F);
                     callback.cancel();
                     break;
                  }
               }
            }
         }
      }

   }

   @Shadow
   public ItemStack getArrowStack() {
      throw new IllegalStateException("Mixin failed to shadow the \"AbstractArrowEntity.getArrowStack()\" method!");
   }
}
