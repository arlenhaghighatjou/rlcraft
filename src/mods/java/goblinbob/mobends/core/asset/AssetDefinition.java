package goblinbob.mobends.core.asset;

public class AssetDefinition {
   private int id;
   private int version;
   private AssetLocation path;

   public AssetDefinition() {
   }

   public int getId() {
      return this.id;
   }

   public int getVersion() {
      return this.version;
   }

   public AssetLocation getPath() {
      return this.path;
   }
}
