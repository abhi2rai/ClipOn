package com.abc.klpt;

/**
 * Created by abhishekrai on 3/13/15.
 */
public class Clipboard {

    protected int id;
    protected String clipboardText;
    protected String timestamp;
    protected boolean starred;
    public Clipboard() {

    }

    public Clipboard(int id, String clipboardText, boolean starred,String timestamp) {
        this.id = id;
        this.clipboardText = clipboardText;
        this.timestamp = timestamp;
        this.starred = starred;
    }

    public int getId() {
        return this.id;
    }

    public boolean getStarred() {
        return this.starred;
    }

    public String getClipboardText() {
        return this.clipboardText;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public void setClipboardText(String clipboardText) {
        this.clipboardText = clipboardText;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
