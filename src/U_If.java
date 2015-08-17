import java.util.ArrayList;
//if statements of the form if(x==y)
public class U_If extends U_Command {
	public U_Block main_block;
	public U_Block else_block;
	public U_Variable left;
	public U_Variable right;
	
	public U_If(U_Variable l,U_Variable r,ArrayList<String> lines1,ArrayList<String> lines2,U_Program par){
		left=l;
		right=r;
		main_block=new U_Block(lines1,par,new ArrayList<U_Variable>(),null,this,false);
		else_block=new U_Block(lines2,par,new ArrayList<U_Variable>(),null,this,false);
	}

}
