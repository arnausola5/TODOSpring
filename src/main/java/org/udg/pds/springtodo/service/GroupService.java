package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.GroupRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserService userService;

    public GroupRepository crud() { return groupRepository; }

    public Group getGroup(Long userId, Long groupId) {
        Optional<Group> g = groupRepository.findById(groupId);
        if(!g.isPresent()) throw new ServiceException("Group does no exists");
        if(g.get().getOwner().getId() != userId) throw new ServiceException("User does not own this group");

        return g.get();
    }

    public Collection<Group> getMemberGroups(Long userId) { return userService.getUser(userId).getMemberGroups(); }

    public Collection<Group> getOwnerGroups(Long userId) { return userService.getUser(userId).getOwnerGroups(); }

    public Collection<Group> getGroups(Long userId) {
        Collection<Group> groups = getMemberGroups(userId);
        groups.addAll(getOwnerGroups(userId));

        return groups;
    }

    public Collection<User> getGroupMembers(Long userId, Long groupId) {
        Group g = getGroup(userId, groupId);
        User u = userService.getUser(userId);

        if(g.getOwner().getId() == userId || g.getMembers().contains(u))
            return g.getMembers();
        else
            throw new ServiceException("User does not own/member this group");
    }

    @Transactional
    public IdObject addGroup(Long userId, String name, String description) {
        try {
            User user = userService.getUser(userId);
            Group group = new Group(name, description);

            group.setOwner(user);
            user.addOwnerGroups(group);
            groupRepository.save(group);

            return new IdObject(group.getId());
        } catch(Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Transactional
    public void addMemberToGroup(Long userId, Long groupId, Long memberId) {
        Group g = this.getGroup(userId, groupId);

        if(g.getOwner().getId() != userId)
            throw new ServiceException("This user is not the owner of the group");

        try {
            User member = userService.getUser(memberId);
            g.addMember(member);
        } catch(Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
}
