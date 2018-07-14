package com.redrock.schedule.event;

import com.redrock.schedule.model.Position;

import java.util.ArrayList;

/**
 * Created by ：AceMurder
 * Created on ：2016/11/12
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class TimeChooseEvent {
    private ArrayList<Position> positions = new ArrayList<>();


    public TimeChooseEvent(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }
}
