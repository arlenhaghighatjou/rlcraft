package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import materialtweaker.util.ToolAttributeEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ToolMaterial.class})
public abstract class ItemToolMaterialMixin {
   @Shadow(
      remap = false
   )
   private ItemStack repairMaterial;
   private int harvestLevelReplace;
   private int maxUsesReplace;
   private float efficiencyReplace;
   private float attackDamageReplace;
   private int enchantabilityReplace;
   private boolean checkedAttributes = false;
   private boolean checkedRepairs = false;
   private boolean replacedAttributes = false;
   private boolean replacedRepair = false;

   public ItemToolMaterialMixin() {
   }

   @Inject(
      method = {"getMaxUses"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getMaxUses(CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesTool();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(this.maxUsesReplace));
      }

   }

   @Inject(
      method = {"getEfficiency"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getEfficiency(CallbackInfoReturnable<Float> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesTool();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Float.valueOf(this.efficiencyReplace));
      }

   }

   @Inject(
      method = {"getAttackDamage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getAttackDamage(CallbackInfoReturnable<Float> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesTool();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Float.valueOf(this.attackDamageReplace));
      }

   }

   @Inject(
      method = {"getHarvestLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getHarvestLevel(CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesTool();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(this.harvestLevelReplace));
      }

   }

   @Inject(
      method = {"getEnchantability"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getEnchantability(CallbackInfoReturnable<Integer> cir) {
      if (!this.checkedAttributes) {
         this.checkAttributesTool();
      }

      if (this.replacedAttributes) {
         cir.setReturnValue(Integer.valueOf(this.enchantabilityReplace));
      }

   }

   @Inject(
      method = {"getRepairItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_tool_getRepairItem(CallbackInfoReturnable<Item> cir) {
      if (!this.checkedRepairs) {
         this.checkRepairsTool();
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
   public void materialtweaker_tool_getRepairItemStack(CallbackInfoReturnable<ItemStack> cir) {
      if (!this.checkedRepairs) {
         this.checkRepairsTool();
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
   public void materialtweaker_tool_setRepairItem(CallbackInfoReturnable<ToolMaterial> cir) {
      if (!this.checkedRepairs) {
         this.checkRepairsTool();
      }

      if (this.replacedRepair) {
         cir.setReturnValue((ToolMaterial)this);
      }

   }

   private void checkAttributesTool() {
      this.checkedAttributes = true;

      try {
         ToolAttributeEntry attributeEntry = CustomConfigHandler.getToolAttributes(((ToolMaterial)this).name());
         if (attributeEntry != null) {
            this.harvestLevelReplace = attributeEntry.harvestLevel;
            this.maxUsesReplace = attributeEntry.maxUses;
            this.efficiencyReplace = attributeEntry.efficiency;
            this.attackDamageReplace = attributeEntry.attackDamage;
            this.enchantabilityReplace = attributeEntry.enchantability;
            this.replacedAttributes = true;
         }
      } catch (Exception var2) {
         MaterialTweaker.LOGGER.log(Level.WARN, "materialtweaker: Caught exception while checking tool material attributes: " + var2);
      }

   }

   private void checkRepairsTool() {
      this.checkedRepairs = true;

      try {
         String[] repairEntry = CustomConfigHandler.getToolRepairs(((ToolMaterial)this).name());
         if (repairEntry != null) {
            this.repairMaterial = new ItemStack(Item.getByNameOrId(repairEntry[0]), 1, repairEntry[1].equals("*") ? 32767 : Integer.parseInt(repairEntry[1]));
            this.replacedRepair = true;
         }
      } catch (Exception var2) {
         MaterialTweaker.LOGGER.log(Level.WARN, "materialtweaker: Caught exception while checking tool material repairs: " + var2);
      }

   }
}
