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
public class subjects {

    int subjectID;
    int subjectUNITS;
    String subjectCODE;
    String subjectDESCRIPTION;
    String subjectSCHEDULE;

    DBConnect a = new DBConnect();

    /* add method */

    public void add_subject(int id, String code, String description, int units, String schedule) {

        String query = "insert into subjects values(" + id + ",'" + code + "','" + description + "','" + units + "','"
                + schedule + "')";

        try {
            a.st.executeUpdate(query);
            System.out.println("Insert successful");
        }

        catch (Exception ex) {
            System.out.println("Failed to insert: " + ex);
        }
    }

    /* update method */

    public void edit_subject(int id, String code, String description, int units, String schedule) {

        String query = "update subjects set subjectCODE='" + code + "', subjectDESCRIPTION='" + description
                + "', subjectUNITS='" + units + "', subjectSCHEDULE='" + schedule + "' where subjectID=" + id + ";";

        try {
            a.st.executeUpdate(query);
            System.out.println("Update successful");
        }

        catch (Exception ex) {
            System.out.println("Failed to update: " + ex);
        }

    }

    /* delete method */

    public void delete_subject(String remove) {

        String query = "delete from subjects where subjectID in(select subjectID from(select * from subjects " + remove
                + ") as x)";

        try {
            a.st.executeUpdate(query);
            System.out.println("Delete successful");
        }

        catch (Exception ex) {
            System.out.println("Failed to Delete: " + ex);
        }

    }

}
