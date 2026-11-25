package vazkii.quark.world.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.PirateShips;

public class EntityPirate extends EntitySkeleton {
   private static final String TAG_CAPTAIN = "captain";
   private boolean captain;

   public EntityPirate(World worldIn) {
      super(worldIn);
   }

   protected boolean canDespawn() {
      return false;
   }

   public void setCaptain(boolean captain) {
      this.captain = captain;
   }

   public boolean isCaptain() {
      return this.captain;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("captain", this.captain);
   }

   public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.captain = compound.getBoolean("captain");
   }

   protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
   }

   public void updatePassenger(@Nonnull Entity passenger) {
      if (this.isPassenger(passenger) && passenger instanceof EntityParrot) {
         EntityParrot parrot = (EntityParrot)passenger;
         parrot.flap = -1.0F;
         parrot.rotationPitch = 0.0F;
         float rotation = this.rotationYaw;
         float radius = 0.5F;
         float x = (float)Math.cos((double)(rotation * 0.017453292F)) * radius;
         float z = (float)Math.sin((double)(rotation * 0.017453292F)) * radius;
         parrot.setPosition(this.posX + (double)x, this.posY + this.getMountedYOffset(), this.posZ + (double)z);
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      if (this.captain) {
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
      }

   }
}
