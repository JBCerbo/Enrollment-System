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
public class teachers {

    int teacherID;
    String teacherNAME;
    String teacherDEPARTMENT;
    String teacherADDRESS;
    String teacherCONTACT;
    String teacherSTATUS;

    DBConnect a = new DBConnect();
    
    public void getLoggedInTeacherId(){
        DBConnect connect = new DBConnect();
        try {
            String getLoggedInTeacherIDQuery = String.format("SELECT teacherID FROM (SELECT teacherID, CONCAT(teacherID, teacherName) AS username FROM teachers) AS teacher_users WHERE username = '%s'", EnrollmentSystem.logged_in_username);
            System.out.println(getLoggedInTeacherIDQuery);
            connect.rs = connect.st.executeQuery(getLoggedInTeacherIDQuery);
            System.out.println("Query Success!");
            while (connect.rs.next()) {
                EnrollmentSystem.logged_in_teacherID = connect.rs.getInt(1);
            }
        }
        catch(Exception ex){
            System.out.println("Failed: " + ex);
        }
    }

    /* add method */

    public void add_teacher(int id, String name, String department, String address, String contact, String status) {

        String query = "insert into teachers values(" + id + ",'" + name + "','" + department + "','" + address + "','"
                + contact + "','" + status + "')";
        try {
            a.st.executeUpdate(query);
            System.out.println("Insert successful");

            // Only create User and Grant user when the insert teacher succeeds.

            // Create the username. Its the combination of id and name.
            String mySqlUsername = String.format("%d%s", id, name);

            // Create the password. Its the combination of letter 't' for teacher, id and
            // name.
            String mySqlPassword = String.format("t%d%s", id, name);

            // Attempt to create the MySQL User
            try {
                // Create MySQL User
                String createUserQuery = String.format("CREATE USER '%s'@'localhost' IDENTIFIED BY '%s'", mySqlUsername,
                        mySqlPassword);
                System.out.println(createUserQuery);
                a.st.execute(createUserQuery);
                System.out.println("Create user successful");
            } catch (Exception ex) {
                System.out.println("Failed to create user: " + ex);
            }

            // Even if the create MySQL User failed, always attempt to grant access to the
            // teacher for the current database/school year.
            // The reason is because, the same teacher might have been created on a
            // different database/school year, just grant access to that same MySQL User.
            try {
                // Grant access to this database to the MySQL User
                String grantPrivilegeQuery = String.format(
                        "GRANT SELECT, INSERT, UPDATE, DELETE ON %s.* TO '%s'@'localhost';",
                        EnrollmentSystem.selected_database, mySqlUsername);
                System.out.println(grantPrivilegeQuery);
                a.st.execute(grantPrivilegeQuery);
                System.out.println("Grant privileges successful");
            } catch (Exception ex) {
                System.out.println("Failed to Grant privileges: " + ex);
            }
        } catch (Exception ex) {
            System.out.println("Failed to insert: " + ex);
        }
    }

    /* update method */

    public void edit_teacher(int id, String name, String department, String address, String contact, String status) {

        String query = "update teachers set teacherNAME='" + name + "',teacherDEPARTMENT='" + department
                + "',teacherADDRESS='" + address + "',teacherCONTACT='" + contact + "',teacherSTATUS='" + status
                + "' where teacherID=" + id + ";";

        try {
            a.st.executeUpdate(query);
            System.out.println("Update successful");
        }

        catch (Exception ex) {
            System.out.println("Failed to update: " + ex);
        }

    }

    /* delete method */
    public void delete_teacher(String remove) {
        // We cannot determine the exact MySQL User to remove if we delete the teacher
        // first.
        // We will drop the MySQL Users first, then delete the teacher from the teacher
        // table after.
        try {
            // Get all teacher records from the teacher table that satisfies the remove
            // condition
            String getTeachersQuery = "select * from teachers " + remove;
            System.out.println(getTeachersQuery);
            a.rs = a.st.executeQuery(getTeachersQuery);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                // Drop the MySQL User for every teacher record
                DBConnect dropUserConnect = new DBConnect();
                // Create the username of the MySQL User to be dropped. Its the combination of
                // teacherID and teacherNAME.
                String mySqlUsername = String.format("%d%s", a.rs.getInt("teacherID"), a.rs.getString("teacherNAME"));
                // Create the Drop user query.
                String dropUserQuery = String.format("DROP USER '%s'@'localhost'", mySqlUsername);
                System.out.println(dropUserQuery);
                // Execute the query
                dropUserConnect.st.execute(dropUserQuery);
                System.out.println("Drop user successful");
            }
        } catch (Exception ex) {
            System.out.println("Failed to Drop user: " + ex);
        }

        // Delete all teacher records from the teacher table that satisfies the remove
        // condition
        String query = "delete from teachers where teacherID in(select teacherID from(select * from teachers " + remove
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
