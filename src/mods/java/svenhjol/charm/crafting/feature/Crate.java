package svenhjol.charm.crafting.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.base.CharmLootTables;
import svenhjol.charm.crafting.block.BlockCrate;
import svenhjol.charm.crafting.client.RenderTileCrate;
import svenhjol.charm.crafting.tile.TileCrate;
import svenhjol.meson.Feature;
import svenhjol.meson.MesonBlock;
import svenhjol.meson.MesonItemBlock;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.helper.ItemNBTHelper;
import svenhjol.meson.helper.LootHelper;
import svenhjol.meson.registry.ProxyRegistry;

public class Crate extends Feature {
   public static BlockCrate crate;
   public static BlockCrate crateSealed;
   public static float hardness;
   public static boolean showCrateNames;
   public static int xpCost;
   public static boolean crateTooltip;
   public static boolean requireShift;
   public static ResourceLocation WIDGET_RESOURCE = new ResourceLocation("charm", "textures/misc/crate_widget.png");
   public static List<Class<? extends Block>> blacklist = new ArrayList();
   public static Map<Crate.RARITY, List<Crate.CrateType>> types = new HashMap();

   public Crate() {
   }

   public static Crate.CrateType getRandomCrateType(Crate.RARITY r) {
      EaglercraftRandom rand = new EaglercraftRandom();
      List<Crate.CrateType> t = (List)types.get(r);
      return (Crate.CrateType)t.get(rand.nextInt(t.size()));
   }

   public String getDescription() {
      return "Offers a smaller storage solution with the benefit of being transportable.  Name your crate on an anvil to change the displayed label.\nYou can also seal the crate by combining it with an iron ingot - the only way to get things out is to break it.";
   }

   public void configure() {
      super.configure();
      xpCost = this.propInt("XP cost", "Amount of XP (levels) required to use a name tag to seal a crate.", 0);
      crateTooltip = this.propBoolean("Crate tooltip", "Shows tooltip containing crate contents when hovering over the crate in the inventory.", true);
      requireShift = this.propBoolean("Tooltip shift key", "Crate tooltip needs the shift key held down to be visible", false);
      hardness = 2.0F;
      showCrateNames = true;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      crate = new BlockCrate(BlockCrate.Type.CRATE);
      crateSealed = new BlockCrate(BlockCrate.Type.CRATE_SEALED);
      GameRegistry.registerTileEntity(crate.getTileEntityClass(), new ResourceLocation("charm:crate"));
      blacklist.add(BlockCrate.class);
      blacklist.add(BlockShulkerBox.class);
      Map<Crate.RARITY, List<ResourceLocation>> map = new HashMap<Crate.RARITY, List<ResourceLocation>>() {
         {
            this.put(Crate.RARITY.COMMON, LootHelper.getLootTables(LootHelper.RARITY.COMMON, LootHelper.TYPE.MISC));
            this.put(Crate.RARITY.UNCOMMON, LootHelper.getLootTables(LootHelper.RARITY.UNCOMMON, LootHelper.TYPE.MISC));
            this.put(Crate.RARITY.VALUABLE, LootHelper.getLootTables(LootHelper.RARITY.VALUABLE, LootHelper.TYPE.MISC));
            this.put(Crate.RARITY.RARE, LootHelper.getLootTables(LootHelper.RARITY.RARE, LootHelper.TYPE.MISC));
         }
      };

      for(Crate.RARITY rarity : map.keySet()) {
         List<ResourceLocation> tables = (List)map.get(rarity);
         List<Crate.CrateType> crates = new ArrayList();

         for(ResourceLocation res : tables) {
            String[] a = res.getResourcePath().split("/");
            crates.add(new Crate.CrateType(a.length > 1 ? a[1] : a[0], res));
         }

         types.put(rarity, crates);
      }

      ((List)types.get(Crate.RARITY.VALUABLE)).add(new Crate.CrateType("dangerous", CharmLootTables.TREASURE_DANGEROUS));
      ((List)types.get(Crate.RARITY.RARE)).add(new Crate.CrateType("explosive", CharmLootTables.TREASURE_EXPLOSIVE));

      for(int i = 0; i < MesonBlock.WoodVariant.values().length; ++i) {
         RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(crate, 1, i), "IWI", "W W", "IWI", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i), 'I', "ingotIron");
      }

   }

   public static ItemStack getSealedCrateItem(ItemStack in, String name) {
      ItemStack out = new ItemStack(crateSealed, 1, in.getItemDamage());
      if (name == null || name.isEmpty()) {
         name = in.getDisplayName();
      }

      NBTTagCompound inTag = in.getTagCompound();
      NBTTagCompound outTag;
      if (inTag != null && !inTag.hasNoTags() && !inTag.getCompoundTag("BlockEntityTag").hasNoTags()) {
         outTag = inTag.copy();
      } else {
         outTag = out.getOrCreateSubCompound("BlockEntityTag");
      }

      outTag.getCompoundTag("BlockEntityTag");
      out.setTagCompound(outTag);
      out.setStackDisplayName(name);
      return out;
   }

   public static void generateCrate(World world, BlockPos pos, Crate.CrateType crateType, IBlockState state) {
      world.setBlockState(pos, state, 2);
      TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof TileCrate) {
         TileCrate crate = (TileCrate)tile;
         crate.setName(crateType.name);
         crate.setShowName(true);
         crate.setLootTable(crateType.pool);
         crate.markDirty();
      }

   }

   @SubscribeEvent
   public void onAnvilRepair(AnvilRepairEvent event) {
      ItemStack in = event.getItemInput();
      ItemStack combine = event.getIngredientInput();
      if (!in.isEmpty() && combine.isEmpty()) {
         if (this.isItemCrate(in)) {
            event.setBreakChance(0.0F);
         }
      }
   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack in = event.getLeft();
      ItemStack combine = event.getRight();
      if (!in.isEmpty() || !combine.isEmpty()) {
         if (this.isItemCrate(in)) {
            ItemStack out = null;
            int cost = 0;
            if (combine.getItem() == Items.NAME_TAG) {
               out = in.copy();
               cost = in.getCount();
               NBTTagCompound be = this.getBlockEntityTag(out);
               be.setBoolean("showname", true);
            }

            if (!this.isSealedCrate(in) && combine.getItem() == Items.IRON_INGOT) {
               cost = Math.min(in.getCount(), combine.getCount());
               String name = in.getDisplayName();
               out = getSealedCrateItem(in, name);
            }

            if (out != null) {
               out.setCount(cost);
               event.setCost(xpCost);
               event.setMaterialCost(cost);
               event.setOutput(out);
            }
         }

      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void makeTooltip(ItemTooltipEvent event) {
      if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof MesonItemBlock && ((MesonItemBlock)event.getItemStack().getItem()).getBlock() instanceof BlockCrate && event.getItemStack().hasTagCompound()) {
         NBTTagCompound tag = ItemNBTHelper.getCompound(event.getItemStack(), "BlockEntityTag");
         if (!tag.hasNoTags()) {
            List<String> tooltip = event.getToolTip();
            List<String> tooltipCopy = new ArrayList(tooltip);

            for(int i = 1; i < tooltipCopy.size(); ++i) {
               String s = (String)tooltipCopy.get(i);
               if (!s.startsWith("\u00a7") || s.startsWith("\u00a7o")) {
                  tooltip.remove(s);
               }
            }

            if (requireShift && !GuiScreen.isShiftKeyDown()) {
               tooltip.add(1, I18n.format("charm.crate_tooltip_shift", new Object[0]));
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void renderTooltip(PostText event) {
      if (!event.getStack().isEmpty() && event.getStack().getItem() instanceof MesonItemBlock && ((MesonItemBlock)event.getStack().getItem()).getBlock() instanceof BlockCrate && event.getStack().hasTagCompound() && (!requireShift || GuiScreen.isShiftKeyDown())) {
         NBTTagCompound tag = ItemNBTHelper.getCompound(event.getStack(), "BlockEntityTag");
         if (!tag.hasNoTags() && tag.hasKey("inventory") && !tag.getCompoundTag("inventory").getTagList("Items", 10).hasNoTags()) {
            NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(tag.getCompoundTag("inventory"), items);
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc);
            if (((BlockCrate)((MesonItemBlock)event.getStack().getItem()).getBlock()).isSealedCrate() && !mc.player.isCreative()) {
               return;
            }

            int x = event.getX() - 5;
            int y = event.getY() - 35;
            int w = 172;
            int h = 64;
            int right = x + w;
            if (y < 0) {
               y = event.getY() + event.getLines().size() * 10 + 5;
            }

            if (right > res.getScaledWidth()) {
               x -= right - res.getScaledWidth();
            }

            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0.0F, 0.0F, 700.0F);
            mc.getTextureManager().bindTexture(WIDGET_RESOURCE);
            RenderHelper.disableStandardItemLighting();
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, w, h, 256.0F, 256.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            RenderItem render = mc.getRenderItem();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepth();
            int i = 0;

            for(ItemStack item : items) {
               int xp = x + 6 + i % 9 * 18;
               int yp = y + 6 + i / 9 * 18;
               if (!item.isEmpty()) {
                  render.renderItemAndEffectIntoGUI(item, xp, yp);
                  render.renderItemOverlays(mc.fontRenderer, item, xp, yp);
               }

               ++i;
            }

            GlStateManager.disableDepth();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
         }
      }

   }

   public static boolean canInsertItem(ItemStack stack) {
      Class<? extends Block> clazz = Block.getBlockFromItem(stack.getItem()).getClass();
      return !blacklist.contains(clazz);
   }

   protected NBTTagCompound getBlockEntityTag(ItemStack stack) {
      NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey("BlockEntityTag")) {
         stack.getOrCreateSubCompound("BlockEntityTag");
         tag = stack.getTagCompound();
      }

      return tag.getCompoundTag("BlockEntityTag");
   }

   protected boolean isItemCrate(ItemStack stack) {
      return Block.getBlockFromItem(stack.getItem()) instanceof BlockCrate;
   }

   protected boolean isSealedCrate(ItemStack stack) {
      return this.isItemCrate(stack) && ((BlockCrate)Block.getBlockFromItem(stack.getItem())).isSealedCrate();
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient(FMLPreInitializationEvent event) {
      if (showCrateNames) {
         ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.class, new RenderTileCrate());
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public class CrateType {
      public String name;
      public String id;
      public ResourceLocation pool;

      public CrateType(String id, ResourceLocation pool) {
         this.id = id;
         this.name = net.minecraft.util.text.translation.I18n.translateToLocal(id) + " " + net.minecraft.util.text.translation.I18n.translateToLocal("crate");
         this.pool = pool;
      }
   }

   public static enum RARITY {
      COMMON,
      UNCOMMON,
      VALUABLE,
      RARE;

      private RARITY() {
      }
   }
}
