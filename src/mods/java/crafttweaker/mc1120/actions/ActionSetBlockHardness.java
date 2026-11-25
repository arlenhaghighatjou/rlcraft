package crafttweaker.mc1120.actions;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ActionSetBlockHardness implements IAction {
   private final ItemStack stack;
   private final float hardness;

   public ActionSetBlockHardness(ItemStack stack, float hardness) {
      this.stack = stack;
      this.hardness = hardness;
   }

   private static void set(ItemStack stack, float hardness) {
      if (isBlock(stack)) {
         Block block = Block.getBlockFromItem(stack.getItem());
         block.setHardness(hardness);
      } else {
         CraftTweakerAPI.logError("Item is not a block");
      }

   }

   public static boolean isBlock(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         ResourceLocation name = (ResourceLocation)Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem()));
         return !name.toString().equals("minecraft:air") && Block.REGISTRY.containsKey(name);
      }
   }

   public void apply() {
      if (isBlock(this.stack)) {
         set(this.stack, this.hardness);
      }

   }

   public String describe() {
      return isBlock(this.stack) ? "Setting hardness of " + this.stack.getDisplayName() + " to " + this.hardness : "Unable to set hardness of " + this.stack.getDisplayName() + " because it is an Item";
   }
}
