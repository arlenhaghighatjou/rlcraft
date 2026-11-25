package com.endertech.minecraft.forge.versions;

import com.endertech.common.CommonCollect;
import com.endertech.common.CommonPath;
import com.endertech.common.CommonString;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.math.Percentage;
import com.endertech.minecraft.forge.threads.ForgeThread;
import io.netty.handler.codec.http.HttpMethod;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class Dependencies implements Closeable {
   private static boolean checked = false;
   final UserAgent agent = UserAgent.COMMON_AGENTS.getRandom();
   final BasicCookieStore store;
   final CloseableHttpClient client;
   final RequestConfig config;
   final RequestConfig config_no_rdr;
   final String mcVersion;
   final String domain = "https://www.curseforge.com";
   final IntBounds userDelay = IntBounds.from(Integer.valueOf(3), Integer.valueOf(25));
   final List<Dependencies.Entry> entries = new ArrayList();
   float mainFactor = 0.0F;

   public static void checkIntegrityOnce() {
      if (!checked) {
         checked = true;
         (new ForgeThread() {
            public void run() {
               try {
                  Dependencies dependencies = new Dependencies();
                  Throwable var2 = null;

                  try {
                     dependencies.sleep(dependencies.userDelay);
                     dependencies.resolve();
                     dependencies.check();
                  } catch (Throwable var12) {
                     var2 = var12;
                     throw var12;
                  } finally {
                     if (dependencies != null) {
                        if (var2 != null) {
                           try {
                              dependencies.close();
                           } catch (Throwable var11) {
                              var2.addSuppressed(var11);
                           }
                        } else {
                           dependencies.close();
                        }
                     }

                  }
               } catch (Exception var14) {
                  ForgeMain.developMsg(var14.toString());
               }

            }
         }).start();
      }
   }

   public Dependencies() throws NoSuchAlgorithmException {
      SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[]{"TLSv1", "TLSv1.1"}, (String[])null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
      this.store = new BasicCookieStore();
      this.client = HttpClientBuilder.create().useSystemProperties().setDefaultCookieStore(this.store).setUserAgent(this.agent.toString()).setSSLSocketFactory(ssl).build();
      int timeout = 60000;
      this.config = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setConnectTimeout(timeout).setCookieSpec("compatibility").build();
      this.config_no_rdr = RequestConfig.copy(this.config).setRedirectsEnabled(false).build();
      String version = "1.12.2";
      Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(version);
      if (matcher.find()) {
         version = matcher.group();
      }

      this.mcVersion = version;
   }

   void resolve() throws URISyntaxException, ClientProtocolException, IOException, InterruptedException {
      CommonString.Joiner path = CommonString.Joiner.with("/");
      String minecraft = "minecraft";
      String mods = "mc-mods";
      String parent = "https://www.curseforge.com";
      Dependencies.Content content = null;
      Dependencies.Entry entry = null;
      boolean ok = false;

      for(String link : new String[]{"https://www.curseforge.com", path.join("https://www.curseforge.com", "minecraft", "modpacks"), path.join("https://www.curseforge.com", "minecraft", "mc-mods"), path.join("https://www.curseforge.com", "minecraft", "mc-mods", "forgeendertech")}) {
         if (!ok && link.endsWith("forgeendertech")) {
            break;
         }

         entry = new Dependencies.Entry(link, parent, 1.0F);
         content = this.getContentFrom(entry.link, entry.parent);
         if (content.isStatusOK()) {
            ok = true;
         }

         parent = link;
         this.sleep(this.userDelay);
      }

      String data = new String(content.data);
      Matcher matcher = Pattern.compile("library is required for .+?<ul>(?<list>.+?)</ul>.*?>(?<factor>\\d{1}\\.\\d{1,})<.+?Modpacks", 32).matcher(data);
      if (!matcher.find()) {
         throw new IllegalStateException("Unable to find dependents list!");
      } else {
         String list = matcher.group("list");
         this.mainFactor = Float.parseFloat(matcher.group("factor"));
         ForgeMain.developMsg("Main factor: " + this.mainFactor);
         matcher = Pattern.compile("<li>.*<a href=\"(?<link>[^\"]+).*</li>", 8).matcher(list);
         int count = 0;

         while(matcher.find()) {
            ++count;
            float factor = 0.1F * (float)count;
            this.entries.add(new Dependencies.Entry(matcher.group("link"), parent, factor));
         }

         Dependencies.Entry random = (Dependencies.Entry)CommonCollect.getRandomElementFrom(this.entries);
         if (random != null) {
            this.entries.add(new Dependencies.Entry(entry.link, Optional.of(random.link), 1.0F));
         }

         if (this.entries.isEmpty()) {
            throw new IllegalStateException("Dependents list is empty!");
         } else {
            Collections.shuffle(this.entries);
         }
      }
   }

   void check() {
      for(Dependencies.Entry entry : this.entries) {
         if (Percentage.from(entry.factor * this.mainFactor).takeChance()) {
            try {
               Dependencies.Content reference = this.getLatestFile(entry);
               String location = reference.location.toString();
               String fileName = location.substring(location.lastIndexOf(47) + 1, location.length());
               if (fileName.isEmpty()) {
                  throw new Exception("Empty file name for " + location);
               }

               Path modsDir = Loader.instance().getConfigDir().toPath().getParent().resolve("mods");
               if (ForgeMain.DEVELOPER_ENVIRONMENT) {
                  modsDir = modsDir.resolve("dev");
               }

               File modFile = modsDir.resolve(fileName).toFile();
               if (reference.isStatusOK() && reference.data.length == 0) {
                  throw new Exception("Empty content for " + fileName);
               }

               if (!modFile.exists()) {
                  throw new NoSuchElementException("Mod not installed or older than " + fileName);
               }

               byte[] modContent = FileUtils.readFileToByteArray(modFile);
               if (Arrays.equals(reference.data, modContent)) {
                  ForgeMain.developMsg(fileName + " is identical to the original one.");
               } else {
                  ForgeMain.instance.getLogger().warn("The installed {} mod is different from the original one. It may have been modified or downloaded from a third-party site.", fileName);
               }

               this.sleep(this.userDelay);
            } catch (Exception var9) {
               ForgeMain.developMsg(var9.toString());
            }
         }
      }

   }

   Dependencies.Content getLatestFile(Dependencies.Entry entry) throws ClientProtocolException, IOException, NoSuchElementException, InterruptedException {
      String href = "href=\"(" + entry.link.getPath() + "/download/[0-9]{6,}[^\"]*)\"";
      IntBounds loadDelay = IntBounds.from(Integer.valueOf(5), Integer.valueOf(8));
      String recentFiles = "(?s)Recent Files.+?Minecraft " + this.mcVersion + ".+?" + href;
      Optional<URI> link = this.findLinkAt(entry.link, entry.parent, recentFiles, this.userDelay);
      if (!link.isPresent()) {
         recentFiles = "(?s)-" + this.mcVersion + ".+?-build.+?" + href;
         URI files = URI.create(entry.link.toString() + "/files");
         link = this.findLinkAt(files, Optional.of(entry.link), recentFiles, this.userDelay);
      }

      if (!link.isPresent()) {
         link = Optional.ofNullable(URI.create(entry.link.toString() + "/download"));
      }

      if (link.isPresent()) {
         Optional<URI> parent = link;
         link = this.findLinkAt((URI)link.get(), Optional.of(entry.link), href, loadDelay);
         if (link.isPresent()) {
            return this.getContentFrom((URI)link.get(), parent);
         }
      }

      String name = CommonPath.getFileNameOnly(Paths.get(entry.link.getPath()));
      throw new NoSuchElementException("Latest link to " + name + " not found!");
   }

   Optional<URI> findLinkAt(URI page, Optional<URI> referer, String regex, IntBounds delay) throws ClientProtocolException, IOException, InterruptedException {
      Dependencies.Content content = this.getContentFrom(page, referer);
      this.sleep(delay);
      String data = new String(content.data);
      Matcher matcher = Pattern.compile(regex).matcher(data);
      if (matcher.find()) {
         String path = matcher.group(1);
         return Optional.of(URI.create("https://www.curseforge.com" + path));
      } else {
         return Optional.empty();
      }
   }

   void sleep(IntBounds seconds) throws InterruptedException {
      Thread.sleep((long)(seconds.randomBetween().intValue() * 1000));
   }

   List<Cookie> getCookiesFor(final URI uri) {
      List<Cookie> cookies = new ArrayList<Cookie>() {
         public String toString() {
            StringBuilder builder = new StringBuilder();
            String host = uri.getHost();

            for(Cookie cookie : Dependencies.this.store.getCookies()) {
               String domain = cookie.getDomain();
               if (host == null || domain == null || host.contains(domain)) {
                  if (builder.length() > 0) {
                     builder.append("; ");
                  }

                  builder.append(cookie.getName() + "=" + cookie.getValue());
               }
            }

            return builder.toString();
         }
      };
      return cookies;
   }

   Dependencies.Content getContentFrom(URI link, Optional<URI> referer) throws ClientProtocolException, IOException {
      return this.getContent(link, referer, (Dependencies.Content)null, 0, 10);
   }

   Dependencies.Content getContent(URI link, Optional<URI> referer, Dependencies.Content lastContent, int depth, int maxDepth) throws ClientProtocolException, NoSuchElementException, IOException {
      if (depth >= maxDepth) {
         return lastContent;
      } else {
         HttpGet request = new HttpGet(link);
         request.setConfig(this.config_no_rdr);
         this.setHeadersFor(request, referer);
         CloseableHttpResponse response = this.client.execute(request);
         Throwable var8 = null;

         Dependencies.Content header;
         try {
            Dependencies.Content content = new Dependencies.Content(link, response);
            if (!content.isStatusOK()) {
               Header header = response.getFirstHeader("Location");
               if (header != null) {
                  URI redirect = URI.create(header.getValue());
                  Dependencies.Content var12 = this.getContent(redirect, referer, content, depth + 1, maxDepth);
                  return var12;
               }

               ForgeMain.developMsg(response.getStatusLine() + " -> " + link);
               Dependencies.Content redirect = content;
               return redirect;
            }

            header = content;
         } catch (Throwable var23) {
            var8 = var23;
            throw var23;
         } finally {
            if (response != null) {
               if (var8 != null) {
                  try {
                     response.close();
                  } catch (Throwable var22) {
                     var8.addSuppressed(var22);
                  }
               } else {
                  response.close();
               }
            }

         }

         return header;
      }
   }

   void setHeadersFor(HttpUriRequest request, Optional<URI> referer) {
      boolean isPostMethod = request.getMethod().equals(HttpMethod.POST.name());
      request.setHeader("Host", request.getURI().getHost());
      request.setHeader("DNT", "1");
      request.setHeader("Connection", "keep-alive");
      request.setHeader("Cache-Control", "max-age=0");
      request.setHeader("Upgrade-Insecure-Requests", "1");
      if (isPostMethod) {
         request.setHeader("Content-Type", "application/x-www-form-urlencoded");
      }

      request.setHeader("User-Agent", this.agent.toString());
      request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
      if (referer.isPresent()) {
         request.setHeader("Referer", ((URI)referer.get()).toString());
      }

      request.setHeader("Accept-Encoding", "gzip, deflate, br");
      request.setHeader("Accept-Language", "en-US,en;q=0.5");
      String cookies = this.getCookiesFor(request.getURI()).toString();
      if (!cookies.isEmpty()) {
         request.setHeader("Cookie", cookies);
      }

   }

   public void close() throws IOException {
      this.client.close();
      ForgeMain.developMsg("Client closed");
   }

   static class Content {
      final URI location;
      final StatusLine status;
      final byte[] data;

      public Content(URI location, CloseableHttpResponse response) throws IOException {
         this.location = location;
         this.status = response.getStatusLine();
         HttpEntity entity = response.getEntity();
         this.data = entity != null ? EntityUtils.toByteArray(entity) : new byte[0];
      }

      public boolean isStatusOK() {
         return this.status.getStatusCode() == 200;
      }
   }

   static class Entry {
      final URI link;
      final Optional<URI> parent;
      final float factor;

      public Entry(String link, String parent, float factor) throws URISyntaxException {
         this(new URI(link), parent.isEmpty() ? Optional.empty() : Optional.of(new URI(parent)), factor);
      }

      public Entry(URI link, Optional<URI> parent, float factor) {
         this.link = link;
         this.parent = parent;
         this.factor = factor;
      }

      public String toString() {
         return this.factor + " " + this.link.toString();
      }
   }
}
