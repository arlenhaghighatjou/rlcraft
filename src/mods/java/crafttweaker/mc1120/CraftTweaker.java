package crafttweaker.mc1120;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.mc1120.brewing.MCBrewing;
import crafttweaker.mc1120.client.MCClient;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.events.ActionApplyEvent;
import crafttweaker.mc1120.formatting.MCFormatter;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.game.MCGame;
import crafttweaker.mc1120.item.MCItemUtils;
import crafttweaker.mc1120.logger.MCLogger;
import crafttweaker.mc1120.mods.MCLoadedMods;
import crafttweaker.mc1120.network.MessageCopyClipboard;
import crafttweaker.mc1120.network.MessageOpenBrowser;
import crafttweaker.mc1120.oredict.MCOreDict;
import crafttweaker.mc1120.preprocessors.ModLoadedPreprocessor;
import crafttweaker.mc1120.preprocessors.ZsLintPreprocessor;
import crafttweaker.mc1120.proxies.CommonProxy;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.util.CraftTweakerPlatformUtils;
import crafttweaker.mc1120.vanilla.MCVanilla;
import crafttweaker.preprocessor.PreprocessorFactory;
import crafttweaker.runtime.IScriptProvider;
import crafttweaker.runtime.providers.ScriptProviderCascade;
import crafttweaker.runtime.providers.ScriptProviderDirectory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "crafttweaker",
   version = "4.1.20",
   name = "Crafttweaker",
   acceptedMinecraftVersions = "[1.12]",
   updateJSON = "https://updates.blamejared.com/get?n=crafttweaker&gv=1.12.2",
   dependencies = "before:jei"
)
public class CraftTweaker {
   public static final String MODID = "crafttweaker";
   public static final String NAME = "Crafttweaker";
   public static final Logger LOG = LogManager.getLogger("Crafttweaker");
   public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("crafttweaker");
   public static MinecraftServer server;
   @Instance("crafttweaker")
   public static CraftTweaker INSTANCE;
   @SidedProxy(
      clientSide = "crafttweaker.mc1120.proxies.ClientProxy",
      serverSide = "crafttweaker.mc1120.proxies.CommonProxy"
   )
   public static CommonProxy PROXY;
   private static Set<String> PATRON_LIST = new HashSet();
   public static List<IAction> LATE_ACTIONS = new LinkedList();
   public MCRecipeManager recipes;
   private IScriptProvider scriptsGlobal;
   public static boolean alreadyChangedThePlayer = false;

   public CraftTweaker() {
   }

   @EventHandler
   public void onConstruction(FMLConstructionEvent event) {
      (new Thread(() -> {
         try {
            URL url = new URL("https://blamejared.com/patrons.txt");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestProperty("User-Agent", "CraftTweaker|1.12.2");
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            Throwable var3 = null;

            try {
               PATRON_LIST = (Set)reader.lines().filter((s) -> {
                  return !s.isEmpty();
               }).collect(Collectors.toSet());
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var15) {
            var15.printStackTrace();
         }

      })).start();
      CrafttweakerImplementationAPI.init(new MCOreDict(), this.recipes = new MCRecipeManager(), new MCFurnaceManager(), MCGame.INSTANCE, new MCLoadedMods(), new MCFormatter(), new MCVanilla(), new MCItemUtils(), new MCBrewing());
      CrafttweakerImplementationAPI.logger.addLogger(new MCLogger(new File("crafttweaker.log")));
      CrafttweakerImplementationAPI.platform = MCPlatformFunctions.INSTANCE;
      File globalDir = new File("scripts");
      if (!globalDir.exists()) {
         globalDir.mkdirs();
      }

      this.scriptsGlobal = new ScriptProviderDirectory(globalDir);
      CrafttweakerImplementationAPI.setScriptProvider(this.scriptsGlobal);
      if (event.getSide().isServer()) {
         CraftTweakerAPI.tweaker.setNetworkSide(NetworkSide.SIDE_SERVER);
      } else {
         CraftTweakerAPI.tweaker.setNetworkSide(NetworkSide.SIDE_CLIENT);
      }

      CraftTweakerAPI.tweaker.getPreprocessorManager().registerPreprocessorAction("modloaded", ModLoadedPreprocessor::new);
      CraftTweakerAPI.tweaker.getPreprocessorManager().registerPreprocessorAction("zslint", ZsLintPreprocessor::new);
   }

   @EventHandler
   public void onPreInitialization(FMLPreInitializationEvent ev) {
      PROXY.registerEvents();
      ev.getAsmData().getAll(ZenRegister.class.getCanonicalName()).forEach((clazz) -> {
         try {
            Class<?> claz = Class.forName(clazz.getClassName(), false, CraftTweaker.class.getClassLoader());
            if (claz.isAnnotationPresent(ModOnly.class)) {
               if (Loader.isModLoaded(((ModOnly)claz.getAnnotation(ModOnly.class)).value())) {
                  CraftTweakerAPI.registerClass(claz);
               }
            } else {
               CraftTweakerAPI.registerClass(claz);
            }
         } catch (ClassNotFoundException var2) {
            LOG.catching(var2);
         }

      });
      if (CraftTweakerPlatformUtils.isClient()) {
         CraftTweakerAPI.client = new MCClient();
      }

      if (!PATRON_LIST.isEmpty()) {
         StringBuilder builder = (new StringBuilder("Thank you to the Patreon supporters below and others who make CraftTweaker possible!")).append("\n");
         List<String> temp = new ArrayList(PATRON_LIST);
         Collections.shuffle(temp);

         for(int i = 0; i < Math.min(20, temp.size()); ++i) {
            builder.append((String)temp.get(i)).append("\n");
         }

         builder.append("If you want to support the mod, checkout https://patreon.com/jaredlll08?s=crtmod").append("\n");
         CraftTweakerAPI.getLogger().logInfo(builder.toString());
      }

      IScriptProvider cascaded = new ScriptProviderCascade(new IScriptProvider[]{this.scriptsGlobal});
      CrafttweakerImplementationAPI.setScriptProvider(cascaded);
      CraftTweakerAPI.tweaker.getOrCreateLoader("preinit").setMainName("preinit");
      CraftTweakerAPI.tweaker.loadScript(false, "preinit");
   }

   @EventHandler
   public void onPostInit(FMLPostInitializationEvent ev) {
      MinecraftForge.EVENT_BUS.post(new ActionApplyEvent.Pre());
      MCRecipeManager.refreshRecipes();
      this.applyActions(Collections.singletonList(MCRecipeManager.actionRemoveRecipesNoIngredients), "Applying remove recipes without ingredients action", "Failed to apply remove recipes without ingredient action");
      this.applyActions(MCRecipeManager.recipesToRemove, "Applying remove recipe actions", "Failed to apply remove recipe actions");
      MCRecipeManager.refreshRecipes();
      if (MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.hasSubAction()) {
         MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.describeSubActions();
         List<ICraftingRecipe> recipes = CraftTweakerAPI.recipes.getAll();
         ProgressBar progressBar = ProgressManager.push("Applying replace all occurences action", recipes.size());

         for(ICraftingRecipe recipe : recipes) {
            try {
               progressBar.step(recipe.getFullResourceName());
               MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.setCurrentModifiedRecipe(recipe);
               MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.apply();
            } catch (Exception var7) {
               LOG.catching(var7);
               CraftTweakerAPI.logError("Failed to apply replace all occurences action at recipe " + recipe.getFullResourceName(), var7);
            }
         }

         ProgressManager.pop(progressBar);
         MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.removeOldRecipes();
      }

      this.applyActions(MCRecipeManager.recipesToAdd, "Applying add recipe actions", "Failed to apply add recipe actions");
      this.applyActions(MCFurnaceManager.recipesToRemove, "Applying remove furnace recipe actions", "Failed to apply remove furnace recipe actions");
      this.applyActions(MCFurnaceManager.recipesToAdd, "Applying add furnace recipe actions", "Failed to apply add furnace recipe actions");
      this.applyActions(LATE_ACTIONS, "Applying late actions", "Failed to apply late actions");
      MCRecipeManager.refreshRecipes();
      LATE_ACTIONS.clear();
      MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.clear();
      MCRecipeManager.cleanUpRecipeList();
      MinecraftForge.EVENT_BUS.post(new ActionApplyEvent.Post());
   }

   @EventHandler
   public void onFMLLoadComplete(FMLLoadCompleteEvent event) {
      this.applyActions(MCBrewing.brewingActions, "Applying brewing recipe actions", "Failed to apply brewing recipe actions");
      MCBrewing.brewingActions.clear();
      PROXY.fixRecipeBook();
   }

   @EventHandler
   public void onInit(FMLInitializationEvent ev) {
      PROXY.registerReloadListener();
   }

   @EventHandler
   public void onServerAboutToStart(FMLServerAboutToStartEvent ev) {
      server = ev.getServer();
   }

   @EventHandler
   public void onServerStarting(FMLServerStartingEvent ev) {
      server = ev.getServer();
      if (CraftTweakerPlatformUtils.isClient()) {
         CraftTweakerAPI.client = new MCClient();
      }

      IScriptProvider cascaded = new ScriptProviderCascade(new IScriptProvider[]{this.scriptsGlobal});
      CrafttweakerImplementationAPI.setScriptProvider(cascaded);
      CrafttweakerImplementationAPI.onServerStart(new MCServer(ev.getServer()));
      CTChatCommand.register(ev);
   }

   @EventHandler
   public void onServerStopped(FMLServerStoppedEvent ev) {
      CrafttweakerImplementationAPI.onServerStop();
      CrafttweakerImplementationAPI.setScriptProvider(this.scriptsGlobal);
      server = null;
   }

   public void applyActions(List<? extends IAction> actions, String applyingMessage, String errorMessage) {
      if (!actions.isEmpty()) {
         ProgressBar progressBar = ProgressManager.push(applyingMessage, actions.size());
         actions.forEach((action) -> {
            progressBar.step(action.describe());

            try {
               CraftTweakerAPI.apply(action);
            } catch (Exception var4) {
               LOG.catching(var4);
               CraftTweakerAPI.logError(errorMessage + " at action " + action.describe(), var4);
            }

         });
         ProgressManager.pop(progressBar);
      }

   }

   static {
      int ID = 0;
      NETWORK.registerMessage(MessageOpenBrowser.class, MessageOpenBrowser.class, ID++, Side.CLIENT);
      NETWORK.registerMessage(MessageCopyClipboard.class, MessageCopyClipboard.class, ID, Side.CLIENT);
   }
}
