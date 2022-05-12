package com.zms.openzone.interact.utils;

import com.mysql.cj.util.StringUtils;
import org.apache.commons.lang.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/1/28 15:53
 */
@Component
public class SensitiveFilter {


    //替换的字符
    private final static String REPLACEMENT = "***";

    //根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() throws IOException {
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {

        } finally {
            resourceAsStream.close();
        }


    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TrieNode temNoode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNodes = temNoode.getSubNodes(c);
            if (subNodes == null) {
                subNodes = new TrieNode();
                temNoode.addSubNodes(c, subNodes);
            }
            //指向子节点，进行下一轮循环
            temNoode = subNodes;
            //设置结束标识
            if (i == keyword.length() - 1) {
                temNoode.setKeywordEnd(true);
            }
        }
    }

    /**
     * create by: zms
     * description: TODO
     * create time: 2022/1/28 16:31
     *
     * @param text 待过滤文本
     *             No such property: code for class: Script1
     * @return
     */
    public String filter(String text) {
        if (StringUtils.isNullOrEmpty(text)) return null;
        //指针1
        TrieNode tempNode = root;
        //指针2 遍历所有子字符串的开头
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder stringBuilder = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            //跳过符号
            if (isSymbol(c)) {  //如果是特殊符号
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode == root) {
                    stringBuilder.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNodes(c);
            if (tempNode == null) {
                //说明以begin开头的不是敏感词
                stringBuilder.append(text.charAt(begin));
                //进入下一个检查点
                position = ++begin;
                //重新指向根节点
                tempNode = root;
            } else if (tempNode.isKeywordEnd()) {
                //发现敏感词，将begin到position字符串替换掉
                stringBuilder.append(REPLACEMENT);
                //进入下一个检查点
                begin = ++position;
            } else {
                position++;
            }

        }
        //将最后的字符拼接上
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        //关键词结束标志
        private boolean isKeywordEnd = false;

        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNodes(Character c, TrieNode subNode) {
            subNodes.put(c, subNode);
        }

        //获取子节点
        public TrieNode getSubNodes(Character c) {
            return subNodes.get(c);
        }
    }
}
