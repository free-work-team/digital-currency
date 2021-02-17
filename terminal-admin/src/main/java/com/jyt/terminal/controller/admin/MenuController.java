package com.jyt.terminal.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jyt.terminal.commom.node.ZTreeNode;
import com.jyt.terminal.model.Role;
import com.jyt.terminal.service.IMenuService;
import com.jyt.terminal.service.IRoleService;
import com.jyt.terminal.util.ToolUtil;

/**
 * 菜单控制器
 *
 * @author fengshuonan
 * @Date 2017年2月12日21:59:14
 */
@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController {

    private static String PREFIX = "/system/menu/";

    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleService roleService;
    
    /*@Autowired
    private IConstantFactory constantFactory;
    
    @Value("${hru.sys.merchantId}")
    private String sysMerchantId;*/

    /**
     * 跳转到菜单列表列表页面
     */
    /*@RequestMapping("")
    public String index() {
        return PREFIX + "menu.html";
    }*/

    /**
     * 跳转到菜单列表列表页面
     */
    /*@RequestMapping(value = "/menu_add")
    public String menuAdd() {
        return PREFIX + "menu_add.html";
    }*/

    /**
     * 跳转到菜单详情列表页面
     */
    /*@Permission
    @RequestMapping(value = "/menu_edit/{menuId}")
    public String menuEdit(@PathVariable Long menuId, Model model) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new HruException(BizExceptionEnum.REQUEST_NULL);
        }
        Menu menu = this.menuService.selectById(menuId);

        //获取父级菜单的id
        Menu temp = new Menu();
        temp.setCode(menu.getPcode());
        Menu pMenu = this.menuService.selectOne(new EntityWrapper<>(temp));

        //如果父级是顶级菜单
        if (pMenu == null) {
            menu.setPcode("0");
        } else {
            //设置父级菜单的code为父级菜单的id
            menu.setPcode(String.valueOf(pMenu.getId()));
        }

        Map<String, Object> menuMap = BeanKit.beanToMap(menu);
        menuMap.put("pcodeName", constantFactory.getMenuNameByCode(temp.getCode(),AppCode.ADMIN.getValue()));
        model.addAttribute("menu", menuMap);
        LogObjectHolder.me().set(menu);
        return PREFIX + "menu_edit.html";
    }*/

    /**
     * 修该菜单
     */
    /*@Permission
    @RequestMapping(value = "/edit")
    @ResponseBody
    public Tip edit(@Valid Menu menu, BindingResult result) {
        if (result.hasErrors()) {
            throw new HruException(BizExceptionEnum.REQUEST_NULL);
        }
        //设置父级菜单编号
        menuSetPcode(menu);

        this.menuService.updateById(menu);
        return SUCCESS_TIP;
    }*/

    /**
     * 获取菜单列表
     */
    /*@Permission
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(@RequestParam(required = false) String menuName, @RequestParam(required = false) String level) {
        List<Map<String, Object>> menus = this.menuService.selectMenus(menuName, level);
        return super.warpObject(new MenuWarpper(menus));
    }*/

    /**
     * 新增菜单
     */
    /*@Permission
    @RequestMapping(value = "/add")
    @ResponseBody
    public Tip add(@Valid Menu menu, BindingResult result) {
        if (result.hasErrors()) {
            throw new HruException(BizExceptionEnum.REQUEST_NULL);
        }

        //判断是否存在该编号
        String existedMenuName = constantFactory.getMenuNameByCode(menu.getCode(),AppCode.ADMIN.getValue());
        if (ToolUtil.isNotEmpty(existedMenuName)) {
            throw new HruException(BizExceptionEnum.EXISTED_THE_MENU);
        }

        //设置父级菜单编号
        menuSetPcode(menu);

        menu.setStatus(MenuStatus.ENABLE.getCode());
        this.menuService.insert(menu);
        return SUCCESS_TIP;
    }*/

    /**
     * 删除菜单
     */
    /*@Permission
    @RequestMapping(value = "/remove")
    @ResponseBody
    public Tip remove(@RequestParam Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new HruException(BizExceptionEnum.REQUEST_NULL);
        }

        //缓存菜单的名称
        LogObjectHolder.me().set(constantFactory.getMenuName(menuId));

        this.menuService.delMenuContainSubMenus(menuId);
        return SUCCESS_TIP;
    }*/

    /**
     * 查看菜单
     */
    /*@RequestMapping(value = "/view/{menuId}")
    @ResponseBody
    public Tip view(@PathVariable Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            throw new HruException(BizExceptionEnum.REQUEST_NULL);
        }
        this.menuService.selectById(menuId);
        return SUCCESS_TIP;
    }*/

    /**
     * 获取菜单列表(首页用)
     */
    /*@RequestMapping(value = "/menuTreeList")
    @ResponseBody
    public List<ZTreeNode> menuTreeList() {
        List<ZTreeNode> roleTreeList = this.menuService.menuTreeList(AppCode.ADMIN.getValue());
        return roleTreeList;
    }*/

    /**
     * 获取菜单列表(选择父级菜单用)
     */
    /*@RequestMapping(value = "/selectMenuTreeList")
    @ResponseBody
    public List<ZTreeNode> selectMenuTreeList() {
        List<ZTreeNode> roleTreeList = this.menuService.menuTreeList(null);
        roleTreeList.add(ZTreeNode.createParent());
        return roleTreeList;
    }*/

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/menuTreeListByRoleId/{roleId}")
    @ResponseBody
    public List<ZTreeNode> menuTreeListByRoleId(@PathVariable Integer roleId) {
        List<Long> menuIds = this.menuService.getMenuIdsByRoleId(roleId);
        Role role = roleService.selectById(roleId);
       
        if (ToolUtil.isEmpty(menuIds)) {
            List<ZTreeNode> roleTreeList = this.menuService.menuTreeList();
            return roleTreeList;
        } else {
            List<ZTreeNode> roleTreeListByUserId = this.menuService.menuTreeListByMenuIds(menuIds);
            return roleTreeListByUserId;
        }
    }

    /**
     * 根据请求的父级菜单编号设置pcode和层级
     */
    /*private void menuSetPcode(@Valid Menu menu) {
        if (ToolUtil.isEmpty(menu.getPcode()) || menu.getPcode().equals("0")) {
            menu.setPcode("0");
            menu.setPcodes("[0],");
            menu.setLevels(1);
        } else {
            long code = Long.parseLong(menu.getPcode());
            Menu pMenu = menuService.selectById(code);
            Integer pLevels = pMenu.getLevels();
            menu.setPcode(pMenu.getCode());

            //如果编号和父编号一致会导致无限递归
            if (menu.getCode().equals(menu.getPcode())) {
                throw new HruException(BizExceptionEnum.MENU_PCODE_COINCIDENCE);
            }

            menu.setLevels(pLevels + 1);
            menu.setPcodes(pMenu.getPcodes() + "[" + pMenu.getCode() + "],");
        }
    }*/

}
