package illimiteremi.domowidget.DomoGeneralSetting;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import illimiteremi.domowidget.DomoAdapter.BoxAdapter;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.BoxSettingFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.EquipementsFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.IconSettingFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WearSettingFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WebViewFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetExportFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetLocationFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetMultiFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetPushFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetSeekBarFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetStateFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetToogleFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetVocalFragment;
import illimiteremi.domowidget.DomoGeneralSetting.Fragments.WidgetWebCamFragment;
import illimiteremi.domowidget.DomoUtils.DomoUtils;
import illimiteremi.domowidget.DomoUtils.FileExplorerActivity;
import illimiteremi.domowidget.DomoWidgetBdd.DomoBaseSQLite;
import illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget;
import illimiteremi.domowidget.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCATION_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MULTI_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PERMISSION_OK;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PUSH_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SEEKBAR_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.STATE_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.TOOGLE_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.URL_PAYPAL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.URL_WORDPRESS;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.VOCAL_LABEL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEBCAM_LABEL;

public class ManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String   TAG      = "[DOMO_MAIN_FRAGMENT]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Context context = getApplicationContext();

        // Init du drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Vérification sur la création / update de la BDD
        DomoBaseSQLite domoBase = new DomoBaseSQLite(getApplicationContext(), UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
        domoBase.getWritableDatabase();
        domoBase.close();

        // Information de l'intent de création d'un widget
        String intentAction = getIntent().getAction() == null ? "" : getIntent().getAction();
        Bundle extras       = getIntent().getExtras();
        Fragment myFragment = null;

        if (intentAction.contentEquals("android.appwidget.action.APPWIDGET_CONFIGURE")){
            int idWidget = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AppWidgetProviderInfo infos = appWidgetManager.getAppWidgetInfo(idWidget);
            Log.d(TAG, "Création d'un Widget : EXTRA_APPWIDGET_ID = " + idWidget + " / " + infos.label);
            // Selection du fragment suivant le type du widget
            //noinspection deprecation
            switch(infos.label) {
                case LOCATION_LABEL:
                    myFragment = new WidgetLocationFragment();
                    break;
                case STATE_LABEL:
                    myFragment = new WidgetStateFragment();
                    break;
                case PUSH_LABEL:
                    myFragment = new WidgetPushFragment();
                    break;
                case SEEKBAR_LABEL:
                    myFragment = new WidgetSeekBarFragment();
                    break;
                case TOOGLE_LABEL:
                    myFragment = new WidgetToogleFragment();
                    break;
                case MULTI_LABEL:
                    myFragment = new WidgetMultiFragment();
                    break;
                case VOCAL_LABEL:
                    myFragment = new WidgetVocalFragment();
                    break;
                case WEBCAM_LABEL:
                    myFragment = new WidgetWebCamFragment();
                    break;
                default:
                    break;
            }

            // Affichage du fragment
            if (myFragment != null) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                myFragment.setArguments(extras);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, myFragment).commitAllowingStateLoss();
            }
        } else {
            Log.d(TAG, "Ouverture de DomoWidget...");
            Random random = new Random();
            Boolean paypal           = random.nextInt(2) == 0 ? true : false;
            Boolean boxExist         = DomoUtils.getAllObjet(context, BOX).size() == 0 ? false : true;
            Boolean networkAvailable = isNetworkAvailable();
            extras                   = new Bundle();
            myFragment               = new BoxSettingFragment();

            if (networkAvailable) {
                if (boxExist) {
                    if (paypal) {
                        extras.putString("URL", URL_PAYPAL);
                        extras.putString("TITLE", getString(R.string.paypal));
                        myFragment = new WebViewFragment();
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                } else {
                    extras.putString("URL", URL_WORDPRESS);
                    extras.putString("TITLE", getString(R.string.tutoriel));
                    myFragment = new WebViewFragment();
                }
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            myFragment.setArguments(extras);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment, myFragment).commitAllowingStateLoss();
            toggle.syncState();
        }

        // Démarrage des services
        DomoUtils.startVoiceService(context, true);
        DomoUtils.startService(context, true);

        // Verification des permissions
        checkPermission();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment myFragment = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Bundle extras = new Bundle();

        // Selection des menus
        switch (id){
            case R.id.nav_manage:
                myFragment = new BoxSettingFragment();
                break;
            case R.id.equipements:
                myFragment = new EquipementsFragment();
                break;
            case R.id.nav_upload_pic:
                myFragment = new IconSettingFragment();
                break;
            case R.id.action:
                myFragment = new WidgetToogleFragment();
                break;
            case R.id.push:
                myFragment = new WidgetPushFragment();
                break;
            case R.id.seekBar:
                myFragment = new WidgetSeekBarFragment();
                break;
            case R.id.mutli:
                myFragment = new WidgetMultiFragment();
                break;
            case R.id.info:
                myFragment = new WidgetStateFragment();
                break;
            case R.id.webcam:
                myFragment = new WidgetWebCamFragment();
                break;
            case R.id.gps:
                myFragment = new WidgetLocationFragment();
                break;
            case R.id.vocal:
                myFragment = new WidgetVocalFragment();
                break;
            case R.id.wear:
                myFragment = new WearSettingFragment();
                break;
            case R.id.exporter:
                myFragment = new WidgetExportFragment();
                break;
            case R.id.importer:
                Intent intent = new Intent(this, FileExplorerActivity.class);
                intent.setAction("IMPORT_DOMO_WIDGET");
                startActivity(intent);
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.paypal:
                myFragment = new WebViewFragment();
                extras.putString("URL", URL_PAYPAL);
                extras.putString("TITLE", getString(R.string.paypal));
                break;
            case R.id.forum:
                myFragment = new WebViewFragment();
                extras.putString("URL", URL_WORDPRESS);
                extras.putString("TITLE", getString(R.string.tutoriel));
                break;
        }

        // Affichage du fragment
        if (myFragment != null) {
            myFragment.setArguments(extras);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_fragment, myFragment).commitAllowingStateLoss();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_OK: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission accordée !");
                } else {
                    Log.d(TAG, "Permission non accordée !");
                }
            }
        }
    }

    /**
     * Check internet
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * checkPermission
     */
    private void checkPermission() {
        // Vérification des permissions
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);

        for (String permission : permissionsNeeded) {
            if (ContextCompat.checkSelfPermission(this, permission) == PERMISSION_DENIED) {
                Log.d(TAG, "Demande de permission : " + permission);
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // Permission deja refusé par l'utilisateur
                    ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PERMISSION_OK);
                }
            }
        }
    }
}
