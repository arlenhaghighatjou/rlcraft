package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCustomSpawnEgg extends ItemBase {
   public CreatureType creatureType;

   public ItemCustomSpawnEgg(String name, CreatureType creatureType) {
      this.setUnlocalizedName(name);
      this.setHasSubtypes(true);
      this.setCreativeTab(LycanitesMobs.creaturesTab);
      this.itemName = name;
      this.creatureType = creatureType;
      this.setRegistryName(this.modInfo.modid, this.itemName);
      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new DispenserBehaviorMobEggCustom());
      LycanitesMobs.logDebug("Creature Type", "Created Creature Type Spawn Egg: " + this.itemName);
   }

   public String getItemStackDisplayName(ItemStack itemStack) {
      String displayName = LanguageManager.translate("creaturetype.spawn") + " " + this.creatureType.getTitle() + ": ";
      String creatureName = this.getCreatureName(itemStack);
      if (creatureName != null) {
         CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(creatureName);
         String creatureTitle;
         if (creatureInfo != null) {
            creatureTitle = creatureInfo.getTitle();
         } else {
            creatureTitle = LanguageManager.translate("entity." + creatureName + ".name");
         }

         displayName = displayName + creatureTitle;
      }

      return displayName;
   }

   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String description = this.getDescription(stack, worldIn, tooltip, flagIn);
      if (!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
         FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth(description, ItemBase.DESCRIPTION_WIDTH)) {
            if (formattedDescription instanceof String) {
               tooltip.add("\u00a7a" + formattedDescription);
            }
         }
      }

   }

   public String getDescription(ItemStack itemStack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      CreatureInfo creatureInfo = this.getCreatureInfo(itemStack);
      if (creatureInfo == null) {
         String creatureName = this.getCreatureName(itemStack);
         LycanitesMobs.logWarning("Mob Spawn Egg", "Unable to get Creature Info for id: " + creatureName);
         return "Unable to get Creature Info for id: '" + creatureName + "' this spawn egg may have been created by a give command without NBT data.";
      } else {
         return creatureInfo.getDescription();
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemStack = player.getHeldItem(hand);
      IBlockState blockState = world.getBlockState(pos);
      Block block = blockState.getBlock();
      if (world.isRemote) {
         return EnumActionResult.SUCCESS;
      } else if (!player.canPlayerEdit(pos.offset(facing), facing, itemStack)) {
         return EnumActionResult.FAIL;
      } else if (block == Blocks.MOB_SPAWNER) {
         TileEntity tileEntity = world.getTileEntity(pos);
         MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
         CreatureInfo creatureInfo = this.getCreatureInfo(itemStack);
         if (creatureInfo == null) {
            return EnumActionResult.FAIL;
         } else {
            mobspawnerbaselogic.setEntityId(creatureInfo.getResourceLocation());
            tileEntity.markDirty();
            world.notifyBlockUpdate(pos, blockState, blockState, 3);
            if (!player.capabilities.isCreativeMode) {
               itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }

            return EnumActionResult.SUCCESS;
         }
      } else {
         pos = pos.offset(facing);
         double d0 = 0.0D;
         if (facing == EnumFacing.UP && blockState.getBlock() instanceof BlockFence) {
            d0 = 0.5D;
         }

         EntityLivingBase entity = this.spawnCreature(world, itemStack, (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);
         if (entity != null) {
            if (itemStack.hasDisplayName()) {
               entity.setCustomNameTag(itemStack.getDisplayName());
            }

            this.applyItemEntityDataToEntity(world, player, itemStack, entity);
            if (!player.capabilities.isCreativeMode) {
               itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }
         }

         return EnumActionResult.SUCCESS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (world.isRemote) {
         return new ActionResult(EnumActionResult.PASS, itemStack);
      } else {
         RayTraceResult rayTraceResult = this.rayTrace(world, player, true);
         if (rayTraceResult == null) {
            return new ActionResult(EnumActionResult.PASS, itemStack);
         } else {
            if (rayTraceResult.typeOfHit == Type.BLOCK) {
               BlockPos pos = rayTraceResult.getBlockPos();
               if (!world.canMineBlockBody(player, pos)) {
                  return new ActionResult(EnumActionResult.FAIL, itemStack);
               }

               if (!player.canPlayerEdit(pos, rayTraceResult.sideHit, itemStack)) {
                  return new ActionResult(EnumActionResult.PASS, itemStack);
               }

               if (world.getBlockState(pos).getMaterial() == Material.WATER) {
                  EntityLivingBase entity = this.spawnCreature(world, itemStack, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                  if (entity != null && itemStack.hasDisplayName()) {
                     entity.setCustomNameTag(itemStack.getDisplayName());
                  }

                  if (!player.capabilities.isCreativeMode) {
                     itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
                  }
               }
            }

            return new ActionResult(EnumActionResult.SUCCESS, itemStack);
         }
      }
   }

   public boolean useItemColors() {
      return true;
   }

   public int getColorFromItemstack(ItemStack itemStack, int tintIndex) {
      CreatureInfo creatureInfo = this.getCreatureInfo(itemStack);
      return creatureInfo != null ? (tintIndex == 0 ? creatureInfo.eggBackColor : creatureInfo.eggForeColor) : 16777215;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.modInfo != null && this.creatureType != null && this.isInCreativeTab(tab)) {
         for(CreatureInfo creatureInfo : this.creatureType.creatures.values()) {
            ItemStack itemstack = new ItemStack(this, 1);
            this.applyCreatureInfoToItemStack(itemstack, creatureInfo);
            items.add(itemstack);
         }

      }
   }

   public CreatureInfo getCreatureInfo(ItemStack itemStack) {
      String creatureName = this.getCreatureName(itemStack);
      return CreatureManager.getInstance().getCreature(creatureName);
   }

   public String getCreatureName(ItemStack itemStack) {
      NBTTagCompound itemStackNBT = itemStack.getTagCompound();
      if (itemStackNBT != null && itemStackNBT.hasKey("CreatureInfoSpawnEgg", 10)) {
         NBTTagCompound spawnEggNBT = itemStackNBT.getCompoundTag("CreatureInfoSpawnEgg");
         return !spawnEggNBT.hasKey("creaturename", 8) ? null : spawnEggNBT.getString("creaturename");
      } else {
         return null;
      }
   }

   public EntityLivingBase spawnCreature(World world, ItemStack itemStack, double x, double y, double z) {
      CreatureInfo creatureInfo = this.getCreatureInfo(itemStack);
      if (creatureInfo == null) {
         return null;
      } else {
         EntityLivingBase entity = creatureInfo.createEntity(world);
         if (entity != null) {
            entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
            entity.rotationYawHead = entity.rotationYaw;
            entity.renderYawOffset = entity.rotationYaw;
            if (entity instanceof EntityLiving) {
               EntityLiving entityliving = (EntityLiving)entity;
               entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
               entityliving.playLivingSound();
            }

            world.spawnEntity(entity);
         }

         return entity;
      }
   }

   public void applyCreatureInfoToItemStack(ItemStack itemStack, CreatureInfo creatureInfo) {
      NBTTagCompound itemStackNBT = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
      NBTTagCompound spawnEggNBT = new NBTTagCompound();
      spawnEggNBT.setString("creaturename", creatureInfo.getName());
      itemStackNBT.setTag("CreatureInfoSpawnEgg", spawnEggNBT);
      itemStack.setTagCompound(itemStackNBT);
   }

   public void applyItemEntityDataToEntity(World entityWorld, EntityPlayer player, ItemStack stack, Entity targetEntity) {
      MinecraftServer minecraftserver = entityWorld.getMinecraftServer();
      if (minecraftserver != null && targetEntity != null) {
         NBTTagCompound nbttagcompound = stack.getTagCompound();
         if (nbttagcompound != null && nbttagcompound.hasKey("CreatureInfoSpawnEgg", 10)) {
            if (!entityWorld.isRemote && targetEntity.ignoreItemEntityData() && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile()))) {
               return;
            }

            NBTTagCompound entityNBT = new NBTTagCompound();
            targetEntity.writeToNBT(entityNBT);
            EaglercraftUUID uuid = targetEntity.getUniqueID();
            entityNBT.merge(nbttagcompound.getCompoundTag("CreatureInfoSpawnEgg"));
            targetEntity.setUniqueId(uuid);
            targetEntity.readFromNBT(entityNBT);
         }
      }

   }
}
