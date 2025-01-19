package com.elan.sample;

import java.util.HashMap;

public class HashMapExample1 {

	public static void main(String[] args) {
		HashMap<String,String> map1 = new HashMap<>();
		map1.put(new String("a"),"a");
		map1.put(new String("a"),"b");
		System.out.println(" Size : "+map1.size()+" values : "+map1);

		
		HashMap<Emp,String> map2 = new HashMap<>();
		map2.put(new Emp("a"),"a");
		map2.put(new Emp("a"),"b");
		System.out.println(" Size : "+map2.size()+" values : "+map2);
	}

}
