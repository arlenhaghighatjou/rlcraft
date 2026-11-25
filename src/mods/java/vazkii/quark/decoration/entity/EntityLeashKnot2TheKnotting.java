package vazkii.quark.decoration.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLeashKnot2TheKnotting extends EntityLiving {
   public EntityLeashKnot2TheKnotting(World worldIn) {
      super(worldIn);
      this.setNoAI(true);
      this.width = 0.375F;
      this.height = 0.5F;
   }

   public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
      this.dismantle(!source.isCreativePlayer());
      return true;
   }

   public void onUpdate() {
      super.onUpdate();
      IBlockState state = this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
      if (!(state.getBlock() instanceof BlockFence)) {
         this.dismantle(true);
      } else {
         Entity holder = this.getHolder();
         if (holder != null && !holder.isDead) {
            if (holder.posY < this.posY && holder instanceof EntityLeashKnot) {
               double targetX = holder.posX;
               double targetY = holder.posY;
               double targetZ = holder.posZ;
               holder.setPosition(this.posX, this.posY, this.posZ);
               this.setPosition(targetX, targetY, targetZ);
            }
         } else {
            this.dismantle(true);
         }
      }

   }

   @Nullable
   private Entity getHolder() {
      return this.getLeashHolder();
   }

   protected boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (!this.world.isRemote) {
         Entity holder = this.getLeashHolder();
         holder.setDead();
         this.dismantle(!player.isCreative());
      }

      return true;
   }

   private void dismantle(boolean drop) {
      this.world.playSound((EntityPlayer)null, this.getPosition(), SoundEvents.ENTITY_LEASHKNOT_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (!this.isDead && this.getHolder() != null && drop && !this.world.isRemote) {
         this.dropItem(Items.LEAD, 1);
      }

      this.setDead();
      Entity holder = this.getHolder();
      if (holder instanceof EntityLeashKnot) {
         holder.setDead();
      }

   }
}
