package levelup2.skills.mining;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.PlankCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WoodcuttingBonus extends BaseSkill {
   public WoodcuttingBonus() {
   }

   public String getSkillName() {
      return "levelup:lumbering";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Blocks.LOG);
   }

   @SubscribeEvent
   public void onHarvest(HarvestDropsEvent evt) {
      if (this.isActive()) {
         if (evt.getHarvester() != null && !evt.getWorld().isRemote) {
            int skill = SkillRegistry.getSkillLevel(evt.getHarvester(), this.getSkillName());
            IBlockState state = evt.getState();
            EaglercraftRandom rand = evt.getHarvester().getRNG();
            if (skill > 0 && PlankCache.contains(state.getBlock(), state.getBlock().damageDropped(state))) {
               if (rand.nextDouble() <= (double)skill / 30.0D) {
                  ItemStack planks = PlankCache.getProduct(state.getBlock(), state.getBlock().damageDropped(state));
                  if (!planks.isEmpty()) {
                     evt.getDrops().add(planks.copy());
                  }
               }

               if (rand.nextDouble() <= (double)skill / 30.0D) {
                  evt.getDrops().add(new ItemStack(Items.STICK, 2));
               }
            }
         }

      }
   }
}
