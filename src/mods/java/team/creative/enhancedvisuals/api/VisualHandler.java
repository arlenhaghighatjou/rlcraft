package team.creative.enhancedvisuals.api;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import com.creativemd.creativecore.common.config.api.CreativeConfig.DecimalRange;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.creative.enhancedvisuals.client.EVClient;
import team.creative.enhancedvisuals.client.sound.PositionedSound;
import team.creative.enhancedvisuals.client.sound.TickedSound;

public class VisualHandler implements ICreativeConfig {
   @CreativeConfig
   public boolean enabled = true;
   @CreativeConfig
   @DecimalRange(
      max = 1.0D,
      min = 0.0D
   )
   public float opacity = 1.0F;

   public VisualHandler() {
   }

   public void configured() {
   }

   public void tick(@Nullable EntityPlayer player) {
   }

   public boolean isEnabled(@Nullable EntityPlayer player) {
      return this.enabled && this.opacity > 0.0F;
   }

   @SideOnly(Side.CLIENT)
   public synchronized void playSound(ResourceLocation location) {
      this.playSound(location, (BlockPos)null, 1.0F);
   }

   @SideOnly(Side.CLIENT)
   public synchronized void playSound(ResourceLocation location, BlockPos pos) {
      this.playSound(location, pos, 1.0F);
   }

   @SideOnly(Side.CLIENT)
   public synchronized void playSound(ResourceLocation location, float volume) {
      this.playSound(location, (BlockPos)null, volume);
   }

   @SideOnly(Side.CLIENT)
   public synchronized void playSound(ResourceLocation location, BlockPos pos, float volume) {
      if (EVClient.shouldRender()) {
         if (pos != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSound(location, SoundCategory.MASTER, volume, 1.0F, pos));
         } else {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSound(location, SoundCategory.MASTER, volume, 1.0F));
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public synchronized void playSoundFadeOut(ResourceLocation location, BlockPos pos, DecimalCurve volume) {
      if (EVClient.shouldRender()) {
         if (pos != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new TickedSound(location, SoundCategory.MASTER, 1.0F, pos, volume));
         } else {
            Minecraft.getMinecraft().getSoundHandler().playSound(new TickedSound(location, SoundCategory.MASTER, 1.0F, volume));
         }

      }
   }
}
