package vazkii.quark.world.entity;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.DepthMobs;

public class EntityAshen extends EntitySkeleton {
   public EntityAshen(World worldIn) {
      super(worldIn);
   }

   protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
      PotionUtils.appendEffects(stack, ImmutableSet.of(new PotionEffect(MobEffects.BLINDNESS, 50, 0)));
      this.setHeldItem(EnumHand.OFF_HAND, stack);
      this.inventoryHandsDropChances[0] = 0.0F;
      this.inventoryHandsDropChances[1] = 0.0F;
   }

   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() && this.posY < (double)DepthMobs.upperBound;
   }
}
