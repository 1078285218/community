/**
 * 提交评论——回复问题
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    commentDTO(questionId,1,content)
}

/**
 * 提交评论——回复评论
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    commentDTO(commentId,2,content);
}

/**
 * 一级/二级评论js封装
 * @param parentId
 * @param type
 * @param content
 */
function commentDTO(parentId,type,content) {

    if (!content){
        alert("回复内容不能为空( ఠൠఠ )ﾉ");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId":parentId,
            "content":content,
            "type":type
        }),
        //回调函数
        success: function (response) {
            if (response.code == 200){
                window.location.reload();
            }else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);

                    //点击确认登录
                    if (isAccepted == true){
                        //跳转登录
                        window.open("https://github.com/login/oauth/authorize?client_id=7fa9483be17ac735da20&redirect_uri=http://localhost:8888/callback&scope=user&state=1");
                        //
                        window.localStorage.setItem("closable",true);
                    }
                }else{
                    alert(response.message);
                }
            }
            console.log(response);
        },
        dataType: "json"
    });
}


/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id =  e.getAttribute("data-id");
    //获取该评论对应 的 二级评论
    var comments = $("#comment-"+id);

    if (comments.hasClass("in") == true){
        //如果已经展开，那么点击是关闭二级评论展示
        comments.removeClass("in");
        e.classList.remove("active");
    } else{
        var commentBody = $("#comment-"+id);
        if (commentBody.children().length != 1 ){
            //如果没有展开，那么点击时展开二级评论展示
            comments.addClass("in");
            e.classList.add("active");
        }else {
            $.getJSON( "/comment/"+id, function( data ) {
                console.log(data);
                $.each( data.data.reverse(), function(index, comment ) {

                    var avatarElement  = $("<img/>",{
                        "class" : "media-object img-rounded",
                        "src" : comment.user.avatarUrl
                    });

                    var mediaLeftElement = $("<div/>",{
                        "class":"media-left haha"
                    });
                    mediaLeftElement.append(avatarElement)

                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body media-body2"
                    });

                    mediaBodyElement.append($("<p/>",{
                        "html" : comment.user.name
                    }));
                    mediaBodyElement.append($("<p/>",{
                        "class" : "reply-content",
                        "html" : comment.content
                    }));
                    mediaBodyElement.append($("<div/>",{
                        "class" : "menu menu2 pull-right",
                    }).append($("<p/>",{
                        "class" : "pull-right",
                        "html" : moment(comment.gmtCreate).format("YYYY-MM-DD HH:mm:ss")
                    })));

                    var commentElement = $("<div/>",{

                    });
                    commentElement.append(mediaLeftElement)
                    commentElement.append(mediaBodyElement)
                    commentBody.prepend(commentElement)
                });
            });
            comments.addClass("in");
            e.classList.add("active");
        }
    }
}


function selectTag(value) {
    var previous = $("#tag").val();

    if (previous.indexOf(value) == -1){
        //该标签不存在才插入
        if (previous){
            //第二个标签及之后，都在原来的基础上+，+value
            $("#tag").val(previous + ',' +value);
        }else {
            //如果原来Input中是空的，就是第一个标签
            $("#tag").val(value);
        }
    }else{
        alert("该标签已经存在");
    }
}

function showSelectTag() {
    $(".selectTag").show();

    //标签
    $(".selectTag ul > :first-child").addClass("active");
    $(".selectTag div > :first-child").addClass("active");
}