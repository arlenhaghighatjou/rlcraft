package levelup2.skills.crafting;

import java.util.Random;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FoodHarvestBonus extends BaseSkill {
   public FoodHarvestBonus() {
   }

   public String getSkillName() {
      return "levelup:harvestbonus";
   }

   public byte getSkillType() {
      return 1;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.WHEAT);
   }

   public boolean hasSubscription() {
      return true;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onBlockBroken(BreakEvent evt) {
      if (this.isActive()) {
         if (!evt.getWorld().isRemote && evt.getPlayer() != null) {
            if (!(evt.getState().getBlock() instanceof BlockCrops) && !(evt.getState().getBlock() instanceof BlockStem)) {
               if (evt.getState().getBlock() instanceof BlockMelon) {
                  this.doCropDrops(evt);
               }
            } else if (!((IGrowable)evt.getState().getBlock()).canGrow(evt.getWorld(), evt.getPos(), evt.getState(), false)) {
               this.doCropDrops(evt);
            }
         }

      }
   }

   private void doCropDrops(BreakEvent evt) {
      Random rand = evt.getPlayer().getRNG();
      int skill = SkillRegistry.getSkillLevel(evt.getPlayer(), this.getSkillName());
      if (skill > 0 && rand.nextInt(10) < skill) {
         Item item = evt.getState().getBlock().getItemDropped(evt.getState(), rand, 0);
         if (item == Items.AIR || item == null) {
            if (evt.getState().getBlock() == Blocks.PUMPKIN_STEM) {
               item = Items.PUMPKIN_SEEDS;
            } else if (evt.getState().getBlock() == Blocks.MELON_STEM) {
               item = Items.MELON_SEEDS;
            }
         }

         if (item != Items.AIR && item != null) {
            evt.getWorld().spawnEntity(new EntityItem(evt.getWorld(), (double)evt.getPos().getX(), (double)evt.getPos().getY(), (double)evt.getPos().getZ(), new ItemStack(item, Math.max(1, evt.getState().getBlock().quantityDropped(evt.getState(), 0, rand)), evt.getState().getBlock().damageDropped(evt.getState()))));
         }
      }

   }
}
