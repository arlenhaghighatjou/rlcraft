package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowTriple extends EntityArrowBase implements IThrowableEntity {
   float offset = 7.0F;
   private boolean spawn = true;
   private float velocity;
   private float inaccuracy;
   private float pitch;
   private float yaw;
   private double damagesetter = ArrowConfig.DamageArrowTriple.getDoub();

   public EntityArrowTriple(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowTriple(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowTriple(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(Items.ARROW);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && this.spawn) {
         this.generateArrow(this.offset);
         this.generateArrow(-this.offset);
         this.spawn = false;
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("spawn", this.spawn);
      compound.setFloat("pitch", this.pitch);
      compound.setFloat("yaw", this.yaw);
      compound.setFloat("velocity", this.velocity);
      compound.setFloat("inaccuracy", this.inaccuracy);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.spawn = compound.getBoolean("spawn");
      this.pitch = compound.getFloat("pitch");
      this.yaw = compound.getFloat("yaw");
      this.velocity = compound.getFloat("velocity");
      this.inaccuracy = compound.getFloat("inaccuracy");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   public void generateArrow(float corner) {
      EntityTippedArrow generateArrow = new EntityTippedArrow(this.world);
      generateArrow.setPotionEffect(new ItemStack(Items.ARROW, 1));
      generateArrow.shootingEntity = this.shootingEntity;
      float yawi = this.yaw + corner;
      generateArrow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.pitch, this.pitch);
      generateArrow.setPosition(this.posX, this.posY, this.posZ);
      float f = -MathHelper.sin(yawi * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F);
      float f1 = -MathHelper.sin(this.pitch * 0.017453292F);
      float f2 = MathHelper.cos(yawi * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F);
      generateArrow.shoot((double)f, (double)f1, (double)f2, this.velocity, this.inaccuracy);
      if (generateArrow.shootingEntity != null) {
         generateArrow.motionX += generateArrow.shootingEntity.motionX;
         generateArrow.motionZ += generateArrow.shootingEntity.motionZ;
      }

      generateArrow.setIsCritical(this.getIsCritical());
      generateArrow.setDamage(this.getDamage());
      generateArrow.pickupStatus = this.pickupStatus;
      if (this.isBurning()) {
         generateArrow.setFire(100);
      }

      if (this.getEntityData().hasKey("NoGravity")) {
         generateArrow.getEntityData().setInteger("NoGravity", 70);
      }

      this.world.spawnEntity(generateArrow);
   }

   public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
      this.pitch = pitch;
      this.yaw = yaw;
      this.velocity = velocity;
      this.inaccuracy = inaccuracy;
      float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
      float f1 = -MathHelper.sin(pitch * 0.017453292F);
      float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
      this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
      this.motionX += shooter.motionX;
      this.motionZ += shooter.motionZ;
      if (!shooter.onGround) {
         this.motionY += shooter.motionY;
      }

   }
}
