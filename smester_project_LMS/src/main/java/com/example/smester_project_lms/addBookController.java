package com.example.smester_project_lms;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class addBookController {
    @FXML TextField title;
    @FXML TextField author;
    @FXML RadioButton radioRef;

    public void add(){
        String countSameBooks = "SELECT * FROM books WHERE name = \'" + title.getText() + "\' AND author = \'" + author.getText() + "\';";
        ArrayList<String[]> x = DatabaseHandler.execute(countSameBooks, "books");

        if(title.getText().trim().isEmpty() || !x.isEmpty())
            return;

        String iss = radioRef.isSelected() ? "ref" : "new";
        String q, date, clipboard;

        Date d = new Date();
        date = String.valueOf(d.getDate() + "/" + (d.getMonth() + 1) + "/20" + d.getYear()%100);

        clipboard = Clipboard.getSystemClipboard().getString();

        if(clipboard == null && author.getText().trim().isEmpty())
            q = String.format("INSERT INTO books (name, issuance, dateField) values(\'%s\', \'%s\', \'%s\');", title.getText(), iss, date);
        else if(clipboard == null)
            q = String.format("INSERT INTO books (name, author, issuance, dateField) values(\'%s\', \'%s\', \'%s\', \'%s\');", title.getText(), author.getText(), iss, date);
        else if(author.getText().trim().isEmpty())
            q = String.format("INSERT INTO books (name, image, issuance, dateField) values(\'%s\', \'%s\', \'%s\', \'%s\');", title.getText(), clipboard, iss, date);
        else
            q = String.format("INSERT INTO books (name, image, author, issuance, dateField) values(\'%s\', \'%s\', \'%s\', \'%s\', \'%s\');", title.getText(), clipboard, author.getText(), iss, date);

        DatabaseHandler.execute(q, "books");
        ((Stage)radioRef.getScene().getWindow()).close();
    }

    public void addImage(){
        try {
            String bookName = title.getText().trim().replace(' ', '+') + "+book+cover";
            URI u = new URI("https://www.google.com/search?q="+ bookName +"&sxsrf=ALiCzsbwNATQEHExJKCN_WHPPnaZfGC1ww:1657528874629&source=lnms&tbm=isch&sa=X&ved=2ahUKEwjIrpueuPD4AhVN7rsIHbAABc4Q_AUoAXoECAIQAw&biw=1151&bih=505&dpr=1.19");
            Desktop.getDesktop().browse(u);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}