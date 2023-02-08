import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
* This program is a tutorial for basic algebra written originally for one
* of my daughters in high school so that she could practice equations.
* Other programs I tried were limited in material in that they had only a
* fixed set of equations whose answers could be memorized much too quickly.
* This program by contrast generates random variables so that each equation
* is new and there is no predictable order to the questions or answers.
* The source code for this program was compiled using Java jdk vers. 1.1.5
* with no Swing components so that it would run as a standalone applet
* within Netscape Communicator 4.5 or later. It incorporates two classes
* as-is which were found in "Java in a Nutshell" by David Flanagan:
* MultilineLabel and InfoDialog, both of which were used in creating a
* dialog box.
* Though an applet it can also be run as either a text-based or
* gui application outside of the browser (text-only if started with
* argument "-c"). It was basically written as an application so it could
* then be run as applet or application with the inclusion of an applet
* front-end, allowing some re-use of code and the use of some components
* which normally could not be used in an applet at all such as the dialog
* box and menu items above the display panel.
* The compiled class files have been put in the file tutorial.jar to speed
* up loading from within a browser and keep it all tidy, but can be still
* be run as an application (if the jre is installed) by something like:
* "java -classpath c:\jdk1.1.5\lib\classes.zip;.\Tutorial.jar Tutorial".
*
* This iteration of the program has only equations in one or two unknowns.
* The next version will have simple fractions also and will be rewritten
* as an applet only to eliminate the ugly yellow Applet warning from the
* Dialog and Frame components (users will still be able to run by
* downloading tutorial.html and tutorial.jar and running the html as a
* local file). Also, future versions will be rewritten and compiled using
* the older jdk vers. 1.0.2 for maximum compatibility with older browsers.
*
**/

public class Tutorial extends Applet implements ActionListener {
   private Button startFrameButton;
   private TutorFrame tutorFrame = null;

   public void init() {
      Label introLabel = new Label ("This is a program to practice basic algebra!");
      startFrameButton = new Button("Start");
      add(introLabel);
      add(startFrameButton);
      startFrameButton.addActionListener(this);
      startFrame();
   }
   public void actionPerformed(ActionEvent e) {
      if (e.getSource()==startFrameButton) startFrame();
   }

   public void startFrame() {
      if (tutorFrame != null) return;
      tutorFrame = new TutorFrame(this);
      tutorFrame.setSize(280,190);
      tutorFrame.setVisible(true);
   }

   public void destroy() {
      if (tutorFrame != null) tutorFrame.dispose();
      tutorFrame = null;
   }

   //   public void paint (Graphics g) {
   //	   g.drawString("This is a study program.",30,60);
   //  }

   // run the main loop if called as application
   public static void main(String[] args) {
      boolean consoleFlag = false;
      if (args.length>0)
      if (args[0].equals("-c")) consoleFlag=true;
      if (consoleFlag) {
         try {
            new TutorConsoleApp(); System.exit(0);
         }
         catch (IOException ioe) {
            System.out.println("Had some kind of io error");
         }
      }
      else {
         Frame tutorFrame = new TutorFrame();
         tutorFrame.setSize(280,165);
         tutorFrame.setVisible(true);
      }
   }
}

class TutorFrame extends Frame implements ActionListener {
   private Applet ivApplet = null;
   private Frame ivFrame = this;
   private int x, attempts=0, difficulty=0;
   private TutorBase mathGen;
   private CustomCanvas canvas; //extends Canvas, so can pass args to it
   private Label promptLabel, answerLabel, scoreLabel;
   private TextField answerTextField, scoreTextField, statusTextField;
   private MenuBar menubar;
   private Menu options;
   private GridBagLayout gbLayout;
   private GridBagConstraints gbConstraints;

   public TutorFrame(Applet applet) {
      this();
      ivApplet = applet;
   }

   public TutorFrame() {
      setTitle("Math Practice - Easy");
      setLocation(300,200); //from Component
      gbLayout = new GridBagLayout(); setLayout(gbLayout);
      gbConstraints = new GridBagConstraints();
      mathGen = new TutorBase(); mathGen.setEquation(difficulty);
      promptLabel = new Label("Solve for x");
      scoreLabel =  new Label("Score: ");
      answerLabel = new Label("Type in your answer here: ");
      canvas = new CustomCanvas(mathGen.getEquation()); x=mathGen.getX();
      canvas.setBackground(Color.blue); canvas.setForeground(Color.white);
      canvas.setSize(200,100);
      answerTextField = new TextField();
      answerTextField.setColumns(4);
      scoreTextField = new TextField("0");
      scoreTextField.setColumns(4);
      scoreTextField.setEditable(false);
      statusTextField = new TextField();
      statusTextField.setEditable(false);
      menubar = new MenuBar();
      setMenuBar(menubar);
      Menu difficulties = new Menu("Options");
      menubar.add(difficulties);
      // Add items to the other two menus, this time using a  convenience
      // method defined below.  Note use of new anonymous array syntax.
      createMenuItems(difficulties, this,
      new String[] { "Easy", "Hard", "Mixed" },
      new String[] { "easy", "hard", "mixed" },
      new int[] { KeyEvent.VK_E, KeyEvent.VK_H, KeyEvent.VK_M });

      gbConstraints.anchor = GridBagConstraints.EAST;
      addComponent(answerLabel,4,1,1,1);
      addComponent(answerTextField,4,2,1,1);
      addComponent(scoreLabel,0,1,1,1);
      addComponent(scoreTextField,0,2,1,1);
      gbConstraints.weightx = 1;
      gbConstraints.fill=GridBagConstraints.HORIZONTAL;
      addComponent(promptLabel,0,0,1,1);
      addComponent(statusTextField,5,0,3,1);
      gbConstraints.weightx = 1; gbConstraints.weighty = 1;
      gbConstraints.fill=GridBagConstraints.BOTH;
      addComponent(canvas,1,0,3,3);

      answerTextField.addActionListener(this);
      addWindowListener(new CloseWindowAndExit());
   }

   public class CloseWindowAndExit extends WindowAdapter {
      public void windowClosing (WindowEvent e) {
         if (ivApplet==null) System.exit(0);
         else ivApplet.destroy();
      }
   }

   private void processAnswer() {
      try {
         int score = Integer.parseInt(scoreTextField.getText());
         int answer = Integer.parseInt(answerTextField.getText());
         if (answer==x) {
            statusTextField.setText("You got it Right!");
            score++;
            attempts=0;
            mathGen.setEquation(difficulty);
            canvas.setText(mathGen.getEquation());
            x = mathGen.getX();
            canvas.repaint();
         }
         else
            {
            statusTextField.setText("Sorry, x is not equal to "+answer+". Try again.");
            score--;
            attempts++;
         }
         scoreTextField.setText(""+score);
         if (attempts==2) {
            InfoDialog infoDialog = new InfoDialog(ivFrame,"Correct Answer"
            ,"The correct answer is: "+mathGen.getAnswer()
            +"\n \nPress the \"Okay\" button to go to a new problem.");
            infoDialog.show();
            attempts=0;
            mathGen.setEquation(difficulty);
            canvas.setText(mathGen.getEquation());
            x = mathGen.getX();
            canvas.repaint();
            statusTextField.setText("");
         }
         answerTextField.setText("");
      }
      catch(NumberFormatException nfe) {
         statusTextField.setText("Invalid input. Enter a number.");
         //System.out.println("Invalid Input. Enter an integer or \"q\" to quit.");
      }
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      if (cmd.equals("easy")) {difficulty=0; setTitle("Math Practice - Easy");}
      else if (cmd.equals("hard")) {difficulty=1; setTitle("Math Practice - Hard");}
      else if (cmd.equals("mixed")) {difficulty=2; setTitle("Math Practice - Mixed");}
      if (cmd.equals("easy") || cmd.equals("hard") || cmd.equals("mixed")) {
         scoreTextField.setText("0");
         attempts=0;
         mathGen.setEquation(difficulty);
         canvas.setText(mathGen.getEquation());
         x = mathGen.getX();
         canvas.repaint();
         statusTextField.setText("");
         answerTextField.setText("");
      }
      else processAnswer();
   }

   // programmer defined
   private void addComponent(Component c, int row, int column, int width, int height) {
      gbConstraints.gridx=column; gbConstraints.gridy=row;
      gbConstraints.gridwidth=width; gbConstraints.gridheight=height;
      gbLayout.setConstraints(c, gbConstraints);
      add(c);
   }

   /**
   * This is the convenience routine for adding menu items to a menu pane.
   * It works for pulldown or popup menu panes, since PopupMenu extends Menu.
   */
   protected static void createMenuItems(Menu pane, ActionListener listener,
   String[] labels, String[] commands,
   int[] shortcuts) {
      for(int i = 0; i < labels.length; i++) {
         MenuItem mi = new MenuItem(labels[i]);
         mi.addActionListener(listener);
         if ((commands != null) && (commands[i] != null))
         mi.setActionCommand(commands[i]);
         if ((shortcuts != null) && (shortcuts[i] != 0))
         mi.setShortcut(new MenuShortcut(shortcuts[i]));
         pane.add(mi);
      }
   }

}

class CustomCanvas extends Canvas {
   private String cs;
   public CustomCanvas(String s) {
      //		super();
      setText(s);
   }
   public void setText(String s) { cs=s; }
   public void paint (Graphics g) {
      g.drawString(cs,30,30);
   }
}


/**
* The math part of this application, used as private member of gui based
* TutorFrame, extended by console based TutorConsoleApp.
**/
class TutorBase {
  private int m, x, b, y, n;
  private String equation, equation2="";
  private boolean two = true;

  // given positive max, generate whole number in range 0 to max, inclusive
  private int getRandInt(int max) { max++; return ((int)(Math.random()*max)); }

  // given positive max, generate whole number in range -max to +max, inclusive
  private int getPlusMinusInt(int max) {	return(getRandInt(max*2) - max); }

  // given positive arg return "+", given negative return "-"
  private String getSign(int aNumber) { return ((aNumber >= 0)? "+":"-"); }
  private String[] stepsArray = new String[6];


  public TutorBase() {} // not ready for prime time yet

  public int getX() { return x; };

  private String getY() { //not used now
    String s1=""+y;
    String s2=n+"y";
    return ((two)? s2:s1);
  } //either "y" or its value as a string

  public String getAnswer() {
    if (two) return ("X = "+x+"\nThe complete solution is: {X = "+x+", Y = "+y+"}");
    else return ("X = "+x);
  }

  public String getEquation() {
    return (equation+"               "+equation2);
  }

  public void setEquation(int difficulty) {
    int switcher = getRandInt(1);//mixed, unless reset
    if (difficulty==0) switcher=0; //easy only, one equation
    else if (difficulty==1) switcher=1; //hard only, two equations
    switch(switcher) {
    case 0:  two=false; equation2=""; setEquation1(); break;
    default: two=true; setEquation2();
    }
  }

  private void setEquation1() {
    // can't have m==0, because then mx+b=y would have x=infinity, too hard for kids
    m = getPlusMinusInt(5);	while (m==0) m=getPlusMinusInt(5);
    x = getPlusMinusInt(20);
    b = getPlusMinusInt(50);
    y=m*x+b;
    int switcher = getRandInt(3);
    switch(switcher) {
    case 0: equation=m+"x"+" "+getSign(b)+" "+Math.abs(b)+" = "+y;
      stepsArray[0]=equation;
      stepsArray[1]=m+"x"+" "+getSign(b)+" "+Math.abs(b)+" "+
	getSign(-b)+" "+Math.abs(b)+" = "+y+" "+getSign(-b)+" "+Math.abs(b);
      stepsArray[2]= m+"x + 0  = "+(y-b);
      stepsArray[3]=m+"x"+" = "+(y-b);
      stepsArray[4]="(" + m + "x)/ "+m+" = "+(y-b)+"/"+m;
      stepsArray[5]="x = "+x;
      System.out.println(stepsArray[0]);
      System.out.println(stepsArray[1]);
      System.out.println(stepsArray[2]);
      System.out.println(stepsArray[3]);
      System.out.println(stepsArray[4]);
      System.out.println(stepsArray[5]);
      break;
    case 1: equation=b+" "+getSign(m)+" "+Math.abs(m)+"x"+" = "+y; break;
    case 2: equation=y+" = "+m+"x"+" "+getSign(b)+" "+Math.abs(b); break;
    default: equation=y+" = "+b+" "+getSign(m)+" "+Math.abs(m)+"x";
    }
  }

  private void setEquation2() {
    // can't have m==0, because then mx+b=y would have x=infinity, too hard for kids
    m = getPlusMinusInt(5);	while (m==0) m=getPlusMinusInt(5);
    n = getPlusMinusInt(5);	while (n==0) n=getPlusMinusInt(5);
    x = getPlusMinusInt(20);
    y = getPlusMinusInt(20);
    b=n*y-m*x;
    int switcher = getRandInt(3);
    switch(switcher) {
    case 0: equation=m+"x"+" "+getSign(b)+" "+Math.abs(b)+" = "+n+"y"; break;
    case 1: equation=b+" "+getSign(m)+" "+Math.abs(m)+"x"+" = "+n+"y"; break;
    case 2: equation=n+"y"+" = "+m+"x"+" "+getSign(b)+" "+Math.abs(b); break;
    default:equation=n+"y"+" = "+b+" "+getSign(m)+" "+Math.abs(m)+"x";
    }
    if(!two) return;
    m = getPlusMinusInt(5);	while (m==0) m=getPlusMinusInt(5);
    n = getPlusMinusInt(5);	while (n==0) n=getPlusMinusInt(5);
    b=n*y-(m*x);
    switcher = getRandInt(3);
    switch(switcher) {
    case 0: equation2=m+"x"+" "+getSign(b)+" "+Math.abs(b)+" = "+n+"y"; break;
    case 1: equation2=b+" "+getSign(m)+" "+Math.abs(m)+"x"+" = "+n+"y"; break;
    case 2: equation2=n+"y"+" = "+m+"x"+" "+getSign(b)+" "+Math.abs(b); break;
    default: equation2=n+"y"+" = "+b+" "+getSign(m)+" "+Math.abs(m)+"x";
    }
  }
}

class TutorConsoleApp extends TutorBase {
   // Application to be run using console
   int x; int score=0;

   public TutorConsoleApp() throws IOException {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      setEquation(2); x=getX();
      for(;;) {
         System.out.println("\n"+getEquation());
         System.out.print("Solve for x: ");
         String line = in.readLine();
         if (line.equals("q")) break;
         try {
            int answer = Integer.parseInt(line);
            if (x==answer) {
               System.out.print("You got it right. Good job!");
               System.out.println("     Score increased by one");
               score++;
               setEquation(2); x=getX();
            }
            else {
               System.out.println("Bummer. Wrong answer.");
               System.out.println("Score DECREASED by one");
               score--;
            }
            System.out.println("Score: "+score);
         }
         catch(Exception ex) {
            System.out.println("Invalid Input. Enter an integer or \"q\" to quit.");
         }
      } //end for
   } //end ctor
} //end class

