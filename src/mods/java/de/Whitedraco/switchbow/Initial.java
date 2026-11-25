package de.Whitedraco.switchbow;

import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.Config.CraftingConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.command.ClientCommand;
import de.Whitedraco.switchbow.command.CommandSwitchBow;
import de.Whitedraco.switchbow.creativTab.CreativTabSwitchbow;
import de.Whitedraco.switchbow.dispense.DispenseArrowFirework;
import de.Whitedraco.switchbow.dispense.DispenseArrows;
import de.Whitedraco.switchbow.entity.EntityBurial;
import de.Whitedraco.switchbow.entity.EntityFire;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowAir;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBone;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBouncy;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowBurial;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowChorus;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowDiamond;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowDragonBreath;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowEnderperle;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFire;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFirework;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowFrost;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowKnockback;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLightningBolt;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLove;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowLuck;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowPiercing;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowProtector;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowRedstone;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSplit;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSplitLove;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowSprinkler;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTNT;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTorch;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowTriple;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowUnderwater;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowVampier;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowWither;
import de.Whitedraco.switchbow.entity.arrow.EntityArrowoftheSearcher;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowAirUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowBoneAOE;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowBurialAOE;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowFireUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowLightningBoltStorm;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowLuckUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowSplitLoveUpgrade;
import de.Whitedraco.switchbow.entity.arrow.upgrade.EntityArrowSplitUpgrade;
import de.Whitedraco.switchbow.entity.living.EntityLittleIronGolem;
import de.Whitedraco.switchbow.event.ClientEventHandler;
import de.Whitedraco.switchbow.event.Mouse_Key_Lisener;
import de.Whitedraco.switchbow.event.WorldtickEvent;
import de.Whitedraco.switchbow.gui.GUIHandle;
import de.Whitedraco.switchbow.proxy.CommonProxy;
import de.Whitedraco.switchbow.proxy.SwitchBowHandler;
import de.Whitedraco.switchbow.proxy.packets.ParticleFreezePacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleHeartPacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleLightningPacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleLovePacket;
import de.Whitedraco.switchbow.proxy.packets.ParticleSplittingPacket;
import de.Whitedraco.switchbow.proxy.packets.PatricleExplosionPacket;
import de.Whitedraco.switchbow.proxy.packets.PatricleWaterDropPacket;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserCountPacket;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserRangePacket;
import de.Whitedraco.switchbow.proxy.packets.TEArrowDispenserShootAnglePacket;
import de.Whitedraco.switchbow.proxy.packets.TEPodestParticlePacket;
import de.Whitedraco.switchbow.proxy.packets.TEPodestUpdatePacket;
import de.Whitedraco.switchbow.recipe.RecipeArrowFirework;
import de.Whitedraco.switchbow.recipe.RecipeHelper;
import de.Whitedraco.switchbow.recipe.RecipeMagicQuiver;
import de.Whitedraco.switchbow.tileentity.TileEntityArrowDispenser;
import de.Whitedraco.switchbow.tileentity.TileEntityPodest;
import java.io.IOException;
import java.util.HashMap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
   modid = "switchbow",
   version = "1.6.8",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Initial {
   @SidedProxy(
      clientSide = "de.Whitedraco.switchbow.proxy.ClientProxy",
      serverSide = "de.Whitedraco.switchbow.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static final String MODID = "switchbow";
   public static final String VERSION = "1.6.8";
   public static SimpleNetworkWrapper network;
   public static CreativeTabs tabSwitchbow = new CreativTabSwitchbow(CreativeTabs.getNextID(), "tabCreativTabSwitchbow");
   @Instance("switchbow")
   public static Initial instance;
   public static HashMap<Item, Item> hashItemTransforming = new HashMap();

   public Initial() {
   }

   @EventHandler
   public void pre(FMLPreInitializationEvent event) {
      proxy.readConfig();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      FMLCommonHandler.instance().bus().register(new WorldtickEvent());
      OreDictionary.registerOre("lovepotion", ItemInit.LovePotion);
      this.regEntityArrow(EntityArrowLightningBolt.class, "ArrowLightningBolt", 0, ItemInit.ArrowLightningBolt);
      this.regEntityArrow(EntityArrowTNT.class, "ArrowTNT", 1, ItemInit.ArrowTNT);
      this.regEntityArrow(EntityArrowFrost.class, "ArrowFrost", 2, ItemInit.ArrowFrost);
      this.regEntityArrow(EntityArrowAir.class, "ArrowAir", 3, ItemInit.ArrowAir);
      this.regEntityArrow(EntityArrowEnderperle.class, "ArrowEnderperle", 4, ItemInit.ArrowEnderperle, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowVampier.class, "ArrowVampier", 5, ItemInit.ArrowVampier, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowWither.class, "ArrowWither", 6, ItemInit.ArrowWither);
      this.regEntityArrow(EntityArrowTorch.class, "ArrowTorch", 7, ItemInit.ArrowTorch);
      this.regEntityArrow(EntityArrowSplit.class, "ArrowSplit", 8, ItemInit.ArrowSplit);
      this.regEntityArrow(EntityArrowUnderwater.class, "ArrowUnderwater", 9, ItemInit.ArrowUnderwater);
      this.regEntityArrow(EntityArrowDiamond.class, "ArrowDiamond", 10, ItemInit.ArrowDiamond);
      this.regEntityArrow(EntityArrowLove.class, "ArrowLove", 11, ItemInit.ArrowLove);
      this.regEntityArrow(EntityArrowFire.class, "ArrowFire", 12, ItemInit.ArrowFire);
      this.regEntityArrow(EntityArrowBone.class, "ArrowBone", 13, ItemInit.ArrowBone);
      this.regEntityArrow(EntityArrowFirework.class, "ArrowFirework", 14, ItemInit.ArrowFirework, new DispenseArrowFirework());
      this.regEntityArrow(EntityArrowSplitLove.class, "ArrowSplitLove", 15, ItemInit.ArrowSplitLove);
      this.regEntityArrow(EntityArrowLuck.class, "ArrowLuck", 16, ItemInit.ArrowLuck);
      this.regEntityArrow(EntityArrowRedstone.class, "ArrowRedstone", 17, ItemInit.ArrowRedstone);
      this.regEntityArrow(EntityArrowBurial.class, "ArrowBurial", 18, ItemInit.ArrowBurial);
      this.regEntityArrow(EntityArrowChorus.class, "ArrowChorus", 19, ItemInit.ArrowChorus);
      this.regEntityArrow(EntityArrowSprinkler.class, "ArrowSprinkler", 20, ItemInit.ArrowSprinkler);
      this.regEntityArrow(EntityArrowDragonBreath.class, "ArrowDragonBreath", 21, ItemInit.ArrowDragonBreath);
      this.regEntityArrow(EntityArrowTriple.class, "ArrowTriple", 22, ItemInit.ArrowTriple, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowProtector.class, "ArrowProtector", 23, ItemInit.ArrowProtector);
      this.regEntityArrow(EntityArrowKnockback.class, "ArrowKnockback", 24, ItemInit.ArrowKnockback);
      this.regEntityArrow(EntityArrowBouncy.class, "ArrowBouncy", 25, ItemInit.ArrowBouncy);
      this.regEntityArrow(EntityArrowoftheSearcher.class, "ArrowoftheSearcher", 26, ItemInit.ArrowoftheSearcher);
      this.regEntityArrow(EntityArrowPiercing.class, "ArrowPiercing", 27, ItemInit.ArrowPiercing);
      this.regEntityArrow(EntityArrowLightningBoltStorm.class, "ArrowLightningBoltStorm", 200, ItemInit.ArrowLightningBoltStorm);
      this.regEntityArrow(EntityArrowBoneAOE.class, "ArrowBoneAOE", 201, ItemInit.ArrowBoneAOE);
      this.regEntityArrow(EntityArrowSplitUpgrade.class, "ArrowSplitUpgrade", 202, ItemInit.ArrowSplitUpgrade);
      this.regEntityArrow(EntityArrowBurialAOE.class, "ArrowBurialAOE", 203, ItemInit.ArrowBurialAOE);
      this.regEntityArrow(EntityArrowFireUpgrade.class, "ArrowFireUpgrade", 204, ItemInit.ArrowFireUpgrade);
      this.regEntityArrow(EntityArrowLuckUpgrade.class, "ArrowLuckUpgrade", 205, ItemInit.ArrowLuckUpgrade);
      this.regEntityArrow(EntityArrowAirUpgrade.class, "ArrowAirUpgrade", 206, ItemInit.ArrowAirUpgrade);
      this.regEntityArrow(EntityArrowSplitLoveUpgrade.class, "ArrowSplitLoveUpgrade", 207, ItemInit.ArrowSplitLoveUpgrade);
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "EntityFire"), EntityFire.class, "EntityFire", 101, instance, 64, 1, false);
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "EntityBurial"), EntityBurial.class, "EntityBurial", 102, instance, 64, 1, false);
      GameRegistry.registerTileEntity(TileEntityArrowDispenser.class, "ArrowLancher");
      GameRegistry.registerTileEntity(TileEntityPodest.class, "Podest");

      try {
         CraftingConfig.readCraftingConfig();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      if (SwitchBowConfig.MagicQuiver.getBool()) {
         ForgeRegistries.RECIPES.register((new RecipeMagicQuiver()).setRegistryName("switchbow", "RecipeMagicQuiver"));
      }

      if (ArrowConfig.CraftingArrowFirework.getBool()) {
         ForgeRegistries.RECIPES.register((new RecipeArrowFirework()).setRegistryName("switchbow", "RecipeArrowFirework"));
      }

      if (ArrowConfig.CraftingArrowLove.getBool()) {
         ShapedOreRecipe shapedrecipes = new ShapedOreRecipe(new ResourceLocation(ItemInit.ArrowLove.getUnlocalizedName()), new ItemStack(ItemInit.ArrowLove, 4), new Object[]{" a ", "ama", " a ", 'a', Items.ARROW, 'm', "lovepotion"});
         shapedrecipes.setRegistryName(ItemInit.ArrowLove.getUnlocalizedName());
         ForgeRegistries.RECIPES.register(shapedrecipes);
      }

      if (ArrowConfig.CraftingArrowSplitLove.getBool()) {
         ShapedOreRecipe shapedrecipes = new ShapedOreRecipe(new ResourceLocation("arrowsplitlove"), new ItemStack(ItemInit.ArrowSplitLove, 4), new Object[]{" a ", "ama", " a ", 'a', ItemInit.ArrowSplit, 'm', "lovepotion"});
         shapedrecipes.setRegistryName("arrowsplitlove");
         ForgeRegistries.RECIPES.register(shapedrecipes);
      }

      if (ArrowConfig.CraftingArrowLightningStorm.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowLightningBolt, ItemInit.ArrowLightningBoltStorm);
      }

      if (ArrowConfig.CraftingArrowBoneAOE.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowBone, ItemInit.ArrowBoneAOE);
      }

      if (ArrowConfig.CraftingArrowSplitUpgrade.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowSplit, ItemInit.ArrowSplitUpgrade);
      }

      if (ArrowConfig.CraftingArrowBurialAOE.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowBurial, ItemInit.ArrowBurialAOE);
      }

      if (ArrowConfig.CraftingArrowFireUpgrade.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowFire, ItemInit.ArrowFireUpgrade);
      }

      if (ArrowConfig.CraftingArrowLuckUpgrade.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowLuck, ItemInit.ArrowLuckUpgrade);
      }

      if (ArrowConfig.CraftingArrowAirUpgrade.getBool()) {
         this.fillHashmapPodest(ItemInit.ArrowAir, ItemInit.ArrowAirUpgrade);
      }

      if (ArrowConfig.CraftingArrowSplitLoveUpgrade.getBool()) {
         ;
      }

      this.fillHashmapPodest(ItemInit.ArrowSplitLove, ItemInit.ArrowSplitLoveUpgrade);
      this.fillHashmapPodest(ItemInit.ArrowLove, ItemInit.ArrowSplitLove);
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "LittleIronGolem"), EntityLittleIronGolem.class, "LittleIronGolem", 150, this, 64, 1, true);
   }

   public void craftingwithfourArrow(Item middleStack, Item result) {
      RecipeHelper.addShapedRecipe(result.getUnlocalizedName(), new ItemStack(result, 4), " a ", "ama", " a ", 'a', Items.ARROW, 'm', middleStack);
   }

   public void craftingswitchstick(Item switchItem, Item result) {
      RecipeHelper.addShapedRecipe(result.getUnlocalizedName(), new ItemStack(result, 4), "a", "m", "e", 'a', Items.FLINT, 'm', switchItem, 'e', Items.FEATHER);
   }

   @EventHandler
   public void postinit(FMLPostInitializationEvent event) {
      proxy.registerRenderers();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GUIHandle());
      MinecraftForge.EVENT_BUS.register(new de.Whitedraco.switchbow.event.EventHandler());
      if (event.getSide().isClient()) {
         MinecraftForge.EVENT_BUS.register(new Mouse_Key_Lisener());
         MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
         ClientCommandHandler.instance.registerCommand(new ClientCommand());
      }

      network = NetworkRegistry.INSTANCE.newSimpleChannel("switchbow");
      network.registerMessage(SwitchBowHandler.Handler.class, SwitchBowHandler.class, 0, Side.SERVER);
      network.registerMessage(ParticleFreezePacket.Handler.class, ParticleFreezePacket.class, 1, Side.CLIENT);
      network.registerMessage(ParticleLovePacket.Handler.class, ParticleLovePacket.class, 2, Side.CLIENT);
      network.registerMessage(PatricleWaterDropPacket.Handler.class, PatricleWaterDropPacket.class, 3, Side.CLIENT);
      network.registerMessage(PatricleExplosionPacket.Handler.class, PatricleExplosionPacket.class, 4, Side.CLIENT);
      network.registerMessage(ParticleHeartPacket.Handler.class, ParticleHeartPacket.class, 5, Side.CLIENT);
      network.registerMessage(ParticleSplittingPacket.Handler.class, ParticleSplittingPacket.class, 6, Side.CLIENT);
      network.registerMessage(ParticleLightningPacket.Handler.class, ParticleLightningPacket.class, 7, Side.CLIENT);
      network.registerMessage(TEArrowDispenserShootAnglePacket.Handler.class, TEArrowDispenserShootAnglePacket.class, 20, Side.SERVER);
      network.registerMessage(TEArrowDispenserRangePacket.Handler.class, TEArrowDispenserRangePacket.class, 21, Side.SERVER);
      network.registerMessage(TEArrowDispenserCountPacket.Handler.class, TEArrowDispenserCountPacket.class, 22, Side.SERVER);
      network.registerMessage(TEPodestUpdatePacket.Handler.class, TEPodestUpdatePacket.class, 23, Side.CLIENT);
      network.registerMessage(TEPodestParticlePacket.Handler.class, TEPodestParticlePacket.class, 24, Side.CLIENT);
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandSwitchBow());
   }

   public void regEntityArrow(Class<? extends Entity> entityClass, String name, int id, Item item, BehaviorProjectileDispense projectileDispense) {
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), name), entityClass, name, id, instance, 64, 1, true);
      if (projectileDispense != null) {
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, projectileDispense);
      }

   }

   public void regEntityArrow(Class<? extends Entity> entityClass, String name, int id, Item item) {
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), name), entityClass, name, id, instance, 64, 1, true);
      BehaviorProjectileDispense dispense = new DispenseArrows(entityClass);
      if (dispense != null) {
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, dispense);
      }

   }

   private void fillHashmapPodest(Item Input, Item Output) {
      hashItemTransforming.put(Input, Output);
   }
}
