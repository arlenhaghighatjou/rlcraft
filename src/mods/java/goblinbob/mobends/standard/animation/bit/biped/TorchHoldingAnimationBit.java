package goblinbob.mobends.standard.animation.bit.biped;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.standard.data.BipedEntityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class TorchHoldingAnimationBit extends AnimationBit<BipedEntityData<?>> {
   private static final String[] ACTIONS = new String[]{"torch_holding"};

   public TorchHoldingAnimationBit() {
   }

   public String[] getActions(BipedEntityData<?> data) {
      return ACTIONS;
   }

   private EnumHandSide getTorchHand(EntityLivingBase living) {
      EnumHandSide mainHand = living.getPrimaryHand();
      EnumHandSide offHand = mainHand == EnumHandSide.LEFT ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
      Item mainItem = living.getHeldItem(EnumHand.MAIN_HAND).getItem();
      Item offItem = living.getHeldItem(EnumHand.OFF_HAND).getItem();
      Item torch = Item.getItemFromBlock(Blocks.TORCH);
      if (mainItem.equals(torch)) {
         return mainHand;
      } else {
         return offItem.equals(torch) ? offHand : null;
      }
   }

   public void perform(BipedEntityData<?> data) {
      EntityLivingBase living = data.getEntity();
      EnumHandSide torchHand = this.getTorchHand(living);
      if (torchHand != null) {
         IModelPart mainArm = torchHand == EnumHandSide.RIGHT ? data.rightArm : data.leftArm;
         IModelPart mainForeArm = torchHand == EnumHandSide.RIGHT ? data.rightForeArm : data.leftForeArm;
         mainArm.getRotation().orientX(-90.0F + ((Float)data.headPitch.get()).floatValue() * 0.5F).rotateY(((Float)data.headYaw.get()).floatValue() * 0.7F);
         mainForeArm.getRotation().orientX(-5.0F);
      }
   }
}
