package de.h2dc.rpstamp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rene.puf on 01.10.2015.
 */
public class Master extends AppCompatActivity {

    public static String login_name;
    public static String login_password;
    public List<String> login_data;
    public List<Worker> input_list_worker;
    public List<Worker> input_list_ProfitCenter_worker;


    public Master() {
        login_data = new ArrayList<>();
        input_list_worker = new ArrayList<>();
        input_list_ProfitCenter_worker = new ArrayList<>();
    }


    public void load_login(File file) {
        Gson gson = new Gson();

        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(file));

            String[] ww = gson.fromJson(br, String[].class);
            List<String> wwl = Arrays.asList(ww);

            if (login_data.size() < 1) {
                login_data.clear();
                login_data = wwl;
                login_name = login_data.get(0);
                login_password = login_data.get(1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load_DB_input(Context context) {
        try {

            File file = new File(context.getFilesDir().getPath(), "input.txt");

            // BufferedReader br1 = new BufferedReader(
            //         new FileReader(file));

            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("ISO-8859-1")));

            String line;

            List<Worker> worker_input_list = new ArrayList<>();

            int size_worker = 0;

            while ((line = br.readLine()) != null) {
                android.util.Log.i("INPUT", line);
                line.trim();

                if (line.toLowerCase().contains("n=")) {
                    if (size_worker <= 0) {
                        android.util.Log.i("INPUT", line.substring(2, line.length()));

                        String zahl = line.substring(2, line.length());
                        size_worker = Integer.parseInt(zahl);
                    }
                }
                //Aktuelle Arbeiter Liste
                if (line.toLowerCase().contains("|")) {
                    int index_seperator = line.indexOf("|");

                    android.util.Log.i("INPUT", line.substring(0, index_seperator));
                    android.util.Log.i("INPUT", line.substring(index_seperator + 1, line.length()));

                    String persID = line.substring(0, index_seperator);
                    String name = line.substring(index_seperator + 1, line.length());

                    Worker worker_input = new Worker();

                    worker_input.set_persId(persID);
                    worker_input.setFirstName(name);

                    worker_input_list.add(worker_input);
                }
            }


            //Alle Arbeiter der aktuellen Kolonne

            for (int i = 0; i < size_worker; i++) {
                input_list_worker.add(worker_input_list.get(i));
            }

            //Alle Arbeiter des Profitcenters
            if (input_list_ProfitCenter_worker.size() > 0) {
                input_list_ProfitCenter_worker.clear();
            }
            for (int i = size_worker; i < worker_input_list.size(); i++) {
                input_list_ProfitCenter_worker.add(worker_input_list.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getTimeStamp() {
        SimpleDateFormat s = new SimpleDateFormat("kk:mm", Locale.getDefault());
        return s.format(new Date());
    }

    public String getDateStamp() {
        SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return s.format(new Date());
    }

    public void save_DB_output(List<Worker> workers, Context context) {

        try {
            File file = new File(context.getFilesDir().getPath(), "output.txt");
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            // myOutWriter.append("N="+ workers.size());
            //myOutWriter.append("\n");

            //TODO: There is no guaranteed solution to this problem because the phone number is not physically stored on all SIM-cards, or broadcasted from the network to the phone. = Bedeutet die Phone Number zu bekommen ist recht schwer Tip: Es gibt die Möglichkeit dies über WhatsApp zu tun = wenn nicht vorhanden pech

            for (Worker obj : workers) {

                if (obj.get_log_list().size() <= 0) {
                   /* myOutWriter.append(obj.get_persId() + "|"
                            + obj.getFirstName() + "|"
                            + "|"
                            + "|"
                            + "|"
                            + "TEL" + "|"
                            + "|"
                            + "|"
                            + "|"
                            + "|");
                    myOutWriter.append("\n");*/

                } else {

                    /* //Tagwechsel Simulieren
                    obj.get_log_list().get(0).startDate = "08.10.2015";
                    obj.get_log_list().get(obj.get_log_list().size() - 1).stopDate = "09.10.2015";
                    obj.get_log_list().get(0).startTime = "21:45";
                    obj.get_log_list().get(obj.get_log_list().size() - 1).stopTime = "04:23";*/

                    String timeDifference = get_Duration(obj.get_log_list().get(0).startTime, obj.get_log_list().get(obj.get_log_list().size() - 1).stopTime);

                    // Wenn das Start Datum und das End Datum gleich sind
                    if (obj.get_log_list().get(0).startDate.equals(obj.get_log_list().get(obj.get_log_list().size() - 1).stopDate)) {
                        myOutWriter.append(obj.get_persId() + "|"
                                + obj.getFirstName() + "|"
                                + obj.get_log_list().get(0).startDate + "|"
                                + obj.get_log_list().get(0).startTime + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).stopDate + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).stopTime + "|"
                                + timeDifference + "|"
                                + "TEL" + "|"
                                + obj.get_log_list().get(0).latitudeStart + "|"
                                + obj.get_log_list().get(0).longitudeStart + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).latitudeStop + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).longitudeStop + "|");
                        myOutWriter.append("\n");
                    }
                    // Wenn das Start Datum und das End Datum nicht gleich sind also ein Tageswechsel dabei ist wird dies anders angezeigt
                    else {
                        //Erste Zeit bis 00:00 Uhr
                        myOutWriter.append(obj.get_persId() + "|"
                                + obj.getFirstName() + "|"
                                + obj.get_log_list().get(0).startDate + "|"
                                + obj.get_log_list().get(0).startTime + "|"
                                + obj.get_log_list().get(0).startDate + "|"
                                + "00:00" + "|"
                                + timeDifference + "|"
                                + "TEL" + "|"
                                + obj.get_log_list().get(0).latitudeStart + "|"
                                + obj.get_log_list().get(0).longitudeStart + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).latitudeStop + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).longitudeStop + "|");
                        myOutWriter.append("\n");

                        //Zweite Zeit nach 00:00 Uhr
                        myOutWriter.append(obj.get_persId() + "|"
                                + obj.getFirstName() + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).stopDate + "|"
                                + "00:00" + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).stopDate + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).stopTime + "|"
                                + timeDifference + "|"
                                + "TEL" + "|"
                                + obj.get_log_list().get(0).latitudeStart + "|"
                                + obj.get_log_list().get(0).longitudeStart + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).latitudeStop + "|"
                                + obj.get_log_list().get(obj.get_log_list().size() - 1).longitudeStop + "|");
                        myOutWriter.append("\n");

                    }
                }
            }
            myOutWriter.close();
            fOut.close();
        } catch (IOException e) {

        }

    }

    public String get_Duration(String start, String end) {
        SimpleDateFormat s = new SimpleDateFormat("kk:mm", Locale.getDefault());
        String dauer = "";
        try {
            Date date = s.parse(start);
            Date date1 = s.parse(end);

            if (date1.getTime() < date.getTime()) {

                Date dd = new Date();
                // 1 Tag also 24 Stunden in Millisekunden
                dd.setTime(86400000);

                long bisMidnight = dd.getTime() - date.getTime();

                long schicht = bisMidnight + date1.getTime();

                int tsec = (int) (schicht / 1000);
                int tseconds = tsec % 60;
                tsec /= 60;
                int tminutes = tsec % 60;
                tsec /= 60;
                int thour = tsec % 24;
                tsec /= 24;

                dauer = String.valueOf(
                        thour + ":" +
                                tminutes);
                return dauer;

            } else {
                long difference = date1.getTime() - date.getTime();

                int tsec = (int) (difference / 1000);
                int tseconds = tsec % 60;
                tsec /= 60;
                int tminutes = tsec % 60;
                tsec /= 60;
                int thour = tsec % 24;
                tsec /= 24;

                dauer = String.valueOf(
                        thour + ":" +
                                tminutes + ":" +
                                tseconds);

                return dauer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dauer;
    }

}
