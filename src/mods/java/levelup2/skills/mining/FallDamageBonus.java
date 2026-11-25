package levelup2.skills.mining;

import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FallDamageBonus extends BaseSkill {
   public FallDamageBonus() {
   }

   public String getSkillName() {
      return "levelup:fallprotect";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.FEATHER);
   }

   @SubscribeEvent
   public void reduceFallDamage(LivingHurtEvent evt) {
      if (this.isActive()) {
         if (evt.getSource() == DamageSource.FALL && evt.getEntityLiving() instanceof EntityPlayer) {
            int skill = SkillRegistry.getSkillLevel((EntityPlayer)evt.getEntityLiving(), this.getSkillName());
            if (skill > 0) {
               float reduction = (float)skill * 0.1F;
               evt.setAmount(evt.getAmount() * (1.0F - reduction));
            }
         }

      }
   }
}
