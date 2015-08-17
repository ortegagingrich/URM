import java.util.ArrayList;
//class of function calls
public class U_Call extends U_Command {
	public ArrayList<U_Variable> inputs;
	public U_Variable output;
	public ArrayList<U_Variable> arguments;
	public U_Block block;
	
	public U_Call(U_Block b,ArrayList<U_Variable> i,U_Variable o,ArrayList<U_Variable> args){
		arguments=args;
		block=b;
		inputs=i;
		output=o;
	}

}
