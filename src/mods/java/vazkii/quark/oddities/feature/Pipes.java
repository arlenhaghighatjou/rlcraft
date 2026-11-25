package vazkii.quark.oddities.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.oddities.block.BlockPipe;
import vazkii.quark.oddities.client.render.RenderTilePipe;
import vazkii.quark.oddities.tile.TilePipe;

public class Pipes extends Feature {
   public static Block pipe;
   public static int pipeSpeed;
   public static int maxPipeItems;
   public static int pipesCrafted;
   public static boolean enableRender;
   public static boolean doPipesWhoosh;

   public Pipes() {
   }

   public void setupConfig() {
      pipeSpeed = this.loadPropInt("Pipe Speed", "How long it takes for an item to cross a pipe. Bigger = slower.", 5) * 2;
      maxPipeItems = this.loadPropInt("Max Pipe Items", "Set to 0 if you don't want pipes to have a max amount of items", 16);
      pipesCrafted = this.loadPropInt("Pipes Crafted", "", 6);
      enableRender = this.loadPropBool("Enable Pipe Render", "Feel free to disable so you don't see items going through pipes, good if your PC is a potato", true);
      doPipesWhoosh = this.loadPropBool("Enable Pipe Sounds", "When items eject or are absorbed by pipes, should they make sounds?", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      pipe = new BlockPipe();
      registerTile(TilePipe.class, "pipe");
   }

   public void postPreInit() {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pipe, pipesCrafted), new Object[]{"I", "G", "I", 'I', "ingotIron", 'G', "blockGlass"});
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      if (enableRender) {
         ClientRegistry.bindTileEntitySpecialRenderer(TilePipe.class, new RenderTilePipe());
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
