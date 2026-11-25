package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCanteen extends ItemDrinkBase implements IItemCanteen {
   public static final String CANTEENTYPE = "CanteenType";
   public static final String DOSES = "Doses";

   public ItemCanteen() {
      this.setMaxStackSize(1);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack emptyCanteen = new ItemStack(this, 1, 0);
         this.createTypeTag(emptyCanteen);
         this.setCanteenEmpty(emptyCanteen);
         ItemStack fullCanteen = emptyCanteen.copy();
         this.setCanteenFull(fullCanteen);
         ItemStack purifiedCanteen = fullCanteen.copy();
         this.setTypeTag(purifiedCanteen, ThirstEnum.PURIFIED.ordinal());
         items.add(emptyCanteen);
         items.add(fullCanteen);
         items.add(purifiedCanteen);
      }

   }

   public String getUnlocalizedName(ItemStack stack) {
      if (this.isCanteenEmpty(stack)) {
         return "item.simpledifficulty:canteen_empty";
      } else {
         int type = this.getTypeTag(stack).getInt();
         return type >= ThirstEnum.values().length ? "item.simpledifficulty:canteen_broken" : "item.simpledifficulty:canteen_" + ThirstEnum.values()[type].toString();
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      int typetag = this.getTypeTag(stack).getInt();
      if (!this.isCanteenFull(stack) || typetag == ThirstEnum.NORMAL.ordinal()) {
         ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
         if (traceBlockPos != null) {
            ThirstEnum trace = traceBlockPos.thirstEnum;
            if (trace == ThirstEnum.PURIFIED) {
               player.world.setBlockToAir(traceBlockPos.pos);
            }

            if (trace == ThirstEnum.RAIN) {
               trace = ThirstEnum.NORMAL;
            }

            this.tryAddDose(stack, trace);
            SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
            player.setActiveHand(hand);
            player.swingArm(hand);
            player.stopActiveHand();
            return new ActionResult(EnumActionResult.SUCCESS, stack);
         }
      }

      if (!this.isCanteenEmpty(stack)) {
         IThirstCapability capability = SDCapabilities.getThirstData(player);
         if (capability.isThirsty() || !QuickConfig.isThirstEnabled()) {
            player.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, stack);
         }
      }

      return new ActionResult(EnumActionResult.FAIL, stack);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
      if (!world.isRemote && entityLiving instanceof EntityPlayer) {
         if (this.isCanteenEmpty(stack)) {
            return stack;
         } else {
            EntityPlayer player = (EntityPlayer)entityLiving;
            ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
            this.removeDose(stack);
            return stack;
         }
      } else {
         return stack;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
      int max = this.getMaxDoses(stack);
      tooltip.add(I18n.format("item.durability", new Object[]{this.getDoses(stack), max}));
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      double max = (double)this.getMaxDoses(stack);
      return max == 0.0D ? 1.0D : (max - (double)this.getDoses(stack)) / max;
   }

   public boolean showDurabilityBar(ItemStack stack) {
      return !this.isCanteenEmpty(stack);
   }

   public int getThirstLevel(ItemStack stack) {
      ThirstEnum thirstEnum = this.getThirstEnum(stack);
      return thirstEnum == null ? 0 : thirstEnum.getThirst();
   }

   public float getSaturationLevel(ItemStack stack) {
      ThirstEnum thirstEnum = this.getThirstEnum(stack);
      return thirstEnum == null ? 0.0F : thirstEnum.getSaturation();
   }

   public float getDirtyChance(ItemStack stack) {
      ThirstEnum thirstEnum = this.getThirstEnum(stack);
      return thirstEnum == null ? 0.0F : thirstEnum.getThirstyChance();
   }

   @Nullable
   public ThirstEnum getThirstEnum(ItemStack stack) {
      int type = this.getTypeTag(stack).getInt();
      return type >= ThirstEnum.values().length ? null : ThirstEnum.values()[type];
   }

   protected NBTTagInt getTypeTag(ItemStack stack) {
      if (stack.getTagCompound() == null) {
         this.createTypeTag(stack);
         this.setCanteenEmpty(stack);
      }

      NBTBase tag = stack.getTagCompound().getTag("CanteenType");
      if (tag instanceof NBTTagInt) {
         return (NBTTagInt)tag;
      } else {
         stack.getTagCompound().removeTag("CanteenType");
         this.createTypeTag(stack);
         return new NBTTagInt(ThirstEnum.NORMAL.ordinal());
      }
   }

   protected void setTypeTag(ItemStack stack, ThirstEnum thirstEnum) {
      this.setTypeTag(stack, thirstEnum.ordinal());
   }

   protected void setTypeTag(ItemStack stack, int tag) {
      stack.setTagInfo("CanteenType", new NBTTagInt(tag));
   }

   protected void createTypeTag(ItemStack stack) {
      this.setTypeTag(stack, ThirstEnum.NORMAL.ordinal());
   }

   protected NBTTagInt getDosesTag(ItemStack stack) {
      if (stack.getTagCompound() == null) {
         this.createDosesTag(stack);
         this.setCanteenEmpty(stack);
      }

      NBTBase tag = stack.getTagCompound().getTag("Doses");
      if (tag instanceof NBTTagInt) {
         return (NBTTagInt)tag;
      } else {
         stack.getTagCompound().removeTag("Doses");
         this.createDosesTag(stack);
         return new NBTTagInt(0);
      }
   }

   protected void setDosesTag(ItemStack stack, int doses) {
      stack.setTagInfo("Doses", new NBTTagInt(doses));
   }

   protected void createDosesTag(ItemStack stack) {
      this.setDosesTag(stack, 0);
   }

   public int getDoses(ItemStack stack) {
      return this.getDosesTag(stack).getInt();
   }

   public int getMaxDoses(ItemStack stack) {
      return ServerConfig.instance.getInteger(ServerOptions.CANTEEN_DOSES);
   }

   public boolean isCanteenFull(ItemStack stack) {
      return this.getDoses(stack) >= this.getMaxDoses(stack);
   }

   public boolean isCanteenEmpty(ItemStack stack) {
      return this.getDoses(stack) <= 0;
   }

   public void setCanteenFull(ItemStack stack) {
      this.setDosesInternal(stack, this.getMaxDoses(stack));
   }

   public void setCanteenEmpty(ItemStack stack) {
      this.setDosesInternal(stack, 0);
   }

   public void removeDose(ItemStack stack) {
      if (!this.isCanteenEmpty(stack)) {
         this.setDosesInternal(stack, this.getDoses(stack) - 1);
      }

   }

   public void setDoses(ItemStack stack, int amount) {
      this.setDosesInternal(stack, amount);
   }

   public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount) {
      this.formatCanteen(stack, thirstEnum);
      this.setDosesInternal(stack, amount);
   }

   public boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum) {
      int oldDamage = this.getDoses(stack);
      if (oldDamage < 0) {
         oldDamage = 0;
      }

      boolean format = this.formatCanteen(stack, thirstEnum);
      if (thirstEnum == ThirstEnum.NORMAL) {
         this.setDosesInternal(stack, this.getMaxDoses(stack));
      } else {
         this.setDosesInternal(stack, this.getDoses(stack) + 1);
      }

      return format || this.getDoses(stack) != oldDamage;
   }

   protected boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum) {
      if (thirstEnum != this.getThirstEnum(stack)) {
         this.setCanteenEmpty(stack);
         this.setTypeTag(stack, thirstEnum);
         return true;
      } else {
         this.getDoses(stack);
         return false;
      }
   }

   protected void setDosesInternal(ItemStack stack, int amount) {
      if (amount <= 0) {
         this.setDosesTag(stack, 0);
      } else {
         int max = this.getMaxDoses(stack);
         if (amount > max) {
            this.setDosesTag(stack, max);
         } else {
            this.setDosesTag(stack, amount);
         }
      }
   }
}
