/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enrollmentsystem;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class teachersForm extends javax.swing.JFrame {

    String filter_string = ";";

    String selected_teacher_ID = "";
    String selected_subject_ID = "";

    String filter_ID = "";
    String filter_name = "";
    String filter_department = "";
    String filter_address = "";
    String filter_contact = "";
    String filter_status = "";

    boolean b_id, b_name, b_department, b_address, b_contact, b_status; // boolean to determine if textfields are empty
                                                                        // or not
                                                                        // true - string is empty | false - string has
                                                                        // text inside

    /**
     * Creates new form studentsForm
     */
    private void getTeacherDetails() {

        DBConnect a = new DBConnect();

        try {
            String query = "select * from (select teachers.*,coalesce(count(subjectUNITS),0) as TotalSUBJECTS from teachers left join assign on teachers.teacherID = assign.teacherID left join subjects on assign.subjectID = subjects.subjectID group by teachers.teacherID) as teacher_table WHERE teacherID="
                    + selected_teacher_ID;
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");
            while (a.rs.next()) {
                teacherID.setText(a.rs.getString("teacherID"));
                teacherNAME.setText(a.rs.getString("teacherNAME"));
                teacherDEPARTMENT.setText(a.rs.getString("teacherDEPARTMENT"));
                teacherADDRESS.setText(a.rs.getString("teacherADDRESS"));
                teacherCONTACT.setText(a.rs.getString("teacherCONTACT"));
                teacherSTATUS.setText(a.rs.getString("teacherSTATUS"));
            }
        } catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    private void clearTeacherDetails() {
        teacherID.setText("");
        teacherNAME.setText("");
        teacherDEPARTMENT.setText("");
        teacherADDRESS.setText("");
        teacherCONTACT.setText("");
        teacherSTATUS.setText("");
    }

    private void getAssignedSubjects() {
        DefaultTableModel tblmodel = (DefaultTableModel) Assigned_subjects_table.getModel();
        tblmodel.setRowCount(0);

        DBConnect a = new DBConnect();

        try {
            // nested query where selecting all data from subjects where the selected
            // subjectID from enroll is equal to studentID from enroll, where it is equal to
            // the selected studentID
            String query = "select * from subjects where subjectID in(select subjectID from assign where teacherID in (select teacherID from assign where teacherID = "
                    + selected_teacher_ID + "))";
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

    private void clearAssignedSubjects() {
        DefaultTableModel tblmodel = (DefaultTableModel) Assigned_subjects_table.getModel();
        tblmodel.setRowCount(0);

    }

    public teachersForm() {
        initComponents();
        teacher_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row
                int selected_row_index = teacher_table.getSelectedRow();
                System.out.println(selected_row_index);

                if (selected_row_index != -1) {
                    System.out.println(teacher_table.getValueAt(selected_row_index, 0).toString());
                    selected_teacher_ID = teacher_table.getValueAt(teacher_table.getSelectedRow(), 0).toString();
                    getTeacherDetails();
                    getAssignedSubjects();
                } else {
                    selected_teacher_ID = null;
                    clearTeacherDetails();
                    clearAssignedSubjects();
                }
            }
        });

        Assigned_subjects_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row

                int selected_row_index = Assigned_subjects_table.getSelectedRow();
                System.out.println(selected_row_index);

                if (selected_row_index != -1) {
                    System.out.println(Assigned_subjects_table.getValueAt(selected_row_index, 0).toString());
                    selected_subject_ID = Assigned_subjects_table
                            .getValueAt(Assigned_subjects_table.getSelectedRow(), 0).toString();
                } else { // deselect row
                    selected_subject_ID = null;
                }
            }
        });

    }

    private void ShowTeacherRecord() {
        DefaultTableModel tblmodel = (DefaultTableModel) teacher_table.getModel();
        tblmodel.setRowCount(0);

        if (ID_filter.getText().equals("")) {
            filter_ID = "";
            b_id = true;
        }

        if (name_filter_input.getText().equals("")) {
            filter_name = "";
            b_name = true;
        }

        if (department_filter_start.getText().equals("") || department_filter_end.getText().equals("")) {
            filter_department = "";
            b_department = true;
        }

        if (address_filter_input.getText().equals("")) {
            filter_address = "";
            b_address = true;
        }

        if (contact_filter.getText().equals("")) {
            filter_contact = "";
            b_contact = true;
        }

        if (status_filter_input.getText().equals("")) {
            filter_status = "";
            b_status = true;
        }

        if (!ID_filter.getText().equals("")) {
            filter_ID = "where teacherID" + compare_operation1.getSelectedItem() + ID_filter.getText();
            b_id = false;
        }

        if (!name_filter_input.getText().equals("")) {
            filter_name = " " + logic_operation1.getSelectedItem() + " teacherNAME = '" + name_filter_input.getText()
                    + "'";
            b_name = false;
        }

        if (!department_filter_start.getText().equals("") || !department_filter_end.getText().equals("")) {
            filter_department = " " + logic_operation2.getSelectedItem() + " teacherDEPARTMENT like '"
                    + department_filter_start.getText() + "%' and teacherDEPARTMENT like '%"
                    + department_filter_end.getText() + "'";
            b_department = false;
        }

        if (!address_filter_input.getText().equals("")) {
            filter_address = " " + logic_operation3.getSelectedItem() + " teacherADDRESS = '"
                    + address_filter_input.getText() + "'";
            b_address = false;
        }

        if (!contact_filter.getText().equals("")) {
            filter_contact = " " + logic_operation4.getSelectedItem() + " teacherCONTACT"
                    + compare_operation2.getSelectedItem() + contact_filter.getText();
            b_contact = false;
        }

        if (!status_filter_input.getText().equals("")) {
            filter_status = " " + logic_operation5.getSelectedItem() + " teacherSTATUS = '"
                    + status_filter_input.getText() + "'";
            b_status = false;
        }

        DBConnect a = new DBConnect();

        try {
            if (b_id == true && b_name == false) {
                filter_string = "where teacherNAME = '" + name_filter_input.getText() + "'" + filter_address
                        + filter_department + filter_address + filter_contact + filter_status;
            }

            else if (b_id == true && b_name == true && b_department == false) {
                filter_string = "where teacherDEPARTMENT like '" + department_filter_start.getText()
                        + "%' and teacherDEPARTMENT like '%" + department_filter_end.getText() + "'" + filter_address
                        + filter_contact + filter_status;
            }

            else if (b_id == true && b_name == true && b_department == true && b_address == false) {
                filter_string = "where teacherADDRESS = '" + address_filter_input.getText() + "'" + filter_contact
                        + filter_status;
            }

            else if (b_id == true && b_name == true && b_department == true && b_address == true
                    && b_contact == false) {
                filter_string = "where teacherCONTACT" + compare_operation2.getSelectedItem() + contact_filter.getText()
                        + filter_status;
            }

            else if (b_id == true && b_name == true && b_department == true && b_address == true && b_contact == true
                    && b_status == false) {
                filter_string = "where teacherSTATUS = '" + status_filter_input.getText() + "'";
            }

            // if name filter is the only one empty
            else if (b_id == false && b_name == true && b_department == false && b_address == false
                    && b_contact == false && b_status == false) {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }

            // if department filter is the only one empty
            else if (b_id == false && b_name == false && b_department == true && b_address == false
                    && b_contact == false && b_status == false) {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }

            // if address filter is the only one empty
            else if (b_id == false && b_name == false && b_department == false && b_address == true
                    && b_contact == false && b_status == false) {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }

            // if contact filter is the only one empty
            else if (b_id == false && b_name == false && b_department == false && b_address == false
                    && b_contact == true && b_status == false) {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }

            // if status filter is the only one empty
            else if (b_id == false && b_name == false && b_department == false && b_address == false
                    && b_contact == false && b_status == true) {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }

            // if all filters are used
            else {
                filter_string = filter_ID + filter_name + filter_department + filter_address + filter_contact
                        + filter_status;
            }
            String query = "select * from (select teachers.*,coalesce(count(subjectUNITS),0) as TotalSUBJECTS from teachers left join assign on teachers.teacherID = assign.teacherID left join subjects on assign.subjectID = subjects.subjectID group by teachers.teacherID) as teacher_table "
                    + filter_string;
            System.out.println(query);
            a.rs = a.st.executeQuery(query);
            System.out.println("Query Success!");

            while (a.rs.next()) {
                String id = a.rs.getString("teacherID");
                String name = a.rs.getString("teacherNAME");
                String dept = a.rs.getString("teacherDEPARTMENT");
                String address = a.rs.getString("teacherADDRESS");
                String contact = a.rs.getString("teacherCONTACT");
                String status = a.rs.getString("teacherSTATUS");
                String Tsubjects = a.rs.getString("TotalSUBJECTS");

                String[] teacher = { id, name, dept, address, contact, status, Tsubjects };
                tblmodel.addRow(teacher);
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        teacherID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        teacherNAME = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        teacherDEPARTMENT = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        teacherADDRESS = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        teacherCONTACT = new javax.swing.JTextField();
        Save_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        teacherSTATUS = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        teacher_table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ID_filter = new javax.swing.JTextField();
        compare_operation1 = new javax.swing.JComboBox();
        logic_operation1 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        department_filter_start = new javax.swing.JTextField();
        department_filter_end = new javax.swing.JTextField();
        logic_operation2 = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        address_filter_input = new javax.swing.JTextField();
        logic_operation3 = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        contact_filter = new javax.swing.JTextField();
        logic_operation4 = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        status_filter_input = new javax.swing.JTextField();
        compare_operation2 = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        name_filter_input = new javax.swing.JTextField();
        logic_operation5 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Assigned_subjects_table = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        retract_btn = new javax.swing.JButton();
        assign_btn = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }));
        jScrollPane1.setViewportView(jTable1);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Enter Teacher ID");

        teacherID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherIDActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter Teacher Name");
        jLabel2.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel3.setText("Enter Teacher Department");
        jLabel3.setMaximumSize(new java.awt.Dimension(81, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(81, 14));

        jLabel4.setText("Enter Teacher Address");

        jLabel5.setText("Enter Teacher Contact No.");

        teacherCONTACT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherCONTACTActionPerformed(evt);
            }
        });

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

        jLabel7.setText("Enter Teacher Status");

        teacherSTATUS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherSTATUSActionPerformed(evt);
            }
        });

        teacher_table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null },
                        { null, null, null, null, null, null, null }
                },
                new String[] {
                        "ID", "Name", "Department", "Address", "Contact", "Status", "Assigned Subjects"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        teacher_table.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(teacher_table);
        if (teacher_table.getColumnModel().getColumnCount() > 0) {
            teacher_table.getColumnModel().getColumn(0).setResizable(false);
            teacher_table.getColumnModel().getColumn(0).setPreferredWidth(25);
            teacher_table.getColumnModel().getColumn(1).setResizable(false);
            teacher_table.getColumnModel().getColumn(1).setPreferredWidth(50);
            teacher_table.getColumnModel().getColumn(2).setResizable(false);
            teacher_table.getColumnModel().getColumn(2).setPreferredWidth(75);
            teacher_table.getColumnModel().getColumn(3).setResizable(false);
            teacher_table.getColumnModel().getColumn(3).setPreferredWidth(75);
            teacher_table.getColumnModel().getColumn(4).setResizable(false);
            teacher_table.getColumnModel().getColumn(5).setResizable(false);
            teacher_table.getColumnModel().getColumn(5).setPreferredWidth(75);
            teacher_table.getColumnModel().getColumn(6).setResizable(false);
            teacher_table.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        jPanel1.setName(""); // NOI18N

        jLabel6.setText("Teacher ID");

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

        compare_operation1
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", "<", ">=", "<=", "<>" }));
        compare_operation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compare_operation1ActionPerformed(evt);
            }
        });

        logic_operation1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));

        jLabel8.setText("Teacher Department");

        jLabel9.setText("Ends");

        department_filter_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                department_filter_startActionPerformed(evt);
            }
        });
        department_filter_start.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                department_filter_startKeyReleased(evt);
            }
        });

        department_filter_end.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                department_filter_endKeyReleased(evt);
            }
        });

        logic_operation2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation2ActionPerformed(evt);
            }
        });

        jLabel10.setText("Starts");

        jLabel11.setText("Teacher Address");

        address_filter_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                address_filter_inputActionPerformed(evt);
            }
        });
        address_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                address_filter_inputKeyReleased(evt);
            }
        });

        logic_operation3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation3ActionPerformed(evt);
            }
        });

        jLabel12.setText("Teacher Contact");

        contact_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                contact_filterKeyReleased(evt);
            }
        });

        logic_operation4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));
        logic_operation4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logic_operation4ActionPerformed(evt);
            }
        });

        jLabel13.setText("Teacher Status");

        status_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                status_filter_inputKeyReleased(evt);
            }
        });

        compare_operation2
                .setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", "<", ">=", "<=", "<>" }));
        compare_operation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compare_operation2ActionPerformed(evt);
            }
        });

        jLabel14.setText("Teacher Name");

        name_filter_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                name_filter_inputActionPerformed(evt);
            }
        });
        name_filter_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                name_filter_inputKeyReleased(evt);
            }
        });

        logic_operation5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OR", "AND" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(status_filter_input))
                        .addComponent(contact_filter, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(name_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(37, 37, 37)
                                .addComponent(compare_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(department_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addGap(10, 10, 10)
                                .addComponent(department_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(logic_operation5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel12)
                                        .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout.createSequentialGroup()
                                                        .addComponent(compare_operation2,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(59, 59, 59))
                                        .addComponent(address_filter_input, javax.swing.GroupLayout.Alignment.TRAILING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(ID_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(compare_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel14)
                                        .addComponent(name_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(department_filter_end, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel10)
                                        .addComponent(department_filter_start, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation3, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel11)
                                        .addComponent(address_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(compare_operation2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(contact_filter, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logic_operation5, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel13)
                                        .addComponent(status_filter_input, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("ASSIGNED SUBJECTS");
        jLabel15.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel15.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        Assigned_subjects_table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(Assigned_subjects_table);
        if (Assigned_subjects_table.getColumnModel().getColumnCount() > 0) {
            Assigned_subjects_table.getColumnModel().getColumn(0).setResizable(false);
            Assigned_subjects_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            Assigned_subjects_table.getColumnModel().getColumn(1).setResizable(false);
            Assigned_subjects_table.getColumnModel().getColumn(1).setPreferredWidth(50);
            Assigned_subjects_table.getColumnModel().getColumn(2).setResizable(false);
            Assigned_subjects_table.getColumnModel().getColumn(3).setResizable(false);
            Assigned_subjects_table.getColumnModel().getColumn(3).setPreferredWidth(15);
            Assigned_subjects_table.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Assigned Subjects"));

        retract_btn.setText("RETRACT");
        retract_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        retract_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        retract_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retract_btnActionPerformed(evt);
            }
        });

        assign_btn.setText("ASSIGN");
        assign_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assign_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addGroup(jPanel2Layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(assign_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(retract_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(36, Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(assign_btn)
                                .addGap(18, 18, 18)
                                .addComponent(retract_btn)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(teacherNAME, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(teacherID, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(teacherDEPARTMENT)
                                        .addComponent(teacherADDRESS, javax.swing.GroupLayout.DEFAULT_SIZE, 160,
                                                Short.MAX_VALUE)
                                        .addComponent(teacherCONTACT)
                                        .addComponent(Save_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(delete_btn, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(update_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 160,
                                                Short.MAX_VALUE)
                                        .addComponent(teacherSTATUS))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 675,
                                                Short.MAX_VALUE)
                                        .addComponent(jScrollPane3)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
                                                .createSequentialGroup()
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(teacherID,
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
                                                                        .addComponent(teacherNAME,
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
                                                                        .addComponent(teacherDEPARTMENT,
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
                                                                        .addComponent(teacherADDRESS,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel5,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(teacherCONTACT,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel7,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(teacherSTATUS,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(Save_btn)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(delete_btn)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(update_btn)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jPanel2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
                                                                .createSequentialGroup()
                                                                .addComponent(jScrollPane2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel15,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane3,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 200,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap()));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void teacherIDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_teacherIDActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_teacherIDActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_update_btnActionPerformed
        teachers teach = new teachers();
        teach.edit_teacher(Integer.parseInt(teacherID.getText()), teacherNAME.getText(), teacherDEPARTMENT.getText(),
                teacherADDRESS.getText(), teacherCONTACT.getText(), teacherSTATUS.getText());
        ShowTeacherRecord();
    }// GEN-LAST:event_update_btnActionPerformed

    private void Save_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_Save_btnActionPerformed
        teachers teach = new teachers();
        teach.add_teacher(Integer.parseInt(teacherID.getText()), teacherNAME.getText(), teacherDEPARTMENT.getText(),
                teacherADDRESS.getText(), teacherCONTACT.getText(), teacherSTATUS.getText());
        ShowTeacherRecord();
    }// GEN-LAST:event_Save_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_delete_btnActionPerformed
        teachers teach = new teachers();
        teach.delete_teacher(filter_string);
        ShowTeacherRecord();
    }// GEN-LAST:event_delete_btnActionPerformed

    private void teacherCONTACTActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_teacherCONTACTActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_teacherCONTACTActionPerformed

    private void teacherSTATUSActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_teacherSTATUSActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_teacherSTATUSActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        ShowTeacherRecord();
    }// GEN-LAST:event_formWindowOpened

    private void department_filter_startActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_department_filter_startActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_department_filter_startActionPerformed

    private void address_filter_inputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_address_filter_inputActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_address_filter_inputActionPerformed

    private void logic_operation3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation3ActionPerformed
        ShowTeacherRecord();
    }// GEN-LAST:event_logic_operation3ActionPerformed

    private void compare_operation2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compare_operation2ActionPerformed
        ShowTeacherRecord();
    }// GEN-LAST:event_compare_operation2ActionPerformed

    private void ID_filterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ID_filterActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_ID_filterActionPerformed

    private void compare_operation1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_compare_operation1ActionPerformed
        ShowTeacherRecord();
    }// GEN-LAST:event_compare_operation1ActionPerformed

    private void logic_operation2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation2ActionPerformed
        ShowTeacherRecord();
    }// GEN-LAST:event_logic_operation2ActionPerformed

    private void logic_operation4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logic_operation4ActionPerformed
        ShowTeacherRecord();
    }// GEN-LAST:event_logic_operation4ActionPerformed

    private void ID_filterKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_ID_filterKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_ID_filterKeyReleased

    private void department_filter_startKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_department_filter_startKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_department_filter_startKeyReleased

    private void department_filter_endKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_department_filter_endKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_department_filter_endKeyReleased

    private void address_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_address_filter_inputKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_address_filter_inputKeyReleased

    private void contact_filterKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_contact_filterKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_contact_filterKeyReleased

    private void status_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_status_filter_inputKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_status_filter_inputKeyReleased

    private void name_filter_inputActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_name_filter_inputActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_name_filter_inputActionPerformed

    private void name_filter_inputKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_name_filter_inputKeyReleased
        ShowTeacherRecord();
    }// GEN-LAST:event_name_filter_inputKeyReleased

    private void assign_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_assign_btnActionPerformed
        String selected_subject_ID = EnrollmentSystem.selected_subject_ID;

        if (selected_subject_ID == null) {// prevents user to send null data
            return;
        }

        try {

            int i = okcancel(
                    "Assign Subject No. " + selected_subject_ID + " to Teacher ID: " + selected_teacher_ID + " ?");

            if (i == 0) {
                Assign assign = new Assign();

                assign.assign_Subject(Integer.parseInt(selected_teacher_ID), Integer.parseInt(selected_subject_ID));
                getTeacherDetails();
                getAssignedSubjects();
                ShowTeacherRecord();

                JOptionPane.showMessageDialog(null, "Subject Assigned!");
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }// GEN-LAST:event_assign_btnActionPerformed

    private void retract_btnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_retract_btnActionPerformed
        if (selected_subject_ID == null) {
            return;
        }

        try {
            int i = okcancel(
                    "Retract Subject No. " + selected_subject_ID + " for Teacher ID: " + selected_teacher_ID + " ?");

            if (i == 0) {
                Assign assign = new Assign();

                assign.retract_Subject(Integer.parseInt(selected_teacher_ID), Integer.parseInt(selected_subject_ID));
                getTeacherDetails();
                getAssignedSubjects();
                ShowTeacherRecord();

                JOptionPane.showMessageDialog(null, "Subject Retracted!");
            }
        }

        catch (Exception ex) {
            System.out.println("Query Failed: " + ex);
        }
    }// GEN-LAST:event_retract_btnActionPerformed

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
            java.util.logging.Logger.getLogger(teachersForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(teachersForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(teachersForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(teachersForm.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new teachersForm().setVisible(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Assigned_subjects_table;
    private javax.swing.JTextField ID_filter;
    private javax.swing.JButton Save_btn;
    private javax.swing.JTextField address_filter_input;
    private javax.swing.JButton assign_btn;
    private javax.swing.JComboBox compare_operation1;
    private javax.swing.JComboBox compare_operation2;
    private javax.swing.JTextField contact_filter;
    private javax.swing.JButton delete_btn;
    private javax.swing.JTextField department_filter_end;
    private javax.swing.JTextField department_filter_start;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox logic_operation1;
    private javax.swing.JComboBox logic_operation2;
    private javax.swing.JComboBox logic_operation3;
    private javax.swing.JComboBox logic_operation4;
    private javax.swing.JComboBox logic_operation5;
    private javax.swing.JTextField name_filter_input;
    private javax.swing.JButton retract_btn;
    private javax.swing.JTextField status_filter_input;
    private javax.swing.JTextField teacherADDRESS;
    private javax.swing.JTextField teacherCONTACT;
    private javax.swing.JTextField teacherDEPARTMENT;
    private javax.swing.JTextField teacherID;
    private javax.swing.JTextField teacherNAME;
    private javax.swing.JTextField teacherSTATUS;
    private javax.swing.JTable teacher_table;
    private javax.swing.JButton update_btn;
    // End of variables declaration//GEN-END:variables
}
