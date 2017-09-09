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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btn_submit) Button mSubmitButton;
    @BindView(R.id.editText) EditText mEditTextString;
    @BindView(R.id.spinner_user_choices) Spinner mSpinnerUserChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
}
