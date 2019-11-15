package boot.spring.authority;

import boot.spring.common.SpringIOCUtil;
import boot.spring.po.*;
import boot.spring.service.SystemService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;


public class AuthorityCheck {
    public static boolean isAuthority(String userId, String authorityCode){
        SystemService systemService = SpringIOCUtil.getBean(SystemService.class);
        User user = systemService.getUserByid(systemService.getUidByusername(userId));
        List<User_role> userRoles = user.getUser_roles();
        if(!CollectionUtils.isEmpty(userRoles)){
            for (int i = 0; i < userRoles.size(); i++) {
                Role role = systemService.getRolebyid(userRoles.get(i).getRole().getRid());
                List<Role_permission> p = role.getRole_permission();
                for (int j = 0; j < p.size(); j++) {
                    Role_permission rp = p.get(j);
                    Permission permission = rp.getPermission();
                    if (permission.getPermissionname().equals(authorityCode))
                        return true;
                    else
                        continue;
                }
            }
        }
        return false;
    }

}
