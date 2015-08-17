import java.util.ArrayList;

//class of URM commands
public class URM_Command {
	
	public String type;
	public ArrayList<Integer> arguments;
	public int line_number;
	
	public URM_Command(int ln,String t,int a1,int a2,int a3){
		type=t;
		line_number=ln;
		arguments=new ArrayList<Integer>();
		arguments.add(a1);
		if(type.equals("T")||type.equals("J")){
			arguments.add(a2);
			if(type.equals("J")){
				arguments.add(a3);
			}
		}
	}
	
	public int arg1(){
		return arguments.get(0);
	}
	
	public int arg2(){
		return arguments.get(1);
	}
	
	public int arg3(){
		return arguments.get(2);
	}

}
