package analysisofalgoritmproject;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.ByteArrayOutputStream;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.python.util.*;

public class ResultsPageController implements Initializable {

    @FXML private Label text, totalProfit, totalCost, title;
    
    @FXML AnchorPane scrollpane, root;
    @FXML private ImageView close, mini;
    private double xOffset = 0;
    private double yOffset = 0;
    
   
    public AnchorPane createAnchorPane(String itemName, String quantity, Double yPos) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(363.0, 72.0);
        pane.setLayoutX(14.0);
        pane.setLayoutY(yPos);
        pane.setStyle("-fx-background-color: green; -fx-background-radius: 10;");
        
        Label item = new Label(itemName);
        item.setTextFill(Paint.valueOf("white"));
        item.setFont(Font.font("System", 24.0));
        item.setLayoutX(7.0);
        item.setLayoutY(13.0);
        
        Label quan = new Label(quantity);
        quan.setFont(Font.font("System", 24.0));
        quan.setTextFill(Paint.valueOf("green"));
        quan.setPrefSize(202.0, 47.0);
        quan.setLayoutX(150.0);
        quan.setLayoutY(12.0);
        quan.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        quan.setAlignment(Pos.CENTER);
        
        pane.getChildren().add(item);
        pane.getChildren().add(quan);
        
        return pane;
    }
    
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        close.setOnMouseEntered(event -> { close.setImage(new Image("/resources/close_color.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        close.setOnMouseClicked(event -> { Platform.exit(); });
        close.setOnMouseExited(event -> { close.setImage(new Image("/resources/close_grey.png", close.getFitWidth(), close.getFitHeight(), false, true)); });
        
        mini.setOnMouseEntered(event -> { mini.setImage(new Image("/resources/mini_color.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
        mini.setOnMouseClicked(event -> { ((Stage)((ImageView)event.getSource()).getScene().getWindow()).setIconified(true); });
        mini.setOnMouseExited(event -> { mini.setImage(new Image("/resources/mini_grey.png", mini.getFitWidth(), mini.getFitHeight(), false, true)); });
        
        title.setFont(Font.loadFont(getClass().getResource("/fonts/RobotoSlab-Bold.ttf").toExternalForm(), 64.0));
        
        PythonInterpreter interp = new PythonInterpreter();
        interp.setOut(bout);
        String A = "   A = ";
        int size = MainPageController.constraintLeft.size();
        int size2 = MainPageController.nameList.size();
        int i, j;
        for(i = 0; i < size; ++i) {
            for(j = 0; j < size2; ++j) {
                if(j == 0 && i == 0) A+= "[[";
                else if(j == 0) A += "[";
                A += MainPageController.constraintLeft.get(i).get(j);
                if(j == size2 - 1 && i == size - 1) A += "]]";
                else if(j == size2 - 1) A += "], ";
                else A+=", ";
            }
        }
        A += "\n";
        
        String additional_A = "";
        for(i = 0; i < size + 1; i++) {
            if(i != size) additional_A += "   A[" + i + "] += ";
            else additional_A += "   c += ";
            
            for(j = 0; j < size; j++) {
                if(j == 0) additional_A += "[";
                
                if(i != size) {
                    if(j == i && i != 0) additional_A += "1";
                    else additional_A += "0";
                } else additional_A += "0";
                
                if(j == size - 1) additional_A += "]\n";
                else additional_A += ", "; 
            }
        }
      
        String b = "   b = [", c = "   c = [";
        for(i = 0; i < size; i++) {
            b += MainPageController.quantityList.get(i);
            if(i == size - 1) b += "]\n";
            else b += ", ";
        }
        
        for(i = 0; i < size2; i++) {
            c += MainPageController.profitList.get(i);
            if(i == size2 - 1) c += "]\n";
            else c += ", ";
        }
        
        String end = b + A + c + additional_A;
        end += "   t, s, v = simplex(c, A, b)\n" +
        "   print(s)\n" +
        "   item = ''\n" +
        "   quan = ''\n" +
        "   for i in s:\n" +
        "      item += str(i[0]) + ' '\n" +
        "      quan += str(i[1]) + ' '\n" +
        "   print(v)";

                //System.out.println(c +" " + b);
                String py = "import heapq\n" +
        "\n" +
        "\n" +
        "'''\n" +
        "   Return a rectangular identity matrix with the specified diagonal entiries, possibly\n" +
        "   starting in the middle.\n" +
        "'''\n" +
        "def identity(numRows, numCols, val=1, rowStart=0):\n" +
        "   return [[(val if i == j else 0) for j in range(numCols)]\n" +
        "               for i in range(rowStart, numRows)]\n" +
        "\n" +
        "\n" +
        "'''\n" +
        "   standardForm: [float], [[float]], [float], [[float]], [float], [[float]], [float] -> [float], [[float]], [float]\n" +
        "   Convert a linear program in general form to the standard form for the\n" +
        "   simplex algorithm.  The inputs are assumed to have the correct dimensions: cost\n" +
        "   is a length n list, greaterThans is an n-by-m matrix, gtThreshold is a vector\n" +
        "   of length m, with the same pattern holding for the remaining inputs. No\n" +
        "   dimension errors are caught, and we assume there are no unrestricted variables.\n" +
        "'''\n" +
        "def standardForm(cost, greaterThans=[], gtThreshold=[], lessThans=[], ltThreshold=[],\n" +
        "                equalities=[], eqThreshold=[], maximization=True):\n" +
        "   newVars = 0\n" +
        "   numRows = 0\n" +
        "   if gtThreshold != []:\n" +
        "      newVars += len(gtThreshold)\n" +
        "      numRows += len(gtThreshold)\n" +
        "   if ltThreshold != []:\n" +
        "      newVars += len(ltThreshold)\n" +
        "      numRows += len(ltThreshold)\n" +
        "   if eqThreshold != []:\n" +
        "      numRows += len(eqThreshold)\n" +
        "\n" +
        "   if not maximization:\n" +
        "      cost = [-x for x in cost]\n" +
        "\n" +
        "   if newVars == 0:\n" +
        "      return cost, equalities, eqThreshold\n" +
        "\n" +
        "   newCost = list(cost) + [0] * newVars\n" +
        "\n" +
        "   constraints = []\n" +
        "   threshold = []\n" +
        "\n" +
        "   oldConstraints = [(greaterThans, gtThreshold, -1), (lessThans, ltThreshold, 1),\n" +
        "                     (equalities, eqThreshold, 0)]\n" +
        "\n" +
        "   offset = 0\n" +
        "   for constraintList, oldThreshold, coefficient in oldConstraints:\n" +
        "      constraints += [c + r for c, r in zip(constraintList,\n" +
        "         identity(numRows, newVars, coefficient, offset))]\n" +
        "\n" +
        "      threshold += oldThreshold\n" +
        "      offset += len(oldThreshold)\n" +
        "\n" +
        "   return newCost, constraints, threshold\n" +
        "\n" +
        "\n" +
        "def dot(a,b):\n" +
        "   return sum(x*y for x,y in zip(a,b))\n" +
        "\n" +
        "def column(A, j):\n" +
        "   return [row[j] for row in A]\n" +
        "\n" +
        "def transpose(A):\n" +
        "   return [column(A, j) for j in range(len(A[0]))]\n" +
        "\n" +
        "def isPivotCol(col):\n" +
        "   return (len([c for c in col if c == 0]) == len(col) - 1) and sum(col) == 1\n" +
        "\n" +
        "def variableValueForPivotColumn(tableau, column):\n" +
        "   pivotRow = [i for (i, x) in enumerate(column) if x == 1][0]\n" +
        "   return tableau[pivotRow][-1]\n" +
        "\n" +
        "# assume the last m columns of A are the slack variables; the initial basis is\n" +
        "# the set of slack variables\n" +
        "def initialTableau(c, A, b):\n" +
        "   tableau = [row[:] + [x] for row, x in zip(A, b)]\n" +
        "   tableau.append([ci for ci in c] + [0])\n" +
        "   return tableau\n" +
        "\n" +
        "\n" +
        "def primalSolution(tableau):\n" +
        "   # the pivot columns denote which variables are used\n" +
        "   columns = transpose(tableau)\n" +
        "   indices = [j for j, col in enumerate(columns[:-1]) if isPivotCol(col)]\n" +
        "   return [(colIndex, variableValueForPivotColumn(tableau, columns[colIndex]))\n" +
        "            for colIndex in indices]\n" +
        "\n" +
        "\n" +
        "def objectiveValue(tableau):\n" +
        "   return -(tableau[-1][-1])\n" +
        "\n" +
        "\n" +
        "def canImprove(tableau):\n" +
        "   lastRow = tableau[-1]\n" +
        "   return any(x > 0 for x in lastRow[:-1])\n" +
        "\n" +
        "\n" +
        "# this can be slightly faster\n" +
        "def moreThanOneMin(L):\n" +
        "   if len(L) <= 1:\n" +
        "      return False\n" +
        "\n" +
        "   x,y = heapq.nsmallest(2, L, key=lambda x: x[1])\n" +
        "   return x == y\n" +
        "\n" +
        "\n" +
        "def findPivotIndex(tableau):\n" +
        "   # pick minimum positive index of the last row\n" +
        "   column_choices = [(i,x) for (i,x) in enumerate(tableau[-1][:-1]) if x > 0]\n" +
        "   column = min(column_choices, key=lambda a: a[1])[0]\n" +
        "\n" +
        "   # check if unbounded\n" +
        "   if all(row[column] <= 0 for row in tableau):\n" +
        "      raise Exception('Linear program is unbounded.')\n" +
        "\n" +
        "   # check for degeneracy: more than one minimizer of the quotient\n" +
        "   quotients = [(i, r[-1] / r[column])\n" +
        "      for i,r in enumerate(tableau[:-1]) if r[column] > 0]\n" +
        "\n" +
        "   if moreThanOneMin(quotients):\n" +
        "      raise Exception('Linear program is degenerate.')\n" +
        "\n" +
        "   # pick row index minimizing the quotient\n" +
        "   row = min(quotients, key=lambda x: x[1])[0]\n" +
        "\n" +
        "   return row, column\n" +
        "\n" +
        "\n" +
        "def pivotAbout(tableau, pivot):\n" +
        "   i,j = pivot\n" +
        "\n" +
        "   pivotDenom = tableau[i][j]\n" +
        "   tableau[i] = [x / pivotDenom for x in tableau[i]]\n" +
        "\n" +
        "   for k,row in enumerate(tableau):\n" +
        "      if k != i:\n" +
        "         pivotRowMultiple = [y * tableau[k][j] for y in tableau[i]]\n" +
        "         tableau[k] = [x - y for x,y in zip(tableau[k], pivotRowMultiple)]\n" +
        "\n" +
        "\n" +
        "'''\n" +
        "   simplex: [float], [[float]], [float] -> [float], float\n" +
        "   Solve the given standard-form linear program:\n" +
        "      max <c,x>\n" +
        "      s.t. Ax = b\n" +
        "           x >= 0\n" +
        "   providing the optimal solution x* and the value of the objective function\n" +
        "'''\n" +
        "def simplex(c, A, b):\n" +
        "   tableau = initialTableau(c, A, b)\n" +
        "   print(\"Initial tableau:\")\n" +
        "   for row in tableau:\n" +
        "      print(row)\n" +
        "   print()\n" +
        "\n" +
        "   while canImprove(tableau):\n" +
        "      pivot = findPivotIndex(tableau)\n" +
        "      print(\"Next pivot index is=%d,%d \\n\" % pivot)\n" +
        "      pivotAbout(tableau, pivot)\n" +
        "      print(\"Tableau after pivot:\")\n" +
        "      for row in tableau:\n" +
        "         print(row)\n" +
        "      print()\n" +
        "\n" +
        "   return tableau, primalSolution(tableau), objectiveValue(tableau)\nif __name__ == \"__main__\":\n" +
        "\n";
        py += end;
        interp.exec(py);
        text.setText(bout.toString());
        
        String[] item = interp.get("item").asString().split(" ");
        String[] quan = interp.get("quan").asString().split(" ");
        Double profit = interp.get("v").asDouble();
        double cost = 0.0;
        
        for(i = 0; i < size2; ++i) {
            //System.out.println(MainPageController.nameList.get(Integer.parseInt(item[i])) + "\t Quantity: " + quan[i] + "\nTotal Cost: " + String.valueOf(Double.parseDouble(quan[i]) * MainPageController.costList.get(i)) + "\n");
            cost += Double.parseDouble(quan[i]) * MainPageController.costList.get(i);      
        }
        
        totalProfit.setText(String.valueOf(profit));
        totalCost.setText(String.valueOf(cost));
        
        double yPos = 63;
        for(i = 0; i < size2; ++i) {
            scrollpane.getChildren().add(createAnchorPane(MainPageController.nameList.get(Integer.parseInt(item[i])), quan[i], yPos));
            yPos += 86.0;
        }
    }    
}
