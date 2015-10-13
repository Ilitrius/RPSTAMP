package de.h2dc.rpstamp;


import java.util.ArrayList;

/**
 * Created by rene.puf on 25.09.2015.
 */

public class Worker {

    private String _firstName = "";
    private String _lastName = "";
    private String _persId = "";
    private String _telNr = "";
    private boolean _running = false;
    private ArrayList<Log> _log_list = new ArrayList<Log>();


    public Worker() {


    }

    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(String firstName) {
        this._firstName = firstName;
    }

    public String get_persId() {
        return _persId;
    }

    public void set_persId(String _persId) {
        this._persId = _persId;
    }

    public String get_telNr() {
        return _telNr;
    }

    public void set_telNr(String _telNr) {
        this._telNr = _telNr;
    }

    public boolean get_IsRunning() {
        return _running;
    }

    public void set_IsRunning(boolean running) {
        this._running = running;
    }

    public ArrayList<Log> get_log_list() {
        return _log_list;
    }

    public void set_log_list(Log logEntry) {

        this._log_list.add(logEntry);
    }
}
