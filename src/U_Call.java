import java.util.ArrayList;
//class of function calls
public class U_Call extends U_Command {
	public ArrayList<U_Variable> inputs; //variables outside the function to be copied to the arguments
	public U_Variable output; //variable that the result is to be copied to
	public ArrayList<U_Variable> arguments; //local variables inside the function to which inputs are copied
	public U_Block block;
	
	public U_Call(U_Block b,ArrayList<U_Variable> i,U_Variable o,ArrayList<U_Variable> args){
		arguments=args;
		block=b;
		inputs=i;
		output=o;
	}

}
