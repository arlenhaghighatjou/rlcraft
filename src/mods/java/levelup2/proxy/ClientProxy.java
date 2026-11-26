package levelup2.proxy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.config.LevelUpConfig;
import levelup2.event.KeybindEventHandler;
import levelup2.gui.GuiSpecialization;
import levelup2.skills.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerGui() {
      KeybindEventHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
   }

   public EntityPlayer getPlayer() {
      return FMLClientHandler.instance().getClient().player;
   }

   public void registerItemMeshes() {
      this.register(MinecraftForge.EVENT_BUS);
   }

   public void openSpecializationGui() {
      Minecraft.getMinecraft().displayGuiScreen(GuiSpecialization.withRespec());
   }

   public void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, this::registerModels);
   }

   public void registerModels(ModelRegistryEvent evt) {
      this.setModelLocation(SkillRegistry.surfaceOreChunk, "inventory");
      this.setModelLocation(SkillRegistry.netherOreChunk, "inventory");
      this.setModelLocation(SkillRegistry.endOreChunk, "inventory");
      this.setModelLocation(SkillRegistry.respecBook, "inventory");
   }

   private void setModelLocation(Item item, String variantSettings) {
      ModelLoader.setCustomMeshDefinition(item, (stack) -> {
         return new ModelResourceLocation(item.getRegistryName(), variantSettings);
      });
      ModelLoader.registerItemVariants(item, new ResourceLocation[]{item.getRegistryName()});
   }

   public void registerColors() {
      ItemColors color = Minecraft.getMinecraft().getItemColors();
      color.registerItemColorHandler((stack, tintIndex) -> {
         return tintIndex == 1 && stack.getMetadata() < LevelUpConfig.oreColors.size() ? ((Integer)LevelUpConfig.oreColors.get(stack.getMetadata())).intValue() : -1;
      }, new Item[]{SkillRegistry.surfaceOreChunk});
      color.registerItemColorHandler((stack, tintIndex) -> {
         return tintIndex == 1 && stack.getMetadata() < LevelUpConfig.netherOreColors.size() ? ((Integer)LevelUpConfig.netherOreColors.get(stack.getMetadata())).intValue() : -1;
      }, new Item[]{SkillRegistry.netherOreChunk});
      color.registerItemColorHandler((stack, tintIndex) -> {
         return tintIndex == 1 && stack.getMetadata() < LevelUpConfig.endOreColors.size() ? ((Integer)LevelUpConfig.endOreColors.get(stack.getMetadata())).intValue() : -1;
      }, new Item[]{SkillRegistry.endOreChunk});
   }
}
