package fionathemortal.betterbiomeblend;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ColorChunkCache {
   public final Lock lock = new ReentrantLock();
   public final Long2ObjectLinkedOpenHashMap<ColorChunk> hash;
   public final Stack<ColorChunk> freeStack;
   public int invalidationCounter;

   public static long getChunkKey(int chunkX, int chunkZ, int colorType) {
      long result = ((long)chunkZ & 67108863L) << 26 | (long)chunkX & 67108863L | (long)colorType << 52;
      return result;
   }

   public ColorChunkCache(int count) {
      this.hash = new Long2ObjectLinkedOpenHashMap(count);
      this.freeStack = new Stack();

      for(int index = 0; index < count; ++index) {
         this.freeStack.add(new ColorChunk());
      }

   }

   public void releaseChunkWithoutLock(ColorChunk chunk) {
      int refCount = chunk.release();
      if (refCount == 0) {
         this.freeStack.push(chunk);
      }

   }

   public void releaseChunk(ColorChunk chunk) {
      int refCount = chunk.release();
      if (refCount == 0) {
         this.lock.lock();
         this.freeStack.push(chunk);
         this.lock.unlock();
      }

   }

   public void invalidateNeighbourhood(int chunkX, int chunkZ) {
      this.lock.lock();
      ++this.invalidationCounter;

      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            for(int colorType = 0; colorType < ColorResolverCompatibility.nextColorID; ++colorType) {
               long key = getChunkKey(chunkX + x, chunkZ + z, colorType);
               ColorChunk chunk = (ColorChunk)this.hash.remove(key);
               if (chunk != null) {
                  this.releaseChunkWithoutLock(chunk);
                  chunk.markAsInvalid();
               }
            }
         }
      }

      this.lock.unlock();
   }

   public void invalidateAll() {
      this.lock.lock();
      ++this.invalidationCounter;
      ObjectIterator var1 = this.hash.values().iterator();

      while(var1.hasNext()) {
         ColorChunk chunk = (ColorChunk)var1.next();
         this.releaseChunkWithoutLock(chunk);
         chunk.markAsInvalid();
      }

      this.hash.clear();
      this.lock.unlock();
   }

   public ColorChunk getChunk(int chunkX, int chunkZ, int colorType) {
      long key = getChunkKey(chunkX, chunkZ, colorType);
      this.lock.lock();
      ColorChunk result = (ColorChunk)this.hash.getAndMoveToFirst(key);
      if (result != null) {
         result.acquire();
      }

      this.lock.unlock();
      return result;
   }

   public void putChunk(ColorChunk chunk) {
      chunk.acquire();
      this.lock.lock();
      ColorChunk prev = (ColorChunk)this.hash.getAndMoveToFirst(chunk.key);
      if (prev != null) {
         ColorChunk olderChunk;
         if (chunk.invalidationCounter >= prev.invalidationCounter) {
            olderChunk = prev;
            this.hash.put(chunk.key, chunk);
         } else {
            olderChunk = chunk;
         }

         this.releaseChunkWithoutLock(olderChunk);
         olderChunk.markAsInvalid();
      } else {
         this.hash.putAndMoveToFirst(chunk.key, chunk);
      }

      this.lock.unlock();
   }

   public ColorChunk newChunk(int chunkX, int chunkZ, int colorType) {
      ColorChunk result = null;
      long key = getChunkKey(chunkX, chunkZ, colorType);
      this.lock.lock();
      if (!this.freeStack.empty()) {
         result = (ColorChunk)this.freeStack.pop();
      } else {
         while(true) {
            long lastKey = this.hash.lastLongKey();
            result = (ColorChunk)this.hash.removeLast();
            if (result.getReferenceCount() == 1) {
               result.release();
               break;
            }

            this.hash.putAndMoveToFirst(lastKey, result);
         }
      }

      result.invalidationCounter = this.invalidationCounter;
      this.lock.unlock();
      result.key = key;
      result.acquire();
      return result;
   }

   public ColorChunk getOrDefaultInitializeChunk(int chunkX, int chunkZ, int colorType) {
      long key = getChunkKey(chunkX, chunkZ, colorType);
      this.lock.lock();
      ColorChunk result = (ColorChunk)this.hash.getAndMoveToFirst(key);
      if (result == null) {
         if (!this.freeStack.empty()) {
            result = (ColorChunk)this.freeStack.pop();
         } else {
            while(true) {
               long lastKey = this.hash.lastLongKey();
               result = (ColorChunk)this.hash.removeLast();
               if (result.getReferenceCount() == 1) {
                  result.release();
                  break;
               }

               this.hash.putAndMoveToFirst(lastKey, result);
            }
         }

         result.key = key;
         result.invalidationCounter = this.invalidationCounter;
         Arrays.fill(result.data, (byte)-1);
         result.acquire();
         ColorChunk prev = (ColorChunk)this.hash.putAndMoveToFirst(result.key, result);
         if (prev != null) {
            this.releaseChunkWithoutLock(prev);
            prev.markAsInvalid();
         }
      }

      result.acquire();
      this.lock.unlock();
      return result;
   }
}
