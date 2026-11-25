package familiarfauna.entities;

import familiarfauna.config.ConfigurationHandler;
import familiarfauna.init.ModLootTable;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySnail extends EntityCreature implements IAnimals {
   public EntitySnail(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAIWander(this, 0.6D));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return ModLootTable.SNAIL_LOOT;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SoundEvents.BLOCK_SLIME_STEP, 0.05F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.2F;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && !ConfigurationHandler.snailEnable) {
         this.setDead();
      }

   }
}
