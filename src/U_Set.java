//commands that set a variable
public class U_Set extends U_Command {
	public U_Variable to;
	public int from;
	
	public U_Set(int t,U_Variable f){
		to=f;
		from=t;
	}

}
