package vazkii.quark.decoration.feature;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockRope;

public class Rope extends Feature {
   public static Block rope;
   public static boolean forceEnableMoveTEs;
   public static int recipeCount;
   public static boolean enableDispenser;

   public Rope() {
   }

   public void setupConfig() {
      forceEnableMoveTEs = this.loadPropBool("Force Enable Move TEs", "Set to true to allow ropes to move Tile Entities even if Pistons Push TEs is disabled.\nNote that ropes will still use the same blacklist.", false);
      recipeCount = this.loadPropInt("Recipe Output", "", 2);
      enableDispenser = this.loadPropBool("Enable Dispenser", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      rope = new BlockRope();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(rope, recipeCount), new Object[]{"SSS", "S S", "SSS", 'S', "string"});
   }

   public void init() {
      if (enableDispenser) {
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(rope), new Rope.BehaviourRope());
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class BehaviourRope extends BehaviorDispenseOptional {
      public BehaviourRope() {
      }

      @Nonnull
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing facing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
         BlockPos pos = source.getBlockPos().offset(facing);
         World world = source.getWorld();
         this.successful = false;
         IBlockState state = world.getBlockState(pos);
         if (state.getBlock() == Rope.rope) {
            if (((BlockRope)Rope.rope).pullDown(world, pos)) {
               this.successful = true;
               stack.shrink(1);
               return stack;
            }
         } else if (world.isAirBlock(pos) && Rope.rope.canPlaceBlockAt(world, pos)) {
            SoundType soundtype = Rope.rope.getSoundType(state, world, pos, (Entity)null);
            world.setBlockState(pos, Rope.rope.getDefaultState());
            world.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            this.successful = true;
            stack.shrink(1);
            return stack;
         }

         return stack;
      }
   }
}
