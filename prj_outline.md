|__backend
|  |__src
|  |  |__main
|  |  |  |__java
|  |  |  |  |__com/aaa/notes
|  |  |  |  |  |__annoation
|  |  |  |  |  |__aspect
|  |  |  |  |  |__config  spring项目模板配置
|  |  |  |  |  |__controller  控制器，调用各种功能函数
|  |  |  |  |  |__exception
|  |  |  |  |  |__filter
|  |  |  |  |  |__interceptor
|  |  |  |  |  |__mapper  数据访问层，对数据库操作的接口
|  |  |  |  |  |__model  数据模型
|  |  |  |  |  |  |__base  包括了其他模型要使用或扩展的基类
|  |  |  |  |  |  |  |__Apiresponse.java
|  |  |  |  |  |  |  |__EmptyVO.java
|  |  |  |  |  |  |  |__PageVO.java
|  |  |  |  |  |  |  |__Pagination.java
|  |  |  |  |  |  |  |__PaginationApiResponse.java
|  |  |  |  |  |  |  |__TokenApiResponse.java
|  |  |  |  |  |  | 
|  |  |  |  |  |  |__dto  service层到controller层的数据传输
|  |  |  |  |  |  |  |__category
|  |  |  |  |  |  |  |__collection
|  |  |  |  |  |  |  |__comment
|  |  |  |  |  |  |  |__message
|  |  |  |  |  |  |  |__note
|  |  |  |  |  |  |  |__notification
|  |  |  |  |  |  |  |__question
|  |  |  |  |  |  |  |__questionList
|  |  |  |  |  |  |  |__questionListItem
|  |  |  |  |  |  |  |__statistic
|  |  |  |  |  |  |  |__user
|  |  |  |  |  |  |  
|  |  |  |  |  |  |__entity  关联到数据库的表
|  |  |  |  |  |  |  |__Category.java
|  |  |  |  |  |  |  |__Collection.java
|  |  |  |  |  |  |  |__CollectionNote.java
|  |  |  |  |  |  |  |__Comment.java
|  |  |  |  |  |  |  |__CommentLike.java
|  |  |  |  |  |  |  |__Message.java
|  |  |  |  |  |  |  |__Note.java
|  |  |  |  |  |  |  |__NoteCollection.java
|  |  |  |  |  |  |  |__NoteComment.java
|  |  |  |  |  |  |  |__NoteLike.java
|  |  |  |  |  |  |  |__Question.java
|  |  |  |  |  |  |  |__QuestionList.java
|  |  |  |  |  |  |  |__QuestionListItem.java
|  |  |  |  |  |  |  |__Statistic.java
|  |  |  |  |  |  |  |__User.java
|  |  |  |  |  |  |  
|  |  |  |  |  |  |__enums  定义类里面的常数
|  |  |  |  |  |  |  |__message
|  |  |  |  |  |  |  |__questionList
|  |  |  |  |  |  |  |__redisKey
|  |  |  |  |  |  |  |__user
|  |  |  |  |  |  |  
|  |  |  |  |  |  |__request/message
|  |  |  |  |  |  |  
|  |  |  |  |  |  |__vo   数据输出到前端
|  |  |  |  |  |  |  |__category
|  |  |  |  |  |  |  |__collection
|  |  |  |  |  |  |  |__comment
|  |  |  |  |  |  |  |__message
|  |  |  |  |  |  |  |__note
|  |  |  |  |  |  |  |__notification
|  |  |  |  |  |  |  |__question
|  |  |  |  |  |  |  |__questionList
|  |  |  |  |  |  |  |__questionListItem
|  |  |  |  |  |  |  |__upload
|  |  |  |  |  |  |  |__user
|  |  |  |  |  |  
|  |  |  |  |  |__scope
|  |  |  |  |  |__service
|  |  |  |  |  |  |__impl  提供各种功能接口的具体实现方法
|  |  |  |  |  |  |__***Service.java  controller里面各种功能的接口  
|  |  |  |  |  |
|  |  |  |  |  |__task
|  |  |  |  |  |__utils
|  |  |  |  |  |  |__ApiResponse.java
|  |  |  |  |  |  |__jwtUtil.java
|  |  |  |  |  |  |__MarkdownAST.java
|  |  |  |  |  |  |__MarkdownUtil.java
|  |  |  |  |  |  |__PaginationUtils.java
|  |  |  |  |  |  |__RandomCodeUtil.java
|  |  |  |  |  |  |__SearchUtils.java
|  |  |  |  |  |  |__SecurityUtils.java
|  |  |  |  |  
|  |  |  |__resources
|  |  |  |  |__mapper  提供main/mapper里面的数据库接口操作的实现方法
|  |  |  |  |  |__**.xml
|  |  |  |  |  
|  |  |  |  |__application.yml
|  |  
|  |  |__test
|  |__pom.xml  项目基本工具配置文档


