package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class DrawSpeedBonus extends BaseSkill {
   public DrawSpeedBonus() {
   }

   public String getSkillName() {
      return "levelup:bowdraw";
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.BOW);
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(ArrowNockEvent.class, EventPriority.LOW, false, this::onBowUse);
   }

   public void onBowUse(ArrowNockEvent evt) {
      if (this.isActive()) {
         int archery = SkillRegistry.getSkillLevel(evt.getEntityPlayer(), this.getSkillName());
         if (archery > 0) {
            evt.getEntityPlayer().setActiveHand(evt.getHand());
            this.setItemUseCount(evt.getEntityPlayer(), archery);
            evt.setAction(new ActionResult(EnumActionResult.SUCCESS, evt.getBow()));
         }

      }
   }

   private void setItemUseCount(EntityPlayer player, int archery) {
      player.activeItemStackUseCount -= archery;
   }
}
