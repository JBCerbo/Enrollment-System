/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 *
 * @author Jesse Benjamin Cerbo
 */
public class CreatePDF {

    public void generateStudentGrades() {

        Document doc = new Document();
        PdfWriter docWriter = null;
        DecimalFormat df = new DecimalFormat("0.00");
        Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
        Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

        String studentID = null;
        String studentNAME = null;
        String studentADDRESS = null;
        String studentCOURSE = null;
        String studentGENDER = null;
        String studentYEARLEVEL = null;

        try {
            DBConnect connect = new DBConnect();
            String getStudentQuery = String.format("SELECT * FROM students where studentID = %d",
                    EnrollmentSystem.logged_in_studentID);
            System.out.println(getStudentQuery);
            connect.rs = connect.st.executeQuery(getStudentQuery);
            System.out.println("Query Success!");
            while (connect.rs.next()) {
                studentID = connect.rs.getString("studentID");
                studentNAME = connect.rs.getString("studentNAME");
                studentADDRESS = connect.rs.getString("studentADDRESS");
                studentCOURSE = connect.rs.getString("studentCOURSE");
                studentGENDER = connect.rs.getString("studentGENDER");
                studentYEARLEVEL = connect.rs.getString("studentYEARLEVEL");
            }

            DBConnect connect2 = new DBConnect();
            String query2 = "SELECT subjectID, subjectCode, Prelim, Midterm, Prefinal, Final FROM (SELECT enroll.enrollID, enroll.subjectID, enroll.studentID, subjects.subjectCode, grades.Prelim, grades.Midterm, grades.Prefinal, grades.Final FROM enroll LEFT JOIN subjects ON enroll.subjectID = subjects.subjectID LEFT JOIN grades ON enroll.enrollID = grades.enrollID ) AS class_grades_tbl WHERE studentID = "
                    + EnrollmentSystem.logged_in_studentID;
            connect2.rs = connect2.st.executeQuery(query2);
            System.out.println("Query Success!");

            String path = "c://docs//report.pdf";
            File file = new File(path);
            File directory = new File(file.getParent());
            if (!directory.exists()) {
                directory.mkdirs();
            }
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            float[] columnWidths2 = { 2f, 5f };
            float[] columnWidths = { 2f, 3f, 2f, 2f, 2f, 2f };

            PdfPTable table2 = new PdfPTable(columnWidths2);
            table2.setWidthPercentage(50f);
            table2.getDefaultCell().setBorder(0);
            File classPathInput = new File(CreatePDF.class.getResource("UniversitySeal240px.png").getFile());
            table2.addCell(Image.getInstance(classPathInput.toURL()));
            table2.getDefaultCell().setBorder(0);
            table2.addCell("Ateneo De Davao University\nRegistrars Office");
            doc.add(table2);

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);
            Paragraph paragraph = new Paragraph();

            insertCell(table, "Student Grade Sheet", Element.ALIGN_CENTER, 6, bfBold12, 0, 255, 255, 255);

            insertCell(table, "", Element.ALIGN_LEFT, 6, bfBold12, 0, 255, 255, 255);

            insertCell(table, "Student ID: " + studentID, Element.ALIGN_LEFT, 3, bfBold12, 0, 255, 255, 255);
            insertCell(table, "School Year: " + EnrollmentSystem.selected_database, Element.ALIGN_LEFT, 3, bfBold12, 0,
                    255, 255, 255);
            insertCell(table, "Student Name: " + studentNAME, Element.ALIGN_LEFT, 3, bfBold12, 0, 255, 255, 255);
            insertCell(table, "Student Course: " + studentCOURSE, Element.ALIGN_LEFT, 3, bfBold12, 0, 255, 255, 255);
            insertCell(table, "Student Year: " + studentYEARLEVEL, Element.ALIGN_LEFT, 3, bfBold12, 0, 255, 255, 255);

            insertCell(table, "", Element.ALIGN_LEFT, 3, bfBold12, 0, 255, 255, 255);
            insertCell(table, "", Element.ALIGN_LEFT, 6, bfBold12, 0, 255, 255, 255);

            insertCell(table, "Subject ID", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);
            insertCell(table, "Code", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);
            insertCell(table, "Prelim", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);
            insertCell(table, "Mid-Term", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);
            insertCell(table, "Pre-Final", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);
            insertCell(table, "Final", Element.ALIGN_CENTER, 1, bfBold12, 1, 255, 255, 255);

            insertCell(table, "", Element.ALIGN_LEFT, 6, bfBold12, 0, 255, 255, 255);

            int subjectCount = 0;

            while (connect2.rs.next()) {
                String id = connect2.rs.getString("subjectID");
                String code = connect2.rs.getString("subjectCode");
                String Prelim = connect2.rs.getString("Prelim");
                String Midterm = connect2.rs.getString("Midterm");
                String Prefinal = connect2.rs.getString("Prefinal");
                String Final = connect2.rs.getString("Final");

                insertCell(table, id, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                insertCell(table, code, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                insertCell(table, Prelim, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                insertCell(table, Midterm, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                insertCell(table, Prefinal, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                insertCell(table, Final, Element.ALIGN_CENTER, 1, bf12, 1, 255, 255, 255);
                subjectCount++;
            }

            insertCell(table, "", Element.ALIGN_LEFT, 6, bfBold12, 0, 255, 255, 255);

            insertCell(table, "Number of Subjects Listed: " + subjectCount, Element.ALIGN_LEFT, 6, bfBold12, 0, 255,
                    255, 255);

            paragraph.add(table);

            doc.add(paragraph);

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                // close the document
                doc.close();
            }
            if (docWriter != null) {
                // close the writer
                docWriter.close();
            }
        }
        try {
            Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + "c:\\docs\\report.pdf");
        } catch (Exception e) {

        }
    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font, int border, int r, int g,
            int b) {

        if (text == null || text.trim().isEmpty()) {
            text = "";
        }

        // create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        // set the cell alignment

        cell.setHorizontalAlignment(align);
        // set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        // in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        if (border == 0) {
            cell.setBorder(Rectangle.NO_BORDER);
        } else {
            cell.setBorder(Rectangle.BOX);
        }
        cell.setBackgroundColor(new BaseColor(r, g, b));
        // add the call to the table
        table.addCell(cell);

    }
}
