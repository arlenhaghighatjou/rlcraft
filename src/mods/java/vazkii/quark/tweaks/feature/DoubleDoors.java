package vazkii.quark.tweaks.feature;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.ai.EntityAIOpenDoubleDoor;

public class DoubleDoors extends Feature {
   public static boolean allowVillagers = true;

   public DoubleDoors() {
   }

   public void setupConfig() {
      allowVillagers = this.loadPropBool("Allow Villagers to use Double Doors", "", allowVillagers);
   }

   @SubscribeEvent
   public void onEntityTick(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityVillager && allowVillagers) {
         EntityVillager villager = (EntityVillager)event.getEntity();
         Iterator<EntityAITaskEntry> it = villager.tasks.taskEntries.iterator();

         while(it.hasNext()) {
            EntityAIBase te = ((EntityAITaskEntry)it.next()).action;
            if (te instanceof EntityAIOpenDoubleDoor) {
               return;
            }

            if (te instanceof EntityAIOpenDoor) {
               it.remove();
               villager.tasks.addTask(4, new EntityAIOpenDoubleDoor(villager, true));
               return;
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPlayerInteract(RightClickBlock event) {
      if (!event.getEntityPlayer().isSneaking() && !event.isCanceled() && event.getResult() != Result.DENY && event.getUseBlock() != Result.DENY) {
         World world = event.getWorld();
         IBlockState state = world.getBlockState(event.getPos()).getActualState(world, event.getPos());
         Block block = state.getBlock();
         if (block instanceof BlockDoor) {
            EnumFacing direction = (EnumFacing)state.getValue(BlockDoor.FACING);
            boolean isOpen = ((Boolean)state.getValue(BlockDoor.OPEN)).booleanValue();
            EnumHingePosition isMirrored = (EnumHingePosition)state.getValue(BlockDoor.HINGE);
            BlockPos mirrorPos = event.getPos().offset(isMirrored == EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
            BlockPos doorPos = state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
            IBlockState other = world.getBlockState(doorPos).getActualState(world, doorPos);
            if (state.getMaterial() != Material.IRON && other.getBlock() == block && other.getValue(BlockDoor.FACING) == direction && ((Boolean)other.getValue(BlockDoor.OPEN)).booleanValue() == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {
               IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
               world.setBlockState(doorPos, newState, 10);
            }

         }
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"malisisdoors"};
   }
}
