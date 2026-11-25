package goblinbob.mobends.core.pack;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.configuration.CoreClientConfig;
import goblinbob.mobends.core.flux.ObservableMap;
import goblinbob.mobends.core.util.ErrorReporter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class PackManager {
   public static final PackManager INSTANCE = new PackManager();
   private File localDirectory;
   private PackCache cache;
   private ThumbnailProvider thumbnailProvider;
   private ObservableMap<String, LocalBendsPack> localPacks = new ObservableMap<String, LocalBendsPack>();
   private CoreClientConfig config;
   private final List<IBendsPack> appliedPacks = new LinkedList();

   public PackManager() {
   }

   public void initialize(CoreClientConfig config) {
      this.localDirectory = new File(Minecraft.getMinecraft().mcDataDir, "bendspacks");
      this.localDirectory.mkdir();
      this.cache = new PackCache(new File(this.localDirectory, "public_cache"));
      this.thumbnailProvider = new ThumbnailProvider(this.cache);
      this.config = config;

      try {
         this.initLocalPacks();
      } catch (InvalidPackFormatException var3) {
         var3.printStackTrace();
         ErrorReporter.showErrorToPlayer(var3);
      }

   }

   public void initLocalPacks() throws InvalidPackFormatException {
      this.localPacks.clear();
      this.appliedPacks.clear();
      File[] files = this.localDirectory.listFiles();
      if (files != null) {
         for(File file : files) {
            if (file.getAbsolutePath().endsWith(".bendsmeta")) {
               try {
                  LocalBendsPack bendsPack = LocalBendsPack.readFromFile(file);
                  this.localPacks.put(bendsPack.getKey(), bendsPack);
               } catch (IOException var8) {
                  Core.LOG.severe(String.format("Couldn't load local bends pack: '%s'", file.getName()));
               }
            }
         }

         for(String key : this.config.appliedPackKeys) {
            IBendsPack pack = this.localPacks.get(key);
            if (pack != null) {
               this.appliedPacks.add(pack);
            }
         }

         try {
            PackDataProvider.INSTANCE.createBendsPackData(this.appliedPacks);
         } catch (InvalidPackFormatException var7) {
            this.resetAppliedPacks(true);
            throw var7;
         }
      }
   }

   public void setAppliedPacks(Collection<String> packKeys, boolean saveToConfig) throws InvalidPackFormatException {
      List<IBendsPack> newAppliedPacks = new LinkedList();

      for(String key : packKeys) {
         IBendsPack pack = this.localPacks.get(key);
         if (pack != null) {
            newAppliedPacks.add(pack);
         }
      }

      PackDataProvider.INSTANCE.createBendsPackData(newAppliedPacks);
      this.appliedPacks.clear();
      this.appliedPacks.addAll(newAppliedPacks);
      if (saveToConfig) {
         this.config.setAppliedPacks(packKeys);
      }

   }

   public void resetAppliedPacks(boolean saveToConfig) {
      this.appliedPacks.clear();
      if (saveToConfig) {
         this.config.setAppliedPacks(new String[0]);
      }

      try {
         PackDataProvider.INSTANCE.createBendsPackData(this.appliedPacks);
      } catch (InvalidPackFormatException var3) {
         ;
      }

   }

   public Collection<IBendsPack> getAppliedPacks() {
      return this.appliedPacks;
   }

   public Collection<LocalBendsPack> getLocalPacks() {
      return this.localPacks.values();
   }

   public ResourceLocation getThumbnailLocation(String packName, String thumbnailUrl) {
      return this.thumbnailProvider.getThumbnailLocation(packName, thumbnailUrl);
   }

   public File getLocalDirectory() {
      return this.localDirectory;
   }

   public File getMetaFileForPack(String filename) throws IOException {
      File packFile = new File(this.localDirectory, filename + ".bendsmeta");
      packFile.createNewFile();
      return packFile;
   }

   public File getDataFileForPack(String filename) throws IOException {
      File packFile = new File(this.localDirectory, filename + ".bends");
      packFile.createNewFile();
      return packFile;
   }
}
