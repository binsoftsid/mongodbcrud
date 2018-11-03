package mongodbcrud;

/**
*
* author: Nenad Ivanisevic dipl.ing.el.teh
* mail: nenadbnband@gmail.com
* najprostije moguce resenja za rad sa mongo bazom podataka
* program se automatski konektuje na bazu a URL baze iscitava
* iz mongo.properties fajla
* metode su razdvojene linijama radi bolje preglednosti programa
*
***/
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.util.Properties;
import java.io.*;
import java.util.Vector;

import javax.swing.table.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;


public class MongoForm extends JDialog {

	private static final long serialVersionUID = 1L;
	private JButton novi,izmeni,brisi,unesi,izlaz;
	private static JLabel l[];
    public static JFormattedTextField t[],mmoj;
	private static Properties prop = null;
	private static String putanjabaza="",koji="";
	private JScrollPane jspane;
	
	//parametri za konekciju na MongoDB
	public static MongoClient mongoClient = null;
	public static DB database = null;
	public static DBCollection collection = null;
	public static DBCursor cursor = null;

	private static boolean postojiunos = false;
	public static mQTM1 qtbl;
   	public static JTable jtbl;
	public Vector<Object[]> totalrows;

//-------------------------------------------------------------------------------------
@SuppressWarnings("serial")
public MongoForm(){
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt)
			{
				Izlaz();}});

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		setTitle("Mongo database CRUD example");


		JPanel pan = new JPanel();
		pan.setLayout ( new GridLayout(1,2));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout ( new FlowLayout(FlowLayout.LEFT) );

		novi = new JButton("Novi");
		novi.setMnemonic('N');
        novi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        novi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Novi();}});
		novi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Novi(); }});
		buttonPanel.add( novi );

		unesi = new JButton("Unesi");
		unesi.setMnemonic('U');
        unesi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        unesi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Unesi();}});
		unesi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Unesi(); }});
		buttonPanel.add( unesi );

		izmeni = new JButton("Izmeni");
		izmeni.setMnemonic('I');
        izmeni.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        izmeni.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Izmeni();}});
		izmeni.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izmeni(); }});
		buttonPanel.add( izmeni );

		brisi = new JButton("Brisi");
		brisi.setMnemonic('B');
        brisi.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        brisi.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Brisi();}});
		brisi.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Brisi(); }});
		buttonPanel.add( brisi );

		izlaz = new JButton("Izlaz");
		izlaz.setMnemonic('Z');
		izlaz.addActionListener(new ActionListener() {
	               public void actionPerformed(ActionEvent e) {
				   Izlaz(); }});
		buttonPanel.add( izlaz );

		procitajParametre();
		konekcijaMongo();
		
		pan.add(buildFilterPanel());
		pan.add(buildTable());

		container.add(pan, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setSize(700,300);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth = d.width;
		setLocation(((screenWidth / 2) - 150), ((screenHeight / 2) - 260));		 
}
//-------------------------------------------------------------------------------------
@SuppressWarnings("deprecation")
public void konekcijaMongo(){
		if (putanjabaza.trim().length()>0)
		{
			try{
				mongoClient = new MongoClient(new MongoClientURI(putanjabaza));
				database = mongoClient.getDB("binplate");
				collection = database.getCollection("sifarnikrobe");
			}catch(MongoException uhc){
				JOptionPane.showMessageDialog(null, "Greska konekcija:"+uhc.getMessage());
			}catch(Exception ee){
				JOptionPane.showMessageDialog(null, "Greska Exception konekcija:"+ee.getMessage());
			}
		}else{
			JOptionPane.showMessageDialog(null, "Putanja na bazu ne postoji - program se zatvara");
			Izlaz();
		}
}
//-------------------------------------------------------------------------------------
@SuppressWarnings("serial")
public JPanel buildFilterPanel() {
		JPanel p = new JPanel();
		p.setLayout( new mCLFields() );
		p.setBorder( new TitledBorder("Unos") );

		int i;
        int n_fields = 5; 
        t = new JFormattedTextField[n_fields]; 
        l = new JLabel[n_fields]; 
		
		String fmm;
		fmm = "*****";
        l[0] = new JLabel("Sifra :");
        t[0] = new JFormattedTextField(createFormatter(fmm,1));
		t[0].setColumns(10);
        t[0].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[0].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[0]);}});

		fmm = "******************************";
        l[1] = new JLabel("Naziv :");
        t[1] = new JFormattedTextField(createFormatter(fmm,3));
		t[1].setColumns(20);
		t[1].addFocusListener(new FL());
        t[1].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[1].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[1]);}});

		fmm = "*";
        l[2] = new JLabel("Tar.br. :");
        t[2] = new JFormattedTextField(createFormatter(fmm,1));
		t[2].setColumns(10);
        t[2].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[2].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[2]);}});

		fmm = "*************";
        l[3] = new JLabel("Barkod :");
        t[3] = new JFormattedTextField(createFormatter(fmm,1));
		t[3].setColumns(10);
        t[3].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[3].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[3]);}});

        l[4] = new JLabel("Cena :");
        t[4] = new JFormattedTextField(createFormatter(fmm,2));
		t[4].setColumns(10);
        t[4].getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"check");
        t[4].getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {Akcija(t[4]);}});

	    for(i=0;i<n_fields;i++){ 
            p.add(l[i]); 
            p.add(t[i]); 
		}

		return p;
    }
//-------------------------------------------------------------------------------------
protected MaskFormatter createFormatter(String s, int koji) {
		MaskFormatter formatter = null;

		try {
			formatter = new MaskFormatter(s);
				switch (koji)	{
				case 1:
					//formater za cele brojeve
					formatter.setValidCharacters("0123456789 ");
					break;
				case 2:
					//formater za iznose sa decimalama
					formatter.setValidCharacters("0123456789. ");
					break;
				case 3:
					//za tekstualna polja bez ogranicenja
					break;
				case 4:
					//formater za datume
					formatter.setValidCharacters("0123456789/ ");
					break;
				}

		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
		}
		return formatter;
}
//-------------------------------------------------------------------------------------
public static void Novi(){
	   int i =0;
		for (i=0;i<5 ;i++ )
		{
			t[i].setText("");
		}
		postojiunos = false;
		t[0].requestFocus();
}
//-------------------------------------------------------------------------------------
public void Izlaz(){
		if (mongoClient != null)
		{
			mongoClient.close();
		}
		System.exit(0);
}
//-------------------------------------------------------------------------------------
public static void Unesi(){
		String naziv,barkod;
		int sifra=0,tarbr=0;
		double cena = 0.0;
		
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 && t[3].getText().trim().length() != 0 &&
		t[4].getText().trim().length() != 0)
	{
		if (postojiunos == false)
		{
			try{ sifra = Integer.parseInt(t[0].getText().trim());
			}catch(Exception e){}
			try{ tarbr = Integer.parseInt(t[2].getText().trim());
			}catch(Exception e){}
			try{ cena = Double.parseDouble(t[4].getText().trim());
			}catch(Exception e){}
			naziv = t[1].getText().trim();
			barkod = t[3].getText().trim();

			DBObject slog = new BasicDBObject("sifra", sifra)
                            .append("tarbr",tarbr)
							.append("naziv",naziv)
							.append("barkod",barkod)
							.append("cena",cena);
			try {
				collection.insert(slog);
				popuniTabelu();
				Novi();
			
				System.out.println("Single Document Insert Successfully...");
			} catch (MongoException | ClassCastException e) {
				System.out.println("Exception occurred while insert **Single Document** : " + e);
			}
		}else{
			JOptionPane.showMessageDialog(null, "Slog vec postoji sa ovom sifrom");
			t[0].requestFocus();
		}

	}else{
		JOptionPane.showMessageDialog(null, "Prvo popunite sva polja");
		t[0].requestFocus();
	}
}
//-------------------------------------------------------------------------------------
public static void Izmeni(){
		String naziv,barkod;
		int sifra=0,tarbr=0;
		double cena = 0.0;
	if (t[0].getText().trim().length() != 0 && t[1].getText().trim().length() != 0 && 
		t[2].getText().trim().length() != 0 && t[3].getText().trim().length() != 0 &&
		t[4].getText().trim().length() != 0 )
	{
		
		try{ sifra = Integer.parseInt(t[0].getText().trim());
		}catch(Exception e){}
		try{ tarbr = Integer.parseInt(t[2].getText().trim());
		}catch(Exception e){}
		try{ cena = Double.parseDouble(t[4].getText().trim());
		}catch(Exception e){}
		naziv = t[1].getText().trim();
		barkod = t[3].getText().trim();
		
		DBObject upit = new BasicDBObject("sifra", sifra);
		DBObject update = new BasicDBObject("sifra", sifra)
        .append("tarbr",tarbr)
		.append("naziv",naziv)
		.append("barkod",barkod)
		.append("cena",cena);
		
		try {
			collection.update(upit, update);
			popuniTabelu();
			Novi();
			
			System.out.println("Single Document Updated Successfully...");
		} catch (MongoException | ClassCastException e) {
			System.out.println("Exception occurred while update **Single Document** : " + e);
		}
	}else{
		JOptionPane.showMessageDialog(null, "Prvo popunite sva polja");
		t[0].requestFocus();
	}
}
//-------------------------------------------------------------------------------------
public static void Brisi(){
		if (t[0].getText().trim().length()>0)
		{
			int sifra=0;
		
			try{ sifra = Integer.parseInt(t[0].getText().trim());
			}catch(Exception e){}
		
			DBObject upit = new BasicDBObject("sifra", sifra);
		
			try {
				collection.remove(upit);
				popuniTabelu();
				Novi();
			
				System.out.println("Single Document Deleted Successfully...");
			} catch (MongoException | ClassCastException e) {
				System.out.println("Exception occurred while delete **Single Document** : " + e);
			}
		}else{
			JOptionPane.showMessageDialog(null, "Unesi polje Sifra");
			t[0].requestFocus();
		}
}
//-------------------------------------------------------------------------------------
public static void Find(String _koji){
		int sifra=0;
		DBCursor cursor = null;
		
		try{ sifra = Integer.parseInt(_koji);
		}catch(Exception e){}

		DBObject upit = new BasicDBObject("sifra", sifra);
		try
		{
			cursor = collection.find(upit);
			t[0].setText(String.valueOf(cursor.one().get("sifra")));
			t[1].setText((String)cursor.one().get("naziv"));
			t[2].setText(String.valueOf(cursor.one().get("tarbr")));
			t[3].setText((String)cursor.one().get("barkod"));
			t[4].setText(String.valueOf(cursor.one().get("cena")));
			postojiunos = true;
		}catch (NullPointerException ee)
		{
		}
}
//-------------------------------------------------------------------------------------
public static void popuniTabelu() {
	   	qtbl.query();
		qtbl.fire();
		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);
}
//-------------------------------------------------------------------------------------
public JPanel buildTable() {
		JPanel ptbl = new JPanel();
	   	ptbl.setLayout( new GridLayout(1,1) );
		ptbl.setBorder( new TitledBorder("Podaci") );

	   	qtbl = new mQTM1();
	   	qtbl.query();
		jtbl = new JTable(qtbl);
		jtbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		jtbl.addMouseListener(new ML());
        
		TableColumn tcol = jtbl.getColumnModel().getColumn(0);
	   	tcol.setPreferredWidth(50);
	   	TableColumn tcol1 = jtbl.getColumnModel().getColumn(1);
	   	tcol1.setPreferredWidth(120);
	   	TableColumn tcol2 = jtbl.getColumnModel().getColumn(2);
	   	tcol2.setPreferredWidth(40);
	   	TableColumn tcol3 = jtbl.getColumnModel().getColumn(3);
	   	tcol3.setPreferredWidth(40);

	   	jspane = new JScrollPane( jtbl );
	   	ptbl.add( jspane );
		return ptbl;
}
//-------------------------------------------------------------------------------------
public static void prikaziIzTabele() {
		int kojirec = jtbl.getSelectedRow();
		koji = jtbl.getValueAt(kojirec,0).toString();
		Find(koji);
}
//-------------------------------------------------------------------------------------
private void procitajParametre(){
		InputStream input = null;
 		File ff = new File("mongo.properties");
		try {
			input = new FileInputStream(ff);
		    prop = new Properties();
			prop.load(input);
			input.close();
			loadParamsStatus();
		} catch (FileNotFoundException e1) {
 				System.out.println( "Ne postoji fajl mongo.properties");
				System.exit(0);
		} catch (IOException e2) {
 				System.out.println( "Ne moze da ucita mongo.properties");
				System.exit(0);
		}
}
//-------------------------------------------------------------------------------------
private void loadParamsStatus(){
		try{
			putanjabaza = prop.getProperty("putanjabaza").trim();
		}catch(Exception e){
 			System.out.println( "Ne postoji putanja na bazu");
		}
}
//-------------------------------------------------------------------------------------
public void Akcija(JFormattedTextField e) {
		JFormattedTextField source;
		source = e;
				if (source == t[0]){
					t[1].setSelectionStart(0);
					t[1].requestFocus();
				}else if (source == t[1])
				{
					t[2].setSelectionStart(0);
					t[2].requestFocus();
				}else if (source == t[2])
				{
					t[3].setSelectionStart(0);
					t[3].requestFocus();
				}else if (source == t[3])
				{
					t[4].setSelectionStart(0);
					t[4].requestFocus();
				}else if (source == t[4])
				{
					if (postojiunos)
					{
						izmeni.requestFocus();
					}else{
						unesi.requestFocus();
					}
				}
}
//=====================================================================================
class FL implements FocusListener {
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		if (source == t[1]){
			if (t[0].getText().trim().length() > 0)
			{
				String koji = t[0].getText().trim();
				Find(koji);
			}
		}
	}
//-------------------------------------------------------------------------------------
	public void focusLost(FocusEvent e) {
	}
}	
//=====================================================================================
class ML extends MouseAdapter{
	public void mousePressed(MouseEvent e) {
		Object source = e.getSource();
		if (source == jtbl){
			prikaziIzTabele();
		}
	}
}//end of class ML
@SuppressWarnings("serial")
//=====================================================================================
 class mQTM1 extends AbstractTableModel {
    String[] colheads = {"Sifra", "Naziv", "Tarbr","Barkod","Cena"};

//-------------------------------------------------------------------------------------
   public mQTM1(){
		totalrows = new Vector<Object[]>();
   }
//-------------------------------------------------------------------------------------
   public String getColumnName(int i) { return colheads[i]; }
   public int getColumnCount() { return 5; }
   public int getRowCount() { return totalrows.size(); }
   public Object getValueAt(int row, int col) {
      return totalrows.elementAt(row)[col];
   }
   public boolean isCellEditable(int row, int col) {
      return false;
   }
   public Class<? extends Object> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
   }
   public void fire() {
      fireTableChanged(null);
   }
//-------------------------------------------------------------------------------------
   public void query() {
			try {
                cursor = collection.find();
				totalrows = new Vector<Object[]>();
                while(cursor.hasNext()) {
					DBObject obj = cursor.next();
					Object[] record = new Object[5];
                    record[0] = String.valueOf(obj.get("sifra"));
                    record[1] = (String)obj.get("naziv");
                    record[2] = String.valueOf(obj.get("tarbr"));
                    record[3] = (String)obj.get("barkod");
                    record[4] = String.valueOf(obj.get("cena"));
					totalrows.addElement( record );
                }
				cursor.close();
			}catch(Exception ee){
				JOptionPane.showMessageDialog(null, "Greska tabela:"+ee.getMessage());
			}
    }
 }//end of class mQTM1

}//end of class MongoForm

//Layout za postavljanje labela i polja za unos podataka
class mCLFields implements LayoutManager {

  int xInset = 5;
  int yInset = 10;
  int yGap = 8;

//-------------------------------------------------------------------------------------
  public void addLayoutComponent(String s, Component c) {}

//-------------------------------------------------------------------------------------
  public void layoutContainer(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      Dimension compSize1 = null;
      for (int i = 0; i < children.length; i = i + 2) {
	  compSize = children[i].getPreferredSize();
	  compSize1 = children[i + 1].getPreferredSize();
	  children[i].setSize(compSize.width, compSize.height);
	  children[i].setLocation( xInset + insets.left, height + 5);
	  children[i + 1].setSize(compSize1.width, compSize1.height);
	  children[i + 1].setLocation( xInset + insets.left + 120, height);
	  height += compSize.height + yGap;
      }
  }
//------------------------------------------------------------------------------------------------------------------
  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
	  compSize = children[i].getPreferredSize();
	  height += compSize.height + yGap;
	  width = Math.max(width, compSize.width + insets.left + insets.right + xInset*2 + 10);
      }
      height += insets.bottom - 740;
      return new Dimension( width, height);
  }
  
//------------------------------------------------------------------------------------------------------------------
  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  	}
//------------------------------------------------------------------------------------------------------------------
  public void removeLayoutComponent(Component c) {} 

}//end class mCLFields

