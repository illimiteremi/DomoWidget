package illimiteremi.domowidget.DomoServices;


import android.util.Log;


import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.READ_TIME_OUT;

/**
 * Created by XZAQ496 on 15/02/2018.
 */

public class DomoOkhttp {

    private static final String TAG            = "[DOMO_OKHTTP]";

    private OkHttpClient client;
    private static DomoOkhttp INSTANCE = null;

    /**
     * Constructeur Privé
     */
    private DomoOkhttp() {
        Log.d(TAG, "Create DomoOkhttp Instance...");
        try {
            // Install the all-trusting trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getInstance
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
            Log.e(TAG,"Erreur " + e);
        }
        return client;
    }
}
