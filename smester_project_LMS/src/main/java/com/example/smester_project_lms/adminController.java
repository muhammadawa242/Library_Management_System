package com.example.smester_project_lms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class adminController implements Initializable {
    @FXML VBox vboxUsers;
    @FXML VBox vboxUsers1;
    @FXML TabPane userAccountTab;
    @FXML TableView<booksTable> table;
    @FXML TableColumn<booksTable, ImageView> image;
    @FXML TableColumn<booksTable, String> name;
    @FXML TableColumn<booksTable, String> author;
    @FXML TableColumn<booksTable, String> issuance;
    @FXML TableColumn<booksTable, String> dateField;
    @FXML HBox hb;
    @FXML VBox issuanceUsers;

    boolean SHOW = true;
    ObservableList<booksTable> items = FXCollections.observableArrayList();
    static boolean u, b;

    public void accountRequests() {
        Iterator<String[]> iteratorUserPending = DatabaseHandler.execute("SELECT * FROM users_pending;", "users_pending").iterator();
        vboxUsers.getChildren().clear();

        while (iteratorUserPending.hasNext()) {
            Label label = new Label("   " + iteratorUserPending.next()[0]);
            label.setPrefWidth(140);
            label.setPrefHeight(37);

            Button button = new Button("add");
            button.setPrefWidth(75);
            button.setPrefHeight(37);

            Button declineButt = new Button("decline");
            declineButt.setPrefWidth(97);
            declineButt.setPrefHeight(37);

            button.setOnMouseClicked(mouseEvent -> {
                String query1 = String.format("SELECT * FROM users_pending WHERE name = \'%s\';", label.getText().trim());
                ArrayList<String[]> x = DatabaseHandler.execute(query1, "users_pending");

                String query2 = String.format("DELETE FROM users_pending WHERE name = \'%s\';", label.getText().trim());
                DatabaseHandler.execute(query2, "users_pending");


                String query3 = String.format("INSERT INTO users (name, password) VALUES (\'%s\', \'%s\');", x.get(0)[0], x.get(0)[1]);
                DatabaseHandler.execute(query3, "users");

                accountRequests();
                allUsers();
            });

            declineButt.setOnMouseClicked(mouseEvent -> {
                String query = String.format("DELETE FROM users_pending WHERE name = \'%s\';", label.getText().trim());
                DatabaseHandler.execute(query, "users_pending");

                accountRequests();
            });

            vboxUsers.getChildren().addAll(new HBox(label, button, declineButt));
        }
    }

    public void allUsers() {
        Iterator<String[]> iteratorUserPending = DatabaseHandler.execute("SELECT * FROM users;", "users").iterator();
        vboxUsers1.getChildren().clear();

        while (iteratorUserPending.hasNext()) {
            Label label = new Label("   "+iteratorUserPending.next()[1]);
            label.setPrefWidth(175);
            label.setPrefHeight(37);

            Button button = new Button("remove");
            button.setPrefWidth(97);
            button.setPrefHeight(37);

            button.setOnMouseClicked(mouseEvent -> {
                String query = String.format("DELETE FROM users WHERE name = \'%s\';", label.getText().trim());
                DatabaseHandler.execute(query, "users");

                allUsers();
            });

            vboxUsers1.getChildren().addAll(new HBox(label, button));
        }
    }

    public void backToScene1(ActionEvent e) {
        new Functions().switchScenes(e, "LoginPage.fxml");
    }

    public void openCloseTab() {
        boolean usersTab = userAccountTab.getSelectionModel().isSelected(0);
        boolean booksTab = userAccountTab.getSelectionModel().isSelected(1);
        if (booksTab)
            bookIssueShow();

        if(SHOW){
            // Show tab
            userAccountTab.setPrefWidth(367);
            table.setPrefWidth(1003);
            SHOW = false;
        }
        else if(u && usersTab || b && booksTab){
            // Hide tab if previously selected tab is selected again
            userAccountTab.setPrefWidth(37);
            table.setPrefWidth(1300);
            SHOW = true;
        }
        u = usersTab;
        b = booksTab;
    }

    public void add_book() throws IOException {
        Clipboard.getSystemClipboard().setContent(null);
        Stage bookStage = new Stage();
        Scene scene = new Scene(new FXMLLoader(app.class.getResource("addBook.fxml")).load(), 300, 300);
        bookStage.setScene(scene);
        bookStage.setTitle("Add Book");
        bookStage.setResizable(false);
        bookStage.show();
    }

    public void bookIssueShow(){
        String stmt = "SELECT * FROM book_issuance_pending;";
        Stage prompt = new Stage();
        Iterator<String[]> x = DatabaseHandler.execute(stmt, "book_issuance_pending").iterator();

        issuanceUsers.getChildren().clear();

        while (x.hasNext()){
            String[] y = x.next();
            int user_id = Integer.parseInt(y[0]);
            int book_id = Integer.parseInt(y[1]);

            String a = String.format("SELECT * FROM users WHERE id =  %d;", user_id);
            ArrayList<String[]> c = DatabaseHandler.execute(a, "users");
            assert c != null;
            String name = c.get(0)[1];

            String b = String.format("SELECT * FROM books WHERE id =  %d;", book_id);
            ArrayList<String[]> d = DatabaseHandler.execute(b, "books");
            assert d != null;
            String book = d.get(0)[2];

            Label label = new Label("   " + name);
            label.setPrefWidth(120);
            label.setPrefHeight(37);

            Button button = new Button(book);
            Button issueBtn = new Button("Issue");
            Button declineBtn =  new Button("Decline");

            String insertStmt = String.format("INSERT INTO books_issued (user_id, book_id) VALUES(%d, %d);", user_id, book_id);
            String deleteStmt = String.format("DELETE FROM book_issuance_pending WHERE user_id = %d AND book_id = %d;", user_id, book_id);

            issueBtn.setOnMouseClicked(e-> {
                DatabaseHandler.execute(insertStmt, "books_issued");
                DatabaseHandler.execute(deleteStmt, "book_issuance_pending");
                bookIssueShow();
                prompt.close();
            });

            declineBtn.setOnMouseClicked(e-> {
                DatabaseHandler.execute(deleteStmt, "book_issuance_pending");
                prompt.close();
                bookIssueShow();
            });

            button.setOnMouseClicked(e->{
                VBox vbox =  new VBox(
                        new ImageView(d.get(0)[1]),
                        new Label("\nBook Id: " + d.get(0)[0]),
                        new Label("Book Title: " + d.get(0)[2]),
                        new Label("Author: " + d.get(0)[3]),
                        new Label("Book Type: " + d.get(0)[4]),
                        new Label("Date Added: " + d.get(0)[5]),
                        new Label("\n"),
                        new HBox(issueBtn, new Label("\t\t"), declineBtn)
                );

                vbox.setStyle("-fx-padding: 50");
                vbox.applyCss();
                prompt.setScene(new Scene(vbox, 370, 600));
                prompt.show();
            });

            issuanceUsers.getChildren().add(new HBox(label,button));
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.getSelectionModel().setCellSelectionEnabled(true);

        load_table();

        image.setCellValueFactory(new PropertyValueFactory<booksTable, ImageView>("image"));
        name.setCellValueFactory(new PropertyValueFactory<booksTable, String>("name"));
        author.setCellValueFactory(new PropertyValueFactory<booksTable, String>("author"));
        issuance.setCellValueFactory(new PropertyValueFactory<booksTable, String>("issuance"));
        dateField.setCellValueFactory(new PropertyValueFactory<booksTable, String>("dateField"));

        table.setItems(items);
    }

    public void load_table() {
        Iterator<String[]> dataIterator = DatabaseHandler.execute("SELECT * FROM books;", "books").iterator();
        String[] x;

        while (dataIterator.hasNext()) {
            x = dataIterator.next();

            ImageView i = new ImageView(x[1]);
            i.setFitHeight(100);
            i.setPreserveRatio(true);

            items.add(new booksTable(i, x[2], x[3], x[4], x[5]));
        }
    }

    public void refresh(){
        items.clear();
        load_table();
    }

    public void edit(){
        String name = table.getSelectionModel().getSelectedItem().getName();
        String author = table.getSelectionModel().getSelectedItem().getAuthor();
        String issuance = table.getSelectionModel().getSelectedItem().getIssuance();
        String dateField = table.getSelectionModel().getSelectedItem().getDateField();
        Button updateBtn = new Button("Update");

        Stage prompt = new Stage();

        String selectedColumn = table.getFocusModel().getFocusedCell().getTableColumn().getId();

        if(selectedColumn.equals("name") || selectedColumn.equals("author")) {
            VBox vbox = new VBox(new TextField(), updateBtn);
            prompt.setScene(new Scene(vbox));
            prompt.show();

            updateBtn.setOnMouseClicked(e->{
                String updatedData = ((TextField)vbox.getChildren().toArray()[0]).getText();
                String update = String.format("UPDATE books SET %s = \'%s\' WHERE name = \'%s\' AND author = \'%s\';", selectedColumn, updatedData, name, author);
                DatabaseHandler.execute(update, "books");
                refresh();
                prompt.close();
            });
        }
        else if(selectedColumn.equals("issuance")){
            RadioButton radBtn = new RadioButton("Reference Book");
            radBtn.styleProperty().setValue("-fx-padding: 20px");
            VBox vbox = new VBox(radBtn, updateBtn);

            if(issuance.equals("ref"))
                radBtn.setSelected(true);

            prompt.setScene(new Scene(vbox));
            prompt.show();

            updateBtn.setOnMouseClicked(e-> {

                String iss, update;

                try {
                    Date d = new SimpleDateFormat("dd/MM/yyyy").parse(dateField);
                    Date d2 = new Date();
                    float monthDiff = (d2.getTime() - d.getTime())/(float)(1000L*60*60*24*30);

                    if (radBtn.isSelected())
                        iss = "ref";
                    else if(monthDiff <= 2)
                        iss = "new";
                    else
                        iss = "old";

                    update = String.format("UPDATE books SET issuance = \'%s\' WHERE name = \'%s\' AND author = \'%s\';", iss, name, author);
                    DatabaseHandler.execute(update, "books");
                    refresh();
                }
                catch (ParseException ex) {
                    ex.printStackTrace();
                }

                prompt.close();
            });
        }
        else if(selectedColumn.equals("image")){
            try {
                Clipboard.getSystemClipboard().setContent(null);
                String bookName = name.trim().replace(' ', '+') + "+book+cover";
                URI u = new URI("https://www.google.com/search?q="+ bookName +"&sxsrf=ALiCzsbwNATQEHExJKCN_WHPPnaZfGC1ww:1657528874629&source=lnms&tbm=isch&sa=X&ved=2ahUKEwjIrpueuPD4AhVN7rsIHbAABc4Q_AUoAXoECAIQAw&biw=1151&bih=505&dpr=1.19");
                Desktop.getDesktop().browse(u);
                prompt.setScene(new Scene(updateBtn));
                prompt.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            updateBtn.setOnMouseClicked(e->{
                String clipboard = Clipboard.getSystemClipboard().getString();
                if(clipboard == null)
                    clipboard = "none.png";

                String update = String.format("UPDATE books SET image = \'%s\' WHERE name = \'%s\' AND author = \'%s\';", clipboard, name, author);
                DatabaseHandler.execute(update, "books");
                refresh();
                prompt.close();
            });
        }
    }

    public void delete(){
        String name = table.getSelectionModel().getSelectedItem().getName();
        String author = table.getSelectionModel().getSelectedItem().getAuthor();
        String deleteQuery = String.format("DELETE FROM books WHERE name = \'%s\' AND author = \'%s\';",name, author);

        DatabaseHandler.execute(deleteQuery, "books");
        refresh();
    }
}