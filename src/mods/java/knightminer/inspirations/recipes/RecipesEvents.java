package knightminer.inspirations.recipes;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.network.MilkablePacket;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class RecipesEvents {
   public RecipesEvents() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void clickCauldron(RightClickBlock event) {
      if (Config.enableCauldronRecipes) {
         EntityPlayer player = event.getEntityPlayer();
         if (!player.isSneaking()) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (InspirationsRegistry.isNormalCauldron(state)) {
               ItemStack stack = event.getItemStack();
               if (!stack.isEmpty()) {
                  boolean result = TileCauldron.interact(world, pos, state, player, event.getHand());
                  if (result || InspirationsRegistry.isCauldronBlacklist(stack)) {
                     event.setCanceled(true);
                     event.setCancellationResult(EnumActionResult.SUCCESS);
                  }

               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void milkSquid(EntityInteract event) {
      if (Config.milkSquids) {
         Entity target = event.getTarget();
         if (target instanceof EntitySquid) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack stack = player.getHeldItem(event.getHand());
            if (stack.getItem() == Items.GLASS_BOTTLE) {
               NBTTagCompound tags = target.getEntityData();
               if (tags.getShort("milk_cooldown") == 0) {
                  if (!player.capabilities.isCreativeMode) {
                     stack.shrink(1);
                  }

                  ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, EnumDyeColor.BLACK.getDyeDamage()), player.inventory.currentItem);
                  player.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
                  tags.setShort("milk_cooldown", Config.milkSquidCooldown);
                  if (!event.getWorld().isRemote) {
                     InspirationsNetwork.sendToClients(event.getWorld(), target.getPosition(), new MilkablePacket(target, false));
                  }

                  event.setCancellationResult(EnumActionResult.SUCCESS);
                  event.setCanceled(true);
               }
            }

         }
      }
   }
}
