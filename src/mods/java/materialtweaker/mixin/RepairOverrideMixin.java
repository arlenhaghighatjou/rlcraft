package materialtweaker.mixin;

import materialtweaker.core.MaterialTweaker;
import materialtweaker.handlers.CustomConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemTool.class, ItemElytra.class, ItemArmor.class, ItemSword.class, Item.class, ItemShield.class})
public abstract class RepairOverrideMixin {
   private boolean checked = false;
   private boolean overrideRepair = false;
   private boolean strict = false;
   private ItemStack repairItemStack;

   public RepairOverrideMixin() {
   }

   @Inject(
      method = {"getIsRepairable"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void materialtweaker_getIsRepairable(ItemStack toRepair, ItemStack repairInput, CallbackInfoReturnable<Boolean> cir) {
      if (!toRepair.isEmpty() && !repairInput.isEmpty()) {
         if (!this.checked) {
            this.checkOverrides();
         }

         if (this.overrideRepair) {
            cir.setReturnValue(Boolean.valueOf(OreDictionary.itemMatches(this.repairItemStack, repairInput, this.strict)));
         }

      }
   }

   private void checkOverrides() {
      this.checked = true;

      try {
         String[] entry = CustomConfigHandler.getItemOverrideRepairs(((Item)this).getRegistryName().toString());
         if (entry != null) {
            this.repairItemStack = new ItemStack(Item.getByNameOrId(entry[0]), 1, entry[1].equals("*") ? 32767 : Integer.parseInt(entry[1]));
            this.strict = !entry[1].equals("*");
            this.overrideRepair = true;
         }
      } catch (Exception var2) {
         MaterialTweaker.LOGGER.log(Level.WARN, "materialtweaker: Error in force overriding repair material: " + var2);
      }

   }
}
