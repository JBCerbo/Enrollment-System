/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class subjectsForm extends javax.swing.JFrame {

    String filter_string = ";";

    String selected_subject_ID = "";

    String filter_ID = "";
    String filter_code = "";
    String filter_description = "";
    String filter_units = "";
    String filter_schedule = "";

    boolean b_id, b_code, b_description, b_units, b_schedule; // boolean to determine if textfields are empty or not
                                                              // true - string is empty | false - string has text inside

    /**
     * Creates new form studentsForm
     */
    private void getSubjectDetails() {

        DBConnect a = new DBConnect();

        try {
            String query = "select * from (select subjects.*,coalesce(count(enroll.studentID),0) as TotalSTUDENTS from subjects left join enroll on enroll.subjectID = subjects.subjectID left join students on students.studentID = enroll.studentID group by subjects.subjectID) as student_table WHERE subjectID="
                    + selected_subject_ID;
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                subjectID.setText(a.rs.getString("subjectID"));
                subjectCODE.setText(a.rs.getString("subjectCODE"));
                subjectDESCRIPTION.setText(a.rs.getString("subjectDESCRIPTION"));
                subjectUNITS.setText(a.rs.getString("subjectUNITS"));
                subjectSCHEDULE.setText(a.rs.getString("subjectSCHEDULE"));
            }
        } catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    private void clearSubjectDetails() {
        subjectID.setText("");
        subjectCODE.setText("");
        subjectDESCRIPTION.setText("");
        subjectUNITS.setText("");
        subjectSCHEDULE.setText("");
    }

    private void getClassList() {
        DefaultTableModel tblmodel = (DefaultTableModel) class_list_table.getModel();
        tblmodel.setRowCount(0);

        DBConnect a = new DBConnect();

        try {
            // nested query where selecting all data from subjects where the selected
            // subjectID from enroll is equal to studentID from enroll, where it is equal to
            // the selected studentID
            String query = "select * from students where studentID in(select studentID from enroll where subjectID in (select subjectID from enroll where subjectID = "
                    + selected_subject_ID + "))";
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

                String[] item = { id, name, address, course, gender, yrlvl };
                tblmodel.addRow(item);
            }

        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    private void clearClassList() {
        DefaultTableModel tblmodel = (DefaultTableModel) class_list_table.getModel();
        tblmodel.setRowCount(0);
    }

    public subjectsForm() {
        initComponents();
        subject_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                int selected_row_index = subject_table.getSelectedRow();
                System.out.println(selected_row_index);

                if (selected_row_index != -1) {
                    System.out.println(subject_table.getValueAt(selected_row_index, 0).toString());
                    selected_subject_ID = subject_table.getValueAt(subject_table.getSelectedRow(), 0).toString();
                    getSubjectDetails();
                    getClassList();
                    EnrollmentSystem.selected_subject_ID = selected_subject_ID;
                } else {
                    selected_subject_ID = null;
                    clearSubjectDetails();
                    clearClassList();
                    EnrollmentSystem.selected_subject_ID = selected_subject_ID;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    private void ShowSubjectRecord() {
        DefaultTableModel tblmodel = (DefaultTableModel) subject_table.getModel();
        tblmodel.setRowCount(0);

        if (ID_filter.getText().equals("")) {
            filter_ID = "";
            b_id = true;
        }

        if (code_filter_input.getText().equals("")) {
            filter_code = "";
            b_code = true;
        }

        if (description_filter_input.getText().equals("")) {
            filter_description = "";
            b_description = true;
        }

        if (units_filter.getText().equals("")) {
            filter_units = "";
            b_units = true;
        }

        if (schedule_filter_start.getText().equals("") && schedule_filter_end.getText().equals("")) {
            filter_schedule = "";
            b_schedule = true;
        }

        if (!ID_filter.getText().equals("")) {
            filter_ID = "where subjectID" + compare_operation1.getSelectedItem() + ID_filter.getText();
            b_id = false;
        }

        if (!code_filter_input.getText().equals("")) {
            filter_code = " " + logic_operation1.getSelectedItem() + " subjectCODE = '" + code_filter_input.getText()
                    + "'";
            b_code = false;
        }

        if (!description_filter_input.getText().equals("")) {
            filter_description = " " + logic_operation2.getSelectedItem() + " subjectDESCRIPTION = '"
                    + description_filter_input.getText() + "'";
            b_description = false;
        }

        if (!units_filter.getText().equals("")) {
            filter_units = " " + logic_operation3.getSelectedItem() + " subjectUNITS"
                    + compare_operation2.getSelectedItem() + units_filter.getText();
            b_units = false;
        }

        if (!schedule_filter_start.getText().equals("") || !schedule_filter_end.getText().equals("")) {
            filter_schedule = " " + logic_operation4.getSelectedItem() + " subjectSCHEDULE like '"
                    + schedule_filter_start.getText() + "%' and subjectSCHEDULE like '%" + schedule_filter_end.getText()
                    + "'";
            b_description = false;
        }

        DBConnect a = new DBConnect();

        try {
            if (b_id == true && b_code == false) {
                filter_string = "where subjectCODE = '" + code_filter_input.getText() + "'" + filter_description
                        + filter_units + filter_schedule;
            }

            else if (b_id == true && b_code == true && b_description == false) {
                filter_string = "where subjectDESCRIPTION = '" + description_filter_input.getText() + "'" + filter_units
                        + filter_schedule;
            }

            else if (b_id == true && b_code == true && b_description == true && b_units == false) {
                filter_string = "where subjectUNITS" + compare_operation2.getSelectedItem() + units_filter.getText()
                        + filter_schedule;
            }

            else if (b_id == true && b_code == true && b_description == true && b_units == true
                    && b_schedule == false) {
                filter_string = "where subjectSCHEDULE like '" + schedule_filter_start.getText()
                        + "%' and subjectSCHEDULE like '%" + schedule_filter_end.getText() + "'";
            }

            // if code filter is the only one empty
            else if (b_id == false && b_code == true && b_description == false && b_units == false
                    && b_schedule == false) {
                filter_string = filter_ID + filter_description + filter_units + filter_schedule;
            }

            // if description filter is the only one empty
            else if (b_id == false && b_code == false && b_description == true && b_units == false
                    && b_schedule == false) {
                filter_string = filter_ID + filter_code + filter_units + filter_schedule;
            }

            // if units filter is the only one empty
            else if (b_id == false && b_code == false && b_description == false && b_units == true
                    && b_schedule == false) {
                filter_string = filter_ID + filter_code + filter_description + filter_schedule;
            }

            // if schedule filter is the only one empty
            else if (b_id == false && b_code == false && b_description == false && b_units == false
                    && b_schedule == true) {
                filter_string = filter_ID + filter_code + filter_description + filter_units;
            }

            // if all filters are used
            else {
                filter_string = filter_ID + filter_code + filter_description + filter_units + filter_schedule;
            }

            String query = "select * from (select subjects.*,coalesce(count(students.studentID), 0) as TotalSTUDENTS from subjects left join enroll on enroll.subjectID = subjects.subjectID left join students on students.studentID = enroll.studentID group by subjects.subjectID) as subject_table "
                    + filter_string;
            System.out.println(query);
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");

            while (a.rs.next()) {
                String id = a.rs.getString("subjectID");
                String code = a.rs.getString("subjectCODE");
                String description = a.rs.getString("subjectDESCRIPTION");
                String units = a.rs.getString("subjectUNITS");
                String schedule = a.rs.getString("subjectSCHEDULE");
                String Tstudents = a.rs.getString("TotalSTUDENTS");

                String[] item = { id, code, description, units, schedule, Tstudents };
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
        subjectID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        subjectCODE = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        subjectDESCRIPTION = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        subjectUNITS = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        subjectSCHEDULE = new javax.swing.JTextField();
        Save_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        subject_table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        compare_operation1 = new javax.swing.JComboBox();
        ID_filter = new javax.swing.JTextField();
        logic_operation1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        code_filter_input = new javax.swing.JTextField();
        logic_operation2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        description_filter_input = new javax.swing.JTextField();
        logic_operation3 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        units_filter = new javax.swing.JTextField();
        compare_operation2 = new javax.swing.JComboBox();
        logic_operation4 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        schedule_filter_start = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        schedule_filter_end = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        class_list_table = new javax.swing.JTable();

        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }

            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Enter Subject ID");

        subjectID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectIDActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter Subject Code");
        jLabel2.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel3.setText("Enter Subject Description");
        jLabel3.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel4.setText("Enter Subject Units");

        jLabel5.setText("Enter Subject Schedule");

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

        subject_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null }
                },
                new String[] {
                        "ID", "Code", "Description", "Units", "Schedule", "Students Enrolled"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        subject_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                subject_tableMouseExited(evt);
            }
        });
        jScrollPane1.setViewportView(subject_table);
        if (subject_table.getColumnModel().getColumnCount() > 0) {
            subject_table.getColumnModel().getColumn(0).setResizable(false);
            subject_table.getColumnModel().getColumn(0).setPreferredWidth(25);
            subject_table.getColumnModel().getColumn(1).setResizable(false);
            subject_table.getColumnModel().getColumn(1).setPreferredWidth(30);
            subject_table.getColumnModel().getColumn(2).setResizable(false);
            subject_table.getColumnModel().getColumn(3).setResizable(false);
            subject_table.getColumnModel().getColumn(3).setPreferredWidth(25);
            subject_table.getColumnModel().getColumn(4).setResizable(false);
            subject_table.getColumnModel().getColumn(5).setResizable(false);
            subject_table.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        jLabel6.setText("Subject ID");

        compare_operation1
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", "<", ">=", "<=", "<>" }));
        compare_operation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compare_operation1ActionPerformed(evt);
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

        jLabel7.setText("Subject Code");

        code_filter_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                code_filter_inputActionPerformed(evt);
            }
        });
        code_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                code_filter_inputKeyReleased(evt);
            }
        });

        logic_operation2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation2ActionPerformed(evt);
            }
        });

        jLabel8.setText("Subject Description");

        description_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                description_filter_inputKeyReleased(evt);
            }
        });

        logic_operation3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation3ActionPerformed(evt);
            }
        });

        jLabel9.setText("Subject Units");

        units_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                units_filterKeyReleased(evt);
            }
        });

        compare_operation2
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", "<", ">=", "<=", "<>" }));
        compare_operation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compare_operation2ActionPerformed(evt);
            }
        });

        logic_operation4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation4ActionPerformed(evt);
            }
        });

        jLabel10.setText("Subject Schedule");

        schedule_filter_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedule_filter_startActionPerformed(evt);
            }
        });
        schedule_filter_start.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                schedule_filter_startKeyReleased(evt);
            }
        });

        jLabel11.setText("Starts");

        jLabel12.setText("Ends");

        schedule_filter_end.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                schedule_filter_endKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(schedule_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(schedule_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel6)
                                                        .addGap(22, 22, 22)
                                                        .addComponent(compare_operation1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel7)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(code_filter_input,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 67,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(description_filter_input,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 67,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(compare_operation2,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(units_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel10))
                                .addGap(0, 0, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(compare_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(code_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(description_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(compare_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(units_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(schedule_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel12)
                                        .addComponent(schedule_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(132, Short.MAX_VALUE)));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("CLASS LIST");
        jLabel13.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        class_list_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null },
                        { null, null, null, null, null, null }
                },
                new String[] {
                        "Student ID", "Name", "Address", "Course", "Gender", "Year Level"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        class_list_table.getTableHeader().setReorderingAllowed(false);
        class_list_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_list_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(class_list_table);
        if (class_list_table.getColumnModel().getColumnCount() > 0) {
            class_list_table.getColumnModel().getColumn(0).setResizable(false);
            class_list_table.getColumnModel().getColumn(1).setResizable(false);
            class_list_table.getColumnModel().getColumn(2).setResizable(false);
            class_list_table.getColumnModel().getColumn(3).setResizable(false);
            class_list_table.getColumnModel().getColumn(4).setResizable(false);
            class_list_table.getColumnModel().getColumn(4).setPreferredWidth(30);
            class_list_table.getColumnModel().getColumn(5).setResizable(false);
            class_list_table.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(subjectCODE,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 136,
                                                                Short.MAX_VALUE)
                                                        .addComponent(subjectDESCRIPTION,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(subjectUNITS)
                                                        .addComponent(subjectID))
                                                .addComponent(subjectSCHEDULE, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(Save_btn, javax.swing.GroupLayout.Alignment.TRAILING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 75,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(delete_btn, javax.swing.GroupLayout.Alignment.TRAILING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 75,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(update_btn, javax.swing.GroupLayout.Alignment.TRAILING,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 75,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 208,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel13,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                                        Short.MAX_VALUE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(subjectID,
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
                                                                        .addComponent(subjectCODE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(subjectDESCRIPTION,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(subjectUNITS,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(subjectSCHEDULE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(Save_btn,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(delete_btn,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(update_btn,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 24,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap()));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void subjectIDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_subjectIDActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_subjectIDActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_update_btnActionPerformed
        subjects sub = new subjects();
        sub.edit_subject(Integer.parseInt(subjectID.getText()), subjectCODE.getText(), subjectDESCRIPTION.getText(),
                Integer.parseInt(subjectUNITS.getText()), subjectSCHEDULE.getText());
        ShowSubjectRecord();
    }// GEN-LAST:event_update_btnActionPerformed

    private void Save_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_Save_btnActionPerformed
        subjects sub = new subjects();
        sub.add_subject(Integer.parseInt(subjectID.getText()), subjectCODE.getText(), subjectDESCRIPTION.getText(),
                Integer.parseInt(subjectUNITS.getText()), subjectSCHEDULE.getText());
        ShowSubjectRecord();
    }// GEN-LAST:event_Save_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_delete_btnActionPerformed
        subjects sub = new subjects();
        sub.delete_subject(filter_string);
        ShowSubjectRecord();
    }// GEN-LAST:event_delete_btnActionPerformed

    private void code_filter_inputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_code_filter_inputActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_code_filter_inputActionPerformed

    private void compare_operation2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compare_operation2ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_compare_operation2ActionPerformed

    private void compare_operation1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compare_operation1ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_compare_operation1ActionPerformed

    private void logic_operation1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation1ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_logic_operation1ActionPerformed

    private void logic_operation2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation2ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_logic_operation2ActionPerformed

    private void logic_operation3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation3ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_logic_operation3ActionPerformed

    private void logic_operation4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation4ActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_logic_operation4ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        ShowSubjectRecord();
    }// GEN-LAST:event_formWindowOpened

    private void ID_filterKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_ID_filterKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_ID_filterKeyReleased

    private void subject_tableMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_subject_tableMouseExited
        // TODO add your handling code here:
    }// GEN-LAST:event_subject_tableMouseExited

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowLostFocus

    }// GEN-LAST:event_formWindowLostFocus

    private void schedule_filter_startKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_schedule_filter_startKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_schedule_filter_startKeyReleased

    private void schedule_filter_endKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_schedule_filter_endKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_schedule_filter_endKeyReleased

    private void units_filterKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_units_filterKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_units_filterKeyReleased

    private void description_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_description_filter_inputKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_description_filter_inputKeyReleased

    private void code_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_code_filter_inputKeyReleased
        ShowSubjectRecord();
    }// GEN-LAST:event_code_filter_inputKeyReleased

    private void schedule_filter_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_schedule_filter_startActionPerformed
        ShowSubjectRecord();
    }// GEN-LAST:event_schedule_filter_startActionPerformed

    private void class_list_tableMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_class_list_tableMouseClicked
        // TODO add your handling code here:
    }// GEN-LAST:event_class_list_tableMouseClicked

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowGainedFocus

    }// GEN-LAST:event_formWindowGainedFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6subjectCODEailable, stay with the default
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
            java.util.logging.Logger.getLogger(subjectsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(subjectsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(subjectsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(subjectsForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new subjectsForm().setVisible(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ID_filter;
    private javax.swing.JButton Save_btn;
    private javax.swing.JTable class_list_table;
    private javax.swing.JTextField code_filter_input;
    private javax.swing.JComboBox compare_operation1;
    private javax.swing.JComboBox compare_operation2;
    private javax.swing.JButton delete_btn;
    private javax.swing.JTextField description_filter_input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox logic_operation1;
    private javax.swing.JComboBox logic_operation2;
    private javax.swing.JComboBox logic_operation3;
    private javax.swing.JComboBox logic_operation4;
    private javax.swing.JTextField schedule_filter_end;
    private javax.swing.JTextField schedule_filter_start;
    private javax.swing.JTextField subjectCODE;
    private javax.swing.JTextField subjectDESCRIPTION;
    private javax.swing.JTextField subjectID;
    private javax.swing.JTextField subjectSCHEDULE;
    private javax.swing.JTextField subjectUNITS;
    private javax.swing.JTable subject_table;
    private javax.swing.JTextField units_filter;
    private javax.swing.JButton update_btn;
    // End of variables declaration//GEN-END:variables
}
