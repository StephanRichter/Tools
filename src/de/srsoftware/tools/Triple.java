package tools.srsoftware;
import java.util.TreeMap;

public class Triple<T1,T2,T3> {
	TreeMap<T1,TreeMap<T2,T3>> map=null;
	public Triple(){
		map=new TreeMap<T1,TreeMap<T2,T3>>(new ObjectComparator());
	}
	
	public void add(T1 key1,T2 key2,T3 value){
		if (map.containsKey(key1)){
		  map.get(key1).put(key2, value);	
		} else {
			TreeMap<T2,T3> subMap=new TreeMap<T2,T3>(new ObjectComparator());
			subMap.put(key2, value);
			map.put(key1, subMap);
		}
	}
	
	public T3 get(T1 key1,T2 key2){
		return map.get(key1).get(key2);
	}
}
