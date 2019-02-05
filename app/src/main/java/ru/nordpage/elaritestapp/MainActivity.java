package ru.nordpage.elaritestapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.nordpage.elaritestapp.api.APIService;
import ru.nordpage.elaritestapp.api.Response;
import ru.nordpage.elaritestapp.api.Service;

import static javax.net.ssl.SSLEngineResult.Status.OK;
import static ru.nordpage.elaritestapp.utils.Constant.ERROR;
import static ru.nordpage.elaritestapp.utils.Constant.TIME;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    TelephonyManager telephonyManager;
    APIService service;
    Scheduler scheduler;
    ProgressDialog pd;

    @BindView(R.id.web_view) WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        service = Service.getService();
        scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.onprogress));
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMax(TIME);
        pd.setCancelable(false);
        pd.setIndeterminate(true);

        check();
    }

    private void OnError(Throwable throwable) {
        pd.dismiss();

        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void OnSuccess(Response response) {
        if (response.getStatus().equals(ERROR)) {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (response.getStatus().equals(OK)){
            pd.dismiss();
            if (response.getUrl() != null && !response.getUrl().isEmpty()) {
                webView.loadUrl(response.getUrl());
            }
        }
    }


    private void check() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        } else {
            pd.show();
            telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String id = telephonyManager.getSubscriberId();
            Observable.interval(TIME, TimeUnit.SECONDS)
                    .flatMap(f -> service.sendRequest(id)
                            .subscribeOn(scheduler))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::OnSuccess, this::OnError);
        }

    }

}
