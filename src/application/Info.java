package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Info {
	private final SimpleIntegerProperty length;
    private final SimpleStringProperty time;

    Info(int length, String time) {
        this.length = new SimpleIntegerProperty(length);
        this.time = new SimpleStringProperty(time);
    }
    
    public int getLength() {
        return length.get();
    }

    public void setLength(int l) {
    	length.set(l);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String t) {
    	time.set(t);
    }
}
