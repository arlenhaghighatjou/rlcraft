package me.lucko.spark.lib.okio;

import java.io.IOException;
import javax.annotation.Nullable;

public final class Pipe {
   final long maxBufferSize;
   final Buffer buffer = new Buffer();
   boolean sinkClosed;
   boolean sourceClosed;
   private final Sink sink = new Pipe.PipeSink();
   private final Source source = new Pipe.PipeSource();
   @Nullable
   private Sink foldedSink;

   public Pipe(long maxBufferSize) {
      if (maxBufferSize < 1L) {
         throw new IllegalArgumentException("maxBufferSize < 1: " + maxBufferSize);
      } else {
         this.maxBufferSize = maxBufferSize;
      }
   }

   public final Source source() {
      return this.source;
   }

   public final Sink sink() {
      return this.sink;
   }

   public void fold(Sink sink) throws IOException {
      while(true) {
         boolean closed = false;
         Buffer sinkBuffer;
         synchronized(this.buffer) {
            if (this.foldedSink != null) {
               throw new IllegalStateException("sink already folded");
            }

            if (this.buffer.exhausted()) {
               this.sourceClosed = true;
               this.foldedSink = sink;
               return;
            }

            closed = this.sinkClosed;
            sinkBuffer = new Buffer();
            sinkBuffer.write(this.buffer, this.buffer.size);
            this.buffer.notifyAll();
         }

         boolean success = false;
         boolean var14 = false;

         try {
            var14 = true;
            sink.write(sinkBuffer, sinkBuffer.size);
            if (closed) {
               sink.close();
            } else {
               sink.flush();
            }

            success = true;
            var14 = false;
         } finally {
            if (var14) {
               if (!success) {
                  synchronized(this.buffer) {
                     this.sourceClosed = true;
                     this.buffer.notifyAll();
                  }
               }

            }
         }

         if (!success) {
            synchronized(this.buffer) {
               this.sourceClosed = true;
               this.buffer.notifyAll();
            }
         }
      }
   }

   final class PipeSink implements Sink {
      final PushableTimeout timeout = new PushableTimeout();

      PipeSink() {
      }

      public void write(Buffer source, long byteCount) throws IOException {
         Sink delegate = null;
         synchronized(Pipe.this.buffer) {
            if (Pipe.this.sinkClosed) {
               throw new IllegalStateException("closed");
            }

            while(byteCount > 0L) {
               if (Pipe.this.foldedSink != null) {
                  delegate = Pipe.this.foldedSink;
                  break;
               }

               if (Pipe.this.sourceClosed) {
                  throw new IOException("source is closed");
               }

               long bufferSpaceAvailable = Pipe.this.maxBufferSize - Pipe.this.buffer.size();
               if (bufferSpaceAvailable == 0L) {
                  this.timeout.waitUntilNotified(Pipe.this.buffer);
               } else {
                  long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
                  Pipe.this.buffer.write(source, bytesToWrite);
                  byteCount -= bytesToWrite;
                  Pipe.this.buffer.notifyAll();
               }
            }
         }

         if (delegate != null) {
            this.timeout.push(delegate.timeout());

            try {
               delegate.write(source, byteCount);
            } finally {
               this.timeout.pop();
            }
         }

      }

      public void flush() throws IOException {
         Sink delegate = null;
         synchronized(Pipe.this.buffer) {
            if (Pipe.this.sinkClosed) {
               throw new IllegalStateException("closed");
            }

            if (Pipe.this.foldedSink != null) {
               delegate = Pipe.this.foldedSink;
            } else if (Pipe.this.sourceClosed && Pipe.this.buffer.size() > 0L) {
               throw new IOException("source is closed");
            }
         }

         if (delegate != null) {
            this.timeout.push(delegate.timeout());

            try {
               delegate.flush();
            } finally {
               this.timeout.pop();
            }
         }

      }

      public void close() throws IOException {
         Sink delegate = null;
         synchronized(Pipe.this.buffer) {
            if (Pipe.this.sinkClosed) {
               return;
            }

            if (Pipe.this.foldedSink != null) {
               delegate = Pipe.this.foldedSink;
            } else {
               if (Pipe.this.sourceClosed && Pipe.this.buffer.size() > 0L) {
                  throw new IOException("source is closed");
               }

               Pipe.this.sinkClosed = true;
               Pipe.this.buffer.notifyAll();
            }
         }

         if (delegate != null) {
            this.timeout.push(delegate.timeout());

            try {
               delegate.close();
            } finally {
               this.timeout.pop();
            }
         }

      }

      public Timeout timeout() {
         return this.timeout;
      }
   }

   final class PipeSource implements Source {
      final Timeout timeout = new Timeout();

      PipeSource() {
      }

      public long read(Buffer sink, long byteCount) throws IOException {
         synchronized(Pipe.this.buffer) {
            if (Pipe.this.sourceClosed) {
               throw new IllegalStateException("closed");
            } else {
               while(Pipe.this.buffer.size() == 0L) {
                  if (Pipe.this.sinkClosed) {
                     return -1L;
                  }

                  this.timeout.waitUntilNotified(Pipe.this.buffer);
               }

               long result = Pipe.this.buffer.read(sink, byteCount);
               Pipe.this.buffer.notifyAll();
               return result;
            }
         }
      }

      public void close() throws IOException {
         synchronized(Pipe.this.buffer) {
            Pipe.this.sourceClosed = true;
            Pipe.this.buffer.notifyAll();
         }
      }

      public Timeout timeout() {
         return this.timeout;
      }
   }
}
