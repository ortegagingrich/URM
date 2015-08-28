import java.util.ArrayList;
import java.util.Arrays;

public class U_Compiler {
	
	public static ArrayList<String> control_structures;
	
	public int cumline;
	public int localenv;
	
	public U_Compiler(){
		String[] a={"for","while","if"};
		control_structures=new ArrayList<String>(Arrays.asList(a));
	}
	
	public URM_Program compile(U_Program program){
		//make URM program for eventual output
		URM_Program p=new URM_Program();
		
		//first, dedicate appropriate registers for global variables
		for(int i=0;i<program.variables.size();i++){
			U_Variable var=program.variables.get(i);
			if(var.identifier.contains("tracker")){
				var.index=9999;
			}else{
				var.index=200001+i;//experimental change
			}
		}
		//add commands from program
		cumline=1;//cumulative number of URM lines used; at any given step cumline is the current line also.
		localenv=0;//current environment number for local variables
		for(U_Command command:program.commands){
			add_code(command,p);
		}
		
		
		
		return p;
	}
	
	public void add_code(U_Command command,URM_Program p){
		//branch based on command type
		if(command instanceof U_Parameter){
			int pn=1;
			for(U_Variable var:((U_Parameter)command).vars){
				p.add_command("T",pn,var.index);
				pn++;
				cumline++;
			}
		}
		if(command instanceof U_Succeed){
			p.add_command("S",((U_Succeed)command).base.index);
			cumline++;
		}
		if(command instanceof U_Set){
			int lim=((U_Set)command).from;
			int reg=((U_Set)command).to.index;
			p.add_command("Z",reg);
			cumline++;
			for(int i=0;i<lim;i++){
				p.add_command("S",reg);
				cumline++;
			}
		}
		if(command instanceof U_Assignment){
			int regf=((U_Assignment)command).from.index;
			int regt=((U_Assignment)command).to.index;
			p.add_command("T",regf,regt);
			cumline++;
		}
		if(command instanceof U_Return){
			int reg=((U_Return)command).from.index;
			p.add_command("T",reg,1);
			p.add_command("J",1,1,0);
			cumline+=2;
		}
		//good luck
		if(command instanceof U_Call){
			//first transfer in inputs
			ArrayList<U_Variable> inputs=((U_Call)command).inputs;
			ArrayList<U_Variable> args=((U_Call)command).arguments;
			for(int i=0;i<inputs.size();i++){
				p.add_command("T",inputs.get(i).index,args.get(i).index);
				cumline++;
			}
			//next read in main block
			U_Block block=((U_Call)command).block;
			for(U_Command com:block.commands){
				add_code(com,p);
			}
		}
		if(command instanceof U_For){
			//first take care of the local variables
			U_Block block=((U_For)command).block;
			block.local_index=15000+(100*localenv);//test change
			localenv++;
			int i=0;
			for(U_Variable var:block.local_variables){
				var.index=block.local_index+i;
				i++;
			}
			
			//now that locals are assigned, get relevant globals
			i=((U_For)command).forindex.index;
			int l=((U_For)command).lower.index;
			int u=((U_For)command).upper.index;
			
			int n=cumline;//for reference later
			p.add_command("T",l,i);
			p.add_jump_tentative(i,u);
			cumline+=2;
			//then block starts
			for(U_Command com:block.commands){
				add_code(com,p);
				//System.out.println(p.length());
			}
			int m=cumline;
			p.finalize_jump(n+1,m+2);
			p.add_command("S",i);
			p.add_command("J",1,1,n+1);
			cumline+=2;
		}
		if(command instanceof U_While){
			//first allocate registers for local variables
			U_Block block=((U_While)command).block;
			block.local_index=15000+(100*localenv);//test change
			localenv++;
			int i=0;
			for(U_Variable var:block.local_variables){
				var.index=block.local_index+i;
				i++;
			}
			
			//preamble
			int left=((U_While)command).left.index;
			int right=((U_While)command).right.index;
			
			int m=cumline;
			p.add_jump_tentative(left,right);
			cumline++;
			//add main block
			for(U_Command com:block.commands){
				add_code(com,p);
			}
			//finish
			int n=cumline;
			p.finalize_jump(m,n+1);
			p.add_command("J",1,1,m);
			cumline++;
		}
		
		if(command instanceof U_If){
			//first allocate registers for local variables
			//main block
			U_Block main_block=((U_If)command).main_block;
			main_block.local_index=15000+(100*localenv);//test change
			localenv++;
			int i=0;
			for(U_Variable var:main_block.local_variables){
				var.index=main_block.local_index+i;
				i++;
			}
			//else block
			U_Block else_block=((U_If)command).else_block;
			else_block.local_index=15000+(100*localenv);//test change
			localenv++;
			i=0;
			for(U_Variable var:else_block.local_variables){
				var.index=else_block.local_index+i;
				i++;
			}
			
			
			//preamble
			int left=((U_If)command).left.index;
			int right=((U_If)command).right.index;
			
			int n=cumline;
			p.add_command("J",left,right,n+2);
			p.add_jump_tentative(1,1);
			cumline+=2;
			//add main block
			for(U_Command com:main_block.commands){
				add_code(com,p);
			}
			int m=cumline;
			p.finalize_jump(n+1,m+1);
			p.add_jump_tentative(1,1);
			cumline++;
			//add else block
			for(U_Command com:else_block.commands){
				add_code(com,p);
			}
			int k=cumline;
			p.finalize_jump(m,k);
		}
		if(command instanceof U_Ifless){
			//first allocate registers for local variables
			//main block
			U_Block main_block=((U_Ifless)command).main_block;
			main_block.local_index=15000+(100*localenv);//test change
			localenv++;
			int i=0;
			for(U_Variable var:main_block.local_variables){
				var.index=main_block.local_index+i;
				i++;
			}
			//else block
			U_Block else_block=((U_Ifless)command).else_block;
			else_block.local_index=15000+(100*localenv);//test change
			localenv++;
			i=0;
			for(U_Variable var:else_block.local_variables){
				var.index=else_block.local_index+i;
				i++;
			}
			
			//preamble
			int left=((U_Ifless)command).left.index;
			int right=((U_Ifless)command).right.index;
			
			int n=cumline;
			p.add_command("T",left,100);
			p.add_command("T",right,101);
			p.add_jump_tentative(101,left);
			p.add_command("S",100);
			p.add_command("S",101);
			p.add_command("J",100,right,n+7);
			p.add_command("J",1,1,n+2);
			cumline+=7;
			//add main block
			for(U_Command com:main_block.commands){
				add_code(com,p);
			}
			int m=cumline;
			p.finalize_jump(n+2,m+1);
			p.add_jump_tentative(1,1);
			cumline++;
			//add else block
			for(U_Command com:else_block.commands){
				add_code(com,p);
			}
			int k=cumline;
			p.finalize_jump(m,k);
		}
		if(command instanceof U_Iflesseq){
			//first allocate registers for local variables
			//main block
			U_Block main_block=((U_Iflesseq)command).main_block;
			main_block.local_index=15000+(100*localenv);//test change
			localenv++;
			int i=0;
			for(U_Variable var:main_block.local_variables){
				var.index=main_block.local_index+i;
				i++;
			}
			//else block
			U_Block else_block=((U_Iflesseq)command).else_block;
			else_block.local_index=15000+(100*localenv);//test change
			localenv++;
			i=0;
			for(U_Variable var:else_block.local_variables){
				var.index=else_block.local_index+i;
				i++;
			}
			
			//preamble
			int left=((U_Iflesseq)command).left.index;
			int right=((U_Iflesseq)command).right.index;
			
			int n=cumline;
			p.add_command("T",left,100);
			p.add_command("T",right,101);
			p.add_command("J",100,right,n+7);
			p.add_command("S",100);
			p.add_command("S",101);
			p.add_jump_tentative(101,left);
			p.add_command("J",1,1,n+2);
			cumline+=7;
			for(U_Command com:((U_Iflesseq)command).main_block.commands){
				add_code(com,p);
			}
			int m=cumline;
			p.finalize_jump(n+5,m+1);
			p.add_jump_tentative(1,1);
			cumline++;
			//add else block
			for(U_Command com:((U_Iflesseq)command).else_block.commands){
				add_code(com,p);
			}
			int k=cumline;
			p.finalize_jump(m,k);
			
		}
	}

}
