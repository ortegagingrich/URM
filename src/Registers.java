import java.util.ArrayList;

//class of register collections
public class Registers {
	
	private ArrayList<Integer[]> entries;
	
	public Registers(){
		entries=new ArrayList<Integer[]>();
	}
	
	public void write(int register,int value){
		for(Integer[] entry:entries){
			if(entry[0]==register){
				if(value==0){
					entries.remove(entry);
					return;
				}else{
					entry[1]=value;
					return;
				}
			}
		}
		//make new entry
		Integer[] a={register,value};
		entries.add(a);
	}
	
	public int read(int register){
		for(Integer[] entry:entries){
			if(entry[0]==register){
				return entry[1];
			}
		}
		return 0;
	}
	
	public void clear(){
		entries.clear();
	}
	

}
