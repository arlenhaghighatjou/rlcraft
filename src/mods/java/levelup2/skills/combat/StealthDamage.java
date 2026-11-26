package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class StealthDamage extends BaseSkill {
   public StealthDamage() {
   }

   public String getSkillName() {
      return "levelup:stealthdamage";
   }

   public byte getSkillType() {
      return 2;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.POISONOUS_POTATO);
   }

   public boolean hasSubscription() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, this::onDamage);
   }

   public void onDamage(LivingHurtEvent evt) {
      if (this.isActive()) {
         DamageSource src = evt.getSource();
         float dmg = evt.getAmount();
         if (src.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)src.getTrueSource();
            int level = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (level > 0) {
               if (src instanceof EntityDamageSourceIndirect) {
                  if (StealthLib.getDistanceFrom(evt.getEntityLiving(), player) < 256.0F && player.isSneaking() && !StealthLib.canSeePlayer(evt.getEntityLiving()) && !StealthLib.entityIsFacing(evt.getEntityLiving(), player)) {
                     dmg *= 1.0F + 0.15F * (float)level;
                     player.sendStatusMessage(new TextComponentTranslation("sneak.attack", new Object[]{1.0D + 0.15D * (double)level}), true);
                  }
               } else if (player.isSneaking() && !StealthLib.canSeePlayer(evt.getEntityLiving()) && !StealthLib.entityIsFacing(evt.getEntityLiving(), player)) {
                  dmg *= 1.0F + 0.3F * (float)level;
                  player.sendStatusMessage(new TextComponentTranslation("sneak.attack", new Object[]{1.0D + 0.3D * (double)level}), true);
               }
            }
         }

         evt.setAmount(dmg);
      }
   }
}
