package rustic.common.tileentity;

public interface ITileEntitySyncable {
   void markForUpdate();

   boolean needsUpdate();

   void clean();
}
