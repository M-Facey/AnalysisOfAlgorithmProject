package analysisofalgoritmproject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OtherPageController implements Initializable {

    @FXML private AnchorPane scrollpane, root;
    @FXML private ImageView close, mini;
    @FXML Stack<AnchorPane> itemStack = new Stack<>();
    @FXML Label title;
    public static String result = "";
    
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    public TextField addField(double w, double h, double x) {
        TextField field = new TextField();
        field.setFont(Font.font("System", 18.0));
        field.setPrefSize(w, h);
        field.setLayoutX(x);
        field.setLayoutY(25);
        return field;
    }
    
    public void addConstraint() {
        AnchorPane container = new AnchorPane();
        container.setPrefSize(434.0, 90.0);
        container.setLayoutX(14);
        if(itemStack.isEmpty())
            container.setLayoutY(14);
        else {
            container.setLayoutY(itemStack.peek().getLayoutY() + 104.0);
        }
        container.setStyle("-fx-background-color: rgba(48, 43, 39, 0.74); -fx-background-radius: 10px;");
        
        container.getChildren().add(addField(216, 39, 14));
        container.getChildren().add(addField(101, 39, 273));
        
        Label signs = new Label("<=");
        signs.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        signs.setLayoutX(237);
        signs.setLayoutY(27);
        container.getChildren().add(signs);
        
        Button delete = new Button("X");
        delete.setFont(Font.font("System", FontWeight.BOLD, 18.0));
        delete.setTextFill(Paint.valueOf("rgb(206,0,31)"));
        delete.setPrefSize(44, 44);
        delete.setLayoutX(383);
        delete.setLayoutY(23);
        delete.setStyle("-fx-background-color: white; -fx-background-radius: 100;");
        delete.setOnMouseClicked(event -> {
            rearrange(container);
            scrollpane.getChildren().remove(scrollpane.getChildren().lastIndexOf(container));
            itemStack.remove(container);
        });
        container.getChildren().add(delete);
        
        // -- adding the container to both the scroll pane and stack
        itemStack.push(container);
        scrollpane.getChildren().add(container);
    }
    
    public void rearrange(AnchorPane pane) {
        if(!itemStack.peek().equals(pane)){
            itemStack.forEach(anchor -> {
                double tempY = pane.getLayoutY();
                if(anchor.getLayoutY() >= tempY) {
                    anchor.setLayoutY(anchor.getLayoutY() - 104.0);
                }
            });
        }
    }
    
    public void printResult() {
        itemStack.forEach(pane ->{
            TextField constraintRight = (TextField) pane.getChildren().get(1);
            MainPageController.quantityList.add(Integer.parseInt(constraintRight.getText()));
            
            TextField constraintLeft = (TextField) pane.getChildren().get(0);
            
            // ASSUMES THAT YOU ONLY ADD AND SUBTRACT CONSTRAINTS
            String cLeft = constraintLeft.getText();
            String operator = null;
            
            String[] sections = null;
            int realSize = itemStack.size();
            ArrayList<Float> left = new ArrayList<>();
            ArrayList<Integer> orderOfOperations = new ArrayList<>();
            int size = MainPageController.nameList.size(), i, cSize = cLeft.length();
            boolean first = true;
            
            for(i = 0; i < cSize; i++) {
                if(cLeft.charAt(i) == '+')
                    orderOfOperations.add(0);
                else if(cLeft.charAt(i) == '-')
                    orderOfOperations.add(1);
            }
            int oCounter = 0;
            
            cLeft = cLeft.replaceAll("\\+", "?").replaceAll("-", "?");
            System.out.println(cLeft);
            sections = cLeft.split(" \\? ");
            
            for(int k = 0; k < size; k++) {
                left.add(0.0f);
            }
            
            for(String s: sections) {
                
                String[] item = s.split(" ");
                
                if(item.length > 3) { 
                    for(int k = 2; k < item.length; ++k) {
                        item[1] +=  " " + item[k];
                    }
                }
                
                if(MainPageController.nameList.contains(item[1])) {
                    i = MainPageController.nameList.indexOf(item[1]);
                    if(orderOfOperations.get(oCounter) == 0 || first)
                        left.set(i, Float.parseFloat(item[0]) + left.get(i));
                    else if(orderOfOperations.get(oCounter) == 1 && !first)
                        left.set(i, Float.parseFloat(item[0]) * -1.0f + left.get(i));
                }
                if(!first)
                    oCounter++;
                first = false;
            }
            MainPageController.constraintLeft.add(left);
            
        });
    }
    
    public void doSomething(ActionEvent event) {
        printResult();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultsPage.fxml"));
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainPageController.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage)((AnchorPane)event.getSource()).getScene().getWindow()).setX(event.getScreenX() - xOffset);
                ((Stage)((AnchorPane)event.getSource()).getScene().getWindow()).setY(event.getScreenY() - yOffset);
            }
        });
        
        close.setImage(new Image("/resources/close_grey.png", close.getFitWidth(), close.getFitHeight(), false, true));
        mini.setImage(new Image("/resources/mini_grey.png", mini.getFitWidth(), mini.getFitHeight(), false, true));
        title.setFont(Font.loadFont(getClass().getResource("/fonts/RobotoSlab-Bold.ttf").toExternalForm(), 64.0));
        
        close.setOnMouseEntered(event -> { close.setImage(new Image("/resources/close_color.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        close.setOnMouseClicked(event -> { Platform.exit(); });
        close.setOnMouseExited(event -> { close.setImage(new Image("/resources/close_grey.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        
        mini.setOnMouseEntered(event -> { mini.setImage(new Image("/resources/mini_color.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
        mini.setOnMouseClicked(event -> { ((Stage)((ImageView)event.getSource()).getScene().getWindow()).setIconified(true); });
        mini.setOnMouseExited(event -> { mini.setImage(new Image("/resources/mini_grey.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
    }    
    
}
