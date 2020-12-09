package com.netty.study.websocket.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

/**
 * @author WangChen
 * @since 2020-12-08 11:09
 **/
public class SslUtils {

    private static final Logger logger = LoggerFactory.getLogger(SslUtils.class);

    private static volatile SSLContext sslContext;

    public static SSLContext createSslContext(String type, String path, String password) throws Exception {

        if (null == sslContext) {
            synchronized (SslUtils.class) {
                if (null == sslContext) {
                    if (Files.notExists(Paths.get(path))) {
                        logger.error("file path not exists");
                        return null;
                    }
                    try {
                        // "JKS"
                        KeyStore keyStore = KeyStore.getInstance(type);

                        keyStore.load(Files.newInputStream(Paths.get(path)), password.toCharArray());
                        //KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
                        //getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        kmf.init(keyStore, password.toCharArray());
                        //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
                        sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(kmf.getKeyManagers(), null, null);

                    } catch (Exception ex) {
                        logger.error("", ex);
                        throw new Exception(ex);
                    }
                }
            }
        }
        return sslContext;
    }
}
