package com.blikoon.qrcodescannerlibrary;

public class LogEntry {

    private String rack;
    private String painting;
    private String time;

    public LogEntry(String r, String p, String t)
    {
        rack = r;
        painting = p;
        time = t;
    }

    public String getRack()
    {
        return rack;
    }

    public String getPainting()
    {
        return painting;
    }

    public String getTime()
    {
        return time;
    }

    public String toString()
    {
        return painting + " was put on " + rack + " on " + time;
    }
}
