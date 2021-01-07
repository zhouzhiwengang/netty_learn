package com.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.stream.Java8GroupBy.Employee;
import com.stream.entity.Student;

public class Java8PartitioningBy {
	
	
	/**
	 * partitioningBy特殊的分组之Integer 类型
	 */
	public void partitioningByInteger(){
		// 数据初始化
		List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
		
		Map<Boolean,List<Integer>> map = list.stream().collect(Collectors.partitioningBy(item ->{
			return item > 6;
		}));
		
		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
			v.forEach(item -> {
				System.out.println("item = " + item);
			});
		});
	}
	
	/**
	 * partitioningBy特殊的分组之Object 类型
	 */
	public void partitioningByObject(){
		// 数据初始化
		List<Integer> sexs = Arrays.asList(1, 2);
		List<Integer> ages =  Arrays.asList(4,5,6,7);
		
		// 数据集合
		List<Student> students = new ArrayList<Student>();
		for (int i = 0; i < 10; i++) {
			Random random = new Random();
			Integer sexIndex = random.nextInt(2);
			Integer ageIndex = random.nextInt(4);
			Student student = new Student(""+i, "姓名" + i, sexs.get(sexIndex), ages.get(ageIndex), new Date());
			students.add(student);
		}
		
		// 特殊分组
		Map<Boolean,List<Student>> map = students.stream().collect(Collectors.partitioningBy(item ->{
			Student student = item;
			return student.getAge() == 5;
		}));
		
		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
			v.forEach(item -> {
				System.out.println("item = " + item);
			});
		});
		
		// 特殊分组后，List拼接
		List<Student> container = new ArrayList<Student>();
	    map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList())
	      	.stream().forEach(item->{
	      		container.addAll(item);
	      	});
	    if(container != null && container.size() > 0){
	    	container.stream().forEach(item ->{
				System.out.println("items = " + item);
			});
	    }
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Java8PartitioningBy instance = new Java8PartitioningBy();
		instance.partitioningByInteger();
		instance.partitioningByObject();
	}

}
