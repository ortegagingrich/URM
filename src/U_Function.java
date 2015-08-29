import java.util.ArrayList;

//class of U++ functions note that block and local variables are really templates; a new instance is made for each call
public class U_Function {
	public String name;
	public ArrayList<String> code;
	public ArrayList<U_Variable> arguments;
	public int instances; //the number of instances called so far
	
	public U_Function(String n,ArrayList<String> args,ArrayList<String> lines){
		instances=0;
		name=n;
		arguments=new ArrayList<U_Variable>();
		for(String name:args){
			U_Variable newvar=new U_Variable(name);
			arguments.add(newvar);
		}
		code=lines;
	}
	
	
	//Note: par must truly be the parent program, not just one level up; this is for variable scope
	public U_Block call(U_Program par,U_Variable output){
		
		//New idea: just make a block; add argument local variables, let the block handle the rest
		ArrayList<U_Variable> locals=new ArrayList<U_Variable>();
		locals.addAll(arguments);
		
		//make deep copy of code array
		ArrayList<String> newcode=new ArrayList<String>(code);
		
		U_Block block=new U_Block(newcode,par,locals,output,this,true);
		
		
		return block;
		
	}

}
