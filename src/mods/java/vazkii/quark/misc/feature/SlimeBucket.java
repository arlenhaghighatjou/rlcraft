package vazkii.quark.misc.feature;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemSlimeBucket;

public class SlimeBucket extends Feature {
   public static boolean oredict;
   public static Item slime_bucket;

   public SlimeBucket() {
   }

   public void setupConfig() {
      oredict = this.loadPropBool("Slime bucket is a slimeball", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      slime_bucket = new ItemSlimeBucket();
      if (oredict) {
         addOreDict("slimeball", slime_bucket);
      }

   }

   @SubscribeEvent
   public void entityInteract(EntityInteract event) {
      if (event.getTarget() != null && !event.getWorld().isRemote) {
         String name = EntityList.getEntityString(event.getTarget());
         if (name != null && name.equals("Slime") && ((EntitySlime)event.getTarget()).getSlimeSize() == 1 && ((EntityLiving)event.getTarget()).getHealth() > 0.0F) {
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = EnumHand.MAIN_HAND;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty() || stack.getItem() != Items.BUCKET) {
               stack = player.getHeldItemOffhand();
               hand = EnumHand.OFF_HAND;
            }

            if (!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
               ItemStack outStack = ProxyRegistry.newStack(slime_bucket);
               NBTTagCompound cmp = event.getTarget().serializeNBT();
               ItemNBTHelper.setCompound(outStack, "slime_nbt", cmp);
               if (stack.getCount() == 1) {
                  player.setHeldItem(hand, outStack);
               } else {
                  stack.shrink(1);
                  if (stack.getCount() == 0) {
                     player.setHeldItem(hand, outStack);
                  } else if (!player.inventory.addItemStackToInventory(outStack)) {
                     player.dropItem(outStack, false);
                  }
               }

               player.swingArm(hand);
               event.getTarget().setDead();
            }
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
