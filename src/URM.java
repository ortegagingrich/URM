

//actual URM; runs a program
public class URM {
	
	private Registers registers;
	private int current_line;
	private boolean finished;
	
	public URM(){
		registers=new Registers();
	}
	
	public void reset(){
		registers.clear();
		current_line=1;
	}
	
	public int run(URM_Program program){
		reset();
		finished=false;
		while(!finished){
			if(current_line>program.length()||current_line==0){
				finished=true;
				break;
			}
			evaluate(program.commands.get(current_line));
		}
		return registers.read(1);
	}
	
	public int run(URM_Program program,int iterations){
		reset();
		int i=0;
		finished=false;
		while(!finished){
			if(i==iterations){
				return -1;
			}
			if(current_line>program.length()||current_line==0){
				finished=true;
				break;
			}
			evaluate(program.commands.get(current_line));
			i++;
		}
		return registers.read(1);
	}
	
	
	private void evaluate(URM_Command com){
		//branch based on type
		if(com.type.equals("Z")){
			registers.write(com.arg1(),0);
			current_line++;
		}
		if(com.type.equals("S")){
			registers.write(com.arg1(),1+registers.read(com.arg1()));
			current_line++;
		}
		if(com.type.equals("T")){
			registers.write(com.arg2(),registers.read(com.arg1()));
			current_line++;
		}
		if(com.type.equals("J")){
			if(registers.read(com.arg1())==registers.read(com.arg2())){
				current_line=com.arg3();
			}else{
				current_line++;
			}
		}
	}
	
	

}
