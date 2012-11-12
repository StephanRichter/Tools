package tools.srsoftware;

public class StringWrapper {
	private String s;
	
	public StringWrapper(String s) {
		this.s=s;
  }
	
	public String get(){
		return s;
	}
	
	public String toString(){
		return get();
	}
}
