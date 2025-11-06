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
public class Enroll {

    int studentID;
    int subjectID;

    DBConnect a = new DBConnect();
    
    public boolean check_enroll(int studentID, int subjectID) {
        int result = -1;
        int conflictSubjectID = -1;
        try {
            String checkEnrollConflictQuery = String.format("CALL `sp_checkEnrollConflict`(%d, %d)", studentID, subjectID);
            System.out.println(checkEnrollConflictQuery);
            a.rs = a.st.executeQuery(checkEnrollConflictQuery);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                result = a.rs.getInt("Result");
                conflictSubjectID = a.rs.getInt("Conflict_Subject_ID");
            }
            System.out.println(String.format("Result: %d, ConflictSubjectID: %d", result, conflictSubjectID));
        } catch (Exception ex) {
            System.out.println("Failed: " + ex);
        }
        return result == 0;
    }

    public void enroll_Subject(int studentID, int subjectID) {
        int nextEnrollID = 0;
        try {
            String nextEnrollIdQuery = "SELECT MAX(enrollID) + 1 AS NextEnrollID FROM enroll;";
            System.out.println(nextEnrollIdQuery);
            a.rs = a.st.executeQuery(nextEnrollIdQuery);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                nextEnrollID = a.rs.getInt("NextEnrollID");
            }
        } catch (Exception ex) {
            System.out.println("Failed to get next enroll ID: " + ex);
        }

        String query = "insert into enroll values(" + nextEnrollID + ", " + studentID + ", " + subjectID + ")";
        try {
            a.st.executeUpdate(query);
        }

        catch (Exception ex) {
            System.out.println("Failed to enroll: " + ex);
        }
    }

    public void drop_Subject(int studentID, int subjectID) {
        String query = "delete from enroll where studentID = " + studentID + " and subjectID = " + subjectID;
        try {
            a.st.executeUpdate(query);
        }

        catch (Exception ex) {
            System.out.println("Failed to drop: " + ex);
        }
    }
}
