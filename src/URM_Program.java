import java.util.ArrayList;
import java.io.*;

//class of URM programs
public class URM_Program {
	
	public ArrayList<URM_Command> commands;
	
	public URM_Program(){
		commands=new ArrayList<URM_Command>();
		//fill zero slot with dummy object
		commands.add(null);
	}
	
	public void clear(){
		commands.clear();
		commands.add(null);
	}
	
	public void add_command(String t,int a1){
		commands.add(new URM_Command(commands.size(),t,a1,0,0));
	}
	
	public void add_command(String t,int a1,int a2){
		commands.add(new URM_Command(commands.size(),t,a1,a2,0));
	}
	
	public void add_command(String t,int a1,int a2,int a3){
		commands.add(new URM_Command(commands.size(),t,a1,a2,a3));
	}
	
	public void add_jump_tentative(int a1,int a2){
		commands.add(new URM_Command(commands.size(),"J",a1,a2,0));
	}
	
	public void finalize_jump(int pos,int tar){
		int a1=commands.get(pos).arg1();
		int a2=commands.get(pos).arg2();
		commands.set(pos,new URM_Command(pos,"J",a1,a2,tar));
	}
	
	
	public int length(){
		return commands.size()-1;
	}
	
	//print program to file
	
	public void print(String filepath){
		String dir=filepath;
		
		try{
			FileWriter ryt=new FileWriter(dir);
			BufferedWriter out=new BufferedWriter(ryt);
			ArrayList<URM_Command> coms=(ArrayList<URM_Command>)commands.clone();
			coms.remove(null);
			for(URM_Command line:coms){
				String s=line.line_number+":"+line.type+"(";
				for(int arg:line.arguments){
					s=s+arg+",";
				}
				s=s.substring(0,s.length()-1)+")";
				out.write(s);
				out.newLine();
			}
			out.close();
		}catch(Exception ex){ex.printStackTrace();}
		
	}
	
	public void load(String filename){
		String dir=URM_Compiler.directory+filename;
		
		clear();
		try{
			FileReader ryt=new FileReader(dir);
			BufferedReader in=new BufferedReader(ryt);
			while(true){
				String line=in.readLine();
				if(line==null){
					break;
				}
				String type=line.substring(1,2);
				int arg1=Integer.parseInt(line.substring(3,4));
				int arg2;
				if(line.length()>5){
					arg2=Integer.parseInt(line.substring(5,6));
				}else{
					arg2=0;
				}
				int arg3;
				if(line.length()>7){
					arg3=Integer.parseInt(line.substring(7,8));
				}else{
					arg3=0;
				}
				add_command(type,arg1,arg2,arg3);
			}
			in.close();
		}catch(Exception ex){ex.printStackTrace();}
		
	}

}
