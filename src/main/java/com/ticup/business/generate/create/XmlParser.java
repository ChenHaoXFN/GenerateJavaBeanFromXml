package com.ticup.business.generate.create;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xml 解析类
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 10:47
 */
public class XmlParser {
  public Map<String, List<JavaFieldModel>> parser(String xml, String className)
      throws IOException, ClassNotFoundException {
    Map<String, List<JavaFieldModel>> result = new HashMap<>();
    List<JavaFieldModel> list = new ArrayList<>();
    List<JavaFieldModel> listModel;
    boolean inRequest = false;
    boolean inList = false;
    boolean endList = false;
    String startListName = null;

    StringBuilder listSb = new StringBuilder();
    // 忽略报文头
    BufferedReader bfr = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(xml.getBytes())));
    String s;

    while ((s = bfr.readLine()) != null && !s.equals("")) {
      s = s.trim();
      // 忽略报文头
      if (s.equals("<request>")) {
        inRequest = true;
        continue;
      }
      if (s.equals("</request>")) {
        inRequest = false;
      }
      // 逐行解析报文体

      // 没有结尾标签，代表开始一个列表了
      if (inRequest && !s.contains("</") && !inList) {
        inList = true;
        startListName = s.substring(1, s.length() - 1);
      }
      // 解析 list 列表
      if (inList && inRequest) {
        listSb.append(s + "\n");
      }

      // 列表结束
      if (startListName != null && !startListName.equals("") && s.contains("</" + startListName)) {
        inList = false;
        endList = true;
      }
      if (endList == true) {
        // 调用 列表解析
        listModel = parserList(listSb.toString(), startListName);
        result.put(startListName, listModel);
        endList = false;
        listSb = new StringBuilder();
        startListName = null;
        continue;
      }
      // 解析一般属性
      if (inRequest && !inList) {
        JavaFieldModel model = parserLine(s);
        list.add(model);
      }

    }
    result.put(className, list);
    return result;
  }

  // 解析list
  private List<JavaFieldModel> parserList(String toString, String listName)
      throws IOException, ClassNotFoundException {
    List<JavaFieldModel> list = new ArrayList<>();
    BufferedReader bfr = new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(toString.getBytes())));
    bfr.readLine();
    String s;
    while ((s = bfr.readLine()) != null && !s.equals("")) {
      if (s.contains("Row") || s.contains(listName)) {
        continue;
      }
      JavaFieldModel model = parserLine(s);
      list.add(model);
    }

    return list;
  }


  // 行解析
  public JavaFieldModel parserLine(String line) throws ClassNotFoundException {
    JavaFieldModel model = new JavaFieldModel();
    line = line.trim();
    // 去掉行首尖括号
    String source = line.substring(1);
    // 截取字段名
    int i = source.indexOf(">");
    String name = source.substring(1, i);
    model.setName(name);

    // 截取注释
    int begin = source.indexOf(">");
    int end = source.indexOf("</");
    String comment = source.substring(begin + 1, end);
    model.setComment(comment);

    // 截取特殊字段类型
    int typeIndex = source.lastIndexOf(">");
    String type = source.substring(typeIndex + 1);
    if (!type.equals("")) {
      model.setClazz(Class.forName(type));
    } else {
      model.setClazz(String.class);
    }

    return model;
  }



}
