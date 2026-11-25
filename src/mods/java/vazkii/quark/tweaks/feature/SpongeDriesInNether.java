package vazkii.quark.tweaks.feature;

import net.minecraft.block.BlockSponge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.sounds.QuarkSounds;

public class SpongeDriesInNether extends Feature {
   public SpongeDriesInNether() {
   }

   @SubscribeEvent
   public void onItemPlaced(PlaceEvent event) {
      if (event.getPlacedBlock().equals(Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(true))) && BiomeDictionary.getTypes(event.getWorld().getBiome(event.getPos())).contains(Type.NETHER)) {
         World world = event.getWorld();
         world.playSound((EntityPlayer)null, event.getPos(), QuarkSounds.BLOCK_SPONGE_HISS, SoundCategory.BLOCKS, 1.0F, 2.4F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.9F);
         world.setBlockState(event.getPos(), Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(false)));
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Sponges Dry In Nether";
   }
}
