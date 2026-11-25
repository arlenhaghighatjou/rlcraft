package vazkii.quark.misc.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityParrotEgg extends EntityThrowable {
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityParrotEgg.class, DataSerializers.VARINT);
   private static final String TAG_COLOR = "color";

   public EntityParrotEgg(World worldIn) {
      super(worldIn);
   }

   public EntityParrotEgg(World worldIn, EntityLivingBase throwerIn, int color) {
      super(worldIn, throwerIn);
      this.dataManager.set(COLOR, Integer.valueOf(color));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(COLOR, Integer.valueOf(0));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("color", this.getColor());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(COLOR, Integer.valueOf(compound.getInteger("color")));
   }

   public int getColor() {
      return ((Integer)this.dataManager.get(COLOR)).intValue();
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 3) {
         double d0 = 0.08D;

         for(int i = 0; i < 8; ++i) {
            this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[]{Item.getIdFromItem(Items.EGG)});
         }
      }

   }

   protected void onImpact(@Nonnull RayTraceResult result) {
      if (result.entityHit != null) {
         result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
      }

      if (!this.world.isRemote) {
         EntityParrot parrot = new EntityParrot(this.world);
         parrot.setVariant(this.getColor());
         parrot.setGrowingAge(-24000);
         parrot.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
         this.world.spawnEntity(parrot);
         this.world.setEntityState(this, (byte)3);
         this.setDead();
      }

   }
}
