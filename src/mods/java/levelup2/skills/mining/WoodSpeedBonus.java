package levelup2.skills.mining;

import levelup2.skills.SkillRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WoodSpeedBonus extends MiningSpeedBonus {
   public WoodSpeedBonus() {
   }

   public String getSkillName() {
      return "levelup:woodcutting";
   }

   @SubscribeEvent
   public void onBreak(BreakSpeed evt) {
      if (this.isActive()) {
         EntityPlayer player = evt.getEntityPlayer();
         if (player != null) {
            int skill = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (skill > 0) {
               IBlockState state = evt.getState();
               float speed = evt.getNewSpeed();
               if (state.getMaterial() == Material.WOOD) {
                  evt.setNewSpeed(speed + (float)skill * 0.2F);
               }
            }
         }

      }
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.IRON_AXE);
   }
}
