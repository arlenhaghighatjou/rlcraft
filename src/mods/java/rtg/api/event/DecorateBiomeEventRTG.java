package rtg.api.event;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

public class DecorateBiomeEventRTG extends DecorateBiomeEvent {
   public DecorateBiomeEventRTG(World world, EaglercraftRandom rand, BlockPos pos) {
      super(world, rand, pos);
   }

   @HasResult
   public static class DecorateRTG extends Decorate {
      private final EventType type;
      private int amount;

      public DecorateRTG(World world, EaglercraftRandom rand, BlockPos pos, EventType type, int amount) {
         super(world, rand, pos, type);
         this.amount = -1;
         this.type = type;
         this.amount = amount;
      }

      public DecorateRTG(World world, EaglercraftRandom rand, BlockPos pos, EventType type) {
         this(world, rand, pos, type, -1);
      }

      public boolean hasAmountData() {
         return this.amount != -1;
      }

      public int getModifiedAmount() {
         return this.amount;
      }

      public void setModifiedAmount(int amount) {
         this.amount = amount;
      }
   }
}
