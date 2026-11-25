package prospector.shootingstar.model;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;

public class ModelCompound {
   private String inventoryVariant;
   private String fileName;
   private String blockstatePath;
   private IProperty[] ignoreProperties;
   private String modid;
   private Block block;
   private Item item;
   private int meta;

   public ModelCompound(String modid, Block block, int meta, String blockstatePath, IProperty... ignoreProperties) {
      this.inventoryVariant = "inventory";
      this.fileName = "shootingstar.undefinedfilename";
      this.ignoreProperties = null;
      this.block = null;
      this.item = null;
      this.modid = modid;
      this.block = block;
      this.blockstatePath = blockstatePath;
      this.ignoreProperties = ignoreProperties;
      this.meta = meta;
   }

   public ModelCompound(String modid, Block block, int meta, IProperty... ignoreProperties) {
      this(modid, block, meta, "", ignoreProperties);
   }

   public ModelCompound(String modid, Block block, IProperty... ignoreProperties) {
      this(modid, block, 0, "", ignoreProperties);
   }

   public ModelCompound(String modid, Block block, String blockstatePath, IProperty... ignoreProperties) {
      this(modid, block, 0, blockstatePath, ignoreProperties);
   }

   public ModelCompound(String modid, Item item, int meta, String blockstatePath) {
      this.inventoryVariant = "inventory";
      this.fileName = "shootingstar.undefinedfilename";
      this.ignoreProperties = null;
      this.block = null;
      this.item = null;
      this.modid = modid;
      this.item = item;
      this.blockstatePath = blockstatePath;
      this.meta = meta;
   }

   public ModelCompound(String modid, Item item, int meta) {
      this(modid, item, meta, "");
   }

   public ModelCompound(String modid, Item item) {
      this(modid, item, 0, "");
   }

   public ModelCompound(String modid, Item item, String blockstatePath) {
      this(modid, item, 0, blockstatePath);
   }

   public String getInventoryVariant() {
      return this.inventoryVariant;
   }

   public String getFileName() {
      return this.fileName;
   }

   public ModelCompound setFileName(String name) {
      this.fileName = name;
      return this;
   }

   public ModelCompound setInvVariant(String variant) {
      this.inventoryVariant = variant;
      return this;
   }

   public void setBlockStatePath(String blockstatePath) {
      this.blockstatePath = blockstatePath;
   }

   public String getModid() {
      return this.modid;
   }

   public Block getBlock() {
      return this.block;
   }

   public Item getItem() {
      return this.isBlock() ? Item.getItemFromBlock(this.block) : this.item;
   }

   public boolean isBlock() {
      return this.block != null;
   }

   public boolean isItem() {
      return this.item != null;
   }

   public String getBlockStatePath() {
      return this.blockstatePath;
   }

   public IProperty[] getIgnoreProperties() {
      return this.ignoreProperties;
   }

   public int getMeta() {
      return this.meta;
   }
}
