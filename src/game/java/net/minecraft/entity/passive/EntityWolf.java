package net.minecraft.entity.passive;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.Item;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.material.Material;
import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.Arrays;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import dev.satyrn.wolfarmor.util.OreDictHelper;
import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import dev.satyrn.wolfarmor.common.network.packets.UpdatePotionEffectMessage;
import dev.satyrn.wolfarmor.common.network.packets.UpdateFoodStatsMessage;
import dev.satyrn.wolfarmor.common.network.packets.RemovePotionEffectMessage;
import dev.satyrn.wolfarmor.common.network.MessageBase;
import dev.satyrn.wolfarmor.api.util.DataHelper;
import dev.satyrn.wolfarmor.api.util.Criteria;
import dev.satyrn.wolfarmor.api.util.CreatureFoodStats;
import dev.satyrn.wolfarmor.api.item.IItemWolfArmor;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.advancements.WolfArmorTrigger;
import dev.satyrn.wolfarmor.WolfArmorMod;
import com.google.common.collect.Multimap;
import com.google.common.base.Predicate;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWolf extends EntityTameable implements IArmoredWolf
{
    private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(EntityWolf.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> BEGGING = EntityDataManager.<Boolean>createKey(EntityWolf.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.<Integer>createKey(EntityWolf.class, DataSerializers.VARINT);
    private float headRotationCourse;
    private float headRotationCourseOld;
    private boolean isWet;
    private boolean isShaking;
    private float timeWolfIsShaking;
    private float prevTimeWolfIsShaking;

    public EntityWolf(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 0.85F);
        this.setTamed(false);
        this.wolfArmorOnConstructed();
    }

    protected void initEntityAI()
    {
        this.aiSit = new EntityAISit(this);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityWolf.AIAvoidEntity(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
        this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(6, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(9, new EntityAIBeg(this, 8.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityAnimal.class, false, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_ instanceof EntitySheep || p_apply_1_ instanceof EntityRabbit;
            }
        }));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, AbstractSkeleton.class, false));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);

        if (this.isTamed())
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
    {
        super.setAttackTarget(entitylivingbaseIn);

        if (entitylivingbaseIn == null)
        {
            this.setAngry(false);
        }
        else if (!this.isTamed())
        {
            this.setAngry(true);
        }
    }

    protected void updateAITasks()
    {
        this.dataManager.set(DATA_HEALTH_ID, Float.valueOf(this.getHealth()));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(DATA_HEALTH_ID, Float.valueOf(this.getHealth()));
        this.dataManager.register(BEGGING, Boolean.valueOf(false));
        this.dataManager.register(COLLAR_COLOR, Integer.valueOf(EnumDyeColor.RED.getDyeDamage()));
        this.wolfArmorOnEntityInit();
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    public static void registerFixesWolf(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityWolf.class);
    }

    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Angry", this.isAngry());
        compound.setByte("CollarColor", (byte)this.getCollarColor().getDyeDamage());
        this.wolfArmorWriteEntityToNBT(compound);
    }

    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setAngry(compound.getBoolean("Angry"));

        if (compound.hasKey("CollarColor", 99))
        {
            this.setCollarColor(EnumDyeColor.byDyeDamage(compound.getByte("CollarColor")));
        }

        this.wolfArmorReadEntityFromNBT(compound);
    }

    protected SoundEvent getAmbientSound()
    {
        SoundEvent wolfArmorSound = this.wolfArmorGetAmbientSound();

        if (wolfArmorSound != null)
        {
            return wolfArmorSound;
        }

        if (this.isAngry())
        {
            return SoundEvents.ENTITY_WOLF_GROWL;
        }
        else if (this.rand.nextInt(3) == 0)
        {
            return this.isTamed() && ((Float)this.dataManager.get(DATA_HEALTH_ID)).floatValue() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
        }
        else
        {
            return SoundEvents.ENTITY_WOLF_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_WOLF;
    }

    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (!this.world.isRemote && this.isWet && !this.isShaking && !this.hasPath() && this.onGround)
        {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
            this.world.setEntityState(this, (byte)8);
        }

        if (!this.world.isRemote && this.getAttackTarget() == null && this.isAngry())
        {
            this.setAngry(false);
        }

        this.wolfArmorOnLivingUpdate();
    }

    public void onUpdate()
    {
        super.onUpdate();
        this.headRotationCourseOld = this.headRotationCourse;

        if (this.isBegging())
        {
            this.headRotationCourse += (1.0F - this.headRotationCourse) * 0.4F;
        }
        else
        {
            this.headRotationCourse += (0.0F - this.headRotationCourse) * 0.4F;
        }

        if (this.isWet())
        {
            this.isWet = true;
            this.isShaking = false;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        }
        else if ((this.isWet || this.isShaking) && this.isShaking)
        {
            if (this.timeWolfIsShaking == 0.0F)
            {
                this.playSound(SoundEvents.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
            this.timeWolfIsShaking += 0.05F;

            if (this.prevTimeWolfIsShaking >= 2.0F)
            {
                this.isWet = false;
                this.isShaking = false;
                this.prevTimeWolfIsShaking = 0.0F;
                this.timeWolfIsShaking = 0.0F;
            }

            if (this.timeWolfIsShaking > 0.4F)
            {
                float f = (float)this.getEntityBoundingBox().minY;
                int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double)f1, (double)(f + 0.8F), this.posZ + (double)f2, this.motionX, this.motionY, this.motionZ);
                }
            }
        }

        this.wolfArmorOnUpdate();
    }

    @SideOnly(Side.CLIENT)
    public boolean isWolfWet()
    {
        return this.isWet;
    }

    @SideOnly(Side.CLIENT)
    public float getShadingWhileWet(float p_70915_1_)
    {
        return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70915_1_) / 2.0F * 0.25F;
    }

    @SideOnly(Side.CLIENT)
    public float getShakeAngle(float p_70923_1_, float p_70923_2_)
    {
        float f = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * p_70923_1_ + p_70923_2_) / 1.8F;

        if (f < 0.0F)
        {
            f = 0.0F;
        }
        else if (f > 1.0F)
        {
            f = 1.0F;
        }

        return MathHelper.sin(f * (float)Math.PI) * MathHelper.sin(f * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
    }

    @SideOnly(Side.CLIENT)
    public float getInterestedAngle(float p_70917_1_)
    {
        return (this.headRotationCourseOld + (this.headRotationCourse - this.headRotationCourseOld) * p_70917_1_) * 0.15F * (float)Math.PI;
    }

    public float getEyeHeight()
    {
        return this.height * 0.8F;
    }

    public int getVerticalFaceSpeed()
    {
        return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
    }

    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            Entity entity = source.getTrueSource();

            if (this.aiSit != null)
            {
                this.aiSit.setSitting(false);
            }

            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
      float damage = (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
      int knockback = 0;
      if (entityIn instanceof EntityLivingBase) {
         damage += EnchantmentHelper.getModifierForCreature(this.getArmorItemStack(), ((EntityLivingBase)entityIn).getCreatureAttribute());
         knockback += EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.KNOCKBACK, (EntityLivingBase)this);
      }

      boolean atkFlag = entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), damage);
      if (atkFlag) {
         if (knockback > 0) {
            ((EntityLivingBase)entityIn).knockBack((Entity)this, (float)knockback * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
            this.motionX *= 0.6000000238418579D;
            this.motionZ *= 0.6000000238418579D;
         }

         int fireAspect = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, (EntityLivingBase)this);
         if (fireAspect > 0) {
            entityIn.setFire(fireAspect * 4);
         }

         this.applyEnchantments((EntityLivingBase)this, entityIn);
         this.foodStats.addExhaustion(0.1F);
      }

      return atkFlag;
   }

    public void setTamed(boolean tamed)
    {
        super.setTamed(tamed);

        if (tamed)
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        Boolean wolfArmorResult = this.wolfArmorProcessInteract(player, hand);

        if (wolfArmorResult != null)
        {
            return wolfArmorResult.booleanValue();
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (this.isTamed())
        {
            if (!itemstack.isEmpty())
            {
                if (itemstack.getItem() instanceof ItemFood)
                {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();

                    if (itemfood.isWolfsFavoriteMeat() && ((Float)this.dataManager.get(DATA_HEALTH_ID)).floatValue() < 20.0F)
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }

                        this.heal((float)itemfood.getHealAmount(itemstack));
                        return true;
                    }
                }
                else if (itemstack.getItem() == Items.DYE)
                {
                    EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());

                    if (enumdyecolor != this.getCollarColor())
                    {
                        this.setCollarColor(enumdyecolor);

                        if (!player.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }

                        return true;
                    }
                }
            }

            if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack))
            {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.navigator.clearPath();
                this.setAttackTarget((EntityLivingBase)null);
            }
        }
        else if (itemstack.getItem() == Items.BONE && !this.isAngry())
        {
            if (!player.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            if (!this.world.isRemote)
            {
                if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player))
                {
                    this.setTamedBy(player);
                    this.navigator.clearPath();
                    this.setAttackTarget((EntityLivingBase)null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte)7);
                }
                else
                {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 8)
        {
            this.isShaking = true;
            this.timeWolfIsShaking = 0.0F;
            this.prevTimeWolfIsShaking = 0.0F;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @SideOnly(Side.CLIENT)
    public float getTailRotation()
    {
        if (this.isAngry())
        {
            return 1.5393804F;
        }
        else
        {
            return this.isTamed() ? (0.55F - (this.getMaxHealth() - ((Float)this.dataManager.get(DATA_HEALTH_ID)).floatValue()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
        }
    }

    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() instanceof ItemFood && ((ItemFood)stack.getItem()).isWolfsFavoriteMeat();
    }

    public int getMaxSpawnedInChunk()
    {
        return 8;
    }

    public boolean isAngry()
    {
        return (((Byte)this.dataManager.get(TAMED)).byteValue() & 2) != 0;
    }

    public void setAngry(boolean angry)
    {
        byte b0 = ((Byte)this.dataManager.get(TAMED)).byteValue();

        if (angry)
        {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 | 2)));
        }
        else
        {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 & -3)));
        }
    }

    public EnumDyeColor getCollarColor()
    {
        return EnumDyeColor.byDyeDamage(((Integer)this.dataManager.get(COLLAR_COLOR)).intValue() & 15);
    }

    public void setCollarColor(EnumDyeColor collarcolor)
    {
        this.dataManager.set(COLLAR_COLOR, Integer.valueOf(collarcolor.getDyeDamage()));
    }

    public EntityWolf createChild(EntityAgeable ageable)
    {
        EntityWolf entitywolf = new EntityWolf(this.world);
        EaglercraftUUID uuid = this.getOwnerId();

        if (uuid != null)
        {
            entitywolf.setOwnerId(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void setBegging(boolean beg)
    {
        this.dataManager.set(BEGGING, Boolean.valueOf(beg));
    }

    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (!this.isTamed())
        {
            return false;
        }
        else if (!(otherAnimal instanceof EntityWolf))
        {
            return false;
        }
        else
        {
            EntityWolf entitywolf = (EntityWolf)otherAnimal;

            if (!entitywolf.isTamed())
            {
                return false;
            }
            else if (entitywolf.isSitting())
            {
                return false;
            }
            else
            {
                return this.isInLove() && entitywolf.isInLove();
            }
        }
    }

    public boolean isBegging()
    {
        return ((Boolean)this.dataManager.get(BEGGING)).booleanValue();
    }

    public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
    {
        if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast))
        {
            if (target instanceof EntityWolf)
            {
                EntityWolf entitywolf = (EntityWolf)target;

                if (entitywolf.isTamed() && entitywolf.getOwner() == owner)
                {
                    return false;
                }
            }

            if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer((EntityPlayer)target))
            {
                return false;
            }
            else
            {
                return !(target instanceof AbstractHorse) || !((AbstractHorse)target).isTame();
            }
        }
        else
        {
            return false;
        }
    }

    public boolean canBeLeashedTo(EntityPlayer player)
    {
        return !this.isAngry() && super.canBeLeashedTo(player);
    }

    class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T>
    {
        private final EntityWolf wolf;

        public AIAvoidEntity(EntityWolf wolfIn, Class<T> p_i47251_3_, float p_i47251_4_, double p_i47251_5_, double p_i47251_7_)
        {
            super(wolfIn, p_i47251_3_, p_i47251_4_, p_i47251_5_, p_i47251_7_);
            this.wolf = wolfIn;
        }

        public boolean shouldExecute()
        {
            if (super.shouldExecute() && this.closestLivingEntity instanceof EntityLlama)
            {
                return !this.wolf.isTamed() && this.avoidLlama((EntityLlama)this.closestLivingEntity);
            }
            else
            {
                return false;
            }
        }

        private boolean avoidLlama(EntityLlama p_190854_1_)
        {
            return p_190854_1_.getStrength() >= EntityWolf.this.rand.nextInt(5);
        }

        public void startExecuting()
        {
            EntityWolf.this.setAttackTarget((EntityLivingBase)null);
            super.startExecuting();
        }

        public void updateTask()
        {
            EntityWolf.this.setAttackTarget((EntityLivingBase)null);
            super.updateTask();
        }
    }


   private int entityXpCooldown;
   private ContainerHorseChest inventory;
   private CreatureFoodStats foodStats;
   private WolfArmorConfig config;
   private SimpleNetworkWrapper connection;

   private void wolfArmorOnConstructed() {
      this.config = WolfArmorMod.getConfig();
      this.connection = WolfArmorMod.getNetworkChannel();
      float minimumHealth = this.config.getCanStarve() ? 0.0F : 8.0F;
      boolean damageEntity = this.config.getFoodStatsLevel() == WolfFoodStatsLevel.FULL;
      this.foodStats = new CreatureFoodStats(minimumHealth, damageEntity);
      this.inventoryInit();
   }

   @Nonnull
   public CreatureFoodStats getFoodStats() {
      return this.foodStats;
   }

   public void addExhaustion(float exhaustion) {
      if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null && !this.getEntityWorld().isRemote) {
         this.foodStats.addExhaustion(exhaustion);
      }

   }

   public void onInventoryChanged(@Nonnull IInventory invBasic) {
      if (!this.getEntityWorld().isRemote) {
         ItemStack armorItemStack = invBasic.getStackInSlot(0);
         this.setArmorItemStack(armorItemStack);
         this.applyArmorModifiers(this.getEntityAttribute(SharedMonsterAttributes.ARMOR), armorItemStack);
         this.applyArmorModifiers(this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS), armorItemStack);
      }
   }

   public Item getChestType() {
      ItemStack chestType = (ItemStack)this.dataManager.get(DataHelper.CHEST_TYPE);
      return chestType.isEmpty() ? null : chestType.getItem();
   }

   public void setChestType(@Nonnull ItemStack stack) {
      ItemStack chestType = stack.copy();
      if (!chestType.isEmpty()) {
         chestType.setCount(1);
      }

      this.dataManager.set(DataHelper.CHEST_TYPE, chestType);
   }

   public boolean getHasChest() {
      return ((Boolean)this.dataManager.get(DataHelper.HAS_CHEST)).booleanValue();
   }

   public void setHasChest(boolean hasChest) {
      this.dataManager.set(DataHelper.HAS_CHEST, Boolean.valueOf(hasChest));
      if (!hasChest) {
         this.dataManager.set(DataHelper.CHEST_TYPE, ItemStack.EMPTY);
      }

   }

   public int getMaxSizeInventory() {
      return 1 + this.config.getChestSize().getColumns() * this.config.getChestSize().getRows();
   }

   @Nonnull
   public InventoryBasic getInventory() {
      return this.inventory;
   }

   @Nonnull
   public ItemStack getArmorItemStack() {
      return (ItemStack)this.dataManager.get(DataHelper.ARMOR_ITEM);
   }

   public void setArmorItemStack(@Nonnull ItemStack armorItemStack) {
      if (armorItemStack != this.dataManager.get(DataHelper.ARMOR_ITEM)) {
         this.dataManager.set(DataHelper.ARMOR_ITEM, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.HEAD, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.CHEST, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.LEGS, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.FEET, armorItemStack);
         Arrays.fill(this.inventoryArmorDropChances, 0.0F);
         Arrays.fill(this.inventoryHandsDropChances, 0.0F);
      }

   }

   public boolean getHasArmor() {
      return !this.getArmorItemStack().isEmpty();
   }

   public boolean canEquipItem(@Nonnull ItemStack armorItemStack) {
      return armorItemStack.isEmpty() || !this.getHasArmor() && armorItemStack.getItem() instanceof ItemWolfArmor;
   }

   public void equipArmor(@Nonnull ItemStack armorItemStack) {
      if (this.canEquipItem(armorItemStack)) {
         this.inventory.setInventorySlotContents(0, armorItemStack);
      }

   }

   public void setInventoryItem(int index, @Nonnull ItemStack itemStack) {
      if (index >= 0 && index < this.inventory.getSizeInventory()) {
         this.inventory.setInventorySlotContents(index, itemStack);
      } else {
         WolfArmorMod.getLogger().log(Level.ERROR, HString.format("Invalid slot: %d", index));
      }

   }

   public void dropEquipment() {
      if (this.getHasArmor()) {
         ItemStack armorItemStack = this.getArmorItemStack();
         if (!this.getEntityWorld().isRemote) {
            this.entityDropItem(armorItemStack, 0.0F);
         }

         this.equipArmor(ItemStack.EMPTY);
      }

   }

   public void dropChest() {
      if (this.getHasChest()) {
         if (!this.getEntityWorld().isRemote) {
            Item chestItem = this.getChestType();
            this.entityDropItem(new ItemStack(chestItem == null ? Item.getItemFromBlock(Blocks.CHEST) : chestItem, 1), 0.0F);
         }

         this.setHasChest(false);
      }

   }

   public void dropInventoryContents() {
      for(int slotIndex = 1; slotIndex <= this.getMaxSizeInventory() - 1; ++slotIndex) {
         ItemStack stackInSlot = this.inventory.getStackInSlot(slotIndex);
         if (!stackInSlot.isEmpty()) {
            if (!this.getEntityWorld().isRemote) {
               this.entityDropItem(stackInSlot, 0.0F);
            }

            this.setInventoryItem(slotIndex, ItemStack.EMPTY);
         }
      }

   }

   protected void damageArmor(float damage) {
      super.damageArmor(damage);
      if (this.getHasArmor()) {
         ItemStack stackInSlot = this.inventory.getStackInSlot(0);
         if (!stackInSlot.isEmpty()) {
            stackInSlot.damageItem((int)Math.ceil((double)damage), (EntityLivingBase)this);
            if (stackInSlot.getCount() == 0) {
               ItemStack particleStack = stackInSlot.copy();
               particleStack.setCount(1);
               this.equipArmor(ItemStack.EMPTY);
               this.renderBrokenItemStack(particleStack);
            }
         }
      }

   }

   public void travel(float strafe, float vertical, float forward) {
      double initX = this.posX;
      double initY = this.posY;
      double initZ = this.posZ;
      super.travel(strafe, vertical, forward);
      this.addMovementStat(this.posX - initX, this.posY - initY, this.posZ - initZ);
   }

   protected void onNewPotionEffect(PotionEffect effect) {
      super.onNewPotionEffect(effect);
      if (!this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, false);
      }

   }

   protected void onChangedPotionEffect(PotionEffect effect, boolean sendUpdate) {
      super.onChangedPotionEffect(effect, sendUpdate);
      if (sendUpdate && !this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, false);
      }

   }

   protected void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      if (!this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, true);
      }

   }

   public void onDeath(DamageSource damageSource) {
      this.dropEquipment();
      this.dropChest();
      this.dropInventoryContents();
      super.onDeath(damageSource);
   }


   private void wolfArmorOnEntityInit() {
      this.dataManager.register(DataHelper.HAS_CHEST, Boolean.valueOf(false));
      this.dataManager.register(DataHelper.ARMOR_ITEM, ItemStack.EMPTY);
      this.dataManager.register(DataHelper.CHEST_TYPE, ItemStack.EMPTY);
   }

   private void wolfArmorOnUpdate() {
      if (this.foodStats != null && this.isTamed() && !this.getEntityWorld().isRemote && this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED) {
         this.foodStats.onUpdate((EntityLivingBase)this);
         this.connection.sendToAll(new UpdateFoodStatsMessage(this.getEntityId(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
      }

      --this.entityXpCooldown;
   }

   private void wolfArmorOnLivingUpdate() {
      if (!this.isDead && !this.getEntityWorld().isRemote) {
         ItemStack enchantedItem = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, (EntityLivingBase)this);
         if (!enchantedItem.isEmpty() && enchantedItem.isItemDamaged()) {
            AxisAlignedBB aabb;
            if (this.isRiding() && !this.getRidingEntity().isDead) {
               aabb = this.getEntityBoundingBox().union(this.getRidingEntity().getEntityBoundingBox());
            } else {
               aabb = this.getEntityBoundingBox();
            }

            for(Entity entity : this.getEntityWorld().getEntitiesWithinAABBExcludingEntity((Entity)this, aabb)) {
               if (entity instanceof EntityXPOrb && !entity.isDead) {
                  EntityXPOrb xpOrb = (EntityXPOrb)entity;
                  if (xpOrb.delayBeforeCanPickup == 0 && this.entityXpCooldown == 0) {
                     float ratio = enchantedItem.getItem().getXpRepairRatio(enchantedItem);
                     int repairAmount = Math.min(this.xpRoundAverage((float)xpOrb.xpValue * ratio), enchantedItem.getItemDamage());
                     xpOrb.xpValue -= this.xpRoundAverage((float)xpOrb.xpValue / ratio);
                     enchantedItem.setItemDamage(enchantedItem.getItemDamage() - repairAmount);
                     this.entityXpCooldown = 2;
                  }

                  if (xpOrb.xpValue == 0) {
                     xpOrb.setDead();
                  }
               }
            }
         }
      }

   }

   @Nullable
   private SoundEvent wolfArmorGetAmbientSound() {
      if (!this.isAngry() && this.isTamed() && this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null) {
         if (this.getRNG().nextInt(3) == 0 && (float)this.foodStats.getFoodLevel() + this.foodStats.getSaturationLevel() <= 18.0F) {
            return SoundEvents.ENTITY_WOLF_WHINE;
         }

      }

      return null;
   }

   private void wolfArmorWriteEntityToNBT(@Nonnull NBTTagCompound compound) {
      boolean hasChest = this.getHasChest();
      compound.setBoolean("HasChest", hasChest);
      if (hasChest) {
         ItemStack chestInSlot = (ItemStack)this.dataManager.get(DataHelper.CHEST_TYPE);
         NBTTagCompound chestItem = new NBTTagCompound();
         chestInSlot.writeToNBT(chestItem);
         compound.setTag("ChestType", chestItem);
         NBTTagList inventoryItems = new NBTTagList();
         IInventory inventory = this.getInventory();

         for(byte slotIndex = 0; slotIndex < inventory.getSizeInventory(); ++slotIndex) {
            ItemStack stackInSlot = inventory.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty()) {
               NBTTagCompound stackInSlotTags = new NBTTagCompound();
               stackInSlotTags.setByte("Slot", slotIndex);
               stackInSlot.writeToNBT(stackInSlotTags);
               inventoryItems.appendTag(stackInSlotTags);
            }
         }

         compound.setTag("Inventory", inventoryItems);
      }

      if (this.getHasArmor()) {
         ItemStack armorItemStack = this.getArmorItemStack();
         if (!armorItemStack.isEmpty()) {
            NBTTagCompound armorItemTag = armorItemStack.writeToNBT(new NBTTagCompound());
            compound.setTag("ArmorItem", armorItemTag);
         } else {
            compound.removeTag("ArmorItem");
         }
      }

      if (this.foodStats != null) {
         this.foodStats.writeNBT(compound);
      }

   }

   private void wolfArmorReadEntityFromNBT(@Nonnull NBTTagCompound compound) {
      if (compound.hasKey("ForgeCaps")) {
         NBTTagCompound capabilities = compound.getCompoundTag("ForgeCaps");
         if (capabilities.hasKey("wolfarmor:wolf_armor")) {
            this.processLegacyDataTags(capabilities.getCompoundTag("wolfarmor:wolf_armor"));
            return;
         }
      }

      boolean hasChest = compound.getBoolean("HasChest");
      this.setHasChest(hasChest);
      if (hasChest) {
         this.inventoryInit();
         ItemStack chestTypeStack = new ItemStack(compound.getCompoundTag("ChestType"));
         this.dataManager.set(DataHelper.CHEST_TYPE, chestTypeStack);
         NBTTagList inventoryTagList = compound.getTagList("Inventory", 10);

         for(int index = 0; index < inventoryTagList.tagCount(); ++index) {
            NBTTagCompound compoundTagAt = inventoryTagList.getCompoundTagAt(index);
            byte slotIndex = compoundTagAt.getByte("Slot");
            if (slotIndex >= 0 && slotIndex < this.inventory.getSizeInventory()) {
               this.inventory.setInventorySlotContents(slotIndex, new ItemStack(compoundTagAt));
            } else {
               WolfArmorMod.getLogger().log(Level.WARN, HString.format("[NBT LOAD] Discarded invalid slot information at index %d", slotIndex));
            }
         }
      }

      NBTTagCompound armorTags = compound.getCompoundTag("ArmorItem");
      if (!armorTags.hasNoTags()) {
         ItemStack armorItemStack = new ItemStack(armorTags);
         this.equipArmor(armorItemStack);
      }

      if (this.foodStats != null) {
         this.foodStats.readNBT(compound);
      }

   }

   @Nullable
   private Boolean wolfArmorProcessInteract(EntityPlayer player, EnumHand hand) {
      if (player.isSneaking() && this.isOwner(player) && this.isTamed() && !this.isChild()) {
         this.openWolfInventory(player);
         return Boolean.valueOf(true);
      } else {
         ItemStack itemStack = player.getHeldItem(hand);
         if (!itemStack.isEmpty()) {
            if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null && itemStack.getItem() instanceof ItemFood) {
               ItemFood foodItem = (ItemFood)itemStack.getItem();
               Boolean result = Boolean.valueOf(false);
               if (!foodItem.isWolfsFavoriteMeat() && !this.isBreedingItem(itemStack)) {
                  if (this.isOwner(player) && !this.world.isRemote) {
                     this.setSitting(!this.isSitting());
                     this.isJumping = false;
                     this.getNavigator().clearPath();
                     this.setAttackTarget((EntityLivingBase)null);
                  }
               } else if (foodItem.isWolfsFavoriteMeat() && this.foodStats.needFood()) {
                  this.foodStats.addStats(foodItem, itemStack);
                  this.consumeItemFromStack(player, itemStack);
                  result = Boolean.valueOf(true);
               } else if (this.isBreedingItem(itemStack)) {
                  if (this.getGrowingAge() == 0 && !this.isInLove()) {
                     this.consumeItemFromStack(player, itemStack);
                     this.setInLove(player);
                     result = Boolean.valueOf(true);
                  } else if (this.isChild()) {
                     this.consumeItemFromStack(player, itemStack);
                     this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                     result = Boolean.valueOf(true);
                  }
               } else {
                  result = Boolean.valueOf(false);
               }

               return result;
            }

            if (this.isTamed() && this.isOwner(player) && !this.isChild()) {
               if (this.config.getChestEnabled() && !this.getHasChest() && OreDictHelper.isOre(false, "chestWood", itemStack)) {
                  if (!this.getEntityWorld().isRemote) {
                     ItemStack chestItem = itemStack.copy();
                     chestItem.setCount(1);
                     this.wolfArmorPlayEquipSound(chestItem);
                     this.setHasChest(true);
                     this.setChestType(chestItem);
                     this.consumeItemFromStack(player, itemStack);
                     if (player instanceof EntityPlayerMP) {
                        ((WolfArmorTrigger)Criteria.EQUIP_WOLF_CHEST).trigger((EntityPlayerMP)player, (EntityWolf)this);
                     }
                  }

                  return Boolean.valueOf(true);
               }

               if (dev.satyrn.wolfarmor.api.util.Items.isValidWolfArmor(itemStack)) {
                  if (this.getArmorItemStack().isEmpty()) {
                     ItemStack toEquip = itemStack.copy();
                     toEquip.setCount(1);
                     this.equipArmor(toEquip);
                     this.consumeItemFromStack(player, itemStack);
                  } else {
                     this.openWolfInventory(player);
                  }

                  return Boolean.valueOf(true);
               }
            }
         }

         return null;
      }
   }

   private void applyArmorModifiers(@Nullable IAttributeInstance instance, @Nonnull ItemStack stack) {
      if (instance != null) {
         instance.removeModifier(IItemWolfArmor.ARMOR_UUID);
         if (!stack.isEmpty() && stack.getItem() instanceof ItemWolfArmor) {
            ItemWolfArmor armorItem = (ItemWolfArmor)stack.getItem();
            Multimap<String, AttributeModifier> map = armorItem.getAttributeModifiers(EntityEquipmentSlot.CHEST, stack);
            if (map.containsKey(instance.getAttribute().getName())) {
               map.get(instance.getAttribute().getName()).forEach(instance::applyModifier);
            }

         }
      }
   }

   private void addMovementStat(double deltaX, double deltaY, double deltaZ) {
      if (!this.isRiding()) {
         if (this.isInsideOfMaterial(Material.WATER)) {
            int magnitude3d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
            if (magnitude3d > 0) {
               this.addExhaustion(0.01F * (float)magnitude3d * 0.01F);
            }
         } else if (this.isInWater()) {
            int magnitude2d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
            if (magnitude2d > 0) {
               this.addExhaustion(0.01F * (float)magnitude2d * 0.01F);
            }
         } else if (this.onGround) {
            int magnitude2d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
            if (magnitude2d > 0 && this.isSprinting()) {
               this.addExhaustion(0.1F * (float)magnitude2d * 0.01F);
            }
         }
      }

   }

   private void inventoryInit() {
      ContainerHorseChest inventoryExisting = this.inventory;
      this.inventory = new ContainerHorseChest("inventory.wolfarmor.wolf", this.getMaxSizeInventory());
      String customWolfName = this.getCustomNameTag();
      if (!customWolfName.isEmpty()) {
         this.inventory.setCustomName(customWolfName);
      }

      if (inventoryExisting != null) {
         inventoryExisting.removeInventoryChangeListener(this);
         int slotCount = Math.min(this.inventory.getSizeInventory(), inventoryExisting.getSizeInventory());

         for(int slotIndex = 0; slotIndex < slotCount; ++slotIndex) {
            ItemStack stackInSlot = inventoryExisting.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty()) {
               this.inventory.setInventorySlotContents(slotIndex, stackInSlot.copy());
            }
         }
      }

      this.inventory.addInventoryChangeListener(this);
      this.inventory.markDirty();
   }

   private void wolfArmorPlayEquipSound(@Nonnull ItemStack itemStack) {
      if (!itemStack.isEmpty()) {
         SoundEvent sound = null;
         if (OreDictHelper.isOre(false, "chestWood", itemStack)) {
            sound = SoundEvents.ENTITY_CHICKEN_EGG;
         }

         if (sound != null) {
            this.playSound(sound, 1.0F, (this.getRNG().nextFloat() - this.getRNG().nextFloat()) * 0.2F + 1.0F);
         }

      }
   }

   private void openWolfInventory(@Nonnull EntityPlayer player) {
      if (!this.getEntityWorld().isRemote) {
         this.getAISit().setSitting(true);
         player.openGui(WolfArmorMod.getInstance(), this.getEntityId(), this.getEntityWorld(), MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
      }

   }

   private void processLegacyDataTags(@Nonnull NBTTagCompound compound) {
      WolfArmorMod.getLogger().log(Level.INFO, "[NBT LOAD] Updating capable wolf to mixin wolf...");
      boolean hasChest = compound.getBoolean("hasChest");
      this.setHasChest(hasChest);
      if (hasChest) {
         this.inventoryInit();
         this.dataManager.set(DataHelper.CHEST_TYPE, new ItemStack(Blocks.CHEST, 1));
         NBTTagList inventoryTagList = compound.getTagList("inventory", 10);

         for(int index = 0; index < inventoryTagList.tagCount(); ++index) {
            NBTTagCompound compoundTagAt = inventoryTagList.getCompoundTagAt(index);
            byte slotIndex = compoundTagAt.getByte("slot");
            if (slotIndex >= 0 && slotIndex < this.inventory.getSizeInventory()) {
               this.inventory.setInventorySlotContents(slotIndex, new ItemStack(compoundTagAt));
            } else {
               WolfArmorMod.getLogger().log(Level.WARN, HString.format("[NBT LOAD] Discarded invalid slot information at index %d", slotIndex));
            }
         }
      }

      NBTTagCompound armorTags = compound.getCompoundTag("armorItem");
      if (!armorTags.hasNoTags()) {
         ItemStack armorItemStack = new ItemStack(armorTags);
         this.equipArmor(armorItemStack);
      }

   }

   private void dispatchPotionEffectMessage(PotionEffect effect, boolean removeEffect) {
      MessageBase<?> message = (MessageBase<?>)(removeEffect ? new RemovePotionEffectMessage(this.getEntityId(), effect.getPotion()) : new UpdatePotionEffectMessage(this.getEntityId(), effect));
      this.connection.sendToAll(message);
   }

   private int xpRoundAverage(float value) {
      float floor = (float)MathHelper.floor(value);
      return (int)floor + (Math.random() < (double)(value - floor) ? 1 : 0);
   }

   static {
      DataHelper.HAS_CHEST = EntityDataManager.createKey(EntityWolf.class, DataSerializers.BOOLEAN);
      DataHelper.ARMOR_ITEM = EntityDataManager.createKey(EntityWolf.class, DataSerializers.ITEM_STACK);
      DataHelper.CHEST_TYPE = EntityDataManager.createKey(EntityWolf.class, DataSerializers.ITEM_STACK);
   }
}
