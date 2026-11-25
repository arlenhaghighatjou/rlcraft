package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashDownAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashInwardAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashOutwardAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackSlashUpAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.AttackWhirlSlashAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.FistGuardAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class AttackAnimationBit extends AnimationBit<PlayerData> {
   protected HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<BipedEntityData<?>>();
   protected AttackSlashUpAnimationBit bitAttackSlashUp = new AttackSlashUpAnimationBit();
   protected AttackSlashDownAnimationBit bitAttackSlashDown = new AttackSlashDownAnimationBit();
   protected AttackSlashInwardAnimationBit bitAttackSlashInward = new AttackSlashInwardAnimationBit();
   protected AttackSlashOutwardAnimationBit bitAttackSlashOutward = new AttackSlashOutwardAnimationBit();
   protected AttackWhirlSlashAnimationBit bitAttackWhirlSlash = new AttackWhirlSlashAnimationBit();
   protected FistGuardAnimationBit bitFistGuard = new FistGuardAnimationBit();

   public AttackAnimationBit() {
   }

   public String[] getActions(PlayerData entityData) {
      return this.layerBase.isPlaying() ? this.layerBase.getPerformedBit().getActions(entityData) : null;
   }

   public boolean shouldPerformAttack(AbstractClientPlayer player) {
      ItemStack heldItemStack = player.getHeldItem(EnumHand.MAIN_HAND);
      return heldItemStack.getItem() != Items.AIR;
   }

   public void perform(PlayerData playerData) {
      AbstractClientPlayer player = (AbstractClientPlayer)playerData.getEntity();
      this.layerBase.perform(playerData);
   }
}
