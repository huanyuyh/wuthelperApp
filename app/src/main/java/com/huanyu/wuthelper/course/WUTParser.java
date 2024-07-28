package com.huanyu.wuthelper.course;


import com.huanyu.wuthelper.course.Course;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import com.huanyu.wuthelper.course.Parser;

public class WUTParser extends Parser {

    Document document;
    Element base_table;
    Elements base_info;
    public WUTParser(@NotNull String source) {
        super(source);
        document = Jsoup.parse(source);
        base_table = document.getElementById("xqkb");
        base_info = document.getElementsByClass("table-box");

    }

    @NotNull
    @Override
    public List<Course> generateCourseList() {
        List<Course> courses = new ArrayList<>();
        //找到学期课表
        Element base_table = document.getElementById("xqkb");
        //找到老师信息
        Elements base_info = document.getElementsByClass("table-box");
        //课程表为一个表格横向排列一个td为一格
        Elements base_class = base_table.select("td");
        int i = 0;
        Element class_info = null;
        for (Element element : base_info) {
            String line = element.toString();
            //System.out.println("line"+line);
            //用空白找到有效的教师信息
            if(line.indexOf("空白")>-1){
                class_info = element;
            }
        }
        //System.out.println(class_info);
        //用tr找到有效信息
        Elements classinfo =class_info.select("tr");
        //删除前两个无效信息
        classinfo.remove(0);
        classinfo.remove(0);
        //System.out.println(classinfo);
        for (Element element : classinfo) {
            String line = element.toString();
            String name = INSubString(line,"<td>","</td>");
            //删除所有空格和回车
            name = name.replaceAll("\\s", "");
            line = STRSubString(line,"</td>");
            String point = INSubString(line,"<td>","</td>");
            line = STRSubString(line,"</td>");
            String QQ = INSubString(line,"<td>","</td>");
            line = STRSubString(line,"</td>");
            String Week = INSubString(line,"<td>","</td>");
            line = STRSubString(line,"</td>");
            String teacher = INSubString(line,"<td>","</td>");
            line = STRSubString(line,"</td>");
//            System.out.println(name);
//            System.out.println(point);
//            System.out.println(QQ);
//            System.out.println(Week);
//            System.out.println(teacher);
            //构建临时表
            if(name!=null||point!=null){
                Course course = new Course(name,0,"",teacher,0,0,
                        0,0,0,Float.valueOf(point),QQ,"","");
                courses.add(course);
            }

        }
        List<Course> newCourses = new ArrayList<>();
        for (Element baseClass : base_class) {
            //一个text为一个课程
            String line = baseClass.toString();
            if(line.indexOf("text-align")>-1){
                i++;
                if(i==8)i=1;
                //一个div为一个课程信息
                Elements oneclass = baseClass.select("div");
                for (Element element : oneclass) {
                    String newline = element.toString();
//                    System.out.println(newline);
                    //解析头
                    if(newline.indexOf("margin-top")>-1){
                        //System.out.println(line);
                        if(newline.indexOf("_blank")>-1){
                            String name = INSubString(newline,"_blank\">"," <p>");
                            name = name.replaceAll("\\s", "");
//                            System.out.println("name"+name);
                            String position = INSubString(newline,"<p>@","</p>");
//                            System.out.println("position"+position);
                            newline = STRSubString(newline,"</p>");
                            String week = INSubString(newline,"<p>","</p>");
//                            System.out.println("week"+week);
                            newline = STRSubString(newline,"</p>");
                            String qq = INSubString(newline,"<p>","</p>");
                            if(qq!=null){
//                                System.out.println("qq"+qq);
                            }else {
                                qq="";
                            }
//                            System.out.println("第"+i +"天");
                            for (Course course : courses) {
//                                System.out.println(course.getName());
//                                System.out.println(name);
                                if(name.contains(course.getName())){
//                                    System.out.println("yes");
//                                    System.out.println(name);
//                                    System.out.println(name);
                                    while (week.indexOf("周")>-1){
                                        int startWeek = 0;
                                        if(INSubString(week,"第","-")!=null){
                                            startWeek = Integer.valueOf(INSubString(week,"第","-"));
                                        }else {
                                            startWeek = Integer.valueOf(INSubString(week,",","-"));
                                        }

                                        int endWeek = Integer.valueOf(INSubString(week,"-","周"));
                                        String day = STRSubString(week,"周");
                                        int startNode = Integer.valueOf(REINSubString(day,"(","-"));
                                        int endNode = Integer.valueOf(REINSubString(day,"-","节"));
                                        week = STRSubString(week,"周");
                                        //System.out.println(week);
                                        //合并新表
                                        Course newCourse = new Course(name,i,position,course.getTeacher(),startNode,endNode,
                                                startWeek,endWeek,0,course.getCredit() ,qq,"","");
                                        newCourses.add(newCourse);
//                                        System.out.println(newCourse);
                                    }

                                }
                            }

                        }
                    }
                }

            }

        }
//        for (Course newCours : newCourses) {
//            System.out.println(newCours);
//        }
        // ("text-align");
        //System.out.println(base_class);
        return newCourses;
    }

    public static String INSubString(String todo,String start,String end){
        if(todo==null||todo.indexOf(start)==-1)return null;
        todo = todo.substring(todo.indexOf(start)+start.length(),todo.indexOf(end));

        return todo;
    }
    public static String STRSubString(String todo,String start){
        if(todo==null||todo.indexOf(start)==-1)return null;
        todo = todo.substring(todo.indexOf(start)+start.length(),todo.length());
        return todo;
    }
    public static String ENDSubString(String todo,String end){
        if(todo==null||todo.indexOf(end)==-1)return null;
        todo = todo.substring(0,todo.indexOf(end));
        return todo;
    }
    public static String REENDSubString(String todo,String end){
        if(todo==null||todo.indexOf(end)==-1)return null;
        todo = todo.substring(todo.lastIndexOf(end),todo.length());
        return todo;
    }
    public static String REINSubString(String todo,String start,String end){
        if(todo==null||todo.indexOf(start)==-1)return null;
        todo = todo.substring(todo.lastIndexOf(start)+start.length(),todo.lastIndexOf(end));

        return todo;
    }

}
