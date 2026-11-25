package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.util.SoundUtil;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonCanteen extends ItemCanteen {
   public static final String EI_CAPACITY = "capacity";
   public int capacity = 5;

   public ItemDragonCanteen(ExtraItem extraItem) {
      Integer oCapacity = extraItem.getInteger("capacity");
      if (oCapacity != null) {
         this.capacity = oCapacity.intValue();
      }

      this.addPropertyOverride(new ResourceLocation("contain"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            if (stack.getItem() instanceof IItemCanteen) {
               IItemCanteen canteen = (IItemCanteen)stack.getItem();
               if (!canteen.isCanteenEmpty(stack)) {
                  return 1.0F;
               }
            }

            return 0.0F;
         }
      });
   }

   public int getMaxDoses(ItemStack stack) {
      return this.capacity;
   }

   public String getUnlocalizedName(ItemStack stack) {
      if (this.isCanteenEmpty(stack)) {
         return "item.simpledifficulty:dragon_canteen_empty";
      } else {
         int type = this.getTypeTag(stack).getInt();
         return type >= ThirstEnum.values().length ? "item.simpledifficulty:dragon_canteen_broken" : "item.simpledifficulty:dragon_canteen_" + ThirstEnum.values()[type].toString();
      }
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack emptyCanteen = new ItemStack(this, 1, 0);
         this.tryAddDose(emptyCanteen, ThirstEnum.PURIFIED);
         this.setCanteenEmpty(emptyCanteen);
         ItemStack fullCanteen = emptyCanteen.copy();
         this.tryAddDose(fullCanteen, ThirstEnum.PURIFIED);
         this.setCanteenFull(fullCanteen);
         this.customSetTypeTag(emptyCanteen);
         this.customSetTypeTag(fullCanteen);
         items.add(emptyCanteen);
         items.add(fullCanteen);
      }

   }

   @Nullable
   public ThirstEnum getThirstEnum(ItemStack stack) {
      return ThirstEnum.PURIFIED;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (!this.isCanteenFull(stack)) {
         ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
         if (traceBlockPos != null) {
            ThirstEnum trace = traceBlockPos.thirstEnum;
            if (trace == ThirstEnum.PURIFIED) {
               player.world.setBlockToAir(traceBlockPos.pos);
            }

            this.tryAddDose(stack, ThirstEnum.PURIFIED);
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

   protected void customSetTypeTag(ItemStack stack) {
      stack.setTagInfo("CanteenType", new NBTTagInt(ThirstEnum.PURIFIED.ordinal()));
   }
}
