package antiesys.antiepidemic.controller;

import antiesys.antiepidemic.pojo.*;
import antiesys.antiepidemic.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @author 李伦霆 秦海祺
 */
@Controller
@RequestMapping("/admin")
@SessionAttributes(value = {"manager","nummap","admuser","goodslist","good","userlist","msglist","adminmessage"})
public class AdminHandler {

    @Autowired
    AdminService adminService;
    List<SignIn> signInList;
    List<Report> reportList;

    /**
     * 管理员签到页面跳转
     * @return 管理员签到页面
     */
    @RequestMapping("/adminSignInPage")
    public String adminSignInPage(){
        return "views/Manager/ManagerSignInPage";
    }

    /**
     * 管理员签到
     * @param temperature 体温
     * @return 签到结果
     */
    @RequestMapping(path="/adminSignIn", produces="text/html;charset=utf-8")
    @ResponseBody
    public String AdminSignIn(@RequestParam(name = "temperature") String temperature){
        boolean isSignIn = adminService.AdminSignIn(temperature);

        if(!isSignIn) {
            return "签到失败";
        }

        return "签到成功";
    }

    /**
     * 添加物品
     * @param goodsName 物品名称
     * @param goodsNum 物品数量
     * @param goodsSource 物品来源
     * @param model 模型
     * @return 物资显示页面
     */
    @RequestMapping(path="/addGoods")
    public String AddGoods(@RequestParam(name = "goodsName") String goodsName, @RequestParam(name = "goodsNum") Integer goodsNum,@RequestParam(name = "goodsSource") String goodsSource,Model model){


        Goods goods=new Goods();
        if(goodsName!=null&&!goodsName.equals("")) {
            goods.setGoodsName(goodsName);
        }
        if(goodsNum!=null) {
            goods.setGoodsNum(goodsNum);
        }
        if(goodsSource!=null&&!goodsSource.equals("")) {
            goods.setGoodsSource(goodsSource);
        }

        boolean isAdd = adminService.AddGoods(goods);

        if(!isAdd) {
            return "views/Manager/ManagerMaterialInformationDisplayPage";
        }

        List<Goods> mygoods = adminService.FindGoodsAll();
        List<Goods> newGoodsList = new ArrayList<>();
        for (int i = 0; i < mygoods.size(); i++) {
            newGoodsList.add(mygoods.get(i));
        }
        model.addAttribute("goodslist",newGoodsList);

        return "views/Manager/ManagerMaterialInformationDisplayPage";
    }

    /**
     * 删除物品
     * @param goodsId 物品ID
     * @param model 模型
     * @return 物资信息显示界面
     */
    @RequestMapping(path="/deleteGoods")
    public String DeleteGoods(@RequestParam(name = "goodsId") int goodsId, Model model){

        boolean isDelete = adminService.DeleteGoods(goodsId);
        List<Goods> goods = adminService.FindGoodsAll();

        List<Goods> newGoodsList = new ArrayList<>();

        for (int i = 0; i < goods.size(); i++) {
            newGoodsList.add(goods.get(i));
        }
        model.addAttribute("goodslist",newGoodsList);

        if(!isDelete)
            return "views/Manager/ManagerMaterialInformationDisplayPage";


        return "views/Manager/ManagerMaterialInformationDisplayPage";
    }

    /**
     * 修改物品
     * @param goodsName 物品名称
     * @param goodsNum 物品数量
     * @param goodsSource 物品来源
     * @param intime 入库时间
     * @param model 模型
     * @return 物品信息显示界面
     */
    @RequestMapping(path="/changeGoods")
    public String ChangeGoods(@RequestParam(name = "goodsName") String goodsName,@RequestParam(name = "goodsNum") Integer goodsNum,@RequestParam(name = "goodsSource") String goodsSource,@RequestParam(name = "intime") String intime, Model model){
        Goods goods=(Goods)model.getAttribute("good");
        if(!goodsName.equals("")&&goodsName!=null) {
            goods.setGoodsName(goodsName);
        }
        if(!goodsSource.equals("")&&goodsSource!=null) {
            goods.setGoodsSource(goodsSource);
        }
        if(intime.equals("入库")) {
            Integer i=goods.getGoodsNum()+goodsNum;
            goods.setGoodsNum(i);
            goods.setGoodsInTime(new Date());
        } else {
            Integer i=goods.getGoodsNum()-goodsNum;
            goods.setGoodsNum(i);
            goods.setGoodsOutTime(new Date());
        }
        boolean isChange = adminService.ChangeGoods(goods);

        if(!isChange)
            return "views/Manager/ManagerMaterialInformationDisplayPage";

        List<Goods> mygoods = adminService.FindGoodsAll();
        List<Goods> newGoodsList = new ArrayList<>();
        for (int i = 0; i < mygoods.size(); i++) {
            newGoodsList.add(mygoods.get(i));
        }
        model.addAttribute("goodslist",newGoodsList);

        return "views/Manager/ManagerMaterialInformationDisplayPage";
    }

    /**
     * 查询一个用户
     * @param userId 用户ID
     * @param request request
     * @param model 模型
     * @return 用户详细信息显示界面
     */
    @RequestMapping(path="/findUseOne")
    public String FindUserOne(@RequestParam(name = "userId") Integer userId, HttpServletRequest request, Model model){

        Users user = adminService.FindUserOne(userId);

        if(user == null) {
            return "ErrorPage";
        }

        model.addAttribute("admuser",user);

        return "views/Manager/ManagerViewUserInformationPage-Details";
    }

    /**
     * 修改用户信息
     * @param userName 用户名
     * @param userAge 年龄
     * @param userPhone 电话
     * @param userSex 性别
     * @param model 模型
     * @return 用户信息界面
     */
    @RequestMapping(path="/changeUser")
    public String ChangeUser(@RequestParam(name = "userName") String userName,@RequestParam(name = "userAge") Integer userAge,@RequestParam(name = "userPhone") Long userPhone,@RequestParam(name = "userSex") String userSex, Model model){

        Users user=(Users) model.getAttribute("admuser");
        if(!userName.equals("")&&userName!=null) {
            user.setUserName(userName);
        }
        if(userAge!=null){
            user.setUserAge(userAge);
        }
        if(userPhone!=null) {
            user.setUserPhone(userPhone);
        }

        //System.out.println(userSex);
        if(userSex!=null) {
            user.setUserSex(userSex);
        }
        int numbers = adminService.ChangeUser(user);

        if(numbers == 0) {
            return "views/Manager/ManagerModifyUserInformationPage";
        }
        //重新获取数据
        List<Users> userlist= adminService.FindUserAll();
        model.addAttribute("userlist",userlist);

        return "views/Manager/ManagerViewUserInformationPage";
    }

    /**
     * 修改用户密码
     * @param mpassword 管理员密码
     * @param newPassword 新的用户密码
     * @param model 模型
     * @return 管理员查看用户信息界面
     */
    @RequestMapping(path="/changePassword", produces="text/html;charset=utf-8")
    public String ChangePassword(@RequestParam(name = "mpassword") String mpassword, @RequestParam(name = "newPassword") String newPassword, Model model){
        Manager manager=(Manager)model.getAttribute("manager");
        if(!mpassword.equals(manager.getAdminPW())){
            return "views/Manager/ManagerModifyUserPasswordPage";
        }
        Users users=(Users)model.getAttribute("admuser");
        int userId=users.getUserId();
        boolean isChange = adminService.ChangePassword(manager, userId, newPassword);

        if(!isChange) {
            return "views/Manager/ManagerModifyUserPasswordPage";
        }

        List<Users> userl= adminService.FindUserAll();
        List<Users> newUsersList = new ArrayList<>();
        for (int i = 0; i < userl.size(); i++) {
            newUsersList.add(userl.get(i));
        }
        model.addAttribute("userlist",newUsersList);

        return "views/Manager/ManagerViewUserInformationPage";
    }

    /**
     * 添加报表信息
     * @param userId 用户ID
     * @param temperature 体温
     * @param remarks 备注
     * @param indoor 是进还是出
     * @param model 模型
     * @return 管理员输入状态界面
     */
    @RequestMapping(path="/addReport", produces="text/html;charset=utf-8")
    public String AddReport(@RequestParam("userId") Integer userId, @RequestParam("temperature") String temperature,@RequestParam("remarks") String remarks,@RequestParam("indoor") String indoor, Model model){
        Report report=new Report();
        if(userId!=null) {
            report.setUserId(userId);
        }
        if(temperature!=null&&!"".equals(temperature)) {
            report.setTemperature(temperature);
        }
        if(remarks!=null&&!"".equals(remarks)) {
            report.setRemarks(remarks);
        }
        if("进".equals(indoor)){
            report.setInTime(new Date());
        }
        else {
            report.setOutTime(new Date());
        }

        System.out.println(report.getInTime());

        int numbers = adminService.AddReport(report);

        if(numbers == 0)
            return "views/Manager/ManagerEnterStatisticsPage";

        return "views/Manager/ManagerEnterStatisticsPage";
    }

    /**
     * 查询统计报表信息
     * @param indoor 进出选项
     * @param beginTime 起始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param model 模型
     * @return 生成统计报表界面
     */
    @RequestMapping(path = "/findReport")
    public String FindReport(String indoor, @RequestParam(name = "beginTime")String beginTime,@RequestParam(name = "endTime") String endTime, @RequestParam(name = "userId") Integer userId, Model model){

        if(indoor == null && userId == null){
            reportList = adminService.FindReportAll();
        }else if(userId != null && indoor == null){
            reportList = adminService.FindReportOne(userId);
        }else if(userId == null){
            reportList = adminService.FindReportTime(indoor, beginTime, endTime);
        }else{
            reportList = adminService.FindReportTimeOne(userId, indoor, beginTime, endTime);
        }

        return "views/Manager/ManagerGenerateStatisticalReportPage";
    }

    @RequestMapping(path = "/reportList")
    @ResponseBody
    public JSONObject reportList(Integer page, Integer limit){

        List<Report> reportListSub;
        if(((page - 1) * limit + limit) <= reportList.size()) {
            reportListSub = reportList.subList((page - 1) * limit, (page - 1) * limit + limit);
        }else{
            reportListSub = reportList.subList((page - 1) * limit, reportList.size());
        }

        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", reportList.size());
        result.put("data", reportListSub);
        return result;
    }

    /**
     * 跳转报表页面
     * @return 报表页面
     */
    @RequestMapping("/ManagerGenerateStatisticalReportPage")
    public String ManagerGenerateStatisticalReportPage(){
        reportList=adminService.FindReportAll();
        return "views/Manager/ManagerGenerateStatisticalReportPage";
    }

    /**
     * 发布疫情防控信息
     * @param title 信息标题
     * @param content 消息内容
     * @param model 模型
     * @return 防控信息界面
     */
    @RequestMapping(path = "/releaseInformation")
    public String ReleaseInformation(@RequestParam(name = "title") String title, @RequestParam(name = "content") String content,Model model){
        Manager manager=(Manager)model.getAttribute("manager");
        Integer i=manager.getAdminId();
        Message message=new Message();
        message.setTitle(title);
        message.setCont(content);
        message.setPuBer(i);
        boolean t=adminService.AddMessage(message);
        if(t==false) {
            return "views/Manager/ManagerReleaseInformationPage-Release";
        }
        //重新获取数据
        List<Message> messageList=adminService.FindMessageAll();
        model.addAttribute("msglist",messageList);
        return "views/Manager/ManagerRecordPage";
    }

    /**
     * 查看具体防疫信息
     * @param meId 信息ID
     * @param model 模型
     * @return 具体信息界面
     */
    @RequestMapping(path = "/taskCompletion")
    public String TaskCompletion(@RequestParam(name = "meId") Integer meId, Model model){

        Message message=adminService.FindMessageOne(meId);
        model.addAttribute("adminmessage",message);

        return "views/Manager/ManagerRecordPage-Details";
    }

    /**
     * 修改防控信息状态
     * @param meId 信息ID
     * @param model 模型
     * @return 记录界面
     */
    @RequestMapping(path = "/updateCompletion")
    public String updateCompletion(@RequestParam(name = "meId") Integer meId, Model model){
        adminService.ChangeMessage(meId);

        List<Message> messageList=adminService.FindMessageAll();
        model.addAttribute("msglist",messageList);
        return "views/Manager/ManagerRecordPage";
    }

    /**
     * 查找一个人的意见反馈
     * @param meID 反馈ID
     * @param model 模型
     * @return 回复界面
     */
    @RequestMapping(path = "/findOpinionOne")
    public String findOpinionOne(@RequestParam(name = "meID") Integer meID, Model model){

        Opinion opinion = adminService.FindOpinionOne(meID);
        model.addAttribute("opinion", opinion);

        return "views/Manager/ManagerAddReplyPage";
    }

    /**
     * 添加回复
     * @param meID 信息ID
     * @param reply 回复内容
     * @param adminId 管理员ID
     * @param model 模型
     * @return 意见反馈显示界面
     */
    @RequestMapping(path = "/addReply")
    public String addReply(@RequestParam(name = "meID") Integer meID, @RequestParam(name = "reply")String reply, @RequestParam(name = "adminId")Integer adminId, Model model){
        Opinion opinion = adminService.FindOpinionOne(meID);
        opinion.setAdCont(reply);
        opinion.setAdDate(new Date());
        opinion.setAdminId(adminId);
        adminService.UpdateOpinion(opinion);

        List<Opinion>  opinionList = adminService.FindOpinionAll();
        model.addAttribute("opinionList", opinionList);
        return "views/Manager/ManagerFeedbackDisplayPage";
    }

    /**
     * 查找指定用户的反馈
     * @param userId 用户ID
     * @param model 模型
     * @return 反馈显示界面
     */
    @RequestMapping(path = "/findOpinion")
    public String findOpinion(@RequestParam(name = "userId")Integer userId, Model model){
        List<Opinion> opinionList;
        if(userId == null){
            opinionList = adminService.FindOpinionAll();
        }else{
            opinionList = adminService.SelectOpinionOne(userId);
        }

        model.addAttribute("opinionList", opinionList);

        return "views/Manager/ManagerFeedbackDisplayPage";
    }

    /**
     * 跳转显示用户签到信息界面
     * @return 显示用户签到信息界面
     */
    @RequestMapping("/ManagerCheckSignInPage")
    public String ManagerCheckSignInPage(){
        signInList = adminService.FindSignInAll();
        return "views/Manager/ManagerCheckSignInPage";
    }

    /**
     * 根据条件查询用户的签到记录
     * @param beginTime 起始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @return 记录显示页面
     */
    @RequestMapping(path = "/findSignIn")
    public String findSignIn(@RequestParam(name = "beginTime")String beginTime,@RequestParam(name = "endTime") String endTime, @RequestParam(name = "userId")Integer userId){

        if(userId == null && "".equals(beginTime)){
            signInList = adminService.FindSignInAll();
        }else if(userId != null && "".equals(beginTime)){
            signInList = adminService.FindSignInOne(userId);
        }else if(userId == null){
            signInList = adminService.FindSignInTime(beginTime, endTime);
        }else{
            signInList = adminService.FindSignInTimeOne(userId, beginTime, endTime);
        }

        return "views/Manager/ManagerCheckSignInPage";
    }

    @RequestMapping(path = "/signInList")
    @ResponseBody
    public JSONObject signInList(Integer page, Integer limit){

        List<SignIn> signInListSub;
        if(((page - 1) * limit + limit) <= signInList.size()) {
            signInListSub = signInList.subList((page - 1) * limit, (page - 1) * limit + limit);
        }else{
            signInListSub = signInList.subList((page - 1) * limit, signInList.size());
        }

        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", signInList.size());
        result.put("data", signInListSub);
        return result;
    }

    /**
     * 更新志愿申请状态
     * @param meId 信息ID
     * @param stat 状态
     * @param model 模型
     * @return 处理志愿申请界面
     */
    @RequestMapping(path = "/updateVolunte")
    public String updateVolunte(@RequestParam(name = "meId")Integer meId, @RequestParam(name = "stat")String stat, Model model){
        Volunte volunte = adminService.FindVolunteOne(meId);;
        adminService.UpdateVolunteStat(volunte, stat);

        List<Volunte> voluntersList = adminService.FindIncompleteVolunte();
        model.addAttribute("voluntersList", voluntersList);
        return "views/Manager/ManagerCheckForVolunteerPage";
    }
}
