package com.wu.service;

import com.wu.bean.DeviceInfo;
import com.wu.bean.UserInfo;
import com.wu.dao.DeviceInfoDao;
import com.wu.dao.UserInfoDao;
import com.wu.util.LunarUtil;
import com.wu.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * 作者    吴振涛
 * 描述
 * 创建时间 2018年04月22日
 * 任务时间
 * 邮件时间
 */
@Service
public class UserInfoService {
    Logger logger= LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
     private UserInfoDao userInfoDao;
    @Autowired
     private DeviceInfoDao deviceInfoDao;
    private String[] mouths={"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private String[] days={"初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
            "十一","十二","十三","十四","十五","十六","十七","十八","十九",
            "廿十","廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九",
            "三十"};
    @Transactional
    public Map<String,Object> insertOne(String username, String gender, String phone, String dateValue,String calendar,String nickName,String avatarUrl){
        Map<String,Object> map=new HashMap<>();
        try{
            logger.info(username+","+gender+","+phone+","+dateValue+","+calendar);
            String md5Code= MD5Utils.MD5(nickName+avatarUrl);
            DeviceInfo deviceInfo=new DeviceInfo();
            int hasDevice = deviceInfoDao.selectRelationNum(md5Code);
            if (hasDevice==0){
                deviceInfo.setMd5Code(md5Code);
                deviceInfo.setGrade("1");
                deviceInfo.setNickName(nickName);
                deviceInfo.setAvatarUrl(avatarUrl);
                deviceInfoDao.genDeviceRelation(deviceInfo);
            }
//            插入用户信息------------------------------------
            UserInfo userInfo=new UserInfo();
            userInfo.setMd5Code(md5Code);
            userInfo.setName(username);
            if ("1".equals(gender)) {
                userInfo.setGender("女");
            }else if ("0".equals(gender)){
                userInfo.setGender("男");
            }
            userInfo.setPhone(phone);
            String birthday="";
//            ----------------------------------------------
//            1是农历  0是阳历
           try{
               if (calendar.equals("1")){
                   String[] dayStrs = dateValue.split("@");
                   int mouthNum=Integer.parseInt(dayStrs[0]);
                   int dayNum=Integer.parseInt(dayStrs[1]);
                   birthday=mouths[mouthNum-1]+"月"+days[dayNum-1];
               }else {
                   String[] dayStrs = dateValue.split("@");
                   int mouthNum=Integer.parseInt(dayStrs[0]);
                   int dayNum=Integer.parseInt(dayStrs[1]);
                   birthday=(mouthNum)+"-"+(dayNum);
               }
           }catch (Exception e){
               logger.info("获得日期失败"+dateValue+" "+calendar);
           }


//            ----------------------------------------------
            userInfo.setBirthDate(birthday);
            int num = userInfoDao.selectNameNum(username, phone);
            if (num>1){
                map.put("code",1);
                map.put("message",username+"信息存在多份明显有误，联系管理员删除");
                return map;
            }else if(num==1){
                userInfoDao.updateInfo(userInfo);
                map.put("code",0);
                map.put("message",username+"信息更新成功");
                return map;
            }else {
                userInfoDao.genTask(userInfo);
                map.put("code",0);
                map.put("message",username+"添加信息成功");
                //插入设备关联信息------------------------------------







                return map;
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        map.put("code",1);
        map.put("message","插入失败");
        return map;
    }
    public List<UserInfo> selectAllByDevice(String nickName,String avatarUrl){
        String md5Code= MD5Utils.MD5(nickName+avatarUrl);
       return userInfoDao.selectAllByDevice(md5Code);
    }

    public List<String> selectCurrentName() throws ParseException {
        List<String> list=new ArrayList<>();
        String currtDay = LunarUtil.getDayStr();
        String[] allDayStr = currtDay.split("@");
        list.addAll(userInfoDao.getBirthdayPerson(allDayStr[0],allDayStr[1]));
        return list;
    }
}