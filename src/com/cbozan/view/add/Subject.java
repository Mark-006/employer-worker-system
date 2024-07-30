package com.cbozan.view.add;

import com.cbozan.view.helper.Observer;

public interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifyObservers();
}
