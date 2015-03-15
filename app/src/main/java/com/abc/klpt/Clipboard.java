package com.abc.klpt;

/**
 * Created by abhishekrai on 3/13/15.
 */
public class Clipboard {

    protected int id;
    protected String clipboardText;
    protected String timestamp;

    public Clipboard() {

    }

    public Clipboard(int id, String clipboardText, String timestamp) {
        this.id = id;
        this.clipboardText = clipboardText;
        this.timestamp = timestamp;
    }

    public int getId() {
        return this.id;
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

    public void setClipboardText(String clipboardText) {
        this.clipboardText = clipboardText;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
