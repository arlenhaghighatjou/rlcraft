package ivorius.reccomplex.client;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RCProxy;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.item.RCItems;
import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements RCProxy {
   public static final String[] VALID_MODIFIER_KEYS = new String[]{"ctrl", "lctrl", "rctrl", "shift", "lshift", "rshift"};

   public ClientProxy() {
   }

   public File getDataDirectory() {
      return Minecraft.getMinecraft().mcDataDir;
   }

   public void loadConfig(String configID) {
      if (configID == null || configID.equals("visual")) {
         RCConfig.hideRedundantNegativeSpace = RecurrentComplex.config.getBoolean("hideRedundantNegativeSpace", "visual", true, "Only show the edges of negative space blocks? (Improves performance in big builds)");
      }

      if (configID == null || configID.equals("controls")) {
         RCConfig.blockSelectorModifierKeys = this.parseModifierKeys(RecurrentComplex.config.getString("blockSelectorModifierKeys", "controls", "ctrl", "The key to be held when you want to make a secondary selection with block selectors", VALID_MODIFIER_KEYS));
      }

   }

   private int[] parseModifierKeys(String key) {
      byte var3 = -1;
      switch(key.hashCode()) {
      case -1093611626:
         if (key.equals("lshift")) {
            var3 = 5;
         }
         break;
      case -921836720:
         if (key.equals("rshift")) {
            var3 = 6;
         }
         break;
      case 3064427:
         if (key.equals("ctrl")) {
            var3 = 1;
         }
         break;
      case 102804695:
         if (key.equals("lctrl")) {
            var3 = 2;
         }
         break;
      case 108345821:
         if (key.equals("rctrl")) {
            var3 = 3;
         }
         break;
      case 109407362:
         if (key.equals("shift")) {
            var3 = 4;
         }
      }

      switch(var3) {
      case 1:
      default:
         return new int[]{29, 157};
      case 2:
         return new int[]{29};
      case 3:
         return new int[]{157};
      case 4:
         return new int[]{42, 54};
      case 5:
         return new int[]{42};
      case 6:
         return new int[]{54};
      }
   }

   public void registerRenderers() {
      if (!RecurrentComplex.isLite()) {
         this.registerItemsForDefaultRender(RCItems.blockSelector, RCItems.blockSelectorFloating);
         this.registerItemsForDefaultRender(RCItems.lootGenerationTag, RCItems.lootGenerationSingleTag, RCItems.lootGenerationComponentTag);
         this.registerItemsForDefaultRender(RCItems.artifactGenerationTag, RCItems.bookGenerationTag);
         this.registerItemsForDefaultRender(RCItems.inspector);
         this.registerTypeItemsForDefaultRender(RCBlocks.genericSpace, RCBlocks.genericSolid);
         this.registerItemsForDefaultRender(RCBlocks.structureGenerator, RCBlocks.mazeGenerator, RCBlocks.spawnCommands, RCBlocks.spawnScript);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      RCCommands.registerClientCommands(ClientCommandHandler.instance);
   }

   protected void registerTypeItemsForDefaultRender(Block... blocks) {
      for(Block block : blocks) {
         this.registerTypeItemForDefaultRender(block);
      }

   }

   protected void registerItemsForDefaultRender(Block... blocks) {
      for(Block block : blocks) {
         this.registerItemForDefaultRender(block);
      }

   }

   protected void registerItemsForDefaultRender(Item... items) {
      for(Item item : items) {
         this.registerItemForDefaultRender(item);
      }

   }

   protected void registerItemForDefaultRender(Block block) {
      this.registerItemForDefaultRender(Item.getItemFromBlock(block));
   }

   protected void registerTypeItemForDefaultRender(Block block) {
      Item item = Item.getItemFromBlock(block);
      ResourceLocation id = RecurrentComplex.mcRegistry.idFromItem(item);

      for(int i = 0; i < 16; ++i) {
         ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(String.format("%s_%d", id, i), "inventory"));
      }

   }

   protected void registerItemForDefaultRender(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(RecurrentComplex.mcRegistry.idFromItem(item), "inventory"));
   }
}
