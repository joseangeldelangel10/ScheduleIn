package com.example.schedulein_20.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TimeSuggestions {
    private final String TAG = "timeSuggestions";
    ArrayList<ArrayList<Events>> daySchedules;
    ArrayList<Events> pivotDay;
    ArrayList<ArrayList<Integer>> mergedSchedules;
    ArrayList<Integer> pivotEvent;
    ArrayList<ArrayList<Integer>> freeSlots;
    ArrayList<Integer> finalSuggestion;
    Integer evDuration;
    Integer A1;
    Integer B1;
    Integer A2;
    Integer B2;

    public TimeSuggestions(ArrayList<ArrayList<Events>> inviteesEvents, int evDuration){
        Log.e(TAG, "starting constructor");
        Log.e(TAG, inviteesEvents.toString());
        this.evDuration = evDuration;
        mergedSchedules = new ArrayList<>();
        freeSlots = new ArrayList<>();
        finalSuggestion = new ArrayList<>();
        daySchedules = inviteesEvents;
    }

    public ArrayList<Integer> generateSuggestions(){
        /* If an empty list is returned there are no sugestions */
        // TODO: STILL HANDLE SUGESTIONS FOR FREE SCHEDULES

        for (int i=0; i<daySchedules.size() ; i++) {
            if (daySchedules.get(i).size() != 0){
                pivotDay = (ArrayList<Events>) daySchedules.get(i).clone();
                break;
            }if (i == daySchedules.size()-1){
                ArrayList<Integer> emptyList = new ArrayList<>();
                return emptyList;
            }
        }
        generateRangesList();
        generateMergedSchedule();
        sortMergedSchedules();
        generateFreeSlots();
        filterFreeSlots();

        return finalSuggestion;
    }

    private void filterFreeSlots() {
        ArrayList<ArrayList<Integer>> filteredFreeSlots = new ArrayList<>();
        for(ArrayList<Integer> range:freeSlots){
            Integer duration = range.get(1)-range.get(0);
            if(duration >= evDuration){
                filteredFreeSlots.add(range);
            }
        }

        if(filteredFreeSlots.size() == 0){
            return;
        }

        ArrayList<Double> functionScores = new ArrayList<>();
        for (ArrayList<Integer> range: filteredFreeSlots){
            Float midpoint = Float.valueOf(range.get(1)-range.get(0));
            midpoint = midpoint/2;
            midpoint += range.get(0);
            functionScores.add( functionEvaluation(midpoint) );
        }

        int finalSuggestionIndex = functionScores.indexOf(Collections.max(functionScores));
        ArrayList<Integer> finalSlot = filteredFreeSlots.get(finalSuggestionIndex);
        ArrayList<Integer> fs = new ArrayList<>();
        Float midpoint = Float.valueOf(finalSlot.get(1)-finalSlot.get(0));
        midpoint = midpoint/2;
        midpoint += finalSlot.get(0);
        fs.add((int) (midpoint- (evDuration/2)));
        fs.add((int) (midpoint+ (evDuration/2)));
        finalSuggestion = fs;
    }

    private Double functionEvaluation(Float midpoint) {
        Float var = midpoint-900;
        Float exp = var*var;
        Double res = 0.005*exp;
        return -res;

    }

    private void sortMergedSchedules() {
        ArrayList<ArrayList<Integer>> newMergedSchedules;
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                ArrayList<Integer> A1 = (ArrayList<Integer>) o1;
                ArrayList<Integer> A2 = (ArrayList<Integer>) o2;
                if ( ((ArrayList<Integer>) o1).get(0) < ((ArrayList<Integer>) o2).get(0) ){
                    return -1;
                }else if( ((ArrayList<Integer>) o1).get(0) == ((ArrayList<Integer>) o2).get(0) ){
                    return 0;
                }else {
                    return 1;
                }
            }
        };
        mergedSchedules.sort(comparator);
        Log.e(TAG, "MERGE SCHEDULES SORTED");
        Log.e(TAG, mergedSchedules.toString());
    }

    private void generateFreeSlots() {
        Integer newRangeStart = 0;
        Integer newRangeEnd;
        for(ArrayList<Integer> range: mergedSchedules){
            ArrayList<Integer> slot = new ArrayList<>();
            newRangeEnd = range.get(0);
            slot.add(newRangeStart);
            slot.add(newRangeEnd);
            freeSlots.add(slot);
            newRangeStart = range.get(1);
        }
        ArrayList<Integer> slot = new ArrayList<>();
        newRangeEnd = 1439;
        slot.add(newRangeStart);
        slot.add(newRangeEnd);
        freeSlots.add(slot);
        Log.e(TAG, "free slots");
        Log.e(TAG, freeSlots.toString());
    }


    private void generateRangesList() {
        Log.e(TAG, "generating ranges list");
        for(Events event:pivotDay){
            ArrayList<Integer> range = new ArrayList<>();
            range.add(event.getStartInMins());
            range.add(event.getEndInMins());
            mergedSchedules.add(range);
        }
        Log.e(TAG, "initial range list: " + mergedSchedules.toString());
    }

    private void generateMergedSchedule() {
        Log.e(TAG, "starting merged schedule generator");
        int daySchedulesSize = daySchedules.size();
        for(int i = 0; i<daySchedulesSize; i++){
            int j = 0;
            checkSelfCrossing();
            while (j < mergedSchedules.size()){
                //ArrayList<Events> inviteeDay = daySchedules.get(i);
                while (daySchedules.get(i).size() > 0){
                    compareAndMerge(mergedSchedules.get(j), daySchedules.get(i).get(0), i);
                }
                Log.e(TAG, "stoped iteration in other days ev");
                j++;
            }
        }
        checkSelfCrossing();
        checkSelfCrossing();
        checkSelfCrossing();
        Log.e(TAG, mergedSchedules.toString());
    }

    private void checkSelfCrossing() {
        int i = 0;
        pivotEvent = mergedSchedules.get(0);
        while (mergedSchedules.size() > i+1){
            ArrayList<Integer> nextEvent = mergedSchedules.get(i+1);
            Integer res = compareAndMergeSelfCheck(pivotEvent, nextEvent);
            if(res == 1){
                pivotEvent = mergedSchedules.get(i+1);
            }
            i++;
        }
    }

    private Integer compareAndMerge(ArrayList<Integer> event1, Events event2, int i) {
        /*
        -1 -> error
        0 -> Above unrelated
        1 -> Below unrelated
        2 -> Above merge
        3 -> Below merge
        4 -> ev2 inside ev1
        5 -> ev1 inside ev2
         */
        A1 = event1.get(0);
        B1 = event1.get(1);
        A2 = event2.getStartInMins();
        B2 = event2.getEndInMins();

        if(A1-B2>0){
            ArrayList<Integer> ev = new ArrayList<>();
            ev.add(A2);
            ev.add(B2);
            mergedSchedules.add(ev);
            daySchedules.get(i).remove(event2);
            return 0;
        }else if(B1-A2<0){
            ArrayList<Integer> ev = new ArrayList<>();
            ev.add(A2);
            ev.add(B2);
            mergedSchedules.add(ev);
            daySchedules.get(i).remove(event2);
            return 1;
        }else{
            if (A1-A2 >= 0 & B1-B2 >=0 & A1-B2<=0){
                event1.set(0, A2);
                daySchedules.get(i).remove(event2);
                return 2;
            }else if (A1-A2 <= 0 & B1-B2 <=0 & B1-A2 >=0){
                event1.set(1, B2);
                daySchedules.get(i).remove(event2);
                return 3;
            }else if (A1-A2 <= 0 & B1-B2 >=0 ){
                daySchedules.get(i).remove(event2);
                return 4;
            }else if (A1-A2 >= 0 & B1-B2 <=0 ){
                event1.set(0, A2);
                event1.set(1, B2);
                daySchedules.get(i).remove(event2);
                return 5;
            }
            return -1;
        }
    }

    private Integer compareAndMergeSelfCheck(ArrayList<Integer> event1, ArrayList<Integer> event2) {
    /*
        -1 -> error
        0 -> Above unrelated
        1 -> Below unrelated
        2 -> Above merge
        3 -> Below merge
        4 -> ev2 inside ev1
        5 -> ev1 inside ev2
         */
        A1 = event1.get(0);
        B1 = event1.get(1);
        A2 = event2.get(0);
        B2 = event2.get(1);

        if(A1-B2>0){
            return 0;
        }else if(B1-A2<0){
            return 1;
        }else{
            if (A1-A2 >= 0 & B1-B2 >=0 & A1-B2<=0){
                event1.set(0, A2);
                mergedSchedules.remove(event2);
                return 2;
            }else if (A1-A2 <= 0 & B1-B2 <=0 & B1-A2 >=0){
                event1.set(1, B2);
                mergedSchedules.remove(event2);
                return 3;
            }else if (A1-A2 <= 0 & B1-B2 >=0 ){
                mergedSchedules.remove(event2);
                return 4;
            }else if (A1-A2 >= 0 & B1-B2 <=0 ){
                event1.set(0, A2);
                event1.set(1, B2);
                mergedSchedules.remove(event2);
                return 5;
            }
            return -1;
        }
    }
}
