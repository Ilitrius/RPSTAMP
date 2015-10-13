package de.h2dc.rpstamp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Activity_HomeScreen extends AppCompatActivity {

    public static List<Worker> newWorker;

    double latitude; //Breitengrad
    double longitude; //Längengrad

    List<Worker> _wl;
    ScrollView scrl;
    List<Button> btn_worker;
    Master _master;
    Context _context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _master = new Master();
        _context = this;
        newWorker = new ArrayList<>();
        _wl = new ArrayList<>();


        Toast.makeText(Activity_HomeScreen.this, "On Create", Toast.LENGTH_SHORT).show();
        // aktuelle Position
        getCurrentGpsLocation();

        // Wenn keine Login Daten (Datei) Existiert wird die if Anweisung im else enden und eine Login Activity zeigen.
        // Diese wird Login Daten Speichern und somit werden die Daten aus diesem Activity gezogen und die
        // if Anweisung wird im ersten Part landen und versuchen diese Daten aus der Datei zu laden.

        //File file = new File(Environment.getExternalStorageDirectory().getPath(), getString(R.string.file_name_login));
        File login_file = new File(this.getFilesDir().getPath(), getString(R.string.file_name_login));
        // Wenn es eine Login Datei gibt werden diese Daten verwendet um sich am Server Anzumelden und eine Arbeiter Liste herrunterladen.
        //TODO: Prüfen ob der Login auch funktioniert. Dann Checken ob es eine neue Arbeiter Liste gibt wenn ja laden und Anbieten !!!!(neue Activity ScrollView with worker for choose)!!!! + ActionBar Add Worker
        if (login_file.exists()) {
            _master.load_login(login_file);
            //Lade Input aus der DB Datei
            _master.load_DB_input(this);
            save_worker_list(_master.input_list_worker);
            load_worker_list();
        } else
        // Wenn keine Login Datei vorhanden ist wird der Login Screen gezeigt um einen Login zu bekommen.
        {
            change_account();
        }
    }

    //TODO Prüfen und VEränderungen der globalen Worker Liste bearbeiten
    /*@Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(Activity_HomeScreen.this, "On Resume", Toast.LENGTH_SHORT).show();

       // newWorker = new ArrayList<>();
        _wl = new ArrayList<>();

       // load_worker_list();
        for (int i = 0; i < newWorker.size();i++){
            _wl.add(newWorker.get(i));
        }
       addWorker(_wl.size());
        newWorker.clear();
        // Normal case behavior follows
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        //return true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        boolean isRunning = false;


        // Prüfen ob ein Arbeiter noch arbeitet
        for (int i = 0; i < _wl.size(); i++) {
            if (_wl.get(i).get_IsRunning() == true) {
                isRunning = true;
            }
        }

        //Wenn das der Fall ist kann nicht beendet werden
        if (isRunning) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("App Verlassen")
                    .setMessage("Are you sure you want to close this activity?")
                    /*.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })*/
                    .setNegativeButton("No", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("App Verlassen")
                    .setMessage("Are you sure you want to close this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save_worker_list(_wl);

                            if (_wl.size() > 0) {
                                _master.save_DB_output(_wl, _context);
                            }
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_worker:
                add_worker();
                return true;
            case R.id.action_del_worker:
                del_worker();
                return true;
            case R.id.action_change_account:
                change_account();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_save:
                save_worker_list(_wl);
                _master.save_DB_output(_wl, this);
                return true;
            case R.id.action_load:
                load_worker_list();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        save_worker_list(_wl);

        if (_wl.size() > 0) {
            _master.save_DB_output(_wl, this);
        }

        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }



    public void add_worker() {
        showWorkerList();
    }

    public void del_worker() {
    }

    public void openSettings() {
    }

    public void change_account() {
        // Send user to LoginSignupActivity.class
        Intent intent = new Intent(getApplicationContext(),
                Activity_Login.class);
        startActivity(intent);
        //finish();
    }

    public void save_worker_list(List listOfWorkers) {
        Gson gson = new Gson();
        String json = gson.toJson(listOfWorkers);

        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(new File(this.getFilesDir().getPath(), getString(R.string.file_name_worker_list)));
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    public void load_worker_list() {
        Gson gson = new Gson();

        File file = new File(this.getFilesDir().getPath(), getString(R.string.file_name_worker_list));
        if (file.exists()) {
            try {

                //Arbeiter Liste aus Datei Laden
                BufferedReader br = new BufferedReader(
                        new FileReader(new File(this.getFilesDir().getPath(), getString(R.string.file_name_worker_list))));
                Worker[] ww = gson.fromJson(br, Worker[].class);
                List<Worker> wwl = Arrays.asList(ww);

                // Wenn etwas in der Aktuellen Liste ist wirde es gelöscht
                if (_wl.size() > 0) {
                    _wl.clear();
                }

                // Die geladene Arbeiter Liste der globalen Liste neu zuweisen
                _wl = wwl;

                //TODO: Wenn Arbeiter aus Datei geladen werden, werden die Zeiten  jedes mal gelöscht. Macht das Sinn?
                // Log Einträge werden gelöscht somit bleibt nur das Team vom vor Tag
               /* for (int i = 0 ; i < _wl.size(); i++){
                    _wl.get(i).log_list.clear();
                }
                Toast.makeText(Activity_HomeScreen.this, getString(R.string.txt_load_found_worker_list_without_logs) + " " , Toast.LENGTH_SHORT).show();*/

                // Die Arbeiter werden visuell zugewiesen
                //TODO: Daten werden geladen aber nicht angezeigt
                addWorker(_wl.size());
                refreshButtons();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Activity_HomeScreen.this, getString(R.string.txt_load_do_not_found_worker_list), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshButtons() {
        for (int i = 0; i < btn_worker.size(); i++) {
            int log_id_start = _wl.get(i).get_log_list().size() - 2;
            int log_id_stop = _wl.get(i).get_log_list().size() - 1;
            android.util.Log.i("INPUT", _wl.get(i).getFirstName());
            if (_wl.get(i).get_log_list().size() >= 2) {
                //Für Debug
                //btn_worker.get(i).setText(_wl.get(i).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + _wl.get(i).get_log_list().get(log_id_start).startTime + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + _wl.get(i).get_log_list().get(log_id_stop).stopTime + "\n" + "Lat: " + latitude + " Long: " + longitude);

                btn_worker.get(i).setText(_wl.get(i).getFirstName());
            } else {
                //Für Debug
                //btn_worker.get(i).setText(_wl.get(i).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + "" + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + "" + "\n" + "Lat: " + latitude + " Long: " + longitude);

                btn_worker.get(i).setText(_wl.get(i).getFirstName());
            }
        }
    }

    public void getCurrentGpsLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public void showWorkerList() {
        // Send user to LoginSignupActivity.class
        Intent intent = new Intent(this,
                Activity_Edit_Worker.class);
        // _master.load_DB_input(this);
        Activity_Edit_Worker.pcl = _master.input_list_ProfitCenter_worker;
        startActivity(intent);
    }


    public void addWorker(int WList) {

        //Vorraussetzungen und das View erzeugen
        scrl = new ScrollView(this);
        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        scrl.addView(ll);
        LayoutInflater layoutInflater = getLayoutInflater();
        final Button[] myButton = new Button[WList];
        btn_worker = new ArrayList<>();

        // Für jeden Arbeiter ein LinearLayout mit Inhalt(Button) erzeugen
        for (int i = 0; i < WList; i++) {

            View view;

            //Dem View das Text Layout zuweisen
            view = layoutInflater.inflate(R.layout.text_layout, scrl, false);
            view.setId(i);

            //Den Button Start Werte zuweisen
            final Button btn = (Button) view.findViewById(R.id.btn_StartStop);
            btn.setId(i);
            myButton[i] = btn;


            //Für Debug
            //myButton[i].setText(_wl.get(i).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + "\n" + "Lat: " + latitude + " Long: " + longitude);
            myButton[i].setText(_wl.get(i).getFirstName());
            myButton[i].setTextSize(20);
            myButton[i].setGravity(Gravity.CENTER);
            myButton[i].setBackground(getResources().getDrawable(R.drawable.green_roundedbutton));

            btn_worker.add(btn);

            //Der Button on Click Listener
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = btn.getId();

                    // myButton[id].setText(_wl.get(id).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + "\n" + "Lat: " + latitude + " Long: " + longitude);

                    //Wenn die Erfassung läuft
                    if (_wl.get(id).get_IsRunning()) {
                        _wl.get(id).set_IsRunning(false);
                        android.util.Log.e("SOMETHING", myButton[id].toString());
                        //myButton[id].setBackgroundColor(Color.GREEN);
                        myButton[id].setBackground(getResources().getDrawable(R.drawable.green_roundedbutton));
                        getCurrentGpsLocation();

                        //Log Eintrag erzeugen
                        getCurrentGpsLocation();
                        Log LOG = new Log();
                        LOG.stopTime = _master.getTimeStamp();
                        LOG.stopDate = _master.getDateStamp();
                        LOG.latitudeStop = latitude;
                        LOG.longitudeStop = longitude;
                        LOG.hint = "Stop Clicked";
                        _wl.get(id).set_log_list(LOG);

                        int log_id = _wl.get(id).get_log_list().size() - 1;

                        //Für Debug
                        //myButton[id].setText(_wl.get(id).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + _wl.get(id).get_log_list().get(log_id - 1).startTime + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + _wl.get(id).get_log_list().get(log_id).stopTime + "\n" + "Lat: " + latitude + " Long: " + longitude);

                        myButton[id].setText(_wl.get(id).getFirstName());
                        myButton[id].setTextSize(20);
                        myButton[id].setGravity(Gravity.CENTER);

                        save_worker_list(_wl);
                        _master.save_DB_output(_wl, _context);
                        //Toast.makeText(Activity_HomeScreen.this, "Anzahl Logs: " + String.valueOf(log_id + 1), Toast.LENGTH_SHORT).show();
                    } else {
                        _wl.get(id).set_IsRunning(true);

                        //myButton[id].setBackgroundColor(Color.RED);
                        myButton[id].setBackground(getResources().getDrawable(R.drawable.red_roundedbutton));

                        //Log Eintrag erzeugen
                        getCurrentGpsLocation();
                        Log LOG = new Log();
                        LOG.startTime = _master.getTimeStamp();
                        LOG.startDate = _master.getDateStamp();
                        LOG.latitudeStart = latitude;
                        LOG.longitudeStart = longitude;
                        LOG.hint = "Start Clicked";
                        _wl.get(id).set_log_list(LOG);

                        int log_id = _wl.get(id).get_log_list().size() - 1;

                        //Für Debug
                        //myButton[id].setText(_wl.get(id).getFirstName() + "\n" + getResources().getString(R.string.btn_worker_start) + ": " + _wl.get(id).get_log_list().get(log_id).startTime + "\n" + getResources().getString(R.string.btn_worker_stop) + ": " + "\n" + "Lat: " + latitude + " Long: " + longitude);
                        myButton[id].setText(_wl.get(id).getFirstName());
                        myButton[id].setTextSize(20);
                        myButton[id].setGravity(Gravity.CENTER);

                        save_worker_list(_wl);
                        _master.save_DB_output(_wl, _context);

                        //android.util.Log.e("SOMETHING", "LOG ID: " + String.valueOf(log_id + 1));

                        // Toast.makeText(Activity_HomeScreen.this, "Anzahl Logs: " + String.valueOf(log_id), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ll.addView(view);
            android.util.Log.i("SOMETHING", _wl.get(i).getFirstName());
        }
        this.setContentView(scrl);
    }


    public void createListWithSomeWorker(int howmany) {
        for (int i = 0; i < howmany; i++) {

            Worker w = new Worker();
            // int zahl = i +1;
            w.setFirstName("Arbeiter" + i);

            Log LOG = new Log();
            LOG.startTime = _master.getTimeStamp();
            LOG.latitudeStart = latitude;
            LOG.longitudeStart = longitude;
            LOG.hint = "Created";

            _wl.add(w);

        }
    }
}