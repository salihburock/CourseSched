package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadCourses {
    public static List<Course> getCourses() {
        String csvFile = "src/assets/coursesEF.csv";
        List<Course> courses = readCourses(csvFile);

        for (Course c : courses) {
            System.out.println(Arrays.asList(c.getFormattedSched()));
        }
        return courses;
    }

    public static List<Course> readCourses(String file) {
        List<Course> courses = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String[] row;
            boolean skipHeader = true;

            while ((row = csvReader.readNext()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                if (row.length < 12) {
                    System.err.println("bozuk: " + Arrays.toString(row));
                    continue;
                }

                String subject = row[0];
                String courseNo = row[1];
                String section = row[2];
                String title = row[3];
                String faculty = row[4];

                int credits = 0;
                try {
                    credits = Integer.parseInt(row[5].trim());
                } catch (NumberFormatException ignored) {}

                String instructor = row[6];
                String partOfTerm = row[7];
                String corequisite = row[8];
                String prerequisite = row[9];
                String description = row[10];

                String schedule = row[11];

                courses.add(new Course(subject, courseNo, section, title, faculty,
                        credits, instructor, partOfTerm,
                        corequisite, prerequisite, description,
                        schedule));
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return courses;
    }
}
