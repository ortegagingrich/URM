import java.util.ArrayList;
//class of for loops
public class U_For extends U_Command {
	public U_Block block; //block of code contained within
	public U_Variable forindex; //scope: local
	public U_Variable lower; //scope: parent
	public U_Variable upper; //scope: parent
	
	public U_For(U_Variable i,U_Variable l,U_Variable u,ArrayList<String> lines,U_Program par){
		forindex=i;
		lower=l;
		upper=u;
		ArrayList<U_Variable> locals=new ArrayList<U_Variable>();
		locals.add(forindex);
		block=new U_Block(lines,par,locals,null,this,false);
	}
	
}
