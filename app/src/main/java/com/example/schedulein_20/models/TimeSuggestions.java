package com.example.schedulein_20.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TimeSuggestions {
    private final String TAG = "timeSuggestions";
    private ArrayList<ArrayList<Events>> inviteesSchedules;
    private ArrayList<Events> pivotDay;
    private ArrayList<ArrayList<Integer>> mergedSchedule;
    private ArrayList<ArrayList<Integer>> freeSlots;
    private ArrayList<Integer> pivotEvent;
    private ArrayList<Integer> finalSuggestion;
    private Integer evDuration;
    private Integer A1;
    private Integer B1;
    private Integer A2;
    private Integer B2;

    public TimeSuggestions(ArrayList<ArrayList<Events>> inviteesSchedules, int evDuration){
        this.evDuration = evDuration;
        this.inviteesSchedules = inviteesSchedules;
        mergedSchedule = new ArrayList<>();
        freeSlots = new ArrayList<>();
        finalSuggestion = new ArrayList<>();
    }

    public ArrayList<Integer> generateSuggestions(){

        // ------------- WE FIND THE FIRST BUSY DAY AND ESTABLISH IT AS OUR PIVOT DAY -----------
        for (int i=0; i<inviteesSchedules.size() ; i++) {
            if (inviteesSchedules.get(i).size() != 0){
                pivotDay = (ArrayList<Events>) inviteesSchedules.get(i).clone();
                break;
            }if (i == inviteesSchedules.size()-1){
                /* If invitees schedules are empty an empty list
                is returned, since there are no suggestions */
                ArrayList<Integer> emptyList = new ArrayList<>();
                return emptyList;
            }
        }
        // we turn the pivotDay events into a array of ranges
        // where a range is a list of two integers
        mergedSchedule = generateRangesList(pivotDay);
        // we merge all day schedules into a single day schedule
        // that represents the busy hours of all invitees (AKA merged schedule)
        generateMergedSchedule();
        sortMergedSchedule();
        // based in the busy hours of all our invitees we get the free slots of
        // time they got in common
        generateFreeSlots();
        // we filter this free slots of time to find the best time for our new event
        filterFreeSlots();
        return finalSuggestion;
    }

    private ArrayList<ArrayList<Integer>> generateRangesList(ArrayList<Events> events) {
        ArrayList<ArrayList<Integer>> ranges = new ArrayList<>();
        for(Events event:events){
            ArrayList<Integer> range = new ArrayList<>();
            range.add(event.getStartInMins());
            range.add(event.getEndInMins());
            ranges.add(range);
        }
        //Log.e(TAG, "initial ranges list: " + mergedSchedule.toString());
        return ranges;
    }

    private void generateMergedSchedule() {
        /*
        IN ORDER TO GENERATE A MERGED SCHEDULE WE SORT THE EVENTS IN THE MERGED SCHEDULE,
        WE MERGE THE EVENTS OVERLAPPED IN IT, WE ADD THE EVENTS FROM NEXT DAY AND
        REPEAT THE PROCEDURE UNTIL ALL EVENTS ARE MERGED
         */
        Log.i(TAG, "starting merged schedule generator");
        int inviteesSchedulesSize = inviteesSchedules.size();
        for(int i = 0; i<inviteesSchedulesSize; i++){
            sortMergedSchedule();
            checkSelfCrossing();
            mergedSchedule.addAll(generateRangesList(inviteesSchedules.get(i)));
        }
        sortMergedSchedule();
        checkSelfCrossing();
        Log.i(TAG, "RESULT : " + mergedSchedule.toString());
    }

    private void checkSelfCrossing() {
        boolean clean = false;
        while (!clean) {
            int i = 0;
            pivotEvent = mergedSchedule.get(0);
            clean = true;
            while (mergedSchedule.size() > i + 1) {
                ArrayList<Integer> nextEvent = mergedSchedule.get(i + 1);
                Integer res = compareAndMergeSelfCheck(pivotEvent, nextEvent);
                if (res == 1) {
                    pivotEvent = mergedSchedule.get(i + 1);
                }else {
                    clean = false;
                }
                i++;
            }
        }
    }

    private void sortMergedSchedule() {
        ArrayList<ArrayList<Integer>> newMergedSchedule;
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
        mergedSchedule.sort(comparator);
    }

    private void generateFreeSlots() {
        ArrayList<Integer> slot;
        Integer newRangeStart = 0;
        Integer newRangeEnd;
        for(ArrayList<Integer> range: mergedSchedule){
            slot = new ArrayList<>();
            newRangeEnd = range.get(0);
            slot.add(newRangeStart);
            slot.add(newRangeEnd);
            freeSlots.add(slot);
            newRangeStart = range.get(1);
        }
        slot = new ArrayList<>();
        newRangeEnd = 1439;
        slot.add(newRangeStart);
        slot.add(newRangeEnd);
        freeSlots.add(slot);
        Log.i(TAG, "free slots");
        Log.i(TAG, freeSlots.toString());
    }

    private void filterFreeSlots() {
        // WE FIRST FILTER THE FREE TIME SLOTS LEAVING ONLY THE ONES WHOSE DURATION
        // IS ENOUGH TO CARRY THE EVENT
        ArrayList<ArrayList<Integer>> filteredFreeSlots = new ArrayList<>();
        for(ArrayList<Integer> range:freeSlots){
            Integer duration = range.get(1)-range.get(0);
            if(duration >= evDuration){
                filteredFreeSlots.add(range);
            }
        }

        // IF NONE OF THE FREE TIME SLOTS HAS ENOUGH TIME TO CARRY THE EVENT
        // WE RETURN AND EMPTY LIST [finalSuggestion] REPRESENTING NO SUGGESTIONS
        if(filteredFreeSlots.size() == 0){
            return;
        }

        // ELSE WE EVALUATE WHICH TIME SLOT IS THE BEST TO CARRY OUT THE EVENT
        // BASED ON THE SCORE THAT THE SLOT HAS IN THE optimumTime FUNCTION

        // we give a score to all time slots
        ArrayList<Double> functionScores = new ArrayList<>();
        for (ArrayList<Integer> range: filteredFreeSlots){
            Float midpoint = Float.valueOf(range.get(1)-range.get(0));
            midpoint = midpoint/2;
            midpoint += range.get(0);
            functionScores.add( optimumTime(midpoint) );
        }

        // we return a final suggestion based in the best score in the
        // optimumTimeFunction and the new event duration
        int finalSuggestionIndex = functionScores.indexOf(Collections.max(functionScores));
        ArrayList<Integer> finalSlot = filteredFreeSlots.get(finalSuggestionIndex);
        Float midpoint = Float.valueOf(finalSlot.get(1)-finalSlot.get(0));
        ArrayList<Integer> fs = new ArrayList<>();
        midpoint = midpoint/2;
        midpoint += finalSlot.get(0);
        fs.add((int) (midpoint- (evDuration/2)));
        fs.add((int) (midpoint+ (evDuration/2)));
        finalSuggestion = fs;
    }

    private Double optimumTime(Float midpoint) {
        Float var = midpoint-900;
        Float exp = var*var;
        Double res = 0.005*exp;
        return -res;

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
            mergedSchedule.add(ev);
            inviteesSchedules.get(i).remove(event2);
            return 0;
        }else if(B1-A2<0){
            ArrayList<Integer> ev = new ArrayList<>();
            ev.add(A2);
            ev.add(B2);
            mergedSchedule.add(ev);
            inviteesSchedules.get(i).remove(event2);
            return 1;
        }else{
            if (A1-A2 >= 0 & B1-B2 >=0 & A1-B2<=0){
                event1.set(0, A2);
                inviteesSchedules.get(i).remove(event2);
                return 2;
            }else if (A1-A2 <= 0 & B1-B2 <=0 & B1-A2 >=0){
                event1.set(1, B2);
                inviteesSchedules.get(i).remove(event2);
                return 3;
            }else if (A1-A2 <= 0 & B1-B2 >=0 ){
                inviteesSchedules.get(i).remove(event2);
                return 4;
            }else if (A1-A2 >= 0 & B1-B2 <=0 ){
                event1.set(0, A2);
                event1.set(1, B2);
                inviteesSchedules.get(i).remove(event2);
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
                mergedSchedule.remove(event2);
                return 2;
            }else if (A1-A2 <= 0 & B1-B2 <=0 & B1-A2 >=0){
                event1.set(1, B2);
                mergedSchedule.remove(event2);
                return 3;
            }else if (A1-A2 <= 0 & B1-B2 >=0 ){
                mergedSchedule.remove(event2);
                return 4;
            }else if (A1-A2 >= 0 & B1-B2 <=0 ){
                event1.set(0, A2);
                event1.set(1, B2);
                mergedSchedule.remove(event2);
                return 5;
            }
            return -1;
        }
    }




    /* ------------------------------------------------------------------------------------------------------------------------------------------------------
    ---------------------------------------------------------------------------------------------------------------------------------------------------------
                                                        STATIC METHODS
    ---------------------------------------------------------------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------------------------------------------------------------- */



    public static Integer evaluateCrossings(Events event1, Events event2) {
        /*
        -1 -> error
        0 -> Above unrelated
        1 -> Below unrelated
        2 -> Above cross
        3 -> Below cross
        4 -> ev2 inside ev1
        5 -> ev1 inside ev2
         */
        Integer A1 = event1.getStartInMins();
        Integer B1 = event1.getEndInMins();
        Integer A2 = event2.getStartInMins();
        Integer B2 = event2.getEndInMins();

        if(A1-B2>0){
            return 0;
        }else if(B1-A2<0){
            return 1;
        }else{
            if (A1-A2 >= 0 & B1-B2 >=0 & A1-B2<=0){
                return 2;
            }else if (A1-A2 <= 0 & B1-B2 <=0 & B1-A2 >=0){
                return 3;
            }else if (A1-A2 <= 0 & B1-B2 >=0 ){
                return 4;
            }else if (A1-A2 >= 0 & B1-B2 <=0 ){
                return 5;
            }
            return -1;
        }
    }

    public static boolean compareForCrossings(Events event1, Events event2) {
        /*
        true when event1 overlaps with event2
         */
        if (evaluateCrossings(event1, event2) == 2){
            return true;
        }else if (evaluateCrossings(event1, event2) == 3){
            return true;
        }else if (evaluateCrossings(event1, event2) == 4){
            return true;
        }else if (evaluateCrossings(event1, event2) == 5){
            return true;
        }else {
            return false;
        }
    }
}
