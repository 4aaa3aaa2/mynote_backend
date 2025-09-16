package com.aaa.notes.utils;


import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class MarkdownAST {
    private final Document markdownAST;
    private final String markdownText;


    //构造函数，初始化并解析markdown文本
    public MarkdownAST (String markdownText){
        this.markdownText = markdownText;
        
        Parser parser = Parser.builder().build();  //创建解析器实例
        this.markdownAST = parser.parse(markdownText);  //解析文本生成AST
    }

    //提取简介，包括了heading和段落，限制字数
    public String extractIntroduction(int maxChars){
        StringBuilder introText = new StringBuilder();

        //遍历AST节点
        for (Node node: markdownAST.getChildren()){  
            if (node instanceof Heading||node instanceof Paragraph){
                String renderedText = getNodeText(node); //获取节点的文本内容
           
                int remainingChars = maxChars - introText.length();  //截取文本到指定字数
                introText.append(renderedText, 0, Math.min(remainingChars, renderedText.length()));
                

                //字数达到上限就停止
                if(introText.length() >= maxChars){
                    break;  
                }
            }
        }
        return introText.toString().trim()+"...";
    }
    

    //检查是否包括图片，返回图片地址
    public List<String> extractImages(){
        List<String> imageUrls = new ArrayList<>();
        //遍历AST节点
        for (Node node: markdownAST.getChildren()){
            if (node instanceof Image imageNode){
                imageUrls.add(imageNode.getUrl().toString());  //获取图片地址
            }
        }
        return imageUrls;
    }


    //判断文本要不要过长收起
    public boolean shouldCollapse(int maxChars){
        return hasImages()||markdownText.length()>=maxChars;

    }
    

    //获取精简之后的文本
    public String getCollapsedMarkdown(){
        String introText = extractIntroduction(150);
        return introText + "...";
    }
    

    //获取节点文本
    private String getNodeText(Node node){
        StringBuilder text = new StringBuilder();

        //处理text节点
        if(node instanceof Text){
            text.append(((Text) node).getChars());
        }
        
        //处理其他节点
        for (Node child: node.getChildren()){
            text.append(getNodeText(child));
        }
        return text.toString();
    }

    // 获取 heading 的文本内容
    public String getHeadingText(Heading headingNode) {
        return headingNode.getText().toString().trim();
    }

    public String getListItemText(ListItem listItem) {
        StringBuilder sb = new StringBuilder();
        for (Node node = listItem.getFirstChild(); node != null; node = node.getNext()) {
            sb.append(node.getChars().toString());
        }
        return sb.toString().trim();
    }

    // 判断 Markdown 文本中是否包含图片
    private boolean hasImages() {
        return !extractImages().isEmpty();
    }

}
