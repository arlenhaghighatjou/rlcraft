package levelup2.skills.mining;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.SkillRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class StoneSpeedBonus extends MiningSpeedBonus {
   public StoneSpeedBonus() {
   }

   public String getSkillName() {
      return "levelup:stonecutting";
   }

   public void register(EventBus bus) {
      bus.register(BreakSpeed.class, this::onBreak);
   }

   public void onBreak(BreakSpeed evt) {
      if (this.isActive()) {
         EntityPlayer player = evt.getEntityPlayer();
         if (player != null) {
            int skill = SkillRegistry.getSkillLevel(evt.getEntityPlayer(), this.getSkillName());
            if (skill > 0) {
               IBlockState state = evt.getState();
               float speed = evt.getNewSpeed();
               if (state.getMaterial() == Material.ROCK) {
                  evt.setNewSpeed(speed + (float)skill * 0.3F);
               }
            }
         }

      }
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.IRON_PICKAXE);
   }
}
