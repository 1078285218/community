package com.community.community.service;

import com.community.community.dto.CommentDTO;
import com.community.community.enums.CommentTypeEnum;
import com.community.community.enums.NotificationStatusEnum;
import com.community.community.enums.NotificationTypeEnum;
import com.community.community.exception.CustomizeErrorCode;
import com.community.community.exception.CustomizeException;
import com.community.community.mapper.CommentMapper;
import com.community.community.mapper.NotificationMapper;
import com.community.community.mapper.QuestionMapper;
import com.community.community.mapper.UserMapper;
import com.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

//  开启事务注解
    @Transactional
    public void insert(Comment comment,User user) {
        if (comment.getParentId() == null || comment.getParentId() ==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                //表示评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加评论数量和   修改Modified
            commentMapper.increaseCommentCountANDChangeQuestionModified(comment.getParentId(),System.currentTimeMillis());

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            //创建通知——评论类
            createNotify(comment,NotificationTypeEnum.REPLY_COMMENT,dbComment.getCommentator().longValue(),comment.getContent(),question.getId(),user.getName());
        }else if (comment.getType() == CommentTypeEnum.QUESTION.getType()){
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                //表示问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加回复数量和   修改Modified
            questionMapper.increaseCommentCountANDChangeQuestionModified(question.getId(),System.currentTimeMillis());
            //创建通知——问题类
            createNotify(comment,NotificationTypeEnum.REPLY_QUESTION,question.getCreator(),question.getTitle(),question.getId(),user.getName());
        }
    }

    private void createNotify(Comment comment,NotificationTypeEnum type,Long  receiver,String OuterTitle,Long outerid,String userName) {
        if (comment.getCommentator() == receiver.intValue()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type.getType());
        notification.setOuterid(outerid);
        notification.setNotifier(comment.getCommentator().longValue());
        notification.setReceiver(receiver);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setOuterTitle(OuterTitle);
        notification.setNotifierName(userName);
        notificationMapper.insert(notification);
    }

    /**
     * 该方法为，获取评论，一级评论和二级评论
     * type =
     *          CommentTypeEnum.COMMENT.getType()   二级评论
     *          CommentTypeEnum.QUESTION.getType()  一级评论
     *
     * @param id
     * @param type
     * @return
     */
    public List<CommentDTO> listByQuestionOrCommentId(Long id, Integer type) {
        CommentExample example = new CommentExample();
        example.setOrderByClause("GMT_CREATE DESC");
        CommentExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id).andTypeEqualTo(type);
        //                              其实就是criteria.andTypeEqualTo（1）
        List<Comment> comments = commentMapper.selectByExample(example);
        if (comments.size()==0){
            return new ArrayList<>();
        }
        //获得去重复评论者的IDcommentators
        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人User对象 并 转换为Map
        UserExample example1 = new UserExample();
        example1.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(example1);
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //comment  转换为  commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }

    public void addcommentLike(Long commentId, Long questionId, User user) {

        //获取该评论的相关信息
        Comment comment = commentMapper.selectByPrimaryKey(commentId);

        if (comment.getCommentator() == user.getId()){
            return;
        }

        System.out.println("获取评论相关信息"+comment);
        //增加点赞数
        comment.setLikeCount(comment.getLikeCount()+1);
        commentMapper.updateByPrimaryKey(comment);

        Notification notification = new Notification();
        notification.setNotifierName(user.getName());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(NotificationTypeEnum.LIKE.getType());
        notification.setOuterid(questionId);
        notification.setNotifier(user.getId().longValue());
        notification.setReceiver(comment.getCommentator().longValue());
        notification.setOuterTitle(comment.getContent());
        notificationMapper.insert(notification);
    }
}