import java.util.ArrayList;

//class of blocks of code
public class U_Block extends U_Program {

	//note: each block inherits from its parents a list of all variables within its scope (local and global)

	public U_Program parent;
	public U_Variable output;
	public ArrayList<U_Variable> local_variables;
	public int local_index;//local index for registries
	public Object structure;//the object requiring the block (e.g. for loop, function)
	public boolean from_function;

	private int index;
	
	//Very important note: the array of strings 'lines' is modified, so to be safe, a clone should be fed in.
	public U_Block(ArrayList<String> lines,U_Program par,ArrayList<U_Variable> locals,U_Variable o,Object s,boolean ff){
		super();
		local_variables=locals;
		commands=new ArrayList<U_Command>();
		variables=new ArrayList<U_Variable>();
		parent=par;
		functions=par.functions;
		output=o;
		structure=s;
		from_function=ff;

		//unless a function call block, transfer in parent variables (local one layer up)
		if(!from_function){
			//note that this is the reason function's cannot access "global" variables
			for(U_Variable var:parent.variables){
				variables.add(var);
			}
		}
		

		//add in local variables
		for(U_Variable var:local_variables){
			variables.add(var);
		}
		
		//parse lines
		index=0;
		while(index<lines.size()){
			String line=lines.get(index);
			
			
			try{
				parse_line(line,lines);
			}catch(Exception ex){
				ex.printStackTrace();
				System.out.println(line);
				System.out.println(line.substring(0,7));
				System.exit(0);
			}
			index++;
		}


	}

	public void parse_line(String line,ArrayList<String> lines){
		//if line is empty, continue
		if(line.length()==0){
			return;
		}
		//get rid of leading whitespace
		while(line.charAt(0)==' '||line.charAt(0)=='\t'){
			line=line.substring(1);
		}
		//check for a comment
		if(line.contains("#")){
			int comment_start=line.indexOf("#");
			line=line.substring(0,comment_start);
		}
		//if line is empty, continue
		if(line.length()==0){
			return;
		}
		//get rid of trailing whitespace
		while(line.charAt(line.length()-1)==' '||line.charAt(line.length()-1)=='\t'){
			line=line.substring(0,line.length()-1);
		}
		
		/*
		 * At this point, the leading whitespace has been eliminated so if there is a keyword at the start of
		 * the line, it should be be at the first position.
		 */
		
		
		//checks for succession
		if(line.contains("++")){
			succeed_variable(line);
			return;
		}
		//check for assignment
		if(line.contains("=")&&!contains_control_structure(line)){
			assign_variable(line);
			return;
		}
		//check if string is short, for some reason
		if(line.length()<4){
			return;
		}
		//check for variable initialization:
		if(line.substring(0,4).equals("int ")){
			initiate_variable(line);
			return;
		}
		//check for return
		if(line.substring(0,7).equals("return ")){
			return_variable(line);
			return;
		}
		//check for for structure
		if(line.substring(0,4).equals("for ")){
			make_for(line,lines);
			return;
		}
		//check for if structure
		if(line.substring(0,3).equals("if(")){
			make_if(line,lines);
			return;
		}
		//check for while structures
		if(line.substring(0,6).equals("while(")){
			make_while(line,lines);
			return;
		}
	}

	public void initiate_variable(String line){
		String rest=line.substring(4,line.length());
		rest=remove_whitespace(rest);
		String[] vars=rest.split(",");
		for(String var:vars){
			U_Variable newvar=new U_Variable(var);
			local_variables.add(newvar);
			variables.add(newvar);
		}
	}
	
	
	//any operation which sets a variable value
	public void assign_variable(String line){
		//remove whitespace
		line=remove_whitespace(line);
		
		String name1=line.split("=")[0];
		U_Variable var1=get_variable(name1);

		//There are three possible types of operations
		try{
			//Type 1: Set variable as a constant integer
			int t=Integer.parseInt(line.split("=")[1]);
			commands.add(new U_Set(t,var1));

		}catch(Exception ex){
			//if parsing the integer fails, clearly this is not a type 1 operation
			String name2=line.split("=")[1];

			//if the name contains parentheses, it must be a Type 2: function call
			if(name2.contains("(")&&name2.contains(")")){
				//type 2
				call_function(line);
			}else{
				//otherwise: type 3: set one variable's value to another's
				U_Variable var2=get_variable(name2);
				commands.add(new U_Assignment(var2,var1));
			}
		}
	}
	

	//saves variables to parent
	public void call_function(String line){
		//first identify the appropriate function
		String name=line.split("\\(")[0].split("=")[1];
		U_Function function=parent.get_function(name);

		//get inputs
		String[] args=line.split("\\(")[1].substring(0,line.split("\\(")[1].length()-1).split(",");
		ArrayList<U_Variable> inputs=new ArrayList<U_Variable>();
		for(String arg:args){
			inputs.add(get_variable(arg));
		}

		//get output
		U_Variable output=get_variable(line.split("=")[0]);

		//get specific code block
		//note: for function calls, the root program is given as the parent rather than this
		//This is because we do not want any local variables accessible inside the function
		U_Block block=function.call(parent,output);

		commands.add(new U_Call(block,inputs,output,function.arguments));
	}

	//doesn't actually end program, just makes transfer
	public void return_variable(String line){
		String rest=line.substring(7);
		U_Variable var=get_variable(rest);
		commands.add(new U_Assignment(var,output));
	}

	public void make_for(String line,ArrayList<String> biglines){
		//first determine the scope of the for loop
		ArrayList<String> lines=new ArrayList<String>();
		int depth=1;
		int forindex=index+1;
		ArrayList<Integer> toremove=new ArrayList<Integer>();
		try{while(depth>0){
			String newline=biglines.get(forindex);
			
			//remove leading and trailing whitespace
			//get rid of leading whitespace
			while(newline.charAt(0)==' '||newline.charAt(0)=='\t'){
				newline=newline.substring(1);
			}
			//check for a comment
			if(newline.contains("#")){
				int comment_start=newline.indexOf("#");
				newline=newline.substring(0,comment_start);
			}
			//get rid of trailing whitespace
			if(newline.length()!=0){
				//get rid of trailing whitespace
				while(newline.charAt(newline.length()-1)==' '||newline.charAt(newline.length()-1)=='\t'){
					newline=newline.substring(0,newline.length()-1);
				}
			}
			
			
			
			toremove.add(forindex);
			if(contains_control_structure(newline)){
				depth++;
			}
			if(newline.equals("end")){
				depth--;
			}
			if(depth>0){
				lines.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();System.out.println(biglines.toString());System.exit(0);}
		//remove unwanted elements of biglines
		for(int i=biglines.size()-1;i>-1;i--){
			if(toremove.contains(i)){
				biglines.remove(i);
			}
		}
		
		
		
		
		//note: at this point lines contains all of the lines for the block inside the for loop
		//next split the line by commas
		String[] parts=line.substring(4).split(",");
		U_Variable u1,u2,u3;
		u1=new U_Variable(parts[0]);//make new local variable, read in the rest
		u2=get_variable(parts[1]);
		u3=get_variable(parts[2]);
		//add for loop to commands
		commands.add(new U_For(u1,u2,u3,lines,this));
	}

	public void make_while(String line,ArrayList<String> biglines){
		ArrayList<String> lines1=new ArrayList<String>();
		int depth=1;
		int forindex=index+1;
		ArrayList<Integer> toremove=new ArrayList<Integer>();
		try{while(depth>0){
			String newline=biglines.get(forindex);
			toremove.add(forindex);
			
			
			//remove leading and trailing whitespace
			//get rid of leading whitespace
			while(newline.charAt(0)==' '||newline.charAt(0)=='\t'){
				newline=newline.substring(1);
			}
			//check for a comment
			if(newline.contains("#")){
				int comment_start=newline.indexOf("#");
				newline=newline.substring(0,comment_start);
			}
			//get rid of trailing whitespace
			if(newline.length()!=0){
				//get rid of trailing whitespace
				while(newline.charAt(newline.length()-1)==' '||newline.charAt(newline.length()-1)=='\t'){
					newline=newline.substring(0,newline.length()-1);
				}
			}
			
			
			
			if(contains_control_structure(newline)){
				depth++;
			}
			if(newline.equals("end")){
				depth--;
			}
			if(depth>0){
				lines1.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();System.out.println(biglines.toString());System.exit(0);}
		//remove unwanted elements of biglines
		for(int i=biglines.size()-1;i>-1;i--){
			if(toremove.contains(i)){
				biglines.remove(i);
			}
		}
		//finally add commands
		if(line.contains("!=")){
			//finally get conditional variables
			String[] parts=line.split("!=");
			String name1=parts[0].substring(6);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//next add while command
			commands.add(new U_While(v1,v2,lines1,this));
		}
	}

	public void make_if(String line,ArrayList<String> biglines){
		ArrayList<String> lines1=new ArrayList<String>();
		int depth=1;
		int forindex=index+1;
		ArrayList<Integer> toremove=new ArrayList<Integer>();
		try{while(depth>0){
			String newline=biglines.get(forindex);
			toremove.add(forindex);
			
			
			//remove leading and trailing whitespace
			//get rid of leading whitespace
			while(newline.charAt(0)==' '||newline.charAt(0)=='\t'){
				newline=newline.substring(1);
			}
			//check for a comment
			if(newline.contains("#")){
				int comment_start=newline.indexOf("#");
				newline=newline.substring(0,comment_start);
			}
			//get rid of trailing whitespace
			if(newline.length()!=0){
				//get rid of trailing whitespace
				while(newline.charAt(newline.length()-1)==' '||newline.charAt(newline.length()-1)=='\t'){
					newline=newline.substring(0,newline.length()-1);
				}
			}
			
			
			
			if(contains_control_structure(newline)){
				depth++;
			}
			if(newline.equals("end")){
				depth--;
				//if depth is zero (i.e. no else block), don't remove this particular end
				if(depth==0){
					toremove.remove(Integer.valueOf(forindex));
				}
			}
			if(newline.equals("else")&&depth==1){
				depth=0;
			}
			if(depth>0){
				lines1.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){
			e.printStackTrace();
			System.out.println(biglines.toString());
			System.out.println("depth:");
			System.out.println(depth);
			System.exit(0);
			}
		//remove unwanted elements of biglines
		for(int i=biglines.size()-1;i>-1;i--){
			if(toremove.contains(i)){
				biglines.remove(i);
			}
		}
		//repeat for else block
		ArrayList<String> lines2=new ArrayList<String>();
		depth=1;
		forindex=index+1;
		toremove.clear();
		try{while(depth>0){
			String newline=biglines.get(forindex);
			toremove.add(forindex);
			
			
			//remove leading and trailing whitespace
			//get rid of leading whitespace
			while(newline.charAt(0)==' '||newline.charAt(0)=='\t'){
				newline=newline.substring(1);
			}
			//check for a comment
			if(newline.contains("#")){
				int comment_start=newline.indexOf("#");
				newline=newline.substring(0,comment_start);
			}
			//get rid of trailing whitespace
			if(newline.length()!=0){
				//get rid of trailing whitespace
				while(newline.charAt(newline.length()-1)==' '||newline.charAt(newline.length()-1)=='\t'){
					newline=newline.substring(0,newline.length()-1);
				}
			}
			
			
			
			if(contains_control_structure(newline)){
				depth++;
			}
			if(newline.equals("end")){
				depth--;
			}
			if(depth>0){
				lines2.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();System.out.println(biglines.toString());System.exit(0);}
		//remove unwanted elements of biglines
		for(int i=biglines.size()-1;i>-1;i--){
			if(toremove.contains(i)){
				biglines.remove(i);
			}
		}

		//now, branch based on the type of if branch
		if(line.contains("==")){
			//finally get conditional variables
			String[] parts=line.split("==");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_If(v1,v2,lines1,lines2,this));
		}else if(line.contains("!=")){
			//same as equality, but with main and else blocks switched
			String[] parts=line.split("!=");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_If(v1,v2,lines2,lines1,this));
		}else if(line.contains("<")&&!line.contains("<=")){
			//finally get conditional variables
			String[] parts=line.split("<");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Ifless(v1,v2,lines1,lines2,this));
		}else if(line.contains("<=")){
			//finally get conditional variables
			String[] parts=line.split("<=");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Iflesseq(v1,v2,lines1,lines2,this));
		}else if(line.contains(">")&&!line.contains(">=")){
			//same as not <, just with variables switched
			String[] parts=line.split(">");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Ifless(v2,v1,lines1,lines2,this));
		}else if(line.contains(">=")){
			//same as <=, just with variables switched
			String[] parts=line.split(">=");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Iflesseq(v2,v1,lines1,lines2,this));
		}
	}
	
	private U_Variable get_variable(String name){
		name=remove_whitespace(name);
		
		for(U_Variable var:variables){
			if(var.identifier.equals(name)){
				return var;
			}
		}
		System.out.println("Failed to retrieve variable: "+name);
		System.out.println("Current variables: ");
		for(U_Variable var:variables){
			System.out.println("    "+var.identifier);
		}
		if(this instanceof U_Block){
			System.out.println("Local Variables:");
			for(U_Variable var:((U_Block)this).local_variables){
				System.out.println("    "+var.identifier);
			}
			System.out.println("Block structure:");
			System.out.println(((U_Block)this).structure);
		}
		return null;
	}
	
	//other methods are basically identical

}
