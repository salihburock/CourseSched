package org.example;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Course {
    private String subject;
    private String courseNo;
    private String section;
    private String title;
    private String faculty;
    private int credits;
    private String instructor;
    private String partOfTerm;
    private String corequisite;
    private String prerequisite;
    private String description;
    private String schedule;

    public Course(String subject, String courseNo, String section, String title, String faculty,
                  int credits, String instructor, String partOfTerm,
                  String corequisite, String prerequisite, String description,
                  String schedule) {
        this.subject = subject;
        this.courseNo = courseNo;
        this.section = section;
        this.title = title;
        this.faculty = faculty;
        this.credits = credits;
        this.instructor = instructor;
        this.partOfTerm = partOfTerm;
        this.corequisite = corequisite;
        this.prerequisite = prerequisite;
        this.description = description;
        this.schedule = schedule;
    }

    // Getters
    public String getSubject() { return subject; }
    public String getCourseNo() { return courseNo; }
    public String getSection() { return section; }
    public String getTitle() { return title; }
    public String getFaculty() { return faculty; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public String getPartOfTerm() { return partOfTerm; }
    public String getCorequisite() { return corequisite; }
    public String getPrerequisite() { return prerequisite; }
    public String getDescription() { return description; }
    public String getSchedule() { return schedule; }

    @Override
    public String toString() {
        return String.format("%s%s (%s) - %s | %s | Credits: %d | %s | Sched: %s",
                subject, courseNo, section, title, faculty, credits, instructor, schedule);
    }



    public Dictionary getFormattedSched() {
        String[] blocks = this.schedule.split("\\r?\\n");
        Dictionary<String, Integer> FinalObject = new java.util.Hashtable<>();

        for (String block : blocks) {
            if (block.isBlank()) continue;
            String[] parts = block.split("\\|");
            if (parts.length != 2) continue;

            String day = parts[0].trim();
            String timeRange = parts[1].trim();


            Integer dayNum = dayMap.get(day);

            if (dayNum == null) continue;



            String[] times = timeRange.split("-");


            if (times.length != 2) continue;
            String startTime = times[0].trim();
            startTime = String.valueOf(Integer.parseInt(startTime.split(":")[0])*60 + Integer.parseInt(startTime.split(":")[1]));
            String endTime = times[1].trim();
            endTime = String.valueOf(Integer.parseInt(endTime.split(":")[0])*60 + Integer.parseInt(endTime.split(":")[1]));
            String formattedBlock = String.format("%d|%s|%s", dayNum, startTime, endTime);
            // FinalObject.put("Day", dayNum);
            // FinalObject.put("Start", Integer.parseInt(startTime));
            // FinalObject.put("End", Integer.parseInt(endTime));
            FinalObject.put(formattedBlock, dayNum);

        }
    
        return FinalObject;
    }

    

    Dictionary<String, Integer> dayMap;

    {
        dayMap = new java.util.Hashtable<>();
        dayMap.put("Pazartesi", 1);
        dayMap.put("Monday", 1);
        dayMap.put("Salı", 2);
        dayMap.put("Tuesday", 2);
        dayMap.put("Çarşamba", 3);
        dayMap.put("Wednesday", 3);
        dayMap.put("Perşembe", 4);
        dayMap.put("Thursday", 4);
        dayMap.put("Cuma", 5);
        dayMap.put("Friday", 5);
    }
}
