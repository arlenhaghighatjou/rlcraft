package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import javax.annotation.Nullable;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeParticle;
import team.creative.enhancedvisuals.client.VisualManager;

public class SandSplatHandler extends VisualHandler {
   @CreativeConfig
   public IntMinMax duration = new IntMinMax(100, 100);
   @CreativeConfig
   public double sprintModifier = 1.5D;
   @CreativeConfig
   public VisualType sand = new VisualTypeParticle("sand", 0, new DecimalMinMax(0.1D, 0.5D));

   public SandSplatHandler() {
   }

   public void tick(@Nullable EntityPlayer player) {
      if (player != null && player.onGround && this.isOnSand(player)) {
         double modifier = 0.0D;
         if (player.isSprinting()) {
            modifier = this.sprintModifier;
         }

         VisualManager.addParticlesFadeOut(this.sand, this, (int)(Math.random() * modifier), this.duration, true);
      }

   }

   private boolean isOnSand(EntityPlayer player) {
      BlockPos pos = player.getPosition().down();
      return player.world.getBlockState(pos).getBlock() instanceof BlockSand;
   }
}
