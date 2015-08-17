//assignment commands
public class U_Assignment extends U_Command {
	public U_Variable from;
	public U_Variable to;
	
	public U_Assignment(U_Variable f,U_Variable t){
		from=f;
		to=t;
	}

}
