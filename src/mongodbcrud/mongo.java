package mongodbcrud;
import javax.swing.*;

public class mongo {

	public static void main(String[] args) {
        try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		}  
		catch ( UnsupportedLookAndFeelException e ) {
			System.out.println ("SystemLookAndFeel not supported on this platform. \nProgram Terminated");
			System.exit(0);
		}
		catch ( IllegalAccessException e ) {
			System.out.println ("SystemLookAndFeel could not be accessed. \nProgram Terminated");
			System.exit(0);
		}
		catch ( ClassNotFoundException e ) {
			System.out.println ("SystemLookAndFeel could not found. \nProgram Terminated");
			System.exit(0);
		}   
		catch ( InstantiationException e ) {
			System.out.println ("SystemLookAndFeel could not be instantiated. \nProgram Terminated");
			System.exit(0);
		}
		catch ( Exception e ) {
			System.out.println ("Unexpected error. \nProgram Terminated");
			e.printStackTrace();
			System.exit(0);
		}
        
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {
				makeGUI();}});
		}catch (Exception e) {}
	
	}
	static void makeGUI(){
		MongoForm iz = new MongoForm();
		iz.setVisible(true);
	}

}
