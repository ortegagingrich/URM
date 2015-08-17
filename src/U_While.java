import java.util.ArrayList;

public class U_While extends U_Command {
	
	public U_Block block;
	public U_Variable left;
	public U_Variable right;
	
	public U_While(U_Variable l,U_Variable r,ArrayList<String> lines,U_Program par){
		left=l;
		right=r;
		block=new U_Block(lines,par,new ArrayList<U_Variable>(),null,this,false);
	}
	

}
