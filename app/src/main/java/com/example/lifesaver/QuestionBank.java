package com.example.lifesaver;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private static List<Questionclass> diseasequestions(){
        final List<Questionclass> questionlist = new ArrayList<>();

        final Questionclass question1 = new Questionclass("A brief loss of consciousness and tone brought on by reduced blood supply to the brain.","cold disease", "faint","hyperventilation", "faint");
        final Questionclass q2 = new Questionclass("A lung-damaging respiratory illness brought on by bacteria, viruses, or fungi.","Pneumonia", "Heat Exhaustion","Hyperventilation","Pneumonia");
        final Questionclass q3 = new Questionclass("This can make breathing difficult and trigger coughing, a whistling sound (wheezing) when you breathe out, and shortness of breath.","Chest Pain","Seizures","Asthma","Asthma");
        final Questionclass q4 = new Questionclass("A state where your breathing becomes incredibly rapid.", "Hyperventilation","Seizures","Asthma","Hyperventilation");
        final Questionclass q5 = new Questionclass("A bacterial infection that can damage the kidneys, bladder, ureters, and urethra, as well as other parts of the urinary system.","Pneumonia","Diabetes","UTI","UTI");
        final Questionclass q6 = new Questionclass("An inflammatory skin ailment that causes dry, itchy, and red skin. It is a chronic disorder that can affect people of all ages and that can be controlled but not cured.", "Allergies","Eczema","Acne","Eczema");

        questionlist.add(question1);
        questionlist.add(q2);
        questionlist.add(q3);
        questionlist.add(q4);
        questionlist.add(q5);
        questionlist.add(q6);

        return questionlist;
    }

    private static List<Questionclass> firstaidquestions(){
        final List<Questionclass> questionlist = new ArrayList<>();

        final Questionclass question1 = new Questionclass("When someone is experiencing an asthma attack, what is the most important thing to do to aid them?","Help them use their inhaler", "Cover their body with a towel","Let them drink water", "Help them use their inhaler");
        final Questionclass q2 = new Questionclass("If a person is bleeding a lot, what would you do?","Keep still","Put pressure on it","Wasth it with cold water","Put pressure on it");
        final Questionclass q3 = new Questionclass("How long should we put pressure on a bleed for?","10-15 minutes","Until help arrives","1 hour","Until help arrives");
        final Questionclass q4 = new Questionclass("If using an inhaler doesn’t make an asthma attack get better we may need to..","Help them lie down","Call for emergency medical services","Leave them alone to calm down","Call for emergency medical services");
        final Questionclass q5 = new Questionclass("What happens to the skin of the person when she gets burned from something?","It will turn pale","It will turn red","It will turn black","It will turn red");
        final Questionclass q6 = new Questionclass("What should we do to help someone who has a burn?", "Hold the burn under cold, running water","Leave the burn and wait for it to cool down","Give the person something to eat","Hold the burn under cold, running water");
        questionlist.add(question1);
        questionlist.add(q2);
        questionlist.add(q3);
        questionlist.add(q4);
        questionlist.add(q5);
        questionlist.add(q6);
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
