import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.io.File;

public class URM_Compiler {
	
	public static URM urm;
	public static U_Compiler ucomp;
	public static String directory;
	
	public static void main(String[] args) {
		
		
		//set directory
		//directory="F:\\Users\\JOG\\Documents\\U++\\";
		
		System.out.println("Hello U++");
		//make URM
		urm=new URM();
		//make compiler
		ucomp=new U_Compiler();
		
		/*
		//make sample program
		URM_Program p=new URM_Program();
		p.add_command("T",1,2);
		for(int i=1;i<=5;i++){
			p.add_command("S",1);
		}
		p.add_command("J",2,3,1);
		p.add_command("J",2,4,0);
		p.add_command("S",4);
		p.add_command("J",1,1,2);
		
		
		//test in
		URM_Program q=new URM_Program();
		q.load("test in.txt");
		System.out.println(urm.run(q,1000));
		q.print("test out.txt");
		*/
		
		//decide program to load
		File inputFile=loadFile();
		directory=inputFile.getParent();
		
		U_Program u=new U_Program(inputFile.getAbsolutePath());
		URM_Program q=ucomp.compile(u);
		System.out.println(q);
		q.print(directory+"//u.out");
	}
	
	
	//open file explorer to get a file
	private static File loadFile(){
		//make the window
		JFrame window=new JFrame();
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex){}
		UIManager.put("FileChooser.readOnly",Boolean.TRUE);
		JFileChooser fc=new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")+"//examples"));
		
		//get the file
		int result=fc.showOpenDialog(window);
		File f;
		if(result==JFileChooser.APPROVE_OPTION){
			f=fc.getSelectedFile();
		}else{
			System.exit(0);
			return null;
		}
		
		//close window and return
		window.dispose();
		return f;
	}

}
