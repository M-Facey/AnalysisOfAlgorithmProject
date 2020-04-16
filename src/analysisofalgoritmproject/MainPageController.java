package analysisofalgoritmproject;

import com.jfoenix.controls.JFXRippler;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainPageController implements Initializable {
    
    // -- the variables 
    @FXML private AnchorPane scrollpane, root;
    @FXML private ImageView plusIcon, close, mini, next;
    @FXML private TextField name, cost, profit, quantity;
    @FXML private final Stack<AnchorPane> itemStack = new Stack<>();
    @FXML private Label title;
    @FXML private Button add;
    
    public static ArrayList<Float> profitList = new ArrayList<>();
    public static ArrayList<Float> costList = new ArrayList<>();
    public static ArrayList<Integer> quantityList = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> constraintLeft = new ArrayList<>();
    public static ArrayList<String> nameList = new ArrayList<>();
    
    
    public void getValues() {
        boolean flag = false;
        int point = 0, size = itemStack.size();
        while(flag != true) {
            ArrayList<Float> left = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                if(i == point)
                    left.add(1.0f);
                else
                    left.add(0.0f);
            }
            constraintLeft.add(left);
            point++;
            if(point == size)
                flag = true;
        }
    }
    
    @FXML
    public void goToOtherPage(ActionEvent event) {
        if(!itemStack.isEmpty()) {
            getValues();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OtherPage.fxml"));
            Parent parent;
            try {
                parent = loader.load();
                Scene scene = new Scene(parent);
                scene.setFill(Color.TRANSPARENT);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
            }  
        }
    }
    
    public boolean validateInputs() {
        boolean flag = true;
        
        try {
            Float.parseFloat(cost.getText());
        } catch(NumberFormatException ex) {
            cost.setText("");
            flag = false;
        }
        try {
            Float.parseFloat(profit.getText());
        } catch(NumberFormatException ex) {
            profit.setText("");
            flag = false;
        }
        
        try {
            Integer.parseInt(quantity.getText());
        } catch(NumberFormatException ex) {
            quantity.setText("");
            flag = false;
        }
        return flag;
    }
    
    public Label addField(String name, double x, double y) {
        Label field = new Label(name);
        field.setPrefSize(147.0, 26.0);
        field.setLayoutX(x);
        field.setLayoutY(y);
        //field.setFont(Font.font("System", 17.0));
        field.setFont(Font.loadFont(getClass().getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 17.0));
        field.setPadding(new Insets(0, 0, 0, 5.0));
        field.setStyle("-fx-background-color: #576CA8;");
        return field;
    }
    
    public void addItemToQueue() {
        if(validateInputs()) {
            AnchorPane container = new AnchorPane();
            container.setPrefSize(414.0, 90.0);
            container.setLayoutX(24);
            if(itemStack.isEmpty()) container.setLayoutY(14);
            else container.setLayoutY(itemStack.peek().getLayoutY() + 104.0);

            container.setStyle("-fx-background-color: rgba(48, 43, 39, 0.74); -fx-background-radius: 10px;");

            // -- these are the labels
            Label index = new Label();
            index.setFont(Font.font("System", FontWeight.BOLD, 24.0));
            index.setAlignment(Pos.CENTER);
            index.setPrefSize(40.0, 40.0);
            index.setLayoutX(-20.0);
            index.setLayoutY(25.0);
            index.setStyle("-fx-background-color: #F5F3F5; -fx-background-radius: 100; -fx-text-fill: #1B264F;");
            container.getChildren().add(index);

            container.getChildren().add(addField(name.getText(), 31.0, 15.0));
            container.getChildren().add(addField(quantity.getText(), 31.0, 49.0));
            container.getChildren().add(addField(cost.getText(), 191.0, 15.0));
            container.getChildren().add(addField(profit.getText(), 191.0, 49.0));
            
            nameList.add(name.getText());
            costList.add(Float.parseFloat(cost.getText()));
            quantityList.add(Integer.parseInt(quantity.getText()));
            profitList.add(Float.parseFloat(profit.getText()));

            // -- this is that one button
            Button delete = new Button();
            delete.setText("X");
            delete.setFont(Font.font("System", FontWeight.BOLD, 24.0));
            delete.setTextFill(Paint.valueOf("white"));
            delete.setTextAlignment(TextAlignment.CENTER);
            delete.setPrefSize(67.0, 90.0);
            delete.setLayoutX(348.0);
            delete.setLayoutY(0.0);
            delete.setStyle("-fx-background-color: #F5F3F5; -fx-background-radius: 0 10 10 0; -fx-text-fill: #1B264F;");

            delete.setOnMouseEntered(event -> {
                delete.setStyle("-fx-background-color: #c20000; -fx-background-radius: 0 10 10 0;");
            });

            delete.setOnMouseClicked(event -> {
                int i = itemStack.indexOf(container);
                nameList.remove(i);
                quantityList.remove(i);
                profitList.remove(i);
                costList.remove(i);
                rearrange(container);
                scrollpane.getChildren().remove(scrollpane.getChildren().lastIndexOf(container));
                itemStack.remove(container);
            });

            delete.setOnMouseExited(event -> {
                delete.setStyle("-fx-background-color: #F5F3F5; -fx-background-radius: 0 10 10 0; -fx-text-fill: #1B264F;");
            });
            container.getChildren().add(delete);

            // -- adding the container to both the scroll pane and stack
            itemStack.push(container);
            index.setText(String.valueOf(itemStack.indexOf(container) + 1));
            scrollpane.getChildren().add(container);
            clearInputs();
        }
    }
    
    public void rearrange(AnchorPane pane) {
        if(!itemStack.peek().equals(pane)){
            itemStack.forEach(anchor -> {
                double tempY = pane.getLayoutY();
                Label l = (Label) anchor.getChildren().get(0);
                
                if(itemStack.indexOf(anchor) == 0) l.setText(String.valueOf(itemStack.indexOf(anchor) + 1));
                else l.setText(String.valueOf(itemStack.indexOf(anchor)));
                
                if(anchor.getLayoutY() >= tempY) anchor.setLayoutY(anchor.getLayoutY() - 104.0);
            });
        }
    }
    
    public void clearInputs() {
        name.setText(""); cost.setText(""); profit.setText(""); quantity.setText("");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        JFXRippler rippler = new JFXRippler();
        rippler.setPrefSize(78.0, 78.0);
        rippler.setLayoutX(409.0); rippler.setLayoutY(190.0);
        rippler.setStyle("-fx-background-radius: 100;");
        rippler.setControl(add);
        rippler.setMaskType(JFXRippler.RipplerMask.CIRCLE);
        rippler.setRipplerFill(Paint.valueOf("red"));
        
        root.getChildren().add(rippler);
        title.setFont(Font.loadFont(getClass().getResource("/fonts/RobotoSlab-Bold.ttf").toExternalForm(), 64.0));
        plusIcon.setImage(new Image("/resources/plus.png"));
        close.setImage(new Image("/resources/close_grey.png", close.getFitWidth() + 20, close.getFitHeight() + 20, false, true));
        mini.setImage(new Image("/resources/mini_grey.png", mini.getFitWidth(), mini.getFitHeight(), false, true));
        next.setImage(new Image("/resources/next1.png"));
        
        
        close.setOnMouseEntered(event -> { close.setImage(new Image("/resources/close_color.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        close.setOnMouseClicked(event -> { Platform.exit(); });
        close.setOnMouseExited(event -> { close.setImage(new Image("/resources/close_grey.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        
        mini.setOnMouseEntered(event -> { mini.setImage(new Image("/resources/mini_color.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
        mini.setOnMouseClicked(event -> { ((Stage)((ImageView)event.getSource()).getScene().getWindow()).setIconified(true); });
        mini.setOnMouseExited(event -> { mini.setImage(new Image("/resources/mini_grey.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
    }    
}
