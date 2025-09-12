package org.example;


public class Main {
    public static void main(String[] args) {
        //CourseSchedulerApp.launch(CourseSchedulerApp.class, args);
        CourseDatabaseHelper helper = new CourseDatabaseHelper();
        try {
            helper.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
}
