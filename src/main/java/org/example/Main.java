package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String csvFile = "src/assets/coursesEF.csv"; // path to your CSV
        List<Course> courses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(csvFile, StandardCharsets.UTF_8))) {

            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue; // skip first line if it's a header
                }

                // Split by comma (use a proper CSV parser if quotes exist)
                String[] values = line.split(",");

                if (values.length < 8) continue; // skip invalid rows

                String code = values[0] + " " + values[1];
                String section = values[2];
                String title = values[3];
                String faculty = values[4];

                int credits = 0;
                try {
                    credits = Integer.parseInt(values[5].trim());
                } catch (NumberFormatException ignored) {}

                String instructor = values[6];
                String level = values[7];

                List<String> schedule = new ArrayList<>();
                for (int i = 8; i < values.length; i++) {
                    if (!values[i].isBlank()) {
                        schedule.add(values[i]);
                    }
                }

                courses.add(new Course(code, section, title, faculty, credits, instructor, level, schedule));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print all courses
        for (Course c : courses) {
            System.out.println(c.getSchedule());
        }
    }
}
