package com.example.jimchiang.shopify_challenge;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener{
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

        //set editText onFocusChangeListener
        mEditTextString.setOnFocusChangeListener(this);

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
                //hide softkeyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),0);

                //When we click submit, we get value from editText and spinner
                String editText = mEditTextString.getText().toString();
                String spinnerText = mSpinnerUserChoices.getSelectedItem().toString();

                getJSONObservable(spinnerText, editText)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Order[]>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull Order[] orders) {
                                /*StringBuilder builder = new StringBuilder();
                                for(Order o : orders){
                                    builder.append(o.email);
                                    builder.append("\n");
                                }
                                mTextViewResult.setText(builder.toString());*/

                                for(Order o: orders){
                                    if(o.customer != null){
                                        System.out.println(o.customer.get("first_name"));
                                        System.out.println(o.customer.get("last_name"));
                                    }
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                mEditTextString.setHint("Full Name");
                break;
            case 1:
                mEditTextString.setHint("Item Name");
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public Observable<Order[]> getJSONObservable(final String spinnerText, final String editText){
        return Observable.defer(new Callable<ObservableSource<? extends Order[]>>() {
            @Override
            public ObservableSource<? extends Order[]> call() throws Exception {
                try {
                    return Observable.just(getJSON(spinnerText, editText));
                }catch (IOException e){
                    return null;
                }
            }
        });
    }

    public Order[] getJSON(String spinnerText, String editText) throws IOException{
        Log.d(TAG, "getJSON parameters: " + spinnerText + " " + editText);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.query_url))
                .build();

        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            try{
                JSONObject jsonObj = new JSONObject(response.body().string());
                Order[] order = new Gson().fromJson(jsonObj.getString("orders"), Order[].class);
                return order;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view.getId() == R.id.editText && !hasFocus){
            InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
