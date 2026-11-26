package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ShieldBlockBonus extends BaseSkill {
   public ShieldBlockBonus() {
   }

   public String getSkillName() {
      return "levelup:shieldblock";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.SHIELD);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, this::onDamageTaken);
   }

   public void onDamageTaken(LivingHurtEvent evt) {
      if (this.isActive()) {
         if (evt.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)evt.getEntityLiving();
            int skill = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (skill > 0 && this.isBlocking(player) && player.getRNG().nextFloat() < (float)skill / 10.0F) {
               evt.setAmount(0.0F);
            }
         }

      }
   }

   private boolean isBlocking(EntityPlayer player) {
      return player.isHandActive() && !player.getActiveItemStack().isEmpty() && player.getActiveItemStack().getItem().getItemUseAction(player.getActiveItemStack()) == EnumAction.BLOCK;
   }
}
