package knightminer.inspirations.utility;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class UtilityEvents {
   public UtilityEvents() {
   }

   @SubscribeEvent
   public static void placeCarpetOnPressurePlate(RightClickBlock event) {
      if (Config.enableCarpetedPressurePlate) {
         ItemStack stack = event.getItemStack();
         if (stack.getItem() == Item.getItemFromBlock(Blocks.CARPET)) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState current = world.getBlockState(pos);
            if (current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
               pos = pos.up();
               current = world.getBlockState(pos);
               if (current.getBlock() != Blocks.STONE_PRESSURE_PLATE) {
                  return;
               }
            }

            int meta = stack.getMetadata();
            EnumDyeColor color = EnumDyeColor.byMetadata(meta);
            IBlockState state;
            if (meta < 8) {
               state = InspirationsUtility.carpetedPressurePlate1.getDefaultState().withProperty(BlockCarpetedPressurePlate.COLOR1, color);
            } else {
               state = InspirationsUtility.carpetedPressurePlate2.getDefaultState().withProperty(BlockCarpetedPressurePlate.COLOR2, color);
            }

            EntityPlayer player = event.getEntityPlayer();
            SoundType sound = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            if (!world.isRemote) {
               world.setBlockState(pos, state);
               if (player instanceof EntityPlayerMP) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
               }

               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }
            }

            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
         }
      }
   }

   @SubscribeEvent
   public static void clickHopperWithPipe(RightClickBlock event) {
      if (Config.enablePipe && event.getItemStack().getItem() == InspirationsUtility.pipeItem) {
         World world = event.getWorld();
         if (!world.isRemote && world.getBlockState(event.getPos()).getBlock() instanceof BlockHopper) {
            event.setUseBlock(Result.DENY);
            event.setUseItem(Result.ALLOW);
         }
      }
   }
}
