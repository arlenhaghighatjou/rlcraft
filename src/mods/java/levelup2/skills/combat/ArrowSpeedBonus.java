package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class ArrowSpeedBonus extends BaseSkill {
   public ArrowSpeedBonus() {
   }

   public String getSkillName() {
      return "levelup:arrowspeed";
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.ARROW);
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, EventPriority.LOW, false, this::onArrowLoose);
   }

   public void onArrowLoose(EntityJoinWorldEvent evt) {
      if (this.isActive()) {
         if (evt.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow)evt.getEntity();
            if (arrow.shootingEntity instanceof EntityPlayer) {
               int archer = SkillRegistry.getSkillLevel((EntityPlayer)arrow.shootingEntity, this.getSkillName());
               if (archer > 0) {
                  arrow.motionX *= (double)(1.0F + (float)archer / 10.0F);
                  arrow.motionY *= (double)(1.0F + (float)archer / 10.0F);
                  arrow.motionZ *= (double)(1.0F + (float)archer / 10.0F);
               }
            }
         }

      }
   }
}
