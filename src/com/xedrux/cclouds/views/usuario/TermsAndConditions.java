package com.xedrux.cclouds.views.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.activities.MainActivity;
import com.xedrux.cclouds.utils.Utils;

/**
 * Created by reinier on 03/09/2015.
 */
public class TermsAndConditions extends Activity {
    private CheckBox acceptConditions;
    private Button acceptConditionsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_conditions);

        PreferenceManager.setDefaultValues(getApplicationContext(),
                R.xml.preferences, false);

        acceptConditions = (CheckBox)findViewById(R.id.acceptTerms);
        acceptConditions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(acceptConditions.isChecked())
                {
                    acceptConditionsButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    acceptConditionsButton.setVisibility(View.INVISIBLE);
                }

            }
        });

        acceptConditionsButton = (Button)findViewById(R.id.acceptTermsButton);

        acceptConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.setConditionAccept(getApplication(), acceptConditions.isChecked());
                Intent intent = new Intent(TermsAndConditions.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
