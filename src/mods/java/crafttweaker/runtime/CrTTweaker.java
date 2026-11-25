package crafttweaker.runtime;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.CrTScriptLoadEvent;
import crafttweaker.preprocessor.PreprocessorManager;
import crafttweaker.runtime.events.CrTLoaderLoadingEvent;
import crafttweaker.runtime.events.CrTLoadingScriptEventPost;
import crafttweaker.runtime.events.CrTLoadingScriptEventPre;
import crafttweaker.runtime.events.CrTLoadingStartedEvent;
import crafttweaker.runtime.events.CrTScriptLoadingEvent;
import crafttweaker.socket.SingleError;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import crafttweaker.util.SuppressErrorFlag;
import crafttweaker.zenscript.CrTGlobalEnvironment;
import crafttweaker.zenscript.CrtStoringErrorLogger;
import crafttweaker.zenscript.GlobalRegistry;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import stanhebben.zenscript.ZenModule;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.ParseException;

public class CrTTweaker implements ITweaker {
   private static final String defaultLoaderName = "crafttweaker";
   private static boolean DEBUG = false;
   private final List<IAction> actions = new ArrayList();
   private final List<ScriptLoader> loaders = new ArrayList();
   private final EventList<CrTLoaderLoadingEvent.Started> CRT_LOADING_STARTED_EVENT_EVENT_LIST = new EventList<CrTLoaderLoadingEvent.Started>();
   private final EventList<CrTLoaderLoadingEvent.Finished> CRT_LOADING_FINISHED_EVENT_EVENT_LIST = new EventList<CrTLoaderLoadingEvent.Finished>();
   private final EventList<CrTLoaderLoadingEvent.Aborted> CRT_LOADING_ABORTED_EVENT_EVENT_LIST = new EventList<CrTLoaderLoadingEvent.Aborted>();
   private final EventList<CrTScriptLoadingEvent.Pre> CRT_LOADING_SCRIPT_PRE_EVENT_LIST = new EventList<CrTScriptLoadingEvent.Pre>();
   private final EventList<CrTScriptLoadingEvent.Post> CRT_LOADING_SCRIPT_POST_EVENT_LIST = new EventList<CrTScriptLoadingEvent.Post>();
   private NetworkSide networkSide = NetworkSide.INVALID_SIDE;
   private PreprocessorManager preprocessorManager = new PreprocessorManager();
   private IScriptProvider scriptProvider;

   public CrTTweaker() {
      PreprocessorManager.registerOwnPreprocessors(this.preprocessorManager);
   }

   public void apply(IAction action) {
      if (!action.validate()) {
         CraftTweakerAPI.logError("Action could not be applied", new UnsupportedOperationException(action.describeInvalid()));
      } else {
         String describe = action.describe();
         if (describe != null && !describe.isEmpty()) {
            CraftTweakerAPI.logInfo(describe);
         }

         action.apply();
         this.actions.add(action);
      }
   }

   public void setScriptProvider(IScriptProvider provider) {
      this.scriptProvider = provider;
   }

   public void load() {
      this.loadScript(false, "crafttweaker");
   }

   public boolean loadScript(boolean isSyntaxCommand, String loaderName) {
      return this.loadScript(isSyntaxCommand, loaderName, (List)null, false);
   }

   private List<ScriptFile> loadPreprocessor(boolean isSyntaxCommand) {
      List<ScriptFile> scriptFiles = this.collectScriptFiles(isSyntaxCommand);

      for(ScriptFile scriptFile : scriptFiles) {
         scriptFile.addAll(this.preprocessorManager.checkFileForPreprocessors(scriptFile));
      }

      scriptFiles.sort(PreprocessorManager.SCRIPT_FILE_COMPARATOR);
      return scriptFiles;
   }

   public boolean loadScript(boolean isSyntaxCommand, List<SingleError> parseExceptions, boolean isLinter, String... loaderNames) {
      return this.loadScript(isSyntaxCommand, this.getOrAddLoader(loaderNames).removeDelay(loaderNames), parseExceptions, isLinter);
   }

   public boolean loadScript(boolean isSyntaxCommand, String loaderName, List<SingleError> parseExceptions, boolean isLinter) {
      return this.loadScript(isSyntaxCommand, parseExceptions, isLinter, loaderName);
   }

   public void loadScript(boolean isSyntaxCommand, ScriptLoader loader) {
      this.loadScript(isSyntaxCommand, loader, (List)null, false);
   }

   private boolean loadScript(boolean isSyntaxCommand, ScriptLoader loader, List<SingleError> parseExceptions, boolean isLinter) {
      if (isSyntaxCommand) {
         CraftTweakerAPI.setSuppressErrorFlag(SuppressErrorFlag.FORCED);
      }

      if (loader == null) {
         CraftTweakerAPI.logError("Error when trying to load with a null loader");
         return false;
      } else {
         CraftTweakerAPI.logInfo("Loading scripts for loader with names " + loader.toString());
         if (loader.isLoaded() && !isSyntaxCommand) {
            CraftTweakerAPI.logDefault("Skipping loading for loader " + loader + " since it's already been loaded");
            return false;
         } else if (loader.isDelayed() && !isSyntaxCommand) {
            CraftTweakerAPI.logDefault("Skipping loading for loader " + loader + " since its execution is being delayed by another mod.");
            return false;
         } else if (loader.getLoaderStage() == ScriptLoader.LoaderStage.INVALIDATED) {
            CraftTweakerAPI.logWarning("Skipping loading for loader " + loader + " since it's become invalidated");
            return false;
         } else {
            loader.setLoaderStage(ScriptLoader.LoaderStage.LOADING);
            if (!isLinter) {
               this.CRT_LOADING_STARTED_EVENT_EVENT_LIST.publish(new CrTLoadingStartedEvent(loader, isSyntaxCommand, this.networkSide));
            }

            this.preprocessorManager.clean();
            Set<String> executed = new HashSet();
            boolean loadSuccessful = true;
            List<ScriptFile> scriptFiles = this.loadPreprocessor(isSyntaxCommand);
            ((CrtStoringErrorLogger)GlobalRegistry.getErrors()).clear();
            Map<String, byte[]> classes = new HashMap();
            IEnvironmentGlobal environmentGlobal = GlobalRegistry.makeGlobalEnvironment(classes, loader.getMainName());
            long totalTime = System.currentTimeMillis();

            for(ScriptFile scriptFile : scriptFiles) {
               String loaderName = loader.getMainName();
               if (!loader.canExecute(scriptFile.getLoaderNames())) {
                  if (!isSyntaxCommand) {
                     CraftTweakerAPI.logDefault(this.getTweakerDescriptor(loaderName) + ": Skipping file " + scriptFile + " as we are currently loading with a different loader");
                  }
               } else {
                  environmentGlobal.getClassNameGenerator().setPrefix(scriptFile.loaderNamesConcatCapitalized());
                  if (!scriptFile.shouldBeLoadedOn(this.networkSide)) {
                     CraftTweakerAPI.logDefault(this.getTweakerDescriptor(loaderName) + ": Skipping file " + scriptFile + " as we are on the wrong side of the Network");
                  } else if (!executed.contains(scriptFile.getEffectiveName())) {
                     executed.add(scriptFile.getEffectiveName());
                     CraftTweakerAPI.logDefault(this.getTweakerDescriptor(loaderName) + ": Loading Script: " + scriptFile);
                     ZenParsedFile zenParsedFile = null;
                     String filename = scriptFile.getEffectiveName();
                     String className = ZenModule.extractClassName(filename);
                     if (!isLinter) {
                        this.CRT_LOADING_SCRIPT_PRE_EVENT_LIST.publish(new CrTLoadingScriptEventPre(filename));
                     }

                     ZenTokener parser = null;

                     try {
                        Reader reader = new InputStreamReader(new BufferedInputStream(scriptFile.open()), StandardCharsets.UTF_8);
                        Throwable runnable = null;

                        try {
                           this.preprocessorManager.postLoadEvent(new CrTScriptLoadEvent(scriptFile));
                           if (scriptFile.isParsingBlocked()) {
                              if (!isLinter) {
                                 this.CRT_LOADING_SCRIPT_POST_EVENT_LIST.publish(new CrTLoadingScriptEventPost(filename));
                              }
                              continue;
                           }

                           parser = new ZenTokener(reader, environmentGlobal.getEnvironment(), filename, scriptFile.areBracketErrorsIgnored());
                           zenParsedFile = new ZenParsedFile(filename, className, parser, environmentGlobal);
                        } catch (Throwable var34) {
                           runnable = var34;
                           throw var34;
                        } finally {
                           if (reader != null) {
                              if (runnable != null) {
                                 try {
                                    reader.close();
                                 } catch (Throwable var33) {
                                    runnable.addSuppressed(var33);
                                 }
                              } else {
                                 reader.close();
                              }
                           }

                        }
                     } catch (IOException var37) {
                        CraftTweakerAPI.logError(this.getTweakerDescriptor(loaderName) + ": Could not load script " + scriptFile + ": " + var37.getMessage());
                        loadSuccessful = false;
                     } catch (ParseException var38) {
                        CraftTweakerAPI.logError(this.getTweakerDescriptor(loaderName) + ": Error parsing " + var38.getFile().getFileName() + ":" + var38.getLine() + " -- " + var38.getExplanation());
                        loadSuccessful = false;
                        if (parseExceptions != null) {
                           parseExceptions.add(new SingleError(var38.getFile().getFileName(), var38.getLine(), var38.getLineOffset(), var38.getExplanation(), SingleError.Level.ERROR));
                        }
                     } catch (Exception var39) {
                        CraftTweakerAPI.logError(this.getTweakerDescriptor(loaderName) + ": Error loading " + scriptFile + ": " + var39.toString(), var39);
                        if (parser != null && parseExceptions != null) {
                           parseExceptions.add(new SingleError(parser.getFile().getFileName(), parser.getLine(), parser.getLineOffset(), "Generic ERROR", SingleError.Level.ERROR));
                        }

                        loadSuccessful = false;
                     }

                     try {
                        if (zenParsedFile == null || scriptFile.isCompileBlocked() || !loadSuccessful) {
                           if (!isLinter) {
                              this.CRT_LOADING_SCRIPT_POST_EVENT_LIST.publish(new CrTLoadingScriptEventPost(filename));
                           }
                           continue;
                        }

                        ZenModule.compileScripts(className, Collections.singletonList(zenParsedFile), environmentGlobal, scriptFile.isDebugEnabled() || DEBUG);
                        if (scriptFile.isExecutionBlocked() || isSyntaxCommand || isLinter) {
                           if (!isLinter) {
                              this.CRT_LOADING_SCRIPT_POST_EVENT_LIST.publish(new CrTLoadingScriptEventPost(filename));
                           }
                           continue;
                        }

                        ZenModule module = new ZenModule(classes, CraftTweakerAPI.class.getClassLoader());
                        Runnable runnable = module.getMain();
                        if (runnable != null) {
                           runnable.run();
                        }
                     } catch (Throwable var35) {
                        CraftTweakerAPI.logError("[" + loaderName + "]: Error executing " + scriptFile + ": " + var35.getMessage(), var35);
                     }

                     if (!isLinter) {
                        this.CRT_LOADING_SCRIPT_POST_EVENT_LIST.publish(new CrTLoadingScriptEventPost(filename));
                     }
                  }
               }
            }

            if (parseExceptions != null) {
               CrtStoringErrorLogger logger = (CrtStoringErrorLogger)GlobalRegistry.getErrors();
               parseExceptions.addAll(logger.getErrors());
               CrTGlobalEnvironment global = (CrTGlobalEnvironment)environmentGlobal;
               parseExceptions.addAll(global.getErrors());
            }

            loader.setLoaderStage(loadSuccessful ? ScriptLoader.LoaderStage.LOADED_SUCCESSFUL : ScriptLoader.LoaderStage.ERROR);
            if (!isLinter) {
               this.CRT_LOADING_FINISHED_EVENT_EVENT_LIST.publish(new CrTLoaderLoadingEvent.Finished(loader, this.networkSide, isSyntaxCommand));
            }

            CraftTweakerAPI.logDefault("Completed script loading in: " + (System.currentTimeMillis() - totalTime) + "ms");
            return loadSuccessful;
         }
      }
   }

   protected List<ScriptFile> collectScriptFiles(boolean isSyntaxCommand) {
      List<ScriptFile> fileList = new ArrayList();
      HashSet<String> collected = new HashSet();
      Iterator<IScriptIterator> scripts = this.scriptProvider.getScripts();

      while(scripts.hasNext()) {
         IScriptIterator script = (IScriptIterator)scripts.next();
         if (!collected.contains(script.getGroupName())) {
            collected.add(script.getGroupName());

            while(script.next()) {
               fileList.add(new ScriptFile(this, script.copyCurrent(), isSyntaxCommand));
            }
         }
      }

      return fileList;
   }

   private String getTweakerDescriptor(String loaderName) {
      return "[" + loaderName + " | " + this.networkSide + "]";
   }

   public List<IAction> getActions() {
      return this.actions;
   }

   public void enableDebug() {
      DEBUG = true;
   }

   public PreprocessorManager getPreprocessorManager() {
      return this.preprocessorManager;
   }

   public NetworkSide getNetworkSide() {
      return this.networkSide;
   }

   public void setNetworkSide(NetworkSide networkSide) {
      this.networkSide = networkSide;
   }

   public void registerLoadStartedEvent(IEventHandler<CrTLoaderLoadingEvent.Started> eventHandler) {
      this.CRT_LOADING_STARTED_EVENT_EVENT_LIST.add(eventHandler);
   }

   public void registerLoadFinishedEvent(IEventHandler<CrTLoaderLoadingEvent.Finished> eventHandler) {
      this.CRT_LOADING_FINISHED_EVENT_EVENT_LIST.add(eventHandler);
   }

   public void registerLoadAbortedEvent(IEventHandler<CrTLoaderLoadingEvent.Aborted> eventHandler) {
      this.CRT_LOADING_ABORTED_EVENT_EVENT_LIST.add(eventHandler);
   }

   public void registerScriptLoadPreEvent(IEventHandler<CrTScriptLoadingEvent.Pre> eventHandler) {
      this.CRT_LOADING_SCRIPT_PRE_EVENT_LIST.add(eventHandler);
   }

   public void registerScriptLoadPostEvent(IEventHandler<CrTScriptLoadingEvent.Post> eventHandler) {
      this.CRT_LOADING_SCRIPT_POST_EVENT_LIST.add(eventHandler);
   }

   private ScriptLoader getLoader(String... names) {
      for(ScriptLoader loader : this.loaders) {
         if (loader.canExecute(names)) {
            return loader;
         }
      }

      return null;
   }

   private ScriptLoader getOrAddLoader(String... names) {
      ScriptLoader loader = this.getLoader(names);
      return loader != null ? loader : this.getOrCreateLoader(names);
   }

   public List<ScriptLoader> getLoaders() {
      return this.loaders;
   }

   public ScriptLoader getOrCreateLoader(String... nameAndAliases) {
      Iterator<ScriptLoader> it = this.loaders.iterator();
      ScriptLoader mergeLoader = null;

      while(it.hasNext()) {
         ScriptLoader loader = (ScriptLoader)it.next();
         if (loader.canExecute(nameAndAliases)) {
            loader.addAliases(nameAndAliases);
            if (mergeLoader == null) {
               mergeLoader = loader;
            } else {
               mergeLoader.addAliases((String[])loader.getNames().toArray(new String[0]));
               mergeLoader.setMainName(loader.getMainName());
               if (loader.getLoaderStage() != ScriptLoader.LoaderStage.NOT_LOADED && loader.getLoaderStage() != ScriptLoader.LoaderStage.INVALIDATED) {
                  mergeLoader.setLoaderStage(loader.getLoaderStage());
               }

               loader.setLoaderStage(ScriptLoader.LoaderStage.INVALIDATED);
               it.remove();
            }
         }
      }

      if (mergeLoader == null) {
         mergeLoader = new ScriptLoader(nameAndAliases);
         this.loaders.add(mergeLoader);
      }

      CraftTweakerAPI.logDefault("Current loaders after merging: " + this.loaders);
      return mergeLoader;
   }

   public void resetLoaderStats() {
      for(ScriptLoader loader : this.loaders) {
         loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
      }

   }
}
