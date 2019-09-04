package com.community.community.service;

import com.community.community.dto.NotificationDTO;
import com.community.community.dto.PaginationDTO;
import com.community.community.enums.NotificationStatusEnum;
import com.community.community.exception.CustomizeErrorCode;
import com.community.community.exception.CustomizeException;
import com.community.community.mapper.NotificationMapper;
import com.community.community.mapper.UserMapper;
import com.community.community.model.Notification;
import com.community.community.model.NotificationExample;
import com.community.community.model.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO findNotificationByUserId(Long userId, Integer page, Integer size) {
        Integer start = size*(page-1);

        List<NotificationDTO> notificationDTOS = new ArrayList<NotificationDTO>();

        //获取回复总数 total_count
        NotificationExample example = new NotificationExample();
        NotificationExample.Criteria criteria = example.createCriteria();
        criteria.andReceiverEqualTo(userId);
        Integer count = (int)notificationMapper.countByExample(example);
        //总页数
        Integer totalPageNumber;
        if(count % size == 0){
            totalPageNumber = count / size;
        }else{
            totalPageNumber = count / size + 1;
        }
        if(page < 1){
            start = 1;
        }
        if (page > totalPageNumber){
            page = totalPageNumber;
            start = totalPageNumber;
        }

        /*----------------------------------分页PageHelper--------------------------------------------*/

        Page<Object> pages = PageHelper.startPage(page , size);
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.setOrderByClause("GMT_CREATE DESC");
        notificationExample.createCriteria().andReceiverEqualTo(userId.longValue());
        List<Notification> notifications = notificationMapper.selectByExample(notificationExample);

        PaginationDTO paginationDTO = new PaginationDTO();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            //快速的把notification上属性的值，复制到notificationDTO对应的属性上
            BeanUtils.copyProperties(notification,notificationDTO);
            //设置notificationDTO中user的值
            notificationDTO.setNotifier(userId.intValue());
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        paginationDTO.setPagination(page,totalPageNumber);
        return paginationDTO;
    }

    public Long unreadCount(Integer userId) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId.longValue()).andStatusEqualTo(0);
        return notificationMapper.countByExample(example);
    }

    public Notification read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null){
            throw  new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (notification.getReceiver() != user.getId().longValue()){
            /*如果不是这个人的消息*/
            throw  new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

//      该消息状态标记为已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        return notification;
    }
}
