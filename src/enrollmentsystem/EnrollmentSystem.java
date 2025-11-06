/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class EnrollmentSystem {
    
    // Shared Data
    
    /**
     * 
     */
    public static int logged_in_teacherID = -1;
    
    /**
     * 
     */
    public static int logged_in_studentID = -1;;
    
    /**
     * The username of the user currently logged into the system
     */
    public static String logged_in_username = "";
    
    /**
     * The password of the user currently logged into the system
     */
    public static String logged_in_password = "";
    
    /**
     * The database / school year that the user has selected
     */
    public static String selected_database = "";
    
    /**
     * The student ID selected in the student list screen
     */
    public static String selected_student_ID = "";
    
    /**
     * The subject ID selected in the subject list screen
     */
    public static String selected_subject_ID = "";

    /**
     * The teacher ID selected in the teacher list screen
     */
    public static String selected_teacher_ID = "";
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // !!! Do not show the other screens except login form !!!
        
        // Display Login Form for all users to log into the system
        LoginForm loginForm = new LoginForm();
        
        // Display it in the middle of the screen
        loginForm.setLocationRelativeTo(null);
        
        // Display the form
        loginForm.setVisible(true);
    }
}