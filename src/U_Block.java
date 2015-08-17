import java.util.ArrayList;
//class of blocks of code
public class U_Block extends U_Program {

	public U_Program parent;
	public U_Variable output;
	public ArrayList<U_Variable> local_variables;
	public int local_index;//local index for registries
	public Object structure;//the object requiring the block (e.g. for loop, function)
	public boolean from_function;

	private int index;

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
		//transfer in parent variables
		for(U_Variable var:parent.variables){
			variables.add(var);
		}
		for(U_Variable var:local_variables){
			variables.add(var);
		}

		//parse lines
		index=0;
		while(index<lines.size()){
			parse_line(lines.get(index),lines);
			index++;
		}


	}

	public void parse_line(String line,ArrayList<String> lines){
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
		if(line.contains("int")){
			initiate_variable(line);
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
			make_for(line,lines);
		}
		//check for while structure
		if(line.contains("while")){
			make_while(line,lines);
		}
		//check for if structure
		if(line.contains("if")){
			make_if(line,lines);
		}
	}

	public void initiate_variable(String line){
		String rest=line.substring(4,line.length());
		String[] vars=rest.split(",");
		for(String var:vars){
			U_Variable newvar=new U_Variable(var);
			local_variables.add(newvar);
			variables.add(newvar);
		}
	}

	//saves variables to parent
	public void call_function(String line){
		//first identify the appropriate function
		String name=line.split("\\(")[0].split("=")[1];
		U_Function function=parent.get_function(name);
		//next make appropriate global variables
		ArrayList<U_Variable> locals=new ArrayList<U_Variable>();
		for(U_Variable var:function.arguments){
			String newname=function.name+"%"+function.instances+"%"+var.identifier;
			U_Variable newvar=new U_Variable(newname);
			parent.variables.add(newvar);
			locals.add(newvar);
		}
		//get inputs
		String[] args=line.split("\\(")[1].substring(0,line.split("\\(")[1].length()-1).split(",");
		ArrayList<U_Variable> inputs=new ArrayList<U_Variable>();
		for(String arg:args){
			inputs.add(get_variable(arg));
		}
		//get output
		U_Variable output=get_variable(line.split("=")[0]);
		//get specific code block
		U_Block block=function.call(locals,parent,output);

		commands.add(new U_Call(block,inputs,output,locals));

		function.instances++;
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
			toremove.add(forindex);
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
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();}
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
		commands.add(new U_For(u1,u2,u3,lines,parent));
	}

	public void make_while(String line,ArrayList<String> biglines){
		ArrayList<String> lines1=new ArrayList<String>();
		int depth=1;
		int forindex=index+1;
		ArrayList<Integer> toremove=new ArrayList<Integer>();
		try{while(depth>0){
			String newline=biglines.get(forindex);
			toremove.add(forindex);
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
			}
			if(depth>0){
				lines1.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();}
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
			commands.add(new U_While(v1,v2,lines1,parent));
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
			}
			if(depth>0){
				lines1.add(newline);
			}
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();}
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
			//increase index
			forindex++;
		}}catch(Exception e){e.printStackTrace();}
		//remove unwanted elements of biglines
		for(int i=biglines.size()-1;i>-1;i--){
			if(toremove.contains(i)){
				biglines.remove(i);
			}
		}

		if(line.contains("==")){
			//finally get conditional variables
			String[] parts=line.split("==");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_If(v1,v2,lines1,lines2,parent));
		}else if(line.contains("<")&&!line.contains("<=")){
			//finally get conditional variables
			String[] parts=line.split("<");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Ifless(v1,v2,lines1,lines2,parent));
		}else if(line.contains("<=")){
			//finally get conditional variables
			String[] parts=line.split("<=");
			String name1=parts[0].substring(3);
			String name2=parts[1].substring(0,parts[1].length()-1);
			U_Variable v1,v2;
			v1=get_variable(name1);
			v2=get_variable(name2);
			//add if to command
			commands.add(new U_Iflesseq(v1,v2,lines1,lines2,parent));
		}
	}

	public U_Variable get_variable(String name){
		for(U_Variable var:variables){
			if(var.identifier.equals(name)){
				return var;
			}
		}
		for(U_Variable var:parent.variables){
			if(var.identifier.equals(name)){
				return var;
			}
		}
		return null;
	}

	//other methods are basically identical

}
