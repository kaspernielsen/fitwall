/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reverseproxy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ReverseProxy {

    public static void main(String[] args) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
        } };

        // Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {}

        final HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        final ExecutorService es = Executors.newFixedThreadPool(10);
        server.setExecutor(es);
        server.createContext("/wms", new WMSHandler());
        server.createContext("/echo", new EchoHandler());
        server.setExecutor(null); // creates a default executor

        System.out.println("Listing on " + server.getAddress());
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("SHUTDOWN");
                es.shutdownNow();
                server.stop(0);
            }
        }));

        server.start();
    }

//    static final String BASE_URL = "https://untrusted-root.badssl.com/icons/icon-green.png";
//    static final String BASE_URL = "https://gst-arcgis-p02.prod.sitad.dk/arcgis/rest/services/SampleWorldCities/MapServer/exts/MaritimeChartService/WMSServer?WMS&SERVICENAME=enc_dk_3857&VERSION=1.3.0&LOGIN=StatSofart&PASSWORD=114karls&LAYERS=8,7,6,5,4,3,2,1,0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=TRUE&LAYERS=cells&WIDTH=256&HEIGHT=256&CRS=EPSG%3A3857&STYLES=&BBOX=1056665.4790142775%2C7807583.817161044%2C1076233.3582552825%2C7827151.69640205";

    static final String BASE_URL = "https://gst-arcgis-p02.prod.sitad.dk/arcgis/rest/services/SampleWorldCities/MapServer/exts/MaritimeChartService/WMSServer?";

    static class EchoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String msg = t.getRequestURI().getRawQuery();
            if (msg == null) {
                msg = "";
            }
            System.out.println("Echo: " + msg);
            byte[] b = msg.getBytes();
            t.sendResponseHeaders(200, b.length);
            OutputStream os = t.getResponseBody();
            os.write(b);
            os.flush();
            os.close();
        }
    }

    static class WMSHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            byte[] b = readImage(t.getRequestURI());
            t.sendResponseHeaders(200, b.length);
            OutputStream os = t.getResponseBody();
            os.write(b);
            os.flush();
            os.close();
        }
    }

    static byte[] readImage(URI request) throws IOException {
        String url = BASE_URL + request.getRawQuery();
        System.out.println("Fetching " + url);
        BufferedImage image = ImageIO.read(new URL(url));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
