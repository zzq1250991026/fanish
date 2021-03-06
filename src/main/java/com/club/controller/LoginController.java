package com.club.controller;

import com.club.bean.Student;
import com.club.service.AdminService;
import com.club.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by cyan on 16/6/22.
 */

@Controller
public class LoginController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/login")
    public String userLogin(@RequestParam String id, @RequestParam String pwd, HttpServletRequest req) {
        Student student = null;
        if (id.length() > 0 && id.length() < 20 && pwd.length() > 0 && pwd.length() < 20) {
            student = studentService.login(id, pwd);
        }
        if (student != null) {
            req.getSession().setAttribute("user", student.getName());
            req.getSession().setAttribute("userId", student.getId());
//            req.getSession().setAttribute("msg","登录成功!欢迎您 "+student.getName()+"!");
        } else {
            req.getSession().setAttribute("msg", "登录失败!用户名或密码错误!");
        }
        return "redirect:/index";
    }

    @RequestMapping("/logout")
    public String userLogout(HttpServletRequest req) {
        req.getSession().setAttribute("user", null);
        req.getSession().setAttribute("userId", null);
        return "redirect:/index";
    }

    @RequestMapping("/changePwd")
    public String changePwd(HttpServletRequest req, @RequestParam String old,
                            @RequestParam String newpwd, @RequestParam String newagain) {

        String stuId = (String) req.getSession().getAttribute("userId");
        if (newpwd.equals(newagain) && studentService.changePwd(stuId, old, newpwd)) {
            req.getSession().setAttribute("msg", "修改成功!");
        } else {
            req.getSession().setAttribute("msg", "修改失败!");
        }
        return "redirect:/index";
    }
//
//    @RequestMapping("/adminLogin")
//    public String adminLogin(@RequestParam String username, @RequestParam String pwd, HttpServletRequest req){
//        if (username.length() > 0 && username.length() < 20 && pwd.length() > 0 && pwd.length() < 20) {
//            if(adminService.login(username, pwd)){
//                Student student=studentService.selectStudentById(username);
//                if(student.getRole().equals(1)){
//
//                }
//                req.getSession().setAttribute("id",username);
//                return "redirect:adminIndex";
//            }
//        }
//        return "adminLogin";
//    }
//登录
    @RequestMapping("/adminLogin")
    public ModelAndView adminLogin(@RequestParam String id, @RequestParam String pwd,HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv =new ModelAndView();
        if (id.length() > 0 &&id.length() < 20 && pwd.length() > 0 && pwd.length() < 20) {
            if(adminService.login(id, pwd)){
                Student student=studentService.selectStudentById(id);
                if(student.getRole()==1){
                    request.getSession().setAttribute("user", student.getName());
                    request.getSession().setAttribute("userId", student.getId());
                    mv.setViewName("redirect:index");
                    return mv;
                }else if(student.getRole()==2){
                    request.getSession().setAttribute("id",student.getId());
                    mv.setViewName("admin");
                    return mv;
                }
                //教师页面
                else if(student.getRole().equals(3))
                {
                    mv.setViewName("adminLogin");
                    return mv;
                }
            }
        }
        mv.setViewName("adminLogin");
        return mv;
    }

}
