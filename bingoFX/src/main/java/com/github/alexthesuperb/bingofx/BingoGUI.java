package com.github.alexthesuperb.bingofx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class BingoGUI extends Application {

   /** This card's values */
   private int[][] bingoVals;

   /** A pointer to the buttons */
   private Button[][] bingoSquares;

   /** The text field displaying the last drawn number. */
   private Text currNumberDisplay;

   /** Blank by default. Can display either "You Won!" or "Game Over". */
   private Text gameStateField;

   /** Randomized set of numbers 1-75. One is drawn on each play. */
   private List<Integer> numberBalls;

   /** Held for restarts. */
   private Stage primaryStage;

   /** False by default. Checked every time a square is highlighted. */
   private boolean userHasBingo;

   /** Numbers removed from numberBalls. These numbers have been played. */
   private List<Integer> drawnNumbers;

   private int numBallsDrawn;

   public static void main(String[] args) {
       launch();
   }

   /**
    * <b>Note</b>: the element <code>generateCardVals()[2][2]</code> <i>always</i>
    * has a value of <code>-1</code>. This index represents the free space in the 
    * center of a Bingo card.
    * @return a randomly-generated 5-by-5 bingo card.
    */
   private int[][] generateCardVals() {
       int[] bNums = getRandomSet(5, 1, 15);
       int[] iNums = getRandomSet(5, 16, 30);
       int[] nNums = getRandomSet(5, 31, 45);
       int[] gNums = getRandomSet(5, 46, 60);
       int[] oNums = getRandomSet(5, 61, 75);

       int[][] vals = { bNums, iNums, nNums, gNums, oNums };
       vals[2][2] = -1; //Center number is a free space

       return vals;
   }

   /**
    * 
    * @param n the number on a single Bingo card square.
    * @return <code>'B'</code> for <code>1 <= n <= 15</code>, 
    *         <code>'I'</code> for <code>16 <= n <= 30</code>, etc.
    */
   private char getBingoColumnChar(int n) {
       if (n >= 1 && n <= 15) {
           return 'B'; 
       }
       if (n >= 16 && n <= 30) {
           return 'I';
       }
       if (n >= 31 && n <= 45) {
           return 'N';
       }
       if (n >= 46 && n <= 60) {
           return 'G';
       }
       if (n >= 61 && n <= 75) {
           return 'O';
       }
       /* Should never fall through... */
       return ' ';
   }

   /**
    * 
    * @param n The number of elements in the set.
    * @param a Left bound of set (inclusive).
    * @param b Right bound of set (inclusive).
    * @return An array of <code>n</code> random numbers within bounds <code>(a, b)</code>.
    */
   private int[] getRandomSet(int n, int a, int b) {
       if (n > (b - a + 1)) {
           return new int[n];
       }
       LinkedList<Integer> lst = new LinkedList<Integer>();
       
       for (int i = a; i <= b; i++) {
           lst.add(i);
       }
       Collections.shuffle(lst);

       int[] arr = new int[n];
       for (int i = 0; i < n; i++) {
           arr[i] = lst.get(i);
       }
       return arr;
   }

   /**
    * Check a single row or column for Bingo.
    * @param row <code>true</code> to check row, <code>false</code> to check column.
    * @param n The index of the row/column to check.
    * @param vals The <code>List</code> of called numbers to check against.
    * @param arr The card from which the row/column originates.
    * @return <code>true</code> if <code>vals</code> contains every value in <code>arr[n][..]</code>
    * or <code>arr[..][n]</code>, where the dimension checked if determined by <code>row</code>. 
    * If either <code>vals</code> or <code>arr</code> is <code>null</code>, <code>false</code> will
    * automatically be returned.
    */
   private boolean checkSingleForBingo(boolean row, int n, List<Integer> vals, int[][] arr) {
       if (arr == null || vals == null) {
           return false;
       }

       if (row) {
           if (vals.contains(arr[n][0]) && 
                   vals.contains(arr[n][1]) &&
                   vals.contains(arr[n][2]) &&
                   vals.contains(arr[n][3]) &&
                   vals.contains(arr[n][4])) {
               return true;
           }
           return false;
       } else {
           if (vals.contains(arr[0][n]) && 
                   vals.contains(arr[1][n]) &&
                   vals.contains(arr[2][n]) &&
                   vals.contains(arr[3][n]) &&
                   vals.contains(arr[4][n])) {
               return true;
           }
           return false;
       }
   }

   /**
    * Check if user has a bingo. 
    * @return <code>true</code> if Bingo is achieved across, down, or diagonal, or <code>false</code>
    * otherwise.
    */
   private boolean checkForUserBingo() {
       /* 
        * Since the center of the grid is a free space, add that value to 
        * the list of drawn numbers, regardless of what it is. 
        */
       if (!drawnNumbers.contains(bingoVals[2][2])) {
           drawnNumbers.add(bingoVals[2][2]);
       }
       
       //Row 1 across
       if (checkSingleForBingo(true, 0, drawnNumbers, bingoVals)) {
           return true;
       }

       //Row 2 across
       if (checkSingleForBingo(true, 1, drawnNumbers, bingoVals)) {
           return true;
       }

       //Row 3 across
       if (checkSingleForBingo(true, 2, drawnNumbers, bingoVals)) {
           return true;
       }

       //Row 4 across
       if (checkSingleForBingo(true, 3, drawnNumbers, bingoVals)) {
           return true;
       }

       //Row 5 across
       if (checkSingleForBingo(true, 4, drawnNumbers, bingoVals)) {
           return true;
       }

       //Column B across
       if (checkSingleForBingo(false, 0, drawnNumbers, bingoVals)) {
           return true;
       }

       //Column I across
       if (checkSingleForBingo(false, 1, drawnNumbers, bingoVals)) {
           return true;
       }

       //Column N across
       if (checkSingleForBingo(false, 2, drawnNumbers, bingoVals)) {
           return true;
       }

       //Column G across
       if (checkSingleForBingo(false, 3, drawnNumbers, bingoVals)) {
           return true;
       }

       //Column O across
       if (checkSingleForBingo(false, 4, drawnNumbers, bingoVals)) {
           return true;
       }

       //Diagonal top left (0,0) to bottom right (5,5)
       if (drawnNumbers.contains(bingoVals[0][0]) &&
               drawnNumbers.contains(bingoVals[1][1]) &&
               drawnNumbers.contains(bingoVals[2][2]) &&
               drawnNumbers.contains(bingoVals[3][3]) &&
               drawnNumbers.contains(bingoVals[4][4])){
           return true;
       } 

       //Diagonal buttom left to top right
       if (drawnNumbers.contains(bingoVals[4][0]) &&
               drawnNumbers.contains(bingoVals[3][1]) &&
               drawnNumbers.contains(bingoVals[2][2]) &&
               drawnNumbers.contains(bingoVals[1][3]) &&
               drawnNumbers.contains(bingoVals[0][4])){
           return true;
       }

       return false;
   }

   @Override
   public void start(Stage primaryStage) throws Exception {
       this.primaryStage = primaryStage;
       resetGUI(primaryStage);
   }

   /**
    * Reset JavaFX GUI and game states. This method is called whenever the user
    * clicks 'New Game' or when <code>start(Stage primaryStage)</code> is called.
    * @param primaryStage the JavaFX primary stage container.
    */
   protected void resetGUI(Stage primaryStage) {
       userHasBingo = false;
       drawnNumbers = new LinkedList<Integer>();

       bingoVals = generateCardVals();
       numberBalls = new LinkedList<Integer>();
       for (int i : getRandomSet(75, 1, 75)) {
           numberBalls.add(i);
       }

       primaryStage.setTitle("BingoFX");

       BorderPane borderPane = new BorderPane();

       HBox currNumberBar = createTopPanel();
       GridPane bingoCard = createCenterPanel();
       HBox footer = createBottomPanel();

       borderPane.setTop(currNumberBar);
       borderPane.setCenter(bingoCard);
       borderPane.setBottom(footer);

       Scene scene = new Scene(borderPane);
       primaryStage.setScene(scene);

       primaryStage.setResizable(false);
       primaryStage.show();
   }

   private HBox createTopPanel() {
       HBox hbox = new HBox();

       hbox.setPadding(new Insets(15, 12, 15, 12));
       hbox.setSpacing(10);
       hbox.setStyle("-fx-background-color: #336699;");

       int n = numberBalls.remove(0);
       char c = getBingoColumnChar(n);
       numBallsDrawn = 1; //Default 1 since 1st number comes pre-displayed when game reset

       currNumberDisplay = new Text(String.format("%c%d", c, n));
       currNumberDisplay.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       currNumberDisplay.setFill(Color.WHITE);

       hbox.getChildren().add(currNumberDisplay);
       hbox.setAlignment(Pos.CENTER);

       return hbox;
   }

   private GridPane createCenterPanel() {
       GridPane grid = new GridPane();

       /* Set column labels */
       Label bLabel = new Label("B");
       bLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       bLabel.setPrefSize(75, 50);
       grid.add(bLabel, 0, 0);
       bLabel.setAlignment(Pos.CENTER);

       Label iLabel = new Label("I");
       iLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       iLabel.setPrefSize(75, 50);
       grid.add(iLabel, 1, 0);
       iLabel.setAlignment(Pos.CENTER);

       Label nLabel = new Label("N");
       nLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       nLabel.setPrefSize(75, 50);
       grid.add(nLabel, 2, 0);
       nLabel.setAlignment(Pos.CENTER);

       Label gLabel = new Label("G");
       gLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       gLabel.setPrefSize(75, 50);
       grid.add(gLabel, 3, 0);
       gLabel.setAlignment(Pos.CENTER);

       Label oLabel = new Label("O");
       oLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
       oLabel.setPrefSize(75, 50);
       grid.add(oLabel, 4, 0);
       oLabel.setAlignment(Pos.CENTER);

       /* Set buttons */
       bingoSquares = new Button[5][5];
       for (int row = 0; row < 5; row++) {
           for (int col = 0; col < 5; col++) {
               bingoSquares[row][col] = new Button(String.valueOf(bingoVals[col][row]));
               if (row == 2 && col == 2) {
                   bingoSquares[row][col].setText("FREE");
               }
               bingoSquares[row][col].setPrefSize(75, 75);
               final Button btn = bingoSquares[row][col];

               btn.setOnAction(new EventHandler<ActionEvent>() {
                   // @Override
                   public void handle(ActionEvent e) {
                       /* If no one has won yet, user can highlight spaces. */
                       if (!userHasBingo) {
                           if (btn.getText().equalsIgnoreCase("FREE")) {
                               btn.setStyle("-fx-background-color: #FFFF00;" + 
                                   "-fx-border-color: black;");
                           } else {
                               int n = Integer.parseInt(btn.getText());
                               if (currNumberDisplay.getText().equalsIgnoreCase(
                                       String.format("%c%d", getBingoColumnChar(n), n))) {
                                   /* 
                                    * Add n to drawnNumbers before checking for bingo.
                                    * This is done here, rather than in the "Next" button
                                    * handling, so that only numbers the user has pressed will
                                    * be checked. 
                                    */
                                   drawnNumbers.add(n);
                                   btn.setStyle("-fx-background-color: #FFFF00;" + 
                                       "-fx-border-color: black;");
                               }
                           }
                       }
                       /* If user wins, change game state method to congratulate them! */
                       userHasBingo = checkForUserBingo();
                       if (userHasBingo) {
                           gameStateField.setText(String.format("Bingo! You won in %d moves!",
                           numBallsDrawn));
                       }
                   }
               });

               grid.add(bingoSquares[row][col], col, row + 1);
           }
       }

       grid.getColumnConstraints().add(new ColumnConstraints(75));
       grid.setAlignment(Pos.CENTER);
       grid.setPadding(new Insets(10));

       return grid;
   }

   private HBox createBottomPanel() {
       Button getNextBtn = new Button("Next");
       getNextBtn.setPrefSize(100, 20);

       getNextBtn.setOnAction(new EventHandler<ActionEvent>() {
           // @Override
           public void handle(ActionEvent e) {
               if (!userHasBingo) {
                   if (numberBalls.isEmpty()) {
                       gameStateField.setText("Game Over. Start New Game.");
                       gameStateField.setFill(Color.WHITE);
                   } else {
                       int n = numberBalls.remove(0);
                       char c = getBingoColumnChar(n);
                       currNumberDisplay.setText(String.format("%c%d", c, n));
                       numBallsDrawn++;
                   }
               }
           }
       });

       Button newGameBtn = new Button("New Game");
       newGameBtn.setPrefSize(100, 20);

       newGameBtn.setOnAction(new EventHandler<ActionEvent>() {

           public void handle(ActionEvent event) {
               resetGUI(primaryStage);	
           }

       });

       gameStateField = new Text("Get Five Across, Down, or Diagonal!");
       gameStateField.setFill(Color.WHITE);
       gameStateField.prefHeight(30);
       gameStateField.prefWidth(1000);

       Region region1 = new Region();
       HBox.setHgrow(region1, Priority.ALWAYS);

       Region region2 = new Region();
       HBox.setHgrow(region2, Priority.ALWAYS);

       Region region3 = new Region();
       HBox.setHgrow(region3, Priority.ALWAYS);

       HBox hbox = new HBox(newGameBtn, region1, gameStateField, region2, getNextBtn, region3);

       // HBox hbox = new HBox(newGameBtn, gameStateField, getNextBtn);
       hbox.setPadding(new Insets(15, 12, 15, 12));
       hbox.setSpacing(20);

       hbox.setStyle("-fx-background-color: #336699;");
       hbox.setAlignment(Pos.CENTER);

       HBox.setHgrow(gameStateField, Priority.ALWAYS);

       return hbox;
   }

}