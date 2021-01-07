package com.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Java 8 Stream 之groupingBy 分组讲解
 * 
 * @author zzg
 *
 */
public class Java8GroupBy {

	List<Employee> employees = new ArrayList<Employee>();

	/**
	 * 数据初始化
	 */
	public void init() {
		List<String> citys = Arrays.asList("湖南", "湖北", "江西", "广西 ");
		for (int i = 0; i < 10; i++) {
			Random random = new Random();
			Integer index = random.nextInt(4);
			Employee employee = new Employee(citys.get(index), "姓名" + i, (random.nextInt(4) * 10 - random.nextInt(4)),
					(random.nextInt(4) * 1000 - random.nextInt(4)));
			employees.add(employee);
		}
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list
	 */
	public void groupingByCity() {
		Map<String, List<Employee>> map = employees.stream().collect(Collectors.groupingBy(Employee::getCity));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list统计count
	 */
	public void groupingByCount() {
		Map<String, Long> map = employees.stream()
				.collect(Collectors.groupingBy(Employee::getCity, Collectors.counting()));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list并计算分组年龄平均值
	 */
	public void groupingByAverage() {
		Map<String, Double> map = employees.stream()
				.collect(Collectors.groupingBy(Employee::getCity, Collectors.averagingInt(Employee::getAge)));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list并计算分组销售总值
	 */
	public void groupingBySum() {
		Map<String, Long> map = employees.stream()
				.collect(Collectors.groupingBy(Employee::getCity, Collectors.summingLong(Employee::getAmount)));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});

		// 对Map按照分组销售总值逆序排序
		Map<String, Long> sortedMap = new LinkedHashMap<>();
		map.entrySet().stream().sorted(Map.Entry.<String, Long> comparingByValue().reversed())
				.forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));

		sortedMap.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list并通过join操作连接分组list中的对象的name 属性使用逗号分隔
	 */
	public void groupingByString() {
		Map<String, String> map = employees.stream().collect(Collectors.groupingBy(Employee::getCity,
				Collectors.mapping(Employee::getName, Collectors.joining(", ", "Names: [", "]"))));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list,将List转化为name的List
	 */
	public void groupingByList() {
		Map<String, List<String>> map = employees.stream().collect(
				Collectors.groupingBy(Employee::getCity, Collectors.mapping(Employee::getName, Collectors.toList())));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
			v.stream().forEach(item -> {
				System.out.println("item = " + item);
			});
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,按城市分组list,将List转化为name的Set
	 */
	public void groupingBySet() {
		Map<String, Set<String>> map = employees.stream().collect(
				Collectors.groupingBy(Employee::getCity, Collectors.mapping(Employee::getName, Collectors.toSet())));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
			v.stream().forEach(item -> {
				System.out.println("item = " + item);
			});
		});
	}

	/**
	 * 使用java8 stream groupingBy操作,通过Object对象的成员分组List
	 */
	public void groupingByObject() {
		Map<Manage, List<Employee>> map = employees.stream().collect(Collectors.groupingBy(item -> {
			return new Manage(item.getName());
		}));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

	/**
	 * 使用java8 stream groupingBy操作, 基于city 和name 实现多次分组
	 */
	public void groupingBys() {
		Map<String, Map<String, List<Employee>>> map = employees.stream()
				.collect(Collectors.groupingBy(Employee::getCity, Collectors.groupingBy(Employee::getName)));

		map.forEach((k, v) -> {
			System.out.println(k + " = " + v);
			v.forEach((i, j) -> {
				System.out.println(i + " = " + j);
			});
		});
	}

	/**
	 * 使用java8 stream groupingBy操作, 基于Distinct 去重数据
	 */
	public void groupingByDistinct() {
		List<Employee> list = employees.stream().filter(distinctByKey(Employee :: getCity))
				.collect(Collectors.toList());;

		list.stream().forEach(item->{
			System.out.println("city = " + item.getCity());
		});
		
		
	}

	/**
	 * 自定义重复key 规则
	 * @param keyExtractor
	 * @return
	 */
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Java8GroupBy instance = new Java8GroupBy();
		instance.init();
		instance.groupingByCity();
		instance.groupingByCount();
		instance.groupingByAverage();
		instance.groupingBySum();
		instance.groupingByString();
		instance.groupingByList();
		instance.groupingBySet();
		instance.groupingByObject();
		instance.groupingBys();
		instance.groupingByDistinct();

	}

	class Employee {
		private String city;
		private String name;
		private Integer age;
		private Integer amount;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

		public Employee(String city, String name, Integer age, Integer amount) {
			super();
			this.city = city;
			this.name = name;
			this.age = age;
			this.amount = amount;
		}

		public Employee() {
			super();
		}
	}

	class Manage {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Manage(String name) {
			super();
			this.name = name;
		}

		public Manage() {
			super();
		}
	}

}
