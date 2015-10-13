package de.h2dc.rpstamp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rene.puf on 08.10.2015.
 */
public class Activity_Edit_Worker extends AppCompatActivity {

    public static List<Worker> pcl;
    Master _master;
    ScrollView scrl;
    List<Button> btn_worker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_edit_worker);

        _master = new Master();

        addWorker(pcl.size());

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
                // Geht zurück zur Main Activity ohne onCreate aufzurufen
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

            myButton[i].setText(pcl.get(i).getFirstName());
            myButton[i].setTextSize(20);
            myButton[i].setGravity(Gravity.CENTER);


            if (pcl.get(i).get_IsRunning()) {
                myButton[i].setBackground(getResources().getDrawable(R.drawable.red_roundedbutton));
                btn_worker.add(btn);
            } else {
                myButton[i].setBackground(getResources().getDrawable(R.drawable.green_roundedbutton));
                btn_worker.add(btn);
            }


            // btn_worker.add(btn);

            //Der Button on Click Listener
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId();

                    //Wenn die Erfassung läuft
                    if (pcl.get(id).get_IsRunning()) {
                        pcl.get(id).set_IsRunning(false);
                        myButton[id].setBackground(getResources().getDrawable(R.drawable.green_roundedbutton));
                       // Activity_HomeScreen.newWorker.add(pcl.get(id));


                        //Toast.makeText(Activity_HomeScreen.this, "Anzahl Logs: " + String.valueOf(log_id + 1), Toast.LENGTH_SHORT).show();
                    } else {
                        pcl.get(id).set_IsRunning(true);
                        myButton[id].setBackground(getResources().getDrawable(R.drawable.red_roundedbutton));



                        // Toast.makeText(Activity_HomeScreen.this, "Anzahl Logs: " + String.valueOf(log_id), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ll.addView(view);
            //    android.util.Log.i("SOMETHING", _wl.get(i).getFirstName());
        }
        this.setContentView(scrl);

    }
}
