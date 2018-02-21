package illimiteremi.domowidget.DomoServices;

import android.annotation.SuppressLint;
import android.util.Log;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.READ_TIME_OUT;

/**
 * Created by XZAQ496 on 15/02/2018.
 */

public class DomoOkhttp {

    private static final String TAG         = "[DOMO_OKHTTP]";

    private OkHttpClient        client;
    private static DomoOkhttp   INSTANCE    = null;

    static class TrustAllX509TrustManager implements X509TrustManager {
        public static final X509TrustManager INSTANCE = new TrustAllX509TrustManager();

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * Constructeur Privé
     */
    private DomoOkhttp() {
        Log.d(TAG, "Create DomoOkhttp Instance...");
        try {
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(
                            sslContext(null,
                                    new TrustManager[] {TrustAllX509TrustManager.INSTANCE}).getSocketFactory(),
                            TrustAllX509TrustManager.INSTANCE)
                   // .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier(new HostnameVerifier() {
                        @SuppressLint("BadHostnameVerifier")
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }

    /**
     * SSLContext
     * @param keyManagers
     * @param trustManagers
     * @return
     */
    private static SSLContext sslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }

    /**
     * getInstance
     *
     * @return Instance
     */
    public static DomoOkhttp getInstance() {
        // Log.d(TAG, "getInstance...");
        if (INSTANCE == null) {
            Log.d(TAG, "INSTANCE = NULL");
            INSTANCE = new DomoOkhttp();
        }
        return INSTANCE;
    }

    /**
     * Mise à jour du builder avec le timeOut
     *
     * @param connectTimeout - TimeOut de connexion
     * @return client OkHttp
     */
    public OkHttpClient setBuilder(int connectTimeout) {
        try {
            client.newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                    .build();
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Erreur " + e);
        }
        return client;
    }
}
