package com.alcatrazescapee.notreepunching;

import com.alcatrazescapee.alcatrazcore.util.RegistryHelper;
import com.alcatrazescapee.notreepunching.common.capability.CapabilityPlayerItem;
import com.alcatrazescapee.notreepunching.common.capability.IPlayerItem;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import com.alcatrazescapee.notreepunching.util.HarvestBlockHandler;
import com.alcatrazescapee.notreepunching.util.PlayerInteractionHandler;
import com.alcatrazescapee.notreepunching.util.WoodRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(
   modid = "notreepunching"
)
public final class ModEventHandler {
   public ModEventHandler() {
   }

   @SubscribeEvent
   public static void breakEvent(BreakEvent event) {
      EntityPlayer player = event.getPlayer();
      if (player != null) {
         IPlayerItem cap = (IPlayerItem)player.getCapability(CapabilityPlayerItem.CAPABILITY, (EnumFacing)null);
         if (cap != null) {
            cap.setStack(player.getHeldItemMainhand());
         }
      }

   }

   @SubscribeEvent
   public static void breakSpeed(BreakSpeed event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null && !(player instanceof FakePlayer) && !player.isCreative() && ModConfig.GENERAL.enableBreakingChanges) {
         ItemStack stack = player.getHeldItemMainhand();
         IBlockState state = event.getState();
         if (HarvestBlockHandler.isInvalidTool(stack, player, state)) {
            String toolClass = state.getBlock().getHarvestTool(state);
            event.setNewSpeed(event.getOriginalSpeed() * HarvestBlockHandler.getSpeedModifier(toolClass == null ? "" : toolClass));
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onHarvestBlock(HarvestDropsEvent event) {
      EntityPlayer player = event.getHarvester();
      if (!event.getWorld().isRemote && player != null && !(player instanceof FakePlayer) && !player.isCreative() && ModConfig.GENERAL.enableBreakingChanges) {
         IBlockState state = event.getState();
         IPlayerItem cap = (IPlayerItem)player.getCapability(CapabilityPlayerItem.CAPABILITY, (EnumFacing)null);
         ItemStack stack = ItemStack.EMPTY;
         if (cap != null) {
            stack = cap.getStack();
         }

         if (stack.isEmpty()) {
            stack = player.getHeldItemMainhand();
         }

         HarvestBlockHandler.addExtraDrops(event.getDrops(), state, player, stack, event.isSilkTouching());
         if (HarvestBlockHandler.isInvalidTool(stack, player, state)) {
            event.getDrops().clear();
         }
      }

   }

   @SubscribeEvent
   public static void playerInteractEvent(RightClickBlock event) {
      ItemStack stack = event.getItemStack();
      EntityPlayer player = event.getEntityPlayer();
      if (event.getHand() == EnumHand.OFF_HAND) {
         ItemStack mainStack = player.getHeldItem(EnumHand.MAIN_HAND);
         if (PlayerInteractionHandler.hasAction(event.getWorld(), event.getPos(), mainStack, event.getFace())) {
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
            return;
         }
      }

      if (PlayerInteractionHandler.hasAction(event.getWorld(), event.getPos(), stack, event.getFace()) && PlayerInteractionHandler.performAction(event.getWorld(), event.getPos(), player, stack, event.getFace(), event.getHand())) {
         event.setCancellationResult(EnumActionResult.SUCCESS);
         event.setCanceled(true);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void registerRecipes(Register<IRecipe> event) {
      ModRecipes.registerRecipes(event);
      WoodRecipeHandler.registerRecipes(event);
   }

   @SubscribeEvent
   public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(CapabilityPlayerItem.KEY, new CapabilityPlayerItem.Instance());
      }

   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      RegistryHelper.get("notreepunching").initItems(event);
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      RegistryHelper.get("notreepunching").initBlocks(event);
   }

   @SubscribeEvent
   public static void registerSounds(Register<SoundEvent> event) {
      RegistryHelper.get("notreepunching").initSounds(event);
   }

   @SubscribeEvent
   public void harvestBlockInitialCheck(HarvestCheck event) {
      if (HarvestBlockHandler.isWhitelisted(event.getTargetBlock())) {
         event.setCanHarvest(true);
      }

   }

   @SubscribeEvent
   public static void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("notreepunching")) {
         ConfigManager.sync("notreepunching", Type.INSTANCE);
         HarvestBlockHandler.reloadWhitelist();
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void registerModels(ModelRegistryEvent event) {
      RegistryHelper.get("notreepunching").initModels(event);
   }
}
