package crafttweaker.runtime;

import crafttweaker.api.network.NetworkSide;
import crafttweaker.preprocessor.IPreprocessor;
import crafttweaker.runtime.providers.ScriptIteratorZip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScriptFile {
   private IScriptIterator script;
   private List<IPreprocessor> affectingPreprocessors = new ArrayList();
   private ITweaker tweaker;
   private int priority = 0;
   private static final String[] LOADER_NAMES_DEFAULT = new String[]{"crafttweaker"};
   private String[] loaderNames;
   private boolean isExecutionBlocked;
   private boolean isParsingBlocked;
   private boolean isCompileBlocked;
   private boolean isDebugEnabled;
   private boolean ignoreBracketErrors;
   private boolean isSyntaxCommand;
   private NetworkSide networkSide;

   public ScriptFile(ITweaker tweaker, IScriptIterator script, boolean isSyntaxCommand) {
      this.loaderNames = LOADER_NAMES_DEFAULT;
      this.isExecutionBlocked = false;
      this.isParsingBlocked = false;
      this.isCompileBlocked = false;
      this.isDebugEnabled = false;
      this.ignoreBracketErrors = false;
      this.tweaker = tweaker;
      this.script = script;
      this.isSyntaxCommand = isSyntaxCommand;
   }

   public IScriptIterator getScript() {
      return this.script;
   }

   public String getGroupName() {
      return this.script.getGroupName();
   }

   public String getName() {
      return this.script.getName();
   }

   public String getEffectiveName() {
      if (this.script instanceof ScriptIteratorZip) {
         ScriptIteratorZip scriptIterator = (ScriptIteratorZip)this.script;
         return this.getGroupName() + File.separator + scriptIterator.getCurrentName().replace('/', File.separatorChar);
      } else {
         return this.getGroupName();
      }
   }

   public InputStream open() throws IOException {
      return this.script.open();
   }

   public boolean add(IPreprocessor preprocessor) {
      return this.affectingPreprocessors.add(preprocessor);
   }

   public boolean addAll(Collection<? extends IPreprocessor> c) {
      return this.affectingPreprocessors.addAll(c);
   }

   public List<IPreprocessor> getAffectingPreprocessors() {
      return this.affectingPreprocessors;
   }

   public int getPriority() {
      return this.priority;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public String[] getLoaderNames() {
      return this.loaderNames.length == 0 ? LOADER_NAMES_DEFAULT : this.loaderNames;
   }

   public void setLoaderNames(String... loaderNames) {
      this.loaderNames = loaderNames;
   }

   /** @deprecated */
   @Deprecated
   public String getLoaderName() {
      return this.getLoaderNames()[0];
   }

   /** @deprecated */
   @Deprecated
   public void setLoaderName(String loaderName) {
      this.loaderNames = new String[]{loaderName};
   }

   public ITweaker getTweaker() {
      return this.tweaker;
   }

   public boolean isExecutionBlocked() {
      return this.isExecutionBlocked;
   }

   public void setExecutionBlocked(boolean executionBlocked) {
      this.isExecutionBlocked = executionBlocked;
   }

   public boolean isParsingBlocked() {
      return this.isParsingBlocked;
   }

   public void setParsingBlocked(boolean parsingBlocked) {
      this.isParsingBlocked = parsingBlocked;
   }

   public boolean isCompileBlocked() {
      return this.isCompileBlocked;
   }

   public void setCompileBlocked(boolean compileBlocked) {
      this.isCompileBlocked = compileBlocked;
   }

   public boolean isSyntaxCommand() {
      return this.isSyntaxCommand;
   }

   public String toString() {
      return "{[" + this.priority + ":" + this.loaderNamesToString() + "]: " + this.getEffectiveName() + (this.networkSide == null ? "" : "[Side: " + this.networkSide + "]") + "}";
   }

   private String loaderNamesToString() {
      StringBuilder builder = new StringBuilder();

      for(String name : this.getLoaderNames()) {
         builder.append('|').append(name);
      }

      return builder.substring(1);
   }

   public String loaderNamesConcatCapitalized() {
      StringBuilder builder = new StringBuilder();

      for(String name : this.getLoaderNames()) {
         builder.append(capitalize(name));
      }

      return builder.toString();
   }

   private static String capitalize(String in) {
      return in != null && in.length() >= 1 ? Character.toUpperCase(in.charAt(0)) + in.substring(1) : "";
   }

   public boolean areBracketErrorsIgnored() {
      return this.ignoreBracketErrors;
   }

   public void setIgnoreBracketErrors(boolean ignoreBracketErrors) {
      this.ignoreBracketErrors = ignoreBracketErrors;
   }

   public boolean isDebugEnabled() {
      return this.isDebugEnabled;
   }

   public void setDebugEnabled(boolean debugEnabled) {
      this.isDebugEnabled = debugEnabled;
   }

   public void setNetworkSide(NetworkSide networkSide) {
      this.networkSide = networkSide;
   }

   public boolean shouldBeLoadedOn(NetworkSide proposedSide) {
      return this.networkSide == null || this.networkSide.equals(proposedSide);
   }
}
