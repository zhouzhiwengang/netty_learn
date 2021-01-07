package com.stream.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {

	private String id;
	private String name;
	private Integer sex;
	private Integer age;
	private Date birthDay;
}
