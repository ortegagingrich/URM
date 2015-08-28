//class of variables
public class U_Variable {
	
	public String identifier;
	public int value;
	
	//URM register index to store this variable; not assigned until the URM program object is created.
	public int index;
	
	public U_Variable(String id){
		identifier=id;
		value=0;
	}
	
	public U_Variable(String id,int v){
		identifier=id;
		value=v;
	}
	
	
}
