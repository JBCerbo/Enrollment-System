/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Jesse Benjamin Cerbo
 */

public class Assign {

    int teacherID;
    int subjectID;

    DBConnect a = new DBConnect();

    public void assign_Subject(int teacherID, int subjectID) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String query = "insert into assign values (" + subjectID + ", " + teacherID + ", '"
                + format1.format(cal.getTime()) + "')";
        try {
            a.st.executeUpdate(query);
        }

        catch (Exception ex) {
            System.out.println("Failed to assign: " + ex);
        }
    }

    public void retract_Subject(int teacherID, int subjectID) {
        String query = "delete from assign where teacherID = " + teacherID + " and subjectID = " + subjectID;
        try {
            a.st.executeUpdate(query);
        }

        catch (Exception ex) {
            System.out.println("Failed to retract: " + ex);
        }
    }
}
