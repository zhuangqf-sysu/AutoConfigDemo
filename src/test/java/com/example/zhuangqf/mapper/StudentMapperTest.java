package com.example.zhuangqf.mapper;

import com.example.zhuangqf.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by zhuangqf on 11/18/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentMapperTest {

    @Autowired
    StudentMapper studentMapper;

    @Test
    public void findAllStudents(){
        List<Student> studentList = studentMapper.findAllStudents();
        for (Student student:studentList){
            System.out.println(student.getId()+" "
                    +student.getName()+" "+student.getAge());
        }
    }

    @Test
    public void findById(){
        Student student = studentMapper.findById(2);
        if(student!=null)
            System.out.println(student.getId()+" "
               +student.getName()+" "+student.getAge());
    }

    @Test
    public void insert(){
        Student student = new Student();
        student.setName("test");
        student.setAge(20);
        studentMapper.insert(student);
        findAllStudents();
    }

    @Test
    public void update(){
        Student student = studentMapper.findById(3);
        student.setName("update");
        studentMapper.update(student);
        System.out.println(student.getId()+" "
                +student.getName()+" "+student.getAge());
    }

    @Test
    public void delete(){
        int result = studentMapper.deleteById(1);
        System.out.println(result);
        findAllStudents();
    }

}
