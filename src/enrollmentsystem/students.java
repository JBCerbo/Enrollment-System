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
public class students {
    int studentID;
    String studentNAME;
    String studentADDRESS;
    String studentCOURSE;
    String studentGENDER;
    String studentYEARLEVEL;

    DBConnect a = new DBConnect();
    
    public void getLoggedInStudentId(){
        DBConnect connect = new DBConnect();
        try {
            String getLoggedInStudentIDQuery = String.format("SELECT studentID FROM (SELECT studentID, CONCAT(studentID, studentName) AS username FROM students) AS student_users WHERE username = '%s'", EnrollmentSystem.logged_in_username);
            System.out.println(getLoggedInStudentIDQuery);
            connect.rs = connect.st.executeQuery(getLoggedInStudentIDQuery);
            System.out.println("Query Success!");
            while (connect.rs.next()) {
                EnrollmentSystem.logged_in_studentID = connect.rs.getInt(1);
            }
        }
        catch(Exception ex){
            System.out.println("Failed: " + ex);
        }
    }

    /* add method */

    public void add_student(int id, String name, String address, String course, String gender, String yrlvl) {

        String query = "Insert into students values(" + id + ",'" + name + "','" + address + "','" + course + "','"
                + gender + "','" + yrlvl + "')";

        try {
            a.st.executeUpdate(query);
            System.out.println("Insert successful");

            // Only create User and Grant user when the insert student succeeds.

            // Create the username. Its the combination of id and name.
            String mySqlUsername = String.format("%d%s", id, name);

            // Create the password. Its the combination of letter 's' for student, id and
            // name.
            String mySqlPassword = String.format("s%d%s", id, name);

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
            // student for the current database/school year.
            // The reason is because, the same student might have been created on a
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

    public void edit_student(int id, String name, String address, String course, String gender, String yrlvl) {

        String query = "update students set studentNAME='" + name + "', studentADDRESS='" + address
                + "', studentCOURSE='" + course + "',studentGENDER='" + gender + "', studentYEARLEVEL='" + yrlvl
                + "' where studentID=" + id + ";";

        try {
            a.st.executeUpdate(query);
            System.out.println("Update successful");
        }

        catch (Exception ex) {
            System.out.println("Failed to update: " + ex);
        }

    }

    /* delete method */
    public void delete_student(String remove) {
        // We cannot determine the exact MySQL User to remove if we delete the student
        // first.
        // We will drop the MySQL Users first, then delete the student from the student
        // table after.
        try {
            // Get all student records from the student table that satisfies the remove
            // condition
            String getStudentsQuery = "select * from students " + remove;
            System.out.println(getStudentsQuery);
            a.rs = a.st.executeQuery(getStudentsQuery);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                // Drop the MySQL User for every student record
                DBConnect dropUserConnect = new DBConnect();
                // Create the username of the MySQL User to be dropped. Its the combination of
                // studentID and studentName.
                String mySqlUsername = String.format("%d%s", a.rs.getInt("studentID"), a.rs.getString("studentNAME"));
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

        // Delete all student records from the student table that satisfies the remove
        // condition
        String deleteStudentQuery = "delete from students where studentID in(select studentID from(select * from students "
                + remove + ") as x)";
        try {
            a.st.executeUpdate(deleteStudentQuery);
            System.out.println("Delete successful");
        } catch (Exception ex) {
            System.out.println("Failed to Delete: " + ex);
        }
    }
}