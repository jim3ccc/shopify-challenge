package com.example.jimchiang.shopify_challenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btn_submit) Button mSubmitButton;
    @BindView(R.id.editText) EditText mEditTextString;
    @BindView(R.id.spinner_user_choices) Spinner mSpinnerUserChoices;
    @BindView(R.id.textView_result) TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getJSONObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Order[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Order[] orders) {
                        StringBuilder builder = new StringBuilder();
                        for(Order o : orders){
                            builder.append(o.email);
                            builder.append("\n");
                        }
                        mTextViewResult.setText(builder.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //set adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerUserChoices.setAdapter(adapter);

        //set listener for spinner
        mSpinnerUserChoices.setOnItemSelectedListener(this);

        //Button onClick
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When we click submit, we get value from editText and spinner
                String editText = mEditTextString.getText().toString();
                Log.d(TAG, "editText value: " + editText);


            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public Observable<Order[]> getJSONObservable(){
        return Observable.defer(new Callable<ObservableSource<? extends Order[]>>() {
            @Override
            public ObservableSource<? extends Order[]> call() throws Exception {
                try {
                    return Observable.just(getJSON());
                }catch (IOException e){
                    return null;
                }
            }
        });
    }

    public Order[] getJSON() throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6")
                .build();

        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            try{
                JSONObject jsonObj = new JSONObject(response.body().string());
                System.out.println(jsonObj.getString("orders"));
                Order[] order = new Gson().fromJson(jsonObj.getString("orders"), Order[].class);
                return order;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
