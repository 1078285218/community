package com.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
//    private List<QuestionDTO> questions;
//    private List<NotificationDTO> notifications;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPageNumber;

    public void setPagination(Integer page, Integer totalPageNumber) {
        this.page=page;
        this.totalPageNumber=totalPageNumber;
        //总页数totalPageNumber
        /*if(count % size == 0){
            totalPageNumber = count / size;
        }else{
            totalPageNumber = count / size + 1;
        }*/

        pages.add(page);
        for(int i =1 ; i<=3 ; i++){
            if(page-i>=1){
                //头部插入
                pages.add(0,page-i);
            }
            if(page+i<=totalPageNumber){
                //尾部插入
                pages.add(page+i);
            }
        }
        //上面可以却表pages中的数字左边小右边大，是有序的



        //是否展示上一页
        if(page!=1){
            showPrevious = true;
        }else{
            showPrevious = false;
        }
        //是否展示下一页
        if (page!=totalPageNumber){
            showNext = true;
        }else{
            showNext = false;
        }
        //是否展示第一页
        if (pages.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }
        //是否展示最后一页
        if (pages.contains(totalPageNumber)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }
}
