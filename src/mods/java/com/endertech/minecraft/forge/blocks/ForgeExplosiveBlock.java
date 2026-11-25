package com.endertech.minecraft.forge.blocks;

import com.endertech.common.CommonTime;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.entities.FuseEntity;
import com.endertech.minecraft.forge.world.ForgeWorld;
import com.endertech.minecraft.forge.world.WorldBounds;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgeExplosiveBlock<E extends FuseEntity> extends ForgeEntityBlock<E> {
   private boolean alwaysDropOres;
   private final CommonTime.Interval ignitionTime;
   private final float breakingStrength;
   private final int explosionRadius;
   private final FloatBounds resistanceBounds;

   public ForgeExplosiveBlock(ForgeMod mod, UnitConfig config, Class<E> entityClass, CommonTime.Interval ignitionTime, int explosionRadius, float breakingStrength, boolean alwaysDropOres) {
      super(mod, config, entityClass, "", Material.TNT, CreativeTabs.REDSTONE);
      this.alwaysDropOres = alwaysDropOres;
      this.ignitionTime = ignitionTime;
      this.explosionRadius = explosionRadius;
      this.breakingStrength = breakingStrength;
      this.resistanceBounds = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(breakingStrength));
   }

   public abstract void doDestruction(World var1, BlockPos var2, IBlockState var3, Explosion var4);

   protected abstract E createFuseEntity(World var1, BlockPos var2, IBlockState var3, CommonTime.Interval var4, @Nullable EntityLivingBase var5);

   public float getBreakingStrength() {
      return this.breakingStrength;
   }

   protected void destroyBlockAt(World world, BlockPos pos, Explosion explosion) {
      IBlockState state = world.getBlockState(pos);
      if (!ForgeWorld.isAirBlock(state)) {
         Block block = state.getBlock();
         float resistance = block.getExplosionResistance((Entity)null);
         if (this.resistanceBounds.encloses(Float.valueOf(resistance))) {
            boolean dropBlock = this.resistanceBounds.randomFits(Float.valueOf(resistance));
            if (!dropBlock && this.alwaysDropOres) {
               dropBlock = ForgeWorld.isOreBlock(world, pos);
            }

            if (dropBlock) {
               block.dropBlockAsItem(world, pos, state, 0);
            }

            block.onBlockExploded(world, pos, explosion);
         }

      }
   }

   public float getExplosionDamage() {
      return (float)(this.getExplosionRadius() * this.getExplosionRadius());
   }

   public float getSafeDistance() {
      return (float)(this.getExplosionRadius() + 1);
   }

   protected void damageEntitiesWithIn(World world, WorldBounds bounds, BlockPos epicenter, Explosion explosion) {
      List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity((Entity)null, bounds.toAABB());
      float radius = (float)this.getExplosionRadius();
      ForgeEventFactory.onExplosionDetonate(world, explosion, entities, (double)(radius * 2.0F));
      Vec3d epicenterVec = new Vec3d(epicenter);

      for(Entity entity : entities) {
         double distance = entity.getPositionVector().distanceTo(epicenterVec);
         if (distance < (double)this.getSafeDistance()) {
            float damage = this.getExplosionDamage();
            if (entity instanceof EntityLivingBase) {
               damage = (float)EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, (double)damage);
            }

            float factor = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(this.getSafeDistance())).approxFactor(Float.valueOf((float)distance));
            damage = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(damage)).approxDown(factor).floatValue();
            if (damage >= 1.0F) {
               entity.attackEntityFrom(DamageSource.causeExplosionDamage(explosion), damage);
            }
         }
      }

   }

   protected void ignite(World world, BlockPos pos, IBlockState state, @Nullable EntityLivingBase igniter) {
      if (this.isServerSide(world)) {
         FuseEntity fuse = this.createFuseEntity(world, pos, state, this.getIgnitionTime(), igniter);
         world.spawnEntity(fuse);
         world.playSound((EntityPlayer)null, fuse.posX, fuse.posY, fuse.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         world.setBlockToAir(pos);
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack heldStack = player.getHeldItem(hand);
      Item heldItem = heldStack.getItem();
      if (heldItem != Items.FLINT_AND_STEEL && heldItem != Items.FIRE_CHARGE) {
         return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
      } else {
         this.ignite(world, pos, state, player);
         if (heldItem == Items.FLINT_AND_STEEL) {
            heldStack.damageItem(1, player);
         } else if (!player.capabilities.isCreativeMode) {
            heldStack.shrink(1);
         }

         return true;
      }
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (this.isServerSide(world) && entity instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)entity;
         if (arrow.isBurning()) {
            EntityLivingBase igniter;
            if (arrow.shootingEntity instanceof EntityLivingBase) {
               igniter = (EntityLivingBase)arrow.shootingEntity;
            } else {
               igniter = null;
            }

            this.ignite(world, pos, state, igniter);
         }
      }

   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (world.isBlockPowered(pos)) {
         this.ignite(world, pos, state, (EntityLivingBase)null);
      }

   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      if (this.isServerSide(world)) {
         IBlockState state = world.getBlockState(pos);
         FuseEntity fuse = this.createFuseEntity(world, pos, state, CommonTime.Interval.ZERO, explosion.getExplosivePlacedBy());
         world.spawnEntity(fuse);
      }

      world.setBlockToAir(pos);
      super.onBlockExploded(world, pos, explosion);
   }

   public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
   }

   public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      if (world.isBlockPowered(pos) || world.getBlockState(fromPos).getBlock() == Blocks.FIRE) {
         this.ignite(world, pos, state, (EntityLivingBase)null);
      }

   }

   public boolean canDropFromExplosion(Explosion explosion) {
      return false;
   }

   public CommonTime.Interval getIgnitionTime() {
      return this.ignitionTime;
   }

   protected void registerEntity() {
      this.getMod().getRegistrator().registerEntity(this.getEntityClass(), 255, 20, true);
   }

   @SideOnly(Side.CLIENT)
   public void registerRender() {
      this.getMod().getRegistrator().registerRenderingHandler(this.getEntityClass(), new FuseEntity.Renderer.Factory());
   }

   public int getExplosionRadius() {
      return this.explosionRadius;
   }
}
