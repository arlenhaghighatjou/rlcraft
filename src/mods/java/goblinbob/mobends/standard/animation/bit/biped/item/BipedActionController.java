package goblinbob.mobends.standard.animation.bit.biped.item;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.standard.AttackActionType;
import goblinbob.mobends.standard.UseActionType;
import goblinbob.mobends.standard.animation.bit.biped.EatingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.ShieldAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.main.ModConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class BipedActionController {
   protected HardAnimationLayer<BipedEntityData<?>> layerAction = new HardAnimationLayer<BipedEntityData<?>>();
   protected UseActionType currentUseActionType = null;
   protected AttackActionType currentAttackActionType = null;
   protected AnimationBit<BipedEntityData<?>> actionBit = null;
   private static final Map<UseActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>> ITEM_USE_ACTION_MAP = new HashMap();
   private static final Map<AttackActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>> ITEM_ATTACK_ACTION_MAP = new HashMap();

   public BipedActionController() {
   }

   private static ArmPose getAction(EntityLivingBase entity, ItemStack heldItem) {
      if (!heldItem.isEmpty()) {
         if (entity.getItemInUseCount() > 0) {
            EnumAction enumaction = heldItem.getItemUseAction();
            if (enumaction == EnumAction.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (enumaction == EnumAction.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }
         }

         return ArmPose.ITEM;
      } else {
         return ArmPose.EMPTY;
      }
   }

   public static UseActionType getBuiltInItemUseAction(Item item, ArmPose armPoseMain, ArmPose armPoseOff) {
      if (item == Items.AIR) {
         return null;
      } else if (item instanceof ItemFood) {
         return UseActionType.FOOD;
      } else if (!(item instanceof ItemBow) && armPoseMain != ArmPose.BOW_AND_ARROW && armPoseOff != ArmPose.BOW_AND_ARROW) {
         return armPoseMain != ArmPose.BLOCK && armPoseOff != ArmPose.BLOCK ? UseActionType.FOOD : UseActionType.SHIELD;
      } else {
         return UseActionType.BOW;
      }
   }

   public static UseActionType getItemUseAction(Item item, ArmPose armPoseMain, ArmPose armPoseOff) {
      UseActionType useActionType = ModConfig.getItemUseAction(item);
      return useActionType != null ? useActionType : getBuiltInItemUseAction(item, armPoseMain, armPoseOff);
   }

   public static AttackActionType getBuiltInItemAttackAction(Item item) {
      if (item instanceof ItemSword) {
         return AttackActionType.SWORD;
      } else {
         return item == Items.AIR ? AttackActionType.FISTS : AttackActionType.TOOL;
      }
   }

   public static AttackActionType getItemAttackAction(Item item) {
      AttackActionType attackActionType = ModConfig.getItemAttackAction(item);
      return attackActionType != null ? attackActionType : getBuiltInItemAttackAction(item);
   }

   public void perform(BipedEntityData<?> data, EnumHandSide primaryHand, ItemStack heldItemMainhand, ItemStack heldItemOffhand, Item activeItem) {
      EntityLivingBase entity = data.getEntity();
      EnumHandSide offHand = primaryHand == EnumHandSide.RIGHT ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
      ArmPose armPoseMain = getAction(entity, heldItemMainhand);
      ArmPose armPoseOff = getAction(entity, heldItemOffhand);
      EnumHandSide activeHandSide = entity.getActiveHand() == EnumHand.MAIN_HAND ? primaryHand : offHand;
      UseActionType useActionType = getItemUseAction(activeItem, armPoseMain, armPoseOff);
      if (useActionType != this.currentUseActionType) {
         this.currentUseActionType = useActionType;
         if (useActionType != null) {
            ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory = (ItemActionFactory)ITEM_USE_ACTION_MAP.get(useActionType);
            this.actionBit = factory.create(activeHandSide);
            this.layerAction.playOrContinueBit(this.actionBit, data);
         } else {
            this.layerAction.clearAnimation();
            this.currentAttackActionType = null;
         }
      }

      AttackActionType attackActionType = getItemAttackAction(heldItemMainhand.getItem());
      if (this.currentAttackActionType != attackActionType) {
         this.currentAttackActionType = attackActionType;
         ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory = (ItemActionFactory)ITEM_ATTACK_ACTION_MAP.get(attackActionType);
         if (factory == null) {
            this.actionBit = null;
            this.layerAction.clearAnimation();
         } else {
            this.actionBit = factory.create(primaryHand);
            this.layerAction.playOrContinueBit(this.actionBit, data);
         }
      }

      this.layerAction.perform(data);
   }

   public void clearAction() {
      this.layerAction.clearAnimation();
   }

   static {
      ITEM_USE_ACTION_MAP.put(UseActionType.FOOD, EatingAnimationBit::new);
      ITEM_USE_ACTION_MAP.put(UseActionType.BOW, BowAction::new);
      ITEM_USE_ACTION_MAP.put(UseActionType.SHIELD, ShieldAnimationBit::new);
      ITEM_ATTACK_ACTION_MAP.put(AttackActionType.TOOL, ToolAction::new);
      ITEM_ATTACK_ACTION_MAP.put(AttackActionType.FISTS, PunchingAction::new);
      ITEM_ATTACK_ACTION_MAP.put(AttackActionType.SWORD, SwordAction::new);

      for(UseActionType type : UseActionType.values()) {
         if (!ITEM_USE_ACTION_MAP.containsKey(type)) {
            throw new IllegalStateException("The ITEM_USE_ACTION_MAP map needs to be complete.");
         }
      }

      for(AttackActionType type : AttackActionType.values()) {
         if (!ITEM_ATTACK_ACTION_MAP.containsKey(type)) {
            throw new IllegalStateException("The ITEM_ATTACK_ACTION_MAP map needs to be complete.");
         }
      }

   }
}
