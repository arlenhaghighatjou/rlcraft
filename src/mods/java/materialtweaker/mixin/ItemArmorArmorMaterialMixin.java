package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import materialtweaker.util.ArmorAttributeEntry;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorMaterial.class})
public abstract class ItemArmorArmorMaterialMixin {
   @Shadow
   @Final
   private int maxDamageFactor;
   @Shadow(
      remap = false
   )
   public ItemStack repairMaterial;
   private int maxDamageFactorReplace;
   private int[] damageReductionAmountReplace;
   private int enchantabilityReplace;
   private float toughnessReplace;
   private boolean checkedAttributes = false;
   private boolean checkedRepair = false;
   private boolean replacedAttributes = false;
   private boolean replacedRepair = false;

   public ItemArmorArmorMaterialMixin() {
   }

   @Inject(
      method = {"getDurability"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void materialtweaker_armor_getDurability(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesArmor();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(cir.getReturnValueI() / this.maxDamageFactor * this.maxDamageFactorReplace));
      }

   }

   @Inject(
      method = {"getDamageReductionAmount"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_armor_getDamageReductionAmount(EntityEquipmentSlot armorType, CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesArmor();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(this.damageReductionAmountReplace[armorType.getIndex()]));
      }

   }

   @Inject(
      method = {"getEnchantability"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_armor_getEnchantability(CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesArmor();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(this.enchantabilityReplace));
      }

   }

   @Inject(
      method = {"getToughness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_armor_getToughness(CallbackInfoReturnable<Float> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesArmor();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Float.valueOf(this.toughnessReplace));
      }

   }

   @Inject(
      method = {"getRepairItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_armor_getRepairItem(CallbackInfoReturnable<Item> cir) {
      if (!this.checkedRepair) {
         this.checkRepairsArmor();
      }

      if (this.replacedRepair) {
         cir.setReturnValue(this.repairMaterial.getItem());
      }

   }

   @Inject(
      method = {"getRepairItemStack"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void materialtweaker_armor_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
      if (!this.checkedRepair) {
         this.checkRepairsArmor();
      }

      if (this.replacedRepair) {
         cir.setReturnValue(this.repairMaterial);
      }

   }

   @Inject(
      method = {"setRepairItem"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void materialtweaker_armor_setRepairItem(CallbackInfoReturnable<ArmorMaterial> cir) {
      if (!this.checkedRepair) {
         this.checkRepairsArmor();
      }

      if (this.replacedRepair) {
         cir.setReturnValue((ArmorMaterial)this);
      }

   }

   private void checkAttributesArmor() {
      this.checkedAttributes = true;

      try {
         ArmorAttributeEntry attributeEntry = CustomConfigHandler.getArmorAttributes(((ArmorMaterial)this).name());
         if (attributeEntry != null) {
            this.maxDamageFactorReplace = attributeEntry.durabilityFactor;
            this.damageReductionAmountReplace = new int[]{attributeEntry.damageReductionFeet, attributeEntry.damageReductionLegs, attributeEntry.damageReductionChest, attributeEntry.damageReductionHead};
            this.enchantabilityReplace = attributeEntry.enchantability;
            this.toughnessReplace = attributeEntry.toughness;
            this.replacedAttributes = true;
         }
      } catch (Exception var2) {
         MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Caught exception while checking armor material attributes: " + var2);
      }

   }

   private void checkRepairsArmor() {
      this.checkedRepair = true;

      try {
         String[] repairEntry = CustomConfigHandler.getArmorRepairs(((ArmorMaterial)this).name());
         if (repairEntry != null) {
            this.repairMaterial = new ItemStack(Item.getByNameOrId(repairEntry[0]), 1, repairEntry[1].equals("*") ? 32767 : Integer.parseInt(repairEntry[1]));
            this.replacedRepair = true;
         }
      } catch (Exception var2) {
         MaterialTweaker.LOGGER.log(Level.ERROR, "materialtweaker: Caught exception while checking armor material repairs: " + var2);
      }

   }
}
