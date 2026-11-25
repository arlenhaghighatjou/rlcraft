package com.alcatrazescapee.notreepunching.integration.guide;

import amerifrance.guideapi.api.GuideBook;
import amerifrance.guideapi.api.IGuideBook;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.BookBinder;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.page.PageTextImage;
import com.alcatrazescapee.notreepunching.client.ModTabs;
import com.alcatrazescapee.notreepunching.common.blocks.BlockPottery;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.integration.guide.utils.CategoryBuilder;
import com.alcatrazescapee.notreepunching.integration.guide.utils.ILazyLoader;
import com.alcatrazescapee.notreepunching.integration.guide.utils.PageFurnaceRecipeLazy;
import com.alcatrazescapee.notreepunching.util.types.Pottery;
import com.alcatrazescapee.notreepunching.util.types.ToolType;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

@GuideBook
public final class ModGuideBook implements IGuideBook {
   private Book book;

   public ModGuideBook() {
   }

   @Nullable
   public Book buildBook() {
      this.book = (new BookBinder(new ResourceLocation("notreepunching", "guide_book"))).addCategory((new CategoryBuilder((new CategoryItemStack("notreepunching.guide.category.intro", new ItemStack(ModItems.getFlintTool(ToolType.KNIFE)))).withKeyBase("intro"))).addEntry("notreepunching.guide.intro.overview").addPage(new PageTextImage("notreepunching.guide.intro.page.1", new ResourceLocation("notreepunching", "textures/guide/splash.png"), false)).addPage("notreepunching.guide.intro.page.2").addEntry("notreepunching.guide.intro.block_breaking").addPage("notreepunching.guide.intro.page.3").addPage("notreepunching.guide.intro.page.4").addEntry("notreepunching.guide.intro.world_gen").addPage(new PageTextImage("notreepunching.guide.intro.page.5", new ResourceLocation("notreepunching", "textures/guide/loose_rocks.png"), false)).addPage("notreepunching.guide.intro.page.6").addEntry("notreepunching.guide.intro.mechanics").addPage("notreepunching.guide.intro.page.7").build()).addCategory((new CategoryBuilder((new CategoryItemStack("notreepunching.guide.category.early_game", new ItemStack(ModItems.FLINT_SHARD))).withKeyBase("early_game"))).addEntry("notreepunching.guide.early_game.sticks").addPage("notreepunching.guide.early_game.page.1").addEntry("notreepunching.guide.early_game.stones").addPage("notreepunching.guide.early_game.page.2").addPage(new ResourceLocation("notreepunching", "blocks/cobblestone_stone")).addPage(new ResourceLocation("notreepunching", "blocks/cobblestone_andesite")).addPage(new ResourceLocation("notreepunching", "blocks/cobblestone_diorite")).addPage(new ResourceLocation("notreepunching", "blocks/cobblestone_granite")).addPage(new ResourceLocation("notreepunching", "blocks/rocks_to_sandstone")).addEntry("notreepunching.guide.early_game.knapping").addPage(new PageTextImage("notreepunching.guide.early_game.page.3", new ResourceLocation("notreepunching", "textures/guide/knapping.png"), false)).addPage("notreepunching.guide.early_game.page.4").addEntry("notreepunching.guide.early_game.knife").addPage("notreepunching.guide.early_game.page.5").addPage(new ResourceLocation("notreepunching", "tools/flint_knife")).addEntry("notreepunching.guide.early_game.plant_fiber").addPage("notreepunching.guide.early_game.page.6").addPage(new ResourceLocation("notreepunching", "misc/grass_string")).addEntry("notreepunching.guide.early_game.hatchet").addPage("notreepunching.guide.early_game.page.7").addPage(new ResourceLocation("notreepunching", "tools/flint_axe")).addEntry("notreepunching.guide.early_game.chopping").addPage("notreepunching.guide.early_game.page.8").addEntry("notreepunching.guide.early_game.fire_pit").addPage("notreepunching.guide.early_game.page.9").addPage(new ResourceLocation("notreepunching", "tools/fire_starter")).addPage("notreepunching.guide.early_game.page.10").addPage(new PageTextImage("notreepunching.guide.early_game.page.11", new ResourceLocation("notreepunching", "textures/guide/fire_pit.png"), false)).addPage("notreepunching.guide.early_game.page.12").build()).addCategory((new CategoryBuilder((new CategoryItemStack("notreepunching.guide.category.tools", new ItemStack(ModItems.FIRE_STARTER))).withKeyBase("tools"))).addEntry("notreepunching.guide.tools.flint").addPage("notreepunching.guide.tools.page.1").addPage(new ResourceLocation("notreepunching", "tools/flint_knife")).addPage(new ResourceLocation("notreepunching", "tools/flint_axe")).addPage(new ResourceLocation("notreepunching", "tools/flint_pickaxe")).addPage(new ResourceLocation("notreepunching", "tools/flint_shovel")).addPage(new ResourceLocation("notreepunching", "tools/flint_hoe")).addEntry("notreepunching.guide.tools.knife").addPage("notreepunching.guide.tools.page.2").addPage(new ResourceLocation("notreepunching", "tools/iron_knife")).addEntry("notreepunching.guide.tools.saw").addPage("notreepunching.guide.tools.page.3").addPage(new ResourceLocation("notreepunching", "tools/iron_saw")).addEntry("notreepunching.guide.tools.mattock").addPage("notreepunching.guide.tools.page.4").addPage(new ResourceLocation("notreepunching", "tools/iron_mattock")).addEntry("notreepunching.guide.tools.fire_starter").addPage("notreepunching.guide.tools.page.5").addPage(new ResourceLocation("notreepunching", "tools/fire_starter")).build()).addCategory((new CategoryBuilder((new CategoryItemStack("notreepunching.guide.category.pottery", new ItemStack(ModItems.CLAY_TOOL))).withKeyBase("pottery"))).addEntry("notreepunching.guide.pottery.intro").addPage("notreepunching.guide.pottery.page.1").addPage("notreepunching.guide.pottery.page.2").addPage("notreepunching.guide.pottery.page.3").addEntry("notreepunching.guide.pottery.clay_brick").addPage("notreepunching.guide.pottery.page.4").addPage(new ResourceLocation("notreepunching", "misc/clay_tool_clay_brick")).addPage(new ResourceLocation("notreepunching", "misc/clay_tool_clay_brick_2")).addPage(new PageFurnaceRecipeLazy(ModItems.CLAY_BRICK)).addEntry("notreepunching.guide.pottery.large_vessel").addPage("notreepunching.guide.pottery.page.5").addPage(new PageFurnaceRecipeLazy(BlockPottery.get(Pottery.LARGE_VESSEL))).addEntry("notreepunching.guide.pottery.small_vessel").addPage("notreepunching.guide.pottery.page.6").addPage(new PageFurnaceRecipeLazy(BlockPottery.get(Pottery.SMALL_VESSEL))).addEntry("notreepunching.guide.pottery.bucket").addPage("notreepunching.guide.pottery.page.7").addPage(new PageFurnaceRecipeLazy(BlockPottery.get(Pottery.BUCKET))).addEntry("notreepunching.guide.pottery.flower_pot").addPage("notreepunching.guide.pottery.page.8").addPage(new PageFurnaceRecipeLazy(BlockPottery.get(Pottery.FLOWER_POT))).build()).setGuideTitle("notreepunching.guide.title").setHeader("notreepunching.guide.title").setItemName("notreepunching.guide.item_name").setAuthor("AlcatrazEscapee").setColor(Color.CYAN).setSpawnWithBook().setCreativeTab(ModTabs.TAB_ITEMS).build();
      return this.book;
   }

   @Nonnull
   public IRecipe getRecipe(@Nonnull ItemStack bookStack) {
      ResourceLocation loc = new ResourceLocation("guideapi", "guide_book_recipe");
      return (IRecipe)(new ShapedOreRecipe(loc, bookStack, new Object[]{"SR", 'S', Items.BOOK, 'R', "rock"})).setRegistryName(loc);
   }

   public void handlePost(@Nonnull ItemStack bookStack) {
      ILazyLoader.init(this.book);
   }
}
