package nonapi.io.github.classgraph.fastzipfilereader;

import io.github.classgraph.ClassGraphException;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.recycler.RecycleOnClose;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.Join;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class LogicalZipFile extends ZipFileSlice implements AutoCloseable {
   public List<FastZipEntry> entries;
   private boolean isMultiReleaseJar;
   Set<String> classpathRoots = Collections.newSetFromMap(new ConcurrentHashMap());
   public String classPathManifestEntryValue;
   public String bundleClassPathManifestEntryValue;
   public String addExportsManifestEntryValue;
   public String addOpensManifestEntryValue;
   public String automaticModuleNameManifestEntryValue;
   public boolean isJREJar;
   static final String META_INF_PATH_PREFIX = "META-INF/";
   private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
   public static final String MULTI_RELEASE_PATH_PREFIX = "META-INF/versions/";
   private static final byte[] IMPLEMENTATION_TITLE_KEY = manifestKeyToBytes("Implementation-Title");
   private static final byte[] SPECIFICATION_TITLE_KEY = manifestKeyToBytes("Specification-Title");
   private static final byte[] CLASS_PATH_KEY = manifestKeyToBytes("Class-Path");
   private static final byte[] BUNDLE_CLASSPATH_KEY = manifestKeyToBytes("Bundle-ClassPath");
   private static final byte[] SPRING_BOOT_CLASSES_KEY = manifestKeyToBytes("Spring-Boot-Classes");
   private static final byte[] SPRING_BOOT_LIB_KEY = manifestKeyToBytes("Spring-Boot-Lib");
   private static final byte[] MULTI_RELEASE_KEY = manifestKeyToBytes("Multi-Release");
   private static final byte[] ADD_EXPORTS_KEY = manifestKeyToBytes("Add-Exports");
   private static final byte[] ADD_OPENS_KEY = manifestKeyToBytes("Add-Opens");
   private static final byte[] AUTOMATIC_MODULE_NAME_KEY = manifestKeyToBytes("Automatic-Module-Name");
   private static byte[] toLowerCase = new byte[256];

   LogicalZipFile(ZipFileSlice zipFileSlice, LogNode log) throws IOException, InterruptedException {
      super(zipFileSlice);
      RecycleOnClose<ZipFileSliceReader, RuntimeException> zipFileSliceReaderRecycleOnClose = this.zipFileSliceReaderRecycler.acquireRecycleOnClose();

      try {
         this.readCentralDirectory(zipFileSliceReaderRecycleOnClose.get(), log);
      } catch (Throwable var7) {
         if (zipFileSliceReaderRecycleOnClose != null) {
            try {
               zipFileSliceReaderRecycleOnClose.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (zipFileSliceReaderRecycleOnClose != null) {
         zipFileSliceReaderRecycleOnClose.close();
      }

   }

   private static Entry<String, Integer> getManifestValue(byte[] manifest, int startIdx) {
      int curr = startIdx;

      int len;
      for(len = manifest.length; curr < len && manifest[curr] == 32; ++curr) {
         ;
      }

      int firstNonSpaceIdx = curr;

      boolean isMultiLine;
      for(isMultiLine = false; curr < len && !isMultiLine; ++curr) {
         byte b = manifest[curr];
         if (b == 13 && curr < len - 1 && manifest[curr + 1] == 10) {
            if (curr < len - 2 && manifest[curr + 2] == 32) {
               isMultiLine = true;
            }
            break;
         }

         if (b == 13 || b == 10) {
            if (curr < len - 1 && manifest[curr + 1] == 32) {
               isMultiLine = true;
            }
            break;
         }
      }

      String val;
      if (!isMultiLine) {
         val = new String(manifest, firstNonSpaceIdx, curr - firstNonSpaceIdx, StandardCharsets.UTF_8);
      } else {
         ByteArrayOutputStream buf = new ByteArrayOutputStream();

         for(curr = firstNonSpaceIdx; curr < len; ++curr) {
            byte b = manifest[curr];
            boolean isLineEnd;
            if (b == 13 && curr < len - 1 && manifest[curr + 1] == 10) {
               curr += 2;
               isLineEnd = true;
            } else if (b != 13 && b != 10) {
               buf.write(b);
               isLineEnd = false;
            } else {
               ++curr;
               isLineEnd = true;
            }

            if (isLineEnd && curr < len && manifest[curr] != 32) {
               break;
            }
         }

         try {
            val = buf.toString("UTF-8");
         } catch (UnsupportedEncodingException var10) {
            throw ClassGraphException.newClassGraphException("UTF-8 encoding unsupported", var10);
         }
      }

      return new SimpleEntry(val.endsWith(" ") ? val.trim() : val, curr);
   }

   private static byte[] manifestKeyToBytes(String key) {
      byte[] bytes = new byte[key.length()];

      for(int i = 0; i < key.length(); ++i) {
         bytes[i] = (byte)Character.toLowerCase(key.charAt(i));
      }

      return bytes;
   }

   private static boolean keyMatchesAtPosition(byte[] manifest, byte[] key, int pos) {
      if (pos + key.length + 1 <= manifest.length && manifest[pos + key.length] == 58) {
         for(int i = 0; i < key.length; ++i) {
            if (toLowerCase[manifest[i + pos]] != key[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void parseManifest(FastZipEntry manifestZipEntry, LogNode log) throws IOException, InterruptedException {
      byte[] manifest = manifestZipEntry.load();
      int i = 0;

      while(i < manifest.length) {
         boolean skip = false;
         if (manifest[i] != 10 && manifest[i] != 13) {
            if (keyMatchesAtPosition(manifest, IMPLEMENTATION_TITLE_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + IMPLEMENTATION_TITLE_KEY.length + 1);
               if (((String)manifestValueAndEndIdx.getKey()).equalsIgnoreCase("Java Runtime Environment")) {
                  this.isJREJar = true;
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, SPECIFICATION_TITLE_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPECIFICATION_TITLE_KEY.length + 1);
               if (((String)manifestValueAndEndIdx.getKey()).equalsIgnoreCase("Java Platform API Specification")) {
                  this.isJREJar = true;
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, CLASS_PATH_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + CLASS_PATH_KEY.length + 1);
               this.classPathManifestEntryValue = (String)manifestValueAndEndIdx.getKey();
               if (log != null) {
                  log.log("Found Class-Path entry in manifest file: " + this.classPathManifestEntryValue);
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, BUNDLE_CLASSPATH_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + BUNDLE_CLASSPATH_KEY.length + 1);
               this.bundleClassPathManifestEntryValue = (String)manifestValueAndEndIdx.getKey();
               if (log != null) {
                  log.log("Found Bundle-ClassPath entry in manifest file: " + this.bundleClassPathManifestEntryValue);
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, SPRING_BOOT_CLASSES_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPRING_BOOT_CLASSES_KEY.length + 1);
               String springBootClassesFieldVal = (String)manifestValueAndEndIdx.getKey();
               if (!springBootClassesFieldVal.equals("BOOT-INF/classes") && !springBootClassesFieldVal.equals("BOOT-INF/classes/") && !springBootClassesFieldVal.equals("WEB-INF/classes") && !springBootClassesFieldVal.equals("WEB-INF/classes/")) {
                  throw new IOException("Spring boot classes are at \"" + springBootClassesFieldVal + "\" rather than the standard location \"BOOT-INF/classes/\" or \"WEB-INF/classes/\" -- please report this at https://github.com/classgraph/classgraph/issues");
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, SPRING_BOOT_LIB_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + SPRING_BOOT_LIB_KEY.length + 1);
               String springBootLibFieldVal = (String)manifestValueAndEndIdx.getKey();
               if (!springBootLibFieldVal.equals("BOOT-INF/lib") && !springBootLibFieldVal.equals("BOOT-INF/lib/") && !springBootLibFieldVal.equals("WEB-INF/lib") && !springBootLibFieldVal.equals("WEB-INF/lib/")) {
                  throw new IOException("Spring boot lib jars are at \"" + springBootLibFieldVal + "\" rather than the standard location \"BOOT-INF/lib/\" or \"WEB-INF/lib/\" -- please report this at https://github.com/classgraph/classgraph/issues");
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, MULTI_RELEASE_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + MULTI_RELEASE_KEY.length + 1);
               if (((String)manifestValueAndEndIdx.getKey()).equalsIgnoreCase("true")) {
                  this.isMultiReleaseJar = true;
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, ADD_EXPORTS_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + ADD_EXPORTS_KEY.length + 1);
               this.addExportsManifestEntryValue = (String)manifestValueAndEndIdx.getKey();
               if (log != null) {
                  log.log("Found Add-Exports entry in manifest file: " + this.addExportsManifestEntryValue);
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, ADD_OPENS_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + ADD_OPENS_KEY.length + 1);
               this.addExportsManifestEntryValue = (String)manifestValueAndEndIdx.getKey();
               if (log != null) {
                  log.log("Found Add-Opens entry in manifest file: " + this.addOpensManifestEntryValue);
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else if (keyMatchesAtPosition(manifest, AUTOMATIC_MODULE_NAME_KEY, i)) {
               Entry<String, Integer> manifestValueAndEndIdx = getManifestValue(manifest, i + AUTOMATIC_MODULE_NAME_KEY.length + 1);
               this.automaticModuleNameManifestEntryValue = (String)manifestValueAndEndIdx.getKey();
               if (log != null) {
                  log.log("Found Automatic-Module-Name entry in manifest file: " + this.automaticModuleNameManifestEntryValue);
               }

               i = ((Integer)manifestValueAndEndIdx.getValue()).intValue();
            } else {
               skip = true;
            }
         } else {
            skip = true;
         }

         if (skip) {
            while(i < manifest.length - 2) {
               if (manifest[i] == 13 && manifest[i + 1] == 10 && manifest[i + 2] != 32) {
                  i += 2;
                  break;
               }

               if ((manifest[i] == 13 || manifest[i] == 10) && manifest[i + 1] != 32) {
                  ++i;
                  break;
               }

               ++i;
            }

            if (i >= manifest.length - 2) {
               break;
            }
         }
      }

   }

   private void readCentralDirectory(ZipFileSliceReader zipFileSliceReader, LogNode log) throws IOException, InterruptedException {
      long eocdPos = -1L;

      for(long i = this.len - 22L; i >= 0L; --i) {
         if (zipFileSliceReader.getInt(i) == 101010256) {
            eocdPos = i;
            break;
         }
      }

      if (eocdPos < 0L) {
         throw new IOException("Jarfile central directory signature not found: " + this.getPath());
      } else {
         long numEnt = (long)zipFileSliceReader.getShort(eocdPos + 8L);
         if (zipFileSliceReader.getShort(eocdPos + 4L) <= 0 && zipFileSliceReader.getShort(eocdPos + 6L) <= 0 && numEnt == (long)zipFileSliceReader.getShort(eocdPos + 10L)) {
            long cenSize = (long)zipFileSliceReader.getInt(eocdPos + 12L) & 4294967295L;
            if (cenSize > eocdPos) {
               throw new IOException("Central directory size out of range: " + cenSize + " vs. " + eocdPos + ": " + this.getPath());
            } else {
               long cenOff = (long)zipFileSliceReader.getInt(eocdPos + 16L) & 4294967295L;
               long cenPos = eocdPos - cenSize;
               long zip64cdLocIdx = eocdPos - 20L;
               if (zip64cdLocIdx >= 0L && zipFileSliceReader.getInt(zip64cdLocIdx) == 117853008) {
                  if (zipFileSliceReader.getInt(zip64cdLocIdx + 4L) > 0 || zipFileSliceReader.getInt(zip64cdLocIdx + 16L) > 1) {
                     throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
                  }

                  long eocdPos64 = zipFileSliceReader.getLong(zip64cdLocIdx + 8L);
                  if (zipFileSliceReader.getInt(eocdPos64) != 101075792) {
                     throw new IOException("Zip64 central directory at location " + eocdPos64 + " does not have Zip64 central directory header: " + this.getPath());
                  }

                  long numEnt64 = zipFileSliceReader.getLong(eocdPos64 + 24L);
                  if (zipFileSliceReader.getInt(eocdPos64 + 16L) > 0 || zipFileSliceReader.getInt(eocdPos64 + 20L) > 0 || numEnt64 != zipFileSliceReader.getLong(eocdPos64 + 32L)) {
                     throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
                  }

                  if (numEnt == 65535L) {
                     numEnt = numEnt64;
                  } else if (numEnt != numEnt64) {
                     numEnt = -1L;
                  }

                  long cenSize64 = zipFileSliceReader.getLong(eocdPos64 + 40L);
                  if (cenSize == 4294967295L) {
                     cenSize = cenSize64;
                  } else if (cenSize != cenSize64) {
                     throw new IOException("Mismatch in central directory size: " + cenSize + " vs. " + cenSize64 + ": " + this.getPath());
                  }

                  cenPos = eocdPos64 - cenSize;
                  long cenOff64 = zipFileSliceReader.getLong(eocdPos64 + 48L);
                  if (cenOff == 4294967295L) {
                     cenOff = cenOff64;
                  } else if (cenOff != cenOff64) {
                     throw new IOException("Mismatch in central directory offset: " + cenOff + " vs. " + cenOff64 + ": " + this.getPath());
                  }
               }

               long locPos = cenPos - cenOff;
               if (locPos < 0L) {
                  throw new IOException("Local file header offset out of range: " + locPos + ": " + this.getPath());
               } else {
                  byte[] entryBytes = cenSize > 2147483639L ? null : new byte[(int)cenSize];
                  if (entryBytes != null) {
                     zipFileSliceReader.read(cenPos, entryBytes, 0, (int)cenSize);
                  }

                  if (numEnt == -1L) {
                     numEnt = 0L;

                     for(long entOff = 0L; entOff + 46L <= cenSize; ++numEnt) {
                        int sig = entryBytes != null ? ZipFileSliceReader.getInt(entryBytes, entOff) : zipFileSliceReader.getInt(cenPos + entOff);
                        if (sig != 33639248) {
                           throw new IOException("Invalid central directory signature: 0x" + Integer.toString(sig, 16) + ": " + this.getPath());
                        }

                        int filenameLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 28L) : zipFileSliceReader.getShort(cenPos + entOff + 28L);
                        int extraFieldLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 30L) : zipFileSliceReader.getShort(cenPos + entOff + 30L);
                        int commentLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 32L) : zipFileSliceReader.getShort(cenPos + entOff + 32L);
                        entOff += (long)(46 + filenameLen + extraFieldLen + commentLen);
                     }
                  }

                  if (numEnt > 2147483639L) {
                     throw new IOException("Too many zipfile entries: " + numEnt);
                  } else if (entryBytes != null && numEnt > (long)(entryBytes.length / 46)) {
                     throw new IOException("Too many zipfile entries: " + numEnt + " (expected a max of " + entryBytes.length / 46 + " based on central directory size)");
                  } else {
                     this.entries = new ArrayList((int)numEnt);
                     FastZipEntry manifestZipEntry = null;
                     CharsetDecoder decoder = null;

                     try {
                        int entSize = 0;

                        for(long entOff = 0L; entOff + 46L <= cenSize; entOff += (long)entSize) {
                           int sig = entryBytes != null ? ZipFileSliceReader.getInt(entryBytes, entOff) : zipFileSliceReader.getInt(cenPos + entOff);
                           if (sig != 33639248) {
                              throw new IOException("Invalid central directory signature: 0x" + Integer.toString(sig, 16) + ": " + this.getPath());
                           }

                           int filenameLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 28L) : zipFileSliceReader.getShort(cenPos + entOff + 28L);
                           int extraFieldLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 30L) : zipFileSliceReader.getShort(cenPos + entOff + 30L);
                           int commentLen = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 32L) : zipFileSliceReader.getShort(cenPos + entOff + 32L);
                           entSize = 46 + filenameLen + extraFieldLen + commentLen;
                           long filenameStartOff = entOff + 46L;
                           long filenameEndOff = filenameStartOff + (long)filenameLen;
                           if (filenameEndOff > cenSize) {
                              if (log != null) {
                                 log.log("Filename extends past end of entry -- skipping entry at offset " + entOff);
                              }
                              break;
                           }

                           String entryName = entryBytes != null ? ZipFileSliceReader.getString(entryBytes, filenameStartOff, filenameLen) : zipFileSliceReader.getString(cenPos + filenameStartOff, filenameLen);
                           String entryNameSanitized = FileUtils.sanitizeEntryPath(entryName, true);
                           if (!entryNameSanitized.isEmpty() && !entryName.endsWith("/")) {
                              int flags = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 8L) : zipFileSliceReader.getShort(cenPos + entOff + 8L);
                              if ((flags & 1) != 0) {
                                 if (log != null) {
                                    log.log("Skipping encrypted zip entry: " + entryNameSanitized);
                                 }
                              } else {
                                 int compressionMethod = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 10L) : zipFileSliceReader.getShort(cenPos + entOff + 10L);
                                 if (compressionMethod != 0 && compressionMethod != 8) {
                                    if (log != null) {
                                       log.log("Skipping zip entry with invalid compression method " + compressionMethod + ": " + entryNameSanitized);
                                    }
                                 } else {
                                    boolean isDeflated = compressionMethod == 8;
                                    long compressedSize = (long)(entryBytes != null ? ZipFileSliceReader.getInt(entryBytes, entOff + 20L) : zipFileSliceReader.getInt(cenPos + entOff + 20L)) & 4294967295L;
                                    long uncompressedSize = (long)(entryBytes != null ? ZipFileSliceReader.getInt(entryBytes, entOff + 24L) : zipFileSliceReader.getInt(cenPos + entOff + 24L)) & 4294967295L;
                                    int externalFileAttributes = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 40L) : zipFileSliceReader.getShort(cenPos + entOff + 40L);
                                    Set<PosixFilePermission> perms;
                                    if (externalFileAttributes == 0) {
                                       perms = null;
                                    } else {
                                       perms = new HashSet();
                                       if ((externalFileAttributes & 256) > 0) {
                                          perms.add(PosixFilePermission.OWNER_READ);
                                       }

                                       if ((externalFileAttributes & 128) > 0) {
                                          perms.add(PosixFilePermission.OWNER_WRITE);
                                       }

                                       if ((externalFileAttributes & 64) > 0) {
                                          perms.add(PosixFilePermission.OWNER_EXECUTE);
                                       }

                                       if ((externalFileAttributes & 32) > 0) {
                                          perms.add(PosixFilePermission.GROUP_READ);
                                       }

                                       if ((externalFileAttributes & 16) > 0) {
                                          perms.add(PosixFilePermission.GROUP_WRITE);
                                       }

                                       if ((externalFileAttributes & 8) > 0) {
                                          perms.add(PosixFilePermission.GROUP_EXECUTE);
                                       }

                                       if ((externalFileAttributes & 4) > 0) {
                                          perms.add(PosixFilePermission.OTHERS_READ);
                                       }

                                       if ((externalFileAttributes & 2) > 0) {
                                          perms.add(PosixFilePermission.OTHERS_WRITE);
                                       }

                                       if ((externalFileAttributes & 1) > 0) {
                                          perms.add(PosixFilePermission.OTHERS_EXECUTE);
                                       }
                                    }

                                    long pos = entryBytes != null ? (long)ZipFileSliceReader.getInt(entryBytes, entOff + 42L) : (long)zipFileSliceReader.getInt(cenPos + entOff + 42L);
                                    long lastModifiedMillis = 0L;
                                    int size;
                                    if (extraFieldLen > 0) {
                                       for(int extraFieldOff = 0; extraFieldOff + 4 < extraFieldLen; extraFieldOff += 4 + size) {
                                          long tagOff = filenameEndOff + (long)extraFieldOff;
                                          int tag = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, tagOff) : zipFileSliceReader.getShort(cenPos + tagOff);
                                          size = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, tagOff + 2L) : zipFileSliceReader.getShort(cenPos + tagOff + 2L);
                                          if (extraFieldOff + 4 + size > extraFieldLen) {
                                             if (log != null) {
                                                log.log("Skipping zip entry with invalid extra field size: " + entryNameSanitized);
                                             }
                                             break;
                                          }

                                          if (tag == 1 && size >= 20) {
                                             long uncompressedSize64 = entryBytes != null ? ZipFileSliceReader.getLong(entryBytes, tagOff + 4L + 0L) : zipFileSliceReader.getLong(cenPos + tagOff + 4L + 0L);
                                             if (uncompressedSize == 4294967295L) {
                                                uncompressedSize = uncompressedSize64;
                                             } else if (uncompressedSize != uncompressedSize64) {
                                                throw new IOException("Mismatch in uncompressed size: " + uncompressedSize + " vs. " + uncompressedSize64 + ": " + entryNameSanitized);
                                             }

                                             long compressedSize64 = entryBytes != null ? ZipFileSliceReader.getLong(entryBytes, tagOff + 4L + 8L) : zipFileSliceReader.getLong(cenPos + tagOff + 4L + 8L);
                                             if (compressedSize == 4294967295L) {
                                                compressedSize = compressedSize64;
                                             } else if (compressedSize != compressedSize64) {
                                                throw new IOException("Mismatch in compressed size: " + compressedSize + " vs. " + compressedSize64 + ": " + entryNameSanitized);
                                             }

                                             if (size >= 28) {
                                                long pos64 = entryBytes != null ? ZipFileSliceReader.getLong(entryBytes, tagOff + 4L + 16L) : zipFileSliceReader.getLong(cenPos + tagOff + 4L + 16L);
                                                if (pos == 4294967295L) {
                                                   pos = pos64;
                                                } else if (pos != pos64) {
                                                   throw new IOException("Mismatch in entry pos: " + pos + " vs. " + pos64 + ": " + entryNameSanitized);
                                                }
                                             }
                                             break;
                                          }

                                          if (tag == 21589 && size >= 5) {
                                             byte bits = entryBytes != null ? ZipFileSliceReader.getByte(entryBytes, tagOff + 4L + 0L) : zipFileSliceReader.getByte(cenPos + tagOff + 4L + 0L);
                                             if ((bits & 1) == 1 && size >= 13) {
                                                lastModifiedMillis = (entryBytes != null ? ZipFileSliceReader.getLong(entryBytes, tagOff + 4L + 1L) : zipFileSliceReader.getLong(cenPos + tagOff + 4L + 1L)) * 1000L;
                                             }
                                          } else if (tag == 22613 && size >= 20) {
                                             lastModifiedMillis = (entryBytes != null ? ZipFileSliceReader.getLong(entryBytes, tagOff + 4L + 8L) : zipFileSliceReader.getLong(cenPos + tagOff + 4L + 8L)) * 1000L;
                                          } else if (tag != 30805 && tag == 28789) {
                                             byte version = entryBytes != null ? ZipFileSliceReader.getByte(entryBytes, tagOff + 4L + 0L) : zipFileSliceReader.getByte(cenPos + tagOff + 4L + 0L);
                                             if (version != 1) {
                                                throw new IOException("Unknown Unicode entry name format " + version + " in extra field: " + entryNameSanitized);
                                             }

                                             if (size > 9) {
                                                byte[] utf8Bytes = entryBytes != null ? ZipFileSliceReader.getBytes(entryBytes, tagOff + 9L, size - 9) : zipFileSliceReader.getBytes(cenPos + tagOff + 9L, size - 9);
                                                if (decoder == null) {
                                                   decoder = StandardCharsets.UTF_8.newDecoder();
                                                   decoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
                                                }

                                                try {
                                                   entryNameSanitized = decoder.decode(ByteBuffer.wrap(utf8Bytes)).toString();
                                                } catch (CharacterCodingException var57) {
                                                   throw new IOException("Malformed Unicode entry name: " + entryNameSanitized);
                                                }
                                             }
                                          }
                                       }
                                    }

                                    if (lastModifiedMillis == 0L) {
                                       int lastModifiedTime = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 12L) : zipFileSliceReader.getShort(cenPos + entOff + 12L);
                                       int lastModifiedDate = entryBytes != null ? ZipFileSliceReader.getShort(entryBytes, entOff + 14L) : zipFileSliceReader.getShort(cenPos + entOff + 14L);
                                       int lastModifiedSecond = (lastModifiedTime & 31) * 2;
                                       int lastModifiedMinute = lastModifiedTime >> 5 & 63;
                                       size = lastModifiedTime >> 11;
                                       int lastModifiedDay = lastModifiedDate & 31;
                                       int lastModifiedMonth = (lastModifiedDate >> 5 & 7) - 1;
                                       int lastModifiedYear = (lastModifiedDate >> 9) + 1980;
                                       Calendar lastModifiedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                       lastModifiedCalendar.set(lastModifiedYear, lastModifiedMonth, lastModifiedDay, size, lastModifiedMinute, lastModifiedSecond);
                                       lastModifiedCalendar.set(14, 0);
                                       lastModifiedMillis = lastModifiedCalendar.getTimeInMillis();
                                    }

                                    if (compressedSize >= 0L && pos >= 0L) {
                                       long locHeaderPos = locPos + pos;
                                       if (locHeaderPos < 0L) {
                                          if (log != null) {
                                             log.log("Skipping zip entry with invalid loc header position: " + entryNameSanitized);
                                          }
                                       } else if (locHeaderPos + 4L >= this.len) {
                                          if (log != null) {
                                             log.log("Unexpected EOF when trying to read LOC header: " + entryNameSanitized);
                                          }
                                       } else {
                                          FastZipEntry entry = new FastZipEntry(this, locHeaderPos, entryNameSanitized, isDeflated, compressedSize, uncompressedSize, this.physicalZipFile.nestedJarHandler, lastModifiedMillis, perms);
                                          this.entries.add(entry);
                                          if (entry.entryName.equals("META-INF/MANIFEST.MF")) {
                                             manifestZipEntry = entry;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     } catch (IndexOutOfBoundsException | EOFException var58) {
                        if (log != null) {
                           log.log("Reached premature EOF" + (this.entries.isEmpty() ? "" : " after reading zip entry " + this.entries.get(this.entries.size() - 1)));
                        }
                     }

                     if (manifestZipEntry != null) {
                        this.parseManifest(manifestZipEntry, log);
                     }

                     if (this.isMultiReleaseJar) {
                        if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
                           if (log != null) {
                              log.log("This is a multi-release jar, but JRE version " + VersionFinder.JAVA_MAJOR_VERSION + " does not support multi-release jars");
                           }
                        } else {
                           if (log != null) {
                              Set<Integer> versionsFound = new HashSet();

                              for(FastZipEntry entry : this.entries) {
                                 if (entry.version > 8) {
                                    versionsFound.add(Integer.valueOf(entry.version));
                                 }
                              }

                              List<Integer> versionsFoundSorted = new ArrayList(versionsFound);
                              CollectionUtils.sortIfNotEmpty(versionsFoundSorted);
                              log.log("This is a multi-release jar, with versions: " + Join.join(", ", versionsFoundSorted));
                           }

                           CollectionUtils.sortIfNotEmpty(this.entries);
                           List<FastZipEntry> unversionedZipEntriesMasked = new ArrayList(this.entries.size());
                           Map<String, String> unversionedPathToVersionedPath = new HashMap();

                           for(FastZipEntry versionedZipEntry : this.entries) {
                              if (!unversionedPathToVersionedPath.containsKey(versionedZipEntry.entryNameUnversioned)) {
                                 unversionedPathToVersionedPath.put(versionedZipEntry.entryNameUnversioned, versionedZipEntry.entryName);
                                 unversionedZipEntriesMasked.add(versionedZipEntry);
                              } else if (log != null) {
                                 log.log((String)unversionedPathToVersionedPath.get(versionedZipEntry.entryNameUnversioned) + " masks " + versionedZipEntry.entryName);
                              }
                           }

                           this.entries = unversionedZipEntriesMasked;
                        }
                     }

                  }
               }
            }
         } else {
            throw new IOException("Multi-disk jarfiles not supported: " + this.getPath());
         }
      }
   }

   public boolean equals(Object o) {
      return super.equals(o);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      return this.getPath();
   }

   public void close() {
      if (this.zipFileSliceReaderRecycler != null) {
         this.zipFileSliceReaderRecycler.close();
      }

      if (this.entries != null) {
         this.entries.clear();
         this.entries = null;
      }

   }

   static {
      for(int i = 32; i < 127; ++i) {
         toLowerCase[i] = (byte)Character.toLowerCase((char)i);
      }

   }
}
