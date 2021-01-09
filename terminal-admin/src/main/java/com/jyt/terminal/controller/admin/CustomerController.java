package com.jyt.terminal.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jyt.terminal.commom.enums.BitEnum;
import com.jyt.terminal.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jyt.terminal.commom.LogObjectHolder;
import com.jyt.terminal.commom.PageFactory;
import com.jyt.terminal.commom.Permission;
import com.jyt.terminal.commom.enums.BitEnum.CustomerStatus;
import com.jyt.terminal.commom.enums.BitEnum.CardType;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.commom.enums.EmailTemplateConstant;
import com.jyt.terminal.dto.CustomerDTO;
import com.jyt.terminal.model.Customer;
import com.jyt.terminal.model.EmailSetting;

import com.jyt.terminal.model.NotifyMailbox;

import com.jyt.terminal.model.EmailTemplate;

import com.jyt.terminal.service.ICustomerService;
import com.jyt.terminal.service.IEmailSettingService;

import com.jyt.terminal.service.INotifyMailboxService;

import com.jyt.terminal.service.IEmailTemplateService;

import com.jyt.terminal.service.impl.EmailService;
import com.jyt.terminal.util.ShiroKit;
import com.jyt.terminal.util.Tip;
import com.jyt.terminal.util.ToolUtil;
import com.jyt.terminal.warpper.CustomerWarpper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/customer")
public class CustomerController extends BaseController {

    private static String PREFIX = "/system/customer/";

    @Autowired
    public ICustomerService customerService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private IEmailSettingService emailSettingService;

    @Autowired
    private INotifyMailboxService notifyMailboxService;

    @Autowired
    private IEmailTemplateService emailTemplateService;


    /**
     * 跳转到KYC审核List
     */
    @RequestMapping("/kycReview")
	@Permission
	public String kycReview(Model model) {
        //证件类型枚举
        List<Map<String, Object>> cardTypes = new ArrayList<Map<String, Object>>();
        for (BitEnum.CardType param : BitEnum.CardType.values()) {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("code", param.getCode());
            value.put("label", param.getMessage());
            cardTypes.add(value);
        }
        model.addAttribute("cardTypes", cardTypes);
        return PREFIX + "kycReview.html";
    }

    /**
     * 分页查询列表信息
     */
    @ApiOperation(value = "分页查询客户列表信息", notes = "分页查询客户列表信息")
    @RequestMapping("/kycReview/list/{currentType}")
	@Permission
    @ResponseBody
    public Object kycReviewList(@ApiParam(name = "分页查询客户列表信息", required = true) @PathVariable String currentType, CustomerDTO customerDTO) {
        Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.customerService.getKycReviewList(page, customerDTO);
        page.setRecords((List<Map<String, Object>>) new CustomerWarpper(result).warp(currentType));
        return super.packForBT(page);
    }


    /**
     * 跳转客户审核页面
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(value = "/examine/{id}",method = RequestMethod.GET)
	@Permission
    public String openReviewView(@PathVariable Integer id, Model model) {
        if (ToolUtil.isEmpty(id)) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        Customer customer = this.customerService.selectById(id);
        model.addAttribute("customer", customer);
        return PREFIX + "customer_examine.html";
    }

    /**
     * 审核  通过
     */
    @RequestMapping("/examine/adopt")
	@Permission
    @ResponseBody
    public Tip adopt(Customer customer) {
        if (ToolUtil.isEmpty(customer.getId())) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        Customer entity = customerService.selectById(customer.getId());
        if (!entity.getStatus().equals(CustomerStatus.AUDIT.getCode())) {
            SUCCESS_TIP.setCode(500);
            SUCCESS_TIP.setMessage("Information has been audited");
            return SUCCESS_TIP;
        }
        entity.setStatus(CustomerStatus.AUDIT_PASSED.getCode());
        entity.setAuditOpinion(customer.getAuditOpinion());
        entity.setUpdateUser(ShiroKit.getUser().getAccount());
        entity.setUpdateTime(new Date());
        customerService.updateById(entity);
        //审核通过发送邮件
        sendCustomerEmail(entity);
        SUCCESS_TIP.setCode(200);
        SUCCESS_TIP.setMessage("success");
        return SUCCESS_TIP;

    }

    /**
     * 审核  不通过
     */
    @RequestMapping("/examine/reject")
	@Permission
    @ResponseBody
    public Tip reject(Customer customer) {
        if (ToolUtil.isEmpty(customer.getId())) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        Customer entity = customerService.selectById(customer.getId());
        if (!entity.getStatus().equals(CustomerStatus.AUDIT.getCode())) {
            SUCCESS_TIP.setCode(500);
            SUCCESS_TIP.setMessage("Information has been audited");
            return SUCCESS_TIP;
        }
        entity.setStatus(CustomerStatus.AUDIT_NOPASS.getCode());
        entity.setAuditOpinion(customer.getAuditOpinion());
        entity.setUpdateUser(ShiroKit.getUser().getAccount());
        entity.setUpdateTime(new Date());
        customerService.updateById(entity);
        //审核不通过发送邮件
        sendCustomerEmail(entity);
        SUCCESS_TIP.setCode(200);
        SUCCESS_TIP.setMessage("success");
        return SUCCESS_TIP;
    }




    /*----------------------------客户列表--------------------------------*/

    /**
     * 跳转到客户列表页面
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @Permission
    public String index(Model model) {
        //客户状态枚举
        List<Map<String, Object>> customerStatus = new ArrayList<Map<String, Object>>();
        for (CustomerStatus param : CustomerStatus.values()) {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("code", param.getCode());
            value.put("v1", param.getV1());
            value.put("v2", param.getV2());
            customerStatus.add(value);
        }
        model.addAttribute("customerStatus", customerStatus);
        //证件类型枚举
        List<Map<String, Object>> cardTypes = new ArrayList<Map<String, Object>>();
        for (CardType param : CardType.values()) {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("code", param.getCode());
            value.put("label", param.getMessage());
            cardTypes.add(value);
        }
        model.addAttribute("cardTypes", cardTypes);
        return PREFIX + "customer.html";
    }

    /**
     * 分页查询列表信息
     */
    @ApiOperation(value = "分页查询客户列表信息", notes = "分页查询客户列表信息")
    @RequestMapping(value = "",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Object list(@ApiParam(name = "分页查询客户列表信息", required = true) HttpServletRequest request, CustomerDTO customerDTO) {
        Map<String, String> respParam = Utils.getAllRequestParam(request);
        String currentType = respParam.get("currentType");
        Page<Map<String, Object>> page = new PageFactory<Map<String, Object>>().defaultPage();
        List<Map<String, Object>> result = this.customerService.getCustomerList(page, customerDTO);
        page.setRecords((List<Map<String, Object>>) new CustomerWarpper(result).warp(currentType));
        return super.packForBT(page);
    }

    /**
     * 通知邮箱页面
     */
    @RequestMapping(value = "/notifyBox",method = RequestMethod.GET)
    @Permission
    public String tosetNotifyBox(Model model) {
        NotifyMailbox notifyMailbox = notifyMailboxService.selectOne(new EntityWrapper<>());
        if (notifyMailbox != null) {
            model.addAttribute("email", notifyMailbox.getMailBox());
        } else {
            model.addAttribute("email", "");
        }
        return PREFIX + "notify_mailbox.html";
    }

    /**
     * 设置通知邮箱
     */
    @RequestMapping(value = "/notifyBox",method = RequestMethod.POST)
    @Permission
    @ResponseBody
    public Tip setNotifyBox(String email) {
        NotifyMailbox notifyMailbox = notifyMailboxService.selectOne(new EntityWrapper<>());
        if (notifyMailbox != null) {
            notifyMailbox.setMailBox(email);
            notifyMailbox.setUpdateTime(new Date());
            notifyMailbox.setUpdateUser(ShiroKit.getUser().getAccount());
            notifyMailboxService.updateById(notifyMailbox);
        } else {
            notifyMailbox = new NotifyMailbox();
            notifyMailbox.setMailBox(email);
            notifyMailbox.setCreateTime(new Date());
            notifyMailbox.setCreateUser(ShiroKit.getUser().getAccount());
            notifyMailboxService.insert(notifyMailbox);
        }
        return SUCCESS_TIP;
    }



    private void sendCustomerEmail(Customer customer) {
        Map<String, Object> map = new HashMap<>();
        map.put("custname", customer.getCustName());
        EmailTemplate emailTemplate = new EmailTemplate();
        if (customer.getStatus().equals(CustomerStatus.AUDIT_PASSED.getCode())) {
            emailTemplate = emailTemplateService.selectOne(new EntityWrapper<EmailTemplate>().eq("code", EmailTemplateConstant.KYC_REVIEW_SUCCESS));
        }
        if (customer.getStatus().equals(CustomerStatus.AUDIT_NOPASS.getCode())) {
            emailTemplate = emailTemplateService.selectOne(new EntityWrapper<EmailTemplate>().eq("code", EmailTemplateConstant.KYC_REVIEW_FAIL));
        }
        String result = emailTemplate.getTemplate();
        result = result.replaceAll("#custname#", customer.getCustName());
        map.put("result", result);
        EmailSetting emailSetting = emailSettingService.selectOne(new EntityWrapper<>());
        emailService.sendMail("email_template.html", "KYC", map, customer.getEmail(), emailSetting);
    }


    /**
     * 跳转客户详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/details/{id}")
    @Permission
    public String details(@PathVariable Integer id, Model model) {
        if (ToolUtil.isEmpty(id)) {
            throw new BitException(BizExceptionEnum.REQUEST_NULL);
        }
        Customer customer = this.customerService.selectById(id);
        model.addAttribute("customer", customer);
        LogObjectHolder.me().set(customer);
        return PREFIX + "customer_details.html";
    }


}
