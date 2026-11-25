package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIronCanteen extends ItemCanteen {
   public ItemIronCanteen() {
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
      return ServerConfig.instance.getInteger(ServerOptions.IRON_CANTEEN_DOSES);
   }

   public String getUnlocalizedName(ItemStack stack) {
      if (this.isCanteenEmpty(stack)) {
         return "item.simpledifficulty:iron_canteen_empty";
      } else {
         int type = this.getTypeTag(stack).getInt();
         return type >= ThirstEnum.values().length ? "item.simpledifficulty:iron_canteen_broken" : "item.simpledifficulty:iron_canteen_" + ThirstEnum.values()[type].toString();
      }
   }
}
