import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class U_Program {

	public ArrayList<U_Variable> variables;
	public ArrayList<U_Function> functions;
	public ArrayList<U_Command> commands;
	public BufferedReader in;

	public U_Program(){

	}

	public U_Program(String filepath){

		variables=new ArrayList<U_Variable>();
		commands=new ArrayList<U_Command>();
		functions=new ArrayList<U_Function>();
		String dir=filepath;


		try{
			FileReader ryt=new FileReader(dir);
			in=new BufferedReader(ryt);

			while(true){
				String line=in.readLine();
				if(line==null){
					break;
				}
				parse_line(line,in);
			}
			in.close();
		}catch(Exception ex){ex.printStackTrace();}

	}

	public void parse_line(String line,BufferedReader in){
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


		//check for variable initialization:
		if(line.contains("int ")){
			initiate_variable(line);
		}
		//check for parameter read in:
		if(line.contains("par ")){
			read_parameters(line);
		}
		//checks for succession
		if(line.contains("++")){
			succeed_variable(line);
		}
		//check for assignment
		if(line.contains("=")&&!line.contains("if")&&!line.contains("while")){
			assign_variable(line);
		}
		//check for return
		if(line.contains("return")){
			return_variable(line);
		}
		//check for for structure
		if(line.contains("for")){
			make_for(line);
		}
		//check for if structure
		if(line.contains("if")){
			make_if(line);
		}
		//check for while structures
		if(line.contains("while")){
			make_while(line);
		}
		//check for function definitions
		if(line.contains("function")){
			make_function(line);
		}
	}

	public void initiate_variable(String line){
		String rest=line.substring(4,line.length());
		String[] vars=rest.split(",");
		for(String var:vars){
			variables.add(new U_Variable(var));
		}
	}

	public void read_parameters(String line){
		String rest=line.substring(4,line.length());
		String[] vars=rest.split(",");
		ArrayList<U_Variable> v=new ArrayList<U_Variable>();
		for(String n:vars){
			v.add(get_variable(n));
		}
		commands.add(new U_Parameter(v));
	}

	public void return_variable(String line){
		String rest=line.substring(7);
		U_Variable var=get_variable(rest);
		commands.add(new U_Return(var));
	}

	public void succeed_variable(String line){
		//first get appropriate variable
		String name=line.substring(0,line.length()-2);
		U_Variable var=get_variable(name);
		commands.add(new U_Succeed(var));
	}
	
	//any operation which sets a variable value
	public void assign_variable(String line){
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
			
			//try for Type 2: set one variable's value to anothers
			U_Variable var2=get_variable(name2);

			if(var2!=null){
				commands.add(new U_Assignment(var2,var1));
			}else{
				//Type 3: means that it is a function call
				call_function(line);
			}
		}
	}

	public void call_function(String line){
		//first identify the appropriate function
		String name=line.split("\\(")[0].split("=")[1];
		U_Function function=get_function(name);
		
		//next make appropriate global variables
		/*ArrayList<U_Variable> locals=new ArrayList<U_Variable>();
		for(U_Variable var:function.arguments){
			String newname=function.name+"%"+function.instances+"%"+var.identifier;
			U_Variable newvar=new U_Variable(newname);
			variables.add(newvar);
			locals.add(newvar);
		}*/
		
		//get inputs
		String[] args=line.split("\\(")[1].substring(0,line.split("\\(")[1].length()-1).split(",");
		ArrayList<U_Variable> inputs=new ArrayList<U_Variable>();
		for(String arg:args){
			inputs.add(get_variable(arg));
		}
		
		//get output
		U_Variable output=get_variable(line.split("=")[0]);
		
		//get specific code block
		U_Block block=function.call(this,output);

		commands.add(new U_Call(block,inputs,output,function.arguments));
	}

	public void make_for(String line){
		//first, read all lines inside of the loop structure (will turn this into a block)
		ArrayList<String> lines=new ArrayList<String>();
		int depth=1;
		try{while(depth>0){
			String newline=in.readLine();
			for(String s:U_Compiler.control_structures){
				if(newline.contains(s)){
					depth++;
				}
			}
			if(newline.contains("end")){
				depth--;
			}
			if(depth>0){
				lines.add(newline);
			}
			//check end
		}}catch(Exception e){}
		
		//note: at this point lines contains all of the lines for the block inside the for loop
		
		//next split the line by commas
		String[] parts=line.substring(4).split(",");
		U_Variable u1,u2,u3;
		
		//iterator variable; will be local
		u1=new U_Variable(parts[0]);
		
		//program variables
		u2=get_variable(parts[1]);
		u3=get_variable(parts[2]);
		
		//add for loop to commands
		commands.add(new U_For(u1,u2,u3,lines,this));
	}

	public void make_while(String line){
		//first determine the scope of the for loop
		ArrayList<String> lines=new ArrayList<String>();
		int depth=1;
		try{while(depth>0){
			String newline=in.readLine();
			for(String s:U_Compiler.control_structures){
				if(newline.contains(s)){
					depth++;
				}
			}
			if(newline.contains("end")){
				depth--;
			}
			if(depth>0){
				lines.add(newline);
			}
			//check end
		}}catch(Exception e){}
		//note: at this point lines contains all of the lines for the block inside the for loop
		//next split based on while condition types
		if(line.contains("!=")){
			//finally get conditional variables
			String[] parts=line.split("!=");
			String name1=parts[0].substring(6);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//next add while command
			commands.add(new U_While(v1,v2,lines,this));
		}

	}

	public void make_if(String line){
		ArrayList<String> lines1=new ArrayList<String>();
		int depth=1;
		boolean include_else=false;
		try{while(depth>0){
			String newline=in.readLine();
			for(String s:U_Compiler.control_structures){
				if(newline.contains(s)){
					depth++;
				}
			}
			if(newline.contains("end")){
				depth--;
			}
			if(newline.contains("else")&&depth==1){
				depth=0;
				include_else=true;
			}
			if(depth>0){
				lines1.add(newline);
			}
			//check end
		}}catch(Exception e){}
		
		//repeat for else block
		ArrayList<String> lines2=new ArrayList<String>();
		
		//if there is an else block, read in those lines, otherwise leave empty
		if(include_else){
			depth=1;
			try{while(depth>0){
				String newline=in.readLine();
				for(String s:U_Compiler.control_structures){
					if(newline.contains(s)){
						depth++;
					}
				}
				if(newline.contains("end")){
					depth--;
				}
				if(depth>0){
					lines2.add(newline);
				}
			}}catch(Exception e){}
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

	public void make_function(String line){
		//first, read in all lines containing the function's code
		ArrayList<String> lines=new ArrayList<String>();
		int depth=1;
		try{while(depth>0){
			String newline=in.readLine();
			for(String s:U_Compiler.control_structures){
				if(newline.contains(s)){
					depth++;
				}
			}
			if(newline.contains("end")){
				depth--;
			}
			if(depth>0){
				lines.add(newline);
			}
			//check end
		}}catch(Exception e){}
		
		//next get the name of the function
		String[] rest=line.split("\\(");
		String name=rest[0].substring(9);
		//next get the argument names
		String[] args=rest[1].substring(0,rest[1].length()-1).split(",");
		
		//make function object
		functions.add(new U_Function(name,new ArrayList<String>(Arrays.asList(args)),lines));
	}


	public U_Variable get_variable(String name){
		for(U_Variable var:variables){
			if(var.identifier.equals(name)){
				return var;
			}
		}
		return null;
	}

	public U_Function get_function(String name){
		for(U_Function fun:functions){
			if(fun.name.equals(name)){
				return fun;
			}
		}
		return null;
	}



}
