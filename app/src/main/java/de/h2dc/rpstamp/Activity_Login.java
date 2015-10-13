package de.h2dc.rpstamp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Activity_Login extends AppCompatActivity {

    public Button btn;
    EditText editText_name;
    EditText editText_password;
    private String _login_name;
    private String _login_password;
    private List<String> _login_data;

    public String get_login_name() {
        return _login_name;
    }

    public void set_login_name(String login_name) {
        this._login_name = login_name;
    }

    public String get_login_password() {
        return _login_password;
    }

    public void set_login_password(String login_password) {
        //TODO: Eingabe Prüfen ob das Passwort richtig ist = Anmelden zur Probe eventuell loadingBarr oder so
        //TODO: Password verschlüsseln
        this._login_password = login_password;
    }

    public List<String> get_login_data() {
        return _login_data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _login_data = new ArrayList<>();
        editText_name = (EditText) findViewById(R.id.login_name);
        editText_password = (EditText) findViewById(R.id.login_password);

        btn = (Button) this.findViewById(R.id.btn_login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_login_name(editText_name.getText().toString());
                set_login_password(editText_password.getText().toString());

                save_login();
                Master.login_name = get_login_name();
                Master.login_password = get_login_password();


                Intent intent = new Intent(getApplicationContext(),
                        Activity_HomeScreen.class);
                intent.putExtra("login_name", get_login_name());
                intent.putExtra("login_password", get_login_password());
                startActivity(intent);
                finish();
                // setContentView(R.layout.text_layout);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void save_login() {
        Gson gson = new Gson();

        if (_login_data.size() < 1) {
            _login_data.add(get_login_name());
            _login_data.add(get_login_password());
        } else {
            _login_data.clear();
            _login_data.add(get_login_name());
            _login_data.add(get_login_password());
        }
        String json = gson.toJson(_login_data);

        try {
            //write converted json data to a file named "file.json"

            File file = new File(this.getFilesDir().getPath(), getString(R.string.file_name_login));
            file.createNewFile();
            FileWriter writer = new FileWriter(new File(this.getFilesDir().getPath(), getString(R.string.file_name_login)));

            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
