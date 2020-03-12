package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.service.GroupService;
import org.udg.pds.springtodo.entity.Views;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@RequestMapping(path = "/groups")
@RestController
public class GroupController extends BaseController {
    @Autowired
    GroupService groupService;

    @GetMapping(path = "/owner")
    @JsonView(Views.Private.class)
    public Collection<Group> listAllOwnerGroups(HttpSession session, @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return groupService.getOwnerGroups(userId);
    }

    @GetMapping(path = "/{gid}/members")
    @JsonView(Views.Private.class)
    public Collection<User> getGroupMembers(HttpSession session, @PathVariable("gid") Long groupId) {
        Long userId = getLoggedUser(session);

        return groupService.getGroupMembers(userId, groupId);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addGroup(HttpSession session, @Valid @RequestBody R_Group group) {
        Long userId = getLoggedUser(session);

        return groupService.addGroup(userId, group.name, group.description);
    }

    @PostMapping(path = "/{gid}/members/{uid}")
    public String addMember(HttpSession session, @PathVariable("gid") Long groupId, @PathVariable("uid") Long memberId) {
        Long userId = getLoggedUser(session);

        groupService.addMemberToGroup(userId, groupId, memberId);

        return BaseController.OK_MESSAGE;
    }

    static class R_Group {
        @NotNull
        public String name;

        @NotNull
        public String description;
    }
}
