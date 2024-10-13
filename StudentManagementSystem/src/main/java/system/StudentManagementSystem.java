package system;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StudentManagementSystem extends Application {
    private BarChart<String, Number> barChart;
    private ObservableList<Student> studentsData = FXCollections.observableArrayList();
    private TableView<Student> table = new TableView<>();
    private ScatterChart<Number, Number> scatterChart;
    private ComboBox<String> studentComboBox;
    private static final Logger logger = Logger.getLogger(StudentManagementSystem.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FileHandler fileHandler = new FileHandler("student_management.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 800, 600);

            scatterChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
            scatterChart.setTitle("Semester Marks");
            scatterChart.getXAxis().setLabel("Semester");
            scatterChart.getYAxis().setLabel("Marks");

            MenuBar menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");
            MenuItem newRecordMenuItem = new MenuItem("Add new record");
            MenuItem updateChartMenuItem = new MenuItem("Update Bar Graph");
            fileMenu.getItems().addAll(newRecordMenuItem, updateChartMenuItem);
            menuBar.getMenus().add(fileMenu);

            Menu viewMenu = new Menu("View");
            MenuItem viewGradingMenuItem = new MenuItem("View Student Grading");
            viewMenu.getItems().addAll(viewGradingMenuItem);
            menuBar.getMenus().add(viewMenu);
            viewGradingMenuItem.setOnAction(event -> {
                showStudentGrading();
            });

            root.setTop(menuBar);

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.getChildren().add(table);

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Marks of Students");
            xAxis.setLabel("Student");
            yAxis.setLabel("Total Marks");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Marks");
            for (Student student : studentsData) {
                series.getData().add(new XYChart.Data<>(student.getName(), student.getTotalMarks()));
            }
            barChart.getData().add(series);

            vbox.getChildren().add(barChart);

            root.setCenter(vbox);

            newRecordMenuItem.setOnAction(event -> {
                showAddRecordDialog();
            });

            updateChartMenuItem.setOnAction(event -> {
                updateBarGraph();
            });

            studentComboBox = new ComboBox<>();
            updateStudentComboBox();

            table.getItems().clear();
            table.getItems().addAll(studentsData);

            TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

            TableColumn<Student, String> genderColumn = new TableColumn<>("Gender");
            genderColumn.setCellValueFactory(cellData -> cellData.getValue().genderProperty());

            TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
            emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

            TableColumn<Student, String> contactNumberColumn = new TableColumn<>("Contact Number");
            contactNumberColumn.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());

            TableColumn<Student, String> addressColumn = new TableColumn<>("Address");
            addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

            TableColumn<Student, Integer> mathMarksColumn = new TableColumn<>("Mathematics Marks");
            mathMarksColumn.setCellValueFactory(cellData -> cellData.getValue().mathematicsMarksProperty().asObject());

            TableColumn<Student, Integer> physicsMarksColumn = new TableColumn<>("Physics Marks");
            physicsMarksColumn.setCellValueFactory(cellData -> cellData.getValue().physicsMarksProperty().asObject());

            TableColumn<Student, Integer> chemistryMarksColumn = new TableColumn<>("Chemistry Marks");
            chemistryMarksColumn.setCellValueFactory(cellData -> cellData.getValue().chemistryMarksProperty().asObject());

            TableColumn<Student, Integer> totalMarksColumn = new TableColumn<>("Total Marks");
            totalMarksColumn.setCellValueFactory(cellData -> cellData.getValue().totalMarksProperty().asObject());

            TableColumn<Student, String> admissionNumberColumn = new TableColumn<>("Admission Number");
            admissionNumberColumn.setCellValueFactory(cellData -> cellData.getValue().admissionNumberProperty());

            TableColumn<Student, String> semesterColumn = new TableColumn<>("Semester");
            semesterColumn.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());

            TableColumn<Student, Void> deleteColumn = new TableColumn<>("Delete");
            deleteColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        deleteButton.setOnAction(event -> {
                            Student student = getTableView().getItems().get(getIndex());
                            deleteRecord(student);
                            studentsData.remove(student);
                            updateStudentComboBox();
                        });
                        setGraphic(deleteButton);
                    }
                }
            });

            table.getColumns().addAll(admissionNumberColumn, nameColumn, genderColumn, addressColumn, contactNumberColumn, emailColumn, semesterColumn, mathMarksColumn, physicsMarksColumn, chemistryMarksColumn, totalMarksColumn, deleteColumn);
            table.setItems(studentsData);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            fetchDataFromDatabase();

            table.setItems(studentsData);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Student Management System");
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while initializing the application", e);
        }
    }

    private void fetchDataFromDatabase() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_database", "root", "root");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next()) {
                studentsData.add(new Student(
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        rs.getInt("mathematics_marks"),
                        rs.getInt("physics_marks"),
                        rs.getInt("chemistry_marks"),
                        rs.getString("admission_number"),
                        rs.getString("semester")));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error fetching data from the database", e);
        }
    }

    private void updateStudentComboBox() {
        try {
            studentComboBox.getItems().clear();
            studentComboBox.getItems().addAll(getUniqueNames());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while updating student ComboBox", e);
        }
    }

    private Set<String> getUniqueNames() {
        Set<String> uniqueNames = new HashSet<>();
        for (Student student : studentsData) {
            uniqueNames.add(student.getName());
        }
        return uniqueNames;
    }

    private void showAddRecordDialog() {
        try {
            Dialog<Student> dialog = new Dialog<>();
            dialog.setTitle("Add New Record");
            dialog.setHeaderText(null);

            TextField nameField = new TextField();
            ComboBox<String> genderComboBox = new ComboBox<>();
            genderComboBox.getItems().addAll("Male", "Female", "Rather not say");
            TextField emailField = new TextField();
            TextField contactNumberField = new TextField();
            TextField addressField = new TextField();
            TextField mathMarksField = new TextField();
            TextField physicsMarksField = new TextField();
            TextField chemistryMarksField = new TextField();
            TextField admissionNumberField = new TextField();
            TextField semesterField = new TextField();

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            gridPane.addRow(0, new Label("Admission Number:"), admissionNumberField);
            gridPane.addRow(1, new Label("Name:"), nameField);
            gridPane.addRow(2, new Label("Gender:"), genderComboBox);
            gridPane.addRow(3, new Label("Address:"), addressField);
            gridPane.addRow(4, new Label("Contact Number:"), contactNumberField);
            gridPane.addRow(5, new Label("Email:"), emailField);
            gridPane.addRow(6, new Label("Semester:"), semesterField);
            gridPane.addRow(7, new Label("Mathematics Marks:"), mathMarksField);
            gridPane.addRow(8, new Label("Physics Marks:"), physicsMarksField);
            gridPane.addRow(9, new Label("Chemistry Marks:"), chemistryMarksField);

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(gridPane);

            dialog.getDialogPane().setContent(borderPane);

            ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButton) {
                    Student student = new Student(
                            nameField.getText(),
                            genderComboBox.getValue(),
                            emailField.getText(),
                            contactNumberField.getText(),
                            addressField.getText(),
                            Integer.parseInt(mathMarksField.getText()),
                            Integer.parseInt(physicsMarksField.getText()),
                            Integer.parseInt(chemistryMarksField.getText()),
                            admissionNumberField.getText(),
                            semesterField.getText()
                    );

                    studentsData.add(student);

                    saveRecord(student);

                    updateStudentComboBox();

                    return student;
                }
                return null;
            });

            dialog.showAndWait();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while showing add record dialog", e);
        }
    }

    private void deleteRecord(Student student) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_database", "root", "root");
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE name=? AND email=?");
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while deleting record", e);
        }
    }

    private void updateBarGraph() {
        try {
            Map<String, Integer> totalMarksMap = new HashMap<>();

            for (Student student : studentsData) {
                String name = student.getName();
                int totalMarks = student.getTotalMarks();
                totalMarksMap.put(name, totalMarksMap.getOrDefault(name, 0) + totalMarks);
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Marks");

            for (Map.Entry<String, Integer> entry : totalMarksMap.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChart.getData().setAll(series);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while updating bar graph", e);
        }
    }

    private void saveRecord(Student student) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_database", "root", "root");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO students (name, gender, email, contact_number, address, mathematics_marks, physics_marks, chemistry_marks, admission_number, semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getGender());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getContactNumber());
            stmt.setString(5, student.getAddress());
            stmt.setInt(6, student.getMathematicsMarks());
            stmt.setInt(7, student.getPhysicsMarks());
            stmt.setInt(8, student.getChemistryMarks());
            stmt.setString(9, student.getAdmissionNumber());
            stmt.setString(10, student.getSemester());
            stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while saving record", e);
        }
    }

    private void showStudentGrading() {
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Student Grading");
            dialog.setHeaderText(null);

            ComboBox<String> subjectComboBox = new ComboBox<>();
            subjectComboBox.getItems().addAll("Mathematics", "Physics", "Chemistry");
            subjectComboBox.setValue("Mathematics");

            studentComboBox.getItems().addAll(getUniqueNames());

            Button selectStudentButton = new Button("Select Student");
            selectStudentButton.setOnAction(event -> {
                String selectedSubject = subjectComboBox.getValue();
                String selectedStudentName = studentComboBox.getValue();
                populateScatterChart(selectedStudentName, selectedSubject);
            });

            scatterChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
            scatterChart.setTitle("Semester Marks");
            scatterChart.getXAxis().setLabel("Semester");
            scatterChart.getYAxis().setLabel("Marks");

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.getChildren().addAll(subjectComboBox, studentComboBox, selectStudentButton, scatterChart);

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(vbox);

            dialog.getDialogPane().setContent(borderPane);

            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            dialog.showAndWait();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while showing student grading dialog", e);
        }
    }

    private void populateScatterChart(String studentName, String subject) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_database", "root", "root");
            PreparedStatement stmt = conn.prepareStatement("SELECT semester, " + subject.toLowerCase() + "_marks FROM students WHERE name = ?");
            stmt.setString(1, studentName);
            ResultSet rs = stmt.executeQuery();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(subject);

            VBox gradingBox = new VBox();
            gradingBox.getChildren().clear();

            while (rs.next()) {
                int semester = rs.getInt("semester");
                int marks = rs.getInt(subject.toLowerCase() + "_marks");

                String grade = calculateGrade(marks);

                series.getData().add(new XYChart.Data<>(semester, marks));
                gradingBox.getChildren().add(new Label("Semester " + semester + " Grade: " + grade));
            }

            scatterChart.getData().clear();
            scatterChart.getData().add(series);

            VBox root = (VBox) scatterChart.getParent();
            root.getChildren().removeIf(node -> node instanceof VBox);
            root.getChildren().add(gradingBox);

            conn.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occurred while populating scatter chart", e);
        }
    }

    private String calculateGrade(int marks) {
        if (marks >= 75 && marks <= 100) {
            return "A";
        } else if (marks >= 65 && marks < 75) {
            return "B";
        } else if (marks >= 55 && marks < 65) {
            return "C";
        } else if (marks >= 35 && marks < 55) {
            return "S";
        } else {
            return "F";
        }
    }
}