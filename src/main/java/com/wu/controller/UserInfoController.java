package com.wu.controller;

import com.wu.bean.UserInfo;
import com.wu.bean.UserList;
import com.wu.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 作者    吴振涛
 * 描述
 * 创建时间 2018年04月22日
 * 任务时间
 * 邮件时间
 */
@Controller
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @RequestMapping( "/genUser" )
    @ResponseBody
    public Map<String, Object> genUser(@RequestBody UserInfo userInfo){
        return userInfoService.insertOne(userInfo.getName(),userInfo.getGender(),userInfo.getPhone(),userInfo.getBirthDate(),userInfo.getCalendar(),userInfo.getNickName(),userInfo.getAvatarUrl());
    }
//    public Map<String, Object> genUser(String name,String gender,String phone,String birthday,String calender,String nickName,String avatarUrl){
//        return userInfoService.insertOne(name,gender,phone,birthday,calender,nickName,avatarUrl);
//    }
    @RequestMapping( "/getUserList" )
    @ResponseBody
    public List<UserList> getUserList(@RequestBody(required = false) UserInfo userInfo){
        System.out.println(userInfo);
        return userInfoService.selectAllByDevice(userInfo.getNickName(),userInfo.getAvatarUrl());
    }

    @RequestMapping( "/getNesPerson" )
    @ResponseBody
    public List<UserInfo> getNesPerson() throws ParseException {
        return userInfoService.selectNewPerson();
    }

}
