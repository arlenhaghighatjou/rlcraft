package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowCatcher extends EntityArrowBase implements IThrowableEntity {
   public EntityArrowCatcher(World worldIn) {
      super(worldIn);
   }

   public EntityArrowCatcher(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowCatcher(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowAir);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote && !(living instanceof EntityPlayer) && living.isNonBoss()) {
         ItemStack egg = new ItemStack(Items.SPAWN_EGG);
         NBTTagCompound nbt = new NBTTagCompound();
         living.writeToNBTAtomically(nbt);
         egg.setTagCompound(nbt);
         this.entityDropItem(egg, 0.1F);
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
