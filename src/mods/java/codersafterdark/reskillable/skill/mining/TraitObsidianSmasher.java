package codersafterdark.reskillable.skill.mining;

import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.base.ConditionHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class TraitObsidianSmasher extends Trait {
   public TraitObsidianSmasher() {
      super(new ResourceLocation("reskillable", "obsidian_smasher"), 1, 2, new ResourceLocation("reskillable", "mining"), 4, "reskillable:mining|16");
   }

   public void getBreakSpeed(BreakSpeed event) {
      if (!event.isCanceled()) {
         EntityPlayer player = event.getEntityPlayer();
         IBlockState state = event.getState();
         if (state.getBlock() == Blocks.OBSIDIAN && ConditionHelper.hasRightTool(player, state, "pickaxe", ToolMaterial.DIAMOND.getHarvestLevel())) {
            event.setNewSpeed(event.getOriginalSpeed() * 10.0F);
         }

      }
   }
}
