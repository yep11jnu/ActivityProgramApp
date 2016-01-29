package com.assignment.schoolprogram;

/**
 * 6266991
 */
public class SchoolActivity {

    String code = null;
    String name = null;
    private String start = null;
    private String end = null;
    private String date;
    boolean selected = false;

    public SchoolActivity(String code, String name, String start, String end, String date, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.setStart(start);
        this.setEnd(end);
        this.setDate(date);
        this.selected = selected;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
