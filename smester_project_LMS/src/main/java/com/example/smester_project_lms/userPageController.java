package com.example.smester_project_lms;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.plaf.basic.BasicBorders;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

public class userPageController implements Initializable {
    @FXML TableView<booksTable> table;
    @FXML TableColumn<booksTable, ImageView> image;
    @FXML TableColumn<booksTable, String> title;
    @FXML TableColumn<booksTable, String> author;
    @FXML TableColumn<booksTable, String> type;
    @FXML TableView<booksTable> table1;
    @FXML TableColumn<booksTable, ImageView> image1;
    @FXML TableColumn<booksTable, String> title1;
    @FXML TableColumn<booksTable, String> author1;
    @FXML Label LabelId;
    @FXML TextField searchBox;

    public void back(ActionEvent event){
        new Functions().switchScenes(event, "LoginPage.fxml");
    }

    ObservableList<booksTable> items = FXCollections.observableArrayList();
    ObservableList<booksTable> items1 = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load_table();
        new Tooltip("");

        image.setCellValueFactory(new PropertyValueFactory<booksTable, ImageView>("image"));
        title.setCellValueFactory(new PropertyValueFactory<booksTable, String>("name"));
        author.setCellValueFactory(new PropertyValueFactory<booksTable, String>("author"));
        type.setCellValueFactory(new PropertyValueFactory<booksTable, String>("issuance"));

        image1.setCellValueFactory(new PropertyValueFactory<booksTable, ImageView>("image"));
        title1.setCellValueFactory(new PropertyValueFactory<booksTable, String>("name"));
        author1.setCellValueFactory(new PropertyValueFactory<booksTable, String>("author"));

        table.setItems(items);
        table1.setItems(items1);
    }

    public void load_table() {
        String txt = searchBox.getText();

        String stmt = (!txt.isEmpty())? "SELECT * FROM books WHERE name LIKE '%"+txt+"%' OR author LIKE '%"+txt+"%' OR issuance LIKE '%"+txt+"%';" :
                "SELECT * FROM books;";

        Iterator<String[]> dataIterator = DatabaseHandler.execute(stmt, "books").iterator();

        String stmt1 = String.format("SELECT * FROM books WHERE id IN (SELECT book_id FROM books_issued WHERE user_id = %d);", LoginPage.user_id);
        Iterator<String[]> dataIterator1 = DatabaseHandler.execute(stmt1, "books").iterator();

        String[] x, y;

        while (dataIterator.hasNext()) {
            x = dataIterator.next();

            ImageView i = new ImageView(x[1]);
            i.setFitHeight(100);
            i.setPreserveRatio(true);

            items.add(new booksTable(i, x[2], x[3], x[4], x[5]));
        }

        while (dataIterator1.hasNext()) {
            y = dataIterator1.next();

            ImageView i = new ImageView(y[1]);
            i.setFitHeight(100);
            i.setPreserveRatio(true);

            items1.add(new booksTable(i, y[2], y[3], y[4], y[5]));
        }
    }

    public void RequestIssue() throws ParseException {
        String name = table.getSelectionModel().getSelectedItem().getName();
        String author = table.getSelectionModel().getSelectedItem().getAuthor();
        String id = String.format("SELECT * FROM books WHERE name = '%s' AND author = '%s';", name, author);
        ArrayList<String []> StringBookId = DatabaseHandler.execute(id, "books");

        assert StringBookId != null;
        if(!StringBookId.get(0)[4].equals("old")) {
            String error;
            if(StringBookId.get(0)[4].equals("ref"))
                error = "The requested book is a reference book \nand cannot be issued";
            else {
                Date date = new Date();
                SimpleDateFormat frmt = new SimpleDateFormat("dd/MM/yyyy");
                float timeLeft = (date.getTime() - frmt.parse(StringBookId.get(0)[5]).getTime())/(3600f * 1000 * 24);
                error = "The requested book is not issuable for\n the next " + Math.round(60 - timeLeft) + " days!";
            }
            Functions.warningLabel(LabelId, error, "red");
            return;
        }

        int book_id = Integer.parseInt(StringBookId.get(0)[0]);

        String stmt = "SELECT * FROM books_issued;";
        Iterator<String[]> x = DatabaseHandler.execute(stmt, "books_issued").iterator();

        while (x.hasNext()) {
            String[] v = x.next();
            int bookId = Integer.parseInt(v[1]);

            if(bookId == book_id) {
                Functions.warningLabel(LabelId, "This book has been issued to someone else", "red");
                return;
            }
        }

        String stmt2 = "SELECT * FROM book_issuance_pending;";
        Iterator<String[]> y = DatabaseHandler.execute(stmt2, "book_issuance_pending").iterator();

        while (y.hasNext()) {
            int bookId2 = Integer.parseInt(y.next()[1]);
            if(bookId2 == book_id) {
                Functions.warningLabel(LabelId, "This book has already been requested for issuance", "red");
                return;
            }
        }

        String bookIssuance = String.format("INSERT INTO book_issuance_pending (user_id, book_id) VALUES(%d, %d);", LoginPage.user_id, book_id);
        DatabaseHandler.execute(bookIssuance, "book_issuance_pending");

        Functions.warningLabel(LabelId, "Request has been sent to the admin successfully!", "green");
    }

    public void returnBook(){
        String name = table1.getSelectionModel().getSelectedItem().getName();
        String author = table1.getSelectionModel().getSelectedItem().getAuthor();
        String id = String.format("SELECT * FROM books WHERE name = '%s' AND author = '%s';", name, author);
        ArrayList<String []> StringBookId = DatabaseHandler.execute(id, "books");
        int book_id = Integer.parseInt(StringBookId.get(0)[0]);

        // Delete the row from table books_issued based on the book_id
        DatabaseHandler.execute("DELETE FROM books_issued WHERE book_id = " + book_id + ";", "books_issued");
        items1.clear();
        load_table();
    }

    public void search(){
        items1.clear();
        items.clear();
        load_table();
    }
}