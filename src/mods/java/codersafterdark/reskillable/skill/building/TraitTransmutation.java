package codersafterdark.reskillable.skill.building;

import codersafterdark.reskillable.api.transmutations.TransmutationRegistry;
import codersafterdark.reskillable.api.unlockable.Trait;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class TraitTransmutation extends Trait {
   public TraitTransmutation() {
      super(new ResourceLocation("reskillable", "transmutation"), 3, 2, new ResourceLocation("reskillable", "building"), 8, "reskillable:building|16", "reskillable:magic|16");
      TransmutationRegistry.initDefaultMap();
   }

   public void onRightClickBlock(RightClickBlock event) {
      if (!event.isCanceled()) {
         Item item = event.getItemStack().getItem();
         if (TransmutationRegistry.doesReagentStateMapContainReagentItem(item)) {
            Map<IBlockState, IBlockState> stateMap = TransmutationRegistry.getStateMapByReagent(item);
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            if (stateMap.containsKey(state)) {
               IBlockState placeState = (IBlockState)stateMap.get(state);
               BlockPos pos = event.getPos();
               event.getWorld().setBlockState(pos, placeState);
               SoundEvent sound = placeState.getBlock().getSoundType(placeState, event.getWorld(), pos, (Entity)null).getPlaceSound();
               event.getWorld().playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
               if (event.getWorld().isRemote) {
                  event.getEntityPlayer().swingArm(event.getHand());
                  IntStream.range(0, 20).forEach((i) -> {
                     event.getWorld().spawnParticle(EnumParticleTypes.PORTAL, (double)pos.getX() + Math.random(), (double)pos.getY() + Math.random(), (double)pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                  });
               }

               if (!event.getEntityPlayer().isCreative()) {
                  event.getItemStack().shrink(1);
               }
            }
         }

      }
   }
}
