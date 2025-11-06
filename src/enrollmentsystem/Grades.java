/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class Grades {
    
    DBConnect dbConnect = new DBConnect();

    /**
     * Update the grade of the student for a subject
     * 
     * @param studentID the ID of the student to associate the grade
     * @param subjectID the ID of the subject to associate the grade
     * @param prelimGrade the grade for the Prelim
     * @param midTermGrade the grade for the Mid-Term
     * @param preFinalGrade the grade for the Pre-Final
     * @param finalGrade  the grade for the Final
     */
    public void update_grades(int studentID, int subjectID, String prelimGrade, String midTermGrade, String preFinalGrade, String finalGrade){
        try {
            
            // Get the EnrollID for the student and subject
            int enrollID = -1;
            String getEnrollIDQuery = String.format("SELECT enrollID FROM enroll WHERE studentID = %d and subjectID = %d",
                    studentID, subjectID);
            System.out.println(getEnrollIDQuery);
            dbConnect.rs = dbConnect.st.executeQuery(getEnrollIDQuery);
            System.out.println("Query Success!");
            while(dbConnect.rs.next()) {
                enrollID = dbConnect.rs.getInt(1);
            }
            
            // If the EnrollID exists, proceed with the update
            if(enrollID >= 0){
                
                // Get the existing grade for the EnrollID
                String getGradeForEnrollIDQuery = String.format("SELECT enrollID FROM grades WHERE enrollID = %d",
                        enrollID);
                System.out.println(getGradeForEnrollIDQuery);
                dbConnect.rs = dbConnect.st.executeQuery(getGradeForEnrollIDQuery);
                System.out.println("Query Success!");
                int count = 0;
                while(dbConnect.rs.next()) {
                    enrollID = dbConnect.rs.getInt(1);
                    count++;
                }
                
                // If the grade exists, update the grade.
                if(count > 0){
                    String updateGradeQuery = String.format("UPDATE grades SET prelim='%s', midterm='%s', prefinal='%s', final='%s' WHERE enrollID = %d",
                            prelimGrade, midTermGrade, preFinalGrade, finalGrade, enrollID);
                    System.out.println(updateGradeQuery);
                    dbConnect.st.execute(updateGradeQuery);
                    System.out.println("Query Success!");
                }
                // Else, we create the grade record.
                else {
                    String insertGradeQuery = String.format("INSERT INTO grades VALUES (%d, '%s', '%s', '%s', '%s')",
                            enrollID, prelimGrade, midTermGrade, preFinalGrade, finalGrade);
                    System.out.println(insertGradeQuery);
                    dbConnect.st.execute(insertGradeQuery);
                    System.out.println("Query Success!");
                }
            }
        } catch (Exception ex){
            System.out.println("Failed: " + ex);
        }
    }
}