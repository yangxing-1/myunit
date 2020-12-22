package com.example.demo.httplisten;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.stereotype.Component;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.net.httpserver.HttpsServer;

@Component
public class HttpsListenRegister {
    
    public static void main(String[] args) {
        InetSocketAddress socketAddr = new InetSocketAddress(8069);
        HttpsServer httpsServer;
        try {
            httpsServer = HttpsServer.create(socketAddr, 0);
            httpsServer.createContext("/b", new HttpsInboundTransport());
            httpsServer.setExecutor(Executors.newCachedThreadPool());
            try {
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());   //建立证书库
                ks.load(new FileInputStream(System.getProperty("user.dir")+"\\esb_server.jks"), "123456".toCharArray());  //载入证书
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  //建立一个密钥管理工厂
                kmf.init(ks, "123456".toCharArray());  //初始工厂
                
                KeyStore trustKeyStore = KeyStore.getInstance("JKS");
                trustKeyStore.load(new FileInputStream(System.getProperty("user.dir")+"\\esb_root.jks"), "123456".toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustKeyStore);
                
                SSLContext sslContext = SSLContext.getInstance("SSLv3");
                HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext);
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);   //初始化证书
                httpsServer.setHttpsConfigurator(httpsConfigurator);
            } catch (UnrecoverableKeyException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                e.printStackTrace();
            }
            
//            try {
//                httpsServer.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
            httpsServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class HttpsInboundTransport implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            HttpsExchange httpsExchange = (HttpsExchange)httpExchange;
            OutputStream outputStream = httpsExchange.getResponseBody();
            httpsExchange.sendResponseHeaders(200, 0);
            outputStream.write("def456".getBytes());
            outputStream.close();
        }

       

    }
}
