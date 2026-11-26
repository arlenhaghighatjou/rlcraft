package de.Whitedraco.switchbow;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraft.world.World;
import java.util.function.Function;
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
   public static CommonProxy proxy;
   public static final String MODID = "switchbow";
   public static final String VERSION = "1.6.8";
   public static SimpleNetworkWrapper network;
   public static CreativeTabs tabSwitchbow = new CreativTabSwitchbow(CreativeTabs.getNextID(), "tabCreativTabSwitchbow");
   public static Initial instance;
   public static HashMap<Item, Item> hashItemTransforming = new HashMap();

   public Initial() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new de.Whitedraco.switchbow.proxy.ClientProxy() : new de.Whitedraco.switchbow.proxy.CommonProxy();
         de.Whitedraco.switchbow.RegisterUtil.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.pre((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postinit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverLoad((FMLServerStartingEvent)event);
      }

   }

   public void pre(FMLPreInitializationEvent event) {
      proxy.readConfig();
   }

   public void init(FMLInitializationEvent event) {
      (new WorldtickEvent()).register(FMLCommonHandler.instance().bus());
      OreDictionary.registerOre("lovepotion", ItemInit.LovePotion);
      this.regEntityArrow(EntityArrowLightningBolt.class, "ArrowLightningBolt", 0, ItemInit.ArrowLightningBolt, EntityArrowLightningBolt::new, EntityArrowLightningBolt::new);
      this.regEntityArrow(EntityArrowTNT.class, "ArrowTNT", 1, ItemInit.ArrowTNT, EntityArrowTNT::new, EntityArrowTNT::new);
      this.regEntityArrow(EntityArrowFrost.class, "ArrowFrost", 2, ItemInit.ArrowFrost, EntityArrowFrost::new, EntityArrowFrost::new);
      this.regEntityArrow(EntityArrowAir.class, "ArrowAir", 3, ItemInit.ArrowAir, EntityArrowAir::new, EntityArrowAir::new);
      this.regEntityArrow(EntityArrowEnderperle.class, "ArrowEnderperle", 4, ItemInit.ArrowEnderperle, EntityArrowEnderperle::new, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowVampier.class, "ArrowVampier", 5, ItemInit.ArrowVampier, EntityArrowVampier::new, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowWither.class, "ArrowWither", 6, ItemInit.ArrowWither, EntityArrowWither::new, EntityArrowWither::new);
      this.regEntityArrow(EntityArrowTorch.class, "ArrowTorch", 7, ItemInit.ArrowTorch, EntityArrowTorch::new, EntityArrowTorch::new);
      this.regEntityArrow(EntityArrowSplit.class, "ArrowSplit", 8, ItemInit.ArrowSplit, EntityArrowSplit::new, EntityArrowSplit::new);
      this.regEntityArrow(EntityArrowUnderwater.class, "ArrowUnderwater", 9, ItemInit.ArrowUnderwater, EntityArrowUnderwater::new, EntityArrowUnderwater::new);
      this.regEntityArrow(EntityArrowDiamond.class, "ArrowDiamond", 10, ItemInit.ArrowDiamond, EntityArrowDiamond::new, EntityArrowDiamond::new);
      this.regEntityArrow(EntityArrowLove.class, "ArrowLove", 11, ItemInit.ArrowLove, EntityArrowLove::new, EntityArrowLove::new);
      this.regEntityArrow(EntityArrowFire.class, "ArrowFire", 12, ItemInit.ArrowFire, EntityArrowFire::new, EntityArrowFire::new);
      this.regEntityArrow(EntityArrowBone.class, "ArrowBone", 13, ItemInit.ArrowBone, EntityArrowBone::new, EntityArrowBone::new);
      this.regEntityArrow(EntityArrowFirework.class, "ArrowFirework", 14, ItemInit.ArrowFirework, EntityArrowFirework::new, new DispenseArrowFirework());
      this.regEntityArrow(EntityArrowSplitLove.class, "ArrowSplitLove", 15, ItemInit.ArrowSplitLove, EntityArrowSplitLove::new, EntityArrowSplitLove::new);
      this.regEntityArrow(EntityArrowLuck.class, "ArrowLuck", 16, ItemInit.ArrowLuck, EntityArrowLuck::new, EntityArrowLuck::new);
      this.regEntityArrow(EntityArrowRedstone.class, "ArrowRedstone", 17, ItemInit.ArrowRedstone, EntityArrowRedstone::new, EntityArrowRedstone::new);
      this.regEntityArrow(EntityArrowBurial.class, "ArrowBurial", 18, ItemInit.ArrowBurial, EntityArrowBurial::new, EntityArrowBurial::new);
      this.regEntityArrow(EntityArrowChorus.class, "ArrowChorus", 19, ItemInit.ArrowChorus, EntityArrowChorus::new, EntityArrowChorus::new);
      this.regEntityArrow(EntityArrowSprinkler.class, "ArrowSprinkler", 20, ItemInit.ArrowSprinkler, EntityArrowSprinkler::new, EntityArrowSprinkler::new);
      this.regEntityArrow(EntityArrowDragonBreath.class, "ArrowDragonBreath", 21, ItemInit.ArrowDragonBreath, EntityArrowDragonBreath::new, EntityArrowDragonBreath::new);
      this.regEntityArrow(EntityArrowTriple.class, "ArrowTriple", 22, ItemInit.ArrowTriple, EntityArrowTriple::new, (BehaviorProjectileDispense)null);
      this.regEntityArrow(EntityArrowProtector.class, "ArrowProtector", 23, ItemInit.ArrowProtector, EntityArrowProtector::new, EntityArrowProtector::new);
      this.regEntityArrow(EntityArrowKnockback.class, "ArrowKnockback", 24, ItemInit.ArrowKnockback, EntityArrowKnockback::new, EntityArrowKnockback::new);
      this.regEntityArrow(EntityArrowBouncy.class, "ArrowBouncy", 25, ItemInit.ArrowBouncy, EntityArrowBouncy::new, EntityArrowBouncy::new);
      this.regEntityArrow(EntityArrowoftheSearcher.class, "ArrowoftheSearcher", 26, ItemInit.ArrowoftheSearcher, EntityArrowoftheSearcher::new, EntityArrowoftheSearcher::new);
      this.regEntityArrow(EntityArrowPiercing.class, "ArrowPiercing", 27, ItemInit.ArrowPiercing, EntityArrowPiercing::new, EntityArrowPiercing::new);
      this.regEntityArrow(EntityArrowLightningBoltStorm.class, "ArrowLightningBoltStorm", 200, ItemInit.ArrowLightningBoltStorm, EntityArrowLightningBoltStorm::new, EntityArrowLightningBoltStorm::new);
      this.regEntityArrow(EntityArrowBoneAOE.class, "ArrowBoneAOE", 201, ItemInit.ArrowBoneAOE, EntityArrowBoneAOE::new, EntityArrowBoneAOE::new);
      this.regEntityArrow(EntityArrowSplitUpgrade.class, "ArrowSplitUpgrade", 202, ItemInit.ArrowSplitUpgrade, EntityArrowSplitUpgrade::new, EntityArrowSplitUpgrade::new);
      this.regEntityArrow(EntityArrowBurialAOE.class, "ArrowBurialAOE", 203, ItemInit.ArrowBurialAOE, EntityArrowBurialAOE::new, EntityArrowBurialAOE::new);
      this.regEntityArrow(EntityArrowFireUpgrade.class, "ArrowFireUpgrade", 204, ItemInit.ArrowFireUpgrade, EntityArrowFireUpgrade::new, EntityArrowFireUpgrade::new);
      this.regEntityArrow(EntityArrowLuckUpgrade.class, "ArrowLuckUpgrade", 205, ItemInit.ArrowLuckUpgrade, EntityArrowLuckUpgrade::new, EntityArrowLuckUpgrade::new);
      this.regEntityArrow(EntityArrowAirUpgrade.class, "ArrowAirUpgrade", 206, ItemInit.ArrowAirUpgrade, EntityArrowAirUpgrade::new, EntityArrowAirUpgrade::new);
      this.regEntityArrow(EntityArrowSplitLoveUpgrade.class, "ArrowSplitLoveUpgrade", 207, ItemInit.ArrowSplitLoveUpgrade, EntityArrowSplitLoveUpgrade::new, EntityArrowSplitLoveUpgrade::new);
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "EntityFire"), EntityFire.class, "EntityFire", 101, instance, 64, 1, false, EntityFire::new);
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "EntityBurial"), EntityBurial.class, "EntityBurial", 102, instance, 64, 1, false, EntityBurial::new);
      GameRegistry.registerTileEntity(TileEntityArrowDispenser.class, new ResourceLocation("ArrowLancher"), TileEntityArrowDispenser::new);
      GameRegistry.registerTileEntity(TileEntityPodest.class, new ResourceLocation("Podest"), TileEntityPodest::new);

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
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), "LittleIronGolem"), EntityLittleIronGolem.class, "LittleIronGolem", 150, this, 64, 1, true, EntityLittleIronGolem::new);
   }

   public void craftingwithfourArrow(Item middleStack, Item result) {
      RecipeHelper.addShapedRecipe(result.getUnlocalizedName(), new ItemStack(result, 4), " a ", "ama", " a ", 'a', Items.ARROW, 'm', middleStack);
   }

   public void craftingswitchstick(Item switchItem, Item result) {
      RecipeHelper.addShapedRecipe(result.getUnlocalizedName(), new ItemStack(result, 4), "a", "m", "e", 'a', Items.FLINT, 'm', switchItem, 'e', Items.FEATHER);
   }

   public void postinit(FMLPostInitializationEvent event) {
      proxy.registerRenderers();
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GUIHandle());
      (new de.Whitedraco.switchbow.event.EventHandler()).register(MinecraftForge.EVENT_BUS);
      if (event.getSide().isClient()) {
         (new Mouse_Key_Lisener()).register(MinecraftForge.EVENT_BUS);
         (new ClientEventHandler()).register(MinecraftForge.EVENT_BUS);
         ClientCommandHandler.instance.registerCommand(new ClientCommand());
      }

      network = NetworkRegistry.INSTANCE.newSimpleChannel("switchbow");
      network.registerMessage(new SwitchBowHandler.Handler(), SwitchBowHandler.class, SwitchBowHandler::new, 0, Side.SERVER);
      network.registerMessage(new ParticleFreezePacket.Handler(), ParticleFreezePacket.class, ParticleFreezePacket::new, 1, Side.CLIENT);
      network.registerMessage(new ParticleLovePacket.Handler(), ParticleLovePacket.class, ParticleLovePacket::new, 2, Side.CLIENT);
      network.registerMessage(new PatricleWaterDropPacket.Handler(), PatricleWaterDropPacket.class, PatricleWaterDropPacket::new, 3, Side.CLIENT);
      network.registerMessage(new PatricleExplosionPacket.Handler(), PatricleExplosionPacket.class, PatricleExplosionPacket::new, 4, Side.CLIENT);
      network.registerMessage(new ParticleHeartPacket.Handler(), ParticleHeartPacket.class, ParticleHeartPacket::new, 5, Side.CLIENT);
      network.registerMessage(new ParticleSplittingPacket.Handler(), ParticleSplittingPacket.class, ParticleSplittingPacket::new, 6, Side.CLIENT);
      network.registerMessage(new ParticleLightningPacket.Handler(), ParticleLightningPacket.class, ParticleLightningPacket::new, 7, Side.CLIENT);
      network.registerMessage(new TEArrowDispenserShootAnglePacket.Handler(), TEArrowDispenserShootAnglePacket.class, TEArrowDispenserShootAnglePacket::new, 20, Side.SERVER);
      network.registerMessage(new TEArrowDispenserRangePacket.Handler(), TEArrowDispenserRangePacket.class, TEArrowDispenserRangePacket::new, 21, Side.SERVER);
      network.registerMessage(new TEArrowDispenserCountPacket.Handler(), TEArrowDispenserCountPacket.class, TEArrowDispenserCountPacket::new, 22, Side.SERVER);
      network.registerMessage(new TEPodestUpdatePacket.Handler(), TEPodestUpdatePacket.class, TEPodestUpdatePacket::new, 23, Side.CLIENT);
      network.registerMessage(new TEPodestParticlePacket.Handler(), TEPodestParticlePacket.class, TEPodestParticlePacket::new, 24, Side.CLIENT);
   }

   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandSwitchBow());
   }

   public <T extends Entity> void regEntityArrow(Class<T> entityClass, String name, int id, Item item, Function<World, T> factory, BehaviorProjectileDispense projectileDispense) {
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), name), entityClass, name, id, instance, 64, 1, true, factory);
      if (projectileDispense != null) {
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, projectileDispense);
      }

   }

   public <T extends Entity> void regEntityArrow(Class<T> entityClass, String name, int id, Item item, Function<World, T> factory, DispenseArrows.ArrowFactory dispenseFactory) {
      EntityRegistry.registerModEntity(new ResourceLocation("switchbow".toLowerCase(), name), entityClass, name, id, instance, 64, 1, true, factory);
      BehaviorProjectileDispense dispense = new DispenseArrows(dispenseFactory);
      if (dispense != null) {
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, dispense);
      }

   }

   private void fillHashmapPodest(Item Input, Item Output) {
      hashItemTransforming.put(Input, Output);
   }
}
