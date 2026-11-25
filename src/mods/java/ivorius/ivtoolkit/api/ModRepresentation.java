package ivorius.ivtoolkit.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class ModRepresentation {
   public String modID;

   public ModRepresentation(String modID) {
      this.modID = modID;
   }

   public static String id(Block block) {
      return name(block).toString();
   }

   public static ResourceLocation name(Block block) {
      return (ResourceLocation)Block.REGISTRY.getNameForObject(block);
   }

   public static String id(Item item) {
      return name(item).toString();
   }

   public static ResourceLocation name(Item item) {
      return (ResourceLocation)Item.REGISTRY.getNameForObject(item);
   }

   public boolean isLoaded() {
      return Loader.isModLoaded(this.modID);
   }

   public String getID() {
      return this.modID;
   }

   public Block block(String id) {
      return (Block)Block.REGISTRY.getObject(new ResourceLocation(this.modID, id));
   }

   public Item item(String id) {
      return (Item)Item.REGISTRY.getObject(new ResourceLocation(this.modID, id));
   }
}
