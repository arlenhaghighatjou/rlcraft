package vazkii.quark.world.entity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import vazkii.quark.world.feature.Archaeologist;

public class EntityArchaeologist extends EntityCreature implements IMerchant {
   private MerchantRecipeList buyingList;
   private EntityPlayer buyingPlayer;
   private int timeUntilReset;
   private int randomTickDivider;

   public EntityArchaeologist(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 1.95F);
      if (Archaeologist.enableHat && Archaeologist.dropHat) {
         this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Archaeologist.archaeologist_hat));
      }

   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
   }

   private void populateBuyingList() {
      if (this.buyingList == null) {
         this.buyingList = new MerchantRecipeList();
      }

      EaglercraftRandom r = this.world.rand;
      this.buyingList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2 + this.rand.nextInt(3)), new ItemStack(Items.BONE, 3 + this.rand.nextInt(3))));
      if (r.nextBoolean()) {
         this.buyingList.add(new MerchantRecipe(new ItemStack(Items.BONE, 10 + this.rand.nextInt(5)), new ItemStack(Items.EMERALD, 1)));
      } else {
         this.buyingList.add(new MerchantRecipe(new ItemStack(Items.GUNPOWDER, 7 + this.rand.nextInt(10)), new ItemStack(Items.EMERALD, 1)));
      }

      if (r.nextBoolean()) {
         this.buyingList.add(new MerchantRecipe(new ItemStack(Items.COAL, 16 + this.rand.nextInt(10)), new ItemStack(Items.EMERALD, 1)));
      }

      if (r.nextBoolean()) {
         this.buyingList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 12 + this.rand.nextInt(10)), new ItemStack(Items.DIAMOND, 1)));
      }

      if (r.nextBoolean()) {
         if (r.nextBoolean()) {
            this.buyingList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 8 + this.rand.nextInt(5)), new ItemStack(Items.IRON_PICKAXE, 1)));
         } else {
            this.buyingList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 6 + this.rand.nextInt(4)), new ItemStack(Items.IRON_SHOVEL, 1)));
         }
      }

      if (Archaeologist.enableHat && Archaeologist.sellHat) {
         this.buyingList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 6 + this.rand.nextInt(4)), ItemStack.EMPTY, new ItemStack(Archaeologist.archaeologist_hat, 1), 0, 1));
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
   }

   protected void updateAITasks() {
      if (--this.randomTickDivider <= 0) {
         this.randomTickDivider = 70 + this.rand.nextInt(50);
         if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;
            if (this.timeUntilReset <= 0) {
               for(MerchantRecipe merchantrecipe : this.buyingList) {
                  if (merchantrecipe.isRecipeDisabled()) {
                     merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                  }
               }
            }
         }
      }

   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = itemstack.getItem() == Items.NAME_TAG;
      if (flag) {
         itemstack.interactWithEntity(player, this, hand);
         return true;
      } else if (this.isEntityAlive() && !this.isTrading() && !player.isSneaking()) {
         if (this.buyingList == null) {
            this.populateBuyingList();
         }

         if (hand == EnumHand.MAIN_HAND) {
            player.addStat(StatList.TALKED_TO_VILLAGER);
         }

         if (!this.world.isRemote && !this.buyingList.isEmpty()) {
            boolean any = false;

            for(MerchantRecipe recipe : this.buyingList) {
               if (!recipe.isRecipeDisabled()) {
                  any = true;
                  break;
               }
            }

            if (!any) {
               player.sendStatusMessage(new TextComponentTranslation("quarkmisc.out_of_stock", new Object[]{this.getDisplayName()}), true);
               return true;
            }

            this.setCustomer(player);
            player.displayVillagerTradeGui(this);
         } else if (this.buyingList.isEmpty()) {
            return super.processInteract(player, hand);
         }

         return true;
      } else {
         return super.processInteract(player, hand);
      }
   }

   public void setCustomer(EntityPlayer player) {
      this.buyingPlayer = player;
   }

   public EntityPlayer getCustomer() {
      return this.buyingPlayer;
   }

   public MerchantRecipeList getRecipes(@Nonnull EntityPlayer player) {
      if (this.buyingList == null) {
         this.populateBuyingList();
      }

      return this.buyingList;
   }

   public void setRecipes(MerchantRecipeList recipeList) {
      this.buyingList = recipeList;
      this.livingSoundTime = -this.getTalkInterval();
      this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
   }

   public void useRecipe(@Nonnull MerchantRecipe recipe) {
      recipe.incrementToolUses();
      if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
         this.timeUntilReset = 40;
      }

   }

   public void verifySellingItem(@Nonnull ItemStack stack) {
      if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   @Nonnull
   public World getWorld() {
      return this.world;
   }

   @Nonnull
   public BlockPos getPos() {
      return this.getPosition();
   }

   public boolean isTrading() {
      return this.buyingPlayer != null;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      if (this.buyingList != null) {
         compound.setTag("Offers", this.buyingList.getRecipiesAsTags());
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("Offers", 10)) {
         NBTTagCompound nbttagcompound = compound.getCompoundTag("Offers");
         this.buyingList = new MerchantRecipeList(nbttagcompound);
      }

   }

   protected boolean canDespawn() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADING : SoundEvents.ENTITY_VILLAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VILLAGER_DEATH;
   }
}
