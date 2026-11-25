package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.item.ItemGlassShard;

public class GlassShards extends Feature {
   public static Item glass_shard;

   public GlassShards() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      glass_shard = new ItemGlassShard();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.GLASS), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(glass_shard, 1, 0)});

      for(int i = 0; i < 16; ++i) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.STAINED_GLASS, 1, i), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(glass_shard, 1, i + 1)});
      }

      addOreDict("shardGlass", glass_shard);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onDrops(HarvestDropsEvent event) {
      Block block = event.getState().getBlock();
      if (event.getDrops() != null && event.getDrops().isEmpty() && (block == Blocks.GLASS || block == Blocks.STAINED_GLASS) && !event.isSilkTouching()) {
         int meta = 0;
         if (block == Blocks.STAINED_GLASS) {
            meta = block.getMetaFromState(event.getState()) + 1;
         }

         int quantity = MathHelper.clamp(2 + event.getWorld().rand.nextInt(3) + event.getWorld().rand.nextInt(event.getFortuneLevel() + 1), 1, 4);
         event.getDrops().add(ProxyRegistry.newStack(glass_shard, quantity, meta));
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
