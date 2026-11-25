package gigaherz.toolbelt.customslots.example;

import com.google.common.collect.ImmutableList;
import gigaherz.toolbelt.customslots.IExtensionContainer;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VanillaLivingEquipment implements IExtensionContainer {
   public static final ResourceLocation HEAD = new ResourceLocation("minecraft", EntityEquipmentSlot.HEAD.getName());
   public static final ResourceLocation CHEST = new ResourceLocation("minecraft", EntityEquipmentSlot.CHEST.getName());
   public static final ResourceLocation LEGS = new ResourceLocation("minecraft", EntityEquipmentSlot.LEGS.getName());
   public static final ResourceLocation FEET = new ResourceLocation("minecraft", EntityEquipmentSlot.FEET.getName());
   public static final ResourceLocation OFFHAND = new ResourceLocation("minecraft", EntityEquipmentSlot.OFFHAND.getName());
   public static final ResourceLocation MAINHAND = new ResourceLocation("minecraft", EntityEquipmentSlot.MAINHAND.getName());
   private final EntityLivingBase owner;
   private final ImmutableList<IExtensionSlot> slots;

   public VanillaLivingEquipment(EntityLivingBase owner) {
      this.slots = ImmutableList.of(new VanillaLivingEquipment.Slot(HEAD, EntityEquipmentSlot.HEAD), new VanillaLivingEquipment.Slot(CHEST, EntityEquipmentSlot.CHEST), new VanillaLivingEquipment.Slot(LEGS, EntityEquipmentSlot.LEGS), new VanillaLivingEquipment.Slot(FEET, EntityEquipmentSlot.FEET), new VanillaLivingEquipment.Slot(OFFHAND, EntityEquipmentSlot.OFFHAND), new VanillaLivingEquipment.Slot(MAINHAND, EntityEquipmentSlot.MAINHAND));
      this.owner = owner;
   }

   @Nonnull
   public ImmutableList<IExtensionSlot> getSlots() {
      return this.slots;
   }

   public void onContentsChanged(IExtensionSlot slot) {
   }

   @Nonnull
   public EntityLivingBase getOwner() {
      return this.owner;
   }

   private class Slot implements IExtensionSlot {
      private final ResourceLocation id;
      private final EntityEquipmentSlot slot;

      private Slot(ResourceLocation id, EntityEquipmentSlot slot) {
         this.id = id;
         this.slot = slot;
      }

      @Nonnull
      public IExtensionContainer getContainer() {
         return VanillaLivingEquipment.this;
      }

      @Nonnull
      public ResourceLocation getType() {
         return this.id;
      }

      @Nonnull
      public ItemStack getContents() {
         return VanillaLivingEquipment.this.owner.getItemStackFromSlot(this.slot);
      }

      public void setContents(@Nonnull ItemStack stack) {
         VanillaLivingEquipment.this.owner.setItemStackToSlot(this.slot, stack);
      }

      public void onContentsChanged() {
         VanillaLivingEquipment.this.onContentsChanged(this);
      }

      public boolean canEquip(@Nonnull ItemStack stack) {
         return stack.getItem().isValidArmor(stack, this.slot, VanillaLivingEquipment.this.owner) ? true : super.canEquip(stack);
      }
   }
}
