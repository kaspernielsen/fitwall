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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 */
public class DooBar {

    //Just old code we know that works
    public static void main(String[] args) throws MalformedURLException, IOException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Activate the new trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
            }
    
            
        String url = "https://frontimg.dk/users/drf-eb/images/40934053.jpg?t%5Bstrip%5D=true&t%5Bcrop%5D%5Bx%5D=0&t%5Bcrop%5D%5By%5D=0&t%5Bcrop%5D%5Bwidth%5D=1600&t%5Bcrop%5D%5Bheight%5D=742&t%5Bresize%5D%5Bwidth%5D=910&t%5Bresize%5D%5Bheight%5D=422&accessToken=1d2aa854d90d582571761f84330a9a4a87d91abf59288615f10eba17392d6c99";
        url = "https://untrusted-root.badssl.com/icons/icon-green.png";
       // url = "https://gst-arcgis-p02.prod.sitad.dk/arcgis/rest/services/SampleWorldCities/MapServer/exts/MaritimeChartService/WMSServer?WMS&SERVICENAME=enc_dk_3857&VERSION=1.3.0&LOGIN=StatSofart&PASSWORD=114karls&LAYERS=8,7,6,5,4,3,2,1,0&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=TRUE&LAYERS=cells&WIDTH=256&HEIGHT=256&CRS=EPSG%3A3857&STYLES=&BBOX=1056665.4790142775%2C7807583.817161044%2C1076233.3582552825%2C7827151.69640205";

        System.out.println("Reading " + url);
        BufferedImage image = ImageIO.read(new URL(url));
        
        OutputStream out = new FileOutputStream("ff.png");
        ImageIO.write(image, "png", out);
        image.flush();
        out.close();
        System.out.println("DONE");
    }
}
