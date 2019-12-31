package illimiteremi.domowidget.DomoUtils;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import illimiteremi.domowidget.DomoWidgetBdd.DomoBaseSQLite;
import illimiteremi.domowidget.DomoWidgetBdd.ImportWidget;
import illimiteremi.domowidget.DomoWidgetBdd.UtilsDomoWidget;
import illimiteremi.domowidget.R;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.IMPORT_DOMO_WIDGET;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.IMPORT_ICON;

public class FileExplorerActivity extends ListActivity {

    private static final String      TAG                                 = "[DOMO_FILE_EXPLORER]";

    private Context context;

    private List<String> path      = null;
    private EditText     editPath;
    private final String root      =  Environment.getExternalStorageDirectory().toString();
    private String       fileExt   =  "png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals(IMPORT_DOMO_WIDGET)) {
            Log.d(TAG, intent.getAction());
            fileExt = "txt";
        }

        if (action.equals(IMPORT_ICON)) {
            Log.d(TAG, intent.getAction());
            fileExt = "png";
        }

        setContentView(R.layout.file_explorer_main);
        editPath = findViewById(R.id.editPath);
        getDir(root);

        // Action Ok sur le path
        editPath.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getDir(editPath.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Recuperation des informations du repertoire
     * @param dirPath
     */
    private void getDir(String dirPath) {
        editPath.setText(dirPath);
        try {
            List<String> item = new ArrayList<>();
            path = new ArrayList<>();
            File f = new File(dirPath);
            File[] files = f.listFiles();

            if(!dirPath.equals(root)) {
                item.add(root);
                path.add(root);
                item.add("../");
                path.add(f.getParent());
            }

            for (File file : files) {
                path.add(file.getPath());
                if (file.isDirectory()) {
                    item.add(file.getName() + "/");
                } else {
                    item.add(file.getName());
                }
            }
            ArrayAdapter<String> fileList = new ArrayAdapter<>(this, R.layout.file_explorer_row, item);
            setListAdapter(fileList);
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
        }
    }

    /**
     * onListItemClick
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        try {
            final File file = new File(path.get(position));
            if (file.isDirectory()) {
                if(file.canRead()) {
                    getDir(path.get(position));
                } else {
                    new AlertDialog.Builder(this)
                            //.setIcon(R.drawable.icon)
                            .setTitle("[" + file.getName() + "] Impossible à lire!")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).show();
                }
            } else {
                if (file.getName().substring(file.getName().lastIndexOf(".")).contains(fileExt)) {
                    Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
                    switch (fileExt) {
                        case "png" :
                            new AlertDialog.Builder(this)
                                    .setTitle(file.getName())
                                    .setIcon(drawable)
                                    .setMessage("Intégration de l'image dans DomoWidget ?")
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    copyFileToBdd(file);
                                                    finish();
                                                }
                                            })
                                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }

                                    }).show();
                            break;
                        case "txt" :
                            final ImportWidget importWidget = new ImportWidget(getApplicationContext());
                            final String fileContent = importWidget.readTextFile(file.getAbsolutePath());
                            Boolean fileIsValide = importWidget.isJSONValid(fileContent);
                            if (fileIsValide) {
                                new AlertDialog.Builder(this)
                                        .setTitle(file.getName())
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage("Restauration de la sauvegarde DomoWidget ?")
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.d(TAG, "Update de DomoWidget...");
                                                        importWidget.importBox(fileContent);
                                                        importWidget.importToogleWidget(fileContent);
                                                        importWidget.importPushWidget(fileContent);
                                                        importWidget.importStateWidget(fileContent);
                                                        importWidget.importMultiWidget(fileContent);
                                                        importWidget.importLocationWidget(fileContent);
                                                        importWidget.importVolcalWidget(fileContent);
                                                        importWidget.importSeekBarWidget(fileContent);
                                                        importWidget.importWebCamWidget(fileContent);
                                                        Toast.makeText(context, context.getResources().getString(R.string.import_ok), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                            }

                                        }).show();
                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.file_ko), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Erreur : " + e);
        }
    }

    /**
     * Enregistement du fichier en base
     * @param imageFile
     */
    private void copyFileToBdd(File imageFile) {
        Log.d(TAG, "Enregistrement information image dans la BDD");
        DomoBaseSQLite domoBaseSQLite = new DomoBaseSQLite(getApplicationContext(), UtilsDomoWidget.NOM_BDD, null, UtilsDomoWidget.VERSION_BDD);
        SQLiteDatabase bdd = domoBaseSQLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UtilsDomoWidget.COL_RESS_NAME, imageFile.getName());
        values.put(UtilsDomoWidget.COL_RESS_PATH, imageFile.getAbsolutePath());
        bdd.insert(UtilsDomoWidget.TABLE_RESS_WIDGET, null, values);
        bdd.close();
    }
}
