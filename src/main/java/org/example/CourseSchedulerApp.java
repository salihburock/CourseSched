package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.border.StrokeBorder;

import org.example.*;

public class CourseSchedulerApp extends Application {

    private GridPane scheduleGrid;
    private final int startHour = 8;   // earliest time
    private final int endHour = 20;    // latest time (8 PM)
    private final int slotMinutes = 30; // resolution of grid (30 min slots)
    ObservableList<Course> SelectedCourses = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        // Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search courses...");


        // Left: list of courses
        ListView<Course> courseListView = new ListView<>();
        courseListView.setPrefWidth(300);
        List<Course> courses = ReadCourses.getCourses();
        
        ListView<Course> selectedCourseListView = new ListView<>();
        selectedCourseListView.setPrefWidth(300);
        selectedCourseListView.setItems(SelectedCourses);


        ObservableList<Course> observableCourses = FXCollections.observableArrayList(courses);
        FilteredList<Course> filteredCourses = new FilteredList<>(observableCourses, p -> true);
        
        courseListView.setItems(filteredCourses);

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            try{
                Pattern pattern = Pattern.compile(newVal, Pattern.CASE_INSENSITIVE);
                filteredCourses.setPredicate(course -> pattern.matcher(course.toString()).find());
            } catch (PatternSyntaxException e) {
                filteredCourses.setPredicate(course -> false);
            }
        });


        
        // Right: weekly schedule grid
        scheduleGrid = new GridPane();
        scheduleGrid.setHgap(5);
        scheduleGrid.setVgap(5);
        scheduleGrid.setPadding(new Insets(10));


        buildScheduleGrid();

        courseListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !SelectedCourses.contains(newVal)) {
                SelectedCourses.add(0, newVal);
                drawSchedule(SelectedCourses);
            }
        });



        BorderPane root = new BorderPane();
        root.setLeft(courseListView);
        root.setLeft(selectedCourseListView);
        root.setCenter(scheduleGrid);

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Course Scheduler");
        stage.setScene(scene);
        stage.show();
    
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            SelectedCourses.clear();
            drawSchedule(SelectedCourses);
        });

        Label creditLabel = new Label();
        creditLabel.setTextFill(Color.BLUE);
        creditLabel.setPadding(new Insets(5));
        creditLabel.setText("Total Credits: 0");
        SelectedCourses.addListener((javafx.collections.ListChangeListener.Change<? extends Course> c) -> {
            int totalCredits = 0;
            for (Course course : SelectedCourses) {
                totalCredits += course.getCredits();
            }
            creditLabel.setText("Total Credits: " + totalCredits);
        });
        root.setBottom(creditLabel);



        VBox leftPane = new VBox(10, searchBar, courseListView, clearButton, selectedCourseListView);
        leftPane.setPadding(new Insets(10));
        root.setLeft(leftPane);

    }

    private void buildScheduleGrid() {
        scheduleGrid.getChildren().clear();

        String[] days = {"Time", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        for (int col = 0; col < days.length; col++) {
            Label lbl = new Label(days[col]);
            lbl.setStyle("-fx-font-weight: bold;");
            scheduleGrid.add(lbl, col, 0);
            GridPane.setHalignment(lbl, HPos.CENTER);
        }

        int totalSlots = ((endHour - startHour) * 60) / slotMinutes;
        for (int i = 0; i <= totalSlots; i++) {
            int minutes = startHour * 60 + i * slotMinutes;
            int hour = minutes / 60;
            int min = minutes % 60;
            String timeLabel = String.format("%02d:%02d", hour, min);

            Label lbl = new Label(timeLabel);
            scheduleGrid.add(lbl, 0, i + 1); // first column is time
        }
    }

    private void drawSchedule(List<Course> courses) {
        buildScheduleGrid();
        for (Course course : courses) {
            
            Dictionary dict = course.getFormattedSched();
            Enumeration keys = dict.keys();
            
            while (keys.hasMoreElements()) {
                String block = (String) keys.nextElement();
                String[] parts = block.split("\\|");
                if (parts.length != 3) continue;
                
                int dayCol = Integer.parseInt(parts[0]); // 1 = Monday, 5 = Friday
                int startMin = Integer.parseInt(parts[1]);
                int endMin = Integer.parseInt(parts[2]);
                
                // Convert times to grid rows
                int startSlot = (startMin - startHour * 60) / slotMinutes + 1; // +1 because row 0 is header
                int endSlot = (endMin - startHour * 60) / slotMinutes + 1;
                
                Label blockLabel = new Label(course.getTitle() + " " +course.getCourseNo() + course.getSection() + "\n" + course.getInstructor());
                Random generator = new Random(course.getCourseNo().hashCode());

                blockLabel.setStyle("-fx-background-color: COLORTOCHANGE; -fx-border-color: black; -fx-padding: 5;".replace("COLORTOCHANGE","#"+String.format("%06d",generator.nextInt(999999))+"40").toString());
                blockLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                blockLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        SelectedCourses.remove(course);
                        drawSchedule(SelectedCourses);
                    }
                });
                // Add to grid with row span
                scheduleGrid.add(blockLabel, dayCol, startSlot, 1, endSlot - startSlot);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
