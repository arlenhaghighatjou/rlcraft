package vazkii.quark.world.entity;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.feature.Pickarang;

public class EntityPickarang extends EntityThrowable {
   private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntityPickarang.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(EntityPickarang.class, DataSerializers.BOOLEAN);
   private int liveTime;
   private int slot;
   private static final ThreadLocal<Boolean> IS_PICKARANG_UPDATING = ThreadLocal.withInitial(() -> {
      return false;
   });
   private static final String TAG_RETURNING = "returning";
   private static final String TAG_LIVE_TIME = "liveTime";
   private static final String TAG_RETURN_SLOT = "returnSlot";
   private static final String TAG_ITEM_STACK = "itemStack";

   public EntityPickarang(World worldIn) {
      super(worldIn);
   }

   public EntityPickarang(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
      this.setPosition(this.posX, throwerIn.posY + (double)throwerIn.getEyeHeight(), this.posZ);
   }

   public void setThrowData(int slot, ItemStack stack) {
      this.slot = slot;
      this.setStack(stack.copy());
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(STACK, new ItemStack(Pickarang.pickarang));
      this.dataManager.register(RETURNING, Boolean.valueOf(false));
   }

   protected void onImpact(@Nonnull RayTraceResult result) {
      if (!((Boolean)this.dataManager.get(RETURNING)).booleanValue() && !this.world.isRemote) {
         EntityLivingBase owner = this.getThrower();
         if (result.typeOfHit == Type.BLOCK) {
            this.dataManager.set(RETURNING, Boolean.valueOf(true));
            if (!(owner instanceof EntityPlayerMP)) {
               return;
            }

            EntityPlayerMP player = (EntityPlayerMP)owner;
            BlockPos hit = result.getBlockPos();
            IBlockState state = this.world.getBlockState(hit);
            float hardness = state.getBlockHardness(this.world, hit);
            if ((double)hardness <= Pickarang.maxHardness && hardness >= 0.0F) {
               ItemStack prev = player.getHeldItemMainhand();
               player.setHeldItem(EnumHand.MAIN_HAND, this.getStack());
               if (player.interactionManager.tryHarvestBlock(hit)) {
                  this.world.playEvent((EntityPlayer)null, 2001, hit, Block.getStateId(state));
               } else {
                  this.playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1.0F, 1.0F);
               }

               this.setStack(player.getHeldItemMainhand());
               player.setHeldItem(EnumHand.MAIN_HAND, prev);
            } else {
               this.playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1.0F, 1.0F);
            }
         } else if (result.typeOfHit == Type.ENTITY) {
            Entity hit = result.entityHit;
            if (hit != owner) {
               this.setReturning();
               if (hit instanceof EntityPickarang) {
                  ((EntityPickarang)hit).setReturning();
                  this.playSound(QuarkSounds.ENTITY_PICKARANG_CLANK, 1.0F, 1.0F);
               } else {
                  ItemStack pickarang = this.getStack();
                  Multimap<String, AttributeModifier> modifiers = pickarang.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
                  if (owner != null) {
                     ItemStack prev = owner.getHeldItemMainhand();
                     owner.setHeldItem(EnumHand.MAIN_HAND, pickarang);
                     owner.getAttributeMap().applyAttributeModifiers(modifiers);
                     int ticksSinceLastSwing = ((Integer)ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, owner, "ticksSinceLastSwing")).intValue();
                     int cooldownPeriod = (int)(1.0D / owner.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);
                     ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, owner, Integer.valueOf(cooldownPeriod), "ticksSinceLastSwing");
                     Pickarang.setActivePickarang(this);
                     if (owner instanceof EntityPlayer) {
                        ((EntityPlayer)owner).attackTargetEntityWithCurrentItem(hit);
                     } else {
                        owner.attackEntityAsMob(hit);
                     }

                     Pickarang.setActivePickarang((EntityPickarang)null);
                     ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, owner, Integer.valueOf(ticksSinceLastSwing), "ticksSinceLastSwing");
                     this.setStack(owner.getHeldItemMainhand());
                     owner.setHeldItem(EnumHand.MAIN_HAND, prev);
                     owner.getAttributeMap().removeAttributeModifiers(modifiers);
                  } else {
                     AttributeMap map = new AttributeMap();
                     map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
                     map.applyAttributeModifiers(modifiers);
                     ItemStack stack = this.getStack();
                     stack.attemptDamageItem(1, this.world.rand, (EntityPlayerMP)null);
                     this.setStack(stack);
                     hit.attackEntityFrom((new EntityDamageSourceIndirect("player", this, this)).setProjectile(), (float)map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                  }
               }
            }
         }

      }
   }

   protected void setReturning() {
      this.dataManager.set(RETURNING, Boolean.valueOf(true));
   }

   public boolean canBeCollidedWith() {
      return ((Boolean)IS_PICKARANG_UPDATING.get()).booleanValue();
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void onUpdate() {
      IS_PICKARANG_UPDATING.set(Boolean.valueOf(true));
      super.onUpdate();
      IS_PICKARANG_UPDATING.set(Boolean.valueOf(false));
      if (!this.isDead) {
         boolean returning = ((Boolean)this.dataManager.get(RETURNING)).booleanValue();
         ++this.liveTime;
         if (!returning) {
            if (this.liveTime > Pickarang.timeout) {
               this.setReturning();
            }
         } else {
            this.noClip = true;
            ItemStack stack = this.getStack();
            int eff = this.getEfficiencyModifier();
            List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(2.0D));
            List<EntityXPOrb> xp = this.world.getEntitiesWithinAABB(EntityXPOrb.class, this.getEntityBoundingBox().grow(2.0D));
            Vec3d ourPos = this.getPositionVector();

            for(EntityItem item : items) {
               if (!item.isRiding()) {
                  item.startRiding(this);
                  item.setPickupDelay(2);
               }
            }

            for(EntityXPOrb xpOrb : xp) {
               if (!xpOrb.isRiding()) {
                  xpOrb.startRiding(this);
                  xpOrb.delayBeforeCanPickup = 2;
               }
            }

            EntityLivingBase owner = this.getThrower();
            if (owner == null || owner.isDead || !(owner instanceof EntityPlayer)) {
               if (!this.world.isRemote) {
                  this.entityDropItem(stack, 0.0F);
                  this.setDead();
               }

               return;
            }

            Vec3d ownerPos = owner.getPositionVector().addVector(0.0D, 1.0D, 0.0D);
            Vec3d motion = ownerPos.subtract(ourPos);
            double motionMag = 3.25D + (double)eff * 0.25D;
            if (motion.lengthSquared() < motionMag) {
               EntityPlayer player = (EntityPlayer)owner;
               ItemStack stackInSlot = player.inventory.getStackInSlot(this.slot);
               if (!this.world.isRemote) {
                  this.playSound(QuarkSounds.ENTITY_PICKARANG_PICKUP, 1.0F, 1.0F);
                  if (!stack.isEmpty()) {
                     if (!player.isDead && stackInSlot.isEmpty()) {
                        player.inventory.setInventorySlotContents(this.slot, stack);
                     } else if (player.isDead || !player.inventory.addItemStackToInventory(stack)) {
                        player.dropItem(stack, false);
                     }
                  }

                  if (!player.isDead) {
                     for(EntityItem item : items) {
                        ItemStack drop = item.getItem();
                        if (!player.addItemStackToInventory(drop)) {
                           player.dropItem(drop, false);
                        }

                        item.setDead();
                     }

                     for(EntityXPOrb xpOrb : xp) {
                        xpOrb.onCollideWithPlayer(player);
                     }

                     for(Entity riding : this.getPassengers()) {
                        if (!riding.isDead) {
                           if (riding instanceof EntityItem) {
                              ItemStack drop = ((EntityItem)riding).getItem();
                              if (!player.addItemStackToInventory(drop)) {
                                 player.dropItem(drop, false);
                              }

                              riding.setDead();
                           } else if (riding instanceof EntityXPOrb) {
                              riding.onCollideWithPlayer(player);
                           }
                        }
                     }
                  }

                  this.setDead();
               }
            } else {
               motion = motion.normalize().scale(0.7D + (double)((float)eff * 0.325F));
               this.motionX = motion.x;
               this.motionY = motion.y;
               this.motionZ = motion.z;
            }
         }

      }
   }

   protected boolean canFitPassenger(Entity passenger) {
      return super.canFitPassenger(passenger) || passenger instanceof EntityItem || passenger instanceof EntityXPOrb;
   }

   public double getMountedYOffset() {
      return 0.0D;
   }

   @Nonnull
   public SoundCategory getSoundCategory() {
      return SoundCategory.PLAYERS;
   }

   public int getEfficiencyModifier() {
      return EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, this.getStack());
   }

   public ItemStack getStack() {
      return (ItemStack)this.dataManager.get(STACK);
   }

   public void setStack(ItemStack stack) {
      this.dataManager.set(STACK, stack);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(RETURNING, Boolean.valueOf(compound.getBoolean("returning")));
      this.liveTime = compound.getInteger("liveTime");
      this.slot = compound.getInteger("returnSlot");
      if (compound.hasKey("itemStack")) {
         this.setStack(new ItemStack(compound.getCompoundTag("itemStack")));
      } else {
         this.setStack(new ItemStack(Pickarang.pickarang));
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("returning", ((Boolean)this.dataManager.get(RETURNING)).booleanValue());
      compound.setInteger("liveTime", this.liveTime);
      compound.setInteger("returnSlot", this.slot);
      compound.setTag("itemStack", this.getStack().serializeNBT());
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }
}
