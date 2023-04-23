package com.example.lifesaver;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private static List<Questionclass> diseasequestions(){
        final List<Questionclass> questionlist = new ArrayList<>();

        final Questionclass question1 = new Questionclass("A brief loss of consciousness and tone brought on by reduced blood supply to the brain.","cold disease", "faint","hyperventilation", "faint");
        questionlist.add(question1);

        return questionlist;
    }

    private static List<Questionclass> firstaidquestions(){
        final List<Questionclass> questionlist = new ArrayList<>();

        final Questionclass question1 = new Questionclass("A brief loss of consciousness and tone brought on by reduced blood supply to the brain.","cold disease", "faint","hyperventilation", "faint");
        questionlist.add(question1);

        return questionlist;
    }

    public static List<Questionclass> getquestions(String selectedtopic){
        switch (selectedtopic){
            case "Disease":
                return diseasequestions();
            default:
                return firstaidquestions();
        }
    }
}
