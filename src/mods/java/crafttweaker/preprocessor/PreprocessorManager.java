package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class PreprocessorManager {
   public static final PreprocessorManager.ScriptFileComparator SCRIPT_FILE_COMPARATOR = new PreprocessorManager.ScriptFileComparator();
   private final EventList<CrTScriptLoadEvent> SCRIPT_LOAD_EVENT_EVENT_LIST = new EventList<CrTScriptLoadEvent>();
   private HashMap<String, PreprocessorFactory<?>> registeredPreprocessorActions = new HashMap();
   public HashMap<String, List<IPreprocessor>> preprocessorActionsPerFile = new HashMap();

   public PreprocessorManager() {
   }

   public void registerPreprocessorAction(String name, PreprocessorFactory<?> preprocessorFactory) {
      this.registeredPreprocessorActions.put(name, preprocessorFactory);
   }

   public void clean() {
      this.preprocessorActionsPerFile.clear();
   }

   private IPreprocessor checkLine(ScriptFile scriptFile, String line, int lineIndex) {
      String s = line.trim();
      if (s.toCharArray().length > 0 && s.toCharArray()[0] == '#') {
         s = s.substring(1);
         String[] splits = s.split(" ");
         if (splits.length > 0) {
            PreprocessorFactory<?> preprocessorFactory = (PreprocessorFactory)this.registeredPreprocessorActions.get(splits[0]);
            if (preprocessorFactory != null) {
               IPreprocessor preprocessor = preprocessorFactory.createPreprocessor(scriptFile.getName(), line, lineIndex);
               preprocessor.executeActionOnFind(scriptFile);
               this.addPreprocessorToFileMap(scriptFile.getName(), preprocessor);
               return preprocessor;
            }
         }
      }

      return null;
   }

   private void addPreprocessorToFileMap(String filename, IPreprocessor preprocessor) {
      if (this.preprocessorActionsPerFile.containsKey(filename)) {
         ((List)this.preprocessorActionsPerFile.get(filename)).add(preprocessor);
      } else {
         List<IPreprocessor> l = new ArrayList();
         l.add(preprocessor);
         this.preprocessorActionsPerFile.put(filename, l);
      }

   }

   public List<IPreprocessor> checkFileForPreprocessors(ScriptFile scriptFile) {
      List<IPreprocessor> preprocessorList = new ArrayList();
      String filename = scriptFile.getName();

      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(scriptFile.open(), "UTF-8"));
         int lineIndex = -1;

         String line;
         while((line = reader.readLine()) != null) {
            ++lineIndex;
            IPreprocessor preprocessor = this.checkLine(scriptFile, line, lineIndex);
            if (preprocessor != null) {
               preprocessorList.add(preprocessor);
            }
         }

         reader.close();
      } catch (IOException var8) {
         CraftTweakerAPI.logError("Could not read preprocessor functions in " + filename);
         var8.printStackTrace();
      }

      this.executePostActions(scriptFile);
      return preprocessorList;
   }

   private void executePostActions(ScriptFile scriptFile) {
      for(Entry<String, List<IPreprocessor>> stringListEntry : this.preprocessorActionsPerFile.entrySet()) {
         for(IPreprocessor preprocessor : (List)stringListEntry.getValue()) {
            preprocessor.executeActionOnFinish(scriptFile);
         }
      }

   }

   public static void registerOwnPreprocessors(PreprocessorManager manager) {
      manager.registerPreprocessorAction("debug", DebugPreprocessor::new);
      manager.registerPreprocessorAction("ignoreBracketErrors", IgnoreBracketErrorPreprocessor::new);
      manager.registerPreprocessorAction("norun", NoRunPreprocessor::new);
      manager.registerPreprocessorAction("loader", LoaderPreprocessor::new);
      manager.registerPreprocessorAction("priority", PriorityPreprocessor::new);
      manager.registerPreprocessorAction("ikwid", IKWIDPreprocessor::new);
      manager.registerPreprocessorAction("profile", ProfilePreprocessor::new);
      manager.registerPreprocessorAction("disable_search_tree", SearchTreePreprocessor::new);
      manager.registerPreprocessorAction("nowarn", NoWarnPreprocessor::new);
      manager.registerPreprocessorAction("sideonly", SideOnlyPreprocessor::new);
   }

   public void registerLoadEventHandler(IEventHandler<CrTScriptLoadEvent> handler) {
      this.SCRIPT_LOAD_EVENT_EVENT_LIST.add(handler);
   }

   public void postLoadEvent(CrTScriptLoadEvent event) {
      this.SCRIPT_LOAD_EVENT_EVENT_LIST.publish(event);
   }

   public static class ScriptFileComparator implements Comparator<ScriptFile>, Serializable {
      private static final long serialVersionUID = -4445491840364336642L;

      public ScriptFileComparator() {
      }

      public int compare(ScriptFile o1, ScriptFile o2) {
         int compare = Integer.compare(o2.getPriority(), o1.getPriority());
         return compare == 0 ? o1.getGroupName().compareToIgnoreCase(o2.getGroupName()) : compare;
      }
   }
}
