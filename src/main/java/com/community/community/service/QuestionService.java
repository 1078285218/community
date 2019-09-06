package com.community.community.service;

import com.community.community.dto.PaginationDTO;
import com.community.community.dto.QuestionDTO;
import com.community.community.exception.CustomizeErrorCode;
import com.community.community.exception.CustomizeException;
import com.community.community.mapper.QuestionMapper;
import com.community.community.mapper.UserMapper;
import com.community.community.model.Question;
import com.community.community.model.QuestionExample;
import com.community.community.model.User;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;


    //之后记得改用用多表级联，而不是用foreach
    public PaginationDTO findAllQuestion(Integer page, Integer size,String search) {
        if (!StringUtils.isEmpty(search)){

            String[] split = StringUtils.split(search, " ");
            if (split == null){
                search = search;
            }else {
                search = Arrays.stream(split).collect(Collectors.joining("|"));
            }
        }

        Integer start = size*(page-1);

        List<QuestionDTO> questionDTOList = new ArrayList<QuestionDTO>();

        //获取问题总数 total_count
        Integer count = questionMapper.countBySearch(search);

        /*这个限制其实不对，因为，如果可以手动修改页数的话，那么可能会输入“字母”输入字母会报错*/
        //总页数
        Integer totalPageNumber;
        if(count % size == 0){
            totalPageNumber = count / size;
        }else{
            totalPageNumber = count / size + 1;
        }
        if(page <= 1){
            start = 1;
        }
        if (page > totalPageNumber){
            page = totalPageNumber;
            start = totalPageNumber;
        }
        /*----------------------------------分页PageHelper--------------------------------------------*/


        Page<Object> pages = PageHelper.startPage(page , size);
        List<Question> questions = questionMapper.selectBySearch(search);
//        System.out.println("当前页是:"+pages.getPageNum());
//        System.out.println("每页显示的记录数是:"+pages.getPageSize());
//        System.out.println("总页数是:"+pages.getPages());
//        System.out.println("总记录数是:"+pages.getTotal());
        /*List<Question> questions = questionMapper.findAllQuestion(start,size);*/

        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator().intValue());
            QuestionDTO questionDTO = new QuestionDTO();
            //快速的把question上属性的值，复制到questionDTO对应的属性上
            BeanUtils.copyProperties(question,questionDTO);
            //设置questionDTO中user的值
            questionDTO.setUser(user);

            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);


        paginationDTO.setPagination(page,totalPageNumber);
        return paginationDTO;
    }





    public PaginationDTO findAllQuestionByUserId(Long userId, Integer page, Integer size) {
        Integer start = size*(page-1);

        List<QuestionDTO> questionDTOList = new ArrayList<QuestionDTO>();

        //获取问题总数 total_count
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andCreatorEqualTo(userId);
        Integer count = (int)questionMapper.countByExample(example);

        /*这个限制其实不对，因为，如果可以手动修改页数的话，那么可能会输入“字母”输入字母会报错*/
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
        QuestionExample example1 = new QuestionExample();
        QuestionExample.Criteria criteria1 = example.createCriteria();
        criteria1.andCreatorEqualTo(userId.longValue());
        List<Question> questions = questionMapper.selectByExample(example1);
        /*List<Question> questions = questionMapper.findAllQuestionById(userId,start,size);*/

        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator().intValue());
            QuestionDTO questionDTO = new QuestionDTO();
            //快速的把question上属性的值，复制到questionDTO对应的属性上
            BeanUtils.copyProperties(question,questionDTO);
            //设置questionDTO中user的值
            questionDTO.setUser(user);

            questionDTOList.add(questionDTO);
        }

        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(page,totalPageNumber);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);

        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator().intValue());
        questionDTO.setUser(user);

        
        return questionDTO;
    }

    public void increaseView(Long id) {
        questionMapper.increaseView(id);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (queryDTO.getTag().isEmpty()){
            return new ArrayList<>();
        }
        //把Tag拆开
        String[] split = StringUtils.split(queryDTO.getTag(), ",");

        String regexpTag = null;
        if (split == null){
            regexpTag = queryDTO.getTag();
        }else {
            //拆开的Tag用  |  进行拼接    因为regexp的sql语法是|
            regexpTag = Arrays.stream(split).collect(Collectors.joining("|"));
        }
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionMapper.selectRelated(question);
        List<QuestionDTO> questionDTOList = questions.stream().map(question1 -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question1,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOList;
    }
}
