package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.EatBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.ItemDrop;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class EntityYale extends AgeableCreatureEntity implements IShearable {
   protected static final DataParameter<Byte> FUR = EntityDataManager.createKey(EntityYale.class, DataSerializers.BYTE);
   public ItemDrop woolDrop;
   private final InventoryCrafting colorMixer = new InventoryCrafting(new Container() {
      public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
         return false;
      }
   }, 2, 1);

   public EntityYale(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
      this.woolDrop = (new ItemDrop(Blocks.WOOL.getRegistryName().toString(), 0, 1.0F)).setMinAmount(1).setMaxAmount(3);
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextIdleGoalIndex++, (new EatBlockGoal(this)).setBlocks(Blocks.GRASS).setReplaceBlock(Blocks.DIRT));
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(FUR, Byte.valueOf((byte)1));
   }

   public void onFirstSpawn() {
      if (!this.isChild()) {
         this.setColor(this.getRandomFurColor(this.getRNG()));
      }

      super.onFirstSpawn();
   }

   public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
      return this.hasFur() && !this.isChild();
   }

   public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      ArrayList<ItemStack> dropStacks = new ArrayList();
      if (this.woolDrop == null) {
         return dropStacks;
      } else {
         this.setFur(false);
         this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
         int quantity = this.woolDrop.getQuantity(this.getRNG(), fortune, 1);
         ItemStack dropStack = this.woolDrop.getEntityDropItemStack(this, quantity);
         this.dropItem(dropStack);
         dropStacks.add(dropStack);
         return dropStacks;
      }
   }

   public boolean hasFur() {
      if (this.dataManager == null) {
         return true;
      } else {
         return ((Byte)this.dataManager.get(FUR)).byteValue() > 0;
      }
   }

   public void setFur(boolean fur) {
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(FUR, Byte.valueOf((byte)(fur ? 1 : 0)));
      }

   }

   public void onEat() {
      if (!this.getEntityWorld().isRemote) {
         this.setFur(true);
      }

   }

   public boolean canBeColored(EntityPlayer player) {
      return true;
   }

   public void setColor(int color) {
      if (this.woolDrop == null) {
         this.woolDrop = (new ItemDrop(Blocks.WOOL.getRegistryName().toString(), 0, 1.0F)).setMinAmount(1).setMaxAmount(3);
      } else if (this.woolDrop.getItemStack().getItem() == Item.getItemFromBlock(Blocks.WOOL)) {
         this.woolDrop.setDrop(new ItemStack(Blocks.WOOL, 1, color));
      }

      super.setColor(color);
   }

   public int getRandomFurColor(Random random) {
      int i = random.nextInt(100);
      return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
   }

   public int getMixedFurColor(BaseCreatureEntity entityA, BaseCreatureEntity entityB) {
      int i = 15 - entityA.getColor();
      int j = 15 - entityB.getColor();
      if (i == j) {
         return 15 - i;
      } else {
         this.colorMixer.getStackInSlot(0).setItemDamage(i);
         this.colorMixer.getStackInSlot(1).setItemDamage(j);
         ItemStack itemstack = CraftingManager.findMatchingResult(this.colorMixer, entityA.world);
         int k;
         if (!itemstack.isEmpty() && itemstack.getItem() == Items.DYE) {
            k = itemstack.getItemDamage();
         } else {
            k = this.getEntityWorld().rand.nextBoolean() ? i : j;
         }

         return 15 - k;
      }
   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      Block block = blockState.getBlock();
      if (block != Blocks.AIR) {
         if (blockState.getMaterial() == Material.GRASS) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.GROUND) {
            return 7.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 50.0F;
   }

   public boolean canDropItem(ItemDrop itemDrop) {
      if (!super.canDropItem(itemDrop)) {
         return false;
      } else {
         return itemDrop.getItemStack().getItem() instanceof ItemBlock && ((ItemBlock)itemDrop.getItemStack().getItem()).getBlock() == Blocks.WOOL ? this.hasFur() : true;
      }
   }

   public void dropItem(ItemStack itemStack) {
      if (this.woolDrop != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() == Blocks.WOOL) {
         itemStack = new ItemStack(itemStack.getItem(), itemStack.getCount(), this.woolDrop.getItemStack().getMetadata());
      }

      super.dropItem(itemStack);
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
      AgeableCreatureEntity baby = new EntityYale(this.getEntityWorld());
      int color = this.getMixedFurColor(this, partner);
      baby.setColor(color);
      return baby;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      super.readEntityFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("HasFur")) {
         this.setFur(nbtTagCompound.getBoolean("HasFur"));
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setBoolean("HasFur", this.hasFur());
   }
}
