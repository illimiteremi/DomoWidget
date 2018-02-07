package illimiteremi.domowidget.DomoUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import illimiteremi.domowidget.DomoWidgetBdd.MultiWidgetBDD;
import illimiteremi.domowidget.DomoWidgetBdd.PushWidgetBDD;
import illimiteremi.domowidget.DomoWidgetBdd.SeekBarWidgetBDD;
import illimiteremi.domowidget.DomoWidgetBdd.ToogleWidgetBDD;
import illimiteremi.domowidget.DomoWidgetBdd.VocalWidgetBDD;
import illimiteremi.domowidget.DomoWidgetBdd.WebCamWidgetBDD;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidget;
import illimiteremi.domowidget.DomoWidgetMulti.MultiWidgetRess;
import illimiteremi.domowidget.DomoWidgetPush.PushWidget;
import illimiteremi.domowidget.DomoWidgetSeekBar.SeekBarWidget;
import illimiteremi.domowidget.DomoWidgetToogle.ToogleWidget;
import illimiteremi.domowidget.DomoWidgetVocal.VocalWidget;
import illimiteremi.domowidget.DomoWidgetWebCam.WebCamWidget;

import static illimiteremi.domowidget.DomoUtils.DomoConstants.BOX;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.LOCATION;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MULTI;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.MULTI_RESS;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.PUSH;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.SEEKBAR;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.STATE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.TOOGLE;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.VOCAL;
import static illimiteremi.domowidget.DomoUtils.DomoConstants.WEBCAM;

/**
 * Created by XZAQ496 on 22/05/2017.
 */

public class DomoBitmapUtils {

    private static final String TAG      = "[DOMO_BITMAP_UTILS]";

    private final Context context;
    private int   widgetWidth;

    /**
     * DomoBitmapUtils
     * @param context
     */
    public DomoBitmapUtils(Context context) {
        this.context = context;
    }

    /**
     * setImageWidth
     * @param widgetWidth
     */
    public void setImageWidth(int widgetWidth) {
        this.widgetWidth = widgetWidth;
    }

    /**
     * Création du message en Bitmap avec la Font Digital
     * @param value
     * @return
     */
    public Bitmap setColorText(String value, String colorRGB) {

        // Calcul de la taille du widget
        String hexColor;
        try {
            hexColor = String.format("#%06X", (0xFFFFFF & Integer.parseInt(colorRGB)));
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            hexColor = "0xFFFFFF";
        }
        // Log.d(TAG, "Couleur RGB: " + colorRGB + " => " + color[0] + "," + color[1] + "," + color[2]);
        try {
            int bitmapWidth = 50 * value.length();
            // Création de l'image
            final int[] color = DomoUtils.hexStringToRGB(hexColor);
            Bitmap myBitmap = Bitmap.createBitmap(bitmapWidth, 100, Bitmap.Config.ARGB_4444);
            Canvas myCanvas = new Canvas(myBitmap);
            myCanvas.drawARGB(colorRGB.contentEquals("0") ? 0 : 100, color[0], color[1], color[2]);
            Paint paint = new Paint();
            Typeface textFont = Typeface.createFromAsset(context.getAssets(), "fonts/ds_digi.ttf");
            paint.setAntiAlias(true);
            paint.setSubpixelText(true);
            paint.setTypeface(textFont);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize(96);
            paint.setTextAlign(Paint.Align.CENTER);
            myCanvas.drawText(value, bitmapWidth / 2, 82, paint);
            Bitmap resizedBitmap = scaleDown(myBitmap, widgetWidth, false);
            myBitmap.recycle();
            return resizedBitmap;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Erreur Bitmap - " + value + " : " + e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Erreur : " + e);
            return null;
        }
    }

    /**
     * scaleDown
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    public Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        maxImageSize = maxImageSize == 0 ? 200 : maxImageSize;
        float ratio = Math.min(maxImageSize / realImage.getWidth(), maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    /**
     * Récuperation de la ressource image d'un widget
     * @param object (objet du widget)
     * @param isON
     * @return
     */
    public Bitmap getBitmapRessource(Object object, boolean isON) {
        Bitmap bitmap = null;
        switch(object.getClass().getSimpleName()) {
            case BOX:
                break;
            case TOOGLE:
                ToogleWidget toogleWidget = (ToogleWidget) object;
                ToogleWidgetBDD toogleWidgetBDD = new ToogleWidgetBDD(context);
                toogleWidgetBDD.open();
                bitmap = toogleWidgetBDD.getRessource(toogleWidget, isON);
                toogleWidgetBDD.close();
                break;
            case STATE:
                // NOTHING
                break;
            case PUSH:
                PushWidget pushWidget = (PushWidget) object;
                PushWidgetBDD pushWidgetBDD = new PushWidgetBDD(context);
                pushWidgetBDD.open();
                bitmap = pushWidgetBDD.getRessource(pushWidget, isON);
                pushWidgetBDD.close();
                break;
            case VOCAL:
                VocalWidget vocalWidget = (VocalWidget) object;
                VocalWidgetBDD vocalWidgetBDD = new VocalWidgetBDD(context);
                vocalWidgetBDD.open();
                bitmap = vocalWidgetBDD.getRessource(vocalWidget);
                vocalWidgetBDD.close();
                break;
            case SEEKBAR:
                SeekBarWidget seekBarWidget = (SeekBarWidget) object;
                SeekBarWidgetBDD seekBarWidgetBDD = new SeekBarWidgetBDD(context);
                seekBarWidgetBDD.open();
                bitmap = seekBarWidgetBDD.getRessource(seekBarWidget);
                seekBarWidgetBDD.close();
                break;
            case WEBCAM:
                WebCamWidget webCamWidget = (WebCamWidget) object;
                WebCamWidgetBDD webCamWidgetBDD = new WebCamWidgetBDD(context);
                webCamWidgetBDD.open();
                bitmap = webCamWidgetBDD.getRessource(webCamWidget);
                webCamWidgetBDD.close();
                break;
            case LOCATION:
                // NOTHING
                break;
            case MULTI:
                MultiWidget multiWidget = (MultiWidget) object;
                MultiWidgetBDD multiWidgetBDD = new MultiWidgetBDD(context);
                multiWidgetBDD.open();
                bitmap = multiWidgetBDD.getRessource(multiWidget, isON);
                multiWidgetBDD.close();
                break;
            case MULTI_RESS:
                MultiWidgetRess multiWidgetRess = (MultiWidgetRess) object;
                MultiWidgetBDD multiWidgetRessBDD = new MultiWidgetBDD(context);
                multiWidgetRessBDD.open();
                bitmap = multiWidgetRessBDD.getMutliWidgetRess(multiWidgetRess, isON);
                multiWidgetRessBDD.close();
                break;
        }
        return bitmap;
    }

    /**
     * addBorderToBitmap
     * @param srcBitmap
     * @param borderWidth
     * @param borderColor
     * @return
     */
    public Bitmap addBorderToBitmap(Bitmap srcBitmap, int borderWidth, int borderColor){
        try {
            // Initialize a new Bitmap to make it bordered bitmap
            Bitmap dstBitmap = Bitmap.createBitmap(
                    srcBitmap.getWidth() + borderWidth * 2,   // Width
                    srcBitmap.getHeight() + borderWidth * 2, // Height
                    Bitmap.Config.ARGB_8888                         // Config
            );

            Canvas canvas = new Canvas(dstBitmap);
            // Initialize a new Paint instance to draw border
            Paint paint = new Paint();
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            paint.setAntiAlias(true);

            Rect rect = new Rect(
                    borderWidth / 2,
                    borderWidth / 2,
                    canvas.getWidth() - borderWidth / 2,
                    canvas.getHeight() - borderWidth / 2
            );
            canvas.drawRect(rect, paint);
            canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);
            srcBitmap.recycle();
            return dstBitmap;
        }  catch (OutOfMemoryError outOfMemoryError) {
            Log.e(TAG, "Erreur Mémoire : " + outOfMemoryError.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG,"Erreur " + e);
            return null;
        }
    }
}
