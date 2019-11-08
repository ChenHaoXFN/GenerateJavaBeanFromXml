# 约定
- xml 头不解析，只解析 body
- body 以 request 标签开始解析
- 标签内为该字段注释内容
- 默认都是 String 类型，如有特殊类型，请在标签结尾后加上类型 全名 例如： \<bizDate>日期\</bizDate>java.util.Date
- 列表默认用\<Row> 隔断，列表参数赋值一条，请勿多赋值
- 不需要列表类上打注解的 自行删除
- 参考FAS.OB0050000.01.xml

 

