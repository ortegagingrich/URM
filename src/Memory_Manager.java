import java.util.ArrayList;

//keeps track of which registers have been assigned to variables
public class Memory_Manager {
	
	private int reserved; //the number or registers reserved for input/output
	private ArrayList<Integer> allocated; //registers which have been allocated
	private ArrayList<Integer> released; //registers which have been released
	
	public Memory_Manager(int nargs){
		reserved=nargs;
		allocated=new ArrayList<Integer>();
		released=new ArrayList<Integer>();
	}
	
	//get an unused register (priority given to those released recently
	public int malloc(){
		//if there are any released registers, use the first of those
		if(released.size()>0){
			int reg=released.get(0);
			released.remove(0);
			return reg;
		}
		
		//otherwise, get the next largest
		int next=reserved+allocated.size()+1;
		allocated.add(next);
		return next;
	}
	
	
	//release an allocated register
	public void free(int reg){
		if(allocated.remove(Integer.valueOf(reg))){
			released.add(reg);
		}
	}
	
	
}
