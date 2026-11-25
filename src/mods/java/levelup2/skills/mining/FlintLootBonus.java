package levelup2.skills.mining;

import java.util.Random;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlintLootBonus extends BaseSkill {
   public FlintLootBonus() {
   }

   public String getSkillName() {
      return "levelup:flintloot";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Blocks.GRAVEL);
   }

   @SubscribeEvent
   public void gravelLooting(HarvestDropsEvent evt) {
      if (this.isActive()) {
         if (evt.getHarvester() != null && !evt.getWorld().isRemote) {
            IBlockState state = evt.getState();
            Random rand = evt.getHarvester().getRNG();
            int skill = SkillRegistry.getSkillLevel(evt.getHarvester(), this.getSkillName());
            if (!evt.isSilkTouching() && skill > 0 && state.getBlock() instanceof BlockGravel && rand.nextInt(10) < skill) {
               Library.removeFromList(evt.getDrops(), new ItemStack(state.getBlock()));
               evt.getDrops().add(new ItemStack(Items.FLINT));
            }
         }

      }
   }
}
