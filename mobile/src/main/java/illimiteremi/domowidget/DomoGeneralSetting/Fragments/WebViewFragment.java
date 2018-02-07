package illimiteremi.domowidget.DomoGeneralSetting.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import illimiteremi.domowidget.R;

/**
 * Created by XZAQ496 on 24/04/2017.
 */

public class WebViewFragment extends Fragment {

    private static final String   TAG      = "[DOMO_WEBVIEW_FRAGMENT]";

    private Context context;

    private WebView webView;

    private String  title;
    private String  url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

        // Récuperation de l'id Widget
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Création d'un nouveau Widget GPS
            url   = bundle.getString("URL");
            title = bundle.getString("TITLE");

            // Maj du titre
            getActivity().setTitle(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setUserAgentString("Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;
            @Override
            public void onProgressChanged(WebView view, int progress) {
                try {
                    if (mProgress == null) {
                        mProgress = new ProgressDialog(context);
                        mProgress.show();
                    }
                    mProgress.setMessage("Chargement de la page : " + String.valueOf(progress) + "%");
                    if (progress == 100) {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Erreur : " + e);
                }
            }
        });
        webView.loadUrl(url);
        webView.requestFocus();
        return view;
    }

}
