package illimiteremi.domowidget.FireBaseJobDispatcher;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import illimiteremi.domowidget.DomoUtils.DomoUtils;

/**
 * Created by XZAQ496 on 31/01/2018.
 */

public class FireBaseJobService extends JobService {

    private static final String TAG = "[DOMO_FIREBASE]";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        final String jobTAG      = jobParameters.getTag();
        final Bundle bundle      = jobParameters.getExtras();
        final Integer boxId      = bundle.getInt("boxId");
        final Integer timeRepeat = bundle.getInt("trigger");

        HandlerThread handlerThread = new HandlerThread(jobParameters.getTag());
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Envoi Explicit
                Log.d(TAG, "Rafraîchissement " + timeRepeat + " sec | Id box : " + boxId);
                DomoUtils.updateAllWidget(getApplicationContext());
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
