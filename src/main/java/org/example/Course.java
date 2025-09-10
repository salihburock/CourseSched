package org.example;

import java.util.List;

public class Course {
    private String code;
    private String section;
    private String title;
    private String faculty;
    private int credits;
    private String instructor;
    private String level;
    private List<String> schedule;

    public Course(String code, String section, String title, String faculty,
                  int credits, String instructor, String level, List<String> schedule) {
        this.code = code;
        this.section = section;
        this.title = title;
        this.faculty = faculty;
        this.credits = credits;
        this.instructor = instructor;
        this.level = level;
        this.schedule = schedule;
    }

    // Getters and toString() for debugging
    public String getCode() { return code; }
    public String getSection() { return section; }
    public String getTitle() { return title; }
    public String getFaculty() { return faculty; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public String getLevel() { return level; }
    public List<String> getSchedule() { return schedule; }

    @Override
    public String toString() {
        return code + " - " + title + " (" + instructor + ") [" + schedule + "]";
    }
}
