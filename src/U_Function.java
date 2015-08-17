import java.util.ArrayList;

//class of U++ functions note that block and local variables are really templates; a new one is made for each call
public class U_Function {
	public String name;
	public ArrayList<String> code;
	public ArrayList<U_Variable> arguments;
	public int instances;
	
	
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
	
	public U_Block call(ArrayList<U_Variable> argument_vars,U_Program par,U_Variable output){
		ArrayList<String> newcode=(ArrayList<String>)code.clone();
		
		//first find initialized variables
		ArrayList<String> newvars=new ArrayList<String>();
		for(String line:newcode){
			if(line.contains("int ")&&!line.contains("#")){
				//get rid of leading whitespace
				while(line.charAt(0)==' '||line.charAt(0)=='\t'){
					line=line.substring(1);
				}
				String[] vars=line.substring(4).split(",");
				ArrayList<String> newnames=new ArrayList<String>();
				for(String var:vars){
					String newname=name+"%"+instances+"%"+var;
					par.variables.add(new U_Variable(newname));
					newnames.add(newname);
					newvars.add(newname);
				}
			}
		}
		
		for(int i=0;i<newcode.size();i++){
			//get line
			String line=newcode.get(i);
			//replace argument variables
			for(int j=0;j<argument_vars.size();j++){
				line=line.replaceAll(arguments.get(j).identifier,argument_vars.get(j).identifier);
			}
			//replace other variables
			for(String name:newvars){
				String oldname=name.split("%")[2];
				line=line.replaceAll(oldname,name);
				newcode.set(i,line);
				//System.out.println(newcode.get(i));
			}
		}
		//delete lines with int
		for(int i=0;i<newcode.size();i++){
			String line=newcode.get(newcode.size()-i-1);
			if(line.contains("int")){
				newcode.remove(line);
			}
		}
		
		//System.out.println(newcode);
		//System.out.println(par.variables.size());
		U_Block r= new U_Block(newcode,par,new ArrayList<U_Variable>(),output,this,true);
		return r;
	}

}
