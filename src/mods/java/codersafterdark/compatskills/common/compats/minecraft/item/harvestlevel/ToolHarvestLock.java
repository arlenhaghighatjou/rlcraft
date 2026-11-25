package codersafterdark.compatskills.common.compats.minecraft.item.harvestlevel;

import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.LockKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ToolHarvestLock implements FuzzyLockKey {
   private final Map<String, Integer> typeLevels = new HashMap();
   private final String toolType;
   private final int harvestLevel;

   public ToolHarvestLock(String toolType, int harvestLevel) {
      this.harvestLevel = harvestLevel;
      this.toolType = toolType;
      if (this.toolType != null) {
         this.typeLevels.put(this.toolType, Integer.valueOf(this.harvestLevel));
      }

   }

   public ToolHarvestLock(ItemStack stack) {
      if (stack.isEmpty()) {
         this.harvestLevel = 0;
         this.toolType = null;
      } else {
         int highestLevel = 0;
         Item item = stack.getItem();

         for(String toolClass : item.getToolClasses(stack)) {
            int level = item.getHarvestLevel(stack, toolClass, (EntityPlayer)null, (IBlockState)null);
            if (level >= 0) {
               this.typeLevels.put(toolClass, Integer.valueOf(level));
               if (level > highestLevel) {
                  highestLevel = level;
               }
            }
         }

         this.harvestLevel = highestLevel;
         this.toolType = null;
      }
   }

   public boolean fuzzyEquals(FuzzyLockKey o) {
      if (o == this) {
         return true;
      } else {
         if (o instanceof ToolHarvestLock) {
            ToolHarvestLock toolLock = (ToolHarvestLock)o;
            if (this.harvestLevel >= toolLock.harvestLevel) {
               if (toolLock.toolType == null) {
                  return this.toolType == null;
               }

               return toolLock.typeLevels.keySet().stream().noneMatch((s) -> {
                  return !this.typeLevels.containsKey(s) || ((Integer)this.typeLevels.get(s)).intValue() < ((Integer)toolLock.typeLevels.get(s)).intValue();
               });
            }
         }

         return false;
      }
   }

   public boolean isNotFuzzy() {
      return false;
   }

   public LockKey getNotFuzzy() {
      return null;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ToolHarvestLock)) {
         return false;
      } else {
         ToolHarvestLock toolLock = (ToolHarvestLock)o;
         if (this.toolType == null) {
            return toolLock.toolType == null && this.harvestLevel == toolLock.harvestLevel;
         } else {
            return this.harvestLevel == toolLock.harvestLevel && this.toolType.equals(toolLock.toolType);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.toolType, this.harvestLevel});
   }
}
