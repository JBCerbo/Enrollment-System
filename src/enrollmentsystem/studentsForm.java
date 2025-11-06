/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

import java.awt.Component;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class studentsForm extends javax.swing.JFrame {

    subjectsForm subject_window = new subjectsForm();
    teachersForm teacher_window = new teachersForm();

    String filter_string = ";";

    String selected_student_ID = "";
    String selected_subject_ID = "";

    String filter_ID = "";
    String filter_name = "";
    String filter_address = "";
    String filter_course = "";
    String filter_gender = "";
    String filter_yrlvl = "";

    boolean b_id, b_name, b_address, b_course, b_gender, b_yrlvl; // boolean to determine if textfields are empty or not
                                                                  // true - string is empty | false - string has text
                                                                  // inside

    /**
     * Creates new form studentsForm
     */

    // function for selecting a row, use data from row, display it at text fields
    // [exclude filter text fields]
    private void getStudentDetails() {

        DBConnect a = new DBConnect();

        try {
            String query = "select * from (select students.*,coalesce(sum(subjectUNITS),0) as TotalUNITS from students left join enroll on students.studentID = enroll.studentID left join subjects on enroll.subjectID = subjects.subjectID group by students.studentID) as student_table WHERE studentID="
                    + selected_student_ID;
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                studentID.setText(a.rs.getString("studentID"));
                studentNAME.setText(a.rs.getString("studentNAME"));
                studentADDRESS.setText(a.rs.getString("studentADDRESS"));
                studentCOURSE.setText(a.rs.getString("studentCOURSE"));
                studentGENDER.setText(a.rs.getString("studentGENDER"));
                studentYEARLEVEL.setText(a.rs.getString("studentYEARLEVEL"));
            }
        } catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    // deselect row (ctrl + mouse1/left mouse btn) and set all text fields(except
    // filter textfields) to empty strings
    private void clearStudentDetails() {
        studentID.setText("");
        studentNAME.setText("");
        studentADDRESS.setText("");
        studentCOURSE.setText("");
        studentGENDER.setText("");
        studentYEARLEVEL.setText("");
    }

    private void getEnrolledSubjects() {
        DefaultTableModel tblmodel = (DefaultTableModel) Enrolled_subjects_table.getModel();
        tblmodel.setRowCount(0);

        DBConnect a = new DBConnect();

        try {
            // nested query where selecting all data from subjects where the selected
            // subjectID from enroll is equal to studentID from enroll, where it is equal to
            // the selected studentID
            String query = "select * from subjects where subjectID in(select subjectID from enroll where studentID in (select studentID from enroll where studentID = "
                    + selected_student_ID + "))";
            System.out.println(query);
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");

            while (a.rs.next()) {
                String id = a.rs.getString("subjectID");
                String code = a.rs.getString("subjectCODE");
                String description = a.rs.getString("subjectDESCRIPTION");
                String units = a.rs.getString("subjectUNITS");
                String schedule = a.rs.getString("subjectSCHEDULE");

                String[] item = { id, code, description, units, schedule };
                tblmodel.addRow(item);
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    private void clearEnrolledSubjects() {
        DefaultTableModel tblmodel = (DefaultTableModel) Enrolled_subjects_table.getModel();
        tblmodel.setRowCount(0);
    }

    public studentsForm() {
        initComponents();
        student_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row

                int selected_row_index = student_table.getSelectedRow();
                System.out.println(selected_row_index);

                if (selected_row_index != -1) {
                    System.out.println(student_table.getValueAt(selected_row_index, 0).toString());
                    selected_student_ID = student_table.getValueAt(student_table.getSelectedRow(), 0).toString();
                    getStudentDetails();
                    getEnrolledSubjects();
                } else { // deselect row
                    selected_student_ID = null;
                    clearStudentDetails();
                    clearEnrolledSubjects();
                }
            }
        });

        Enrolled_subjects_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row

                int selected_row_index = Enrolled_subjects_table.getSelectedRow();
                System.out.println(selected_row_index);

                if (selected_row_index != -1) {
                    System.out.println(Enrolled_subjects_table.getValueAt(selected_row_index, 0).toString());
                    selected_subject_ID = Enrolled_subjects_table
                            .getValueAt(Enrolled_subjects_table.getSelectedRow(), 0).toString();
                } else { // deselect row
                    selected_subject_ID = null;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // display database in table when function is called
    private void ShowStudentRecord() {
        DefaultTableModel tblmodel = (DefaultTableModel) student_table.getModel();
        tblmodel.setRowCount(0);

        if (ID_filter.getText().equals("")) {
            filter_ID = "";
            b_id = true;
        }

        if (name_filter_start.getText().equals("") && name_filter_end.getText().equals("")) {
            filter_name = "";
            b_name = true;
        }

        if (address_filter_start.getText().equals("") && address_filter_end.getText().equals("")) {
            filter_address = "";
            b_address = true;
        }

        if (course_filter_start.getText().equals("") && course_filter_end.getText().equals("")) {
            filter_course = "";
            b_course = true;
        }

        if (gender_filter_input.getText().equals("")) {
            filter_gender = "";
            b_gender = true;
        }

        if (yearlevel_filter_input.getText().equals("")) {
            filter_yrlvl = "";
            b_yrlvl = true;
        }

        if (!ID_filter.getText().equals("")) {
            filter_ID = "where studentID" + compare_operation.getSelectedItem() + ID_filter.getText();
            b_id = false;
        }

        if (!name_filter_start.getText().equals("") || !name_filter_end.getText().equals("")) {
            filter_name = " " + logic_operation1.getSelectedItem() + " studentNAME like '" + name_filter_start.getText()
                    + "%' and studentNAME like '%" + name_filter_end.getText() + "'";
            b_name = false;
        }

        if (!address_filter_start.getText().equals("") || !address_filter_end.getText().equals("")) {
            filter_address = " " + logic_operation2.getSelectedItem() + " studentADDRESS like '"
                    + address_filter_start.getText() + "%' and studentADDRESS like '%" + address_filter_end.getText()
                    + "'";
            b_address = false;
        }

        if (!course_filter_start.getText().equals("") || !course_filter_end.getText().equals("")) {
            filter_course = " " + logic_operation3.getSelectedItem() + " studentCOURSE like '"
                    + address_filter_start.getText() + "%' and studentCOURSE like '%" + course_filter_end.getText()
                    + "'";
            b_course = false;
        }

        if (!gender_filter_input.getText().equals("")) {
            filter_gender = " " + logic_operation4.getSelectedItem() + " studentGENDER = '"
                    + gender_filter_input.getText() + "'";
            b_gender = false;
        }

        if (!yearlevel_filter_input.getText().equals("")) {
            filter_yrlvl = " " + logic_operation5.getSelectedItem() + " studentYEARLEVEL = '"
                    + yearlevel_filter_input.getText() + "'";
            b_yrlvl = false;
        }

        DBConnect a = new DBConnect();

        try {

            if (b_id == true && b_name == false) {
                filter_string = "where studentNAME like '" + name_filter_start.getText() + "%' and studentNAME like '%"
                        + name_filter_end.getText() + "'" + filter_address + filter_course + filter_gender
                        + filter_yrlvl;
            }

            else if (b_id == true && b_name == true && b_address == false) {
                filter_string = "where studentADDRESS like '" + address_filter_start.getText()
                        + "%' and studentADDRESS like '%" + address_filter_end.getText() + "'" + filter_course
                        + filter_gender + filter_yrlvl;
            }

            else if (b_id == true && b_name == true && b_address == true && b_course == false) {
                filter_string = "where studentCOURSE like '" + course_filter_start.getText()
                        + "%' and studentCOURSE like '%" + course_filter_end.getText() + "'" + filter_gender
                        + filter_yrlvl;
            }

            else if (b_id == true && b_name == true && b_address == true && b_course == true && b_gender == false) {
                filter_string = "where studentGENDER = '" + gender_filter_input.getText() + "'" + filter_yrlvl;
            }

            else if (b_id == true && b_name == true && b_address == true && b_course == true && b_gender == true
                    && b_yrlvl == false) {
                filter_string = "where studentYEARLEVEL = '" + yearlevel_filter_input.getText() + "'";
            }

            // if name filter is the only one empty
            else if (b_id == false && b_name == true && b_address == false && b_course == false && b_gender == false
                    && b_yrlvl == false) {
                filter_string = filter_ID + filter_address + filter_course + filter_gender + filter_yrlvl;
            }

            // if address filter is the only one empty
            else if (b_id == false && b_name == false && b_address == true && b_course == false && b_gender == false
                    && b_yrlvl == false) {
                filter_string = filter_ID + filter_name + filter_course + filter_gender + filter_yrlvl;
            }

            // if course filter is the only one empty
            else if (b_id == false && b_name == false && b_address == false && b_course == true && b_gender == false
                    && b_yrlvl == false) {
                filter_string = filter_ID + filter_name + filter_address + filter_course + filter_gender + filter_yrlvl;
            }

            // if gender filter is the only one empty
            else if (b_id == false && b_name == false && b_address == false && b_course == false && b_gender == true
                    && b_yrlvl == false) {
                filter_string = filter_ID + filter_name + filter_address + filter_course + filter_gender + filter_yrlvl;
            }

            // if yearlevel filter is the only one empty
            else if (b_id == false && b_name == false && b_address == false && b_course == false && b_gender == false
                    && b_yrlvl == true) {
                filter_string = filter_ID + filter_name + filter_address + filter_course + filter_gender + filter_yrlvl;
            }

            // if all filters are used OR id filter is used only
            else {
                filter_string = filter_ID + filter_name + filter_address + filter_course + filter_gender + filter_yrlvl;
            }
            // nested query where it selects all data from student_table(select all student
            // data from students and subjectUNITS from subjects where subjectID and
            // studentID is equal to enroll's variable names[subjectID and studentID]) where
            // the set filters' conditions are met
            String query = "select * from (select students.*,coalesce(sum(subjectUNITS),0) as TotalUNITS from students left join enroll on students.studentID = enroll.studentID left join subjects on enroll.subjectID = subjects.subjectID group by students.studentID) as student_table "
                    + filter_string;
            System.out.println(query);
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");

            while (a.rs.next()) {
                String id = a.rs.getString("studentID");
                String name = a.rs.getString("studentNAME");
                String address = a.rs.getString("studentADDRESS");
                String course = a.rs.getString("studentCOURSE");
                String gender = a.rs.getString("studentGENDER");
                String yrlvl = a.rs.getString("studentYEARLEVEL");
                String Tunits = a.rs.getString("TotalUNITS");

                String[] item = { id, name, address, course, gender, yrlvl, Tunits };
                tblmodel.addRow(item);
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        studentID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        studentNAME = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        studentADDRESS = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        studentCOURSE = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        studentGENDER = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        studentYEARLEVEL = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        compare_operation = new javax.swing.JComboBox();
        ID_filter = new javax.swing.JTextField();
        logic_operation1 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        name_filter_start = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        name_filter_end = new javax.swing.JTextField();
        logic_operation2 = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        address_filter_start = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        address_filter_end = new javax.swing.JTextField();
        logic_operation3 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        course_filter_start = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        course_filter_end = new javax.swing.JTextField();
        logic_operation4 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        logic_operation5 = new javax.swing.JComboBox();
        gender_filter_input = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        yearlevel_filter_input = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        student_table = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Enrolled_subjects_table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        enroll_btn = new javax.swing.JButton();
        drop_btn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        Save_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        subjects_window = new javax.swing.JMenuItem();
        teachers_window = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuNewDatabase1stSemester = new javax.swing.JMenuItem();
        jMenuNewDatabase2ndSemester = new javax.swing.JMenuItem();
        jMenuNewDatabaseSummer = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }

            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Enter Student ID");

        studentID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIDActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter Student Name");
        jLabel2.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel3.setText("Enter Student Address");
        jLabel3.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel4.setText("Enter Student Course");

        jLabel5.setText("Enter Student Gender");

        jLabel6.setText("Enter Student Year Level");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""),
                "Filter"));

        jLabel7.setText("Student ID");

        compare_operation
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", "<", ">=", "<=", "<>" }));
        compare_operation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compare_operationActionPerformed(evt);
            }
        });

        ID_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ID_filterActionPerformed(evt);
            }
        });
        ID_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ID_filterKeyReleased(evt);
            }
        });

        logic_operation1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation1ActionPerformed(evt);
            }
        });
        logic_operation1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                logic_operation1KeyReleased(evt);
            }
        });

        jLabel8.setText("Student Name");

        jLabel9.setText("Starts");

        name_filter_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                name_filter_startActionPerformed(evt);
            }
        });
        name_filter_start.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                name_filter_startKeyReleased(evt);
            }
        });

        jLabel10.setText("Ends");

        name_filter_end.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                name_filter_endKeyReleased(evt);
            }
        });

        logic_operation2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation2ActionPerformed(evt);
            }
        });

        jLabel11.setText("Student Address");

        jLabel12.setText("Starts");

        address_filter_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                address_filter_startActionPerformed(evt);
            }
        });
        address_filter_start.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                address_filter_startKeyReleased(evt);
            }
        });

        jLabel13.setText("Ends");

        address_filter_end.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                address_filter_endKeyReleased(evt);
            }
        });

        logic_operation3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation3ActionPerformed(evt);
            }
        });

        jLabel14.setText("Student Course");

        jLabel15.setText("Starts");

        course_filter_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_filter_startActionPerformed(evt);
            }
        });
        course_filter_start.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                course_filter_startKeyReleased(evt);
            }
        });

        jLabel16.setText("Ends");

        course_filter_end.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                course_filter_endActionPerformed(evt);
            }
        });
        course_filter_end.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                course_filter_endKeyReleased(evt);
            }
        });

        logic_operation4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation4ActionPerformed(evt);
            }
        });

        jLabel17.setText("Gender");

        logic_operation5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation5ActionPerformed(evt);
            }
        });

        gender_filter_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gender_filter_inputActionPerformed(evt);
            }
        });
        gender_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                gender_filter_inputKeyReleased(evt);
            }
        });

        jLabel18.setText("Year Level");

        yearlevel_filter_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearlevel_filter_inputActionPerformed(evt);
            }
        });
        yearlevel_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                yearlevel_filter_inputKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(logic_operation5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(address_filter_start,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel13)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(address_filter_end,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel14)
                                        .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel8)
                                                .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel7)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(compare_operation,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(25, 25, 25)
                                                        .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel9)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(name_filter_start,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jLabel10)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(name_filter_end,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(course_filter_start,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel16)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(course_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel18)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(yearlevel_filter_input))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout
                                                        .createSequentialGroup()
                                                        .addComponent(jLabel17)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(gender_filter_input,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(compare_operation, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(name_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(name_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(address_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(address_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel15)
                                        .addComponent(course_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel16)
                                        .addComponent(course_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel17)
                                        .addComponent(gender_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logic_operation5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel18)
                                        .addComponent(yearlevel_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        student_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null }
                },
                new String[] {
                        "ID", "Name", "Address", "Course", "Gender", "Year Level", "Total Units"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        student_table.getTableHeader().setReorderingAllowed(false);
        student_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                student_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(student_table);
        if (student_table.getColumnModel().getColumnCount() > 0) {
            student_table.getColumnModel().getColumn(0).setResizable(false);
            student_table.getColumnModel().getColumn(0).setPreferredWidth(25);
            student_table.getColumnModel().getColumn(1).setResizable(false);
            student_table.getColumnModel().getColumn(2).setResizable(false);
            student_table.getColumnModel().getColumn(2).setPreferredWidth(50);
            student_table.getColumnModel().getColumn(3).setResizable(false);
            student_table.getColumnModel().getColumn(4).setResizable(false);
            student_table.getColumnModel().getColumn(5).setResizable(false);
            student_table.getColumnModel().getColumn(6).setResizable(false);
            student_table.getColumnModel().getColumn(6).setPreferredWidth(55);
        }

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("ENROLLED SUBJECTS");
        jLabel19.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel19.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel19.setIconTextGap(14);

        Enrolled_subjects_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "Subject ID", "Code", "Description", "Units", "Schedule"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane2.setViewportView(Enrolled_subjects_table);
        if (Enrolled_subjects_table.getColumnModel().getColumnCount() > 0) {
            Enrolled_subjects_table.getColumnModel().getColumn(0).setResizable(false);
            Enrolled_subjects_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            Enrolled_subjects_table.getColumnModel().getColumn(1).setResizable(false);
            Enrolled_subjects_table.getColumnModel().getColumn(2).setResizable(false);
            Enrolled_subjects_table.getColumnModel().getColumn(2).setPreferredWidth(125);
            Enrolled_subjects_table.getColumnModel().getColumn(3).setResizable(false);
            Enrolled_subjects_table.getColumnModel().getColumn(3).setPreferredWidth(25);
            Enrolled_subjects_table.getColumnModel().getColumn(4).setResizable(false);
            Enrolled_subjects_table.getColumnModel().getColumn(4).setPreferredWidth(125);
        }

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Enrolled Subjects"));

        enroll_btn.setText("ENROLL");
        enroll_btn.setAutoscrolls(true);
        enroll_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        enroll_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enroll_btnActionPerformed(evt);
            }
        });

        drop_btn.setText("DROP");
        drop_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drop_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel2Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(enroll_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 75,
                                                Short.MAX_VALUE)
                                        .addComponent(drop_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(13, Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(enroll_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                                .addComponent(drop_btn)
                                .addContainerGap()));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 182, Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 194, Short.MAX_VALUE));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        Save_btn.setText("Save");
        Save_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Save_btnActionPerformed(evt);
            }
        });

        delete_btn.setText("Delete");
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });

        update_btn.setText("Update");
        update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Save_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(delete_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(update_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap()));
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(Save_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(delete_btn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11,
                                        Short.MAX_VALUE)
                                .addComponent(update_btn)));

        jMenu1.setText("Window");

        subjects_window.setText("Subjects");
        subjects_window.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjects_windowActionPerformed(evt);
            }
        });
        jMenu1.add(subjects_window);

        teachers_window.setText("Teachers");
        teachers_window.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teachers_windowActionPerformed(evt);
            }
        });
        jMenu1.add(teachers_window);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("New");

        jMenu3.setText("Database");

        jMenuNewDatabase1stSemester.setText("1st Semester");
        jMenuNewDatabase1stSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewDatabase1stSemesterActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuNewDatabase1stSemester);

        jMenuNewDatabase2ndSemester.setText("2nd Semester");
        jMenuNewDatabase2ndSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewDatabase2ndSemesterActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuNewDatabase2ndSemester);

        jMenuNewDatabaseSummer.setText("Summer");
        jMenuNewDatabaseSummer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewDatabaseSummerActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuNewDatabaseSummer);

        jMenu2.add(jMenu3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(jLabel1,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(studentNAME,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(studentID,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(studentADDRESS)
                                                        .addComponent(studentCOURSE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 150,
                                                                Short.MAX_VALUE)
                                                        .addComponent(studentGENDER)
                                                        .addComponent(studentYEARLEVEL)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(studentID,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(studentNAME,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(21, 21, 21)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(studentADDRESS,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(studentCOURSE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(studentGENDER,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                                Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(studentYEARLEVEL,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel6,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(7, 7, 7)
                                                                                .addComponent(jPanel4,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(jPanel2,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jPanel3,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addContainerGap())))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel19,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 54,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jScrollPane2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                                        Short.MAX_VALUE)))))));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void studentIDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_studentIDActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_studentIDActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_update_btnActionPerformed
        students stud = new students();
        stud.edit_student(Integer.parseInt(studentID.getText()), studentNAME.getText(), studentADDRESS.getText(),
                studentCOURSE.getText(), studentGENDER.getText(), studentYEARLEVEL.getText());
        ShowStudentRecord();
    }// GEN-LAST:event_update_btnActionPerformed

    private void Save_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_Save_btnActionPerformed
        students stud = new students();
        stud.add_student(Integer.parseInt(studentID.getText()), studentNAME.getText(), studentADDRESS.getText(),
                studentCOURSE.getText(), studentGENDER.getText(), studentYEARLEVEL.getText());
        ShowStudentRecord();
    }// GEN-LAST:event_Save_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_delete_btnActionPerformed
        students stud = new students();
        stud.delete_student(filter_string);
        ShowStudentRecord();
    }// GEN-LAST:event_delete_btnActionPerformed

    private void subjects_windowActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_subjects_windowActionPerformed
        subject_window.setVisible(true);
    }// GEN-LAST:event_subjects_windowActionPerformed

    private void teachers_windowActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_teachers_windowActionPerformed
        teacher_window.setVisible(true);
    }// GEN-LAST:event_teachers_windowActionPerformed

    private void name_filter_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_name_filter_startActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_name_filter_startActionPerformed

    private void compare_operationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compare_operationActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_compare_operationActionPerformed

    private void course_filter_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_course_filter_startActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_course_filter_startActionPerformed

    private void course_filter_endActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_course_filter_endActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_course_filter_endActionPerformed

    private void logic_operation3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation3ActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_logic_operation3ActionPerformed

    private void logic_operation5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation5ActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_logic_operation5ActionPerformed

    private void yearlevel_filter_inputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_yearlevel_filter_inputActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_yearlevel_filter_inputActionPerformed

    private void gender_filter_inputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_gender_filter_inputActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_gender_filter_inputActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        ShowStudentRecord();
    }// GEN-LAST:event_formWindowOpened

    private void logic_operation4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation4ActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_logic_operation4ActionPerformed

    private void logic_operation1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation1ActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_logic_operation1ActionPerformed

    private void logic_operation2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation2ActionPerformed
        ShowStudentRecord();
    }// GEN-LAST:event_logic_operation2ActionPerformed

    private void student_tableMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_student_tableMouseClicked
        // TODO add your handling code here:
    }// GEN-LAST:event_student_tableMouseClicked

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowGainedFocus
        ShowStudentRecord();
    }// GEN-LAST:event_formWindowGainedFocus

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowDeactivated
        // TODO add your handling code here:
    }// GEN-LAST:event_formWindowDeactivated

    private void logic_operation1KeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_logic_operation1KeyReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_logic_operation1KeyReleased

    private void ID_filterKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_ID_filterKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_ID_filterKeyReleased

    private void name_filter_startKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_name_filter_startKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_name_filter_startKeyReleased

    private void ID_filterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ID_filterActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_ID_filterActionPerformed

    private void name_filter_endKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_name_filter_endKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_name_filter_endKeyReleased

    private void address_filter_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_address_filter_startActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_address_filter_startActionPerformed

    private void address_filter_startKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_address_filter_startKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_address_filter_startKeyReleased

    private void address_filter_endKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_address_filter_endKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_address_filter_endKeyReleased

    private void course_filter_startKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_course_filter_startKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_course_filter_startKeyReleased

    private void course_filter_endKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_course_filter_endKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_course_filter_endKeyReleased

    private void gender_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_gender_filter_inputKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_gender_filter_inputKeyReleased

    private void yearlevel_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_yearlevel_filter_inputKeyReleased
        ShowStudentRecord();
    }// GEN-LAST:event_yearlevel_filter_inputKeyReleased

    private void enroll_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_enroll_btnActionPerformed
        String selected_subject_ID = EnrollmentSystem.selected_subject_ID;

        if (selected_subject_ID == null) {// prevents user to send null data
            return;
        }

        try {

            int i = okcancel(
                    "Enroll Subject No. " + selected_subject_ID + " to Student ID: " + selected_student_ID + " ?");

            if (i == 0) {
                Enroll enroll = new Enroll();
                boolean canEnroll = enroll.check_enroll(Integer.parseInt(selected_student_ID), Integer.parseInt(selected_subject_ID));
                if(canEnroll){
                    enroll.enroll_Subject(Integer.parseInt(selected_student_ID), Integer.parseInt(selected_subject_ID));
                    getStudentDetails();
                    getEnrolledSubjects();
                    ShowStudentRecord();
                    JOptionPane.showMessageDialog(null, "Enrolled!");
                } else {
                    JOptionPane.showMessageDialog(null, "Conflict!");
                }
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }// GEN-LAST:event_enroll_btnActionPerformed

    private void drop_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_drop_btnActionPerformed
        if (selected_subject_ID == null) {
            return;
        }

        try {
            int i = okcancel(
                    "Drop Subject No. " + selected_subject_ID + " for Student ID: " + selected_student_ID + " ?");

            if (i == 0) {
                Enroll enroll = new Enroll();

                enroll.drop_Subject(Integer.parseInt(selected_student_ID), Integer.parseInt(selected_subject_ID));
                getStudentDetails();
                getEnrolledSubjects();
                ShowStudentRecord();

                JOptionPane.showMessageDialog(null, "Dropped!");
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }// GEN-LAST:event_drop_btnActionPerformed

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowLostFocus

    }// GEN-LAST:event_formWindowLostFocus

    private void jMenuNewDatabase1stSemesterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuNewDatabase1stSemesterActionPerformed
        createDatabase("1st");
    }// GEN-LAST:event_jMenuNewDatabase1stSemesterActionPerformed

    private void jMenuNewDatabase2ndSemesterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuNewDatabase2ndSemesterActionPerformed
        createDatabase("2nd");
    }// GEN-LAST:event_jMenuNewDatabase2ndSemesterActionPerformed

    private void jMenuNewDatabaseSummerActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuNewDatabaseSummerActionPerformed
        createDatabase("summer");
    }// GEN-LAST:event_jMenuNewDatabaseSummerActionPerformed

    /**
     * Creates the database and all the necessary tables inside the newly created
     * database
     * 
     * @param semesterName The name of the semester for the name of the database
     */
    private void createDatabase(String semesterName) {
        // Get the current year
        int currentYear = Calendar.getInstance().getTime().getYear() + 1900;

        // Create database name with format <semestername>_<sy><Current Year>_<Current
        // Year + 1>
        String databaseName = String.format("%s_sy%s_%s", semesterName, currentYear, currentYear + 1);

        // Print for tracing purposes
        System.out.println("Create database: " + databaseName);

        // Connect to your default DB
        DBConnect a = new DBConnect();

        try {
            // Create the query to create the database
            String query = "CREATE DATABASE `" + databaseName + "`";
            System.out.println(query);

            // Execute Query
            a.st.execute(query);
            System.out.println("Query Success!");

            // Connect to the newly created database
            DBConnect newDbConnect = new DBConnect(databaseName);

            // Populate tables

            // Creates the Students Table
            String createTableStudentsQuery = "CREATE TABLE `Students` (`studentID` int NOT NULL,`studentName` TEXT NOT NULL,`studentAddress` TEXT NOT NULL,`studentCourse` TEXT NOT NULL,`studentGender` TEXT NOT NULL,`studentYearLevel` TEXT NOT NULL,PRIMARY KEY (`studentID`));";
            System.out.println(createTableStudentsQuery);
            newDbConnect.st.execute(createTableStudentsQuery);
            System.out.println("Query Success!");

            // Creates the Subjects Table
            String createTableSubjectsQuery = "CREATE TABLE `Subjects` (`subjectID` int NOT NULL,`subjectCode` TEXT NOT NULL,`subjectDescription` TEXT NOT NULL,`subjectUnits` INT NOT NULL,`subjectSchedule` TEXT NOT NULL,PRIMARY KEY (`subjectID`));";
            System.out.println(createTableSubjectsQuery);
            newDbConnect.st.execute(createTableSubjectsQuery);
            System.out.println("Query Success!");

            // Creates the Enroll Table
            String createTableEnrollQuery = "CREATE TABLE `Enroll` (`enrollID` int NOT NULL,`studentID` int NOT NULL,`subjectID` int NOT NULL,PRIMARY KEY (`enrollID`),FOREIGN KEY (`studentID`) REFERENCES `Students` (`studentID`),FOREIGN KEY (`subjectID`) REFERENCES `Subjects` (`subjectID`));";
            System.out.println(createTableEnrollQuery);
            newDbConnect.st.execute(createTableEnrollQuery);
            System.out.println("Query Success!");

            // Creates the Grades Table
            String createTableGradesQuery = "CREATE TABLE `Grades` (`enrollID` int NOT NULL,`Prelim` TEXT NOT NULL,`Midterm` TEXT NOT NULL,`Prefinal` TEXT NOT NULL,`Final` TEXT NOT NULL,PRIMARY KEY (`enrollID`),FOREIGN KEY (`enrollID`) REFERENCES `Enroll` (`enrollID`));";
            System.out.println(createTableGradesQuery);
            newDbConnect.st.execute(createTableGradesQuery);
            System.out.println("Query Success!");

            // Creates the Teachers Table
            String createTableTeachersQuery = "CREATE TABLE `Teachers` (`teacherID` int NOT NULL,`teacherName` TEXT NOT NULL,`teacherDepartment` TEXT NOT NULL,`teacherAddress` TEXT NOT NULL,`teacherContact` TEXT NOT NULL,`teacherStatus` TEXT NOT NULL,PRIMARY KEY (`teacherID`));";
            System.out.println(createTableTeachersQuery);
            newDbConnect.st.execute(createTableTeachersQuery);
            System.out.println("Query Success!");

            // Creates the Assign Table
            String createTableAssignQuery = "CREATE TABLE `Assign` (`subjectID` int NOT NULL,`teacherID` int NOT NULL,`dateAssigned` DATE NOT NULL,PRIMARY KEY (`subjectID`),FOREIGN KEY (`teacherID`) REFERENCES `Teachers` (`teacherID`),FOREIGN KEY (`subjectID`) REFERENCES `Subjects` (`subjectID`));";
            System.out.println(createTableAssignQuery);
            newDbConnect.st.execute(createTableAssignQuery);
            System.out.println("Query Success!");

            // Creates the Transaction Charges Table
            String createTableTransactionChargesQuery = "CREATE TABLE `TransactionCharges` (`transactionID` int NOT NULL,`departmentFee` DECIMAL(15, 2) NOT NULL,`subjectUnitPrice` DECIMAL(15, 2) NOT NULL,`insuranceFee` DECIMAL(15, 2) NOT NULL,`computerFee` DECIMAL(15, 2) NOT NULL,`laboratoryFee` DECIMAL(15, 2) NOT NULL,`culturalFee` DECIMAL(15, 2) NOT NULL,`libraryFee` DECIMAL(15, 2) NOT NULL,PRIMARY KEY (`transactionID`));";
            System.out.println(createTableTransactionChargesQuery);
            newDbConnect.st.execute(createTableTransactionChargesQuery);
            System.out.println("Query Success!");

            // Creates the Invoice Table
            String createTableInvoiceQuery = "CREATE TABLE `Invoice` (`studentID` int NOT NULL,`transactionID` int NOT NULL,`invoiceDueDate` DATE NOT NULL,PRIMARY KEY (`studentID`),FOREIGN KEY (`studentID`) REFERENCES `Students` (`studentID`),FOREIGN KEY (`transactionID`) REFERENCES `TransactionCharges` (`transactionID`));";
            System.out.println(createTableInvoiceQuery);
            newDbConnect.st.execute(createTableInvoiceQuery);
            System.out.println("Query Success!");

            // Show Completion Prompt
            JOptionPane.showMessageDialog(null, String.format("Created database %s. Yey!", databaseName));
        } catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    public static int okcancel(String theMessage) {
        int result = JOptionPane.showConfirmDialog((Component) null, theMessage, "Alert", JOptionPane.OK_CANCEL_OPTION);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(studentsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(studentsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(studentsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(studentsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new studentsForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Enrolled_subjects_table;
    private javax.swing.JTextField ID_filter;
    private javax.swing.JButton Save_btn;
    private javax.swing.JTextField address_filter_end;
    private javax.swing.JTextField address_filter_start;
    private javax.swing.JComboBox compare_operation;
    private javax.swing.JTextField course_filter_end;
    private javax.swing.JTextField course_filter_start;
    private javax.swing.JButton delete_btn;
    private javax.swing.JButton drop_btn;
    private javax.swing.JButton enroll_btn;
    private javax.swing.JTextField gender_filter_input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuNewDatabase1stSemester;
    private javax.swing.JMenuItem jMenuNewDatabase2ndSemester;
    private javax.swing.JMenuItem jMenuNewDatabaseSummer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox logic_operation1;
    private javax.swing.JComboBox logic_operation2;
    private javax.swing.JComboBox logic_operation3;
    private javax.swing.JComboBox logic_operation4;
    private javax.swing.JComboBox logic_operation5;
    private javax.swing.JTextField name_filter_end;
    private javax.swing.JTextField name_filter_start;
    private javax.swing.JTextField studentADDRESS;
    private javax.swing.JTextField studentCOURSE;
    private javax.swing.JTextField studentGENDER;
    private javax.swing.JTextField studentID;
    private javax.swing.JTextField studentNAME;
    private javax.swing.JTextField studentYEARLEVEL;
    private javax.swing.JTable student_table;
    private javax.swing.JMenuItem subjects_window;
    private javax.swing.JMenuItem teachers_window;
    private javax.swing.JButton update_btn;
    private javax.swing.JTextField yearlevel_filter_input;
    // End of variables declaration//GEN-END:variables
}
