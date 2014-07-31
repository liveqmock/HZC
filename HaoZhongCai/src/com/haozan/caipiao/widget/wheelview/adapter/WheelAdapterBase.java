package com.haozan.caipiao.widget.wheelview.adapter;

import java.util.LinkedList;
import java.util.List;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract class WheelAdapterBase {
    private List<DataSetObserver> datasetObservers;

    public View getEmptyItem(View convertView, ViewGroup parent) {
        return null;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        if (datasetObservers == null) {
            datasetObservers = new LinkedList<DataSetObserver>();
        }
        datasetObservers.add(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (datasetObservers != null) {
            datasetObservers.remove(observer);
        }
    }

    protected void notifyDataChangedEvent() {
        if (datasetObservers != null) {
            for (DataSetObserver observer : datasetObservers) {
                observer.onChanged();
            }
        }
    }

    protected void notifyDataInvalidatedEvent() {
        if (datasetObservers != null) {
            for (DataSetObserver observer : datasetObservers) {
                observer.onInvalidated();
            }
        }
    }

    public int getItemsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getItem(int index, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }
}
